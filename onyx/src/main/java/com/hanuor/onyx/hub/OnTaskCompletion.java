package com.hanuor.onyx.hub;

import java.util.ArrayList;

/**
 * Created by Shantanu Johri on 07-08-2016.
 */
public interface OnTaskCompletion {

    public void onComplete(ArrayList<String> response);

    /**
     * This method is called is called when request is
     *  not completed due to some fault
     */

}
