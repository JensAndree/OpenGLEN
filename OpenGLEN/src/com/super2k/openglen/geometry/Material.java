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

package com.super2k.openglen.geometry;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.program.ShaderProgram;
import com.super2k.openglen.texture.Texture2D;


/**
 * Class for material definition, stored as float array when possible.
 *
 * @author Richard Sahlin
 */
public class Material  {

    /**
     * Unlit shading - no lightsource, only use texture.
     */
    public final static int SHADING_UNLIT = 1;
    /**
     * Lambert lighting
     */
    public final static int SHADING_LAMBERT = 2;
    /**
     * Phong lighting
     */
    public final static int SHADING_PHONG = 3;

    /**
     * Lit texture, calculation is lightdirection * texture * lightColor
     */
    public final static int SHADING_LIT = 4;

    /**
     * Colored texture. calculation is texture * light color
     */
    public final static int SHADING_COLORED = 5;

    /**
     * Blur texture result is done by adding 5 texels together.
     * Center value is multiplied by weight factor.
     * center + left + right + up + down
     */
    public final static int SHADING_BLUR5_TEXTURE = 6;

    /**
     * Blur texture result is done by adding 9 texels together.
     * Center value is multiplied by weight factor.
     */
    public final static int SHADING_BLUR9_TEXTURE = 7;

    /**
     * Blurring is done by blurring depth buffer and storing
     * result in colorbuffer.
     * User must setup framebuffer with copy of colorbuffer
     * in depthbuffer (renderbuffer)
     */
    public final static int SHADING_BLUR_DEPTH = 8;

    /**
     * A custom shader program. When this shading is specified a ShaderProgram to be used
     * must also be specified.
     */
    public final static int SHADING_CUSTOM = 0x08000;

    /**
     * Texture(s) for this material - may be null if not used.
     */
    public Texture2D[] texture;

    /**
     * The specular (shiny) material colour.
     */
    public float[] specular = new float[] {
            0, 0, 0, 0
    };

    /**
     * *************************************************************
     * Blur parameters
     * Blurring will only be enabled when the correct shader is
     * selected, one of the BLUR_ shaders.
     * *************************************************************
     */
    private final static float DEFAULT_X_BLUR_FACTOR = 1;
    private final static float DEFAULT_Y_BLUR_FACTOR = 1;
    private final static float DEFAUT_WEIGHT_FACTOR = 1.7f;

    /**
     * How much to blur in x axis, this value is multiplied by the texel x axis
     * divisor. The X axis texel step will be 1/ (texture width * xBlurFactor)
     * Larger values, above 1 will not produce blur.
     * The smaller the value the bigger
     * the blur effect usually is but this also depends on what is being rendered.
     */
    public float xBlurFactor = DEFAULT_X_BLUR_FACTOR;

    /**
     * How much to blur in y axis, this value is multiplied by the texel y axis
     * divisor. The Y axis texel step will be 1/ (texture width * yBlurFactor)
     * Larger values, above 1 will not produce blur.
     * The smaller the value the bigger
     * the blur effect usually is but this also depends on what is being rendered.
     */
    public float yBlurFactor = DEFAULT_Y_BLUR_FACTOR;

    /**
     * How much to weight the centerpixel compared to surrounding pixels.
     * A value of 1 will weight center equal to surrounding.
     * Larger values will dim out effect of surrounding pixels.
     */
    public float weightFactor = DEFAUT_WEIGHT_FACTOR;

    /**
     * The specular power, the shininess.
     */
    public float power;

    /**
     * GLES blend source function, blending is turned off by default.
     */
    public int sourceBlend = ConstantValues.NONE;

    /**
     * GLES blend dest function, blending is turned off by default.
     */
    public int destBlend = ConstantValues.NONE;


    /**
     * The diffuse (directed) material colour.
     */
    public float[] diffuse = new float[] {
            0, 0, 0, 0
    };

    /**
     * The ambient (scattered) material colour.
     */
    public float[] ambient = new float[] {
            0, 0, 0, 0
    };


    /**
     * This is what shader is used to render the objects.
     * Any of the SHADING_XXX values.
     */
    public int materialShading;

    /**
     * A specific shader program to be used when rendering this material.
     * This is used when the material shading is SHADING_CUSTOM
     */
    public ShaderProgram shaderProgram;

    /**
     * Default constructor.
     */
    public Material() {

    }

    /**
     * Constructor that copies the source material to this (creates a new copy)
     * @param source
     * @throws IllegalArgumentException if source is NULL
     */
    public Material(Material source) {
        set(source);
    }

