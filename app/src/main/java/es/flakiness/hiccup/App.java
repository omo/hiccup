package es.flakiness.hiccup;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import dagger.ObjectGraph;

public class App extends Application implements InjectionScope {

    private ObjectGraph graph;

    @Override
    public void onCreate() {
        super.onCreate();
        this.graph = ObjectGraph.create(new AppModule(this));
    }

    @Override
    public ObjectGraph getGraph() {
        return graph;
    }
}
