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
package com.super2k.openglen.particles;

import java.util.Random;

import com.super2k.openglen.nibbler.BitmapHandler;

/**
 * Utilities for Particles
 * @author Richard Sahlin
 *
 */
public class ParticleUtils {

    public final static Random random = new Random();

    /**
     * Creates an array of random positions based on the specified bitmap, with
     * alpha as a mask. Use as a source when emitting particles it will create
     * the look of the opaque parts of the source bitmap.
     * If the alpha value at a random position is less than the alphaThreshold
     * then the value is discarded and a new value picked.
     * Note that using this method will most likely lead to duplicated particle positions.
     * @param bitmap The bitmap to base random values on, must be Bitmap class on Android
     * and BufferedImage on J2SE.
     * @param result An array where the result positions are stored.
     * @param bitmapHandler The bitmap handler to use.
     * @param anchorX Anchor X position, 0 for left side.
     * @param anchorY Anchor Y position, 0 for top.
     * @param count Number of (X and Y) values to create.
     * @param alphaThreshold If alpha in a position is less than this then the value will be masked off.
     * @throws IllegalArgumentException if any of the parameters are null, or if count is <= 0.
     */
    public static void createRandomPositions2D(Object bitmap, float[] result,
                                                  BitmapHandler bitmapHandler,
                                                  int anchorX, int anchorY,
                                                  int count, float alphaThreshold) {
        if (bitmap==null||bitmapHandler==null||count<=0) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        //Fetch the pixel data
        int[] imgBuffer = bitmapHandler.getBitmapArray(bitmap);
        createRandomPositions2D(imgBuffer, result,
                                bitmapHandler.getWidth(bitmap),
                                bitmapHandler.getHeight(bitmap), anchorX, anchorY,
                                count, alphaThreshold);
    }

    /**
     * Creates an array of random positions based on the specified bitmap, with
     * alpha as a mask. Use as a source when emitting particles it will create
     * the look of the opaque parts of the source bitmap.
     * If the alpha value at a random position is less than the alphaThreshold
     * then the value is discarded and a new value picked,otherwise that position
     * is stored.
     * Note that using this method will most likely lead to duplicated particle positions.
     * @param imgBuffer Array containing the image.
     * @param result An array where the result positions are stored.
     * @param width Width in pixels of the image
     * @param height Height in pixels of the image
     * @param anchorX Anchor X position, 0 for left side.
     * @param anchorY Anchor Y position, 0 for top.
     * @param count Number of (X and Y) values to create.
     * @param alphaThreshold If alpha in a position is less than this then the value will be masked off.
     * @throws IllegalArgumentException if any of the parameters are null, or if count is <= 0 or
     * if the width * height is not equal to the image array size.
     */
    public static void createRandomPositions2D(int[] imgBuffer, float[] result,
                                                int width, int height,
                                                int anchorX, int anchorY,
                                                int count, float alphaThreshold) {
        if (imgBuffer==null||imgBuffer.length!=width*height||count<=0) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        int index = 0;
        int threshold = ((int)(alphaThreshold*255))<<24;
        int x;
        int y;
        int loop = count<<1;
        while (index<loop) {
            x = (int)(random.nextFloat()*width);
            y = (int)(random.nextFloat()*height);
            //Check if position in array has valid alpha.
            if ((imgBuffer[y*width+x]&0x0ff000000)>threshold) {
                result[index++] = x-anchorX;
                result[index++] = y-anchorY;
            }
        }
    }

    /**
     * Creates an array of random positions and colors based on the specified bitmap, with
     * alpha as a mask. Use as a source when emitting particles it will create
     * the look of the opaque parts of the source bitmap.
     * If the alpha value at a random position is less than the alphaThreshold
     * then the value is discarded and a new value picked, otherwise that position
     * is stored together with the color of that position in the bitmap.
     * Note that using this method will most likely lead to duplicated particle positions.
     * @param imgBuffer Array containing the image.
     * @param resultPos An array where the result positions are stored.
     * @param resultColor An array where the color of the position is stored, RGB in one float each.
     * @param width Width in pixels of the image
     * @param height Height in pixels of the image
     * @param anchorX Anchor X position, 0 for left side.
     * @param anchorY Anchor Y position, 0 for top.
     * @param count Number of (X and Y) values to create.
     * @param alphaThreshold If alpha in a position is less than this then the value will be masked off.
     * nomrally used for objects that have alpha values.
     * @throws IllegalArgumentException if any of the parameters are null, or if count is <= 0 or
     * if the width * height is not equal to the image array size.
     */
    public static void createRandomPositionsAndColor2D(int[] imgBuffer, float[] resultPos,
                                                float[] resultColor,
                                                int width, int height,
                                                int anchorX, int anchorY,
                                                int count, float alphaThreshold) {
        if (imgBuffer==null||imgBuffer.length!=width*height||count<=0||
                resultPos.length!=count*2||resultColor.length!=count*3) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        int index = 0;
        int index2 = 0;
        int threshold = ((int)(alphaThreshold*255))<<24;
        int x;
        int y;
        int loop = count<<1;
        int col;
        while (index<loop) {
            x = (int)(random.nextFloat()*width);
            y = (int)(random.nextFloat()*height);
            //Check if position in array has valid alpha.
            if (((col = imgBuffer[y*width+x])&0x0ff000000)>threshold) {
                resultPos[index++] = x-anchorX;
                resultPos[index++] = y-anchorY;
                resultColor[index2++] = (float)(((col&0x0ff0000)>>>16))/255;
                resultColor[index2++] = (float)(((col&0x0ff00)>>>8))/255;
                resultColor[index2++] = (float)((col&0x0ff))/255;
            }
        }
    }

