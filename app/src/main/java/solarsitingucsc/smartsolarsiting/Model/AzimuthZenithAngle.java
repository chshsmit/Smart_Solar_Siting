package solarsitingucsc.smartsolarsiting.Model;

/**
 * Created by chrissmith on 2/1/18.
 */

public class AzimuthZenithAngle {
    private final double azimuth;
    private final double zenithAngle;
    private final double elevationFromTheHorizon;
    private final double negativeAzimuth;

    public AzimuthZenithAngle(final double azimuth, final double zenithAngle) {
        this.zenithAngle = zenithAngle;
        this.azimuth = azimuth;
        this.elevationFromTheHorizon = 90 - zenithAngle;
        this.negativeAzimuth = azimuth - 360;
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

    @Override
    public String toString() {
        return String.format("azimuth %.6f°, zenith angle %.6f°", azimuth, zenithAngle);
    }

}