package es.flakiness.hiccup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import dagger.ObjectGraph;

public class Injections {
    private static ObjectGraph getGraphFrom(Context context) {
        return ((InjectionScope)context).getGraph();
    }

    public static <T> T get(Context appContext, Class<T> clazz) {
        return getGraphFrom(appContext).get(clazz);
    }

    static public <T> void injectWith(Context context, T object) {
        getGraphFrom(context).inject(object);
    }

    static public <T extends View> void injectToView(T view) {
        injectWith(view.getContext(), view);
        ButterKnife.inject(view);
    }

    static public <T extends ViewGroup> void inflateAndInject(int resource, T view) {
        LayoutInflater.from(view.getContext()).inflate(resource, view);
        injectToView(view);
    }

    public static ObjectGraph plus(Context appContext, Object module) {
        return getGraphFrom(appContext).plus(module);
    }
}
