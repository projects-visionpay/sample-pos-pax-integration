package au.com.visionpay.app;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * VisionPay POS Simulator
 *
 */
public class App 
{
    private static Gson gson = new Gson();
    private static SerialPort selectedDevice;
    private static final String ExternalDeviceToken = "C6104D16-67DB-4156-A86E-08DBFF9D7DF6";
    private static int transactionId;
    private static final int currecy = 36; //AUD
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private static final long TIMEOUT = 61;

    public static void main(String[] args) {
        System.out.println("Welcome to VisionPay POS Simulator");

        Random rand = new Random();
        transactionId = rand.nextInt(10000);

        //List USB Devices
        System.out.println("Listing connected USB devices...");
        selectedDevice = selectUSBDevice();
        if (selectedDevice == null) {
            System.out.println("No device selected. Exiting.");
            return;
        }

        System.out.println("Device selected, ready for transactions.");
        startReceiver();

        // Main menu loop
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nSelect operation:");
            System.out.println("1. Process Payment");
            System.out.println("2. Process Magnetic Card");
            System.out.println("3. Process Receipt");
            System.out.println("4. Exit");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    processPayment();
                    break;
                case 2:
                    processMagneticCard();
                    break;
                case 3:
                    processReceipt();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    selectedDevice.closePort();
                    System.exit(0);
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private static SerialPort selectUSBDevice() {
        Scanner scanner = new Scanner(System.in);
        int index = 0;
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            System.out.printf("%d: Port: %s - %s\n", index++, port.getSystemPortName(), port.getDescriptivePortName());
        }

        System.out.println("Please select a device by number: ");
        int deviceIndex = scanner.nextInt();
        if (deviceIndex < 0 || deviceIndex >= ports.length) {
            System.out.println("Invalid device index.");
            return null;
        }

        SerialPort comPort = ports[deviceIndex];
        comPort.setBaudRate(9600); 

        if (comPort.openPort()) {
            System.out.println("Port opened successfully.");
        } else {
            System.out.println("Failed to open port.");
            return null;
        }

        return comPort;
    }

    private static void startReceiver(){
        Thread receiverThread = new Thread(App::receiveData);
        receiverThread.start();
    }

