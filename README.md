# POSSimulator

POSSimulator is a Java-based console application that simulates a Point-of-Sale (POS) system and integrates with a payment terminal via USB. The application handles payment transactions, magnetic card reading, and receipt processing by sending and receiving data in JSON format to and from the terminal.

## Features
- **Payment Processing**: Send payment requests and handle responses from the terminal.
- **Magnetic Card Reading**: Simulate the process of reading discount cards using magnetic stripes.
- **Receipt Processing**: Process card details without sending the data to the bank.

## Prerequisites
- Java 8 or higher
- Maven (for managing dependencies and building the project)
- A USB-connected terminal device that communicates via serial (COM) ports.
- Libraries:
  - `com.fazecast.jSerialComm` for serial communication with the USB device.

## Installation

### Clone the Repository
```bash
git clone https://github.com/projects-visionpay/sample-pos-pax-integration.git
```

### Build the Project
The project uses Maven for dependency management. Run the following command to build the project:

```bash
mvn clean install
```

### Run the Application
Upon starting the application, the console will display a list of connected USB devices along with their corresponding COM ports and device names (similar to how they are displayed in the Windows Device Manager). The user can select the correct terminal device by entering the respective COM port (e.g., `COM1`, `COM2`).

## How to Use

### 1. List USB Devices
The application automatically detects all USB devices connected to the machine. It will display their COM ports and device names. The user must select the terminal by entering the corresponding COM port.

### 2. Initiating a Payment Transaction
To initiate a payment transaction, select the "Payment" option. The application sends a `POSTransaction` JSON object to the terminal. The `processType` should be set to `0` for payment processing. The terminal will respond with an `InitiateTransaction` object containing the transaction state.

If the transaction is accepted (`state = 1`), the terminal will prompt the customer to insert or tap their card. The application will continue to monitor the transaction states, which may include statuses such as:

- Awaiting Card Read
- Sending Online
- Pre-Authorization Complete

Once the payment is complete, the terminal will return an authorization response, which will include the transaction status and necessary information to generate a receipt, such as the authorization ID and processor response code.

### 3. Reading Magnetic Cards
To read a magnetic stripe card, typically used for discount or loyalty cards, select the "Magnetic Card" option. The application sends a `POSTransaction` with `processType` set to `3`, indicating a magnetic card read request. The terminal will respond with a `MagneticCardMessage` object containing track data.

If the magnetic card read is successful (`magneticState = 1`), the track data will be returned in the response. Otherwise, an error state (`magneticState = 2`) will be returned, indicating a failed card read.

### 4. Processing Receipts
For receipt generation without sending data to the bank, use the "Receipt" option. The application sends a `POSTransaction` with `processType = 4`, requesting card information for receipt purposes. The terminal will return a `ReceiptMessage` containing the card signature.

## Code Structure

- **Main.java**: The main class that runs the console application. It manages user interaction and coordinates the transaction process, including sending requests to and receiving responses from the terminal.
- **POSTransaction.java**: Represents the payment transaction object that is sent to the terminal. This class contains fields such as `externalReference`, `transactionAmount`, `processType`, etc.
- **VisionPayTransactionMessage.java**: Contains the various states returned by the terminal during the transaction process, such as "Card Read Success," "Sending Online," and "Pre-Authorization Complete."
- **MagneticCardMessage.java**: Represents the response from the terminal when reading magnetic stripe cards, including track data fields.
- **ReceiptMessage.java**: Represents the receipt data returned by the terminal, including the card signature.

## Dependencies

The project uses Maven for dependency management. The following dependencies are used in the project:

```xml
<dependency>
    <groupId>com.fazecast</groupId>
    <artifactId>jSerialComm</artifactId>
    <version>2.9.2</version>
</dependency>
```

## Contributing

We welcome contributions! If you'd like to add features or fix bugs, feel free to open an issue or submit a pull request. When contributing, please follow these guidelines:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeatureName`).
3. Make your changes.
4. Test thoroughly to ensure the feature works as expected.
5. Commit your changes (`git commit -m 'Add new feature'`).
6. Push to your branch (`git push origin feature/YourFeatureName`).
7. Open a pull request to the `main` branch of the repository.

Please ensure your code adheres to the project's coding standards and that you provide relevant unit tests when applicable.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## Contact

For any questions, feel free to reach out to the repository owner or open an issue in the repository.




