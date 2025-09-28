package guiUserLogin;

import database.Database;
import entityClasses.User;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ControllerUserLogin {
	
	/*-********************************************************************************************

	The User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/


	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	private static Stage theStage;	
	
	/**********
	 * <p> Method: public doLogin() </p>
	 * * <p> Description: This method is called when the user has clicked on the Login button. This
	 * method checks the username and password to see if they are valid.  If so, it then logs that
	 * user in my determining which role to use.
	 * * The method reaches batch to the view page and to fetch the information needed rather than
	 * passing that information as parameters.
	 * */	
	protected static void doLogin(Stage ts) {
	    theStage = ts;
	    String username = ViewUserLogin.text_Username.getText();
	    String password = ViewUserLogin.text_Password.getText();
	    
	    // Fetch the user's details without checking the password first
	    User user = theDatabase.getUserByUsername(username);

	    if (user == null) {
	        // The user doesn't exist
	        ViewUserLogin.alertUsernamePasswordError.setContentText(
	                "Incorrect username/password. Try again!");
	        ViewUserLogin.alertUsernamePasswordError.showAndWait();
	        return;
	    }
	    System.out.println("*** Username is valid");
	    
	    // 1. Check against the permanent password
	    if (password.compareTo(user.getPassword()) == 0) {
	        System.out.println("*** Password is valid for this user");
	        goToUserHome(user);
	    } 
	    // 2. If permanent password check fails, check against the one-time password
	    else if (user.getOneTimePassword() != null && password.compareTo(user.getOneTimePassword()) == 0) {
	        System.out.println("*** One-time password is valid for this user");
	        
	        // Nullify the one-time password in the database so it can't be reused
	        theDatabase.nullifyOneTimePassword(user.getEmailAddress());
	        
	        // Call the new method to display the password reset popup
	        showPasswordResetPopup(user);
	        
	        // Do NOT call goToUserHome(user) here. The password reset popup will handle the next step.
	        
	    } else {
	        // Both checks failed
	        ViewUserLogin.alertUsernamePasswordError.setContentText(
	                "Incorrect username/password. Try again!");
	        ViewUserLogin.alertUsernamePasswordError.showAndWait();
	        return;
	    }
	}

	private static void goToUserHome(User user) {
		// See which home page dispatch to use
		int numberOfRoles = theDatabase.getNumberOfRoles(user);		
		System.out.println("*** The number of roles: "+ numberOfRoles);
		
		if (numberOfRoles == 1) {
			// Single Account Home Page - The user has no choice here
			
			// Admin role
			if (user.getAdminRole()) {
				if (theDatabase.loginAdmin(user)) {
					guiAdminHome.ViewAdminHome.displayAdminHome(theStage, user);
				}
			} else if (user.getNewRole1()) {
				if (theDatabase.loginRole1(user)) {
					guiRole1.ViewRole1Home.displayRole1Home(theStage, user);
				}
			} else if (user.getNewRole2()) {
				if (theDatabase.loginRole2(user)) {
					guiRole2.ViewRole2Home.displayRole2Home(theStage, user);
				}
				// Other roles
			} else {
				System.out.println("***** UserLogin goToUserHome request has an invalid role");
			}
		} else if (numberOfRoles > 1) {
			// Multiple Account Home Page - The user chooses which role to play
			System.out.println("*** Going to displayMultipleRoleDispatch");
			guiMultipleRoleDispatch.ViewMultipleRoleDispatch.
				displayMultipleRoleDispatch(theStage, user);
		}
	}
	
		
	/**********
	 * <p> Method: setup() </p>
	 * * <p> Description: This method is called to reset the page and then populate it with new
	 * content.</p>
	 * */
	protected static void doSetupAccount(Stage theStage, String invitationCode) {
		guiNewAccount.ViewNewAccount.displayNewAccount(theStage, invitationCode);
	}

	
	/**********
	 * <p> Method: public performQuit() </p>
	 * * <p> Description: This method is called when the user has clicked on the Quit button.  Doing
	 * this terminates the execution of the application.  All important data must be stored in the
	 * database, so there is no cleanup required.  (This is important so we can minimize the impact
	 * of crashed.)
	 * */	
	protected static void performQuit() {
		System.out.println("Perform Quit");
		System.exit(0);
	}
	
	protected static void showPasswordResetPopup(User user) {
	    Stage popupStage = new Stage();
	    popupStage.initModality(Modality.APPLICATION_MODAL);
	    popupStage.setTitle("Reset Password");

	    // Layout for the popup
	    VBox layout = new VBox(10);
	    layout.setAlignment(Pos.CENTER);
	    layout.setPadding(new Insets(20));

	    Label prompt = new Label("Please set a new permanent password.");
	    PasswordField newPasswordField = new PasswordField();
	    newPasswordField.setPromptText("New Password");

	    PasswordField confirmPasswordField = new PasswordField();
	    confirmPasswordField.setPromptText("Confirm New Password");

	    Label errorLabel = new Label("");
	    errorLabel.setStyle("-fx-text-fill: red;");

	    Button changePasswordButton = new Button("Change Password");
	    changePasswordButton.setOnAction(event -> {
	        String newPass = newPasswordField.getText();
	        String confirmPass = confirmPasswordField.getText();

	        if (newPass.isEmpty() || confirmPass.isEmpty()) {
	            errorLabel.setText("Password cannot be empty.");
	            return;
	        }

	        if (!newPass.equals(confirmPass)) {
	            errorLabel.setText("Passwords do not match.");
	            return;
	        }

	        // Check the password against your custom recognizer logic
	        if (!passwordRecognizer(newPass)) {
	            errorLabel.setText("Password must be 8-32 characters, and contain a capital, a lowercase, a number, and a special character.");
	            return;
	        }

	        // If all validations pass, update the password in the database
	        boolean success = theDatabase.updatePermanentPassword(user.getEmailAddress(), newPass);
	        if (success) {
	            Alert successAlert = new Alert(AlertType.INFORMATION);
	            successAlert.setTitle("Success");
	            successAlert.setHeaderText("Password Changed Successfully");
	            successAlert.setContentText("Your permanent password has been updated. Please log in with your new password.");
	            successAlert.showAndWait();
	            
	            // Close the popup window
	            popupStage.close();

	            // Redirect to the login page so the user can log in with their new password
	            guiUserLogin.ViewUserLogin.displayUserLogin(theStage);

	        } else {
	            errorLabel.setText("An error occurred. Please try again.");
	        }
	    });

	    layout.getChildren().addAll(prompt, newPasswordField, confirmPasswordField, errorLabel, changePasswordButton);
	    Scene scene = new Scene(layout, 350, 250);
	    popupStage.setScene(scene);
	    popupStage.showAndWait();
	}
	
	protected static boolean passwordRecognizer(String password) {
	    
	    // Check password length (between 8 and 32 characters)
	    if (password.length() < 8 || password.length() > 32) {
	        return false;
	    }

	    // Use regular expressions to check for character types
	    // Matches if the password contains at least one uppercase letter
	    if (!password.matches(".*[A-Z].*")) {
	        return false;
	    }
	    
	    // Matches if the password contains at least one lowercase letter
	    if (!password.matches(".*[a-z].*")) {
	        return false;
	    }
	    
	    // Matches if the password contains at least one number (digit)
	    if (!password.matches(".*\\d.*")) {
	        return false;
	    }
	    
	    // Matches if the password contains at least one special character from your list
	    // The backslashes are for escaping special characters within the regex itself.
	    if (!password.matches(".*[~!@#$%^&*()_+\\-=\\{\\}\\[\\]|\\\\:;\"'<>,.?\\/].*")) {
	        return false;
	    }
	    
	    // If all checks pass, the password is valid
	    return true;
	}

}