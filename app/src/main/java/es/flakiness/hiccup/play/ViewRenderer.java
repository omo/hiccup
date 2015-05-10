package es.flakiness.hiccup.play;

import android.graphics.Canvas;
import android.view.View;

public interface ViewRenderer {
    void draw(ViewRenderingContext context, Canvas canvas);
}
