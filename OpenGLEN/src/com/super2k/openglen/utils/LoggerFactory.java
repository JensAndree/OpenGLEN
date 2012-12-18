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


/**
 * Factory for creating logger for the specific platform.
 * @author Richard Sahlin
 *
 */
public class LoggerFactory {

    protected final static String ANDROID_LOGGER =
            "com.super2k.openglen.android.utils.AndroidLogger";
    protected final static String J2SE_LOGGER =
            "com.super2k.openglen.j2se.utils.J2SELogger";

    /**
     * Creates a platform specific logger.
     * Currently supports Android and J2SE.
     * @return
     */
    public static Logger createLogger() {
        try     {

            String jre = System.getProperty("java.vendor");

            if (jre.equalsIgnoreCase("the android project")){
                return (Logger) Class.forName(ANDROID_LOGGER).newInstance();
            }
            else
                return (Logger) Class.forName(J2SE_LOGGER).newInstance();
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
