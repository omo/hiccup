package es.flakiness.hiccup.index;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import dagger.ObjectGraph;
import es.flakiness.hiccup.InjectionScope;
import es.flakiness.hiccup.Injections;
import es.flakiness.hiccup.talk.AddTalkEvent;
import es.flakiness.hiccup.talk.PlayTalkEvent;
import es.flakiness.hiccup.R;
import es.flakiness.hiccup.play.PlayActivity;
import es.flakiness.hiccup.talk.TalkStore;


public class IndexActivity extends ActionBarActivity implements InjectionScope {

    private ObjectGraph graph;

    @Inject Bus bus;
    @Inject TalkStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        graph = Injections.plus(getApplicationContext(), new IndexModule());
        graph.inject(this);
        bus.register(this);

        setContentView(R.layout.activity_index);
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            store.addTalk(this.getApplicationContext(), data.getData());
        }
    }

    @Subscribe
    public void requestAddTalk(AddTalkEvent event) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 1);
    }


    @Subscribe
    public void playTalk(PlayTalkEvent event) {
        Intent intent = new Intent(this, PlayActivity.class);
        intent.putExtra(PlayActivity.EXTRA_KEY_URL, event.getUri());
        intent.putExtra(PlayActivity.EXTRA_KEY_LAST_POSITION, event.getLastPosition());
        startActivity(intent);
    }

    @Override
    public ObjectGraph getGraph() {
        return graph;
    }
}
