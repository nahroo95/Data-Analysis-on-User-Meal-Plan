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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PopUp {

	/**
	 * Pop Up
	 * 
	 * @param title   header
	 * @param content content
	 */
	public static void showMessageDialog(String title, String content) {
		Stage window = new Stage();

		// Block events to other windows
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
//		window.setMinWidth(250);
		VBox vbox = new VBox();

		vbox.setPadding(new Insets(50, 100, 50, 100));
		vbox.getChildren().add(new Text(content));

		BorderPane root = new BorderPane();

		root.setCenter(vbox);

		// Display window and wait for it to be closed before returning
		Scene scene = new Scene(root);
		window.setScene(scene);
		window.showAndWait();
	}
}
