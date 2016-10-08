package com.sunlianglong.volleystudy;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private RequestQueue mQueue;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.imageView);
        mQueue = Volley.newRequestQueue(this);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache() {
                    @Override
                    public Bitmap getBitmap(String s) {
                        return null;
                    }
                    @Override
                    public void putBitmap(String s, Bitmap bitmap) {
                    }
                });
                ImageLoader.ImageListener listener = ImageLoader.getImageListener(
                        imageView,R.drawable.facebook,R.drawable.facebook);
                imageLoader.get("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",listener);
            }
        });
    }
}
