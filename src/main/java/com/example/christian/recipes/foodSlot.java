package com.example.christian.recipes;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


/**
 * TODO: document your custom view class.
 */

public class foodSlot extends LinearLayout {

    View rootView;
//    TextView valueTextView;
    View steps_fab;
    View ingredients_fab;
    View dish_name;
    View dish_image;

    public foodSlot(Context context) {
        super(context);
        init(context, null, null);
    }

    public foodSlot(Context context, Recipes recipe, Bitmap image) {
        super(context);
        init(context, recipe, image);
    }

    public foodSlot(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, null, null);
    }
    public foodSlot(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, null, null);
    }

    private void init(final Context context, final Recipes recipe, final Bitmap image) {
        rootView = inflate(context, R.layout.sample_food_slot, this);
        steps_fab = rootView.findViewById(R.id.steps_fab);
        ingredients_fab = rootView.findViewById(R.id.ingredients_fab);
        dish_name = rootView.findViewById(R.id.dish_name_box);
        dish_image = rootView.findViewById(R.id.dish_image);

        steps_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = recipe.getRecipe_name();
                String ingredients = recipe.getIngredients();
                String steps = recipe.getDirections();
                Intent i = new Intent(context, ViewStepsActivity.class);
                i.putExtra("ingredients", ingredients);
                i.putExtra("dishName", name);
                i.putExtra("steps", steps);
                i.putExtra("image", image);
                context.startActivity(i);
                //we'll define this method later
            }
        });
        ingredients_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = recipe.getRecipe_name();
                String ingredients = recipe.getIngredients();
                Intent i = new Intent(context,IngredientsPopUp.class);
                i.putExtra("ingredients", ingredients);
                i.putExtra("dishName", name);
                i.putExtra("image", image);
                context.startActivity(i);
                //we'll define this method later
            }

        });

        dish_image.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            }
        });
    }


}


