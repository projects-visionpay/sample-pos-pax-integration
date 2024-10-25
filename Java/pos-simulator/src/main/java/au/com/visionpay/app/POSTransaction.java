package au.com.visionpay.app;

public class POSTransaction {
    private String externalDeviceToken;
    private String externalReference;
    private int transactionAmount;
    private int transactionCurrency;
    private int processType;

    public POSTransaction(String externalDeviceToken, int externalReference, int transactionAmount, int transactionCurrency, int processType) {
        this.externalDeviceToken = externalDeviceToken;
        this.externalReference = String.valueOf(externalReference);
        this.transactionAmount = transactionAmount;
        this.transactionCurrency = transactionCurrency;
        this.processType = processType;
    }
    
}