    /**
     * Sets the data from the specified material into this.
     * @param source The material to copy.
     * @throws IllegalArgumentException if source is NULL
     */
    public void set(Material source) {

        if (source == null){
            throw new IllegalArgumentException("source is NULL");
        }
        xBlurFactor = source.xBlurFactor;
        yBlurFactor = source.yBlurFactor;
        weightFactor = source.weightFactor;
        materialShading = source.materialShading;
        power = source.power;
        destBlend = source.destBlend;
        sourceBlend = source.sourceBlend;

        ambient = new float[source.ambient.length];
        for (int i = 0; i < ambient.length; i++)
            ambient[i] = source.ambient[i];

        diffuse = new float[source.diffuse.length];
        for (int i = 0; i < diffuse.length; i++)
            diffuse[i] = source.diffuse[i];

        specular = new float[source.specular.length];
        for (int i = 0; i < specular.length; i++)
            specular[i] = source.specular[i];
        if (source.texture != null) {
            texture = new Texture2D[source.texture.length];
            for (int i = 0; i < texture.length;i ++) {
                texture[i] = source.texture[i];
            }
        }
    }

    /**
     * Set the source and destination blend mode. This will turn on alpha blend for this blit.
     * Set to ConstantValues.NONE to disable blending.
     * @param sourceBlend
     * @param destBlend
     */
    public void setBlendFunc(int sourceBlend, int destBlend) {
        this.sourceBlend = sourceBlend;
        this.destBlend = destBlend;
    }

    /**
     * The effect of setting the material properties are dependent on the shader being used, some shaders
     * may not include material properties.
     * Set the ambient color of this material, the ambient material is always reflected.
     * Note that the alpha parameter may not be used by the shader.
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public void setAmbient(float red, float green, float blue,float alpha)  {
        set(ambient, red, green, blue, alpha);
    }

    /**
     * The effect of setting the material properties are dependent on the shader being used, some shaders
     * may not include material properties.
     * Set the ambient color of this material, the ambient material is always reflected.
     * Alpha is set to fully opaque
     * @param red
     * @param green
     * @param blue
     */
    public void setAmbient(float red, float green, float blue)  {
        set(ambient, red, green, blue, 1f);
    }


    /**
     * The effect of setting the material properties are dependent on the shader being used, some shaders
     * may not include material properties.
     * Set the diffuse color of this material, the diffuse part is equally reflected in all directions
     * depending on the angle of the incoming light.
     * Note that the alpha parameter may not be used by the shader.
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public void setDiffuse(float red, float green, float blue,float alpha)  {
        set(diffuse, red, green, blue, alpha);
    }
    /**
     * The effect of setting the material properties are dependent on the shader being used, some shaders
     * may not include material properties.
     * Set the diffuse color of this material, the diffuse part is equally reflected in all directions
     * depending on the angle of the incoming light.
     * Alpha is set to fully opaque.
     * @param red
     * @param green
     * @param blue
     */
    public void setDiffuse(float red, float green, float blue)  {
        set(diffuse, red, green, blue, 1f);
    }

    /**
     * The effect of setting the material properties are dependent on the shader being used, some shaders
     * may not include material properties.
     * Set the specular color of the material, the specular part is reflected when view angle is same or very close to
     * reflected angle. Will produce highlight if enabled.
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public void setSpecular(float red, float green, float blue,float alpha) {
        set(specular, red, green, blue, alpha);
    }

    /**
     * Set the material diffuse properties from an int, highest value is alpha, eg 0x0ff0ff00 is fully apaque green
     * and 0x0ff0000ff is fully opaque blue.
     * Values are remapped from 0-255 into float 0 to 1.
     * @param argb
     */
    public void setDiffuse(int argb)    {

        diffuse[2] = (float)((argb) & 0x0ff)/255;
        diffuse[1] = (float)((argb>>>8) & 0x0ff)/255;
        diffuse[0] = (float)((argb>>>16) & 0x0ff)/255;
        diffuse[3] = (float)((argb>>>24) & 0x0ff)/255;

    }

    /**
     * Sets the specular RGBA color as a reference to the specified array.
     * Values are not copied, changes to the rgba array will be reflected.
     * @param rgba Array with (at least) 4 values.
     * @throws IllegalArgumentException If rgba is null or does not contain 4 values.
     */
    public void setSpecular(float[] rgba) {
        specular = rgba;
    }

    /**
     * Sets the specular color of the material, the specular part is reflected when view angle is same or very close to
     * reflected angle. Will produce highlight if enabled.
     * The effect of setting the material properties are dependent on the shader being used, some shaders
     * may not include material properties.
     * alpha is set to fully opaque.
     * @param red
     * @param green
     * @param blue
     */
    public void setSpecular(float red, float green, float blue) {
        set(specular, red, green, blue, 1f);
    }

    /**
     * Sets the specular power, this controlls the 'shininess' of phong shaded objects.
     * Higher values for a more defined and smaller highlight,
     * lower values for a more spread highlight.
     * @param power
     */
    public void setSpecularPower(float power)  {
        this.power = power;
    }


    /**
     * Conveniance method to set the values in an array.
     * @param dest
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    private final void set(float[] dest,float red, float green, float blue,float alpha) {
        dest[0] = red;
        dest[1] = green;
        dest[2] = blue;
        dest[3] = alpha;
    }
}