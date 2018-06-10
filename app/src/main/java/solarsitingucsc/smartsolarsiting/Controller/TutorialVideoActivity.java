package solarsitingucsc.smartsolarsiting.Controller;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import solarsitingucsc.smartsolarsiting.R;

public class TutorialVideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_video);

        VideoView mVideoView =(VideoView) findViewById(R.id.tutorial_video_view);
        MediaController mMediaController = new MediaController(this);

        mMediaController.setAnchorView(mVideoView);

        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.tutorialVideo);

        mVideoView.setMediaController(mMediaController);
        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();

        mVideoView.start();

    }
}
