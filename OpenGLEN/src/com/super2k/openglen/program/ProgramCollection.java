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
import java.util.Vector;

import com.super2k.openglen.ConstantValues;
import com.super2k.openglen.OpenGLENException;
import com.super2k.openglen.nibbler.InputStreamResolver;
import com.super2k.openglen.utils.GraphicsLibraryHandler;
import com.super2k.openglen.utils.Log;

/**
 * A collection of program data for vertex and fragment shaders.
 * Program, vertex-fragment shader names, uniform locations.
 *
 * @author Richard Sahlin
 *
 */
public abstract class ProgramCollection {

    /**
     * Default flag for debug outoput.
     */
    protected final static boolean DEFAULT_DEBUG_OUTPUT = false;

    private final String TAG = getClass().getSimpleName();

    public final static String ERROR_OPENING_STREAM = "Could not open InputStream:";
    public final static String INVALID_SHADER_TYPE = "Invalid shader type:";
    protected final static String SHADER_NAMES_NULL = "Shader names is NULL";
    protected final static String COULD_NOT_GET_UNIFORM = "Could not get uniform location for program:";
    protected final static String INVALID_PROGRAM_INDEX = "Invalid program index:";
    protected final static String PARAMETER_NULL_STR = "Parameter is null";

    protected String[][] mVertexShaderNames = null;
    protected String[][] mFragmentShaderNames = null;
    protected int[] mEnableBlitArrays; //Attribute arrays to use

    /**
     * String array with attribs to bind.
     */
    protected String[] mBindAttribNames = null;




    /**
     * Base shader path
     */
    String mShaderPath = "";



    /**
     * The handler used to create programs, load and compile etc.
     */
    protected ProgramHandler    mProgramHandler;

    /**
     * Handler to underlying graphics implementation (normally GL)
     */
    protected GraphicsLibraryHandler mGraphicHandler;

    /**
     * InputStreamResolver to use for opening programs, if not set
     * then class.getResourceAsStream is used.
     */
    protected InputStreamResolver mInputResolver;

    /**
     * Program names
     */
    protected int[] mPrograms;

    /**
     * Shader names for the vertex shaders, one int[]  for each program
     */
    protected Vector<int[]> mVertexShaders;

    /**
     * Fragment names for the fragment shaders, on int[]  for each program.
     */
    protected Vector<int[]> mFragmentShaders;

    /**
     * Uniform locations
     */
    protected int[] [] mUniformLocations;

    /**
     * Set this in subclasses
     */
    public int mProgramCount = -1;

    /**
     * Set to true to enable logging of source etc.
     */
    protected boolean mDebugOutput = DEFAULT_DEBUG_OUTPUT;

    /**
     * Creates a new program collection with the specified ProgramHandler
     * and GraphcisLibraryHandler.
     * This will create a default program collection with the needed shader programs for the
     * GLES20Renderer implementation.
     * @param programHandler The handler to create, load and compile programs.
     * @param graphicHandler The platform specific graphicslibrary handler.
     * @throws IllegalArgumentException If programHandler or graphicHandler is NULL.
     */
    public ProgramCollection(ProgramHandler programHandler,
            GraphicsLibraryHandler graphicHandler) {
        if (programHandler == null || graphicHandler == null ){
            throw new IllegalArgumentException(PARAMETER_NULL_STR);
        }
        mProgramHandler = programHandler;
        mGraphicHandler = graphicHandler;


    }

    /**
     * Sets the vertex and fragment shader names to be used, number of uniforms
     * and programs, attribute information.
     * @param vertexShaderNames List of vertex shaders, first array is for each program.
     * Second array is for more than one source for each program.
     * @param fragmentShaderNames List of fragment shaders, first array is for each program.
     * Second array is for more than one source for each program.
     * @param programCount Number of shader programs to create, and number of
     * programs to load when calling loadPrograms(). This is the total of shaders to create.
     * @param uniformCount Number of uniforms, to allocate storage for uniform locations.
     * @param bindAttribNames Array with names of attribs to bind, optional may be null.
     * Stores a reference, values are not copied.
     * @param enableAttribArrays Arrays with attribute arrays to enable.
     * @throws IllegalArgumentException If vertexShaderNames or fragmentShaderNames, enableAttribArrays or
     * programHandler or graphicHandler is NULL. If shaderCount > lenght of shader name array.
     */
    public void setup(String[][] vertexShaderNames, String[][] fragmentShaderNames,
            int programCount, int uniformCount, String[] bindAttribNames,
            int[] enableAttribArrays) {

        if (enableAttribArrays == null || vertexShaderNames == null || fragmentShaderNames == null){
            throw new IllegalArgumentException(PARAMETER_NULL_STR);
        }
        if (vertexShaderNames == null || fragmentShaderNames == null)   {
            throw new IllegalArgumentException(SHADER_NAMES_NULL);
        }
        mFragmentShaderNames = fragmentShaderNames;
        mVertexShaderNames = vertexShaderNames;
        mProgramCount = programCount;  //Number of programs.
        mBindAttribNames = bindAttribNames;
        mEnableBlitArrays = enableAttribArrays;

        mPrograms = new int[programCount];
        mVertexShaders = new Vector<int[]>();
        mFragmentShaders = new Vector<int[]>();
        mUniformLocations = new int[programCount][uniformCount];

    }

