/**
 * Filename:   FoodItem.java
 * Project:    Milestone3
 * Authors:    D-team 85 
 *             Sukyoung Cho, Nahroo Yun, Yeeun Lim, Yongsang Park
 *
 * Semester:   Fall 2018
 * Course:     CS400
 *
 * Due Date:   December 12th,2018
 * Version:    1.0
 *
 * Credits:    none
 *
 * Bugs:       no bugs
 */
package application;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * This class represents a food item with all its properties.
 * 
 * @author aka
 */
public class FoodItem {

	private String name;// The name of the food item.

	private String id;// The id of the food item.

	// Map of nutrients and value.
	private HashMap<String, Double> nutrients;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            name of the food item
	 * @param id
	 *            unique id of the food item
	 */
	public FoodItem(String id, String name) {
		this.name = name;
		this.id = id;
		nutrients = new HashMap<String, Double>();
	}

	/**
	 * Gets the name of the food item
	 * 
	 * @return name of the food item
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the unique id of the food item
	 * 
	 * @return id of the food item
	 */
	public String getID() {
		return this.id;
	}

	/**
	 * Gets the nutrients of the food item
	 * 
	 * @return nutrients of the food item
	 */
	public HashMap<String, Double> getNutrients() {
		return this.nutrients;
	}

	/**
	 * Adds a nutrient and its value to this food. If nutrient already exists,
	 * updates its value.
	 */
	public void addNutrient(String name, double value) {
		nutrients.put(name, value);
	}

	/**
	 * Returns the value of the given nutrient for this food item. If not present,
	 * then returns 0.
	 */
	public double getNutrientValue(String name) {
		if (this.nutrients.containsKey(name)) {
			return nutrients.get(name);
		} else
			return 0;
	}

}
