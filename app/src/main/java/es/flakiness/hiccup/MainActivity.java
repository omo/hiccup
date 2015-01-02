package es.flakiness.hiccup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import es.flakiness.hiccup.play.PlayActivity;
import es.flakiness.hiccup.talk.AddDebugTalkEvent;
import es.flakiness.hiccup.talk.AddTalkEvent;
import es.flakiness.hiccup.talk.ClearTalkEvent;


public class MainActivity extends Activity {

    @Inject Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.inject(getApplicationContext(), this);
        bus.register(this);
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
            bus.post(new AddTalkEvent(this.getApplicationContext(), data));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_debug_item:
                bus.post(new AddDebugTalkEvent());
                return true;
            case R.id.action_clear_talk_list:
                bus.post(new ClearTalkEvent());
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
}
