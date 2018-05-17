package solarsitingucsc.smartsolarsiting.Controller;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import solarsitingucsc.smartsolarsiting.R;
import solarsitingucsc.smartsolarsiting.View.SliderAdapter;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager mSLideViewPager;
    private LinearLayout mDotLayout;

    private SliderAdapter mSliderAdapter;

    private TextView[] mDots;

    private Button mNextButton;
    private Button mBackButton;

    private int mCurrentSlide;

    //----------------------------------------------------------------------------------------------
    //Initializations and on create
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        mSLideViewPager = (ViewPager) findViewById(R.id.slide_view_pager);
        mDotLayout = (LinearLayout) findViewById(R.id.dots_layout);

        initializeButtons();

        mSliderAdapter = new SliderAdapter(this);
        mSLideViewPager.setAdapter(mSliderAdapter);

        addDotsIndicator(0);
        mSLideViewPager.addOnPageChangeListener(viewListener);
    }

    private void initializeButtons(){
        //Next Button
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSLideViewPager.setCurrentItem(mCurrentSlide + 1);
            }
        });

        //Back Button
        mBackButton = (Button) findViewById(R.id.prev_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSLideViewPager.setCurrentItem(mCurrentSlide - 1);
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    //Changing state of the dots on bottom of screen
    //----------------------------------------------------------------------------------------------

    public void addDotsIndicator(int position){

        mDots = new TextView[4];
        mDotLayout.removeAllViews();

        for(int i = 0; i < mDots.length; i++){

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length > 0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }

    }

    //----------------------------------------------------------------------------------------------
    //SlideChangeListener
    //----------------------------------------------------------------------------------------------

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);

            mCurrentSlide = i;

            if(i == 0){
                mBackButton.setEnabled(false);
                mBackButton.setVisibility(View.INVISIBLE);
                mNextButton.setEnabled(true);

                mBackButton.setText("");
                mNextButton.setText("Next");

            } else if(i == mDots.length - 1){
                mBackButton.setEnabled(true);
                mBackButton.setVisibility(View.VISIBLE);
                mNextButton.setEnabled(true);

                mBackButton.setText("Back");
                mNextButton.setText("Finish");
            } else {
                mBackButton.setEnabled(true);
                mBackButton.setVisibility(View.VISIBLE);
                mNextButton.setEnabled(true);

                mBackButton.setText("Back");
                mNextButton.setText("Next");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

}
