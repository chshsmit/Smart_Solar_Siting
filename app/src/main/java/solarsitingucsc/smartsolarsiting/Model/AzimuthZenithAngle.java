package solarsitingucsc.smartsolarsiting.Model;

/**
 * Created by chrissmith on 2/1/18.
 */

public class AzimuthZenithAngle {
    private final double azimuth;
    private final double zenithAngle;

    public AzimuthZenithAngle(final double azimuth, final double zenithAngle) {
        this.zenithAngle = zenithAngle;
        this.azimuth = azimuth;
    }

    public final double getZenithAngle() {
        return zenithAngle;
    }

    public final double getAzimuth() {
        return azimuth;
    }

    @Override
    public String toString() {
        return String.format("azimuth %.6f°, zenith angle %.6f°", azimuth, zenithAngle);
    }

}