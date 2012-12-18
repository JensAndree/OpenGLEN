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
package com.super2k.openglen.j2se.utils;

import com.super2k.openglen.ProfileInfo;
import com.super2k.openglen.utils.BaseLogger;
import com.super2k.openglen.utils.Logger;

/**
 * J2SE implementation of the Logger interface.
 * On J2SE output log messages to System.out
 * @author Richard Sahlin
 *
 */
public class J2SELogger extends BaseLogger implements Logger {

    @Override
    public void logMessage(String tag, String message, int loglevel) {

        System.out.println(tag + " : " + message);


    }

    @Override
    public void logProfileInfo(String name, ProfileInfo info, int loglevel) {

        String[] strs = info.getProfileInforStr();
        for (int i = 0; i < strs.length; i++)   {
            System.out.println(name + " : " + strs[i]);
        }

    }

    @Override
    public void logFillrate(String name, int max, int min, int average, int loglevel) {

        System.out.println(name + " : " + LOG_FILLRATE_STR +
                            + average + "/" + max + "/" + min);

    }

    @Override
    public void logStartOfUsecase(String label, int loglevel) {
        System.out.println("Usecase: Start:" + label);
    }

    @Override
    public void logEndOfUsecase(int loglevel) {
        System.out.println("Usecase: Stop");
    }

}
