package com.example.smsgenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.SurfaceControl;
import android.view.View;
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
import java.util.Random;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    EditText startDate;
    EditText endDate;
    EditText numMessages;
    Spinner emulatorSpinner;
    EditText manualMessage;
    private static final int SMS_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        numMessages = findViewById(R.id.numMessages);
        emulatorSpinner = findViewById(R.id.emulatorSpinner);
        manualMessage = findViewById(R.id.manualMessage);

        List<String> emulators = Arrays.asList("Choose an emulator","5554","5556","5558");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1,
                emulators);

        emulatorSpinner.setAdapter(adapter);
    }

    public void onBtnSendBatchClicked(View view) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }
        else{
            String start = startDate.getText().toString();
            String end = endDate.getText().toString();
            int numberOfMessages = Integer.parseInt(numMessages.getText().toString());
            String emulatorNumber = emulatorSpinner.getSelectedItem().toString();
            generateAndSendMessages(start, end, numberOfMessages, emulatorNumber);
        }
    }

    public void onBtnSendManualClicked(View view) {
        String message = manualMessage.getText().toString();
        String emulatorNumber = emulatorSpinner.getSelectedItem().toString();
        sendSMSUsingIntent(emulatorNumber, message);
    }
    private void generateAndSendMessages(String start, String end, int count, String emulatorNumber) {
        Random random = new Random();

        List<String> financialTemplates = Arrays.asList(
                "Absa: CHEQ5678, Pur, %s SETTLEMENT/C - GROCERY DEBIT, Pick n Pay: 123456, R-320.50, Available R2,750.00. Help 0860008600; ACCID 001",
                "Absa: CHEQ5678, Wthdr, %s ATM WITHDRAWAL - SANDTON CITY, R-1,200.00, Available R5,600.00. Help 0860008600; ACCID 001",
                "Absa: CHEQ5678, Dep, %s SETTLEMENT/C - REFUND FROM INSURANCE, R3,000.00, Available R12,000.00. Help 0860008600; ACCID 001",
                "Absa: CHEQ5678, Sch t, %s SETTLEMENT/C - ACB DEBIT:UTILITY BILL, CITY POWER, R-950.00, Available R3,500.00. Help 0860008600; ACCID 001",
                "WFS: CCRD1013, Pur, %s AUTHORIZATION, Woolworths Sandton, R560.00, Total Avail Bal R6,000.00. Help 0861502005; CCID 004",
                "WFS: CCRD1013, Transf. %s INTERNAL FUNDS TRANSFER, ***1018, R1,500.00, Total Avail Bal R5,500.00. Help 0861502005; CCID 004",
                "Absa: CHEQ7890, Pmnt, %s SETTLEMENT/C - RENTAL PAYMENT, LEASE AGREEMENT, R-8,000.00, Available R15,500.00. Help 0860008600; ACCID 001",
                "Absa: CHEQ7890, Pur, %s SETTLEMENT/C - CLOTHING DEBIT, H&M Mall of Africa: 987654, R-1,200.00, Available R6,300.00. Help 0860008600; ACCID 001",
                "Absa: CHEQ7890, Dep, %s SALARY PAYMENT, COMPANY ABC, R18,000.00, Available R24,500.00. Help 0860008600; ACCID 001",
                "WFS: CCRD1000, Pur, %s AUTHORIZATION, KFC Greenacres, R125.00, Total Avail Bal R2,700.00. Help 0861502005; CCID 004",
                "Absa: CHEQ5678, Pur, %s SETTLEMENT/C - FUEL EXPENSE, SHELL: 654321, R-450.00, Available R9,500.00. Help 0860008600; ACCID 001",
                "Absa: CHEQ5678, Dep, %s SETTLEMENT/C - DIVIDEND PAYMENT, INVESTMENT ABC, R1,200.00, Available R14,200.00. Help 0860008600; ACCID 001",
                "Absa: CHEQ5678, Wthdr, %s ATM WITHDRAWAL - ROSEBANK MALL, R-2,000.00, Available R8,300.00. Help 0860008600; ACCID 001",
                "Absa: CHEQ7890, Pur, %s SETTLEMENT/C - ELECTRONICS PURCHASE, GAME STORES, R-2,999.99, Available R11,500.00. Help 0860008600; ACCID 001",
                "WFS: CCRD1013, Pur, %s AUTHORIZATION, Checkers Hyper Sandton, R650.00, Total Avail Bal R5,800.00. Help 0861502005; CCID 004",
                "WFS: CCRD1013, Transf. %s EXTERNAL FUNDS TRANSFER, ***2019, R5,000.00, Total Avail Bal R12,000.00. Help 0861502005; CCID 004",
                "Absa: CHEQ7890, Pmnt, %s SETTLEMENT/C - CAR PAYMENT, FINANCIAL SERVICES ABC, R-6,500.00, Available R18,000.00. Help 0860008600; ACCID 001",
                "Absa: CHEQ7890, Dep, %s BUSINESS INCOME, CLIENT XYZ, R12,500.00, Available R21,000.00. Help 0860008600; ACCID 001",
                "WFS: CCRD1000, Pur, %s AUTHORIZATION, Spar Market Walmer, R350.00, Total Avail Bal R3,200.00. Help 0861502005; CCID 004",
                "WFS: CCRD1000, Transf. %s INTERNAL FUNDS TRANSFER, ***3030, R1,200.00, Total Avail Bal R4,800.00. Help 0861502005; CCID 004"
        );

        List<String> generalMessages = Arrays.asList(
                "You've won 1,000,000 USD!!!",
                "Exclusive offer! Buy one get one free at your local store!"
        );
        List<String> randomDates = generateRandomDatesWithinRange(start, end, count);
        List<String> randomMessages = randomDates.stream()
                .map(date -> random.nextBoolean() ?
                        String.format(financialTemplates.get(random.nextInt(financialTemplates.size())), date) :
                        generalMessages.get(random.nextInt(generalMessages.size())))
                .collect(Collectors.toList());

        randomMessages.forEach(message -> sendSMS(emulatorNumber, message));
    }
    private void sendSMS(String emulatorNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(emulatorNumber, null, message, null, null);
    }
    private void sendSMSUsingIntent(String emulatorNumber, String message) {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("sms:" + emulatorNumber));
        smsIntent.putExtra("sms_body", message);

        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        }
    }
    private List<String> generateRandomDatesWithinRange(String start, String end, int count) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        List<String> randomDates = new ArrayList<>();

        try {
            Date startDate = dateFormat.parse(start);
            Date endDate = dateFormat.parse(end);

            long startMillis = startDate.getTime();
            long endMillis = endDate.getTime();

            Random random = new Random();

            for (int i = 0; i < count; i++) {
                long randomMillis = startMillis + (long) (random.nextDouble() * (endMillis - startMillis));
                Date randomDate = new Date(randomMillis);
                randomDates.add(dateFormat.format(randomDate));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return randomDates;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String start = startDate.getText().toString();
                String end = endDate.getText().toString();
                int numberOfMessages = Integer.parseInt(numMessages.getText().toString());
                String emulatorNumber = emulatorSpinner.getSelectedItem().toString();
                generateAndSendMessages(start, end, numberOfMessages, emulatorNumber);
            } else {
                Toast.makeText(this, "SMS permission is required to send messages", Toast.LENGTH_SHORT).show();
            }
        }
    }
}