package com.example.whatsthemove;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context context;
    private List<String> myAdaptBarNames;
    private List<Integer> myAdaptBarPics;
    private List<String> checkedBarNames;
    private List<Integer> checkedBarPics;
    private LayoutInflater myAdaptInflater;

    //Constructor
    public MyAdapter(Context context, List<String> bn, List<Integer> bp, List<String> sbn, List<Integer> sbp) {
        this.context = context;
        this.myAdaptInflater = LayoutInflater.from(context);
        this.myAdaptBarNames = bn;
        this.myAdaptBarPics = bp;
        this.checkedBarNames = sbn;
        this.checkedBarPics = sbp;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = myAdaptInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, final int position) {
        holder.myImageView.setImageResource(myAdaptBarPics.get(position));
        holder.myTextView.setText(myAdaptBarNames.get(position));
        holder.myCheckBox.setOnCheckedChangeListener(null);
        holder.myCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            Context thisContext = context;

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = thisContext.getSharedPreferences("whatsthemove", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                if (buttonView.isChecked()) {
                    String name = myAdaptBarNames.get(position);
                    Integer pic = myAdaptBarPics.get(position);
                    checkedBarNames.add(name);
                    checkedBarPics.add(pic);
                    editor.putBoolean(name, true).apply();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return myAdaptBarNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        ImageView myImageView;
        CheckBox myCheckBox;

        ViewHolder(View v) {
            super(v);
            myTextView = v.findViewById(R.id.row);
            myImageView = v.findViewById(R.id.barID);
            myCheckBox = v.findViewById(R.id.checkBox);
        }
    }

}
