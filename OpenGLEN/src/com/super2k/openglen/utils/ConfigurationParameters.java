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
package com.super2k.openglen.utils;

import java.util.StringTokenizer;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.RenderSetting;
import com.super2k.openglen.SurfaceConfiguration;



/**
 * Helper class to set configuration options read from system properties.
 * This class shall have the properties that are for OpenGLEN settings, not application or
 * platform dependant configurations.
 * Ie Android specific configurations shall not be put here.
 * @author Richard Sahlin
 *
 */
public class ConfigurationParameters {

    private final static String TAG = "ConfigurationParameters";

    public final static String PIXELFORMAT_RGB_565 = "RGB_565";
    public final static String PIXELFORMAT_RGB_888 = "RGB_888";
    public final static String PIXELFORMAT_RGBA_4444 = "RGBA_4444";
    public final static String PIXELFORMAT_RGBA_5551 = "RGBA_5551";
    public final static String PIXELFORMAT_RGBA_8888 = "RGBA_8888";
    public final static String PIXELFORMAT_RGBX_8888 = "RGBX_8888";

    private final static String[] PIXELFORMAT_STR = new String[]{
        PIXELFORMAT_RGB_565,
        PIXELFORMAT_RGB_888,
        PIXELFORMAT_RGBA_4444,
        PIXELFORMAT_RGBA_5551,
        PIXELFORMAT_RGBA_8888,
        PIXELFORMAT_RGBX_8888,
 };
    private final static int[] PIXELFORMAT_INT = new int[]{
        ConstantValues.PIXELFORMAT_RGB_565,
        ConstantValues.PIXELFORMAT_RGB_888,
        ConstantValues.PIXELFORMAT_RGBA_4444,
        ConstantValues.PIXELFORMAT_RGBA_5551,
        ConstantValues.PIXELFORMAT_RGBA_8888,
        ConstantValues.PIXELFORMAT_RGBX_8888};

    /**
     * int property for EGL multisampling, when > 1 an EGL surface with multisampling will be
     * requested.
     * Allowed values are:
     * 0, 1 - no multisample buffer
     * 2, 4, 8 etc. multisample buffer
     */
    public final static String PROPERTY_EGL_MULTISAMPLES =
        "com.super2k.openglen.eglmultisample";

    public final static String PROPERTY_EGL_RED_BITS =
        "com.super2k.openglen.redbits";
    public final static String PROPERTY_EGL_GREEN_BITS =
        "com.super2k.openglen.greenbits";
    public final static String PROPERTY_EGL_BLUE_BITS =
        "com.super2k.openglen.bluebits";
    public final static String PROPERTY_EGL_ALPHA_BITS =
        "com.super2k.openglen.alphabits";
    public final static String PROPERTY_EGL_DEPTH_BITS =
        "com.super2k.openglen.depth";

    /**
     * Boolean flag to enable or disable multisampling.
     */
    public final static String PROPERTY_OPENGLEN_ENABLEMULTISAMPLING =
        "com.super2k.openglen.enablemultisampling";

    /**
     * cullmode, valid values are taken from the CULLMODE_STR_TABLE
     */
    public final static String PROPERTY_OPENGLEN_CULLMODE =
        "com.super2k.openglen.cullmode";

    /**
     * Set the depth test function, valid values are taken from the
     * DEPTH_FUNCTION_STR_TABLE
     */
    public final static String PROPERTY_OPENGLEN_DEPTHFUNCTION =
        "com.super2k.openglen.depthfunction";

    /**
     * Property for setting GL clear buffer function, this controls if buffers shall be
     * cleared automatically between frames. Valid values are taken from
     * CLEAR_FUNCTION_STR_TABLE and can be ored together with '|'
     * eg color | depth
     */
    public final static String PROPERTY_OPENGLEN_CLEAR_FUNCTION =
        "com.super2k.openglen.clearfunction";

    /**
     * Boolean property to use vertex buffer objects.
     * Allowed values are "true" or "false"
     */
    public final static String PROPERTY_OPENGLEN_USE_VBO =
        "com.super2k.openglen.usevbo";

