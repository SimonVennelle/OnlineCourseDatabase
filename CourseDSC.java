//Simon Vennelle, 20765420, CSE3OAD

//package com.test;

import java.sql.*;
import java.util.*;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class CourseDSC
{

	// the date format we will be using across the application
	public static final String DATE_FORMAT = "dd/MM/yyyy";

	/*
		note: Enums are implicitly public static final
	*/
	public enum SEMESTER
	{
		SE1,
		SE2,
		SUM
	};

	private static Connection connection;
	private static Statement statement;
	private static PreparedStatement preparedStatement;

	public static void connect() throws SQLException
	{
		try {
			//Removed as obsolete put back in before submission ******************************
			//Class.forName("com.mysql.jdbc.Driver");

			/* TODO 1-01 - TO COMPLETE                                           -DONE!
			 * change the value of the string for the following 3 lines:
			 * - url
			 * - user
			 * - password
			 */			
			String url = "jdbc:mysql://localhost:3306/coursedb";
			String user = "root";
			String password = "909090";
			
			connection = DriverManager.getConnection(url, user, password);
			statement = connection.createStatement();

  		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}		
	}

	public static void disconnect() throws SQLException
	{
		if(preparedStatement != null) preparedStatement.close();
		if(statement != null) statement.close();
		if(connection != null) connection.close();
	}


	public Subject searchSubject(String code) throws Exception
	{
		String queryString = "SELECT * FROM subject WHERE code = ?";

		/*TODO 1-02 - TO COMPLETE ****************************************            DONE!
		 * - preparedStatement to add argument name to the queryString
		 * - resultSet to execute the preparedStatement query
		 * - iterate through the resultSet result
		 */

		connect();
		preparedStatement = connection.prepareStatement(queryString);
		preparedStatement.setString(1, code);
		ResultSet rs = preparedStatement.executeQuery();


		Subject subject = null;

		if (rs.next()) { // i.e. the subject exists

			/* TODO 1-03 - TO COMPLETE **************************************** 		Done!
			 * - if resultSet has result, get data and create a subject instance
			 */

			subject = new Subject(code, rs.getString(2), rs.getBoolean(3));

		}
		else
		{
			System.out.println(code + " not found in the Subject list");
		}

		// release resource
		disconnect();

		return subject;
	}

	public Course searchCourse(int id) throws Exception
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		String queryString = "SELECT * FROM course WHERE id = ?";
		

		/* TODO 1-04 - TO COMPLETE ****************************************              DONE!
		 * - preparedStatement to add argument name to the queryString
		 * - resultSet to execute the preparedStatement query
		 * - iterate through the resultSet result
		 */

		connect();
		preparedStatement = connection.prepareStatement(queryString);
		preparedStatement.setInt(1,id);
		ResultSet rs = preparedStatement.executeQuery();


		Course course = null;

		if (rs.next()) // i.e. the subject course exists
		{
			/* TODO 1-05 - TO COMPLETE ****************************************                        DOne!
			 * - if resultSet has result, get data and create a subject course instance
			 * - making sure that the subject code from course exists in 
			 *   subject table (use searchSubject method)
			 * - pay attention about parsing the date string to LocalDate
			 */



			course = new Course(id,
					searchSubject(rs.getString(2)),
					LocalDate.parse(rs.getString(3), dtf),
					rs.getInt(4),
					SEMESTER.valueOf(rs.getString(5)));
		}
		else
		{
			System.out.println("Course:" + id + " not found in the Course list");
		}

		// release resource
		disconnect();

		return course;
	}


	public List<Subject> getAllSubjects() throws Exception {
		String queryString = "SELECT * FROM subject";
		

		/* TODO 1-06 - TO COMPLETE ****************************************            DONE
		 * - resultSet to execute the statement query
		 */
		connect();
		ResultSet rs = statement.executeQuery(queryString);


		List<Subject> subjects = new ArrayList<Subject>();

		/* TODO 1-07 - TO COMPLETE ****************************************    DOne
		 * - iterate through the resultSet result, create intance of Subject 
		 *   and add to list subjects
		 */
		Subject subjectInstance;

		while (rs.next())
		{
			subjectInstance = (new Subject(rs.getString(1),
									 rs.getString(2),
									 rs.getBoolean(3)));
			subjects.add(subjectInstance);
		}

		if(subjects.size() == 0)
		{
			System.out.println("There are no subjects in your database");
		}

		// release resource
		disconnect();

		return subjects;
	}


	public List<Course> getAllCourses() throws Exception {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		String queryString = "SELECT * FROM course";
		

		/* TODO 1-08 - TO COMPLETE ****************************************		Done
		 * - resultSet to execute the statement query
		 */
		connect();
		ResultSet rs = statement.executeQuery(queryString);

		List<Course> courses = new ArrayList<Course>();


		/* TODO 1-09 - TO COMPLETE ****************************************
		 * - iterate through the resultSet result, create intance of Subject
		 *   and add to list subjects
		 * - making sure that the subject code from each subject course exists in 
		 *   subject table (use searchSubject method)
		 * - pay attention about parsing the date string to LocalDate
		 */

		Course courseInstance;

		while (rs.next())
		{
			courseInstance = (new Course(
					rs.getInt(1),
					searchSubject(rs.getString(2)),
					LocalDate.parse(rs.getString(3), dtf),
					rs.getInt(4),
					SEMESTER.valueOf(rs.getString(5))));
			courses.add(courseInstance);
		}

		if(courses.size() == 0)
		{
			System.out.println("There are no courses in your database");
		}
		// release resource
		disconnect();

		return courses;
	}


	public int addCourse(String code, int quantity, SEMESTER semester, LocalDate date) throws Exception
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
		//LocalDate date = LocalDate.now();
		String dateStr = date.format(dtf);
		
		// NOTE: should we check if subjectCode (argument code) exists in subject table?
		//		--> adding a course with a non-existing subject code should throw an exception

		Subject codeCheck = searchSubject(code);

		if(codeCheck == null)
		{
			//System.out.println("Error: The subject code you have chosen does not exist!");

			String msg = "Subject id " + code + " does not exist!";
			System.out.println("\nERROR: " + msg);
			throw new Exception(msg);
		}


		String command = "INSERT INTO course VALUES(?, ?, ?, ?, ?)";
		

		/* TODO 1-10 - TO COMPLETE ****************************************
		 * - preparedStatement to add arguments to the queryString
		 * - resultSet to executeUpdate the preparedStatement query
		 */
		connect();
		preparedStatement = connection.prepareStatement(command);
		preparedStatement.setInt(1, 0);
		preparedStatement.setString(2, code);
		preparedStatement.setString(3, dateStr);
		preparedStatement.setInt(4, quantity);
		preparedStatement.setString(5, semester.toString());
		preparedStatement.executeUpdate();


		// retrieving & returning last inserted record id
		ResultSet rs = statement.executeQuery("SELECT LAST_INSERT_ID()");
		rs.next();
		int newId = rs.getInt(1);

		return newId;		
	}

	public Course updateNumbersOfStudents(int id, int numberOfStds) throws Exception
	{
		/* TODO 1-11 - TO COMPLETE ****************************************
		 * - search course subject by id
		 * - check if has quantity is different Zero; if not throw exception
		 *   with adequate error message
		 */

		Course courseCheck = searchCourse(id);

		if(courseCheck == null)
		{
			String msg = "Course with id: " + id + " does not exist!";
			System.out.println("\nERROR: " + msg);
			throw new Exception(msg);
		}

		if(numberOfStds < 1)
		{
			String msg = "You cant have less than 1 students in a course you goose! ";
			System.out.println("\nERROR: " + msg);
			throw new Exception(msg);
		}

		String queryString = 
			"UPDATE course " +
			"SET numberOfStudents = '" + numberOfStds +"' " +
			" WHERE numberOfStudents >= 1 " + 
			"AND id = " + id + ";";
		

		/* TODO 1-12 - TO COMPLETE ****************************************
		 * - statement execute update on queryString
		 * - should the update affect a row search subject course by id and
		 *   return it; else throw exception with adequate error message
		 *
		 * NOTE: method should return instance of subject course
		 */
		connect();
		preparedStatement = connection.prepareStatement(queryString);
		//preparedStatement.setInt(1, numberOfStds);

		preparedStatement.executeUpdate();

		// release resource
		disconnect();

		return searchCourse(id);
	}


	public int removeCourse(int id) throws Exception {
		String queryString = "SELECT COUNT(*) FROM course WHERE id = ?";
		String deleteStatement = "DELETE FROM course WHERE id = ? ";

		/* TODO 1-13 - TO COMPLETE ****************************************
		 * - search subject course by id
		 * - if subject course exists, statement execute update on queryString
		 *   return the value value of that statement execute update
		 * - if subject course does not exist, throw exception with adequate 
		 *   error message
		 *
		 * NOTE: method should return int: the return value of a
		 *		 statement.executeUpdate(...) on a DELETE query
		 */

		Course courseCheck = searchCourse(id);

		if(courseCheck == null)
		{
			String msg = "Course with id: " + id + " does not exist!";
			System.out.println("\nERROR: " + msg);
			throw new Exception(msg);
		}
		connect();
		preparedStatement = connection.prepareStatement(deleteStatement);
		preparedStatement.setInt(1, id);
		//preparedStatement.executeUpdate();


		return preparedStatement.executeUpdate();
	}

	

	// To perform some quick tests	
	public static void main(String[] args) throws Exception {
		CourseDSC myCourseDSC = new CourseDSC();

		myCourseDSC.connect();

		System.out.println("SYSTEM:");

		System.out.println("\nshowing all of each:");

		System.out.println("Search Subject:");
		System.out.println(myCourseDSC.searchSubject("CSE1OOF"));
		System.out.println();

		System.out.println("Search Course:");
		System.out.println(myCourseDSC.searchCourse(1));
		System.out.println(myCourseDSC.searchCourse(2));
		System.out.println(myCourseDSC.searchCourse(5));
		System.out.println();

		System.out.println("Get All Subjects:");
		System.out.println(myCourseDSC.getAllSubjects());
		System.out.println();

		System.out.println("Get All Courses:");
		System.out.println(myCourseDSC.getAllCourses());
		System.out.println();

		System.out.println("Add a course:");
		//int addedId = myCourseDSC.addCourse("CSE5DMI", 75, SEMESTER.SUM, LocalDate.now());
		//System.out.println("added: " + addedId);
		System.out.println();

		System.out.println("Update Courses Student Number:");
		myCourseDSC.updateNumbersOfStudents(6,20);
		System.out.println();

		//System.out.println("deleting " + (addedId - 1) + ": " + (myCourseDSC.removeCourse(addedId - 1) > 0 ? "DONE" : "FAILED"));
		System.out.println("Delete a Course:");
		myCourseDSC.removeCourse(10);
		System.out.println();
		//System.out.println(myCourseDSC.searchCourse(addedId));

		myCourseDSC.disconnect();
	}
}