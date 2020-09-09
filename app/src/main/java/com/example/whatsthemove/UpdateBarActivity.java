package com.example.whatsthemove;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
    String geofenceID;
    String gkey;
    Integer barStatus;
    Boolean presentFlag;
    Button updatelineButton;
    Button updatebarButton;
    Context context = UpdateBarActivity.this;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatebar);

        updatelineButton = findViewById(R.id.editlinebutton);
        updatebarButton = findViewById(R.id.editinbarbutton);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        name = bundle.getString("name");
        tag = bundle.getString("tag");
        geofenceID = bundle.getString("geofence");
        gkey = bundle.getString("gkey");
        presentFlag = bundle.getBoolean("presentFlag");
        barStatus = bundle.getInt("stat");

        TextView title = findViewById(R.id.title);
        ImageView bitimage = findViewById(R.id.bitimage);
        title.setText(name);
        Resources resources = context.getResources();
        int resourceID = resources.getIdentifier(tag, "drawable", context.getPackageName());
        bitimage.setImageResource(resourceID);

        seeUpdate();

        updatelineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (barStatus == 1) {
                    if (geofenceID.equals(gkey) && presentFlag == true) {
                        AttentionDialogLine(view);
                    } else {
                        notInBarDialog(view);
                    }
                } else {
                    ClosedDialog();
                }
            }
        });

        updatebarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (barStatus == 1) {
                    if (geofenceID.equals(gkey) && presentFlag == true) {
                        AttentionDialogBar(view);
                    } else {
                        notInBarDialog(view);
                    }
                } else {
                    ClosedDialog();
                }
            }
        });

    }


    public void AttentionDialogLine(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Attention!")
                .setMessage("The information you are about to enter will update to all users. Please enter accurate numbers so we can all benefit!")
                .setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BuildDialogLine();
                    }
                })
                .show();
    }

    public void BuildDialogLine() {

        final EditText input = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Update the Line")
                .setMessage("Enter the number of people currently in line at " + name)
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Editable usrinput = input.getText();
                        validUsrInputLine(usrinput);
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

    public void AttentionDialogBar(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Attention!")
                .setMessage("The information you are about to enter will update to all users. Please enter accurate numbers so we can all benefit!")
                .setPositiveButton("Ok!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BuildDialogBar();
                    }
                })
                .show();
    }

    public void BuildDialogBar() {

        final EditText input = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Who's in the bar?")
                .setMessage("Enter the number of people currently at " + name)
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Editable usrinput = input.getText();
                        validUsrInputBar(usrinput);
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


    public void notInBarDialog(View view) {
        new AlertDialog.Builder(this)
                .setTitle("You're not at " + name)
                .setMessage("You can only update the information of the bar that you are at")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
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

    public void validUsrInputLine(Editable usrinput) {
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
                                BuildDialogLine();
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
                        BuildDialogLine();
                    }
                })
                .show();
        }

        seeUpdate();

    }

    public void validUsrInputBar(Editable usrinput) {
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
                                BuildDialogBar();
                            }
                        })
                        .show();
            } else {
                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("locations");
                myRef.child(gkey).child("tracked").setValue(usr);
            }
        } catch (NumberFormatException e) {
            new AlertDialog.Builder(this)
                    .setTitle("Error!")
                    .setMessage("Please input a numeric value between 0 and 500")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            BuildDialogBar();
                        }
                    })
                    .show();
        }

        seeUpdate();

    }

    public void seeUpdate() {

        final TextView pplinline = findViewById(R.id.pplinline);
        final TextView pplinbar = findViewById(R.id.peepinbar);
        final DatabaseReference locationDatabase = FirebaseDatabase.getInstance().getReference().child("locations");
        final DatabaseReference inline = FirebaseDatabase.getInstance().getReference(tag);

        inline.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (barStatus == 1) {
                    String value = snapshot.getValue(String.class);
                    pplinline.setText(value);
                } else {
                    inline.setValue("Closed");
                    pplinline.setText("Closed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException();
            }
        });

        locationDatabase.child(geofenceID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (barStatus == 1) {
                    String value = snapshot.child("tracked").getValue(String.class);
                    pplinbar.setText(value);
                } else {
                    locationDatabase.child(geofenceID).child("tracked").setValue("0");
                    pplinbar.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException();
            }
        });
    }

    public void gotoMain(View view) {
        finish();
    }

}
