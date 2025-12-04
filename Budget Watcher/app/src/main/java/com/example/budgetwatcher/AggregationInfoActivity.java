package com.example.budgetwatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class AggregationInfoActivity extends AppCompatActivity {

    TextView txtDateRange;
    TextView txtTotalAmount;
    TextView txtSelectedTags;
    ListView lstOfTrans;

    Aggregation aggregation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggregation_info);

        txtDateRange = findViewById(R.id.txt_date_range);
        txtTotalAmount = findViewById(R.id.txt_total_amount);
        txtSelectedTags = findViewById(R.id.txt_selected_tags);
        lstOfTrans = findViewById(R.id.lst_transactions);
        Intent intent =getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            String[] tags = (String[])extras.getStringArray("Tags");
            List<String> newTagsList = Arrays.asList(tags);
            Transaction[] transactions = (Transaction[]) extras.getSerializable("Transactions");
            List<Transaction> newTransList = Arrays.asList(transactions);
            double totalAmount = extras.getDouble("TotalAmount");
            String dateRange = extras.getString("DateRange");
            setTags(newTagsList);
            txtDateRange.setText(dateRange);
            txtTotalAmount.setText("R"+Double.toString(totalAmount));
            setTransactions(newTransList);
        }
    }

    private void setTransactions(List<Transaction> transactions) {
        ArrayAdapter<Transaction> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1,
                transactions);
        lstOfTrans.setAdapter(adapter);
    }

    private void setTags(List<String> tags) {
        String s="";
        for(String tag:tags){
            s+=tag+", ";
        }
        txtSelectedTags.setText("Tags: "+s);
    }

    public void onBackBtnClicked(View view) {
        finish();
    }
}