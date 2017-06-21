package com.example.christian.recipes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class AddStepsActivity extends AppCompatActivity {

    String dishName;
    String ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        dishName = bundle.getString("dishName");
        ingredients = bundle.getString("ingredients");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_steps);
        TextView nameTxt = (TextView) findViewById(R.id.steps_title);
        nameTxt.setText("Add instructions to make " + dishName + " (one per line)");
        final EditText write = (EditText) findViewById(R.id.addStepsEditText);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == write.getId()){
                    write.setCursorVisible(true);
                }
            }
        });
        write.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void onContinue(View v){
        Intent i = new Intent(getApplicationContext(), AddPhotoActivity.class);
        Bundle bundle = new Bundle();
        TextView textView = (TextView) findViewById(R.id.addStepsEditText);
        String steps = textView.getText().toString();
//        System.out.println(ingredients);
        bundle.putString("steps", steps);
        bundle.putString("ingredients", ingredients);
        bundle.putString("dishName", dishName);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void OnFinish(View v) {
        super.finish();
    }

    public void onSendHome(View v){
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
