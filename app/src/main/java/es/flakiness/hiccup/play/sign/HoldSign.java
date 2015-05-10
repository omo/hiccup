package es.flakiness.hiccup.play.sign;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import es.flakiness.hiccup.play.ViewRenderer;
import es.flakiness.hiccup.play.ViewRenderingContext;

/**
* Created by omo on 4/9/15.
*/
public class HoldSign implements ViewRenderer {

    private final float delta;

    public HoldSign(float delta) {
        this.delta = delta;
    }

    @Override
    public void draw(ViewRenderingContext context, Canvas canvas) {
        RectF container = context.getContainerBox();
        RectF rbound = Signs.getRightBoundingBox(container);
        rbound.inset(Math.min(rbound.width()*0.5f, -rbound.width()*delta*0.5f),
                     Math.min(rbound.height()*0.5f, -rbound.height()*delta*0.5f));

        Path rpath = new Path();
        rpath.moveTo(rbound.left, rbound.top);
        rpath.lineTo(rbound.centerX(), rbound.centerY());
        rpath.lineTo(rbound.left, rbound.bottom);
        rpath.close();
        rpath.moveTo(rbound.centerX(), rbound.top);
        rpath.lineTo(rbound.right, rbound.centerY());
        rpath.lineTo(rbound.centerX(), rbound.bottom);
        rpath.close();

        canvas.drawPath(rpath, context.getPaint());

        RectF lbound = Signs.getLeftBoundingBox(container);
        lbound.inset(Math.min(lbound.width()*0.5f, lbound.width()*delta*0.5f),
                     Math.min(lbound.height()*0.5f, lbound.height()*delta*0.5f));

        Path lpath = new Path();
        lpath.moveTo(lbound.right, lbound.top);
        lpath.lineTo(lbound.centerX(), lbound.centerY());
        lpath.lineTo(lbound.right, lbound.bottom);
        lpath.close();
        lpath.moveTo(lbound.centerX(), lbound.top);
        lpath.lineTo(lbound.left, lbound.centerY());
        lpath.lineTo(lbound.centerX(), lbound.bottom);
        lpath.close();

        canvas.drawPath(lpath, context.getPaint());
    }
}
