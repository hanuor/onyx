package com.hanuor.onyx;

import android.content.Context;

import com.hanuor.onyx.hub.Tags;

/**
 * Created by Shantanu Johri on 07-08-2016.
 */
public class Onyx {
    volatile static Tags mTags =  null;
    public static Tags with(Context context){
        mTags = new Tags(context);
        mTags.setInstance(mTags);
        return mTags;
    }
}
