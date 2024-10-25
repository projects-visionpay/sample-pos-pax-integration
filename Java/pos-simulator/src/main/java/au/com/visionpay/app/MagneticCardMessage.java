package au.com.visionpay.app;

public class MagneticCardMessage {
    private String externalReference;
    private String im30Reference;
    private String completedUTCDateTime;
    private String createdUTCDateTime;
    private int magneticState;
    private String trackData1;
    private String trackData2;
    private String trackData3;

    public MagneticCardMessage(String externalReference, String im30Reference, String completedUTCDateTime,
                               String createdUTCDateTime, int magneticState, String trackData1, String trackData2,
                               String trackData3) {
        this.externalReference = externalReference;
        this.im30Reference = im30Reference;
        this.completedUTCDateTime = completedUTCDateTime;
        this.createdUTCDateTime = createdUTCDateTime;
        this.magneticState = magneticState;
        this.trackData1 = trackData1;
        this.trackData2 = trackData2;
        this.trackData3 = trackData3;
    }

    public int getMagneticState() {
        return magneticState;
    }

    public String getTrackData1() {
        return trackData1;
    }

    public String getTrackData2() {
        return trackData2;
    }

    public String getTrackData3() {
        return trackData3;
    }
}
