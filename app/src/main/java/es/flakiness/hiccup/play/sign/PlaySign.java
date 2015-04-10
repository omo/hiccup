package es.flakiness.hiccup.play.sign;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import es.flakiness.hiccup.play.ViewRenderer;

/**
* Created by omo on 4/9/15.
*/
public class PlaySign implements ViewRenderer {
    @Override
    public void draw(View view, Canvas canvas) {
        RectF container = Signs.getContainerBox(view);
        RectF bound = Signs.getCenterBoundingBox(container);

        Path path = new Path();
        path.moveTo(bound.left, bound.top);
        path.lineTo(bound.right, bound.centerY());
        path.lineTo(bound.left, bound.bottom);
        path.close();

        canvas.drawPath(path, Signs.getSignPaint());
    }
}