    public final static String CLEAR_FUNCTION_COLOR = "color";
    public final static String CLEAR_FUNCTION_DEPTH = "depth";
    public final static String CLEAR_FUNCTION_STENCIL = "stencil";
    public final static String CLEAR_FUNCTION_NONE = "none";
    public final static String CLEAR_FUNCTION_DISABLE = "disable";

    /**
     * The allowed values for gl clear buffer function, setting a value of none or disabled
     * will turn off clear function between frames.
     * Values can be ored together, ie color | depth | stencil will clear all buffers.
     */
    public final static String[] CLEAR_FUNCTION_STR_TABLE = new String[] {
                                            CLEAR_FUNCTION_COLOR,
                                            CLEAR_FUNCTION_DEPTH,
                                            CLEAR_FUNCTION_STENCIL,
                                            CLEAR_FUNCTION_NONE,
                                            CLEAR_FUNCTION_DISABLE };

    protected final static int[] CLEAR_FUNCTION_VALUE_TABLE = new int[] {
                                            ConstantValues.COLOR_BUFFER_BIT,
                                            ConstantValues.DEPTH_BUFFER_BIT,
                                            ConstantValues.STENCIL_BUFFER_BIT, 0, 0};


    public final static String CULLMODE_DISABLE = "disable";
    public final static String CULLMODE_NONE = "none";
    public final static String CULLMODE_FRONT = "front";
    public final static String CULLMODE_BACK = "back";
    public final static String CULLMODE_FRONT_AND_BACK = "front_and_back";

    public final static String[] CULLMODE_STR_TABLE = new String[] {
                        CULLMODE_NONE,
                        CULLMODE_DISABLE,
                        CULLMODE_FRONT,
                        CULLMODE_BACK,
                        CULLMODE_FRONT_AND_BACK };

    public final static int[] CULLMODE_VALUE_TABLE = new int[] {0, 0, ConstantValues.CULL_FRONT,
                                                        ConstantValues.CULL_BACK,
                                                        ConstantValues.CULL_FRONT_AND_BACK};

    public final static String DEPTH_FUNCTION_GEQUAL = "gequal";
    public final static String DEPTH_FUNCTION_LEQUAL = "lequal";
    public final static String DEPTH_FUNCTION_DISABLE = "disable";
    public final static String DEPTH_FUNCTION_GREATER = "greater";
    public final static String DEPTH_FUNCTION_LESS = "less";
    public final static String DEPTH_FUNCTION_EQUAL = "equal";
    public final static String DEPTH_FUNCTION_NOTEQUAL = "notequal";
    public final static String DEPTH_FUNCTION_ALWAYS = "always";
    public final static String DEPTH_FUNCTION_NEVER = "never";

    /**
     * The defined depth test functions
     */
    public final static String[] DEPTH_FUNCTION_STR_TABLE = new String[] {
        DEPTH_FUNCTION_GEQUAL,
        DEPTH_FUNCTION_LEQUAL,
        DEPTH_FUNCTION_DISABLE,
        DEPTH_FUNCTION_GREATER,
        DEPTH_FUNCTION_LESS,
        DEPTH_FUNCTION_EQUAL,
        DEPTH_FUNCTION_NOTEQUAL,
        DEPTH_FUNCTION_ALWAYS,
        DEPTH_FUNCTION_NEVER };

    protected final static int[] DEPTH_FUNCTION_VALUE_TABLE = new int[] {
                                                            ConstantValues.GEQUAL,
                                                            ConstantValues.LEQUAL,
                                                            ConstantValues.NONE,
                                                            ConstantValues.GREATER,
                                                            ConstantValues.LESS,
                                                            ConstantValues.EQUAL,
                                                            ConstantValues.NOTEQUAL,
                                                            ConstantValues.ALWAYS,
                                                            ConstantValues.NEVER};


    /**
     * Windowformat, use any of the PixelFormat String values.
     * RGBX_8888, RGBA_8888, RGB_565, RGBA_5551, RGB_888 or RGBA_4444
     */
    public final static String PROPERTY_WINDOW_FORMAT =
        "com.super2k.openglen.windowformat";

