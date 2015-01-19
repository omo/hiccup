package es.flakiness.hiccup;

import android.content.Context;

import dagger.ObjectGraph;

public interface InjectionScope {
    ObjectGraph getGraph();
}

