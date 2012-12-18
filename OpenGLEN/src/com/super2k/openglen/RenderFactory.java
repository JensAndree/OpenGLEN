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
package com.super2k.openglen;



/**
 * Creates platform implementation of Renderer interface.
 * Renderers can be created with different type of configurations.
 * The configuration controls how shader programs are handled
 * for the renderer.
 * @author Richard Sahlin
 *
 */
public class RenderFactory {

    /**
     * Default renderer for J2SE
     */
    protected final static String J2SE_RENDERER = "com.super2k.openglen.j2se.JOGLGLES20Renderer";

    /**
     * Default renderer for Android
     */
    protected final static String ANDROID_RENDERER = "com.super2k.openglen.android.GLES20EGLRenderer";
    /**
     * This will create the default renderer for the current platform,
     * currently Android or J2SE
     * @return The default renderer implementation.
     */
    public Renderer createRenderer() {
        try     {

            String jre = System.getProperty("java.vendor");

            if (jre.equalsIgnoreCase("the android project")){
                return (Renderer) Class.forName(ANDROID_RENDERER).newInstance();
            }
            else
                return (Renderer) Class.forName(J2SE_RENDERER).newInstance();
        }
        catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(cnfe);
        }
        catch (IllegalAccessException iae)  {
            throw new RuntimeException( iae);
        }
        catch (InstantiationException ie)   {
            throw new RuntimeException( ie);
        }

    }


}
