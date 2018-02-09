package solarsitingucsc.smartsolarsiting.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawOnTop extends View {
    public DrawOnTop(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(100);
        paint.setTextSize(100);
        canvas.drawText("Test Text", 310, 310, paint);

        super.onDraw(canvas);
    }
}
