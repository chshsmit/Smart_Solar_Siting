package solarsitingucsc.smartsolarsiting.View;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import solarsitingucsc.smartsolarsiting.R;

/**
 * Created by chrissmith on 5/15/18.
 */

public class SliderAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;

    public SliderAdapter(Context context){
        this.mContext = context;
    }


    //Arrays to fill in the values
    private int[] slide_images = {
            R.drawable.welcome_onboarding,
            R.drawable.power_onboarding,
            R.drawable.settings_onboarding,
            R.drawable.help_onboarding

    };

    //TODO: Change headings
    private String[] slide_headings = {
            "Welcome",
            "Solar",
            "Customize",
            "Help"
    };

    //TODO:Change Descriptions
    private String[] slide_descriptions = {
            "Welcome to Smart Solar Siting",
            "Our platform allows you to perform solar site surveys right from your mobile phone!" +
                    "Find all your solar site surveu needs here!",
            "Customize your panel and camera in the settings! Click the cog in the top " +
                    "right corner on the homepage to access them!",
            "If you have any more questions view our tooltips and tutorial video" +
                    "in the settings!"
    };


    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }


    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mLayoutInflater = (LayoutInflater) mContext.
                getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

        View view = mLayoutInflater.inflate(R.layout.onboarding_slide_layout,
                container, false);

        //Initializing Views
        TextView slideHeading = (TextView) view.findViewById(R.id.slide_heading);
        TextView slideDescription = (TextView) view.findViewById(R.id.slide_description);
        ImageView myIcon = (ImageView) view.findViewById(R.id.slide_image);

        //Setting values within the views
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descriptions[position]);
        myIcon.setImageResource(slide_images[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }
}

