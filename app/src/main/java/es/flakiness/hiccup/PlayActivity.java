package es.flakiness.hiccup;

import es.flakiness.hiccup.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.io.IOException;


public class PlayActivity extends Activity {

    public static final String EXTRA_KEY = "PLAY_ACTIVITY_URI";

    private TalkPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // http://stackoverflow.com/questions/8500283/how-to-hide-action-bar-before-activity-is-created-and-then-show-it-again
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_play);

        Uri uri = (Uri)getIntent().getParcelableExtra(EXTRA_KEY);
        try {
            this.player = new TalkPlayer(this.getApplicationContext(), uri);
        } catch (IOException e) {
            // TODO(omo): handle Gracefully.
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDestroy() {
        this.player.onHostClose();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO(omo): What should I do here?
        super.onConfigurationChanged(newConfig);
    }
}
