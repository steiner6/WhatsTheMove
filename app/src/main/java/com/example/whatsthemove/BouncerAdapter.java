package com.example.whatsthemove;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BouncerAdapter extends RecyclerView.Adapter<BouncerAdapter.ViewHolder> {

    private Context context;
    private List<String> bounceAdaptBarNames;
    private List<Integer> bounceAdaptBarPics;
    private LayoutInflater bounceInflater;

    public BouncerAdapter(Context context, List<String> barnames, List<Integer> barpics) {
        this.context = context;
        this.bounceInflater = LayoutInflater.from(context);
        this.bounceAdaptBarNames = barnames;
        this.bounceAdaptBarPics = barpics;
    }

    @Override
    public BouncerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = bounceInflater.inflate(R.layout.bouncerecyclerow, parent, false);
        return new BouncerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BouncerAdapter.ViewHolder holder, int position) {
        holder.myImageView.setImageResource(bounceAdaptBarPics.get(position));
        holder.myTextView.setText(bounceAdaptBarNames.get(position));
    }

    @Override
    public int getItemCount() { return bounceAdaptBarNames.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        ImageView myImageView;

        ViewHolder(View vs) {
            super(vs);
            myTextView = vs.findViewById(R.id.barn);
            myImageView = vs.findViewById(R.id.barp);
        }
    }

}
