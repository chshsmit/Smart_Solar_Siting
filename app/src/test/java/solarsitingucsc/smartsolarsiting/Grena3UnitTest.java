package solarsitingucsc.smartsolarsiting;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import solarsitingucsc.smartsolarsiting.Model.AzimuthZenithAngle;
import solarsitingucsc.smartsolarsiting.Model.Grena3;

/**
 * Created by chrissmith on 3/9/18.
 */

public class Grena3UnitTest {

    private static final double DELTA = 1.0;
    GregorianCalendar newDate = new GregorianCalendar(2018, Calendar.MARCH,
            9, 14, 47);

    AzimuthZenithAngle newAngle = Grena3.calculateSolarPosition(newDate, 36.9,
            -122.03, 68);

    //The expected azimuth for March 9th, 2018 at 2:47pm is 228 degrees
    @Test
    public void azimuthReturnedCorrectly() throws Exception {
        assertEquals(228.0, newAngle.getAzimuth(), DELTA);
    }

    //The expected zenith for March 9th, 2018 at 2:47pm is 54 degrees
    // (corresponds to elevation of 36 degrees)
    @Test
    public void zenithReturnedCorrectly() throws Exception {
        assertEquals(54.0, newAngle.getZenithAngle(), DELTA);
    }

}
