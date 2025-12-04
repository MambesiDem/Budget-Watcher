package com.example.budgetwatcher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    public static Boolean extracted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onBtnViewAggregatedClicked(View view) {
        if(extracted){
            Intent intent = new Intent(this,AggregationActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(this,"Extract messages first",Toast.LENGTH_SHORT).show();
        }
    }

    public void onBtnExtractClicked(View view) {
        extracted = true;
        Intent  intent = new Intent(this,MessageListActivity.class);
        startActivity(intent);
    }
}