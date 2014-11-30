package es.flakiness.hiccup;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;

public class App extends Application {

    private ObjectGraph graph;

    @Override
    public void onCreate() {
        super.onCreate();
        this.graph = ObjectGraph.create(new AppModule(this));
    }

    public static void inject(Context appContext, Object object) {
        getGraph((App) appContext).inject(object);
    }

    public static <T> T get(Context appContext, Class<T> clazz) {
        return getGraph((App) appContext).get(clazz);
    }

    public static ObjectGraph plus(Context appContext, Object module) {
        return getGraph((App) appContext).plus(module);
    }

    private static ObjectGraph getGraph(App appContext) {
        return ((App)appContext).graph;
    }
}
