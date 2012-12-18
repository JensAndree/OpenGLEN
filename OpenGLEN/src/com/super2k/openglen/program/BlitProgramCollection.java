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
package com.super2k.openglen.program;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.OpenGLENException;
import com.super2k.openglen.utils.GraphicsLibraryHandler;

public class BlitProgramCollection extends ProgramCollection {


    public final static String[] BIND_ATTRIB_NAMES = {"vertex_attrib", "normal_attrib",
            "texCoords_attrib"};

    public final static int[] BIND_ATTRIBS = new int[] {0,1,2};

    /**
     * Definition of the programs to load.
     */
    protected final static String[][] BLIT_VERTEXSHADER_NAMES = {
        new String[]{"blit/vertexshader_unlit1texture.essl", "vertexshader_libtransform.essl"},
        new String[]{"blit/vertexshader_lambert1texture.essl", "vertexshader_libtransform.essl"},
        new String[]{"blit/vertexshader_phonghq1texture.essl", "vertexshader_libtransform.essl"},
        new String[]{"blit/vertexshader_lambert1texture.essl", "vertexshader_libtransform.essl"},
        new String[]{"blit/vertexshader_unlit1texture.essl", "vertexshader_libtransform.essl"},
        new String[]{"blit/vertexshader_unlit1texture.essl", "vertexshader_libtransform.essl"},
        new String[]{"blit/vertexshader_unlit1texture.essl", "vertexshader_libtransform.essl"},
        //2 textures
        new String[]{"blit/vertexshader_unlit2texture.essl", "vertexshader_libtransform.essl"},
        new String[]{"blit/vertexshader_lambert1texture.essl", "vertexshader_libtransform.essl"},
        new String[]{"blit/vertexshader_phonghq1texture.essl", "vertexshader_libtransform.essl"},
        new String[]{"blit/vertexshader_lambert1texture.essl", "vertexshader_libtransform.essl"},
        new String[]{"blit/vertexshader_unlit2texture.essl", "vertexshader_libtransform.essl"},
        new String[]{"blit/vertexshader_unlit1texture.essl", "vertexshader_libtransform.essl"},
        new String[]{"blit/vertexshader_unlit1texture.essl", "vertexshader_libtransform.essl"}
    };

    protected final static String[][] BLIT_FRAGMENTSHADER_NAMES = {
        new String[]{"blit/fragmentshader_unlit1texture.essl"},
        new String[]{"blit/fragmentshader_lambert1texture.essl"},
        new String[]{"blit/fragmentshader_phonghq1texture.essl"},
        new String[]{"blit/fragmentshader_littexture.essl"},
        new String[]{"blit/fragmentshader_coloredtexture.essl"},
        new String[]{"blit/fragmentshader_blur5_1texture.essl"},
        new String[]{"blit/fragmentshader_blur9_1texture.essl"},
        //2 textures
        new String[]{"blit/fragmentshader_unlit2texture.essl"},
        new String[]{"blit/fragmentshader_lambert1texture.essl"},
        new String[]{"blit/fragmentshader_phonghq1texture.essl"},
        new String[]{"blit/fragmentshader_littexture.essl"},
        new String[]{"blit/fragmentshader_colored2texture.essl"},
        new String[]{"blit/fragmentshader_blur5_1texture.essl"},
        new String[]{"blit/fragmentshader_blur9_1texture.essl"}
    };

    /**
     * The uniform locations to use, these are indexes into the program collection uniform location storage.
     */
    public final static int PERSPECTIVEMATRIX_UNIFORM = 0;
    public final static int TRANSLATE_UNIFORM = 1;
    public final static int SCALE_UNIFORM = 2;
    public final static int ROTATE_UNIFORM = 3;
    public final static int DIRECTIONALLIGHT_UNIFORM = 4;    //Used for directional based lighting (not phong)
    public final static int LIGHTPOSITION_UNIFORM = 5;       //Used for positional based lights and phong.
    public final static int LIGHTCOLOR_UNIFORM = 6;
    public final static int AMBIENTMATERIAL_UNIFORM = 7; //Ambient color of material
    public final static int DIFFUSEMATERIAL_UNIFORM = 8; //Diffuse color of material
    public final static int SPECULARMATERIAL_UNIFORM = 9;//Specular color of material
    public final static int SHINEMATERIAL_UNIFORM = 10;   //Shininess (used with phong)
    public final static int TEXTURE1_UNIFORM = 11;
    public final static int TEXTURE2_UNIFORM = 12;

    public final static int UNIFORM_COUNT = TEXTURE2_UNIFORM + 1; //Must be updated if new uniforms are added


    /**
     * Create a new program collection with the specified ProgramHandler.
     * This will setup to use the default shader programs for a blit object.
     * If different shader sources should be used, call the setup() method.
     * @param programHandler The handler to create, load and compile programs.
     * @param GraphicsLibraryHandler The platform specific graphicslibrary handler.
     * @throws IllegalArgumentException If vertexShaderNames orfragmentShaderNames or
     * programHandler or graphicHandler is NULL. If shaderCount > length of shader name array.
     * @throws OpenGLENException If programs cannot be created.
     */
    public BlitProgramCollection(ProgramHandler programHandler,
            GraphicsLibraryHandler graphicHandler) throws OpenGLENException {
        super(programHandler, graphicHandler);
        setup(BLIT_VERTEXSHADER_NAMES,
                BLIT_FRAGMENTSHADER_NAMES,
                BLIT_VERTEXSHADER_NAMES.length,
                UNIFORM_COUNT,
                BIND_ATTRIB_NAMES,
                BIND_ATTRIBS);
    }
    @Override
    public void setUniformLocations() throws OpenGLENException{

        mGraphicHandler.clearError();
        for (int i = 0; i < mProgramCount; i++) {
            getUniformLocations(i)[DIRECTIONALLIGHT_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i), "DirLight_uniform");
            getUniformLocations(i)[LIGHTPOSITION_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i), "LightPosition_uniform");
            getUniformLocations(i)[AMBIENTMATERIAL_UNIFORM]=
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i), "MatAmbient_uniform");
            getUniformLocations(i)[SPECULARMATERIAL_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i), "MatSpecular_uniform");
            getUniformLocations(i)[DIFFUSEMATERIAL_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i), "MatDiffuse_uniform");
            getUniformLocations(i)[SHINEMATERIAL_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i), "MatShine_uniform");
            getUniformLocations(i)[LIGHTCOLOR_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i), "LightColor_uniform");
            getUniformLocations(i)[PERSPECTIVEMATRIX_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i),"PerspectiveMatrix_uniform");
            getUniformLocations(i)[TEXTURE1_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i),"Texture_uniform");
            getUniformLocations(i)[TEXTURE2_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i),"Texture2_uniform");
            getUniformLocations(i)[TRANSLATE_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i),"Translate_uniform");
            getUniformLocations(i)[SCALE_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i),"Scale_uniform");
            getUniformLocations(i)[ROTATE_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i),"Rotate_uniform");
        }
        int result = mGraphicHandler.checkError();
        if (result != ConstantValues.NO_ERROR){
            throw new OpenGLENException(COULD_NOT_GET_UNIFORM, result);
        }
    }

}
