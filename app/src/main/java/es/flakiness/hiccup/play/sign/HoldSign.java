package es.flakiness.hiccup.play.sign;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import es.flakiness.hiccup.play.ViewRenderer;

/**
* Created by omo on 4/9/15.
*/
public class HoldSign implements ViewRenderer {
    @Override
    public void draw(View view, Canvas canvas) {
        RectF container = Signs.getContainerBox(view);
        RectF rbound = Signs.getRightBoundingBox(container);

        Path rpath = new Path();
        rpath.moveTo(rbound.left, rbound.top);
        rpath.lineTo(rbound.centerX(), rbound.centerY());
        rpath.lineTo(rbound.left, rbound.bottom);
        rpath.close();
        rpath.moveTo(rbound.centerX(), rbound.top);
        rpath.lineTo(rbound.right, rbound.centerY());
        rpath.lineTo(rbound.centerX(), rbound.bottom);
        rpath.close();

        canvas.drawPath(rpath, Signs.getSignPaint());

        RectF lbound = Signs.getLeftBoundingBox(container);

        Path lpath = new Path();
        lpath.moveTo(lbound.right, lbound.top);
        lpath.lineTo(lbound.centerX(), lbound.centerY());
        lpath.lineTo(lbound.right, lbound.bottom);
        lpath.close();
        lpath.moveTo(lbound.centerX(), lbound.top);
        lpath.lineTo(lbound.left, lbound.centerY());
        lpath.lineTo(lbound.centerX(), lbound.bottom);
        lpath.close();

        canvas.drawPath(lpath, Signs.getSignPaint());
    }
}
