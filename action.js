'use strict';

const doc = require('dynamodb-doc');
const dynamo = new doc.DynamoDB();

exports.handler = function(event, context) {
    try {
        console.log("event.session.application.applicationId=" + event.session.application.applicationId);
        if (event.session.application.applicationId !== "amzn1.ask.skill.c93b5e71-bd8f-477a-acd3-6a0027e99351") {
            context.fail("Invalid Application ID");
        }

        var processRequest = function(event, context, sessionAttributes) {
            event.session.attributes = sessionAttributes;
            //console.log("event.session.attributes: " + JSON.stringify(event.session.attributes));
            if (event.request.type === "LaunchRequest") {
                console.log("onLaunch requestId=" + event.request.requestId + ", sessionId=" + event.session.sessionId);
                context.succeed(buildResponse(event.session.attributes,
                    buildSpeechletResponse(
                        "Recipe assistant, what recipe would you like to make?",
                        "Recipe assistant, what recipe would you like to make?",
                        false)));
            } else if (event.request.type === "IntentRequest") {
                onIntent(event.request,
                    event.session,
                    function callback(sessionAttributes, speechletResponse) {
                        context.succeed(buildResponse(sessionAttributes, speechletResponse));
                    });
            } else if (event.request.type === "SessionEndedRequest") {
                console.log("onSessionEnded requestId=" + event.request.requestId + ", sessionId=" + event.session.sessionId);
                context.succeed();
            }
        }
        if (event.session.new) {
            onSessionStarted({requestId: event.request.requestId}, event.session,
                function callback(sessionAttributes) {
                    processRequest(event, context, sessionAttributes);
                });
        } else {
            processRequest(event, context, event.session.attributes);
        }
    } catch (e) {
        context.fail("Exception: " + e);
    }
};

function onSessionStarted(sessionStartedRequest, session, callback) {
    console.log("onSessionStarted requestId=" + sessionStartedRequest.requestId
        + ", sessionId=" + session.sessionId);

    session.attributes = {
        speechOutput: "Recipe assistant, what recipe would you like to make?",
        repromptText: "Recipe assistant, what recipe would you like to make?",
        location: "main menu",
        recipes: "WHY IS THIS SHOWING UP"
    };

    console.log("Starting dynamo scan");
    dynamo.scan({ TableName: "Recipes" }, function(err, res) {
        console.log("Finished dynamo scan.")
        session.attributes.recipes = err ? err.message : res;
        callback(session.attributes);
    });
}

