package com.example.christian.recipes;

/**
 * Created by daniel on 3/14/17.
 */

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
@DynamoDBTable(tableName = "Recipes")
public class Recipes {
    private String recipe_name;
    private String ingredients;
    private String directions;
    private int image;
    private String steps;

    @DynamoDBAttribute(attributeName = "Directions")
    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }


    @DynamoDBHashKey(attributeName = "RecipeName")
    public String getRecipe_name() {
        return recipe_name;
    }

    public void setRecipe_name(String recipe_name) {
        this.recipe_name = recipe_name;
    }

    @DynamoDBRangeKey(attributeName = "Ingredients")
    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    @DynamoDBAttribute(attributeName = "Image")
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
//
//    @DynamoDBAttribute(attributeName = "Steps")
//    public String getSteps() {
//        return ingredients;
//    }
//
//    public void setSteps(String steps) {
//        this.steps = steps;
//    }
}