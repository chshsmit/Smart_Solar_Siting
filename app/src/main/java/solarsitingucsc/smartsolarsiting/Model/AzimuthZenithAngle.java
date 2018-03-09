package solarsitingucsc.smartsolarsiting.Model;

import android.graphics.Color;

/**
 * Created by chrissmith on 2/1/18.
 */

public class AzimuthZenithAngle {
    private final double azimuth;
    private final double zenithAngle;
    private final double elevationFromTheHorizon;
    private final double negativeAzimuth;
    private int percentageAvailable;

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

    

    public AzimuthZenithAngle twoAngleAverage(AzimuthZenithAngle newPosition){

        double newAzimuth = newPosition.getAzimuth();
        double newZenith = newPosition.getZenithAngle();

        newAzimuth = (this.getAzimuth() + newAzimuth)/2;
        newZenith = (this.getZenithAngle() + newZenith)/2;

        AzimuthZenithAngle averagedPosition = new AzimuthZenithAngle(newAzimuth, newZenith);

        return averagedPosition;
    }


    @Override
    public String toString() {
        return String.format("azimuth %.6f°, zenith angle %.6f°", azimuth, zenithAngle);
    }

}