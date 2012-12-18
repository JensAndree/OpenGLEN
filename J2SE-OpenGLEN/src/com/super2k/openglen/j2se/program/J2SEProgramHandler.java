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
package com.super2k.openglen.j2se.program;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2ES2;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.program.ProgramHandler;
import com.super2k.openglen.utils.GraphicsLibraryHandler;
import com.super2k.openglen.utils.Log;

public class J2SEProgramHandler extends ProgramHandler {

    protected GL2ES2 mGles2;

    public final String TAG = getClass().getSimpleName();

    protected final static String GLES_NULL_STR = "GLES is NULL";

    /**
     * Constructs a new ProgramHandler.
     * @param gles2 GLES2
     * @param graphicsLibrary The graphicslibraryhandler to be used.
     * @throws IllegalArgumentException If gles2 or graphicsLibrary is NULL
     */
    public J2SEProgramHandler(GL2ES2 gles2, GraphicsLibraryHandler graphicsLibrary) {
        super(graphicsLibrary);
        if (gles2 == null){
            throw new IllegalArgumentException(GLES_NULL_STR);
        }
        mGles2 = gles2;

    }

    private void logProgramInfo(GL2ES2 gles2, int program)  {

        IntBuffer msgLength = IntBuffer.allocate(1);
        ByteBuffer infoLog = ByteBuffer.allocate(10000);
        gles2.glGetProgramInfoLog(program, infoLog.capacity(), msgLength, infoLog );
        if (msgLength.array()[0] > 0)
            Log.d(TAG, "Program log " + program + "\n" +
                    new String(infoLog.array(), 0, msgLength.array()[0]));

    }

    private void logShaderInfo(GL2ES2 gles2, int shader)  {

        IntBuffer msgLength = IntBuffer.allocate(1);
        ByteBuffer infoLog = ByteBuffer.allocate(10000);
        gles2.glGetShaderInfoLog(shader, infoLog.capacity(), msgLength, infoLog );
        if (msgLength.array()[0] > 0)
            Log.d(TAG, "Shader log " + shader + "\n" +
                    new String(infoLog.array(), 0, msgLength.array()[0]));
    }

    @Override
    protected int internalDeleteShader(int name) {
        //Make sure no pending error
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glDeleteShader(name);
        return mGles2.glGetError();
    }

    @Override
    protected int internalDeleteProgram(int program) {
        //Make sure no pending error
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glDeleteProgram(program);
        return mGles2.glGetError();

    }


    @Override
    public String getShaderSource(int shaderType,
                                BufferedReader shaderSource,
                                boolean mainFile) throws IOException{

        StringWriter shaderWriter = new StringWriter();
        //Append a version for GLSL, but not for vertex shader
        String line = "#version 120";

        if (!mainFile || shaderType != ConstantValues.VERTEX_SHADER)
            line = shaderSource.readLine();


        while (line != null) {
            if (!line.toLowerCase().startsWith("precision"))	{
                int strindex = line.toLowerCase().indexOf("highp");
                if (strindex >= 0) {
                    //remove highp
                    line = line.substring(0, strindex) + line.substring(strindex + 5);
                }

                shaderWriter.write(line);
                shaderWriter.write("\n");

            }
            line = shaderSource.readLine();
        }
        String result = shaderWriter.toString();
        shaderWriter.close();
        return result;
    }


    @Override
    protected int internalSetShaderSource(int shaderName, String source) {
        //Make sure no pending error
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glShaderSource(shaderName,1, new String[] {source}, new int[] {source.length()}, 0);
        return mGles2.glGetError();
    }


    @Override
    protected int internalCompileShader(int shaderName) {
        //Make sure no pending error
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glCompileShader(shaderName);
        return mGles2.glGetError();

    }


    @Override
    public int getShaderParam(int shaderName, int param) {
        int[] status = new int[1];
        mGles2.glGetShaderiv(shaderName, param, status, 0);
        return status[0];
    }


    @Override
    protected int internalAttachShader(int programName, int shaderName) {
        //Make sure no pending error
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glAttachShader(programName, shaderName);
        return mGles2.glGetError();

    }


    @Override
    public void logShaderInfo(int shaderName) {
        logShaderInfo(mGles2, shaderName);

    }


    @Override
    public int getProgramParam(int programName, int param) {
        int[] status = new int[1];
        mGles2.glGetProgramiv(programName, param, status, 0);
        return status[0];
    }


    @Override
    protected int internalLinkProgram(int program) {
        //Make sure no pending error
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glLinkProgram(program);
        return mGles2.glGetError();
    }


    @Override
    public void logProgramInfo(int programName) {
        logProgramInfo(mGles2, programName);
    }


    @Override
    public void bindAttributeLocation(int programName, int index, String name) {
        mGles2.glBindAttribLocation(programName, index, name);

    }


    @Override
    public void enableVertexAttribArray(int index) {
        mGles2.glEnable(index);

    }


    @Override
    protected int internalCreateShader(int type) {
        return mGles2.glCreateShader(type);
    }


    @Override
    protected int internalCreateProgram() {
        return mGles2.glCreateProgram();
    }

    @Override
    protected int internalDetachShader(int program, int shader) {
        //Make sure no pending error
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glDetachShader(program, shader);
        return mGles2.glGetError();

    }

    @Override
    protected int internalUseProgram(int program) {
        //Make sure no pending error
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glUseProgram(program);
        return mGles2.glGetError();
    }

    @Override
    protected int internalSetUniformMatrix(int uniform, int count, float[] matrix, int offset) {
        //Make sure no pending error.
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glUniformMatrix4fv(uniform, count, false, matrix, offset);
        return mGles2.glGetError();
    }

    @Override
    protected int internalSetUniformInt(int uniform, int value) {
        //Make sure no pending error.
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glUniform1i(uniform, value);
        return mGles2.glGetError();
    }

    @Override
    protected int internalSetUniformFloat(int uniform, float value) {
        //Make sure no pending error.
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glUniform1f(uniform, value);
        return mGles2.glGetError();
    }

    @Override
    protected int internalSetUniformVector(int uniform, int count, float[] vector, int offset) {
        //Make sure no pending error.
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glUniform4fv(uniform, count, vector, offset);
        return mGles2.glGetError();
    }

    @Override
    protected int internalSetUniformVector3(int uniform, int count, float[] vector, int offset) {
        //Make sure no pending error.
        while (mGles2.glGetError() != GL.GL_NO_ERROR);
        mGles2.glUniform3fv(uniform, count, vector, offset);
        return mGles2.glGetError();
    }

    @Override
    protected int internalSetUniformVector2(int uniform, int count, float[] vector, int offset) {
            //Make sure no pending error.
            while (mGles2.glGetError() != GL.GL_NO_ERROR);
            mGles2.glUniform2fv(uniform, count, vector, offset);
            return mGles2.glGetError();
    }

}
