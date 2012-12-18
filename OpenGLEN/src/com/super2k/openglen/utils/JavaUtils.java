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

public class JavaUtils {

    private final static int SLEEP = 10; //Millis to sleep between gc's

    /**
     * Tries to call System.gc() until amount of free memory has stabilized.
     * Will only perform a limited number of System.gc() calls. If free memory
     * hasn't stabilized by then it will give up.
     *
     * @return The number of bytes free memory. As returned by
     *         Runtime.getRuntime().freeMemory().
     */
    public static long stabilizeFreeMemory() {
        /** After having done this many GC calls we shall give up and return. */
        final int MAX_GC_ATTEMPTS = 10;
        /**
         * Number of GC's returning the same amount of free memory before we
         * consider memory to be stable.
         */
        final int MIN_GC_WITH_SAME_RESULT = 2;
        int nTotalGCAttempts = 0;
        int nGCWithSameResult = 0;
        long currFreeMemory = Runtime.getRuntime().freeMemory();
        long prevFreeMemory;

        do {
            prevFreeMemory = currFreeMemory;
            System.gc();

            try {
                Thread.sleep(SLEEP);
            }
            catch (InterruptedException ie) {}

            nTotalGCAttempts++;

            currFreeMemory = Runtime.getRuntime().freeMemory();
            if (currFreeMemory == prevFreeMemory) {
                nGCWithSameResult++;
                if (nGCWithSameResult >= MIN_GC_WITH_SAME_RESULT) {
                    break; // Stabilized!
                }
            } else {
                nGCWithSameResult = 0;
            }
        } while (nTotalGCAttempts < MAX_GC_ATTEMPTS);

        return currFreeMemory;
    }


}
