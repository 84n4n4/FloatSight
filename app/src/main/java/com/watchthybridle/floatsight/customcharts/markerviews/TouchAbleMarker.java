package com.watchthybridle.floatsight.customcharts.markerviews;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.github.mikephil.charting.components.MarkerView;

public abstract class TouchAbleMarker extends MarkerView {
    private Rect markerViewDrawArea;
    private boolean isTouched = false;

    protected TouchAbleMarker(Context context, @LayoutRes int layoutRes) {
        super(context, layoutRes);
        markerViewDrawArea = new Rect(0,0,0,0);
    }

    protected void setTouchArea(float left, float top, float right, float bottom) {
        markerViewDrawArea.top = (int) top;
        markerViewDrawArea.left = (int) left;
        markerViewDrawArea.right = (int) right;
        markerViewDrawArea.bottom = (int) bottom;
    }

    abstract void customOnLongClick();

    abstract void customOnClick();

    @Override
    @SuppressWarnings("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        int longPressTimeout = ViewConfiguration.getLongPressTimeout();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isTouched = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isTouched) {
                        customOnLongClick();
                        isTouched = false;
                    }
                }
            }, longPressTimeout);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (isTouched) {
                customOnClick();
            }
            isTouched = false;
        }
        return true;
    }

    public Rect getMarkerViewDrawArea() {
        return markerViewDrawArea;
    }

    /* Chart has to hand through touch events, to do this add the following to the chart

    @Override
    @SuppressWarnings("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (mMarker != null
                && isDrawMarkersEnabled()
                && valuesToHighlight()
                && getMarker() instanceof TouchAbleMarker) {

            TouchAbleMarker marker = (TouchAbleMarker) getMarker();
            Rect markerViewDrawArea = marker.getMarkerViewDrawArea();

            if (markerViewDrawArea.contains((int) event.getX(),(int) event.getY())) {
                return marker.dispatchTouchEvent(event);
            }
        }
        return super.onTouchEvent(event);
    }*/
}
