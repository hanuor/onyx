/*
 * Copyright (C) 2016 Hanuor Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hanuor.onyx_sample;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanuor.onyx.Onyx;
import com.hanuor.onyx.hub.OnTaskCompletion;
import com.hanuor.pearl.Pearl;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button btn;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.ivy);
        tv = (TextView) findViewById(R.id.textView);
        final ProgressDialog pd = new ProgressDialog(MainActivity.this);
        final String mImage = "https://cdn.tutsplus.com/photo/uploads/legacy/720_blackwhiteRU/65.jpg";
        Pearl.imageLoader(MainActivity.this,mImage,imageView,0);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Loading...");
                pd.show();
                Onyx.with(MainActivity.this).fromURL(mImage).getTagsfromApi( new OnTaskCompletion() {
                    @Override
                    public void onComplete(ArrayList<String> response) {
                        Log.d("Class",""+response);
                        pd.dismiss();
                        tv.setText(response.toString());
                        tv.setTextColor(Color.parseColor("#ffffff"));


                    }
                });
            }
        });

    }
}
