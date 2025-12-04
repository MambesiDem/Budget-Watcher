package com.example.budgetwatcher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private View.OnClickListener onClickListener;
    List<Transaction> transactions;
    public TransactionAdapter(){
        this.transactions = MessageListActivity.transactions;
    }
    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_layout,parent,false);
        return new TransactionViewHolder(view,this);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.setTransaction(transaction);
        holder.itemView.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }
    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }
    public void updateList(List<Transaction> newTransactions){
        transactions = newTransactions;
        notifyDataSetChanged();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView txtType;
        TextView txtDate;
        TextView txtDescription;
        TextView txtAmount;
        TextView txtBalance;
        LinearLayout tagsContainer;
        Button addTag;
        Transaction transaction;
        private TransactionAdapter adapter;
        public TransactionViewHolder(@NonNull View itemView,TransactionAdapter adapter) {
            super(itemView);

            this.adapter = adapter;
            txtType = itemView.findViewById(R.id.tvTransactionType);
            txtDate = itemView.findViewById(R.id.tvTransactionDate);
            txtDescription = itemView.findViewById(R.id.tvTransactionDescription);
            txtAmount = itemView.findViewById(R.id.tvTransactionAmount);
            txtBalance = itemView.findViewById(R.id.tvAvailableBalance);
            tagsContainer = itemView.findViewById(R.id.tag_container);
            addTag = itemView.findViewById(R.id.btn_add_tag);

            addTag.setOnClickListener(view -> {
                showInputDialog();
            });
        }
        @SuppressLint("SetTextI18n")
        public void setTransaction(Transaction transaction){
            this.transaction = transaction;
            txtDate.setText(transaction.getDate().toString());
            txtType.setText(transaction.getType());
            txtDescription.setText(transaction.getDescription());
            txtBalance.setText(String.format("%.2f",transaction.getBalance()));
            txtAmount.setText(String.format("%.2f",transaction.getAmount()));
            tagsContainer.removeAllViews();
            for(String tag: transaction.getTags()){
                TextView textView = new TextView(this.itemView.getContext());
                textView.setText(tag);
                textView.setGravity(Gravity.CENTER);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                       300,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.weight = 1;

                textView.setLayoutParams(params);

                tagsContainer.addView(textView);
            }
        }
        public void showInputDialog() {
            LayoutInflater inflater = LayoutInflater.from(this.itemView.getContext());
            View dialogView = inflater.inflate(R.layout.dialog_edit_text, null);

            EditText editText = dialogView.findViewById(R.id.edit_text);

            AlertDialog.Builder builder = new AlertDialog.Builder(this.itemView.getContext());
            builder.setTitle("Enter Input")
                    .setView(dialogView)
                    .setPositiveButton("OK", (dialog, which) -> {
                        String tag = editText.getText().toString();
                        Set<String> keys = MessageListActivity.tags.keySet();
                        transaction.addTag(tag);
                        if(!keys.contains(tag))MessageListActivity.tags.put(tag.toLowerCase(), Arrays.asList(tag));
                        adapter.notifyItemChanged(this.getAdapterPosition());
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }
    }
}
