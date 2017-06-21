package com.example.christian.recipes;
import android.app.Activity;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;


/**
 * Created by Christian on Mar/13/17.
 */
public class IngredientsPopUp extends Activity {

    TextView dishName;
    ImageView dishImage;
    TextView ingredients;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    //        View rootView = inflate(getApplicationContext(), R.layout.ingredients_window, this);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.ingredients_window);
        Intent i = getIntent();
        String name = i.getExtras().getString("dishName");
        String a_ingredients = i.getExtras().getString("ingredients");
        Bitmap image = (Bitmap) i.getExtras().get("image");
        dishName = (TextView) findViewById(R.id.ingredients_dish_name);
        dishName.setText(name);
        dishImage = (ImageView) findViewById(R.id.ingredients_dish_image);
        dishImage.setImageBitmap(image);
        ingredients = (TextView) findViewById(R.id.ingredients_list);
        ingredients.setText(a_ingredients);

        AlertDialog.Builder dm = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.ingredients_window, null);
        dm.setView(mView);

//        _screen = (TextView) findViewById(R.id.textView);
//        _screen.setText(display);



    }
}

