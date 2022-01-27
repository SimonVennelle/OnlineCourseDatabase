//Simon Vennelle, 20765420, CSE3OAD

//package com.test;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.sql.SQLOutput;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import javafx.collections.*;
import javafx.collections.transformation.*;
import javafx.scene.control.cell.*;
import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.function.Predicate;

import javafx.stage.*;	// Modality
import javafx.util.converter.DateStringConverter;

public class CourseFX extends Application {
	// used as ChoiceBox value for filter
	public enum FILTER_COLUMNS {
		SUBJECT,
		SEMESTER,
		STARTING_MONTH
	};
	// the data source controller
	private CourseDSC courseDSC;
	
	public void init() throws Exception {
		// Data source controller
		courseDSC = new CourseDSC();

		/* TODO 2-01 - TO COMPLETE ****************************************
		 * call the data source controller database connect method
		 * NOTE: that database connect method throws exception
		 */

		try {
			CourseDSC.connect();
		} catch (Exception e) {
			throw new Exception("Database Connection Failed :(");
		}

	}

	public void start(Stage stage) throws Exception {
		/* TODO 2-02 - TO COMPLETE ****************************************
		 * - this method is the start method for your application
		 * - set application title
		 * - show the stage
		 */


		build(stage);
		//stage.setTitle(getClass().getName());
		stage.setTitle("Simons Course Database");
		stage.show();

		/* TODO 2-03 - TO COMPLETE ****************************************
		 * currentThread uncaught exception handler
		 */

		Thread.currentThread().setUncaughtExceptionHandler((thread, exception) ->
		{
			System.out.println("ERROR: " + exception);
		});
	}

