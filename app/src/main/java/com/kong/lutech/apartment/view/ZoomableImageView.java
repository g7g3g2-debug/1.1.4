package com.kong.lutech.apartment.view;

import android.annotation.SuppressLint;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.kong.lutech.apartment.model.ParkImageParkingInfo;
import com.kong.lutech.apartment.utils.SizeUtils;
import com.kongtech.lutech.apartment.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

@SuppressLint("AppCompatCustomView")
public class ZoomableImageView extends ImageView {
    private float MIN_SCALE_FACTOR = 0.1f;
    private float MAX_SCALE_FACTOR = 5f;


    private enum State {NONE, DRAG, ZOOM, FLING}

    private State state;

    private float[] m;
    private Matrix matrix, savedMatrix;
    private ScaleGestureDetector scaleGestureDetector;

    private boolean touchEnabled = true;
    public boolean isTouchEnabled() {
        return touchEnabled;
    }

    public void setTouchEnabled(boolean touchEnabled) {
        this.touchEnabled = touchEnabled;
    }

    private float scaleFactor = 1f;

    private PointF touchStart = new PointF();
    private PointF touchLast = new PointF();
    private PointF touchMiddle = new PointF();

    private int bmWidth;
    private int bmHeight;

    private int width;
    private int height;

    private int limitWidth;
    private int limitHeight;

    private int marginWidth;
    private int marginHeight;

    private int mapWidth;
    private int mapHeight;

    public ZoomableImageView(Context context) {
        this(context, null);
    }