    /**
     * Set the name of a specific program.
     * @param programIndex Index of the program name to set.
     * @param name The name to set.
     * @return The previous name for the specified program.
     * @throws IllegalArgumentException if programIndex < 0
     * programIndex > number of programs or name is <= 0
     */
    public int setProgramName(int programIndex, int name)   {
        if (programIndex < 0 || programIndex >= mPrograms.length ||
                name <= 0)  {
            throw new IllegalArgumentException("Invalid value.");
        }
        int oldname = mPrograms[programIndex];
        mPrograms[programIndex] = name;
        return oldname;

    }

    /**
     * Return the GL name of the specified program, -1 if out of bounds.
     * @param programIndex Index of the program name
     * @return Program name (ID)
     * @throws IllegalArgumentException If programIndex is invalid.
     */
    public int getProgramNameByIndex(int programIndex)  {
        if (programIndex < 0 || programIndex >= mPrograms.length)
            throw new IllegalArgumentException(INVALID_PROGRAM_INDEX + programIndex);
        return mPrograms[programIndex];
    }

    /**
     * Return the array holding program names, program names should be set by client.
     * @return The array containing program names.
     */
    public int[] getProgramNames()  {
        return mPrograms;
    }

    /**
     * Returns an array with the attribute arrays to enable.
     * @return Array with attribute arrays to enable.
     */
    public int[] getEnableAttribArrays() {
        return mEnableBlitArrays;
    }

    /**
     * Return the array containing vertex shader (names),
     * vertex shader names shall be setup by client.
     * @return Vector containing int[] vertex shader names, one int[] for each program.
     */
    public Vector<int[]> getVertexShaders() {
        return mVertexShaders;
    }

    /**
     * Return the array containing fragment shader names,
     * vertex shader names shall be setup by client.
     * @return Vector containing int[] fragment shader names, one int[] for each program.
     */
    public Vector<int[]> getFragmentShaders()       {
        return mFragmentShaders;
    }

    /**
     * Return an array with the uniform locations for the specified program index.
     * If programindex < 0 or >= program count then null is returned.
     * Uniform locations are set by the client.
     * @param programIndex Programindex to fetch uniform locations for.
     * @return Array with uniform locations.
     */
    public int[] getUniformLocations(int programIndex)  {
        return mUniformLocations[programIndex];
    }

    /**
     * Loads and compiles the programs needed by the program collection.
     * This method MUST be implemented correctly by subclasses
     * to load and compile the programs that are needed by the renderer.
     * This has a tight connection to the Render implementation.
     * @throws IOEXception If there is an ioerror reading shader sources.
     * @throws OpenGLENException If there is an error compiling/linking sources.
     */
    public void loadPrograms() throws IOException, OpenGLENException{
        for (int i = 0; i < mProgramCount; i++)  {
            setProgramName(i, mProgramHandler.createProgram());
        }
        loadPrograms(mVertexShaderNames, mFragmentShaderNames, mBindAttribNames);
    }

    /**
     * Loads and compiles the specified shaders, internal method.
     * The arrays contain shader names for each program and can have multiple shaders
     * for each program.
     * First array is one for each program, second is different shader for that program.
     * @param vertexShaderNames Arrays containing the vertex shader names for each program.
     * @param fragmentShaderNames Arrays containing the fragment shader names for each program.
     * @param bindAttribNames Array containing attribute names to bind.
     * @throws IOEXception If there is an ioerror reading shader sources.
     * @throws OpenGLENException If there is an error compiling/linking sources.
     */
    protected void loadPrograms(String[][] vertexShaderNames,
            String[][] fragmentShaderNames,
            String[] bindAttribNames) throws IOException,
            OpenGLENException   {

        int len = vertexShaderNames.length;
        for (int i = 0; i < len; i++)       {
            InputStream[] vin = null;
            InputStream[] fin = null;
            //Compile and attach shaders
            try {

                vin = openVertexShaderSource(getProgramNameByIndex(i),
                        i, vertexShaderNames);
                if (vin == null){
                    //Could not load shader sources!
                    throw new IOException("Could not load vertexshaders for program index " + i);
                }
                int vshader = mProgramHandler.compileAndAttachShader(getProgramNameByIndex(i), -1,
                        ConstantValues.VERTEX_SHADER, vin, mGraphicHandler);

                fin = openFragmentShaderSource(getProgramNameByIndex(i), i,
                        fragmentShaderNames);
                if (fin == null){
                    //Could not load shader sources!
                    throw new IOException("Could not load fragmentshaders for program index " + i);
                }
                int fshader = mProgramHandler.compileAndAttachShader(getProgramNameByIndex(i), -1,
                        ConstantValues.FRAGMENT_SHADER, fin, mGraphicHandler);

                //Shaders compiled and attached ok, bind attributes.
                //Bind attribs:
                int program = getProgramNameByIndex(i);
                bindAttributes(program, bindAttribNames);

                mProgramHandler.linkProgram(program);
                int error = mGraphicHandler.checkError();

                if (error != ConstantValues.NO_ERROR ||
                        mProgramHandler.getProgramParam(program, ConstantValues.LINK_STATUS) !=
                        ConstantValues.TRUE) {

                    mProgramHandler.logProgramInfo(program);
                    throw new IllegalArgumentException("Error linking program " + program);

                }
            }
            finally {
                closeInputStream(vin);
                closeInputStream(fin);
            }
        }

    }

