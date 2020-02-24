/**
 * Filename:   ListPanel.java
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
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ListPanel {

	private BorderPane border;
	private ScrollPane scroll;
	private Text list;
	private VBox vBox;
	private ArrayList<CheckBox> check;

	/**
	 * set a default list panel
	 * 
	 * @param emptyText when the BorderPanel is empty, set the content
	 */
	public ListPanel(String emptyText) {
		// set scrollPanel law
		scroll = new ScrollPane();
		scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
		scroll.setPrefSize(200, 250);
		scroll.setPadding(new Insets(10, 10, 10, 10));
		scroll.setVisible(true);

		Label temp = new Label();
		temp.setText(emptyText);
		scroll.setContent(temp);

		// We will put entire contents in the left VBox
		vBox = new VBox();
		vBox.setSpacing(10);

		border = new BorderPane();
		border.setVisible(true);
		border.setCenter(scroll);

		check = new ArrayList<>();
	}

	/**
	 * set the header
	 * 
	 * @param str that set the header from developer
	 */
	public void header(String str) {
		list = new Text(str);
		vBox.getChildren().add(list);
		border.setTop(list);
	}

	/**
	 * add food from selected check box list for right panel
	 * 
	 * @param node will be represented as check box
	 */
	public void addFoodListThroughNode(Node node) {
		check.add((CheckBox) node);
		vBox.getChildren().add(check.get(check.size() - 1));
		scroll.setContent(vBox);
		border.setCenter(scroll);
//		size++;
	}

	/**
	 * for left panel
	 * 
	 * @param foodItem comes from FoodData List<FoodItem>
	 */
	public void setFoodListThroughFoodItemList(List<FoodItem> foodItem) {
		// set scrollPanel law
		int checkSize = check.size();
		for (int i = 0; i < checkSize; i++)
			check.remove(check.get(0));

		int vBoxSize = vBox.getChildren().size();
		for (int i = 0; i < vBoxSize; i++)
			vBox.getChildren().remove(0);

		for (FoodItem fi : foodItem) {
			check.add(new CheckBox(fi.getName()));
			vBox.getChildren().add(check.get(check.size() - 1));
		}

		scroll.setContent(vBox);
		border.setCenter(scroll);
	}

	/**
	 * reset the border panel
	 */
	public void removeAll() {
		int checkSize = check.size();
		for (int i = 0; i < checkSize; i++)
			check.remove(check.get(0));

		int vBoxSize = vBox.getChildren().size();
		for (int i = 0; i < vBoxSize; i++)
			vBox.getChildren().remove(0);

		scroll.setContent(vBox);
		border.setCenter(scroll);
	}

	/**
	 * For checking border
	 * 
	 * @return border
	 */
	public BorderPane returnBorderPanel() {
		return border;
	}

	/**
	 * For checking all the check box
	 * 
	 * @return check
	 */
	public ArrayList<CheckBox> returnCheckBox() {
		return check;
	}

	/**
	 * For checking the scroll
	 * 
	 * @return scroll
	 */
	public ScrollPane returnScrollPanel() {
		return scroll;
	}

	/**
	 * For checking the VBox status
	 * 
	 * @return VBox
	 */
	public VBox returnVBox() {
		return vBox;
	}
}
