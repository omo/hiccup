package es.flakiness.hiccup.play;

import android.app.Activity;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;

import dagger.ObjectGraph;
import es.flakiness.hiccup.InjectionScope;
import es.flakiness.hiccup.Injections;
import es.flakiness.hiccup.R;


public class PlayActivity extends Activity implements InjectionScope {

    public static final String EXTRA_KEY_URL = "PLAY_ACTIVITY_URI";
    public static final String EXTRA_KEY_LAST_POSITION = "PLAY_LAST_POSITION";

    private ObjectGraph graph;
    private PlayModule module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Uri uri = (Uri) getIntent().getParcelableExtra(EXTRA_KEY_URL);
            int lastPosition = getIntent().getIntExtra(EXTRA_KEY_LAST_POSITION, 0);
            module = new PlayModule(this, uri, lastPosition);
            graph = Injections.plus(getApplicationContext(), module);
        } catch (IOException e) {
            // TODO(omo): handle Gracefully.
            throw new RuntimeException(e);
        }

        // http://stackoverflow.com/questions/8500283/how-to-hide-action-bar-before-activity-is-created-and-then-show-it-again
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        setContentView(R.layout.activity_play);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        module.release();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO(omo): What should I do here?
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public ObjectGraph getGraph() {
        return graph;
    }
}
