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

package com.super2k.openglen.nibbler;

import java.util.Vector;

import com.super2k.openglen.test.OpenGLENTester;
import com.super2k.openglen.utils.Log;

/**
 * Base class that can be used for creating testcases in OpenGLEN using
 * the CompatibilityRunner. Good for testcases that do actual rendering.
 * @author Richard Sahlin
 *
 */
public abstract class BaseOpenGLENTester extends BaseCompatibilityRunner implements OpenGLENTester {

    private final String TAG = this.getClass().getSimpleName();

    /**
     * Millis to sleep each loop while waiting for resultlistener.
     */
    protected final static int WAIT_SLEEP = 100;

    protected Vector<OpenGLENTester.ResultListener> mResultListener =
            new Vector<OpenGLENTester.ResultListener>();

    @Override
    public void addResultListener(ResultListener callback) {
        mResultListener.add(callback);
        Log.d(TAG, "Adding ResultListener: " + callback.getClass().getSimpleName());
    }

    /**
     * Sends the result to the registered listeners, do this after completing
     * the test.
     * @param result Result of test.
     */
    public void finishTest(ResultBuffer result) {
        int count = mResultListener.size();
        Log.d(TAG, "Sending result to " + count + " listeners, with result: " + result);
        for (int i = 0; i < count; i++) {
            mResultListener.elementAt(i).result(result);
        }
    }

    /**
     * Utility method for waiting until a certain number of resultlisteners are set.
     * @param count Number of resultlisteners to wait for. Method will stall until this many number
     * of listeners added.
     * @param timeout Timeout in milliseconds, if the required number of listeners are not
     * added within the time limit an exception is thrown.
     */
    public void waitForResultListener(int count, int timeout) {
        long start = System.currentTimeMillis();
        int timer = 0;
        while (mResultListener.size() < count && timer < timeout) {
            try {
                Thread.sleep(WAIT_SLEEP);
            }
            catch (InterruptedException e) {
                //Cant do anything
            }
            timer = (int) (System.currentTimeMillis() - start);
        }
        //Check for timeout
        if (mResultListener.size() < count) {
            throw new IllegalArgumentException("Timeout wating for " + count + " listeners");
        }

    }


}