    private static void receiveData() {
        try {
            while (true) {
                String data = receiveDataFromTerminal(); 
                if (data != null && !data.isEmpty()) {
                    data = data.replace("}{", "}|{");
                    String[] r = data.split("\\|");
                    for (String d : r) {
                        queue.put(d); 
                    }
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static String getDataFromQueue() {
        try {
            return queue.poll(TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            return null;
        }
    }

    private static void processPayment() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter transaction amount (integer, without decimal places, e.g.: $10.51 = 1051): ");
        int amount = scanner.nextInt();

        POSTransaction transaction = new POSTransaction(ExternalDeviceToken, ++transactionId, amount, currecy, 0);
        String jsonTransaction = gson.toJson(transaction);

        // Send the JSON to the terminal
        sendDataToTerminal(jsonTransaction);

        // Receive response from the terminal
        InitiateTransaction initiateTransaction = receiveInitiateTransaction();

        // Handle the transaction based on the state
        if (initiateTransaction.getState() == 1) {
            // Proceed with transaction
            handleTransactionMessages(initiateTransaction.getExternalReference());
        } else {
            System.out.println("Transaction rejected: " + initiateTransaction.getErrorMessage());
        }
    }

    private static void captureTransaction(int externalReference, int amount) {
        System.out.println("Capturing Transaction...");

        POSTransaction captureTransaction = new POSTransaction(ExternalDeviceToken, externalReference, amount, 0, 1);
        String jsonCapture = gson.toJson(captureTransaction);
        sendDataToTerminal(jsonCapture);

        // Await capture confirmation
        while (true) {
            VisionPayTransactionMessage message = receiveVisionPayTransactionMessage();

            if (!message.getExternalReference().equals(String.valueOf(externalReference))) {
                continue;
            }

            switch (message.getIm30State()) {
                case 0:
                    break;
                case 7:
                    System.out.println("Sending Capture...");
                    break;
                case 8:
                    if(message.getTransactionStatus() == 1)
                        System.out.println("Capture Completed Successfully");
                    else
                        System.out.println("Capture Failed");
                    return;
                case 9:
                    System.out.println("Capture Failed");
                    return;
                default:
                    System.out.println("Cancelled (" + message.getIm30State() + ")");
                    return;
            }
        }
    }

    private static void processMagneticCard() {
        System.out.println("Processing Magnetic Card transaction...");

        POSTransaction transaction = new POSTransaction(ExternalDeviceToken, ++transactionId, 0, 0, 3);
        String jsonTransaction = gson.toJson(transaction);
        sendDataToTerminal(jsonTransaction);

        InitiateTransaction initiateTransaction = receiveInitiateTransaction();

        if (initiateTransaction.getState() == 1) {
            // Await magnetic card messages
            while(true) {
                MagneticCardMessage magneticMessage = receiveMagneticCardMessage();
                switch (magneticMessage.getMagneticState()) {
                    case 0:
                        System.out.println("Awaiting Card Read...");
                        break;
                    case 1:
                        System.out.println("Magnetic Card Read Success");
                        // Process track data
                        System.out.println("Track Data 1: " + magneticMessage.getTrackData1());
                        System.out.println("Track Data 2: " + magneticMessage.getTrackData2());
                        System.out.println("Track Data 3: " + magneticMessage.getTrackData3());
                        return;
                    case 2:
                        System.out.println("Magnetic Card Read Failed");
                        return;
                    default:
                        return;
                }
            }
        } else {
            System.out.println("Transaction rejected: " + initiateTransaction.getErrorMessage());
        }
    }

    private static void processReceipt() {
        System.out.println("Processing Receipt...");

        POSTransaction transaction = new POSTransaction(ExternalDeviceToken, ++transactionId, 0, 0, 4);
        String jsonTransaction = gson.toJson(transaction);
        sendDataToTerminal(jsonTransaction);

        InitiateTransaction initiateTransaction = receiveInitiateTransaction();

        if (initiateTransaction.getState() == 1) {
            // Await receipt messages
            while(true){
                ReceiptMessage receiptMessage = receiveReceiptMessage();
                switch (receiptMessage.getReceiptState()) {
                    case 0:
                        System.out.println("Awaiting Card Read...");
                        break;
                    case 1:
                        System.out.println("Receipt Read Success");
                        System.out.println("Card Signature: " + receiptMessage.getCardSignature());
                        return;
                    case 2:
                        System.out.println("Receipt Read Failed");
                        return;
                    default:
                        return;
                }
            }
        } else {
            System.out.println("Transaction rejected: " + initiateTransaction.getErrorMessage());
        }
    }

    private static void sendDataToTerminal(String message) {
        try {
            selectedDevice.getOutputStream().write(message.getBytes());
            selectedDevice.getOutputStream().flush();
            //System.out.println("Data sent: " + message);
        } catch(IOException ex) {
            throw new RuntimeException("Error sending data to terminal: " + ex.getMessage());
        }
    }

    private static String receiveDataFromTerminal() {
        try {
            int numRead = 0;
            String receivedMessage = "";
            byte[] readBuffer = new byte[5120];
            numRead = selectedDevice.getInputStream().read(readBuffer);
            if (numRead > 0) {
                String received = new String(readBuffer, 0, numRead);
                receivedMessage += received;
            }
            //System.out.println("Data received: " + receivedMessage);
            return receivedMessage;
        } catch(IOException ex) {
            //throw new RuntimeException("Error receiving data from terminal: " + ex.getMessage());
            return "";
        }
    }

    private static void handleTransactionMessages(String externalReference) {
        // Continuously listen for transaction messages
        while (true) {
            VisionPayTransactionMessage message = receiveVisionPayTransactionMessage();
                
            if (!message.getExternalReference().equals(externalReference)) {
                continue; // Ignore messages with different references
            }

            switch (message.getIm30State()) {
                case 0:
                    System.out.println("Awaiting Card Read...");
                    break;
                case 1:
                    System.out.println("Card Read Success");
                    break;
                case 3:
                    System.out.println("Sending Online...");
                    break;
                case 4:
                    System.out.println("Sending Online Complete");
                    break;
                case 6:
                    System.out.println("Pre-Authorization Complete");
                    if (message.getTransactionStatus() == 1) {
                        System.out.println("Transaction Approved");
                        // Proceed to capture
                        captureTransaction(Integer.parseInt(externalReference), message.getTransactionAmount());
                    } else {
                        System.out.println("Transaction Declined");
                    }
                    return;
                default:
                    System.out.println("Failed State: " + message.getIm30State());
                    return;
            }
        }
    }

    private static InitiateTransaction receiveInitiateTransaction() {
        String response = getDataFromQueue();
        return gson.fromJson(response, InitiateTransaction.class);
    }

    private static VisionPayTransactionMessage receiveVisionPayTransactionMessage() {
        String response = getDataFromQueue();
        return gson.fromJson(response, VisionPayTransactionMessage.class);
    }

    private static MagneticCardMessage receiveMagneticCardMessage() {
        String response = getDataFromQueue();
        return gson.fromJson(response, MagneticCardMessage.class);
    }

    private static ReceiptMessage receiveReceiptMessage() {
        String response = getDataFromQueue();
        return gson.fromJson(response, ReceiptMessage.class);
    }
}
