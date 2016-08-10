package com.hanuor.onyx.hub;

import android.content.Context;
import android.os.AsyncTask;

import com.hanuor.onyx.helper.ClarifaiClient;
import com.hanuor.onyx.helper.RecognitionRequest;
import com.hanuor.onyx.helper.RecognitionResult;
import com.hanuor.onyx.helper.Tag;

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
    byte[] videoArray = null;
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
    public Tags fromVideoArray(byte[] videoArray){
        tags.videoArray = videoArray;
        return tags;
    }


    public void getTagsfromApi(final OnTaskCompletion onTaskCompletion) {
        probableTags = new ArrayList<String>();
        try {
            if (tags.urls.length() == 0 && tags.videoArray.length == 0) {
                ErrorHandler.writeError("No string path is entered");
            } else if(tags.urls.length()!=0 ){
                new AsyncTask<String, Void, ArrayList<String>>() {

                    @Override
                    protected ArrayList<String> doInBackground(String... strings) {
                        List<RecognitionResult> results =
                                clarifai.recognize(new RecognitionRequest(tags.urls));
                        if(results.size() != 0){


                        for (Tag tag : results.get(0).getTags()) {
                            probableTags.add(tag.getName());
                        }
                        return probableTags;
                        }else{
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ArrayList<String> strings) {
                        super.onPostExecute(strings);
                        onTaskCompletion.onComplete(strings);
                    }
                }.execute(tags.urls);

            }else if (tags.videoArray.length!=0){
                new AsyncTask<byte[], Void, ArrayList<String>>() {


                    @Override
                    protected ArrayList<String> doInBackground(byte[]... bytes) {

                        List<RecognitionResult> results =
                                clarifai.recognize(new RecognitionRequest(tags.videoArray));
                        if( results.size() != 0){
                            for (Tag tag : results.get(0).getTags()) {
                            probableTags.add(tag.getName());
                            }
                            return probableTags;
                        }else{
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(ArrayList<String> strings) {
                        super.onPostExecute(strings);
                        onTaskCompletion.onComplete(strings);
                    }
                }.execute(videoArray);
            }else{
                ErrorHandler.writeError("Something is wrong. Please check you inputs");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getTagsandProbability(final OnTaskCompletion onTaskCompletion){
        probandTags = new ArrayList<String>();
        try {
            if(tags.urls.length() == 0 && tags.videoArray.length == 0){
                ErrorHandler.writeError("No path is entered");

            }else if (tags.urls.length()!=0){
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

            }else if(tags.videoArray.length!=0){
                new AsyncTask<byte[], Void, ArrayList<String>>() {
                    @Override
                    protected ArrayList<String> doInBackground(byte[]... bytes) {
                        List<RecognitionResult> results =
                                clarifai.recognize(new RecognitionRequest(tags.videoArray));
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
                }.execute(tags.videoArray);
            }else{
                ErrorHandler.writeError("Something is wrong. Please check your inputs");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
