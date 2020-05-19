package com.example.whatsthemove;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<String> barNames;
    private List<Integer> barPics;
    private LayoutInflater mInflater;
    private AdapterView.OnItemClickListener mClickListener;

    //Constructor
    public MyAdapter(Context context, List<String> bn, List<Integer> bp) {
        this.mInflater = LayoutInflater.from(context);
        this.barNames = bn;
        this.barPics = bp;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (ViewHolder holder, int position) {
        holder.myImageView.setImageResource(barPics.get(position));
        holder.myTextView.setText(barNames.get(position));
    }

    @Override
    public int getItemCount() {
        return barNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.row);
            myImageView = itemView.findViewById(R.id.barID);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int x;
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
