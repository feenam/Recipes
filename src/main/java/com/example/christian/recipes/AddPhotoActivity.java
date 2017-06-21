package com.example.christian.recipes;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;

public class AddPhotoActivity extends AppCompatActivity {

    String dishName;
    String ingredients;
    String steps;
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        dishName = bundle.getString("dishName");
        ingredients = bundle.getString("ingredients");
        steps = bundle.getString("steps");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo);
        final TextView nameTxt = (TextView) findViewById(R.id.add_photo_title);
        nameTxt.setText("Add a photo for " + dishName);
        final ImageView image = (ImageView) findViewById(R.id.photo_button);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == image.getId()){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

                }
            }
        });
    }

    public void onContinue(View v){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        ImageView photo = (ImageView) findViewById(R.id.photo_button);
        // I don't know which method it should be.
//        String image = photo.getPhoto()
// ;
        bundle.putString("steps", steps);
        bundle.putString("ingredients", ingredients);
        bundle.putString("dishName", dishName);

        // Again, I don't know
//        bundle.putPhoto("image", image);

        final Recipes newRecipe = new Recipes();
        newRecipe.setDirections(steps);
        newRecipe.setIngredients(ingredients);
        newRecipe.setRecipe_name(dishName);
        newRecipe.setImage(1);

        // Should be this.
//        newRecipe.setImage(image);

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
                mapper.save(newRecipe);
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                final Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                file = new File(selectedImagePath);
                Runnable runnable = new Runnable() {
                    public void run() {
                        //DynamoDB calls go here
                        // Initialize the Amazon Cognito credentials provider
                        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                                getApplicationContext(),
                                "us-east-1:8bf45a37-2fc1-40d5-96c3-35e601e5d3b7", // Identity Pool ID
                                Regions.US_EAST_1 // Region
                        );

                        AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
//                        s3.createBucket("160grouppp");
                        PutObjectRequest por = new PutObjectRequest("160group22", dishName, file);
                        s3.putObject(por);
                    }

                };
                Thread mythread = new Thread(runnable);
                mythread.start();
                try {
                    mythread.join();
                } catch (Exception e) {
                }
            }
        }
        final TextView nameTxt = (TextView) findViewById(R.id.addPhotoText);
        final ImageView image = (ImageView) findViewById(R.id.photo_button);
        image.setImageResource(R.drawable.check_mark);
        nameTxt.setText("Photo uploaded");
    }
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }
}
