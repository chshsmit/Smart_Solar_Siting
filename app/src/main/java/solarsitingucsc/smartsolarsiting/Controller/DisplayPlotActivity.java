package solarsitingucsc.smartsolarsiting.Controller;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import solarsitingucsc.smartsolarsiting.R;

public class DisplayPlotActivity extends AppCompatActivity {

    private static final String[] months = {"January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October",
            "November", "December"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_plot);
        createSomthing();

    }

    public void createSomthing() {
        Random random = new Random();
        LineChart chart = findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entries.add(new Entry(i, random.nextInt(100)));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Power in KW");
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return months[(int) value];
            }
        });

//        findViewById(R.id.save_plot).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chart.saveToGallery("mychart.jpg", 85); // 85 is the quality of the image
//            }
//        });
    }
}