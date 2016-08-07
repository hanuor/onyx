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
        final String m = "http://animals.sandiegozoo.org/sites/default/files/juicebox_slides/rocky_mountains_gray_wolf.jpg";
                Pearl.imageLoader(MainActivity.this,m,imageView,0);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Loading...");
                pd.show();
                Onyx.with(MainActivity.this).fromURL(m).getTagsfromApi(new OnTaskCompletion() {
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
