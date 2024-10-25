package au.com.visionpay.app;

public class VisionPayTransactionMessage {
    private String applicationId;
    private String applicationLabel;
    private int applicationTransactionCounter;
    private String authorizationId;
    private int authorizationType;
    private String cardExpiryDate;
    private String cardSequenceNumber;
    private String cardSignature;
    private String cardType;
    private int cardVerificationMethod;
    private String completedUTCDateTime;
    private String createdUTCDateTime;
    private String externalReference;
    private String gatewayResponse;
    private String gatewayResponseCode;
    private String im30Reference;
    private int im30State;
    private String im30TerminalId;
    private String merchantId;
    private String stan;
    private int transactionAmount;
    private int transactionCurrency;
    private int transactionStatus;
    private int transactionType;

    public VisionPayTransactionMessage(String applicationId, String applicationLabel, int applicationTransactionCounter,
                                       String authorizationId, int authorizationType, String cardExpiryDate,
                                       String cardSequenceNumber, String cardSignature, String cardType,
                                       int cardVerificationMethod, String completedUTCDateTime, String createdUTCDateTime,
                                       String externalReference, String gatewayResponse, String gatewayResponseCode,
                                       String im30Reference, int im30State, String im30TerminalId, String merchantId,
                                       String stan, int transactionAmount, int transactionCurrency, int transactionStatus,
                                       int transactionType) {
        this.applicationId = applicationId;
        this.applicationLabel = applicationLabel;
        this.applicationTransactionCounter = applicationTransactionCounter;
        this.authorizationId = authorizationId;
        this.authorizationType = authorizationType;
        this.cardExpiryDate = cardExpiryDate;
        this.cardSequenceNumber = cardSequenceNumber;
        this.cardSignature = cardSignature;
        this.cardType = cardType;
        this.cardVerificationMethod = cardVerificationMethod;
        this.completedUTCDateTime = completedUTCDateTime;
        this.createdUTCDateTime = createdUTCDateTime;
        this.externalReference = externalReference;
        this.gatewayResponse = gatewayResponse;
        this.gatewayResponseCode = gatewayResponseCode;
        this.im30Reference = im30Reference;
        this.im30State = im30State;
        this.im30TerminalId = im30TerminalId;
        this.merchantId = merchantId;
        this.stan = stan;
        this.transactionAmount = transactionAmount;
        this.transactionCurrency = transactionCurrency;
        this.transactionStatus = transactionStatus;
        this.transactionType = transactionType;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public int getIm30State() {
        return im30State;
    }

    public int getTransactionStatus() {
        return transactionStatus;
    }

    public int getTransactionAmount(){
        return transactionAmount;
    }
}
