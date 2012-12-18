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

public class ParticleProgramCollection extends ProgramCollection {

    /**
     * The uniform locations to use, these are indexes into the program collection uniform location
     * storage.
     */
    public final static int PERSPECTIVEMATRIX_UNIFORM = 0;
    public final static int DIFFUSEMATERIAL_UNIFORM = 1;    //Diffuse color of material
    public final static int DATA_UNIFORM = 2;               //Array of vec4
                                                            //gravity, data (time + size), color add
    public final static int GRAVITY_UNIFORM = 3;            //Gravity
    public final static int TEXTURE1_UNIFORM = 4;           //Texture for image particles
                                                            //or velocity for velocity particles.
    public final static int BUFFERSIZE_UNIFORM = 5;         //1 / texturesize for image particles
    public final static int TEXTURE2_UNIFORM = 6;           //Texture 2, position for velocity
                                                            //textures
    public final static int ROTATE_MATRIX_UNIFORM = 7;      //Rotate matrix for emitter.
    final static int UNIFORM_COUNT = ROTATE_MATRIX_UNIFORM + 1; //Must be updated if new uniforms
                                                           //are added

    protected final static String[] BIND_ATTRIB_NAMES =  {"vertex_attrib","color_attrib",
                                                          "speed_attrib","data_attrib",
                                                          "color_add_attrib" };

    protected final static String[][] PARTICLE_VERTEXSHADER_NAMES = {
        new String[]{"particles/vertexshader_unlitsimpleparticles.essl"},
                    {"particles/vertexshader_unlitparticles.essl"},
                    {"particles/vertexshader_imageparticles.essl"},
                    {"particles/vertexshader_positionparticles.essl"},
        };

    protected final static String[][] PARTICLE_FRAGMENTSHADER_NAMES = {
        new String[]{"particles/fragmentshader_unlitsimpleparticles.essl"},
                    {"particles/fragmentshader_unlitparticles.essl",},
                    {"particles/fragmentshader_imageparticles.essl",},
                    {"particles/fragmentshader_unlitparticles.essl",},
        };

    /**
     * Constructs a new Particle program collection
     * @param programHandler
     * @param graphicHandler
     * @throws OpenGLENException
     */
    public ParticleProgramCollection(ProgramHandler programHandler,
            GraphicsLibraryHandler graphicHandler) throws OpenGLENException {
        super(programHandler, graphicHandler);
        setup(PARTICLE_VERTEXSHADER_NAMES,
                PARTICLE_FRAGMENTSHADER_NAMES,
                PARTICLE_VERTEXSHADER_NAMES.length,
                UNIFORM_COUNT,
                BIND_ATTRIB_NAMES, new int[] {0, 1, 2, 3, 4}); //attrib names and int array must
                                                               //match.
    }

    @Override
    public void setUniformLocations() throws OpenGLENException {
        mGraphicHandler.clearError();
        for (int i = 0; i < mProgramCount; i++) {
            getUniformLocations(i)[PERSPECTIVEMATRIX_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i),
                "PerspectiveMatrix_uniform");
            getUniformLocations(i)[DIFFUSEMATERIAL_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i), "MatDiffuse_uniform");
            /**
             * Storage of data uniforms, instead of taking up several float values
             * (which are vectors anyway) put data into one vec4
             */
            getUniformLocations(i)[DATA_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i), "Data_uniform");
            //Gravity
            getUniformLocations(i)[GRAVITY_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i), "Gravity_uniform");
            //Texture sample, used for image particles
            getUniformLocations(i)[TEXTURE1_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i),"Texture_uniform");
            //1 / Texturesize, used for image particles
            getUniformLocations(i)[BUFFERSIZE_UNIFORM] =
                    mGraphicHandler.getUniformLocation(getProgramNameByIndex(i),
                    "Buffersize_uniform");
            //Texture 2 uniform
            getUniformLocations(i)[TEXTURE2_UNIFORM] =
                mGraphicHandler.getUniformLocation(getProgramNameByIndex(i),"Texture2_uniform");
            //Rotate matrix
            getUniformLocations(i)[ROTATE_MATRIX_UNIFORM] =
                    mGraphicHandler.getUniformLocation(getProgramNameByIndex(i),
                    "RotateMatrix_uniform");

        }
        int result = mGraphicHandler.checkError();
        if (result != ConstantValues.NO_ERROR){
            throw new OpenGLENException(COULD_NOT_GET_UNIFORM, result);
        }

    }

}
