package com.example.christian.recipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class ViewStepsActivity extends AppCompatActivity {

    String dishName;
    String steps;
    String ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
//        dishName = bundle.getString("dishName");
//        ingredients = bundle.getString("ingredients");
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_steps);
//        TextView nameTxt = (TextView) findViewById(R.id.steps_title);
//        nameTxt.setText("Add instructions to make " + dishName + " (one per line)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_steps);

        Intent i = getIntent();
        String dishName = i.getExtras().getString("dishName");
        String ingredients = i.getExtras().getString("ingredients");
        String steps = i.getExtras().getString("steps");
        Bitmap image = (Bitmap) i.getExtras().get("image");

        steps = numeralizeSteps(steps);

        TextView nameTxt = (TextView) findViewById(R.id.steps_title);
        TextView ingredientsTxt = (TextView) findViewById(R.id.steps_ingredients_list);
        TextView stepsTxt = (TextView) findViewById(R.id.steps_list);
        ImageView image_slot = (ImageView) findViewById(R.id.steps_dish_image);

        nameTxt.setText(dishName);
        ingredientsTxt.setText(ingredients);
        stepsTxt.setText(steps);
        image_slot.setImageBitmap(image);
    }

    private String numeralizeSteps(String steps) {
        String lines[] = steps.split("\\r?\\n");
        String newString = "";
        int i = 1;
        for(String step : lines){
            newString += "" + Integer.toString(i) + ")  " + step + "\n";
            i++;
        }
        return newString;
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
