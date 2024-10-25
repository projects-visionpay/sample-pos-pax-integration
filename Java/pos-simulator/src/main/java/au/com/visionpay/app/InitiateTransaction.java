package au.com.visionpay.app;

public class InitiateTransaction {
    private String externalReference;
    private String im30Reference;
    private int state;
    private String errorMessage;

    public InitiateTransaction(String externalReference, String im30Reference, int state, String errorMessage) {
        this.externalReference = externalReference;
        this.im30Reference = im30Reference;
        this.state = state;
        this.errorMessage = errorMessage;
    }

    public int getState(){
        return state;
    }

    public String getExternalReference(){
        return externalReference;
    }

    public String getErrorMessage(){
        return errorMessage;
    }
}