	public void build(Stage stage) throws Exception {

		// Create table data (an observable list of objects)
		ObservableList<Course> tableData = FXCollections.observableArrayList();

		// Define table columns
		TableColumn<Course, String> idColumn = new TableColumn<Course, String>("Id");
		TableColumn<Course, String> subjectCodeColumn = new TableColumn<Course, String>("Subject");
		TableColumn<Course, Integer> quantityColumn = new TableColumn<Course, Integer>("Number Of Students");
		TableColumn<Course, String> semesterColumn = new TableColumn<Course, String>("Semester");
		TableColumn<Course, String> dateColumn = new TableColumn<Course, String>("Starting Date");
		
		/* TODO 2-04 - TO COMPLETE ****************************************
		 * for each column defined, call their setCellValueFactory method 
		 * using an instance of PropertyValueFactory
		 */

		idColumn.setCellValueFactory(new PropertyValueFactory<Course, String>("id"));
		subjectCodeColumn.setCellValueFactory(new PropertyValueFactory<Course, String>("subjectCode"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<Course, Integer>("numberOfStudents"));
		semesterColumn.setCellValueFactory(new PropertyValueFactory<Course, String>("semester"));
		dateColumn.setCellValueFactory(new PropertyValueFactory<Course, String>("DateStr"));


		// Create the table view and add table columns to it
		TableView<Course> tableView = new TableView<Course>();


		/* TODO 2-05 - TO COMPLETE ****************************************
		 * add table columns to the table view create above
		 */
		tableView.getColumns().add(idColumn);
		tableView.getColumns().add(subjectCodeColumn);
		tableView.getColumns().add(quantityColumn);
		tableView.getColumns().add(semesterColumn);
		tableView.getColumns().add(dateColumn);

		//	Attach table data to the table view
		tableView.setItems(tableData);

		/* TODO 2-06 - TO COMPLETE ****************************************
		 * set minimum and maximum width to the table view and each columns
		 */
		tableView.setMinWidth(600);
		tableView.setMinHeight(700);
		idColumn.setMinWidth(20);
		subjectCodeColumn.setMinWidth(100);
		quantityColumn.setMinWidth(220);
		semesterColumn.setMinWidth(120);
		dateColumn.setMinWidth(150);


		/* TODO 2-07 - TO COMPLETE ****************************************
		 * call data source controller get all subject course method to add
		 * all subjects of the course to table data observable list
		 */

		tableData = FXCollections.observableArrayList(courseDSC.getAllCourses());
		//	Attach table data to the table view
		tableView.setItems(tableData);

		// =====================================================
		// ADD the remaining UI elements
		// NOTE: the order of the following TODO items can be 
		// 		 changed to satisfy your UI implementation goals
		// =====================================================
		
		
		/* TODO 2-08 - TO COMPLETE ****************************************
		 * filter container - part 1
		 * add all filter related UI elements you identified
		 */

		TextField filterTF = new TextField();
		Label filterLB = new Label("   Filter By:");
		//ComboBox filterDropDown = new ComboBox();

		//Dropdown box for choosing which column to filter
		ChoiceBox filterChoiceBox = new ChoiceBox();
		ObservableList<String> options = FXCollections.observableArrayList("Subject","Semester", "Starting Month");

		filterChoiceBox.setItems(options); // this statement adds all values in choiceBox

		filterChoiceBox.setValue("Subject"); // this statement shows default value
		//filterChoiceBox.getSelectionModel().selectFirst();


		Label filterS = new Label("   ");
		CheckBox filterCB = new CheckBox("Prerequisite");

		HBox filterHBox = new HBox(filterTF, filterLB, filterChoiceBox, filterS, filterCB);
		//filterHBox.setAlignment(Pos.CENTER_LEFT);


		
		/* TODO 2-09 - TO COMPLETE ****************************************
		 * filter container - part 2:
		 * - addListener to the "Filter By" ChoiceBox to clear the filter
		 *   text field value and set focus to the filter text field
		 */

		filterChoiceBox.setOnAction((event) ->
		{
			filterTF.clear();
			filterTF.requestFocus();

		});

		/* TODO 2-10 - TO COMPLETE ****************************************
		 * filter container - part 2:
		 * - setOnAction on the "Prerequisites" Checkbox to filter the subjects
		 *   that have prerequisites
		 */




		//Filtered List
		FilteredList<Course> filteredList = new FilteredList<>(tableData, x -> true);

		Predicate<Course> checkPrerequisites = q -> q.getSubject().getHasPrerequisites();

		filterCB.setOnAction((e) ->
		{
			if(filterTF.getText().isEmpty() || filterTF== null)
			{
				if (filterCB.isSelected())
				{
					filteredList.setPredicate(checkPrerequisites);
				}
				else
				{
					filteredList.setPredicate(x -> true);
				}
			}
			else
			{

			}

		});



		/* TODO 2-11 - TO COMPLETE ****************************************
		 * filter container - part 3:
		 * - create a filtered list
		 * - create a sorted list from the filtered list
		 * - bind comparators of sorted list with that of table view
		 * - set subjects of table view to be sorted list
		 * - set a change listener to text field to set the filter predicate
		 *   of filtered list
		 */


		Predicate<Course> checkSC = q -> q.getSubjectCode().toUpperCase().contains(filterTF.getText().strip().toUpperCase());
		Predicate<Course> checkSEM = q -> q.getSemester().toString().toUpperCase().contains(filterTF.getText().strip().toUpperCase());
		Predicate<Course> checkMONTH = q -> q.getDateStr().substring(2,5).contains(filterTF.getText().strip().toUpperCase());


		SortedList<Course> sortedList = new SortedList<>(filteredList);
		sortedList.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedList);



		filterTF.textProperty().addListener((observable, oldValue, newValue) ->
		{
			int selectedIndex = filterChoiceBox.getSelectionModel().getSelectedIndex();

			filteredList.setPredicate(course ->
			{
				// If filter text is empty, display all products
				if (newValue == null || newValue.isEmpty())
				{
					return true;
				}

				String filterString = newValue.toUpperCase();
				//System.out.println(filterString);

				if (selectedIndex == 0)
				{
					if (course.getSubjectCode().toUpperCase().contains(filterString))
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else if (selectedIndex == 1)
				{
					if (course.getSemester().toString().toUpperCase().contains(filterString))
					{
						return true;
					}
					else
					{
						return false;
					}
				}
				else if (selectedIndex == 2)
				{
					if (course.getDateStr().substring(2,5).contains(filterString))
					{
						System.out.println(course.getDateStr().substring(2,5));
						return true;
					}
					else if (filterString.contains("e"))
					{
						Alert numbersOnly = new Alert(Alert.AlertType.WARNING);
						numbersOnly.setTitle("Numbers Only");
						numbersOnly.setContentText("Please on enter months in number form e.g. February is 2 or 02");
						numbersOnly.show();
						return false;
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}

			});
			if (filterCB.isSelected() && selectedIndex == 0)
			{
				filteredList.setPredicate(checkPrerequisites.and(checkSC));
			}
			else if (filterCB.isSelected() && selectedIndex == 1)
			{
				filteredList.setPredicate(checkPrerequisites.and(checkSEM));
			}
			else if (filterCB.isSelected() && selectedIndex == 2)
			{
				filteredList.setPredicate(checkPrerequisites.and(checkMONTH));
			}

		});




		
		/* TODO 2-12 - TO COMPLETE ****************************************
		 * ACTION buttons: ADD, UPDATE No of STUDENTS, DELETE
		 * - ADD button sets the add UI elements to visible;
		 *   NOTE: the add input controls and container may have to be
		 * 		   defined before these action controls & container(s)
		 * - UPDATE No of STUDENTS and DELETE buttons action need to check if a
		 *   table view row has been selected first before doing their
		 *   action; hint: should you also use an Alert confirmation?
		 */
		Button addBT = new Button(" ADD ");
		Button updateBT = new Button(" UPDATE No of STUDENTS ");
		Button deleteBT = new Button(" DELETE ");
		HBox buttonHBox = new HBox(addBT, updateBT, deleteBT);


		//TextField filterTF = new TextField();
		//Label filterLB = new Label("   Filter By:");
		//ComboBox filterDropDown = new ComboBox();

		//Dropdown box for choosing which subject to add
		ChoiceBox subjectChoiceBox = new ChoiceBox();
		ObservableList<Subject> subjects = FXCollections.observableArrayList(courseDSC.getAllSubjects());
		//System.out.println(subjects);
		subjectChoiceBox.setItems(subjects); // this statement adds all values in choiceBox

		Label subjectLB = new Label("Subject");
		subjectLB.setGraphic(subjectChoiceBox);
		subjectChoiceBox.setMaxWidth(300);
		subjectLB.setStyle("-fx-content-display: BOTTOM");


		//Dropdown box for choosing which semester to add
		ChoiceBox semesterChoiceBox = new ChoiceBox();
		ObservableList<CourseDSC.SEMESTER> semesters = FXCollections.observableArrayList(CourseDSC.SEMESTER.values());
		//System.out.println(semesters);
		semesterChoiceBox.setItems(semesters); // this statement adds all values in choiceBox

		Label semesterLB = new Label("Semester");
		semesterLB.setGraphic(semesterChoiceBox);
		semesterChoiceBox.setMinWidth(100);
		semesterLB.setStyle("-fx-content-display: BOTTOM");

		TextField quantityTF = new TextField();
		Label quantityLB = new Label("Quantity");
		quantityLB.setGraphic(quantityTF);
		quantityTF.setMaxWidth(150);
		quantityLB.setStyle("-fx-content-display: BOTTOM");

		DatePicker datePicker = new DatePicker();
		Label dateLB = new Label("Starting Date");
		dateLB.setGraphic(datePicker);
		datePicker.setMaxWidth(200);
		dateLB.setStyle("-fx-content-display: BOTTOM");

		Button clearBT = new Button(" CLEAR ");
		Button saveBT = new Button(" SAVE ");



		HBox addContents = new HBox(subjectLB, semesterLB, quantityLB, dateLB);
		HBox extraButtons = new HBox(clearBT, saveBT);
		extraButtons.setAlignment(Pos.CENTER);

		addContents.setVisible(false);
		extraButtons.setVisible(false);
		addBT.setOnAction((e) ->
		{
			if(addContents.isVisible())
			{
				addContents.setVisible(false);
				extraButtons.setVisible(false);
			}
			else
			{
				addContents.setVisible(true);
				extraButtons.setVisible(true);
			}
		});


		// ===================================================================
		// Implement CRUD Functionality
		// ====================================================================

		saveBT.setOnAction(z ->
		{

			try
			{
				// Get input values
				String codeTMP = subjectChoiceBox.getValue().toString().substring(8);
				String code = codeTMP.split(",")[0];
				int quantity = Integer.parseInt(quantityTF.getText());
				CourseDSC.SEMESTER semester = CourseDSC.SEMESTER.valueOf(semesterChoiceBox.getValue().toString());
				LocalDate date = datePicker.getValue();

				//System.out.println(code);
				//System.out.println(quantity);
				//System.out.println(semester);
				//System.out.println(date);


				// add to both catalog and table data
				//
				courseDSC.addCourse(code, quantity, semester, date);

				//tableData.add(new Product(id, name, price, onSale));

				System.out.println("Entry Added to database please restart application");
				System.out.println(courseDSC.getAllCourses());

				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.setTitle("Added course");
				alert.setContentText("Your Inputs have been added please restart the application to see the effects");
				alert.showAndWait().ifPresent((btnType) -> {
				});

			}
			catch(Exception exception)
			{
				throw new RuntimeException(exception.getMessage());
			}
		});
		clearBT.setOnAction(p ->
		{
			subjectChoiceBox.setValue(null);
			semesterChoiceBox.setValue(null);
			quantityTF.clear();
			datePicker.getEditor().clear();

			Alert cleared = new Alert(Alert.AlertType.CONFIRMATION);
			cleared.setTitle("Inputs Cleared");
			cleared.setContentText("Your Inputs have been cleared");
			cleared.showAndWait().ifPresent((btnType) -> {
			});
		});

		deleteBT.setOnAction(e ->
		{
			// ask for confirmation
			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
			confirm.setTitle("Really Delete?");
			confirm.setContentText("Are you sure you want to delete this Course?");

			Optional<ButtonType> result = confirm.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK)
			{
				try
				{
				// todo: delete the selected music album
				Course course = tableView.getSelectionModel().getSelectedItem();
				//System.out.println(course);

				// remove() can throw Exception

					// Must call the DSC method first
					courseDSC.removeCourse(course.getId());
					//tableData.remove(course);

					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setTitle("Deleted course");
					alert.setContentText("Your course have been deleted please restart the application to see the effects");
					alert.showAndWait().ifPresent((btnType) -> {
					});
				}
				catch(Exception exception)
				{
					throw new RuntimeException(exception.getMessage());
				}
			}
		});

		updateBT.setOnAction((e) ->
		{
			Stage updateStage = new Stage();
			// set title for the stage
			updateStage.setTitle("Update Quantity");

			// create a tile pane
			TilePane r = new TilePane();

			// create a text input dialog
			//TextInputDialog td = new TextInputDialog("enter any text");
			TextField enterQuantity = new TextField();
			enterQuantity.setMaxWidth(200);

			// setHeaderText
			Label ql = new Label("Number OF Students");

			// create a button
			Button saveUpdate = new Button("save");
			Button cancel = new Button("cancel");

			// add button and label
			r.getChildren().addAll(ql, enterQuantity, saveUpdate, cancel);

			// create a scene
			Scene sc = new Scene(r, 300, 100);

			// set the scene
			updateStage.setScene(sc);

			updateStage.show();

			//Update Stuff
			Course course = tableView.getSelectionModel().getSelectedItem();
			cancel.setOnAction((l) ->
			{
				updateStage.close();
			});


			saveUpdate.setOnAction((l) ->
			{
				try
				{
					courseDSC.updateNumbersOfStudents(course.getId(), Integer.parseInt(enterQuantity.getText()));
					/*
					for(Course c: tableData)
					{
							// Refresh the column to see the change
							// (This is a work around)
							tableView.getColumns().get(0).setVisible(false);
							tableView.getColumns().get(0).setVisible(true);

							break;
					}

					 */

					//System.out.println("\nAFTER Update / table data:\n" + tableData);
					System.out.println("Student Quantity Successfully Updated please restart to see the change made");

					Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
					alert.setTitle("Updated course");
					alert.setContentText("Your course has been updated please restart the application to see the effects");
					alert.showAndWait().ifPresent((btnType) -> {
					});
				}
				catch(Exception exception)
				{
					throw new RuntimeException(exception.getMessage());
				}

			});
		});


		/* TODO 2-13 - TO COMPLETE ****************************************
		 * add input controls and container(s)
		 * - Item will list item data from the data source controller list
		 *   all items method
		 * - Semester will list all semesters defined in the data source
		 *   controller SEMESTER enum
		 * - NumberOfStudents: a texf field, self descriptive
		 * - CANCEL button: clears all input controls
		 * - SAVE button: sends the new subject information to the data source
		 *   controller add course method; be mindful of exceptions when any
		 *   or all of the input controls are empty upon SAVE button click
		 */	
		
		// =====================================================================
		// SET UP the Stage
		// =====================================================================
		// Create scene and set stage
		VBox root = new VBox();

		/* TODO 2-14 - TO COMPLETE ****************************************
		 * - add all your containers, controls to the root
		 */

		root.getChildren().add(filterHBox);

		//	Display table view(by adding it to the root node)
		root.getChildren().add(tableView);

		root.getChildren().add(buttonHBox);

		root.getChildren().add(addContents);

		root.getChildren().add(extraButtons);


		root.setStyle(
			"-fx-font-size: 20;" //+ "-fx-alignment: center;"
		);

		Scene scene = new Scene(root, 900, 1000);
		stage.setScene(scene);


	}

	public void stop() throws Exception {
		/* TODO 2-15 - TO COMPLETE ****************************************
		 * call the data source controller database disconnect method
		 * NOTE: that database disconnect method throws exception
		 */
		courseDSC.disconnect();
	}
}





