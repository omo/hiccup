package es.flakiness.hiccup.play;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class PlayBarView extends View {

    private float progress;
    private Paint barBackgroundPaint = new Paint();
    private Paint barForegroundPaint = new Paint();

    public PlayBarView(Context context) {
        this(context, null);
    }

    public PlayBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PlayBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        // FIXME: These colors should be resources
        barBackgroundPaint.setColor(0xff222266);
        barBackgroundPaint.setStyle(Paint.Style.FILL);
        barForegroundPaint.setColor(0xffeeeebb);
        barForegroundPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float left = 0.0f + getPaddingLeft();
        float top = 0.0f + getPaddingTop();
        float right = getWidth() - getPaddingRight();
        float bottom = getHeight() - getPaddingBottom();

        float fgRight = left + (right - left)*progress;
        canvas.drawRect(left, top, right, bottom, barBackgroundPaint);
        canvas.drawRect(left, top, fgRight, bottom, barForegroundPaint);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }
}
