package es.flakiness.hiccup.play.sign;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import es.flakiness.hiccup.play.ViewRenderer;

/**
* Created by omo on 4/9/15.
*/
public class PauseSign implements ViewRenderer {

    @Override
    public void draw(View view, Canvas canvas) {
        RectF container = Signs.getContainerBox(view);
        RectF bound = Signs.getCenterBoundingBox(container);

        float pauseBoxHeight = bound.height();
        float pauseBoxSeparation = bound.width() / 4.0f;
        float pauseBoxWidth = (bound.width() - pauseBoxSeparation) / 2;

        Path path = new Path();
        path.addRect(
                bound.left,
                bound.top,
                bound.left + pauseBoxWidth,
                bound.top + pauseBoxHeight,
                Path.Direction.CCW);

        path.addRect(
                bound.left + pauseBoxWidth + pauseBoxSeparation,
                bound.top,
                bound.left + pauseBoxWidth + pauseBoxSeparation + pauseBoxWidth,
                bound.top + pauseBoxHeight,
                Path.Direction.CCW);

        canvas.drawPath(path, Signs.getSignPaint());
    }
}
