package es.flakiness.hiccup;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_debug_item:
                App.get(getApplicationContext(), TalkStore.class).putDebugInstance();
                return true;
            case R.id.action_clear_talk_list:
                App.get(getApplicationContext(), TalkStore.class).clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
