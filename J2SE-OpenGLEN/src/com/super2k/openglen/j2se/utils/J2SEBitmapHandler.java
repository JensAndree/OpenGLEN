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

package com.super2k.openglen.j2se.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import sun.awt.image.ToolkitImage;

import com.super2k.openglen.nibbler.BitmapHandler;
import com.super2k.openglen.nibbler.InputStreamResolver;

public class J2SEBitmapHandler extends BitmapHandler {

    /**
     * Max number of iterations to loop when checking for finished scale image.
     * This is an easy way of waiting for scaled images synchronously.
     */
    private final static int IMAGE_SCALE_TIMEOUT = 500;

    public J2SEBitmapHandler() {
        super();
    }

    @Override
    protected Object internalCreateBitmap(int width, int height, int format) {
        Object result = null;
        switch (format) {
            case BitmapHandler.FORMAT_ARGB4444:
                result = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_555_RGB);
                break;
            case BitmapHandler.FORMAT_ARGB8888:
                result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                break;
            case BitmapHandler.FORMAT_RGB565:
                result = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_565_RGB);
                break;
            default:
                throw new IllegalArgumentException("Can not create bitmap for format:"+format);
        }
        return result;
    }

    @Override
    public Object createBitmap(String name, InputStreamResolver resolver) throws IOException {

        BufferedImage img = ImageIO.read(resolver.openInputStream(name));

        return img;
    }

    @Override
    public Object createScaledBitmap(Object source, int width, int height) {

        if (source instanceof Image) {
            Image s = (Image)source;
            BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            internalDrawScaledBitmap(s, scaled, 0, 0, width, height);
            return scaled;
        }

        return null;
    }

    /**
     * Draws a scaled bitmap on the destination and waits for the scale operation to finish.
     * @param source
     * @param destination
     * @param x X pos in destination
     * @param y Y pos in destination
     * @param width Width of scaled bitmap
     * @param height Height of scaled bitmap
     */
    private void internalDrawScaledBitmap(Image source, Image destination, int x, int y, int width, int height) {
        int count = IMAGE_SCALE_TIMEOUT;
        while ((destination.getGraphics().drawImage(source, x, y, width, height, 0, 0,
                source.getWidth(null), source.getHeight(null), null)
                ==false)&&count>0) {
            try {
                Thread.sleep(10);
                count--;
            } catch (InterruptedException ie) {}

        }

    }

    @Override
    public void drawBitmap(Object source, Object destination, int xpos, int ypos) {

        if (source instanceof Image&&destination instanceof Image) {
            Image d = (Image)destination;
            int count = IMAGE_SCALE_TIMEOUT;
            while ((d.getGraphics().drawImage((Image)source, 0, 0, null)==false)&&count>0) {
                try {
                    Thread.sleep(10);
                    count--;
                } catch (InterruptedException ie) {}

            }
        }

    }

    @Override
    public int getHeight(Object bitmap) {
        if (bitmap instanceof ToolkitImage) {
            return ((ToolkitImage)bitmap).getHeight();
        } else if (bitmap instanceof Image) {
            return ((Image)bitmap).getHeight(null);
        }
        return 0;
    }

    @Override
    public int getWidth(Object bitmap) {
        if (bitmap instanceof ToolkitImage) {
            return ((ToolkitImage)bitmap).getWidth();
        } else if (bitmap instanceof Image) {
            return ((Image)bitmap).getWidth(null);
        }
        return 0;
    }

    @Override
    public void drawText(Object destination, int xpos, int ypos, String string, int color,
            float textSize) {

        if (destination instanceof Image) {
            Graphics g = ((Image)destination).getGraphics();
            g.setColor(new Color(color));
            g.drawString(string, xpos, ypos);
        }

    }

    @Override
    public void drawLine(Object bitmap, int x1, int y1, int x2, int y2, int color) {
        if (bitmap instanceof Image) {
            Graphics g = ((Image)bitmap).getGraphics();
            g.setColor(new Color(color, true));
            g.drawLine(x1, y1, x2, y2);
        }

    }

    @Override
    public void recycle(Object bitmap) {
        //On J2SE we don't need to do anything.

    }

    @Override
    public Object createScaledBitmap(Object source, int width, int height, int format) {
        if (source instanceof Image) {
            Object result = internalCreateBitmap(width, height, format);
            internalDrawScaledBitmap((Image)source, (Image)result, 0, 0, width, height);
            return result;
        }
        throw new IllegalArgumentException("Unsupported Image class: "+source);
    }

    @Override
    public int[] getBitmapArray(Object bitmap) {
        if (bitmap==null||!(bitmap instanceof BufferedImage)) {
            throw new IllegalArgumentException("Unsupported Image class: "+bitmap);
        }
        BufferedImage img = (BufferedImage)bitmap;
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        int[] result = new int[w*h];
        img.getRGB(0, 0, w, h, result, 0, w);
        return result;
    }

    @Override
    protected void internalFill(Object bitmap, int x, int y, int width, int height, int color) {
        if (bitmap==null||!(bitmap instanceof BufferedImage)) {
            throw new IllegalArgumentException("Unsupported Image class: "+bitmap);
        }
        BufferedImage img = (BufferedImage)bitmap;
        Graphics g = img.getGraphics();
        g.setColor(new Color(color, true));
        g.fillRect(x, y, width, height);

    }

}
