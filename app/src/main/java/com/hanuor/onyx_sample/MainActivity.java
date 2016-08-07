package com.hanuor.onyx_sample;

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
        final String m = "http://www.thisiscolossal.com/wp-content/uploads/2012/01/jon-1.jpg";
                Pearl.imageLoader(MainActivity.this,m,imageView,0);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Onyx.with(MainActivity.this).fromURL(m).getTagsfromApi(new OnTaskCompletion() {
                    @Override
                    public void onComplete(ArrayList<String> response) {
                        Log.d("Class",""+response);
                        tv.setText(response.toString());
                    }
                });
            }
        });

    }
}
