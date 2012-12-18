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
package com.super2k.openglen.nibbler;

import java.io.IOException;

/**
 * Class to take care of creation of Bitmaps to be used by Texture objects.
 * This will be done in a platform dependant way, on Android bitmap
 * objects will be android.graphics.Bitmap and on J2SE java.awt.Image
 * @author Richard Sahlin
 *
 */
public abstract class BitmapHandler {

    /**
     * a 32 bit bitmap with 8 bit alpha.
     */
    public final static int FORMAT_ARGB8888 = 0x07000;

    /**
     * 16 bit bitmap with 4 bit alpha.
     */
    public final static int FORMAT_ARGB4444 = 0x07001;

    /**
     * 16 bit bitmap without alpha.
     */
    public final static int FORMAT_RGB565 = 0x07002;

    /**
     * Internal method to create a bitmap
     * @param width Width of bitmap
     * @param height Height of bitmap
     * @param format Format of bitmap
     * FORMAT_ARGB8888, FORMAT_ARGB4444 or FORMAT_RGB565
     * @return A platform specific Bitmap object of correct size.
     * @throws IllegalArgumentException If format is not one of the following:
     * FORMAT_ARGB8888, FORMAT_ARGB4444 or FORMAT_RGB565
     */
    protected abstract Object internalCreateBitmap(int width, int height, int format);

    /**
     * Internal wrapper method to fill bitmap with color.
     * Fills the specified bitmap region with a color.
     * @param x Start x position of fill region.
     * @param y Start y position of fill region.
     * @param width Width of fill region, if width is <= 0 nothing is filled.
     * @param height Height of fill region, if width is <= 0 nothing is filled.
     * @param color Color to fill, ARGB - alpha in most significant byte and blue
     * in least significant byte.
     * @throws IllegalArgumentException If bitmap is null or wrong object
     */
    protected abstract void internalFill(Object bitmap, int x, int y, int width,
                                         int height, int color);

    /**
     * Creates an empty bitmap with the specified width and height.
     * @param width Width of bitmap in pixels
     * @param height Height of bitmap in pixels
     * @param format The bitformat for the bitmap to create.
     * FORMAT_ARGB8888, FORMAT_ARGB4444 or FORMAT_RGB565
     * @return A platform specific Bitmap object of correct size.
     * @throws IllegalArgumentException if width or height is <= 0 or if format is invalid.
     */
    public Object createBitmap(int width, int height, int format) {
        validateSize(width, height);
        return internalCreateBitmap(width, height, format);
    }

    /**
     * Creates a bitmap with the specified width and height and fill color.
     * @param width Width of the bitmap
     * @param height Height of the bitmap
     * @param format The bitformat for the bitmap to create.
     * FORMAT_ARGB8888, FORMAT_ARGB4444 or FORMAT_RGB565
     * @param color Fill color in the form of 0xARGB, alpha in most significant byte.
     * @return A platform specific Bitmap object with the specified size and fill color.
     * @throws IllegalArgumentException if width or height is <= 0 or if format is invalid.
     */
    public Object createBitmap(int width, int height, int format, int color) {
        Object bitmap = createBitmap(width, height, format);
        fill(bitmap, 0, 0, width, height, color);
        return bitmap;
    }

    /**
     * Fills the specified bitmap region with a color.
     * @param x Start x position of fill region.
     * @param y Start y position of fill region.
     * @param width Width of fill region, if width is <= 0 nothing is filled.
     * @param height Height of fill region, if width is <= 0 nothing is filled.
     * @param color Color to fill, ARGB - alpha in most significant byte and blue
     * in least significant byte.
     * @throws IllegalArgumentException If bitmap is null or wrong object
     */
    public void fill(Object bitmap, int x, int y, int width, int height, int color) {
        if (width<=0||height<=0) {
            return;
        }
        internalFill(bitmap, x, y, width, height, color);
    }

