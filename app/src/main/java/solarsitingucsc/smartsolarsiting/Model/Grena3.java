package solarsitingucsc.smartsolarsiting.Model;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.lang.Math.*;


public final class Grena3 {

    private Grena3() {
    }
    

    /**
     * Calculate topocentric solar position, i.e. the location of the sun on the sky for a certain point in time on a
     * certain point of the Earth's surface.
     *
     * This follows the no. 3 algorithm described in Grena, 'Five new algorithms for the computation of sun position
     * from 2010 to 2110', Solar Energy 86 (2012) pp. 1323-1337.
     *
     * The algorithm is supposed to work for the years 2010 to 2110, with a maximum error of 0.01 degrees.
     *
     * This method does not perform refraction correction.
     */

    public static AzimuthZenithAngle calculateSolarPosition(final GregorianCalendar date, final double latitude,
                                                            final double longitude, final double deltaT) {
        return calculateSolarPosition(date, latitude, longitude, deltaT, Double.MIN_VALUE, Double.MIN_VALUE);
    }

    //You can call this method directly to perform refraction correction
    public static AzimuthZenithAngle calculateSolarPosition(final GregorianCalendar date, final double latitude,
                                                            final double longitude, final double deltaT, final double pressure,
                                                            final double temperature) {
        final double t = calcT(date);
        final double tE = t + 1.1574e-5 * deltaT;    //deltaT is the difference between terrestrial and universal timed
        final double fundamentalFrequency = 0.0172019715 * tE;

        final double eclipticLongitude = -1.388803 + 1.720279216e-2 * tE + 3.3366e-2 * sin(fundamentalFrequency - 0.06172)
                + 3.53e-4 * sin(2.0 * fundamentalFrequency - 0.1163);

        final double earthAxisInclination = 4.089567e-1 - 6.19e-9 * tE;

        final double sinEclipLong = sin(eclipticLongitude);
        final double cosEclipLong = cos(eclipticLongitude);
        final double sinInclination = sin(earthAxisInclination);
        final double cosInclination = sqrt(1 - sinInclination * sinInclination);


        double rightAcsension = atan2(sinEclipLong * cosInclination, cosEclipLong);
        if (rightAcsension < 0) {
            rightAcsension += 2 * PI;
        }

        final double declination = asin(sinEclipLong * sinInclination);

        double hourAngle = 1.7528311 + 6.300388099 * t + toRadians(longitude) - rightAcsension;
        hourAngle = ((hourAngle + PI) % (2 * PI)) - PI;
        if (hourAngle < -PI) {
            hourAngle += 2 * PI;
        }

        // end of "short procedure"
        //Now that we have the right acsension, declination, and hour angle we can compute the azimuth and zenith angles


        final double sinLatitude = sin(toRadians(latitude));
        final double cosLatitude = sqrt((1 - sinLatitude * sinLatitude));
        final double sinDeclination = sin(declination);
        final double cosDeclination = sqrt(1 - sinDeclination * sinDeclination);
        final double sinHour = sin(hourAngle);
        final double cosHour = cos(hourAngle);

        final double elevationAngle = sinLatitude * sinDeclination + cosLatitude * cosDeclination * cosHour;
        final double parallaxCorrectedElevation = asin(elevationAngle) - 4.26e-5 * sqrt(1.0 - elevationAngle * elevationAngle);

        final double AZIMUTH = atan2(sinHour, cosHour * sinLatitude - sinDeclination * cosLatitude / cosDeclination);

        // refraction correction (disabled for silly parameter values)
        final double refractionCorrection =
                (temperature < -273 || temperature > 273 || pressure < 0 || pressure > 3000) ? 0.0 : (
                        ((parallaxCorrectedElevation > 0.0) ?
                                (0.08422 * (pressure / 1000)) / ((273.0 + temperature) * tan(parallaxCorrectedElevation + 0.003138 / (parallaxCorrectedElevation + 0.08919)))
                                : 0.0));

        final double ZENITH = PI / 2 - parallaxCorrectedElevation - refractionCorrection;

        return new AzimuthZenithAngle(toDegrees(AZIMUTH + PI) % 360.0, toDegrees(ZENITH),
                makeTimeString(date.get(Calendar.MONTH), date.get(Calendar.HOUR_OF_DAY)));
    }

