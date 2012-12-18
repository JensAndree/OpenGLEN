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

import com.super2k.openglen.ProfileInfo;

/**
 * Interface for logging.
 * @author Richard Sahlin
 *
 */
public interface Logger {

    /**
     * Loglevels, the current loglevel is defined by or'ing the values together.
     * Default is LOGLEVEL_DEBUG | LOGLEVEL_ERROR which will output all debug + error messages.
     */
    public final static int LOGLEVEL_INFO = 1;
    public final static int LOGLEVEL_DEBUG = 2;
    public final static int LOGLEVEL_VERBOSE = 4;
    public final static int LOGLEVEL_WARN = 8;
    public final static int LOGLEVEL_ERROR = 16;

    /**
     * Outputs a message to the log with a specified tag.
     * Where the output is directed is platform dependent.
     * @param tag
     * @param message
     * @param loglevel The loglevel.One of the LOGLEVEL_XXX values.
     * @throws IllegalArgumentException If loglevel is invalid
     */
    void logMessage(String tag, String message, int loglevel);

    /**            String platform = System.getProperty("os.arch");

     * Logs the profile info, if PET is running info will also be sent to PET.
     * PET will only be available on the Android platform
     * @param name
     * @param info
     * @param loglevel The loglevel.One of the LOGLEVEL_XXX values.
     * @throws IllegalArgumentException If loglevel is invalid or info is null
     */
    void logProfileInfo(String name, ProfileInfo info, int loglevel);

    /**
     * Logs the fillrate in million pixels/s with max/min/average values,
     * if PET is running log will be sent to PET.
     * PET will only be available on the Android platform
     * @param name
     * @param max The max fillrate during the measurement period
     * @param min The min fillrate during the measurement period
     * @param average The average fillrate during the measurement period
     * @param loglevel The loglevel.One of the LOGLEVEL_XXX values.
     * @throws IllegalArgumentException If loglevel is invalid
     */
    void logFillrate(String name, int max, int min, int average, int loglevel);

    /**
     * Logs the start of a usecase with a specified text label.
     * If PET is running a START_DATA marker with the specified String
     * will be sent.
     * @param label The text
     * @param loglevel The loglevel.One of the LOGLEVEL_XXX values.
     * @throws IllegalArgumentException If loglevel is invalid
     */
    void logStartOfUsecase(String label, int loglevel);

    /**
     * Logs the end of a usecase.
     * If PET is running a STOP_DATA marker will be sent.
     * @param loglevel The loglevel.One of the LOGLEVEL_XXX values.
     * @throws IllegalArgumentException If loglevel is invalid
     */
    void logEndOfUsecase(int loglevel);
}
