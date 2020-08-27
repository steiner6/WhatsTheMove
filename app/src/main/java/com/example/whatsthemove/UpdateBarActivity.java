package com.example.whatsthemove;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.activity_updatebar);

        Bundle bundle = intent.getExtras();
        String a = bundle.getString("name");
        byte[] b = bundle.getByteArray("picture");
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);

        TextView title = findViewById(R.id.title);
        ImageView bitimage = findViewById(R.id.bitimage);
        title.setText(a);
        bitimage.setImageBitmap(bmp);
    }

    public void gotoMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
