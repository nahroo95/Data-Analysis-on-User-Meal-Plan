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

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class FoodQueryAddition {
//	private static ArrayList<TextField> text; // text.get(0) represents name, text.get(1) represents
	// nutrient
	private static ArrayList<CheckBox> check;
	private static Button submitButton;

	private Stage window = new Stage();

	/**
	 * display the window
	 * 
	 * @param title the header
	 * @param text  saved data from previous display
	 */
	public void display(String title, ArrayList<TextField> text) {

		window.setTitle(title);

		// Block events to other windows
		window.setTitle(title);
		window.setMinWidth(350);

		VBox vbox = new VBox();

		check = new ArrayList<>();

		String[] arr = { "name\t\t", "nutrient\t\t" };

		TextField[] temp = new TextField[2];
		for (int i = 0; i < arr.length; i++) {
//			Label label1 = new Label();
//			label1.setText(arr[i]);
			CheckBox c = new CheckBox(arr[i]);
			c.setPadding(new Insets(10, 0, 10, 0));
			check.add(c);
			HBox hbox = new HBox();
			hbox.getChildren().add(check.get(i));

			temp[i] = text.get(i);
			temp[i].setPadding(new Insets(10, 10, 10, 10));
			hbox.getChildren().add(temp[i]);
			vbox.getChildren().add(hbox);
		}

		Text rules = new Text("Please Follow this Rule for Nutrient Query"
				+ "\nRule 1. set space among all words\nRule 2. separate all rules by comma"
				+ "\nFor example:\n\"calories >= 50.0\"\n\"calories <= 200.0\"\n\"fiber == 2.5\""
				+ "\n\"calories <= 50, fat == 0\"\nList of nutrients:\ncalories, fat, carbohydrate, fiber, protein\n"
				+ "\n Note: If you want to remove rules, you can delete the condition you want to remove."
				+ "\n For example, calories >= 50.0, fiber == 2.5"
				+ "\n If you want to remove fiber rule, your final condition will be" + "\n calories >= 50.0");
		vbox.setPadding(new Insets(15, 15, 15, 15));
		vbox.setSpacing(10);
		vbox.getChildren().add(rules);

		submitButton = new Button("Apply Query");
		submitButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			text.add(temp[0]);
			text.add(temp[1]);
			window.close();
		});

		vbox.getChildren().add(submitButton);

		// Display window and wait for it to be closed before returning
		Scene scene = new Scene(vbox);
		window.setScene(scene);
		window.showAndWait();
	}

	/**
	 * to check the CheckBox status, I made this method
	 * 
	 * @return ArayList<CheckBox> check
	 */
	public ArrayList<CheckBox> returnCheckBox() {
		return check;
	}

	/**
	 * to check the button status, I made this method
	 * 
	 * @return Button
	 */
	public Button returnSubmitButton() {
		return submitButton;
	}
}