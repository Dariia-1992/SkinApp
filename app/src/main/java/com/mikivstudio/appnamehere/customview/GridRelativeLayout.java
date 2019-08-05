package com.mikivstudio.appnamehere.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Dariia Mshanetskaya  on 29.07.2019.
 */
public class GridRelativeLayout extends RelativeLayout {
    private static final int CELL_SIZE = 74;
    private static final int CELL_COLOR = 0xffe6e6e6;
    private static final int CELL_LINE_WIDTH = 4;

    private static final int START_X = (int)(CELL_SIZE * -0.66);
    private static final int START_Y = 0;

    private final Paint paint = new Paint();

    public GridRelativeLayout(Context context) {
        super(context);
        init();
    }

    public GridRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GridRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        paint.setColor(CELL_COLOR);
        paint.setStrokeWidth(CELL_LINE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        canvas.drawARGB(0xff, 0xff, 0xff, 0xff);

        for (int x = START_X; x < width; x += CELL_SIZE)
            canvas.drawLine(x, 0, x, height, paint);

        for (int y = START_Y; y < height; y += CELL_SIZE)
            canvas.drawLine(0, y, width, y, paint);

    }
}
