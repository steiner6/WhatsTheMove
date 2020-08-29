package com.example.whatsthemove;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateBarActivity extends AppCompatActivity {

    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatebar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String a = bundle.getString("name");
        setName(a);
        byte[] b = bundle.getByteArray("picture");
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        TextView title = findViewById(R.id.title);
        ImageView bitimage = findViewById(R.id.bitimage);
        title.setText(a);
        bitimage.setImageBitmap(bmp);

    }

    public void AttentionDialog(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Attention!")
                .setMessage("This app works best with accurate data. Please input accurate numbers to continue making this app great!")
                .setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BuildDialog();
                    }
                })
                .show();
    }

    public void BuildDialog() {

        final EditText input = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Update the Line")
                .setMessage("Enter the number of people currently in line at " + name)
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Editable usrinput = input.getText();
                        validUsrInput(usrinput);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void validUsrInput(Editable usrinput) {
        String usr = usrinput.toString();

        try {
            int num = Integer.parseInt(usr);
            if (num < 0 || num > 500) {
                new AlertDialog.Builder(this)
                        .setTitle("Error!")
                        .setMessage("Please input a numeric value between 0 and 500")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                BuildDialog();
                            }
                        })
                        .show();
            } else {

            }
        } catch (NumberFormatException e) {
            new AlertDialog.Builder(this)
                .setTitle("Error!")
                .setMessage("Please input a numeric value between 0 and 500")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        BuildDialog();
                    }
                })
                .show();
        }

    }

    public void gotoMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void setName(String a) {
        name = a;
    }

}
