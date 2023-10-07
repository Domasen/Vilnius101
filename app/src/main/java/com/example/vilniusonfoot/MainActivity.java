package com.example.vilniusonfoot;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import pl.droidsonroids.gif.GifImageView;
import android.view.ScaleGestureDetector;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView image;
    private GifImageView gifImage0, gifImage1, gifImage2, gifImage3, gifImage4, gifImage5, gifImage6;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    private PointF start = new PointF();
    private float[] m = new float[9];
    private float minScale = 1f;
    private float maxScale = 5f;

    private ScaleGestureDetector scaleGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.image);
        gifImage1 = findViewById(R.id.gifImage1);
        //adjustGifPosition();
        moveToGifPosition(100, 50);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);

                PointF curr = new PointF(event.getX(), event.getY());

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        start.set(curr);
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(curr.x - start.x, curr.y - start.y);
                            constrainDrag();
                            //adjustGifPosition();
                            moveToGifPosition(100, 50);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                }

                image.setImageMatrix(matrix);
                return true;
            }
        });
    }

    private void adjustGifPosition() {
        matrix.getValues(m);

        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];
        float scaleX = m[Matrix.MSCALE_X];
        float scaleY = m[Matrix.MSCALE_Y];

        float imageWidth = image.getDrawable().getIntrinsicWidth();

        // Calculate the GIF's position based on the image's scale and translation
        float gifX = (imageWidth * scaleX) + transX - gifImage.getWidth();

        // Since it's the top, the y position is just the translation without any subtraction
        float gifY = transY;

        gifImage.setX(gifX);
        gifImage.setY(gifY);
    }

    private void moveToGifPosition(float xOffset, float yOffset) {
        matrix.getValues(m);

        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];
        float scaleX = m[Matrix.MSCALE_X];
        float scaleY = m[Matrix.MSCALE_Y];

        // Calculate the GIF's position based on the image's scale and translation
        float gifX = transX + (xOffset * scaleX);
        float gifY = transY + (yOffset * scaleY);

        gifImage.setX(gifX);
        gifImage.setY(gifY);
    }

    private void moveGifToPosition(GifImageView gif, float xOffset, float yOffset) {
        matrix.getValues(m);

        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];
        float scaleX = m[Matrix.MSCALE_X];
        float scaleY = m[Matrix.MSCALE_Y];

        float gifX = transX + (xOffset * scaleX);
        float gifY = transY + (yOffset * scaleY);

        gif.setX(gifX);
        gif.setY(gifY);
    }




    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float origScale = getScale();
            matrix.set(savedMatrix);

            if (origScale * scaleFactor > maxScale) {
                scaleFactor = maxScale / origScale;
            } else if (origScale * scaleFactor < minScale) {
                scaleFactor = minScale / origScale;
            }

            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            constrainDrag();
            adjustGifPosition();
            return true;
        }
    }

    private float getScale() {
        matrix.getValues(m);
        return m[Matrix.MSCALE_X];
    }

    private void constrainDrag() {
        matrix.getValues(m);

        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];
        float scaleX = m[Matrix.MSCALE_X];
        float scaleY = m[Matrix.MSCALE_Y];

        float width = image.getDrawable().getIntrinsicWidth();
        float height = image.getDrawable().getIntrinsicHeight();

        float maxTransX = -(width * scaleX - image.getWidth());
        float maxTransY = -(height * scaleY - image.getHeight());

        if (transX > 0) transX = 0;
        if (transY > 0) transY = 0;
        if (transX < maxTransX) transX = maxTransX;
        if (transY < maxTransY) transY = maxTransY;

        m[Matrix.MTRANS_X] = transX;
        m[Matrix.MTRANS_Y] = transY;

        matrix.setValues(m);
    }
}