    /**
     * Creates a copy of the specified bitmap and returns with the specified bitmap format.
     * @param source Platform specific bitmap source (Bitmap on Android, Image on AWT)
     * @param format The bitmap format to use for the created bitmap, one of:
     * FORMAT_ARGB8888, FORMAT_ARGB4444 or FORMAT_RGB565
     * @return A platform specific bitmap object with a copy of the source image.
     * @throws IllegalArgumentException If source is null or wrong object, or if format is invalid.
     */
    public Object createBitmap(Object source, int format) {
        Object bitmap = createBitmap(getWidth(source), getHeight(source), format);
        drawBitmap(source, bitmap, 0, 0);
        return bitmap;
    }


    /**
     * Returns the bitmap as an int array, each pixel is represented
     * by one int. LSB is blue value.
     * @param bitmap
     * @return An array holding the pixel values for the bitmap,
     * length of array is width * height if bitmap
     * @throws IllegalArgumentException if bitmap is null or wrong object type
     * For Android this must be Bitmap, for J2SE an instance of java.awt.Image
     */
    public abstract int[] getBitmapArray(Object bitmap);

    /**
     * Validate size
     * @param width
     * @param height
     * @throws IllegalArgumentException If width or height is <= 0.
     */
    private void validateSize(int width, int height) {
        if (width<=0||height<=0) {
            throw new IllegalArgumentException("Size of bitmap is invalid: "+width+", "+height);
        }
    }

    /**
     * Draws the source bitmap object onto the destination at the specified position.
     * @param source The source bitmap to draw onto the destination.
     * @param destination The destination bitmap where the source is drawn.
     * @param xpos X position where source is drawn.
     * @param ypos Y position where source is drawn.
     * @throws IllegalArgumentException If source or destination is null or wrong bitmap type
     */
    public abstract void drawBitmap(Object source, Object destination, int xpos, int ypos);

    /**
     * Creates a bitmap from the specified bitmap into a platform dependent object.
     * @param name Name (and path) of the Bitmap to load. Must be in a format that is understood
     * by the platform.
     * @param resolver Platform dependent resolver to use when loading bitmap.
     * @return A platform specific bitmap object with the specified image.
     * @throws IOException If there is an exception opening or reading the file.
     */
    public abstract Object createBitmap(String name, InputStreamResolver resolver)
                    throws IOException;

    /**
     * Creates a scaled version of the specified bitmap.
     * @param source Bitmap object to create scaled copy of.
     * @param width Width of scaled image
     * @param height Height of scaled image.
     * @return The scaled image in a platform specific object
     * @throws IllegalArgumentException If source is null or the wrong bitmap type
     */
    public abstract Object createScaledBitmap(Object source, int width, int height);

    /**
     * Creates a scaled version of the specified bitmap.
     * @param source Bitmap object to create scaled copy of.
     * @param width
     * @param height
     * @return A platform specific object with the scaled bitmap.
     */
    public abstract Object createScaledBitmap(Object source, int width, int height, int format);

    /**
     * Returns the width of the bitmap object.
     * @param bitmap
     * @return Width in pixels of bitmap.
     */
    public abstract int getWidth(Object bitmap);

    /**
     * Returns the height of the bitmap object.
     * @param bitmap
     * @return Height in pixels of the bitmap
     */
    public abstract int getHeight(Object bitmap);

    /**
     * Draws text with the default (currently selected) font on the specified destination.
     * @param destination Destination object for the drawing, platform dependant bitmap.
     * @param xpos The x position of the text
     * @param ypos The y position of the text
     * @param string The string to draw.
     * @param color The color to draw the string with.
     * @param textSize Size of text.
     */
    public abstract void drawText(Object destination, int xpos, int ypos, String string, int color,
                                  float textSize);

    /**
     * Draws a line (one pixel in width) in the specified color.
     * @param bitmap
     * @param x1 Start x
     * @param y1 Start y
     * @param x2 End x
     * @param y2 End y
     * @param color Color to draw line in (ARGB) - Alpha in most significant byte, blue in least
     * significant byte.
     */
    public abstract void drawLine(Object bitmap, int x1, int y1, int x2, int y2, int color);

    /**
     * Free the memory of the bitmap object.
     * On Android this will be a call to Bitmap.recycle();
     * @param bitmap The bitmap object to free.
     */
    public abstract void recycle(Object bitmap);

}
