package solarsitingucsc.smartsolarsiting;

import org.junit.Test;

import solarsitingucsc.smartsolarsiting.Model.AzimuthZenithAngle;

import static org.junit.Assert.*;



public class AzimuthZenithAngleUnitTest {

    private static final double DELTA = 1e-15;

    private AzimuthZenithAngle testAngle = new AzimuthZenithAngle(180.0, 30.0);

    //1
    @Test
    public void zenithSetCorrectly() throws Exception {
        assertEquals(30.0, testAngle.getZenithAngle(), DELTA);
    }

    //2
    @Test
    public void azimuthSetCorrectly() throws Exception {
        assertEquals(180.0, testAngle.getAzimuth(), DELTA);
    }

    //3
    @Test
    public void negativeAzimuthSetCorrectly() throws Exception {
        assertEquals(testAngle.getAzimuth() - 360, testAngle.getNegativeAzimuth(), DELTA);
    }

    //4
    @Test
    public void elevationSetCorrectly() throws Exception {
        assertEquals(90 - testAngle.getZenithAngle(), testAngle.getElevationFromTheHorizon(), DELTA);
    }

    private AzimuthZenithAngle newTestAngle = new AzimuthZenithAngle(100.0, 60.0);
    private AzimuthZenithAngle averagedAngle = testAngle.twoAngleAverage(newTestAngle);

    //5
    @Test
    public void averagedAzimuthCorrectly() throws Exception {
        assertEquals(  (newTestAngle.getAzimuth() + testAngle.getAzimuth())/2,
                averagedAngle.getAzimuth(), DELTA);
    }

    //6
    @Test
    public void averagedZenithCorrectly() throws Exception {
        assertEquals(  (newTestAngle.getZenithAngle() + testAngle.getZenithAngle())/2,
                averagedAngle.getZenithAngle(), DELTA);
    }

}
