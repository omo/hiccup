package es.flakiness.hiccup.play;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.ObjectGraph;
import es.flakiness.hiccup.App;
import es.flakiness.hiccup.R;


public class PlayActivity extends Activity {

    public static final String EXTRA_KEY = "PLAY_ACTIVITY_URI";

    private ObjectGraph graph;

    @InjectView(R.id.play_view) PlayView playView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            graph = App.plus(getApplicationContext(), new PlayModule(this, (Uri) getIntent().getParcelableExtra(EXTRA_KEY)));
        } catch (IOException e) {
            // TODO(omo): handle Gracefully.
            throw new RuntimeException(e);
        }

        // http://stackoverflow.com/questions/8500283/how-to-hide-action-bar-before-activity-is-created-and-then-show-it-again
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_play);
        ButterKnife.inject(this);
        playView.injectFrom(graph);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO(omo): What should I do here?
        super.onConfigurationChanged(newConfig);
    }
}