function onIntent(intentRequest, session, callback) {
    console.log("onIntent requestId=" + intentRequest.requestId
        + ", sessionId=" + session.sessionId);

    var intent = intentRequest.intent,
        intentName = intentRequest.intent.name;

    // dispatch custom intents to handlers here
    var speech = "";
    var reprompt = "Sorry, I couldn't understand you.";
    var location = session.attributes ? session.attributes.location : "main menu";
    var shouldEndSession = false;
    var food = intentName === "FindIntent" ? intent.slots.food.value : null;
    var foodSelected = session.attributes ? session.attributes.foodSelected : null;
    var currentIndex = session.attributes ? session.attributes.currentIndex : null;
    
    var checkFood = function() {
        // search for food in the recipes and update state
        var found = false;
        for (var i = 0; i < session.attributes.recipes.Count; i++) {
            var recipeName = session.attributes.recipes.Items[i].RecipeName;
            console.log("Comparing: " + recipeName.toLowerCase() + ", " + food.toLowerCase());
            if (recipeName.toLowerCase() === food.toLowerCase()) {
                found = true;
                break;
            }
        }

        if (found) {
            speech = "Ok, I'll give you information about " + food + ". Do you want to know the ingredients or the recipe?";
            location = "selected food";
            foodSelected = session.attributes.recipes.Items[i];
            foodSelected.Ingredients = foodSelected.Ingredients.split('\n');
            foodSelected.NumIngredients = foodSelected.Ingredients.length;
            foodSelected.Steps = foodSelected.Directions.split('\n');
            foodSelected.NumSteps = foodSelected.Steps.length;
            delete foodSelected.Directions;
        } else {
            speech = "I couldn't find a recipe for " + food + ". Please try another item.";
        }
    };
    if (location === "main menu") {
        /*
        FindIntent - food
        WhatCanISayIntent
        AMAZON.StopIntent
        IngredientsIntent
        AMAZON.NextIntent
        AMAZON.PreviousIntent
        AMAZON.StartOverIntent
        MainMenuIntent
        StartRecipeIntent
        */

        if (intentName === "FindIntent") {
            checkFood();
        } else if (intentName === "WhatCanISayIntent") {
            speech = "You can ask me to find a recipe, or ask me how to make one.";
        } else if (intentName === "MainMenuIntent") {
            speech = "You're already in the main menu.";
        } else if (intentName === "AMAZON.StopIntent") {
            speech = "Thanks for using the recipe assistant. Goodbye!";
            shouldEndSession = true;
        } else if (intentName === "IngredientsIntent") {
            speech = "Pick a recipe first!";
        } else if (intentName === "AMAZON.NextIntent") {
            speech = "Pick a recipe first!";
        } else if (intentName === "AMAZON.PreviousIntent") {
            speech = "Pick a recipe first!";
        } else if (intentName === "AMAZON.StartOverIntent") {
            speech = "Pick a recipe first!";
        } else if (intentName === "StartRecipeIntent") {
            speech = "Pick a recipe first!";
        } else {
            shouldEndSession = true;
        }
    } else if (location === "selected food") {
        if (intentName === "FindIntent") {
            checkFood();
        } else if (intentName === "WhatCanISayIntent") {
            speech = "You can ask about the ingredients or the recipe, or go back to the main menu.";
        } else if (intentName === "MainMenuIntent" || intentName === "AMAZON.StopIntent" || intentName === "AMAZON.StartOverIntent") {
            speech = "Ok, I'm back at the main menu.";
            location = "main menu";
        } else if (intentName === "IngredientsIntent") {
            speech = "Ok, starting ingredients. Here's the first one: " + foodSelected.Ingredients[0];
            location = "ingredients";
            currentIndex = 0;
        } else if (intentName === "AMAZON.NextIntent") {
            speech = "Do you want to hear the ingredients or the recipe?";
        } else if (intentName === "AMAZON.PreviousIntent") {
            speech = "Do you want to hear the ingredients or the recipe?";
        } else if (intentName === "StartRecipeIntent") {
            speech = "Ok, starting the recipe. Here's the first step: " + foodSelected.Steps[0];
            location = "directions";
            currentIndex = 0;
        } else {
            shouldEndSession = true;
        }
    } else if (location === "ingredients") {
        if (intentName === "FindIntent") {
            checkFood();
        } else if (intentName === "WhatCanISayIntent") {
            speech = "You can ask me for the previous or next ingredient, or go back to the main menu.";
        } else if (intentName === "MainMenuIntent" || intentName === "AMAZON.StopIntent") {
            speech = "Ok, I'm back at the main menu.";
            location = "main menu";
        } else if (intentName === "IngredientsIntent") {
            speech = "The current ingredient is " + foodSelected.Ingredients[currentIndex];
        } else if (intentName === "AMAZON.NextIntent") {
            currentIndex++;
            if (currentIndex < foodSelected.NumIngredients) {
                speech = "The next ingredient is " + foodSelected.Ingredients[currentIndex];
            } else {
                speech = "That's the end of the ingredients. Moving onto the recipe now. Here's the first step: " + foodSelected.Steps[0];
                currentIndex = 0;
                location = "directions";
            }
        } else if (intentName === "AMAZON.PreviousIntent") {
            currentIndex--;
            if (currentIndex < 0) {
                currentIndex = 0;
                speech = "You're at the start already. Here's the first ingredient again: " + foodSelected.Ingredients[0];
            } else {
                speech = "The previous ingredient was " + foodSelected.Ingredients[currentIndex];
            }
        } else if (intentName === "AMAZON.StartOverIntent") {
            currentIndex = 0;
            speech = "I'll restart at the first ingredient, which is " + foodSelected.Ingredients[0];
        } else if (intentName === "StartRecipeIntent") {
            speech = "Ok, starting the recipe. Here's the first step: " + foodSelected.Steps[0];
            currentIndex = 0;
            location = "directions";
        } else {
            shouldEndSession = true;
        }
    } else if (location === "directions") {
        if (intentName === "FindIntent") {
            checkFood();
        } else if (intentName === "WhatCanISayIntent") {
            speech = "You can ask me for the previous or next step, or go back to the main menu.";
        } else if (intentName === "MainMenuIntent" || intentName === "AMAZON.StopIntent") {
            speech = "Ok, I'm back at the main menu.";
            location = "main menu";
        } else if (intentName === "IngredientsIntent") {
            speech = "Ok, starting ingredients. Here's the first one: " + foodSelected.Ingredients[0];
            location = "ingredients";
            currentIndex = 0;
        } else if (intentName === "AMAZON.NextIntent") {
            currentIndex++;
            if (currentIndex < foodSelected.NumSteps) {
                speech = "The next step is " + foodSelected.Steps[currentIndex];
            } else {
                speech = "That's the end of the recipe. What do you want to hear now?";
                location = "selected food";
            }
        } else if (intentName === "AMAZON.PreviousIntent") {
            currentIndex--;
            if (currentIndex < 0) {
                currentIndex = 0;
                speech = "You're at the start already. Here's the first step again: " + foodSelected.Steps[0];
            } else {
                speech = "The previous step was " + foodSelected.Steps[currentIndex];
            }
        } else if (intentName === "AMAZON.StartOverIntent") {
            currentIndex = 0;
            speech = "I'll restart at the first step, which is " + foodSelected.Steps[0];
        } else if (intentName === "StartRecipeIntent") {
            speech = "The current step is " + foodSelected.Steps[currentIndex];
        } else {
            shouldEndSession = true;
        }
    }
    
    session.attributes = {
        speechOutput: speech,
        repromptText: reprompt,
        location: location,
        recipes: session.attributes.recipes,
        foodSelected: foodSelected,
        currentIndex: currentIndex
    };
    callback(session.attributes,
        buildSpeechletResponse(speech, reprompt, shouldEndSession));
}

// ------- Helper functions to build responses -------
function buildSpeechletResponse(output, repromptText, shouldEndSession) {
    return {
        outputSpeech: {
            type: "PlainText",
            text: output
        },
        reprompt: {
            outputSpeech: {
                type: "PlainText",
                text: repromptText
            }
        },
        shouldEndSession: shouldEndSession
    };
}
function buildResponse(sessionAttributes, speechletResponse) {
    return {
        version: "1.0",
        sessionAttributes: sessionAttributes,
        response: speechletResponse
    };
}

