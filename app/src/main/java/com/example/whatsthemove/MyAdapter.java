package com.example.whatsthemove;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    public Context context;
    private List<String> barNames;
    private List<Integer> barPics;
    private List<String> checkedBarNames;
    private List<Integer> checkedBarPics;
    private LayoutInflater mInflater;

    //Constructor
    public MyAdapter(Context context, List<String> bn, List<Integer> bp, List<String> sbn, List<Integer> sbp) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.barNames = bn;
        this.barPics = bp;
        this.checkedBarNames = sbn;
        this.checkedBarPics = sbp;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, final int position) {
        holder.myImageView.setImageResource(barPics.get(position));
        holder.myTextView.setText(barNames.get(position));
        holder.myCheckBox.setOnCheckedChangeListener(null);
        holder.myCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            Context thisContext = context;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = thisContext.getSharedPreferences("whatsthemove", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                if (buttonView.isChecked()) {
                    String name = barNames.get(position);
                    Integer pic = barPics.get(position);
                    checkedBarNames.add(name);
                    checkedBarPics.add(pic);
                    editor.putBoolean(name, true).apply();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return barNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        ImageView myImageView;
        CheckBox myCheckBox;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.row);
            myImageView = itemView.findViewById(R.id.barID);
            myCheckBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }

}
