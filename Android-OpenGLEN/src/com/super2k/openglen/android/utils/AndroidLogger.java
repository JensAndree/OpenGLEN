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
package com.super2k.openglen.android.utils;

import android.util.Log;

import com.super2k.openglen.ProfileInfo;
import com.super2k.openglen.utils.BaseLogger;
import com.super2k.openglen.utils.Logger;

/**
 * Class to abstract the logging on Android, this is so that classes can use a logger in a platform independent manner.
 * This is an implementation of the Logger interface.
 * @link com.super2k.openglen.utils.Log;
 * @author Richard Sahlin
 *
 */
public class AndroidLogger extends BaseLogger implements Logger {

    @Override
    public void logMessage(String tag, String message, int loglevel) {
        switch (loglevel) {
            case (LOGLEVEL_DEBUG):
                Log.d(tag, message);
                break;
            case (LOGLEVEL_INFO):
                Log.i(tag, message);
                break;
            case (LOGLEVEL_VERBOSE):
                Log.v(tag, message);
                break;
            case (LOGLEVEL_WARN):
                Log.w(tag, message);
                break;
            case (LOGLEVEL_ERROR):
                Log.e(tag, message);
                break;
            default:
                throw new IllegalArgumentException("Invalid loglevel: " + loglevel);
        }

    }

    @Override
    public void logProfileInfo(String name, ProfileInfo info, int loglevel) {
        if (info == null) {
            throw new IllegalArgumentException("ProfileInfo is null.");
        }
        String[] strs = info.getProfileInforStr();
        for (int i = 0; i < strs.length; i++)   {
            logMessage(name, strs[i], loglevel);
        }


    }

    @Override
    public void logFillrate(String name, int max, int min, int average, int loglevel) {

        logMessage(name, LOG_FILLRATE_STR + average + "/" + max + "/" + min, loglevel);

    }

    @Override
    public void logStartOfUsecase(String label, int loglevel) {
        logMessage("Usecase", "Start:" + label, loglevel);
    }

    @Override
    public void logEndOfUsecase(int loglevel) {

        logMessage("Usecase", "Stop", loglevel);

    }


}
