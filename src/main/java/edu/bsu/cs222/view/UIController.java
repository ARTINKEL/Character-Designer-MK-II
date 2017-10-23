package edu.bsu.cs222.view;

import edu.bsu.cs222.model.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jdk.internal.util.xml.impl.Input;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UIController extends Application {

    private final int WIDTH = 500;
    private final int HEIGHT = 300;

    private InputMap inputMap = new InputMap();
    private ContentCreator contentCreator = new ContentCreator();

    private Questions questions = new Questions();
    private ErrorHandler errorHandler = new ErrorHandler();
    private TextField inputTextField = new TextField();

    private int currentQuestion = 1;

    private Label questionLabel = new Label(questions.getQuestion(currentQuestion));
    private Label errorLabel = new Label();
    private Label resultLabel = new Label();

    private String raceResult = "";
    private String classResult = "";

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Sentiment Analyzer v0.1");

        final VBox mainBox = new VBox();

        mainBox.getChildren().add(questionLabel);
        mainBox.getChildren().add(inputTextField);
        mainBox.getChildren().add(errorLabel);

        final Button backButton = new Button("Back");
        final Button clearButton = new Button("Clear");
        final Button nextButton = new Button("Next");
        backButton.setVisible(false);

        final HBox secBox = new HBox();

        secBox.getChildren().addAll(backButton, clearButton, nextButton);
        mainBox.getChildren().add(secBox);
        mainBox.getChildren().add(resultLabel);

        nextButton.setOnAction(event -> {
            if (inputTextField.getText().isEmpty()) {
                errorLabel.setText(errorHandler.returnError("blankField"));
                return;
            } else {
                errorLabel.setText(null);
                recordResponse();
                if (currentQuestion != 10) {
                    incrementQuestion();
                    if (currentQuestion == 10) {
                        nextButton.setText("Submit");
                    }
                } else {
                    try {
                        Mapper mapper = new Mapper(inputMap);
                        raceResult = mapper.calculateResult("race");
                        classResult = mapper.calculateResult("class");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    displayResult();
                }
            }
            backButton.setVisible(true);
        });

        backButton.setOnAction(event -> {
            decrementQuestion();
            populateTextField();
            removeResponse();
            if (currentQuestion == 1) {
                backButton.setVisible(false);
            }
        });

        clearButton.setOnAction(event -> inputTextField.setText(""));

        Scene scene = new Scene(mainBox, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void recordResponse() {
        Response response = contentCreator.createResponse(inputTextField.getText());
        inputMap.addInput(currentQuestion, response);
        inputTextField.setText("");
    }

    private void incrementQuestion() {
        currentQuestion++;
        populateQuestion();
    }

    private void decrementQuestion() {
        currentQuestion--;
        populateQuestion();
    }

    private void populateQuestion() {
        questionLabel.setText(questions.getQuestion(currentQuestion));
    }

    private void populateTextField() {
        inputTextField.setText(inputMap.getInputMap().get(currentQuestion).getResponse());
    }

    private void removeResponse() {
        inputMap.removeInput(currentQuestion);
    }

    private void displayResult() {
        resultLabel.setText("Your race: " + raceResult + "\nYour class: " + classResult);
    }
}
