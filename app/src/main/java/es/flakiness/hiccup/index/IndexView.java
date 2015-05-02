package es.flakiness.hiccup.index;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.InjectView;
import es.flakiness.hiccup.Injections;
import es.flakiness.hiccup.R;
import es.flakiness.hiccup.talk.AddTalkEvent;
import es.flakiness.hiccup.talk.TalkStore;

public class IndexView extends FrameLayout {
    @Inject TalkList talkList;
    @Inject TalkStore store;
    @Inject Bus bus;

    @InjectView(R.id.index_toolbar) Toolbar toolbar;
    @InjectView(R.id.card_list) ListView cardList;

    private TalkListActionMode actionMode;

    public IndexView(Context context) {
        this(context, null);
    }

    public IndexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IndexView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        Injections.inflateAndInject(R.layout.index_view, this);
        cardList.setAdapter(talkList);
        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (actionMode.isActive()) {
                    actionMode.viewWasSelectedWhileActive(view, i);
                    return;
                }

                talkList.onClickAt(i);
            }
        });

        actionMode = new TalkListActionMode(cardList, store); // FIXME(omo): This should be built by Dagger.
        cardList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return actionMode.onItemLongClick(toolbar, view, position, id);
            }
        });

        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_add_debug_item:
                        store.addDebugTalk();
                        return true;
                    case R.id.action_clear_talk_list:
                        store.clearTalk();
                        return true;
                    case R.id.action_add_talk:
                        bus.post(new AddTalkEvent());
                        return true;
                    case R.id.action_show_licenses:
                        showLicenses();
                        return true;
                    default:
                        Log.wtf(getClass().getName(), "No such menu");
                        return false;
                }
            }
        });
    }

    private void showLicenses() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("https://github.com/omo/hiccup/blob/master/LICENSES.txt"));
        getContext().startActivity(i);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        talkList.unsubscribe(); // This should be handled by the module
    }
}
