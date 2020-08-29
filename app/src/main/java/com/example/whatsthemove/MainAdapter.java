package com.example.whatsthemove;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>  {
    private Context context;
    private List<String> mainAdaptBarNames;
    private List<Integer> mainAdaptBarPics;
    private List<String> mainAdaptTags;
    private LayoutInflater mainInflater;
    private AdapterInterface listener;

    public MainAdapter(Context context, List<String> bn, List<Integer> bp, List<String> tg, AdapterInterface listener) {
        this.context = context;
        this.mainInflater = LayoutInflater.from(context);
        this.mainAdaptBarNames = bn;
        this.mainAdaptBarPics = bp;
        this.mainAdaptTags = tg;
        this.listener = listener;
    }

    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mainInflater.inflate(R.layout.mainrecyclerow, parent, false);
        return new MainAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (final MainAdapter.ViewHolder holder, final int position) {
        holder.myImageView.setImageResource(mainAdaptBarPics.get(position));
        holder.myImageView.setTag(mainAdaptTags.get(position));
        holder.myTextView.setText(mainAdaptBarNames.get(position));
    }



    @Override
    public int getItemCount() {
        return mainAdaptBarNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView myTextView;
        ImageView myImageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.barname);
            myImageView = itemView.findViewById(R.id.barpic);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.gotoUpdate(myTextView, myImageView);
        }
    }

    public interface AdapterInterface {
         void gotoUpdate(TextView myTextView, ImageView view);
    }

}


