package com.example.whatsthemove;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateBarActivity extends AppCompatActivity {

    String name;
    String tag;
    Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatebar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String a = bundle.getString("name");
        String t = bundle.getString("tag");
        setName(a, t);
        final Integer barStatus = bundle.getInt("stat");
        byte[] b = bundle.getByteArray("picture");
        Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length);
        TextView title = findViewById(R.id.title);
        ImageView bitimage = findViewById(R.id.bitimage);
        title.setText(a);
        bitimage.setImageBitmap(bmp);

        seeUpdate();

        updateButton = findViewById(R.id.editbutton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (barStatus == 1) {
                    AttentionDialog(view);
                } else {
                    ClosedDialog();
                }
            }
        });

    }

    public void AttentionDialog(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Attention!")
                .setMessage("The information you are about to enter will update to all users. Please enter accurate numbers so we can all benefit!")
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

    public void ClosedDialog() {
        new AlertDialog.Builder(this)
                .setTitle(name + " is closed")
                .setMessage("Unfortunately you can't update line information for closed bars")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
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
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(tag);
                myRef.setValue(usr);
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

    public void seeUpdate() {
        final TextView pplinline = findViewById(R.id.pplinline);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(tag);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                pplinline.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException();
            }
        });
    }

    public void gotoMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void setName(String a, String t) {
        name = a;
        tag = t;
    }

}
