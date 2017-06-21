package com.example.christian.recipes;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import com.example.christian.recipes.foodSlot;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.GridLayout.Spec;
import android.view.LayoutInflater;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.amazonaws.services.dynamodbv2.model.*;
import android.graphics.Bitmap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import com.amazonaws.util.IOUtils;
import java.nio.charset.StandardCharsets;




public class MainActivity extends AppCompatActivity {

    PaginatedScanList<Recipes> _listOfRecipes;
    Integer foodSlotCounter;
    ArrayList<Bitmap> bmpList = new ArrayList<Bitmap>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Feature Not Available", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent i = new Intent(MainActivity.this, AddRecipeNameActivity.class);
                MainActivity.this.startActivity(i);
            }
        });
        ImageButton settings = (ImageButton) findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Feature Not Available", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        final EditText search = (EditText) findViewById(R.id.searchEditText);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == search.getId()) {
                    search.setCursorVisible(true);
                }
            }
        });
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        Runnable runnable = new Runnable() {
            public void run() {
                //DynamoDB calls go here
                // Initialize the Amazon Cognito credentials provider
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "us-east-1:8bf45a37-2fc1-40d5-96c3-35e601e5d3b7", // Identity Pool ID
                        Regions.US_EAST_1 // Region
                );
                AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
                DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);
                DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
                _listOfRecipes = mapper.scan(Recipes.class, scanExpression);
                _listOfRecipes.size();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
        try{
            mythread.join();
        }catch (Exception e){

        }
        try {
            createObjects();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createObjects() throws IOException {
        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {

                for(Recipes recipe : _listOfRecipes) {
                    URL url = null;
                    try {
                        url = new URL("https://s3.amazonaws.com/160group22/"+ recipe.getRecipe_name());
//                    url = new URL("https://s3.amazonaws.com/160group22/key4");
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    try {
                        bmpList.add(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        };
        Thread mythread = new Thread(runnable2);
        mythread.start();
        try{
            mythread.join();
        }catch (Exception e){

        }
        foodSlotCounter = 0;
        GridLayout layout = (GridLayout) findViewById(R.id.dashboard);
        for(int i = 0; i < _listOfRecipes.size(); i++){
            Recipes recipe = _listOfRecipes.get(i);
//            System.out.println(recipe.getRecipe_name());
            // This code adds a new recipe object
            foodSlot titleView = new foodSlot(MainActivity.this, recipe, scaleDownBitmap(bmpList.get(i), 100, getApplicationContext()));
            titleView.setId(R.id.foodslot);
            titleView.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            layout.addView(titleView);
            foodSlotCounter++;
        }
        for(int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            TextView name = (TextView) v.findViewById(R.id.dish_name_box);
            ImageView image = (ImageView) v.findViewById(R.id.dish_image);
            image.setImageBitmap(bmpList.get(i));
            name.setText(_listOfRecipes.get(i).getRecipe_name());
        }
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
