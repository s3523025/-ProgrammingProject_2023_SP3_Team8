package com.example.programmingproject_chatterbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Connection;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import Classes.User;
import Classes.UserData;

import Classes.Database;
import Classes.FileStore;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import jakarta.servlet.annotation.MultipartConfig;

import static Classes.PasswordValidations.hashPassword;
import static Classes.UserData.users;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

@WebServlet(name = "Registration", value = "/registration")
@MultipartConfig(location = "/", // Temporary directory for file uploads
		fileSizeThreshold = 0, // No file size threshold
		maxFileSize = 20971520, // Maximum file size allowed (in bytes)
		maxRequestSize = 41943040 // Maximum request size (including all files)
)
public class RegistrationServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Retrieve form data from request parameters
		String username = request.getParameter("username");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String ageString = request.getParameter("age");

		// Parse the date from the POST request
		String birthdateString = request.getParameter("age");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date birthdate = null;
		try {
			birthdate = formatter.parse(birthdateString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(birthdate);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		Calendar today = Calendar.getInstance();
		int age = today.get(Calendar.YEAR) - year;
		if (today.get(Calendar.MONTH) < month) {
			age--;
		} else if (today.get(Calendar.MONTH) == month && today.get(Calendar.DAY_OF_MONTH) < day) {
			age--;
		}

		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		Part imagePart = request.getPart("imageUpload");
		InputStream inputStream = imagePart.getInputStream();

		// System.out.println("------password = -------" + password);
		// System.out.println("------confirmpassword = -------" + confirmPassword);
		// Check if passwords match
		if (!password.equals(confirmPassword)) {
			// Passwords do not match, handle the error (e.g., display an error message)
			response.sendRedirect("Registration.jsp?error=Passwords do not match");
			return;
		}

		if (age < 13) {
			response.sendRedirect("Registration.jsp?error=You must be 13 or older to register");
			return;
		}

		// Check if the username or email is already taken
		// Create DB connection
		Database database = new Database();
		// Check if username is taken
		if (database.doesUsernameExist(username)) {
			// Username is already taken, handle the error
			response.sendRedirect("Registration.jsp?error=Username already taken");
			return;
		}
		// Check if email is taken
		if (database.doesEmailExist(email)) {
			// Email is already taken, handle the error
			response.sendRedirect("Registration.jsp?error=Email already taken");
			return;
		}

		// if neither email or password taken, create new user in database

		User newUser = new User();
		newUser.setUsername(username);
		newUser.setFirstName(firstName);
		newUser.setLastName(lastName);
		newUser.setEmail(email);
		newUser.setPassword(hashPassword(password));
		newUser.setAge(ageString);

		// insert user into database
		try {
			database.insertUser(newUser);
		} catch (Exception e) {

			e.printStackTrace();
		}

		try {
			FileStore filestore = new FileStore();
			// Upload the image to Azure Blob Storage
			filestore.uploadFile(inputStream, username);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Redirect to a success page or login page after registration
		System.out.println(users.toString());
		response.sendRedirect("Login.jsp"); // Replace with your success page URL

		// To remove once DB confirmed working
		/*
		 * // Create a new User object. This needs to be sent to a database.
		 * // For now it goes to an array list for testing.
		 * User newUser = new User();
		 * newUser.setUsername(username);
		 * newUser.setFirstName(firstName);
		 * newUser.setLastName(lastName);
		 * newUser.setEmail(email);
		 * newUser.setPassword(hashPassword(password));
		 *
		 * // Add the new user to the ArrayList using UserDataAccess
		 * UserData.addUser(newUser);
		 *
		 * // Redirect to a success page or login page after registration
		 * System.out.println(users.toString());
		 * response.sendRedirect("Login.jsp"); // Replace with your success page URL
		 */
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.getRequestDispatcher("/Registration.jsp").forward(request, response);
	}

	public void destroy() {
	}

}