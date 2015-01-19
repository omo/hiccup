package es.flakiness.hiccup.talk;

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import es.flakiness.hiccup.R;

public class TalkListActionMode implements ActionMode.Callback, AdapterView.OnItemLongClickListener {

    private final TalkStore store;
    private final ListView parent;
    private ActionMode activeMode;

    public TalkListActionMode(ListView parent, TalkStore store) {
        this.parent = parent;
        this.store = store;
    }

    public boolean isActive() {
        return null != activeMode;
    }

    private void start(Activity activity, View initialSelection, int position) {
        parent.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        activeMode = activity.startActionMode(this);
        parent.setItemChecked(position, true);
        viewWasSelectedWhileActive(initialSelection, position);
    }

    private void didFinish() {
        activeMode = null;
        parent.clearChoices();
        // http://stackoverflow.com/questions/9754170/listview-selection-remains-persistent-after-exiting-choice-mode
        for (int i = 0; i < parent.getChildCount(); i++)
            parent.getChildAt(i).setActivated(false);
        parent.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
    }

    private void removeSelectedTalks() {
        ArrayList<Long> removedItems = new ArrayList();
        for (Long i : parent.getCheckedItemIds())
            removedItems.add(i);
        store.removeTalks(removedItems);
    }

    public void viewWasSelectedWhileActive(View view, int position) {
        if (!isActive())
            new AssertionError("Shouldn't be called while inactive");
        if (parent.getCheckedItemCount() == 0)
            activeMode.finish();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_talk_cab, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.action_remove_talk)
            removeSelectedTalks();
        activeMode.finish();
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        didFinish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (isActive())
            return false;
        start((Activity) view.getContext(), view, position);
        return true;
    }
}