    public ZoomableImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 디텍터
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());

        matrix = new Matrix();
        savedMatrix = new Matrix();

        m = new float[9];

        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);
        setState(State.NONE);
    }

    private void setLimit() {
        float minScaleX = (float) limitWidth / (float) bmWidth;
        float minScaleY = (float) limitHeight / (float) bmHeight;

        MIN_SCALE_FACTOR = Math.max(minScaleX, minScaleY);
        MAX_SCALE_FACTOR = MIN_SCALE_FACTOR * 5f;
    }

    public void printData() {

        matrix.getValues(m);

        final float transX = marginWidth - m[Matrix.MTRANS_X];
        final float transY = marginHeight - m[Matrix.MTRANS_Y];

        final float left = transX / scaleFactor;
        final float top = transY / scaleFactor;
        final float width = limitWidth / scaleFactor;
        final float height = limitHeight / scaleFactor;

        Log.d("Zoomable1", "transX : " + left);
        Log.d("Zoomable1", "transY : " + top);
        Log.d("Zoomable1", "width : " + width);
        Log.d("Zoomable1", "height : " + height);

        Log.d("Zoomable1", "right : " + (bmWidth - left - width));
        Log.d("Zoomable1", "bottom : " + (bmHeight - top - height));

        float[] insets = getInsets();
        Log.d("Zoomable", "inests : " + Arrays.toString(insets));
    }


    // [left, top, right, bottom]
    public float[] getInsets() {
        matrix.getValues(m);

        final float transX = marginWidth - m[Matrix.MTRANS_X];
        final float transY = marginHeight - m[Matrix.MTRANS_Y];

        final float left = transX / scaleFactor;
        final float top = transY / scaleFactor;

        final float width = limitWidth / scaleFactor;
        final float height = limitHeight / scaleFactor;

        final float right = (bmWidth - left - width);
        final float bottom = (bmHeight - top - height);

        final float ratio = (float)mapWidth / (float)bmWidth;

        return new float[]{ left * ratio, top * ratio, right * ratio, bottom * ratio };
    }

    public void setInsets(float[] insets) {

        final float width = mapWidth - insets[0] - insets[2];
        final float height = mapHeight - insets[1] - insets[3];

        final float ratio = Math.max(mapWidth / bmWidth, mapHeight / bmHeight);

        final float tx = ratio * insets[0] * -1;
        final float ty = ratio * insets[1] * -1;

        clear();

        setTranslation(tx + getWidth() / 2 - width / 2, ty + getHeight() / 2 - height / 2);
        //setTranslationBy(getWidth() / 2 - width / 2, getHeight() / 2 - height / 2, true);


        final float minScaleX = getWidth() / width;//mapWidth / width;
        final float minScaleY = getHeight() / height;//mapHeight / height;

        final float scale = Math.max(minScaleX, minScaleY);
        setScale(scale, getWidth() / 2, getHeight() / 2, true);
    }

    public void clear() {
        matrix = new Matrix();

        m = new float[9];

        scaleFactor = 1.0f;
        setImageMatrix(matrix);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);

        if (drawable == null) return;

        bmWidth = drawable.getIntrinsicWidth();
        bmHeight = drawable.getIntrinsicHeight();
        setLimit();
    }


    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        bmWidth = bm.getWidth();
        bmHeight = bm.getHeight();
        setLimit();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);


        if (isTouchEnabled() && bmWidth > 0 && bmHeight > 0) {
            float scale;
            float scaleX = (float) width / (float) bmWidth;
            float scaleY = (float) height / (float) bmHeight;
            scale = Math.max(scaleX, scaleY);
            matrix.setScale(scale, scale);
            scaleFactor = scale;

            float redundantYSpace = height - (scale * bmHeight);
            float redundantXSpace = width - (scale * bmWidth);
            redundantYSpace /= 2;
            redundantXSpace /= 2;

            matrix.postTranslate(redundantXSpace, redundantYSpace);

            setImageMatrix(matrix);

            setLimit();
        }
    }

    public void focusCenter() {
        float scale;
        float scaleX = (float) width / (float) bmWidth;
        float scaleY = (float) height / (float) bmHeight;
        scale = Math.max(scaleX, scaleY);
        matrix.setScale(scale, scale);
        scaleFactor = scale;

        float redundantYSpace = height - (scale * bmHeight);
        float redundantXSpace = width - (scale * bmWidth);
        redundantYSpace /= 2;
        redundantXSpace /= 2;

        matrix.postTranslate(redundantXSpace, redundantYSpace);

        setImageMatrix(matrix);

        setLimit();
    }

    private void setState(State state) {
        this.state = state;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTouchEnabled()) return false;

        scaleGestureDetector.onTouchEvent(event);

        PointF point = new PointF(event.getX(), event.getY());
        switch (event.getAction()) {
            // 손가락 하나
            case MotionEvent.ACTION_DOWN:
                touchStart.set(point);
                touchLast.set(touchStart);
                setState(State.DRAG);
                break;
            // 손가락 두개
            case MotionEvent.ACTION_POINTER_DOWN:
                touchLast.set(point);
                setState(State.ZOOM);
                break;
            case MotionEvent.ACTION_MOVE:
                if (state == State.DRAG) {
                    float deltaX = point.x - touchLast.x;
                    float deltaY = point.y - touchLast.y;

                    matrix.getValues(m);
                    setTranslationBy(deltaX, deltaY, false);
                    touchLast.set(point);
                }
                break;
            case MotionEvent.ACTION_UP:
                float scale = Math.max(MIN_SCALE_FACTOR, scaleFactor);
                setScale(scale, width / 2, height / 2, false);
                setTranslationBy(0, 0, false);
                break;
        }
        return true;
    }

    private void setTranslationBy(float dx, float dy, boolean isStrong) {

        matrix.getValues(m);

        float deltaX = dx;
        float deltaY = dy;

        float startX = dx + m[Matrix.MTRANS_X];
        float startY = dy + m[Matrix.MTRANS_Y];

        if (isStrong || startX > (width - limitWidth) / 2) {
            startX = (width - limitWidth) / 2;
            deltaX = startX - m[Matrix.MTRANS_X];
        }

        if (isStrong || startY > (height - limitHeight) / 2) {
            startY = (height - limitHeight) / 2;
            deltaY = startY - m[Matrix.MTRANS_Y];
        }

        float endX = startX + (float) bmWidth * scaleFactor;
        float endY = startY + (float) bmHeight * scaleFactor;

        if (isStrong || endX < (width + limitWidth) / 2) {
            endX = (width + limitWidth) / 2;
            deltaX = (endX - (float) bmWidth * scaleFactor) - m[Matrix.MTRANS_X];
        }

        if (isStrong || endY < (height + limitHeight) / 2) {
            endY = (height + limitHeight) / 2;
            deltaY = (endY - (float) bmHeight * scaleFactor) - m[Matrix.MTRANS_Y];
        }

        matrix.postTranslate(deltaX, deltaY);
        setImageMatrix(matrix);
    }

    /*private void drag(MotionEvent event) {
        matrix.getValues(m);

        float left = m[2];
        float top = m[5];
        float bottom = (top + (m[0] * mBitmapHeight)) - mViewHeight;
        float right = (left + (m[0] * mBitmapWidth)) - mViewWidth;

        float eventX = event.getX();
        float eventY = event.getY();
        float spacingX = eventX - touchStart.x;
        float spacingY = eventY - touchStart.y;
        float newPositionLeft = (left < 0 ? spacingX : spacingX * -1) + left;
        float newPositionRight = (spacingX) + right;
        float newPositionTop = (top < 0 ? spacingY : spacingY * -1) + top;
        float newPositionBottom = (spacingY) + bottom;

        boolean x = true;
        boolean y = true;

    }*/

    public void setMapSize(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public void setFocus(int x, int y) {

        marginWidth = (getWidth() - x) / 2;
        marginHeight = (getHeight() - y) / 2;

        limitWidth = x;
        limitHeight = y;
        setLimit();
    }

    public void setScale(float scale, float cx, float cy, boolean isMax) {
        Log.d("ZoomableImageView", "scale : " + scale + ", factor : " + scaleFactor + ", cx : " + cx + ", cy : " + cy);
        Log.d("ZoomableImageView", "scale : " + scale + ", max_factor : " + MAX_SCALE_FACTOR);

        if (!isMax) {
            scale = Math.min(MAX_SCALE_FACTOR, scale);
        }

        float sf = scale / scaleFactor;
        scaleFactor = scale;
        matrix.postScale(sf, sf, cx, cy);
        setImageMatrix(matrix);
    }

    public void setTranslation(float tx, float ty) {
        matrix.getValues(m);

        matrix.setTranslate(tx, ty);
        setImageMatrix(matrix);
    }

    public void postTranslation(float tx, float ty) {
        matrix.getValues(m);

        matrix.postTranslate(tx, ty);
        setImageMatrix(matrix);
    }

    public void setCctvLocation(int x, int y) {
        final int tx = x * -1 + getWidth() / 2;
        final int ty = y * -1 + getHeight() / 2;
        Log.d("ZoomableImageView", "t x : " + tx + ", y : " + ty);

        setTranslation(tx, ty);
        setScale(3f, getWidth() / 2, getHeight() / 2, true);


        final float transX = marginWidth - m[Matrix.MTRANS_X];
        final float transY = marginHeight - m[Matrix.MTRANS_Y];

        Log.d("ZoomableImageView", "trans x : " + transX + ", y : " + transY);


        float scale = Math.max(MIN_SCALE_FACTOR, scaleFactor);
        setScale(scale, width / 2, height / 2, true);
        setTranslationBy(0, 0, false);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float sf = detector.getScaleFactor();

            Log.d("ZoomableImageView", "focus sf : " + sf + ", x : " + detector.getFocusX() + ", y : " + detector.getFocusY());
            setScale(sf * scaleFactor, detector.getFocusX(), detector.getFocusY(), false);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            setState(State.ZOOM);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }


    public void setPins(Bitmap originalBitmap, List<Point> pins, boolean isParking) {
        Bitmap.Config config = originalBitmap.getConfig();

        Bitmap newB = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), config);
        Canvas canvas = new Canvas(newB);

        Paint dotPaint = new Paint();
        dotPaint.setColor(Color.RED);

        Paint aroundPaint = new Paint();
        aroundPaint.setColor(Color.RED);
        aroundPaint.setAlpha(100);


        canvas.drawBitmap(originalBitmap, new Matrix(), null);

        for ( Point pin : pins) {
            if (isParking) {
                Bitmap bitmapPin = BitmapFactory.decodeResource(getResources(), R.drawable.pin);

                final float pinWidth = SizeUtils.convertDpToPixel(28, getContext());
                final float pinHeight = SizeUtils.convertDpToPixel(46, getContext());

                final Rect src = new Rect(0, 0, bitmapPin.getWidth(), bitmapPin.getHeight());

                final int startX = (int) (pin.x - pinWidth / 2);
                final int startY = (int) (pin.y - pinHeight / 2);

                final Rect dst = new Rect(startX, startY, startX + (int) pinWidth, startY + (int) pinHeight);
                canvas.drawBitmap(bitmapPin, null, dst, null);

            } else {
                canvas.drawCircle(pin.x, pin.y, 140, aroundPaint);
                canvas.drawCircle(pin.x, pin.y, 10, dotPaint);
            }
        }

        setImageBitmap(newB);
    }


    public static Bitmap getBitmapFromURL(String src, String accessToken) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();

            if (accessToken != null) {
                connection.setRequestProperty("Authorization", accessToken);
            }

            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap mergeParkingLocationBitmap(Bitmap originalBitmap, List<ParkImageParkingInfo.Location> locations) {
        Log.d("ZoomableImageView", new Gson().toJson(locations));


        Bitmap.Config config = originalBitmap.getConfig();

        Bitmap newB = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), config);
        Canvas canvas = new Canvas(newB);

        Paint ePaint = new Paint();
        ePaint.setColor(Color.GREEN);

        Paint fPaint = new Paint();
        fPaint.setColor(Color.RED);


        canvas.drawBitmap(originalBitmap, new Matrix(), null);

        for (ParkImageParkingInfo.Location location : locations) {
            final List<Integer> inset = location.getpLocation();
            if (inset.size() != 4) continue;

            final Rect rect = new Rect(inset.get(0), inset.get(1), inset.get(2), inset.get(3));
            canvas.drawRect(rect, "E".equals(location.getpStatus()) ? ePaint : fPaint);
        }

        return newB;
    }

    public static Bitmap mergeCctvLocationBitmap(Bitmap originalBitmap, ParkImageParkingInfo.Cctv cctv) {
        Bitmap.Config config = originalBitmap.getConfig();

        Bitmap newB = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), config);
        Canvas canvas = new Canvas(newB);

        Paint dotPaint = new Paint();
        dotPaint.setColor(0x52cfe0);
        dotPaint.setAlpha(200);

        Paint aroundPaint = new Paint();
        aroundPaint.setColor(0x52cfe0);
        aroundPaint.setAlpha(100);


        canvas.drawBitmap(originalBitmap, new Matrix(), null);

        canvas.drawCircle(cctv.getxPosition(), cctv.getyPosition(), 100, aroundPaint);
        canvas.drawCircle(cctv.getxPosition(), cctv.getyPosition(), 8, dotPaint);

        return newB;
    }
}
