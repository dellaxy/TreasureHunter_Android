package com.example.lovci_pokladov.objects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.List;

public class PolygonView extends View {

    private PolygonOptions polygonOptions;
    private Paint paint;

    public PolygonView(Context context) {
        super(context);
        init();
    }

    public PolygonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
    }

    public void setPolygonOptions(PolygonOptions options) {
        this.polygonOptions = options;
        invalidate(); // Redraw the view with new polygon options
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float centerX = canvas.getWidth() / 2;
        float centerY = canvas.getHeight() / 2;


        Path path = new Path();
        for(LatLng latLng : polygonOptions.getPoints()) {
            Log.d("PolygonView", "latLng: " + latLng.toString());
            float x = (float) (centerX + latLng.longitude);
            float y = (float) (centerY + latLng.latitude);

            if(path.isEmpty()) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.close();
        paint.setColor(Color.RED);
        canvas.drawPath(path, paint);
    }




}
