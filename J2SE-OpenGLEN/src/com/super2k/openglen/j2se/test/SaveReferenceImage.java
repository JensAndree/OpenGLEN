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
package com.super2k.openglen.j2se.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.super2k.openglen.j2se.OpenGLENApplication;
import com.super2k.openglen.nibbler.CompatibilityRunner;
import com.super2k.openglen.test.OpenGLENTester;
import com.super2k.openglen.test.OpenGLENTester.ResultBuffer;

/**
 * This class runs the class containing the testcode and saves the
 * result as a reference image.
 * Run this class when a new reference image is needed for this test scenario.
 * @author Richard Sahlin
 *
 */
public class SaveReferenceImage implements OpenGLENTester.ResultListener {

    private final String TAG = this.getClass().getSimpleName();

    public final static String PROPERTY_SAVE_NAME = "savename";

    /**
     * Name of image to save
     */
    protected String mImageSaveName;

    /**
     * Image type is taken from savename extension.
     */
    protected String mImageType;

    public SaveReferenceImage() {
        super();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        SaveReferenceImage main = new SaveReferenceImage();
        main.setup(args);
    }

    /**
     * Starts the specified CompatibilityRunner, classname specified with 'runnerClass'
     * either as a system property or command line argument.
     * The runnerClass must also implement the OpenGLENTester interface.
     * Name of image to save result to must be set to 'savename' either as system property
     * or as a command line argument.
     * @param args Command line arguments.
     * @throws IllegalArgumentException If 'runnerclass' or 'savename' properties are not set.
     */
    public void setup(String[] args) {

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase(PROPERTY_SAVE_NAME)) {
                    mImageSaveName = args[i].substring(8);
                }
            }
        }
        String str = System.getProperty(PROPERTY_SAVE_NAME);
        if (str != null) {
            mImageSaveName = str;
        }
        if (mImageSaveName == null) {
            throw new IllegalArgumentException(
                    "Name of image to save is not set, set 'savename' property.");
        }
        int offset = mImageSaveName.indexOf('.');
        if (offset > 0) {
            mImageType = mImageSaveName.substring(offset + 1).trim();
        }
        if (mImageType == null || mImageType.length() == 0) {
            throw new IllegalArgumentException(
                    "Savename must have image mime extension, eg .jpeg .png etc");
        }

        OpenGLENApplication myApp = new OpenGLENApplication();
        myApp.startOpenGLEN(args);

        CompatibilityRunner runner = myApp.getCompatibilityRunner();
        ((OpenGLENTester)runner).addResultListener(this);

    }

    @Override
    public void result(ResultBuffer result) {
        saveResultBuffer(result, mImageSaveName, mImageType, BufferedImage.TYPE_INT_ARGB);
        try {
            BufferedImage source = ImageIO.read(new FileInputStream(mImageSaveName));
            J2SEImageComparison compare = new J2SEImageComparison(source, result.mBuffer);
            boolean same = compare.isSame(10, 10, 10, 10);
            if (!same) {
                throw new IllegalArgumentException(
                "Image compare failed! Loaded version of saved image does not pass comparison.");
            }
        } catch (FileNotFoundException fne) {
            throw new RuntimeException(fne);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Saves the resultbuffer as an image with the specified name and image type.
     * @param result The resultbuffer containing the image buffer.
     * @param imageName Name of image
     * @param imageType Type of image, eg "png", "jpeg"
     * @param type BufferedImage.TYPE_XX value
     * @throws IllegalArgumentException If any parameter is null
     * @throws RuntimeException If an io exception occurs while saving.
     */

    public void saveResultBuffer(ResultBuffer result, String imageName,
                                String imageType, int type) {

        try {
            //Fully opaque pixels, set alpha to 255.
            int size = result.mBuffer.length;
            for (int i = 0; i < size; i++) {
                result.mBuffer[i] |= 0x0ff000000;
            }


            BufferedImage bimg = new BufferedImage(result.mWidth,
                    result.mHeight,
                    type);
            bimg.setRGB(0, 0, result.mWidth, result.mHeight, result.mBuffer, 0, result.mWidth);
            File output = new File(imageName);
            ImageIO.write(bimg, imageType, output);

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
