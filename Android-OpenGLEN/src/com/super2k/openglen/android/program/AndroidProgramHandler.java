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
package com.super2k.openglen.android.program;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;

import android.opengl.GLES20;
import android.util.Log;

import com.super2k.openglen.program.ProgramHandler;
import com.super2k.openglen.utils.GraphicsLibraryHandler;


/**
 * This class handles graphics shader programs on Android.
 * It can create and compile programs, this implementation shall use the underlying graphics library on Android.
 * This is OpenGL ES
 * @author Richard Sahlin
 *
 */
public class AndroidProgramHandler extends ProgramHandler {


    private final String TAG = getClass().getSimpleName();

    /**
     * Constructs an AndroidProgramHandler.
     * @param graphicsLibrary The GraphicsLibraryHandler to be used.
     * @throws IllegalArgumentException if graphicsLibrary is NULL
     */
    public AndroidProgramHandler(GraphicsLibraryHandler graphicsLibrary)  {
        super(graphicsLibrary);
    }

    @Override
    public void logProgramInfo(int program)  {
        Log.d(TAG,"Program info log " + program + "\n" + GLES20.glGetProgramInfoLog(program));
    }

    @Override
    public void logShaderInfo(int shader)  {
        Log.d(TAG,"Shader info log " + shader + "\n" + GLES20.glGetShaderInfoLog(shader));

    }
    @Override
    public int internalDeleteProgram(int program) {
        //Make sure no pending error
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glDeleteProgram(program);
        return GLES20.glGetError();
    }

    @Override
    public int internalDeleteShader(int name) {
        //Make sure no pending error
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glDeleteShader(name);
        return GLES20.glGetError();
    }
    @Override
    public int internalAttachShader(int programName, int shaderName) {
        //Make sure no pending error
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glAttachShader(programName, shaderName);
        return GLES20.glGetError();
    }
    @Override
    public int internalCompileShader(int shaderName) {
        //Make sure no pending errors
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glCompileShader(shaderName);
        return GLES20.glGetError();

    }
    @Override
    public int getShaderParam(int shaderName, int param) {
        int[] status = new int[1];
        GLES20.glGetShaderiv(shaderName, param, status, 0);
        return status[0];
    }
    @Override
    public String getShaderSource(int shaderType, BufferedReader shaderSource, boolean mainFile)
    throws IOException {

        StringWriter shaderWriter = new StringWriter();
        String line = "";

        while (line != null) {
            shaderWriter.write(line);
            shaderWriter.write("\n");
            line = shaderSource.readLine();
        }
        String result = shaderWriter.toString();
        shaderWriter.close();
        return result;

    }
    @Override
    public int internalSetShaderSource(int shaderName, String source) {
        //Make sure no pending error.
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glShaderSource(shaderName, source);
        return GLES20.glGetError();
    }
    @Override
    public int getProgramParam(int programName, int param) {
        int[] status = new int[1];
        GLES20.glGetProgramiv(programName, param, status, 0);
        return status[0];
    }

    @Override
    public int internalLinkProgram(int program) {
        //Make sure no pending error.
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glLinkProgram(program);
        return GLES20.glGetError();
    }
    @Override
    public void bindAttributeLocation(int programName, int index, String name) {
        GLES20.glBindAttribLocation(programName, index, name);

    }
    @Override
    public void enableVertexAttribArray(int index) {
        GLES20.glEnableVertexAttribArray(index);

    }

    @Override
    protected int internalCreateShader(int type) {
        return GLES20.glCreateShader(type);
    }

    @Override
    protected int internalCreateProgram() {
        return GLES20.glCreateProgram();
    }

    @Override
    protected int internalDetachShader(int program, int shader) {
        //Make sure no pending error.
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glDetachShader(program, shader);
        return GLES20.glGetError();
    }

    @Override
    protected int internalUseProgram(int program) {
        //Make sure no pending error.
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glUseProgram(program);
        return GLES20.glGetError();
    }

    @Override
    protected int internalSetUniformMatrix(int uniform, int count, float[] matrix, int offset) {
        //Make sure no pending error.
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glUniformMatrix4fv(uniform, count, false, matrix, offset);
        return GLES20.glGetError();
    }

    @Override
    protected int internalSetUniformInt(int uniform, int value) {
        //Make sure no pending error.
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glUniform1i(uniform, value);
        return GLES20.glGetError();
    }

    @Override
    protected int internalSetUniformFloat(int uniform, float value) {
        //Make sure no pending error.
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glUniform1f(uniform, value);
        return GLES20.glGetError();
    }

    @Override
    protected int internalSetUniformVector(int uniform, int count, float[] vector, int offset) {
        //Make sure no pending error.
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glUniform4fv(uniform, count, vector, offset);
        return GLES20.glGetError();
    }

    @Override
    protected int internalSetUniformVector3(int uniform, int count, float[] vector, int offset) {
        //Make sure no pending error.
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glUniform3fv(uniform, count, vector, offset);
        return GLES20.glGetError();
    }

    @Override
    protected int internalSetUniformVector2(int uniform, int count,
            float[] vector, int offset) {
        while (GLES20.glGetError() != GLES20.GL_NO_ERROR);
        GLES20.glUniform2fv(uniform,  count, vector, offset);
        return GLES20.glGetError();
    }

}