    /**
     * Closes the inputstreams if not null.
     * @param stream Streams to be closed, may be null.
     */
    protected void closeInputStream(InputStream[] stream) {
        if (stream != null) {
            for (int i = 0; i < stream.length; i++) {
                try {
                    stream[i].close();
                }
                catch (IOException ioe) {
                    //Cant do anything.
                    Log.d(TAG, ioe.toString());
                }
            }
        }
    }
    /**
     * Sets uniform locations that are needed to run the shaders, this method
     * shall be called after loading and compiling of programs.
     * Subclasses shall implement this method to get the uniform locations that are
     * needed and store them for faster retrieval when rendering.
     * @throws OpenGLENException If there is an error getting a uniform location.
     * Note that this exception is NOT thrown if a uniform name cannot be found.
     * If the exception is thrown it means a parameter was invalid (to GL) or that
     * a program have not been linked.
     */
    public abstract void setUniformLocations() throws OpenGLENException;

    /**
     * Bind the attributes for the specified program.
     * @param program
     * @param bindAttribNames Array containing attribute names to bind.
     */
    public void bindAttributes(int program, String[] bindAttribNames) {

        for (int i = 0; i < bindAttribNames.length; i++) {
            mProgramHandler.bindAttributeLocation(program, i, bindAttribNames[i]);
        }

        int error = mGraphicHandler.checkError();
        if (error != ConstantValues.NO_ERROR)
            throw new IllegalArgumentException("Could bind attribute in program (" + program + ")");


    }

    /**
     * Opens up inputstreams to the vertex shader sources,
     * streams can then be used to read sources, to attach and compile shader.
     * Throws IOException if source could not be opened.
     * @param program The program to open the vertex shaders for.
     * @param index Index of the program to open.
     * @param vertexShaderNames Arrays containing vertex shader names.
     * @return Array of InputStreams to the vertexshaders for the program.
     * @throws IOException If inputstream could not be opened.
     */
    public InputStream[] openVertexShaderSource(int program, int index,
            String[][] vertexShaderNames) throws IOException{

        InputStream[] streams = new InputStream[vertexShaderNames[index].length];
        int i = 0;
        for (i = 0; i < streams.length; i++)    {
            if (mInputResolver != null) {
                streams[i] = mInputResolver.openInputStream("/" + vertexShaderNames[index][i]);
            } else {
                streams[i] = this.getClass().getResourceAsStream("/" + vertexShaderNames[index][i]);
            }
            if (streams[i] == null) {
                throw new IOException(ERROR_OPENING_STREAM + vertexShaderNames[index][i]);
            }
        }
        return streams;
    }

    /**
     * Sets the inputstreamresolver to be used when opening programs.
     * Set the resolver if programs shall be loaded from a specific
     * location and not use the standard programs.
     * Set to null to remove set resolver and use default programs.
     * @param resolver Resolver to use when loading programs, custom
     * programs can now be loaded. Make sure to use same attributes/uniforms
     * or create new implementation of Renderer to send attributes/uniforms
     * to program.
     */
    public void setInputStreamResolver(InputStreamResolver resolver) {
        mInputResolver = resolver;
    }

    /**
     * Opens up inputstreams to the fragment shader sources,
     * streams can then be used to read sources, to attach and compile shader.
     * Throws IOException if source could not be opened.
     * @param program The program to open the fragment shaders for.
     * @param index The program index to open the fragment shaders for.
     * @param fragmentShaderNames Arrays containing fragment shader names.
     * @return Array of InputStreams to the vertexshaders for the program.
     * @throws IOException If inputstream could not be opened.
     */
    public InputStream[] openFragmentShaderSource(int program,int index,
            String[][] fragmentShaderNames) throws IOException{


        InputStream[] streams = new InputStream[fragmentShaderNames[index].length];
        int i = 0;
        for (i = 0; i < streams.length; i++)    {
            if (mInputResolver != null) {
                streams[i] = mInputResolver.openInputStream("/" + fragmentShaderNames[index][i]);
            } else {
                streams[i] = this.getClass().getResourceAsStream("/" + fragmentShaderNames[index][i]);
            }
            if (streams[i] == null) {
                throw new IOException(ERROR_OPENING_STREAM + fragmentShaderNames[index][i]);
            }
        }
        return streams;
    }


}
