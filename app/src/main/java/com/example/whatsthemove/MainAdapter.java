package com.example.whatsthemove;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>  {
    private Context context;
    private List<String> mainAdaptBarNames;
    private List<Integer> mainAdaptBarPics;
    private List<String> mainAdaptTags;
    private LayoutInflater mainInflater;
    private AdapterInterface listener;
    private List<Integer> barStatus;
    private List<String> fences;

    public MainAdapter(Context context, List<String> bn, List<Integer> bp, List<String> tg, AdapterInterface listener, List<Integer> stat, List<String> fences) {
        this.context = context;
        this.mainInflater = LayoutInflater.from(context);
        this.mainAdaptBarNames = bn;
        this.mainAdaptBarPics = bp;
        this.mainAdaptTags = tg;
        this.listener = listener;
        this.barStatus = stat;
        this.fences = fences;
    }

    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mainInflater.inflate(R.layout.mainrecyclerow, parent, false);
        return new MainAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder (final MainAdapter.ViewHolder holder, final int position) {
        holder.myImageView.setImageResource(mainAdaptBarPics.get(position));
        holder.myImageView.setTag(mainAdaptTags.get(position));
        holder.inlineTextView.setText(mainAdaptBarNames.get(position));
        holder.inlineTextView.setTag(barStatus.get(position));
        holder.pplinbarlabelTextView.setTag(fences.get(position));

        final String barnames = (String) holder.myImageView.getTag();
        final String gfencename = (String) holder.pplinbarlabelTextView.getTag();
        final int status = Integer.parseInt(holder.inlineTextView.getTag().toString());

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference barname = database.getReference(barnames);
        final DatabaseReference locations = database.getReference("locations");

        //Update data if bar is open
        if (status == 1) {
            //Get number of people in line
            barname.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value = snapshot.getValue(String.class);
                    if (value.equals("Closed")) {
                        barname.setValue("0");
                        locations.child(gfencename).child("tracked").setValue("0");
                        holder.pplinlineadaptTextView.setText("0");

                    } else {
                        holder.pplinlineadaptTextView.setText(value);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    error.toException();
                }
            });

            //Get number of people in bar
            locations.child(gfencename).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String value = snapshot.child("tracked").getValue(String.class);
                    holder.inbarTextView.setText(value);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    error.toException();
                }
            });

        //Set bar to closed and people in bar to 0 if bar is closed
        } else {
            barname.setValue("Closed");
            holder.pplinlineadaptTextView.setText("Closed");

            locations.child(gfencename).child("tracked").setValue("0");
            holder.inbarTextView.setText("0");
        }
    }

    @Override
    public int getItemCount() {
        return mainAdaptBarNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView inlineTextView;
        TextView inbarTextView;
        TextView pplinlineadaptTextView;
        ImageView myImageView;
        TextView pplinbarlabelTextView;

        ViewHolder(View itemView) {
            super(itemView);
            inlineTextView = itemView.findViewById(R.id.barname);
            myImageView = itemView.findViewById(R.id.barpic);
            pplinlineadaptTextView = itemView.findViewById(R.id.pplinlineadapt);
            inbarTextView = itemView.findViewById(R.id.pplinbaradapt);
            pplinbarlabelTextView = itemView.findViewById(R.id.pplinbaradaptlabel);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.gotoUpdate(inlineTextView, myImageView, pplinbarlabelTextView);
        }
    }

    public interface AdapterInterface {
         void gotoUpdate(TextView myTextView, ImageView view, TextView gfencename);
    }

}


