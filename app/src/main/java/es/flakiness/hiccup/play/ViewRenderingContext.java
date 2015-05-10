package es.flakiness.hiccup.play;

import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import es.flakiness.hiccup.R;
import es.flakiness.hiccup.play.sign.Signs;

public class ViewRenderingContext {
    private final View view;
    private final int pathColor;

    public ViewRenderingContext(View view) {
        this.view = view;
        this.pathColor = view.getResources().getColor(R.color.app_primary);
    }

    public RectF getContainerBox() {
        return Signs.getContainerBox(view);
    }

    public Paint getPaint() {
        Paint paint = new Paint();
        paint.setColor(pathColor);
        return paint;
    }
}
