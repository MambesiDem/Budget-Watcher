package com.example.budgetwatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AggregationActivity extends AppCompatActivity {
    private Button startDatePickerButton;
    private Button endDatePickerButton;
    private TextView selectedStartDate;
    private TextView selectedEndDate;

    private ChipGroup tagChipGroup;
    List<String> curSelectedTags;
    List<Aggregation> aggregations;
    RecyclerView recyclerView;
    FloatingActionButton btnAddAggregation;
    AggregationAdapter aggregationAdapter;
    String startDate = "";
    String endDate="";
    List<Transaction> trans;
    Button btnAggregate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggregation);

        curSelectedTags = new ArrayList<>();
        aggregations = new ArrayList<>();
        tagChipGroup = findViewById(R.id.tagChipGroup);
        selectedStartDate = findViewById(R.id.startDateLabel);
        selectedEndDate = findViewById(R.id.endDateLabel);
        startDatePickerButton = findViewById(R.id.startDatePickerButton);
        endDatePickerButton = findViewById(R.id.endDatePickerButton);
        recyclerView = findViewById(R.id.aggregatedRecyclerView);
        btnAddAggregation = findViewById(R.id.floatingActionButton);
        btnAggregate = findViewById(R.id.aggregateButton);
        btnAddAggregation.setVisibility(View.GONE);

        Set<String> uniqueTags = new HashSet<>();

        MessageListActivity.transactions.stream()
                .map(transaction -> transaction.getTags())
                .forEach(tagList -> uniqueTags.addAll(tagList));

        List<String> tags = new ArrayList<>(uniqueTags);

        for(String tag:tags){
            Chip chip = new Chip(this);
            chip.setText(tag);
            chip.setCheckable(true);
            tagChipGroup.addView(chip);
        }

        aggregationAdapter = new AggregationAdapter(aggregations);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(aggregationAdapter);
        recyclerView.setLayoutManager(layoutManager);

        aggregationAdapter.setOnClickListener(view -> {
            AggregationAdapter.AggregationViewHolder viewHolder = (AggregationAdapter.AggregationViewHolder) recyclerView.findContainingViewHolder(view);
            Aggregation aggregation = viewHolder.aggregation;
            Intent intent = new Intent(this, AggregationInfoActivity.class);
            String[] lstTags = aggregation.getTags().toArray(new String[0]);
            Transaction[] lstTransactions = aggregation.getTransactions().toArray(new Transaction[0]);
            intent.putExtra("Tags",lstTags);
            intent.putExtra("Transactions",lstTransactions);
            intent.putExtra("TotalAmount",aggregation.getTotal());
            intent.putExtra("DateRage",aggregation.getDateRange());
            startActivity(intent);
        });
    }

    public void onEndDateBtnClicked(View view) {
        showDatePickerDialog(false);
    }

    public void onStartDateBtnClicked(View view) {
        showDatePickerDialog(true);
    }
    private void showDatePickerDialog(boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AggregationActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date="";
                    if(Integer.toString(selectedDay).length()==1||Integer.toString(selectedMonth).length()==1){
                        if(Integer.toString(selectedDay).length()==1&&Integer.toString(selectedMonth).length()==1){
                            date = "0"+selectedDay + "/" + "0"+(selectedMonth + 1) + "/" + selectedYear;
                        }
                        else if(Integer.toString(selectedDay).length()==1){
                            date = "0"+selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        }
                        else{
                            date = selectedDay + "/" + "0"+(selectedMonth + 1) + "/" + selectedYear;
                        }
                    }

                    if (isStartDate) {
                        startDatePickerButton.setText(date);
                        selectedStartDate.setText(date);
                        startDate = date;
                    } else {
                        endDatePickerButton.setText(date);
                        selectedEndDate.setText(date);
                        endDate = date;
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    public void onBtnAggregateClicked(View view) {
        hideView();

        List<Integer> selectedChipIds = tagChipGroup.getCheckedChipIds();
        curSelectedTags.clear();
        for (int id : selectedChipIds) {
            Chip selectedChip = findViewById(id);
            String selectedTag = selectedChip.getText().toString();
            curSelectedTags.add(selectedTag);
        }
        double sum = aggregateTransactions(startDate,endDate,curSelectedTags);
        if(!trans.isEmpty()){
            Aggregation aggregation = new Aggregation("Date Range: "+ selectedStartDate.getText().toString()+" - "+ selectedEndDate.getText().toString(),
                    curSelectedTags,sum,trans);
            aggregationAdapter.add(aggregation);
            Button curView = (Button) view;
            curView.setClickable(false);
        }
        else{
            Toast.makeText(this,"No transaction with this filtering.",Toast.LENGTH_SHORT).show();
        }
    }
    private void hideView() {
        startDatePickerButton.setVisibility(View.GONE);
        endDatePickerButton.setVisibility(View.GONE);
        selectedEndDate.setVisibility(View.GONE);
        selectedStartDate.setVisibility(View.GONE);
        btnAddAggregation.setVisibility(View.VISIBLE);
    }

    private double aggregateTransactions(String startDate, String endDate, List<String> selectedTags) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        trans = MessageListActivity.transactions.stream()
                .filter(transaction -> {
                    Date transactionDate = transaction.getDate();
                    try {
                        Date start = dateFormat.parse(startDate);
                        Date end = dateFormat.parse(endDate);

                        return !transactionDate.before(start) && !transactionDate.after(end)
                                && selectedTags.stream().anyMatch(tag -> transaction.getTags().contains(tag));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                })
                .collect(Collectors.toList());

        return  trans.stream()
                .map(transaction -> transaction.getAmount())
                .reduce(0.0, (a,b)->a+b);
    }

    public void onBtnAddAggregation(View view) {
        startDatePickerButton.setVisibility(View.VISIBLE);
        endDatePickerButton.setVisibility(View.VISIBLE);
        selectedEndDate.setVisibility(View.VISIBLE);
        selectedStartDate.setVisibility(View.VISIBLE);
        view.setVisibility(View.VISIBLE);
        btnAddAggregation.setVisibility(View.INVISIBLE);
        btnAggregate.setClickable(true);
    }
}