    //Returns time t
    //t is the number of days starting from the beginning of the year 2060
    private static double calcT(GregorianCalendar date) {
        //Create calendar object with the date
        GregorianCalendar utc = JulianDate.createUtcCalendar(date);

        int month = utc.get(Calendar.MONTH) + 1;
        int year = utc.get(Calendar.YEAR);
        final int day = utc.get(Calendar.DAY_OF_MONTH);
        final double hour = utc.get(Calendar.HOUR_OF_DAY) +
                utc.get(Calendar.MINUTE) / 60d +
                utc.get(Calendar.SECOND) / (60d * 60);
        if (month <= 2) {
            month += 12;
            year -= 1;
        }

        return (int) (365.25 * (year - 2000)) + (int) (30.6001 * (month + 1))
                - (int) (0.01 * year) + day + 0.0416667 * hour - 21958;
    }

    //---------------------------------------------------------------------------------------------------
    //Averaging Grena3 Algorithm
    //---------------------------------------------------------------------------------------------------

    //static AzimuthZenithAngle[] averagePositionForMonth = new AzimuthZenithAngle[16];


    public static AzimuthZenithAngle[][] calculateWholeYear(double latitude, double longitude){
        AzimuthZenithAngle[][] anglesForYear = new AzimuthZenithAngle[12][];
        GregorianCalendar currentDate = new GregorianCalendar();

        Calendar start = Calendar.getInstance();
        start.setTime(currentDate.getTime());
        Calendar end = Calendar.getInstance();
        currentDate.add(Calendar.YEAR, 1);
        end.setTime(currentDate.getTime());

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.MONTH, 1),
                date = start.getTime()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            anglesForYear = addMonthAverage(anglesForYear, cal.get(Calendar.MONTH),
                    cal.get(Calendar.YEAR), latitude, longitude);
        }

        return anglesForYear;
    }

    private static AzimuthZenithAngle[][] addMonthAverage
            (AzimuthZenithAngle[][] array, int month, int year, double latitude, double longitude) {
        if (array[month] == null) {
            array[month] = calculateWholeMonth(year, month, latitude, longitude);
        }
        return array;
    }


    //Calculate the average position of the sun over a day for a whole month
    private static AzimuthZenithAngle[] calculateWholeMonth(int year, int month, double latitude,
                                                            double longitude){
        int hourIndex = 0, startTime = 6, endTime = 22, startDay = 1, deltaT = 68,
                minuteIncrement = 15;
        GregorianCalendar currentDay = new GregorianCalendar(year, month, startDay, startTime, 0);
        AzimuthZenithAngle position;
        AzimuthZenithAngle[] averagePositionForMonth = new AzimuthZenithAngle[(endTime - startTime) * 4];

        while(currentDay.get(Calendar.MONTH) == month) {

            position = calculateSolarPosition(currentDay, latitude, longitude, deltaT);
            addAngleToAverageArray(position, averagePositionForMonth, hourIndex,
                    currentDay.get(Calendar.MONTH), currentDay.get(Calendar.HOUR_OF_DAY));

            currentDay.add(Calendar.MINUTE, minuteIncrement);
            hourIndex++;

            if(currentDay.get(Calendar.HOUR_OF_DAY) == endTime){
                currentDay.add(Calendar.DAY_OF_MONTH, startDay);
                currentDay.set(Calendar.HOUR_OF_DAY, startTime);
                hourIndex = 0;
            }
        }
        return averagePositionForMonth;
    }

    private static String makeTimeString(int month, int hour) {
        String convertedHour;
        String convertedMonth;

        if (hour < 10)
            convertedHour = "0" + hour;
        else
            convertedHour = hour + "";

        if (month < 10)
            convertedMonth = "0" + month;
        else
            convertedMonth = month + "";

        return convertedMonth + "-" + convertedHour;
    }

    public static void calculateCurrentDay(double latitude, double longitude){
        GregorianCalendar currentDay = new GregorianCalendar();

        AzimuthZenithAngle position = calculateSolarPosition(currentDay, latitude, longitude, 68);

        System.out.println("SPA: " +position);
    }

    public static void printAngleArray(AzimuthZenithAngle[] averageArray){
        System.out.println("Next Month");
        for(int i=0; i < averageArray.length; i++){
            System.out.println(i);
            System.out.println(averageArray[i].toString());
        }
    }

    private static void addAngleToAverageArray(AzimuthZenithAngle newAngle,
                                               AzimuthZenithAngle[] averageArray,
                                               int hourIndex, int month, int hour){
        if(averageArray[hourIndex] == null) {
            averageArray[hourIndex] = newAngle;
        } else {
            averageArray[hourIndex] = averageArray[hourIndex].twoAngleAverage(newAngle,
                    makeTimeString(month, hour));
        }
    }
}