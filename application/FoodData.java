/**
 * Filename:   FoodData.java
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * This class represents the backend for managing all the operations associated
 * with FoodItems
 * 
 */

public class FoodData implements FoodDataADT<FoodItem> {

	// List of all the food items.
	private List<FoodItem> foodItemList;

	// Map of nutrients and their corresponding index
	private HashMap<String, BPTree<Double, FoodItem>> indexes;

	/**
	 * Public constructor
	 */
	public FoodData() {
		this.foodItemList = new ArrayList<>();
		this.indexes = new HashMap<String, BPTree<Double, FoodItem>>();
		indexes.put("calories", new BPTree<Double, FoodItem>(3));
		indexes.put("fat", new BPTree<Double, FoodItem>(3));
		indexes.put("carbohydrate", new BPTree<Double, FoodItem>(3));
		indexes.put("fiber", new BPTree<Double, FoodItem>(3));
		indexes.put("protein", new BPTree<Double, FoodItem>(3));
	}

	/*
	 * static class that implements Comparator usage : for comparing string name
	 * values to make it alphabetical order
	 * 
	 */
	static class FoodItemComparator implements Comparator<FoodItem> {
		public int compare(FoodItem c1, FoodItem c2) {
			String a1 = c1.getName();
			String a2 = c2.getName();
			return a1.compareTo(a2);
		}
	}

	/**
	 * Method to load the file that user wants Read the .csv file and insert
	 * appropriate values into FoodItem and into index
	 * 
	 * @param filePath - absolute filepath that user wants to use
	 * 
	 *
	 */
	@Override
	public void loadFoodItems(String filePath) {
		try {
			// check if filePath exists
			File fileDir = new File(filePath);
			boolean exists = fileDir.exists();
			if (!exists)
				System.out.println("GIVEN FILEPATH DOES NOT EXIST.");

			// test to see if a file exists
			File file = new File(filePath);
			exists = file.exists();
			if (!file.exists() || !file.isFile()) {
				System.out.println("FILE DOES NOT EXIST.");
			}

			String[] tokens; // string array that stores each element in each line by splitting ,
			String id; // id of the food
			String name; // name of the food
			String nutrients_name; // nutrients name of the food
			Double nutrients_val; // nutrients value that matches nutrient
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(filePath)); // read the file
			String line = br.readLine(); // split by each line
			System.out.println(line);
			// while loop that iterated all lines in the given file 
			// check all the format and insert into foodItemList and indexes if met all requirements
			while (line != null) {
				tokens = line.toString().split(",");
				if (tokens.length != 12 ) { // if the food format is incorrect, skip that line
					line = br.readLine();
					continue;
				}
				id = tokens[0];
				name = tokens[1];
				if ( id.isEmpty() || name.isEmpty() ) { // if id or name is empty skip that line
					line = br.readLine();
					continue;
				}

				FoodItem food = new FoodItem(id, name);
				boolean goWhileLoop = false; //boolean value to check whether we should skip adding into index
				// for loop to add all nutrients 
				for (int i = 0; i < 10; i += 2) { // loop through 5 times 
					nutrients_name = tokens[i + 2];
					// if received nutrients name does not match five of them, skip that line by break
					if (!nutrients_name.equals("calories") && !nutrients_name.equals("fat")
							&& !nutrients_name.equals("carbohydrate") && !nutrients_name.equals("fiber")
							&& !nutrients_name.equals("protein")) {
						goWhileLoop = true;
						break;
					}
					// if received nutrients value is empty, skip that line by break
					if (tokens[i + 3].isEmpty()) {
						goWhileLoop = true;
						break;
					}
					nutrients_val = Double.parseDouble(tokens[i + 3]); // make it to double 
					food.addNutrient(nutrients_name, nutrients_val);
				}
				// if all nutrients value and name are all in correct format,  insert into indexes and foodItemList
				if (goWhileLoop == false) {
					// Add to indexes
					for (String nut : food.getNutrients().keySet()) {
						Double nut_val = food.getNutrients().get(nut);
						indexes.get(nut).insert(nut_val, food);
					}
					// Add to foodItemList
					this.foodItemList.add(food);
				}
				// else, set the goWhileLoop to false again for next line, and set the line to nextline and go while loop
				goWhileLoop = false;
				line = br.readLine();
			}

		} catch (FileNotFoundException e) {
			PopUp.showMessageDialog("Error", "Can't find file");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.sort(foodItemList, new FoodItemComparator()); //sort the foodItemList into alphabetical order
	}

	/**
	 * filterByName method : filters all foodItemList by the given substring 
	 * string should be case insensitive
	 * 
	 * @param - substring given by user 
	 * @return List of foodItem that contains substring
	 * 
	 */
	@Override
	public List<FoodItem> filterByName(String substring) {
		List<FoodItem> filteredList = new ArrayList<FoodItem>(); //new ArrayList that will contain the filteredItems
		// go through all foodItemList, if found a name(make it to lowercase), and if it contains the substring(lowercase)
		// then add into filteredList
		for (int i = 0; i < foodItemList.size(); i++) {
			if (foodItemList.get(i).getName().toLowerCase().contains(substring.toLowerCase())) {
				filteredList.add(foodItemList.get(i));
			}
		}
		Collections.sort(filteredList, new FoodItemComparator()); //sort it to alphabetical order
		return filteredList;
	}

