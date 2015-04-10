package es.flakiness.hiccup.play;

import android.graphics.Canvas;
import android.view.View;

public interface ViewRenderer {
    void draw(View view, Canvas canvas);
}
