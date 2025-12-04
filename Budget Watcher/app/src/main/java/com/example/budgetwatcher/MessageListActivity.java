package com.example.budgetwatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MessageListActivity extends AppCompatActivity {
    List<String> extractedMessages;
    private static final String FINANCIAL_MESSAGE_PATTERN = "Absa|WFS";
    public static Map<String, List<String>> tags = new HashMap<>();
    private final String DATE_PATTERN = "\\d{2}/\\d{2}/\\d{2,4}";
    private final String ACCOUNT_PATTERN = "CHEQ\\d{4}|CCRD\\d{4}";
    private final String TYPE_PATTERN = "(Pur|Wthdr|Dep|Pmnt|Sch\\s*t|Transf)";
    private final String AMOUNT_PATTERN = "R(-?\\d{1,3}(?:,\\d{3})*(?:\\.\\d{2})?)";
    private final String DESCRIPTION_PATTERN = "(?<=\\d{2}/\\d{2}/\\d{2}|(%s)\\s)(.*?)(?=\\sR[-,[0-9]])";
    private final String BALANCE_PATTERN = "(Available|Total\\s+Avail\\s+Bal)\\s*R(\\d{1,3}(?:,\\d{3})*(?:\\.\\d{2})?)";
    public static List<Transaction> transactions = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    RecyclerView recyclerView;
    TransactionAdapter adapter;
    private static final int SMS_PERMISSION_CODE = 2;
    String chosen = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        addPossiblePossibleTags();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
        } else {
            extractedMessages = getAllSmsMessages();
            if(extractedMessages.isEmpty())Toast.makeText(this,"There are not messages to extract",Toast.LENGTH_SHORT);
            List<String> financialMessages = filterFinancialMessages(extractedMessages);
            transactionInfo(financialMessages);
            displayTransactions();
        }

    }
    public void displayTransactions(){
        recyclerView = findViewById(R.id.messageRecyclerView);
        adapter = new TransactionAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }
    private List<String> getAllSmsMessages() {

        List<String> smsMessages = new ArrayList<>();
        Uri inboxUri = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver().query(inboxUri, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String message = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                smsMessages.add(message);
            } while (cursor.moveToNext());

            cursor.close();
        }
        return smsMessages;
    }
    private List<String> filterFinancialMessages(List<String> messages) {
        String regex = ".*(" + FINANCIAL_MESSAGE_PATTERN + ").*";
        return messages.stream()
                .filter(msg -> msg.matches(regex))
                .collect(Collectors.toList());
    }
    private List<String> applyTags(String description) {
        List<String> appliedTags = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : tags.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (description.toLowerCase().contains(keyword.toLowerCase())) {
                    appliedTags.add(entry.getKey());
                    break;
                }
            }
        }

        return appliedTags;
    }
    private void addPossiblePossibleTags(){
        tags.put("Food", Arrays.asList("restaurant", "grocery", "expense", "kfc", "mcdonalds"));
        tags.put("Withdrawal", Arrays.asList("withdrawal", "cash withdrawal", "ATM", "banking"));
        tags.put("Airtime", Arrays.asList("expense", "airtime", "telecom", "MTN"));
        tags.put("Deposit", Arrays.asList("income", "salary", "work", "deposit"));
        tags.put("Expense", Arrays.asList("purchase", "coffee", "rent", "rental","grocery","bill","airtime",
                "cinema", "movie","payment", "theater", "netflix", "spotify","checkers",
                "music","uber", "taxi", "bus", "transport", "fuel", "petrol","clothing","kfc","woolworths"));
        tags.put("Income", Arrays.asList("salary", "deposit","refund"));
        tags.put("Rental", Arrays.asList("rent", "accommodation"));
        tags.put("Purchase", Arrays.asList("coffee", "restaurant", "entertainment", "food","clothing","grocery","woolworths"));
        tags.put("Shopping", Arrays.asList("amazon", "clothing", "fashion", "shop","checkers"));
        tags.put("Insurance", Arrays.asList("insurance"));
        tags.put("Transfer", Arrays.asList("transfer"));
        tags.put("Savings", Arrays.asList("transfer","savings"));
    }
    private Map<String,String> extractTransactionInfo(String message) {
        Map<String,String> transaction = new HashMap<>();
        Matcher accountMatcher = Pattern.compile(ACCOUNT_PATTERN).matcher(message);
        if (accountMatcher.find()) {
            transaction.put("account",accountMatcher.group());
        }

        Matcher typeMatcher = Pattern.compile(TYPE_PATTERN).matcher(message);
        if (typeMatcher.find()) {
            transaction.put("type",typeMatcher.group());
        }

        Matcher dateMatcher = Pattern.compile(DATE_PATTERN).matcher(message);
        if (dateMatcher.find()) {
            transaction.put("date",dateMatcher.group());
        }

        Matcher amountMatcher = Pattern.compile(AMOUNT_PATTERN).matcher(message);
        if (amountMatcher.find()) {
            transaction.put("amount",amountMatcher.group());
        }

        Matcher descriptionMatcher = Pattern.compile(DESCRIPTION_PATTERN).matcher(message);
        if (descriptionMatcher.find()) {
            transaction.put("description",descriptionMatcher.group());
        }
        Matcher balanceMatcher = Pattern.compile(BALANCE_PATTERN).matcher(message);
        if (balanceMatcher.find()) {
            transaction.put("balance",balanceMatcher.group());
        }

        return transaction;
    }
    public void transactionInfo(List<String> financialMessages){
        for (String message:financialMessages){

            Map<String,String> transactionInfo = extractTransactionInfo(message);
            String account="",type="",date="",description="",amount="",balance="";
            Date date1=null;
            for(Map.Entry<String, String> entry:transactionInfo.entrySet()){
                if(entry.getKey().equals("account")){
                    account = entry.getValue();
                    Log.i("Account",account);
                }
                else if(entry.getKey().equals("type")){
                    type = entry.getValue();
                    Log.i("Type",type);
                }
                else if(entry.getKey().equals("date")){
                    date = entry.getValue();
                    Log.i("Date",date);
                    try {
                        date1 = dateFormat.parse(date);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                else if(entry.getKey().equals("description")){
                    description = entry.getValue();
                    Log.i("Description",description);
                }
                else if(entry.getKey().equals("amount")){
                    amount = entry.getValue().replace("R","").replace(",", "").trim();
                    Log.i("Amount",amount);
                }
                else if(entry.getKey().equals("balance")){
                    Log.i("Did it get in","Yes");
                    balance = entry.getValue().replace("Available", "")
                            .replace("Total","")
                            .replace("Avail","")
                            .replace("Bal","")
                            .replace("R", "")
                            .replace(",", "")
                            .trim();
                    Log.i("Balance",balance);
                }
            }
            List<String> transAppliedTags = applyTags(description);
            Transaction transaction = new Transaction(account,type,date1,description,
                    Double.parseDouble(amount),Double.parseDouble(balance),transAppliedTags);
            transactions.add(transaction);
        }
    }

    public void onBackBtnClicked(View view) {
        finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                extractedMessages = getAllSmsMessages();
                List<String> financialMessages = filterFinancialMessages(extractedMessages);
                transactionInfo(financialMessages);
                displayTransactions();
            } else {
                Toast.makeText(this, "SMS permission is required to read messages", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onBtnFilterClicked(View view) {
        showInputDialog();
    }
    public void showInputDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_filter, null);

        EditText edtType = dialogView.findViewById(R.id.edtType);
        edtType.setVisibility(View.INVISIBLE);
        Spinner filterSpinner = dialogView.findViewById(R.id.spinnerFilters);

        List<String> filter = Arrays.asList("Choose filter","Type","Description","Tag");
        ArrayAdapter<String> adapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                android.R.id.text1,
                filter);
        filterSpinner.setAdapter(adapter1);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                chosen = adapterView.getItemAtPosition(position).toString();
                edtType.setVisibility(View.VISIBLE);
                edtType.setHint("Enter the "+chosen);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(adapterView.getContext(),"Select the number of players.",Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Input")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    if(chosen.equals("Type")){
                        List<Transaction> filtered = transactions.stream()
                                .filter(transaction -> transaction.getType().equals(edtType.getText().toString()))
                                .collect(Collectors.toList());
                        if(!filtered.isEmpty())adapter.updateList(filtered);
                        else Toast.makeText(this,"Invalid Type entered",Toast.LENGTH_SHORT).show();
                    }else if(chosen.equals("Description")) {
                        List<Transaction> filtered = transactions.stream()
                                .filter(transaction -> transaction.getDescription().contains(edtType.getText().toString()))
                                .collect(Collectors.toList());
                        if(!filtered.isEmpty())adapter.updateList(filtered);
                        else Toast.makeText(this,"Invalid Description entered",Toast.LENGTH_SHORT).show();
                    }
                    else if(chosen.equals("Tag")){
                        List<Transaction> filtered = transactions.stream()
                                .filter(transaction -> transaction.getTags().stream()
                                        .anyMatch(tag -> tag.equals(edtType.getText().toString())))
                                .collect(Collectors.toList());
                        if(!filtered.isEmpty())adapter.updateList(filtered);
                        else Toast.makeText(this,"Invalid Tag entered",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        adapter.updateList(transactions);
                    }
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