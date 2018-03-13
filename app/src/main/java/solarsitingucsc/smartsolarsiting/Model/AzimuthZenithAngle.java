package solarsitingucsc.smartsolarsiting.Model;

public class AzimuthZenithAngle {
    private final double azimuth;
    private final double zenithAngle;
    private final double elevationFromTheHorizon;
    private final double negativeAzimuth;
    private String time;

    public AzimuthZenithAngle(final double azimuth, final double zenithAngle, String time) {
        this.zenithAngle = zenithAngle;
        this.azimuth = azimuth;
        this.elevationFromTheHorizon = 90 - zenithAngle;
        this.negativeAzimuth = azimuth - 360;
        this.time = time;
    }

    public final double getZenithAngle() {
        return zenithAngle;
    }

    public final double getAzimuth() {
        return azimuth;
    }

    public final double getNegativeAzimuth() {
        return negativeAzimuth;
    }

    public final double getElevationFromTheHorizon(){
        return elevationFromTheHorizon;
    }

    public String getTime() {
        return time;
    }

    public AzimuthZenithAngle twoAngleAverage(AzimuthZenithAngle newPosition, String time){

        double newAzimuth = newPosition.getAzimuth();
        double newZenith = newPosition.getZenithAngle();

        newAzimuth = (this.getAzimuth() + newAzimuth)/2;
        newZenith = (this.getZenithAngle() + newZenith)/2;

        AzimuthZenithAngle averagedPosition = new AzimuthZenithAngle(newAzimuth, newZenith, time);

        return averagedPosition;
    }


    @Override
    public String toString() {
        return String.format("azimuth %.6f°, zenith angle %.6f°", azimuth, zenithAngle);
    }

}