    /**
     * Creates an array of random positions and colors based on the specified bitmap, with
     * alpha as a mask. Use as a source when emitting particles it will create
     * the look of the opaque parts of the source bitmap.
     * If the alpha value at a random position is less than the alphaThreshold
     * then the value is discarded and a new value picked, otherwise that position
     * is stored together with the color of that position in the bitmap.
     * Note that using this method will most likely lead to duplicated particle positions.
     * @param imgBuffer Array containing the image.
     * @param resultPos An array where the result positions are stored.
     * @param resultColor An array where the color of the position is stored, RGB in one float each.
     * @param width Width in pixels of the image
     * @param height Height in pixels of the image
     * @param anchorX Anchor X position, 0 for left side.
     * @param anchorY Anchor Y position, 0 for top.
     * @param count Number of (X and Y) values to create.
     * @param alphaThreshold If alpha in a position is less than this then the value will be masked
     * off. Value is in range 0 to 1
     * @throws IllegalArgumentException if any of the parameters are null, or if count is <= 0 or
     * if the width * height is not equal to the image array size.
     */
    public static void createRandomPositionsAndColor2D(int[] imgBuffer, float[] resultPos,
                                                float[] resultColor,
                                                int width, int height,
                                                int anchorX, int anchorY,
                                                int count, float alphaThreshold,
                                                int backgroundColor) {
        if (imgBuffer==null||imgBuffer.length!=width*height||count<=0||
                resultPos.length!=count*2||resultColor.length!=count*3) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        float redBack = (float)(((backgroundColor&0x0ff0000)>>>16))/255;
        float greenBack = (float)(((backgroundColor&0x0ff00)>>>8))/255;
        float blueBack = (float)(((backgroundColor&0x0ff)))/255;
        int index = 0;
        int index2 = 0;
        int threshold = ((int)(alphaThreshold*255));
        int x;
        int y;
        int loop = count<<1;
        int col;
        float alpha;
        while (index<loop) {
            x = (int)(random.nextFloat()*width);
            y = (int)(random.nextFloat()*height);
            //Check if position in array has valid alpha.
            if (((col = imgBuffer[y*width+x])>>>24)>threshold) {
                alpha = ((float)(col>>>24))/255;
                resultPos[index++] = x-anchorX;
                resultPos[index++] = y-anchorY;
                resultColor[index2++] = ((float)(((col&0x0ff0000)>>>16))/255)*alpha+((1-alpha)*redBack);
                resultColor[index2++] = ((float)(((col&0x0ff00)>>>8))/255)*alpha+((1-alpha)*greenBack);
                resultColor[index2++] = ((float)((col&0x0ff))/255)*alpha+((1-alpha)*blueBack);
            }
        }
    }

    /**
     * Creates one array for X+Y position for all pixel values where the alpha component is greater then
     * the threshold value. One array will be created to hold RGB values (one float per component) for each
     * pixel that passes the alpha threshold test.
     * @param bitmap
     * @param bitmapHandler The BitmapHandler to use when fetching pixles from the bitmap.
     * @param anchorX Anchor X position, 0 for left side, in pixel units
     * @param anchorY Anchor Y position, 0 for top, in pixel units.
     * @param resolution Resolution of particles, a value of 3 will calculate 1 value per 3*3 square
     * @param alphaThreshold If alpha in a position is less than this then the value will be masked
     * off. Range 0 to 1
     * @param backgroundColor Color of background that bitmap blends into, this will be used as a factor
     * for color calculations. Resultpixel will be imgColor * (alpha) + ((1-alpha) * backgroundColor)
     * normally used for source bitmaps that have alpha values.
     * @return Arrays for positions (2 values per pixel) and colors (3 values per pixel)
     * @throws IllegalArgumentException If bitmap = null.
     */
    public static ParticleBitmapData createPositionsAndColor2D(Object bitmap,
            BitmapHandler bitmapHandler, int anchorX, int anchorY, int resolution,
            float alphaThreshold) {

        if (bitmap==null) {
            throw new IllegalArgumentException("Invalid parameter, bitmap is null");
        }

        //Fetch the pixel data
        int[] imgBuffer = bitmapHandler.getBitmapArray(bitmap);
        int width = bitmapHandler.getWidth(bitmap);
        int height = bitmapHandler.getHeight(bitmap);
        //Create array for full image then copy matching values.
        float[] positions = new float[width*height*2];
        float[] colors = new float[width*height*3];
        int col = 0;
        float alpha;
        int readindex = 0;
        int posindex = 0;
        int colindex = 0;
        for (int y = 0; y < height; y += resolution) {
            readindex = width * y;
            for (int x = 0; x < width; x += resolution) {
                if (((col = imgBuffer[readindex])>>>24) > alphaThreshold) {
//                    alpha = ((float)(col>>>24))/255;
                    positions[posindex++] = x - anchorX;
                    positions[posindex++] = y - anchorY;
                    colors[colindex++] = ((float)(((col&0x0ff0000)>>>16))/255);
                    colors[colindex++] = ((float)(((col&0x0ff00)>>>8))/255);
                    colors[colindex++] = ((float)((col&0x0ff))/255);
                }
                readindex += resolution;
            }
        }

        ParticleBitmapData result = new ParticleBitmapData();
        float[] resultPos = new float[posindex];
        float[] resultCol = new float[colindex];
        System.arraycopy(positions, 0, resultPos, 0, posindex);
        System.arraycopy(colors, 0, resultCol, 0, colindex);
        result.color = resultCol;
        result.position = resultPos;
        return result;
    }
}
