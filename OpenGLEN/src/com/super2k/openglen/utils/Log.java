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
 * Class used for logging in a platform independent manner.
 * Uses an instance of the Logger interface.
 * @author Richard Sahlin
 *
 */
public class Log {

    /**
     * Creates an instance of the logger for the specific platform.
     */
    private static Logger  logger = LoggerFactory.createLogger();

    /**
     * Output profile info at this interval
     */
    protected int mProfileInterval = 200;

    /**
     * Logs a debug message.
     * @param tag Textual representation to tie to the message, could be caller name.
     * @param message The debug message to log
     */
    public final static void d(String tag, String message) {
        logger.logMessage(tag, message, Logger.LOGLEVEL_DEBUG);
    }

    /**
     * Logs an info message
     * @param tag Textual representation to tie to the message, could be caller name.
     * @param message The debug message to log
     */
    public final static void i(String tag, String message) {
        logger.logMessage(tag, message, Logger.LOGLEVEL_INFO);
    }

    /**
     * Logs an verbose message
     * @param tag Textual representation to tie to the message, could be caller name.
     * @param message The debug message to log
     */
    public final static void v(String tag, String message) {
        logger.logMessage(tag, message, Logger.LOGLEVEL_VERBOSE);
    }
    /**
     * Logs a warning message
     * @param tag Textual representation to tie to the message, could be caller name.
     * @param message The debug message to log
     */
    public final static void w(String tag, String message) {
        logger.logMessage(tag, message, Logger.LOGLEVEL_WARN);
    }
    /**
     * Logs an error message
     * @param tag Textual representation to tie to the message, could be caller name.
     * @param message The debug message to log
     */
    public final static void e(String tag, String message) {
        logger.logMessage(tag, message, Logger.LOGLEVEL_ERROR);
    }

    /**
     * Checks if the profiling has been running for more than the frame interval, if so
     * the profiling is output to the log.
     * NOTE! This method does NOT reset the the profileinfo which means that after the
     * interval number of frames has elapsed the info will be logged every consequent frame
     * unless the caller resets the profileinfo.
     * Message will be logged to LOGLEVEL_INFO
     * @param name
     * @param info
     * @return true if profileinfo was logged.
     * @throws IllegalArgumentException if info is null or loglevel is invalid
     */
    public final static boolean logProfileInfoInterval(String name, ProfileInfo info,
                                                       int frameInterval, int loglevel) {
        if (info.getFramecount() >= frameInterval) {
            logProfileInfo(name, info, loglevel);
            return true;
        }
        return false;
    }

    /**
     * Logs the profile info, if PET is running info will also be sent to PET.
     * Pet will only be available on the Android platform.
     * @param name Name to attach with the output.
     * Usually either use the calling classname or the package name.
     * @param info The profile info to log.
     * @param loglevel The loglevel to log to.
     * @throws IllegalArgumentException If info is null or loglevel is invalid
     */
    public final static void logProfileInfo(String name, ProfileInfo info, int loglevel) {

        logger.logProfileInfo(name, info, loglevel);
    }

    /**
     * Marks the start of a usecase in the log, or to PET if that is running.
     * Message will be logged to LOGLEVEL_DEBUG
     * @param label Text to be set to start of usecase
     */
    public final static void logStartOfUsecase(String label) {
        logger.logStartOfUsecase(label, Logger.LOGLEVEL_DEBUG);
    }

    /**
     * Marks the end of a usecase in the log, or to PET if that is running.
     * Message will be logged to LOGLEVEL_DEBUG
     */
    public final static void logEndOfUsecase() {
        logger.logEndOfUsecase(Logger.LOGLEVEL_DEBUG);
    }

    /**
     * Logs the fillrate with max/min/average values.
     * Message will be logged to LOGLEVEL_INFO
     * @param name Name to attach with the output.
     * Usually either use the calling classname or the package name.
     * @param max The max fillrate during the measurement period, in Mpix/s
     * @param min The min fillrate during the measurement period, in Mpix/s
     * @param average The average fillrate during the measurement period, in Mpix/s
     */
    public final static void logFillrate(String name, int max, int min, int average)   {
        logger.logFillrate(name, max, min, average, Logger.LOGLEVEL_INFO);
    }

}
