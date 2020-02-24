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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main<F extends FoodItem> extends Application {
	private ListPanel leftPanel;
	private ListPanel rightPanel;

	private FoodQueryAddition fqa = new FoodQueryAddition();

	private Button addFoodButton1 = new Button("Add to Food List");; // Add Food with your info
	private Button addFoodButton2; // move foods from left to right
	private Button clearButton; // set clear from query execution, no need at the start point

	private FoodData fd;
	private HashMap<String, Double[]> nutrients; // food name, nutrients list
	private Double[] rightNutrients = { 0.0, 0.0, 0.0, 0.0, 0.0 }; // calories, fat, carbohydrate, fiber, protein
	private final String[] nutrientNames = { "calories", "fat", "carbohydrate", "fiber", "protein" };

	private ArrayList<TextField> text = new ArrayList<>();

	public static boolean isPopUpped;
	public static boolean isPopUppedFoodButton;

	public void start(Stage primaryStage) {
		try {
			text.add(new TextField());
			text.add(new TextField());

			fd = new FoodData();
			nutrients = new HashMap<>();
			// fd.getAllFoodItems(); return type: List<FoodItem>

			// make a menu bar
			MenuBar menuBar = new MenuBar();

			// set a menu series
			Menu menu = new Menu("Menu");

			// set children of menu series
			MenuItem loadAdditionalFood = new MenuItem("Load additional food");
			// when clicked, sort action will be executed
			loadAdditionalFood.setOnAction(e -> {
				try {
					FileChooser choose = new FileChooser(); // Create a file selection dialog
					choose.setTitle("Load new food list");
					File file = choose.showOpenDialog(primaryStage);

					// clear the data values
					rightPanel.removeAll();
					for (int i = 0; i < 5; i++)
						rightNutrients[i] = 0.0;
					nutrients = new HashMap<>();
					fd = new FoodData();

					if (file != null)
						fd.loadFoodItems(file.getAbsolutePath()); // Load the new food list into the food data
					// object with all details of the foods

					for (FoodItem fi : fd.getAllFoodItems()) { // get nutrients from foodItems
						Double[] nutValues = new Double[5];

						for (int i = 0; i < 5; i++) // put nutrition values into HashMap
							nutValues[i] = fi.getNutrientValue(nutrientNames[i]);

						nutrients.put(fi.getName(), nutValues);

					}
					addFoodButton1.setDisable(false);
					clearButton.setDisable(true);

					leftPanel.setFoodListThroughFoodItemList(fd.getAllFoodItems());

					leftPanel.header("Food List     " + "Total number of foods: " + fd.getAllFoodItems().size() + " ");
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			});

			// set children of menu series
			MenuItem foodQuery = new MenuItem("Food Query");
			// when clicked, food query action will be executed
			foodQuery.setOnAction(e -> {
				if (!leftPanel.returnCheckBox().isEmpty()) {

					do {
						isPopUpped = false;
						fqa.display("Food Query", text);

						if (fqa.returnCheckBox().get(0).isSelected() && fqa.returnCheckBox().get(1).isSelected()) { // both
																													// filtered
																													// apllied
							addFoodButton1.setDisable(false);
							clearButton.setDisable(true);

							leftPanel.setFoodListThroughFoodItemList(fd.getAllFoodItems());
							leftPanel.header(
									"Food List     Total number of foods: " + fd.getAllFoodItems().size() + " ");

							TextField answer1 = text.get(0);
							TextField answer2 = text.get(1);

							if (!answer1.getText().isEmpty() && !answer1.getText().isEmpty()) {
								isPopUpped = false;

								String[] answer2Rule = answer2.getText().split(","); // <nutrient>, <comparator>,
																						// <value>
								List<String> answer2RuleArranged = new ArrayList<>();
								for (String a : answer2Rule)
									answer2RuleArranged.add(a.trim());

								List<FoodItem> filterByName = fd.filterByName(answer1.getText());

								ArrayList<String> fliteredDataName = new ArrayList<>();
								for (FoodItem tempFD : filterByName)
									fliteredDataName.add(tempFD.getName());

								List<FoodItem> filterByNutrient = fd.filterByNutrients(answer2RuleArranged);

								ArrayList<FoodItem> input = new ArrayList<>(); // change the input from List to
																				// ArrayList
								if (fd.filterByNutrients(answer2RuleArranged) != null) {
									for (FoodItem nameFilter : filterByName)
										for (FoodItem nutFilter : filterByNutrient)
											if (nameFilter.equals(nutFilter)) {
												input.add(nameFilter);
												break;
											}

									leftPanel.setFoodListThroughFoodItemList(input);

									leftPanel.header("Queried food list    Total number of foods: "
											+ leftPanel.returnCheckBox().size());

									addFoodButton1.setDisable(true);
									clearButton.setDisable(false);
								} else
									leftPanel.setFoodListThroughFoodItemList(fd.getAllFoodItems());

							} else {
								PopUp.showMessageDialog("Error", "Wrong approach");
								isPopUpped = true;
							}
						} else if (fqa.returnCheckBox().get(0).isSelected()) { // filtered by name
							addFoodButton1.setDisable(false);
							clearButton.setDisable(true);

							leftPanel.setFoodListThroughFoodItemList(fd.getAllFoodItems());
							leftPanel.header(
									"Food List     Total number of foods: " + fd.getAllFoodItems().size() + " ");

							TextField answer = text.get(0);

							if (!answer.getText().isEmpty()) {
								isPopUpped = false;
								addFoodButton1.setDisable(true);
								clearButton.setDisable(false);

								List<FoodItem> filterByName = fd.filterByName(answer.getText());

								leftPanel.setFoodListThroughFoodItemList(filterByName);

								leftPanel.header("Queried food list    Total number of foods: "
										+ leftPanel.returnCheckBox().size());

							} else {
								PopUp.showMessageDialog("Error", "Wrong approach");
								isPopUpped = true;
							}
						} else if (fqa.returnCheckBox().get(1).isSelected()) { // filtered by nutriens
							addFoodButton1.setDisable(false);
							clearButton.setDisable(true);

							leftPanel.setFoodListThroughFoodItemList(fd.getAllFoodItems());
							leftPanel.header(
									"Food List     Total number of foods: " + fd.getAllFoodItems().size() + " ");

							TextField answer = text.get(1);

							if (!answer.getText().isEmpty()) {
								isPopUpped = false;

								String[] answer2Rule = answer.getText().split(","); // <nutrient> <comparator> <value>,
																					// other
								// data

								List<String> answer2RuleArranged = new ArrayList<>();
								for (String a : answer2Rule)
									answer2RuleArranged.add(a.trim());

								List<FoodItem> filterByNutrient = fd.filterByNutrients(answer2RuleArranged);

								if (filterByNutrient != null) {
									leftPanel.setFoodListThroughFoodItemList(filterByNutrient);

									leftPanel.header("Queried food list    Total number of foods: "
											+ leftPanel.returnCheckBox().size());

									addFoodButton1.setDisable(true);
									clearButton.setDisable(false);
								} else
									leftPanel.setFoodListThroughFoodItemList(fd.getAllFoodItems());

							} else {
								PopUp.showMessageDialog("Error", "Wrong approach");
								isPopUpped = true;
							}
						}
					} while (isPopUpped);
				} else {
					PopUp.showMessageDialog("Error", "Put some food in Food List");
				}
			});

			// set children of menu series
			MenuItem saveMenu = new MenuItem("Save the Food List");
			saveMenu.setOnAction(e -> {
				FileChooser choose = new FileChooser(); // Create a file selection dialog
				choose.setTitle("Save your food list");
				File file = choose.showSaveDialog(primaryStage);

				if (file != null)
					fd.saveFoodItems(file.getAbsolutePath());
			});

			// add children (of Menu 1)
			menu.getItems().add(loadAdditionalFood);
			menu.getItems().add(foodQuery);
			menu.getItems().add(saveMenu);

			// add menus into menu bar
			menuBar.getMenus().add(menu);
			BorderPane rootPanel = new BorderPane();
			rootPanel.setTop(menuBar);

			Scene scene = new Scene(rootPanel, 700, 700, Color.BLACK);

			// left panel
			leftPanel = new ListPanel("Load a file");
			leftPanel.header("Food List     " + "Total number of foods: 0");

			// right panel
			rightPanel = new ListPanel("Add some foods");
			rightPanel.header("Meal List");

			// button lists
			FoodAddition fa = new FoodAddition();

			HBox hbox = new HBox();
			String style = "-fx-background-color: Beige;";
			hbox.setStyle(style);

			// add food to food list button implementation
			addFoodButton1.setPadding(new Insets(5, 5, 5, 5));
			addFoodButton1.setOnMouseMoved(e -> addFoodButton1.setStyle("-fx-background-color: pink;"));
			addFoodButton1.setOnMouseExited(e -> addFoodButton1.setStyle("-fx-background-color: lightgrey;"));
			addFoodButton1.setOnAction(e -> {
				try {
					do {
						isPopUppedFoodButton = false;
						fa.display("Add Food to Food List", fd, nutrients);

						leftPanel.setFoodListThroughFoodItemList(fd.getAllFoodItems());
						leftPanel.header(
								"Food List     " + "Total number of foods: " + fd.getAllFoodItems().size() + " ");
					} while (isPopUppedFoodButton);
				} catch (Exception exception) {
				}

			});

			// add food to meal list button implementation
			addFoodButton2 = new Button("Add to Meal List");
			addFoodButton2.setPadding(new Insets(5, 5, 5, 5));
			addFoodButton2.setOnMouseMoved(e -> addFoodButton2.setStyle("-fx-background-color: pink;"));
			addFoodButton2.setOnMouseExited(e -> addFoodButton2.setStyle("-fx-background-color: lightgrey;"));
			// when executed, the list of selected food moves from the left to the right
			addFoodButton2.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
				for (CheckBox c : leftPanel.returnCheckBox()) {
					if (c.isSelected()) {
						String name = c.toString().substring(c.toString().indexOf("'") + 1, c.toString().length() - 1);
						c.setSelected(false);
						rightPanel.addFoodListThroughNode(c);

						for (int i = 0; i < 5; i++)
							rightNutrients[i] += nutrients.get(name)[i];
					}
				}
				if (clearButton.isDisabled())
					leftPanel.setFoodListThroughFoodItemList(fd.getAllFoodItems());
			});

			// analyzebutton implementation
			Button analyzeButton = new Button("Analyze Meal");
			analyzeButton.setPadding(new Insets(5, 5, 5, 5));
			analyzeButton.setOnMouseMoved(e -> analyzeButton.setStyle("-fx-background-color: pink;"));
			analyzeButton.setOnMouseExited(e -> analyzeButton.setStyle("-fx-background-color: lightgrey;"));
			analyzeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
				if (rightPanel.returnCheckBox().size() != 0)
					AnalyzeAddition.display("Analyzed Nutrients", rightNutrients);
				else
					PopUp.showMessageDialog("Error", "No food in meal list");
			});

			// clearbutton implementation
			clearButton = new Button("Clear the filter");
			clearButton.setPadding(new Insets(5, 5, 5, 5));
			clearButton.setOnMouseMoved(e -> clearButton.setStyle("-fx-background-color: pink;"));
			clearButton.setOnMouseExited(e -> clearButton.setStyle("-fx-background-color: lightgrey;"));
			clearButton.setDisable(true);
			// when executed, all setting become default
			clearButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
				leftPanel.setFoodListThroughFoodItemList(fd.getAllFoodItems());
				leftPanel.header("Food List     " + "Total number of foods: " + fd.getAllFoodItems().size() + " ");
				addFoodButton1.setDisable(false);
				clearButton.setDisable(true);

				text = new ArrayList<>();

				text.add(new TextField());
				text.add(new TextField());
			});

			// meal list clear button
			Button mealListClear = new Button("Clear the meal list");
			mealListClear.setPadding(new Insets(5, 5, 5, 5));
			mealListClear.setOnMouseMoved(e -> mealListClear.setStyle("-fx-background-color: pink;"));
			mealListClear.setOnMouseExited(e -> mealListClear.setStyle("-fx-background-color: lightgrey;"));
			// when executed, meal list will be clear
			mealListClear.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
				rightPanel.removeAll();
			});

			// removeButton implementation
			Button removeButton = new Button("Remove");
			removeButton.setPadding(new Insets(5, 5, 5, 5));
			removeButton.setOnMouseMoved(e -> removeButton.setStyle("-fx-background-color: pink;"));
			removeButton.setOnMouseExited(e -> removeButton.setStyle("-fx-background-color: lightgrey;"));
			// when executed, right to left
			removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
				for (CheckBox c : rightPanel.returnCheckBox()) {
					if (c.isSelected()) {
						String name = c.toString().substring(c.toString().indexOf("'") + 1, c.toString().length() - 1);
						c.setSelected(false);
						rightPanel.returnVBox().getChildren().remove(c);

						for (int i = 0; i < 5; i++)
							rightNutrients[i] -= nutrients.get(name)[i];
					}
				}
			});

			removeButton.getLineSpacing();

			// add text buttons to the bottom of foodList

			// addFood button to food list and explanation
			Text descrp_foodList = new Text("Type food infomation to add into Food List");
			descrp_foodList.setTextAlignment(TextAlignment.RIGHT);
			VBox text_foodList = new VBox();
			HBox user_addFood = new HBox();
			user_addFood.getChildren().addAll(addFoodButton1, addFoodButton2);
			text_foodList.getChildren().add(descrp_foodList);
			text_foodList.getChildren().add(user_addFood);

			// addFood button to meal list and explanation
			Text descp_foodList2 = new Text("Select foods to add into Meal List");
			descp_foodList2.setTextAlignment(TextAlignment.RIGHT);
			VBox text_foodList2 = new VBox();
			VBox addFoodMeal = new VBox();
			text_foodList2.getChildren().add(descp_foodList2);
			addFoodMeal.getChildren().add(text_foodList2);
			addFoodMeal.getChildren().add(addFoodButton2);
			text_foodList.getChildren().add(addFoodMeal);

			// foodQuery button implementation
			text_foodList.setPadding(new Insets(10, 5, 5, 10));
			text_foodList.setSpacing(10);
			hbox.getChildren().add(text_foodList);

			// add text and remove button to the bottom of mealList
			Text descrp_mealList = new Text("Select foods to remove or clear from the Meal List");
			descrp_mealList.setTextAlignment(TextAlignment.RIGHT);
			VBox text_descrp_mealList = new VBox();
			text_descrp_mealList.setPadding(new Insets(10, 5, 5, 80));
			text_descrp_mealList.setSpacing(10);

			text_descrp_mealList.getChildren().add(mealListClear);

			text_descrp_mealList.getChildren().add(descrp_mealList);

			HBox forButton = new HBox();
			removeButton.setLineSpacing(10);
			mealListClear.setLineSpacing(10);
			forButton.getChildren().addAll(removeButton, new Text("      "), mealListClear);

			text_descrp_mealList.getChildren().add(forButton);

			// add text and analyze button to the bottom of mealList
			Text descrp_mealList2 = new Text("Button for Analyze the Meal List");
			descrp_mealList2.setTextAlignment(TextAlignment.RIGHT);
			text_descrp_mealList.getChildren().add(descrp_mealList2);
			text_descrp_mealList.getChildren().add(analyzeButton);

//			text_descrp_mealList.getChildren().add(mealListClear);

			hbox.getChildren().add(text_descrp_mealList);

			// set the whole hbox to be bottom of rootPanel
			rootPanel.setBottom(hbox);

//			VBox vForCenter = new VBox();

//			vForCenter.getChildren().add(clearButton);
//			vForCenter.getChildren().add(mealListClear);
			rootPanel.setCenter(clearButton);

			rootPanel.setLeft(leftPanel.returnBorderPanel());
			rootPanel.setRight(rightPanel.returnBorderPanel());

			// make empty spaces to the root panel
			rootPanel.setPadding(new Insets(10, 10, 10, 10));

			primaryStage.setScene(scene);
			primaryStage.setTitle("Food Data Analysis");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
