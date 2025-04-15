package com.sohamkore.attendancesuite;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class PieChartView extends View {

    private float[] data;
    private int[] colors = {Color.GREEN, Color.RED}; // Green for present, Red for absent
    private Paint paint;

    public PieChartView(Context context) {
        super(context);
        init();
    }

    public PieChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void setPieData(float[] data) {
        this.data = data;
        invalidate(); // Refresh the view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data == null || data.length == 0) {
            return;
        }

        float total = 0;
        for (float slice : data) {
            total += slice;
        }

        float currentAngle = -90; // Start drawing from top (0 degrees)

        for (int i = 0; i < data.length; i++) {
            float sliceAngle = (data[i] / total) * 360;
            paint.setColor(colors[i]);
            canvas.drawArc(0, 0, getWidth(), getHeight(), currentAngle, sliceAngle, true, paint);
            currentAngle += sliceAngle;
        }
    }
}
