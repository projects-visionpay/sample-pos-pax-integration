package au.com.visionpay.app;

public class ReceiptMessage {
    private String cardSignature;
    private String completedUTCDateTime;
    private String createdUTCDateTime;
    private int receiptState;

    public ReceiptMessage(String cardSignature, String completedUTCDateTime, String createdUTCDateTime, int receiptState) {
        this.cardSignature = cardSignature;
        this.completedUTCDateTime = completedUTCDateTime;
        this.createdUTCDateTime = createdUTCDateTime;
        this.receiptState = receiptState;
    }

    public int getReceiptState() {
        return receiptState;
    }

    public String getCardSignature() {
        return cardSignature;
    }
}