	/**
	 * filterByNutrients method that filters foodItemList by the rules given
	 * if there are more than one rule, using the List intersection algorithm, updated the filteredList
	 * 
	 * @param rules given by users
	 * @return List of foodItem that fulfills all rules 
	 * 
	 */
	@Override
	public List<FoodItem> filterByNutrients(List<String> rules) {
		try {
			List<FoodItem> filteredList = new ArrayList<FoodItem>(); // new arrayList that will contain filteredList for the first rule
			String[] toke; //string array
			String nut; // nutrients name
			Double key; // value of nutrients
			String comparator; // "<=" "==" ">="
			BPTree<Double, FoodItem> a; // bptree to call the rangeSearch
			List<FoodItem> finalList = new ArrayList<FoodItem>(); // arrayList that will be the final return list 
			// go though rule size, to check every rule's requirement
			for (int j = 0; j < rules.size(); j++) {
				toke = rules.get(j).split(" "); // split the rule by space
				nut = toke[0].toLowerCase(); // store the name of nutrients
				comparator = toke[1]; // store the comparator
				key = Double.parseDouble(toke[2]); // store the key and make it to Double
				a = indexes.get(nut); // get the bptree that is of proper nutrients 
				//condition checks if indexes contains the key
				if (!indexes.containsKey(nut)) {
					System.out.println("Indexes does not contain the " + nut);
					continue;
				}
				//condition checks if the comparator is in right format
				if (!comparator.equals(">=") && !comparator.equals("<=") && !comparator.equals("==")) {
					System.out.println("Comparator is in wrong format");
					continue;
				}
				//condition checks if key is double type
				if (!(key instanceof Double)) {
					System.out.println("key is not in Double format");
					continue;
				}
				// if key is negative value, skip this line 
				if (key < 0) {
					System.out.println("key cannot be negative value");
					continue;
				}
				// if filteredList is empty, put the first filtered rule into it
				if (filteredList.isEmpty()) {
					filteredList.addAll(a.rangeSearch(key, comparator));
				}
				// if there is only one rule, set the finalList with the filteredList and get out of loop
				if (rules.size() == 1) {
					finalList = filteredList;
					break;
				}

				a = indexes.get(nut);
				List<FoodItem> ret_list = a.rangeSearch(key, comparator); // stores the result of rangesearch into ret_list
				finalList.clear(); //clear the finalList
				// for each food in ret_list, if filteredList contains same ID in food in ret_list
				// there is a intersection of two rules, add into finalList
				for (FoodItem food : ret_list) {
					for (int i = 0; i < filteredList.size(); i++) {
						if (filteredList.get(i).getID().equals(food.getID())) {
							finalList.add(food);
						}
					}
				}
				filteredList.clear(); // clear the filteredList
				filteredList.addAll(finalList); //add finalList into filteredList -- intersection of previous rules for checking the next rule
			}
			Collections.sort(finalList, new FoodItemComparator());
			return finalList;
		} catch (Exception e) {
			PopUp.showMessageDialog("Error", "Wrong approach");
			return null;
		}
	}

	/**
	 * addFoodItem method that adds the foodItem that user wants into foodItemList
	 * 
	 * @param foodItem that user wnats
	 * 
	 */
	@Override
	public void addFoodItem(FoodItem foodItem) {
		foodItemList.add(foodItem); // add the foodItem into the foodItemList
		Collections.sort(foodItemList, new FoodItemComparator()); // sort in alphabetical order
		BPTree<Double, FoodItem> bpt = new BPTree<>(3); 
		// add into bptree given foodItem nutrients name,value
		// add into indexes
		for (HashMap.Entry<String, Double> entry : foodItem.getNutrients().entrySet()) {
			bpt.insert(foodItem.getNutrientValue(entry.getKey()), foodItem);
			indexes.put(entry.getKey(), bpt);
		}
		Collections.sort(foodItemList, new FoodItemComparator()); // sort in alphabetical order
	}

	/**
	 * getAllFoodItem method that returns the list of foodItemList
	 * 
	 * @return foodItemList
	 */
	@Override
	public List<FoodItem> getAllFoodItems() {
		Collections.sort(foodItemList, new FoodItemComparator()); //sort into alphabetical order
		return foodItemList;
	}
	/**
	 * saveFoodItems method that saves the current foodItem into the given filename
	 * catches exception FileNotFoundException 
	 * 
	 */
	@Override
	public void saveFoodItems(String filename) {
		// Save the list of food items in ascending order by name
		try {
			Collections.sort(foodItemList, new FoodItemComparator()); //sort the list
			PrintWriter pw;
			pw = new PrintWriter(new FileOutputStream(filename));
			// sort by name
			for (FoodItem foodList : foodItemList) {
				pw.println(foodList.getID() + "," + foodList.getName() + ",calories,"
						+ foodList.getNutrientValue("calories") + ",fat," + foodList.getNutrientValue("fat")
						+ ",carbohydrate," + foodList.getNutrientValue("carbohydrate") + ",fiber,"
						+ foodList.getNutrientValue("fiber") + ",protein," + foodList.getNutrientValue("protein"));
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			PopUp.showMessageDialog("Error", "Wrong approach");
		} catch (NullPointerException e) {
			e.printStackTrace();
			PopUp.showMessageDialog("Error", "Wrong approach");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