    /**
     * Read configuration parameters from the Java system properties and store
     * in RenderSettings.
     * Note that the renderSetting parameters may be overridden by the application when it starts.
     * @param renderSetting The class to store settings read from System properties
     */
    public static void getRenderConfiguration(RenderSetting renderSetting) {
        int val;
        if ((val = getBooleanProperty(PROPERTY_OPENGLEN_ENABLEMULTISAMPLING)) != -1 )   {
            if (val == 0){
                renderSetting.enableMultisampling(false);
            }
            else        {
                renderSetting.enableMultisampling(true);
            }

        }

        String cullFace = System.getProperty(PROPERTY_OPENGLEN_CULLMODE);
        if (cullFace != null) {
            renderSetting.setCullFace(getIntFromString(cullFace, CULLMODE_STR_TABLE,
                                                       CULLMODE_VALUE_TABLE));
        }

        String depthFunc = System.getProperty(PROPERTY_OPENGLEN_DEPTHFUNCTION);
        if (depthFunc != null) {
            renderSetting.setDepthFunc(getIntFromString(depthFunc, DEPTH_FUNCTION_STR_TABLE,
                                                        DEPTH_FUNCTION_VALUE_TABLE));
        }

        String clearStr = System.getProperty(PROPERTY_OPENGLEN_CLEAR_FUNCTION);
        if (clearStr != null) {
            renderSetting.setClearFunction(getMultiIntFromString(clearStr,
                                                            CLEAR_FUNCTION_STR_TABLE,
                                                            CLEAR_FUNCTION_VALUE_TABLE, "|"));
        }

    }

    /**
     * Read configuration parameters from the Java system properties and store
     * in surfaceConfig (EGL related).
     * Will read the PROPERTY_EGL_XX properties and store set values in the config class.
     * This should be called before EGL is created in order to get the configuration needed for
     * creating EGL.
     * @param surfaceConfig Class to store surface (EGL) configurations in.
     */
    public static void getSurfaceConfiguration(SurfaceConfiguration surfaceConfig) {

        int val;
        if ((val = getIntProperty(PROPERTY_EGL_MULTISAMPLES)) != -1)    {
            Log.d(TAG,"Set PROPERTY_EGL_MULTISAMPLES: " + val);
            surfaceConfig.setSamples(val);
        }

        //Fetch surface bit configuration
        if ((val = getIntProperty(PROPERTY_EGL_RED_BITS)) != -1 )   {
            surfaceConfig.setRedBits(val);
        }
        if ((val = getIntProperty(PROPERTY_EGL_GREEN_BITS)) != -1 )   {
            surfaceConfig.setGreenBits(val);
        }
        if ((val = getIntProperty(PROPERTY_EGL_BLUE_BITS)) != -1 )   {
            surfaceConfig.setBlueBits(val);
        }
        if ((val = getIntProperty(PROPERTY_EGL_ALPHA_BITS)) != -1 )   {
            surfaceConfig.setAlphaBits(val);
        }
        if ((val = getIntProperty(PROPERTY_EGL_DEPTH_BITS)) != -1 )   {
            surfaceConfig.setDepthBits(val);
        }

    }


    /**
     * Looks for String str in strTable and returns the corresponding value from the intTable
     * @param str
     * @param strTable
     * @param intTable
     * @return The corresponding value from the intTable
     * @throws IllegalArgumentException If no match was found, or strTable and intTable are null
     * or not same size.
     */
    protected static int getIntFromString(String str, String[] strTable, int[] intTable) {
        if (strTable == null || intTable == null || strTable.length != intTable.length) {
            throw new IllegalArgumentException("Invalid argument.");
        }

        int size = strTable.length;
        for (int i = 0; i < size; i++) {
            if (str.equalsIgnoreCase(strTable[i])) {
                return intTable[i];
            }
        }
        throw new IllegalArgumentException("Could not find a matching value for: " + str);

    }

    /**
     * Looks for String str in strTable and returns the corresponding value from the intTable,
     * more than one value is allowed. Values are ored together.
     * @param str
     * @param strTable
     * @param intTable
     * @param delimiter Delimeter chars for multiple values
     * @return The corresponding value from the intTable
     * @throws IllegalArgumentException If no match was found, or strTable and intTable are null
     * or not same size.
     */
    protected static int getMultiIntFromString(String str, String[] strTable, int[] intTable,
                                                String delimiter) {

        int value = 0;
        int count = 0;
        //Find number of tokens using StringTokenizer
        StringTokenizer st = new StringTokenizer(str, delimiter);
        while (st.hasMoreTokens()) {
            if (count++ == 0) {
                value = getIntFromString(st.nextToken(), strTable, intTable);
            } else {
                value |= getIntFromString(st.nextToken(), strTable, intTable);
            }

        }
        return value;
    }


