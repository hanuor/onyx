package com.hanuor.onyx.hub;

import android.content.Context;
import android.os.AsyncTask;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shantanu Johri on 07-08-2016.
 */
public class Tags {
    Context context = null;
    Tags tags = null;
    String urls = null;
    ArrayList<String> probableTags = null;
    ArrayList<String> probandTags = null;
    KeyContainer keyContainer = new KeyContainer();
    ClarifaiClient clarifai = new ClarifaiClient(keyContainer.getApiID(), keyContainer.getApiSecret());
    public Tags(Context context){
        if(context == null){
            ErrorHandler.writeError("Context cannot be null");
        }else{
            this.context = context;
        }
    }
    public void setInstance(Tags tags){
        this.tags = tags;
    }

    public Tags fromURL(String urls){
        tags.urls = urls;
        return tags;
    }
    public void getTagsfromApi(final OnTaskCompletion onTaskCompletion) {
        probableTags = new ArrayList<String>();
        try {
            if (tags.urls.length() == 0) {
                ErrorHandler.writeError("No string path is entered");

            } else {
                new AsyncTask<String, Void, ArrayList<String>>() {

                    @Override
                    protected ArrayList<String> doInBackground(String... strings) {
                        List<RecognitionResult> results =
                                clarifai.recognize(new RecognitionRequest(tags.urls));
                        for (Tag tag : results.get(0).getTags()) {
                            probableTags.add(tag.getName());
                        }
                        return probableTags;

                    }

                    @Override
                    protected void onPostExecute(ArrayList<String> strings) {
                        super.onPostExecute(strings);
                        onTaskCompletion.onComplete(strings);
                    }
                }.execute(tags.urls);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getTagsandProbability(final OnTaskCompletion onTaskCompletion){
        probandTags = new ArrayList<String>();
        try {
            if(tags.urls.length() == 0){
                ErrorHandler.writeError("No string path is entered");

            }else{
                new AsyncTask<String, Void, ArrayList<String>>() {

                    @Override
                    protected ArrayList<String> doInBackground(String... strings) {
                        List<RecognitionResult> results =
                                clarifai.recognize(new RecognitionRequest(tags.urls));
                        for (Tag tag : results.get(0).getTags()) {
                            probandTags.add(tag.getName()+"-"+tag.getProbability());
                        }
                        return probandTags;

                    }

                    @Override
                    protected void onPostExecute(ArrayList<String> strings) {
                        super.onPostExecute(strings);
                        onTaskCompletion.onComplete(strings);
                    }
                }.execute(tags.urls);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
