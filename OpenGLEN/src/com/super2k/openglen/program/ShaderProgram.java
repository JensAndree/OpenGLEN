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

import java.io.IOException;
import java.io.InputStream;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.OpenGLENException;
import com.super2k.openglen.nibbler.InputStreamResolver;
import com.super2k.openglen.utils.GraphicsLibraryHandler;

/**
 * A custom shaderprogram that can be used with objects rendered.
 * Remember that the Uniform data must match those of the standard shaders.
 * This shader program will only work for the uniforms, attributes as defined for
 * the Material shaders.
 * @author Richard Sahlin
 *
 */
public class ShaderProgram {

    protected int mProgram;
    protected int mVertexShader;
    protected int mFragmentShader;

    /**
     * Creates a program from the specified vertex and fragment shader sources.
     * @param vertexShader Name of vertex shader source.
     * @param fragmentShader Name of fragment shader source.
     * @param resolver
     * @param handler
     * @throws IOException If there is an exception reading from file.
     * @throws OpenGLENException If there is an error creating program, compiling or linking.
     */
    public ShaderProgram(String vertexShader, String fragmentShader, InputStreamResolver resolver,
            ProgramHandler handler, GraphicsLibraryHandler graphicsHandler)
            throws OpenGLENException, IOException {

        mProgram = handler.createProgram();

        mVertexShader = handler.compileAndAttachShader(mProgram, -1, ConstantValues.VERTEX_SHADER,
                new InputStream[] {resolver.openInputStream(vertexShader)}, graphicsHandler);

        mFragmentShader = handler.compileAndAttachShader(mProgram, -1,
                ConstantValues.FRAGMENT_SHADER,
                new InputStream[] {resolver.openInputStream(fragmentShader)}, graphicsHandler);


        for (int i = 0; i < BlitProgramCollection.BIND_ATTRIB_NAMES.length; i++) {
            handler.bindAttributeLocation(mProgram, i, BlitProgramCollection.BIND_ATTRIB_NAMES[i]);
        }

        handler.linkProgram(mProgram);
        int error = graphicsHandler.checkError();

        if (error != ConstantValues.NO_ERROR ||
                handler.getProgramParam(mProgram, ConstantValues.LINK_STATUS) !=
                ConstantValues.TRUE) {

            handler.logProgramInfo(mProgram);
            throw new IllegalArgumentException("Error linking program " + mProgram);

        }


    }

    /**
     * Returns the program name for this shader.
     * @return The program name.
     */
    public int getProgram() {
        return mProgram;
    }


}
