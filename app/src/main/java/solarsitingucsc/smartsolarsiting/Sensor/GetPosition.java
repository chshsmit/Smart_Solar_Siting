package solarsitingucsc.smartsolarsiting.Sensor;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import solarsitingucsc.smartsolarsiting.Model.Grena3;
import solarsitingucsc.smartsolarsiting.Model.AzimuthZenithAngle;
import solarsitingucsc.smartsolarsiting.Model.DeltaT;
import solarsitingucsc.smartsolarsiting.Model.JulianDate;

public class GetPosition {

//    LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
//    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//    double longitude = location.getLongitude();
//    double latitude = location.getLatitude();
//
//    Calendar calendar = new GregorianCalendar();
//    Date thisTime = new Date();
//    calendar.setTime(thisTime);
//
//    double deltaT = DeltaT.estimate(calendar);
//    AzimuthZenithAngle solarPos = Grena3.calculateSolarPosition(calendar,latitude,
//            longitude,deltaT);
//
//    float azimuth;
//    float zenith;
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//
//        this.azimuth = event[0];
//        this.zenith = event[1];
//
//        newTime = new Date();
//        this.calendar.setTime(newTime);
//        double deltaT = DeltaT.estimate(this.calendar);
//
//        AzimuthZenithAngle xy = Grena3.calculateSolarPosition(this.calendar,this.latitude,
//                this.longitude,deltaT);
//
//    }

}