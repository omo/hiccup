package es.flakiness.hiccup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class Injections {
    static public <T> void injectWith(Context context, T object) {
        ((InjectionScope)context).getGraph().inject(object);
    }

    static public <T extends View> void injectToView(T view) {
        injectWith(view.getContext(), view);
        ButterKnife.inject(view);
    }

    static public <T extends ViewGroup> void inflateAndInject(int resource, T view) {
        LayoutInflater.from(view.getContext()).inflate(resource, view);
        injectToView(view);
    }
}
