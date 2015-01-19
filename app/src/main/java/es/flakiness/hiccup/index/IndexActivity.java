package es.flakiness.hiccup.index;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import dagger.ObjectGraph;
import es.flakiness.hiccup.App;
import es.flakiness.hiccup.InjectionScope;
import es.flakiness.hiccup.talk.PlayTalkEvent;
import es.flakiness.hiccup.R;
import es.flakiness.hiccup.play.PlayActivity;
import es.flakiness.hiccup.talk.TalkStore;


public class IndexActivity extends Activity implements InjectionScope {

    private ObjectGraph graph;

    @Inject Bus bus;
    @Inject TalkStore store;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        graph = App.plus(getApplicationContext(), new IndexModule());
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

    private void requestAddTalk() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            store.addTalk(this.getApplicationContext(), data.getData());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_debug_item:
                store.addDebugTalk();
                return true;
            case R.id.action_clear_talk_list:
                store.clearTalk();
                return true;
            case R.id.action_add_talk:
                requestAddTalk();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
