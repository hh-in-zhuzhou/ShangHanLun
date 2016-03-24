package me.huanghai.searchController;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hh on 16/3/24.
 */
public class ArrowView extends View {
    public static int UP = 0;
    public static int DOWN = 1;
    private int direction = UP;
    private Paint paint = new Paint();
    private int border = 50;

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public ArrowView(Context context) {
        super(context);
    }

    public ArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    protected void initPaint() {
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPaint();
        Path path = new Path();
        if (direction == UP) {
            path.moveTo(0, border);
            path.lineTo(border / 2, 0);
            path.lineTo(border, border);
            path.close();
        } else {
            path.moveTo(0, 0);
            path.lineTo(border / 2, border);
            path.lineTo(border, 0);
            path.close();
        }
        canvas.drawPath(path, paint);
        invalidate();
    }
}
