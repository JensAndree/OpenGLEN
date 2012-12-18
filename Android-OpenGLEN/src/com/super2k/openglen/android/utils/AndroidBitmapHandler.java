/* Copyright 2012 Richard Sahlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.super2k.openglen.android.utils;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.super2k.openglen.nibbler.BitmapHandler;
import com.super2k.openglen.nibbler.InputStreamResolver;

/**
 * Android implementation of the BitmapHandler.
 * @author Richard Sahlin
 *
 */
public class AndroidBitmapHandler extends BitmapHandler {

    public final static String TAG = Class.class.getSimpleName();

    Paint mPaint = new Paint();

    @Override
    public Object internalCreateBitmap(int width, int height, int format) {

        Object result = null;
        switch (format) {
            case BitmapHandler.FORMAT_ARGB4444:
                result = Bitmap.createBitmap(width, height, Config.ARGB_4444);
                break;
            case BitmapHandler.FORMAT_ARGB8888:
                result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                break;
            case BitmapHandler.FORMAT_RGB565:
                result = Bitmap.createBitmap(width, height, Config.RGB_565);
                break;
            default:
                throw new IllegalArgumentException("Can't create bitmap with format:"+format);
        }
        return result;
    }

    @Override
    public void drawBitmap(Object source, Object destination, int xpos, int ypos) {

        Canvas c = new Canvas((Bitmap)destination);
        c.drawBitmap((Bitmap)source, xpos, ypos, null);

    }

    @Override
    public Object createBitmap(String name, InputStreamResolver resolver) throws IOException {

        return BitmapFactory.decodeStream(resolver.openInputStream(name));

    }

    @Override
    public Object createScaledBitmap(Object source, int width, int height) {

        return Bitmap.createScaledBitmap((Bitmap)source, width, height, true);

    }

    @Override
    public int getWidth(Object bitmap) {
        return ((Bitmap)bitmap).getWidth();
    }

    @Override
    public int getHeight(Object bitmap) {
        return ((Bitmap)bitmap).getHeight();
    }

    @Override
    public void drawText(Object destination, int xpos, int ypos, String string, int color, float textSize) {

        if (destination instanceof Bitmap) {

            Canvas c = new Canvas((Bitmap)destination);
            mPaint.setColor(color);
            mPaint.setTextSize(textSize);
            c.drawText(string, xpos, ypos, mPaint);
        } else {
            Log.d(TAG, "Not implemented drawText for "+destination);
        }
    }

    @Override
    public void drawLine(Object bitmap, int x1, int y1, int x2, int y2, int color) {

        if (bitmap instanceof Bitmap) {

            Canvas c = new Canvas((Bitmap)bitmap);
            mPaint.setColor(color);
            c.drawLine(x1, y1, x2, y2, mPaint);
        } else {
            Log.d(TAG, "Not implemented drawText for "+bitmap);
        }

    }

    @Override
    public void recycle(Object bitmap) {
        if (bitmap instanceof Bitmap) {
            ((Bitmap)bitmap).recycle();
        } else {
            Log.d(TAG, "Not implemented recycle for "+bitmap);
        }
    }

    @Override
    public Object createScaledBitmap(Object source, int width, int height, int format) {
        Bitmap result = (Bitmap)internalCreateBitmap(width, height, format);
        Canvas c = new Canvas(result);
        Rect srcRect = new Rect(0, 0, getWidth(source), getHeight(source));
        Rect dstRect = new Rect(0, 0, width, height);
        c.drawBitmap((Bitmap)source, srcRect, dstRect, null);
        return result;
    }

    @Override
    public int[] getBitmapArray(Object bitmap) {
        if (bitmap == null || !(bitmap instanceof Bitmap)) {
            throw new IllegalArgumentException("Invalid bitmap type: " + bitmap);
        }
        Bitmap bmp = (Bitmap)bitmap;
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] result = new int[w*h];
        bmp.getPixels(result, 0, w, 0, 0, w, h);

        return result;
    }

    @Override
    protected void internalFill(Object bitmap, int x, int y, int width, int height, int color) {
        if (bitmap == null || !(bitmap instanceof Bitmap)) {
            throw new IllegalArgumentException("Invalid bitmap type: "+bitmap);
        }
        Canvas c = new Canvas((Bitmap)bitmap);
        mPaint.setColor(color);
        c.drawRect(x, y, x + width, y + height, mPaint);
    }

}
