package com.example.budgetwatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AggregationAdapter extends RecyclerView.Adapter<AggregationAdapter.AggregationViewHolder> {


    List<Aggregation> aggregations;
    View.OnClickListener onClickListener;
    public AggregationAdapter(List<Aggregation> aggregations){
        this.aggregations=aggregations;
    }
    @NonNull
    @Override
    public AggregationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.aggregation_item,parent,false);
        return new AggregationAdapter.AggregationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AggregationViewHolder holder, int position) {
        Aggregation aggregation = aggregations.get(position);
        holder.setAggregation(aggregation);
        holder.itemView.setOnClickListener(onClickListener);
    }
    public void add(Aggregation aggregation){
        aggregations.add(aggregation);
        notifyItemChanged(aggregations.size()-1);
    }
    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {
        return aggregations.size();
    }

    public class AggregationViewHolder extends RecyclerView.ViewHolder {
        TextView txtDateRange;
        TextView txtTags;
        TextView txtTotal;
        Aggregation aggregation;
        public AggregationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDateRange = itemView.findViewById(R.id.tvDateRange);
            txtTags = itemView.findViewById(R.id.tvTags);
            txtTotal = itemView.findViewById(R.id.tvTotalAmount);
        }

        public void setAggregation(Aggregation aggregation) {
            this.aggregation=aggregation;
            txtDateRange.setText(aggregation.getDateRange());
            txtTotal.setText(String.format("%.2f",aggregation.getTotal()));

            String s="";
            for(String tag:aggregation.getTags()){
                s+=tag+", ";
            }
            txtTags.setText("");
            txtTags.setText("Tags: "+s);
        }
    }
}
