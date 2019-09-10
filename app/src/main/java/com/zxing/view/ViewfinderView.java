/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxing.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.doughnut.R;
import com.google.zxing.ResultPoint;
import com.zxing.camera.CameraManager;

import java.util.Collection;
import java.util.HashSet;

/**
 *
 */
public final class ViewfinderView extends View {

    private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192,
            128, 64};
    private static final long ANIMATION_DELAY = 10L;
    private static final int OPAQUE = 0xFF;

    private final Paint paint;
    private Bitmap resultBitmap;
    private final int maskColor;
    private final int resultColor;
    private final int frameColor;
    private final int filletColor;
    private final int laserColor;
    private final int resultPointColor;
    private int scannerAlpha;
    private int[] mMinColors;
    private int slideTop;
    private Collection<ResultPoint> possibleResultPoints;
    private Collection<ResultPoint> lastPossibleResultPoints;
    private Rect frame;

    // This constructor is used when the class is built from an XML resource.
    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every
        // time in onDraw().
        paint = new Paint();
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.viewfinder_mask);
        resultColor = resources.getColor(R.color.result_view);
        frameColor = resources.getColor(R.color.color_white);
        filletColor = resources.getColor(R.color.color_detail_receive);
        laserColor = resources.getColor(R.color.viewfinder_laser);
        resultPointColor = resources.getColor(R.color.possible_result_points);
        mMinColors = new int[]{resources.getColor(R.color.transparent), resources.getColor(R.color.color_detail_receive), resources.getColor(R.color.color_detail_receive), resources.getColor(R.color.transparent)};
        scannerAlpha = 0;
        possibleResultPoints = new HashSet<ResultPoint>(5);
        slideTop = context.getResources().getDisplayMetrics().heightPixels / 2;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDraw(Canvas canvas) {
        // 中间的扫描框，你要修改扫描框的大小，去CameraManager里面修改
        frame = CameraManager.get().getFramingRect();
        if (frame == null) {
            return;
        }
        // 获取屏幕的宽和高
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        // Draw the exterior (i.e. outside the framing rect) darkened
        //画出扫描框外面的阴影部分，共四个部分，扫描框的上面到屏幕上面，扫描框的下面到屏幕下面
        //扫描框的左边面到屏幕左边，扫描框的右边到屏幕右边  
        paint.setColor(resultBitmap != null ? resultColor : maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1,
                paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);

        if (resultBitmap != null) {
            // Draw the opaque result bitmap over the scanning rectangle
            paint.setAlpha(OPAQUE);
            canvas.drawBitmap(resultBitmap, frame.left, frame.top, paint);
        } else {
//			//画扫描框边上的角，总共8个部分
//			// Draw a two pixel solid black border inside the framing rect
            paint.setColor(frameColor);
//            canvas.drawRoundRect(frame.left, frame.top, frame.right, frame.bottom, 20, 20, paint);
            canvas.drawRect(frame.left, frame.top, frame.right + 1,
                    frame.top + 2, paint);
            canvas.drawRect(frame.left, frame.top + 2, frame.left + 2,
                    frame.bottom - 1, paint);
            canvas.drawRect(frame.right - 1, frame.top, frame.right + 1,
                    frame.bottom - 1, paint);
            canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1,
                    frame.bottom + 1, paint);

            //画四个角上面的绿线
            float filletWith = CameraManager.get().getfilletWith(15, TypedValue.COMPLEX_UNIT_DIP);
            float arctWith = CameraManager.get().getfilletWith(8, TypedValue.COMPLEX_UNIT_DIP);
            float cutWith = CameraManager.get().getfilletWith(3, TypedValue.COMPLEX_UNIT_DIP);
            paint.setColor(filletColor);
            paint.setStrokeWidth((float) 10.0);              //线宽
            paint.setStyle(Paint.Style.FILL);
            canvas.drawArc(frame.left - arctWith / 4, frame.top - arctWith / 4, frame.left + arctWith,
                    frame.top + arctWith, 135, 180, false, paint);
            canvas.drawArc(frame.right - arctWith, frame.top - arctWith / 4, frame.right + arctWith / 4,
                    frame.top + arctWith, -135, 180, false, paint);
            canvas.drawArc(frame.left - arctWith / 4, frame.bottom - arctWith, frame.left + arctWith,
                    frame.bottom + arctWith / 4, 45, 180, false, paint);
            canvas.drawArc(frame.right - arctWith, frame.bottom - arctWith, frame.right + arctWith / 4,
                    frame.bottom + arctWith / 4, -45, 180, false, paint);
            paint.setStrokeWidth(CameraManager.get().getfilletWith(4, TypedValue.COMPLEX_UNIT_DIP));
            canvas.drawLine(frame.left + cutWith, frame.top, frame.left + filletWith, frame.top, paint);//左上角横着的线
            canvas.drawLine(frame.left, frame.top + cutWith, frame.left, frame.top + filletWith, paint);//左上角竖着的线
//
            canvas.drawLine(frame.left, frame.bottom - cutWith, frame.left, frame.bottom - filletWith, paint);//左下角竖着的线
            canvas.drawLine(frame.left + cutWith, frame.bottom, frame.left + filletWith, frame.bottom, paint);//左下角横着的线

            canvas.drawLine(frame.right - cutWith, frame.top, frame.right - filletWith, frame.top, paint);//右上角横着的线
            canvas.drawLine(frame.right, frame.top + cutWith, frame.right, frame.top + filletWith, paint);//右上角竖着的线


            canvas.drawLine(frame.right, frame.bottom - cutWith, frame.right, frame.bottom - filletWith, paint);//右下角竖着的线
            canvas.drawLine(frame.right - cutWith, frame.bottom, frame.right - filletWith, frame.bottom, paint);//右下角横着的线

            // Draw a red "laser scanner" line through the middle to show
            // decoding is active
            //画扫描框下面的字
//            paint.setColor(frameColor);
//            paint.setTextAlign(Paint.Align.CENTER);
//            float textSize = CameraManager.get().getfilletWith(13, TypedValue.COMPLEX_UNIT_SP);
//            float padding = CameraManager.get().getfilletWith(30, TypedValue.COMPLEX_UNIT_DIP);
//            paint.setTextSize(textSize);
//            scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.length;
//            int middle = frame.height() / 2 + frame.top;
//            canvas.drawText(getContext().getString(R.string.qr_text), width / 2, frame.bottom + padding, paint);

            Paint paint1 = new Paint();
            float[] loaction = new float[]{0.01F, 0.1F, 0.9F, 1.0F};
            int middle = frame.height() / 2 + frame.top;
            Shader mShader = new LinearGradient(frame.left, middle, frame.right, middle, mMinColors, loaction, Shader.TileMode.CLAMP);
            //把渐变设置到笔刷
            paint1.setShader(mShader);
            paint1.setColor(filletColor);
            paint1.setStyle(Paint.Style.FILL);
            paint1.setStrokeWidth(CameraManager.get().getfilletWith(1, TypedValue.COMPLEX_UNIT_DIP)); //设置宽度

            slideTop += 6;
            if (slideTop >= frame.bottom) {
                slideTop = frame.top;
            }
            if (slideTop >= frame.top) {
                //绘制中间的线
                canvas.drawRect(frame.left + 3, slideTop, frame.right - 3,
                        slideTop + 3, paint1);
            } else {
                canvas.drawRect(frame.left + 3, slideTop, frame.right - 3,
                        slideTop + 3, paint1);//中间的横线
            }

            Collection<ResultPoint> currentPossible = possibleResultPoints;
            Collection<ResultPoint> currentLast = lastPossibleResultPoints;
            if (currentPossible.isEmpty()) {
                lastPossibleResultPoints = null;
            } else {
                possibleResultPoints = new HashSet<ResultPoint>(5);
                lastPossibleResultPoints = currentPossible;
                paint.setAlpha(OPAQUE);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 6.0f, paint);
                }
            }
            if (currentLast != null) {
                paint.setAlpha(OPAQUE / 2);
                paint.setColor(resultPointColor);
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(frame.left + point.getX(), frame.top
                            + point.getY(), 3.0f, paint);
                }
            }

            // Request another update at the animation interval, but only
            // repaint the laser line,
            // not the entire viewfinder mask.
            //只刷新扫描框的内容，其他地方不刷新
            postInvalidateDelayed(ANIMATION_DELAY, frame.left, frame.top,
                    frame.right, frame.bottom);
        }
    }

    public void drawViewfinder() {
        resultBitmap = null;
        invalidate();
    }

    /**
     * Draw a bitmap with the result points highlighted instead of the live
     * scanning display.
     *
     * @param barcode An image of the decoded barcode.
     */
    public void drawResultBitmap(Bitmap barcode) {
        resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        possibleResultPoints.add(point);
    }
}
