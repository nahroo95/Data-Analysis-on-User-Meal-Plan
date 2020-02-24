/**
 * Filename:   Main.java
 * Project:    Milestone2
 * Authors:    D-team 85 
 *             Sukyoung Cho, Nahroo Yun, Yeeun Lim, Yongsang Park
 *
 * Semester:   Fall 2018
 * Course:     CS400
 *
 * Due Date:   November 30th,2018
 * Version:    1.0
 *
 * Credits:    none
 *
 * Bugs:       no bugs
 */
package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FoodAddition {

   private String[] answers = new String[6];

   /**
    * display the window for add new food button
    * 
    * @param title    header
    * @param fd       update the FoodData
    * @param nutrient update the FoodItems nutrient
    */
   public void display(String title, FoodData fd, HashMap<String, Double[]> nutrient) {
      Stage window = new Stage();

      // Block events to other windows
      window.initModality(Modality.APPLICATION_MODAL);
      window.setTitle(title);
      window.setMinWidth(300);

      VBox vbox = new VBox();

      Label labels;
      ArrayList<TextField> userText = new ArrayList<>();
      String[] arr = { "Name\t\t", "Calories\t\t", "Fat\t\t\t", "Carbohydrate\t", "Fiber\t\t\t", "Protein\t\t" };

      for (int i = 0; i < arr.length; i++) {
         HBox hbox = new HBox();
         labels = new Label();
         labels.setText(arr[i]);
         hbox.getChildren().add(labels);

         TextField temp = new TextField();
         userText.add(temp);
         hbox.getChildren().add(temp);

         vbox.getChildren().add(hbox);
      }

      Button submit = new Button();
      submit.setText("Save the food information");
      vbox.getChildren().add(submit);
      vbox.setPadding(new Insets(15, 15, 15, 15));
      vbox.setSpacing(10);

      submit.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
         for (int i = 0; i < arr.length; i++)
            answers[i] = userText.get(i).getText();

         boolean blank = true; // check if there is a name in the name blank

         for (String a : answers) {
            a = a.trim();
            blank = blank && a == "";
         }

         boolean spaceContains = true; // No spaces in nutrition

         for (int i = 0; i < 5; i++) {
            spaceContains = spaceContains && answers[i + 1].contains(" ");
         }

         boolean isPositive = true; // check if the nutrient is positive or zero

         for (int i = 0; i < 5; i++) {
            if (!answers[i + 1].isEmpty() && answers[i + 1].replaceAll("\\d+", "").length() == 0) {

               Double.parseDouble(answers[i + 1]);
               for (int j = 0; j < answers[i + 1].length(); j++) {
                  double d = Double.parseDouble(answers[i + 1]);
                  isPositive = isPositive && d >= 0;
               }
            } else {
               isPositive = false;
            }
         }

         boolean commaContains = true; // No commas in all answers

         for (String a : answers) {
            commaContains = commaContains && a.contains(",");
         }

         if (!blank && isPositive && !commaContains && !spaceContains) {
            Random rd = new Random();
            Double key = rd.nextDouble() * 1000000 + answers[0].hashCode();

            FoodItem fi = new FoodItem("" + answers[0].hashCode() + key, answers[0]);

            Double[] nutValue = new Double[5];

            try {
               for (int i = 1; i < arr.length; i++) {
                  fi.addNutrient(answers[0], Double.parseDouble(answers[i]));
                  nutValue[i - 1] = Double.parseDouble(answers[i]);
               }
            } catch (Exception exception) {
               PopUp.showMessageDialog("Error", "Invalid approach");
            }

            nutrient.put(answers[0], nutValue);

            fd.addFoodItem(fi);
         } else {
            PopUp.showMessageDialog("Error", "Invalid approach");
            Main.isPopUppedFoodButton = true;
         }
         window.close();
      });

      // Display window and wait for it to be closed before returning
      Scene scene = new Scene(vbox);
      window.setScene(scene);
      window.showAndWait();

   }

   ////////////////////
   // private method //
   ////////////////////

   /**
    * check if it is number or not
    * 
    * @param obj want to know if it is number
    * @return true if it is number
    */
   private boolean isNumeric(Object obj) {
      boolean output = true;
      int[] a = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

      for (int i = 0; i < obj.toString().length(); i++) {
         for (int j = 0; j < a.length; j++) {
            boolean number = true;
            number = number && obj.toString().substring(i).equals("" + a[j]);
            output = output && number;
         }
      }

      return output;
   }

}