    /**
     * Checks if the property is set to true or false, returns 1 for true and 0 for false.
     * If property not set -1 is returned.
     * @param propertyName
     * @return 1 if property is "true", 0 if "false" and -1 if not set.
     * @throws IllegalArgumentException If property is set but does not contain true or false
     * (ignoring case)
     */
    protected static int getBooleanProperty(String propertyName)   {

        String param = System.getProperty(propertyName);
        if (param != null)  {
            if (param.trim().equalsIgnoreCase("true"))  {
                return 1;
            } else if (param.trim().equalsIgnoreCase("false"))  {
                return 0;
            } else {
                throw new IllegalArgumentException("Not a boolean value: " + param.trim());
            }
        }
        return -1;

    }

    /**
     * Returns an int from the specified property. Will return -1 if property is not set.
     * @param propertyName
     * @return Int value from the property, or -1 if property not set.
     * @throws IllegalArgumentException If the value set cannot be parsed to an int.
     */
    protected static int getIntProperty(String propertyName) {

        String param = System.getProperty(propertyName);
        if (param != null)  {
            try {
                return Integer.parseInt(param.trim());
            }
            catch (NumberFormatException nfe){
                throw new IllegalArgumentException("Could not parse int for:" + param.trim());
            }
        }
        return -1;


    }

    /**
     * Checks the windowformat property and returns the PixelFormat corrensponding value.
     * @return PixelFormat value for window or -1 if not set.
     * @throws IllegalArgumentException If windowproperty is not valid value.
     */
    public static int getWindowFormatProperty() {

        String logStr = "Set PROPERTY_WINDOW_FORMAT to ";
        String formatStr = null;
        int format = -1;
        String val = System.getProperty(PROPERTY_WINDOW_FORMAT);
        if (val != null)        {

            val = val.trim();

            for (int i = 0; i < PIXELFORMAT_STR.length; i++) {
                if (val.equalsIgnoreCase(PIXELFORMAT_STR[i])) {
                    formatStr = PIXELFORMAT_STR[i];
                    format = PIXELFORMAT_INT[i];
                    break;
                }
            }
            if (format == -1) {
                throw new IllegalArgumentException("Unknown PROPERTY_WINDOW_FORMAT:" + val);
            }
            Log.d(TAG, logStr + formatStr);
        }
        return format;
    }

    /**
     * Checks the System property for usevbo flag and returns the property set,
     * or "" if property not set.
     * @return String set for use vbo property, or empty String if property not set.
     */
    public static String getUseVBO() {

        String str = System.getProperty(PROPERTY_OPENGLEN_USE_VBO);
        if (str == null) {
            str = "";
        }
        return str;
    }

    /**
     * Sets the system property for usevbo.
     * @param useVBO
     */
    public static void setUseVBO(boolean useVBO) {
        System.setProperty(PROPERTY_OPENGLEN_USE_VBO, String.valueOf(useVBO));
    }

    /**
     * Sets the properties for OpenGLEN, this is the common rendersettings for OpenGLEN.
     * Values can be read back by calling getRenderConfiguration()
     * @param clearFunction The GL clear function, see GL_CLEAR_BUFFER_STR for values
     * @param depthTest Depth test function as defined by DEPTH_FUNCTION_STR
     * @param enableMultisampling True to turn on multisampling, to fully enable multisampling
     * it must also be set in the EGL properties.
     */
    public final static void setOpenGLENProperties(String clearFunction, String depthTest,
                                                   boolean enableMultisampling) {

        System.setProperty(PROPERTY_OPENGLEN_DEPTHFUNCTION, depthTest);
        System.setProperty(PROPERTY_OPENGLEN_CLEAR_FUNCTION, clearFunction);
        System.setProperty(PROPERTY_OPENGLEN_ENABLEMULTISAMPLING,
                           String.valueOf(enableMultisampling));

    }
}
