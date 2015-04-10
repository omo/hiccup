package es.flakiness.hiccup.play.sign;

import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
* Created by omo on 4/9/15.
*/
public class Signs {
    final static float paddingBottom = 100;

    static public RectF getContainerBox(View view) {
        float vw = view.getWidth();
        float vh = view.getHeight();
        float containerWidth = vw;
        float containerHeight = vw;
        float paddingTop = vh - paddingBottom - containerWidth;
        float containerTop = paddingTop;
        float containerLeft = 0;
        return new RectF(containerLeft, containerTop, containerLeft + containerWidth, containerTop + containerHeight);
    }

    static public RectF getCenterBoundingBox(RectF container) {
        float bboxHeight = (container.width()/3.0f)*1.1f;
        float bboxWidth = (container.width()/3.0f)*1.0f;
        float bboxLeft = (container.width() - bboxWidth)/2;
        float bboxTop = container.top + (container.height() - bboxHeight)/2;
        return new RectF(bboxLeft, bboxTop, bboxLeft + bboxWidth, bboxTop + bboxHeight);
    }

    static public RectF getRightBoundingBox(RectF container) {
        RectF bound = getCenterBoundingBox(container);
        float bboxShift = container.width()/10;
        bound.offsetTo(container.right - bound.width() - bboxShift, bound.top);
        return bound;
    }

    static public RectF getLeftBoundingBox(RectF container) {
        RectF bound = getCenterBoundingBox(container);
        float bboxShift = container.width()/10;
        bound.offsetTo(container.left + bboxShift, bound.top);
        return bound;
    }

    static public Paint getSignPaint() {
        Paint paint = new Paint();
        paint.setARGB(255, 0, 0, 0);
        return paint;
    }
}
