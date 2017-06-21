package com.example.christian.recipes;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AddIngredientsActivity extends AppCompatActivity {

    String dishName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        dishName = bundle.getString("dishName");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);
        TextView nameTxt = (TextView) findViewById(R.id.add_ingredients_title);
        nameTxt.setText("Add ingredients for " + dishName + " (one per line)");
        final EditText write = (EditText) findViewById(R.id.addIngredientsEditText);
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
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.name_page_back_button);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Snackbar.make(view, "Feature Not Available", Snackbar.LENGTH_LONG)
////                        .setAction("Action", null).show();
//            }
//        });
    }

    public void onContinue(View v){
        Intent i = new Intent(getApplicationContext(), AddStepsActivity.class);
        Bundle bundle = new Bundle();
        TextView textView = (TextView) findViewById(R.id.addIngredientsEditText);
        String ingredients = textView.getText().toString();
//        System.out.println(ingredients);
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
