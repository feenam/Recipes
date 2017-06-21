package com.example.christian.recipes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AddRecipeNameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe_name);
        final EditText write = (EditText) findViewById(R.id.addNameEditText);
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

    public void onSendHome(View v){
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    public void onContinue(View v){
        Intent i = new Intent(getApplicationContext(), AddIngredientsActivity.class);
        Bundle bundle = new Bundle();
        TextView textView = (TextView) findViewById(R.id.addNameEditText);
        String dishName = textView.getText().toString();
        bundle.putString("dishName", dishName);
        i.putExtras(bundle);
        startActivity(i);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
