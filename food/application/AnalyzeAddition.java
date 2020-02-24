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

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AnalyzeAddition {

	/**
	 * display the analyze window
	 * 
	 * @param title          that for the header
	 * @param rightNutrients to update the nutrients
	 */
	public static void display(String title, Double[] rightNutrients) {
		Stage window = new Stage();
		window.setTitle(title);

		// Block events to other windows
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);

		BorderPane root = new BorderPane();
		VBox boxLeft = new VBox();
		VBox boxRight = new VBox();

		String[] arr = { "Calories", "Fat", "Carbohydrate", "Fiber", "Protein" };

		for (int i = 0; i < arr.length; i++) {
			Label label1 = new Label();
			label1.setText(arr[i]);
			label1.setPadding(new Insets(10, 10, 10, 10));
			boxLeft.getChildren().add(label1);

			Label label2 = new Label();
			label2.setText("" + rightNutrients[i]);
			label2.setPadding(new Insets(10, 10, 10, 10));
			boxRight.getChildren().add(label2);
		}

		root.setLeft(boxLeft);
		root.setRight(boxRight);

		// Display window and wait for it to be closed before returning
		Scene scene = new Scene(root, 300, 200, Color.WHITE);

		window.setScene(scene);
		window.showAndWait();
	}
}