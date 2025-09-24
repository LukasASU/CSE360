<<<<<<< HEAD:TP1/src/guiAdminHome/ControllerAdminHome.java
package guiAdminHome;

import database.Database;
import entityClasses.User;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import guiTools.EmailAddressRecognizer;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void setOnetimePassword () {
		System.out.println("\n*** WARNING ***: One-Time Password Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("One-Time Password Issue");
		ViewAdminHome.alertNotImplemented.setContentText("One-Time Password Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser() {
		// Ask for the email in a simple input dialog
	    TextInputDialog dialog = new TextInputDialog();
	    dialog.setTitle("Delete User");
	    dialog.setHeaderText("Delete a User Account");
	    dialog.setContentText("Enter the email address of the user to delete:");

	    Optional<String> result = dialog.showAndWait();
	    if (result.isPresent()) {
	        String email = result.get().trim();

	        if (invalidEmailAddress(email)) {
	            return; // already shows an error alert
	        }

	        //admin cant delete his own account
	        if (ViewAdminHome.theUser != null && email.equalsIgnoreCase(ViewAdminHome.theUser.getEmailAddress())) {
	            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
	            alert.setTitle("Delete User Error");
	            alert.setHeaderText("Operation Not Allowed");
	            alert.setContentText("You cannot delete your own account while logged in as admin.");
	            alert.showAndWait();
	            return;
	        }

	        //asking are your sure?
	        javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
	        confirmAlert.setTitle("Confirm Deletion");
	        confirmAlert.setHeaderText("Delete User?");
	        confirmAlert.setContentText("Are you sure you want to delete the user with email:\n\n" + email);

	        Optional<javafx.scene.control.ButtonType> confirmation = confirmAlert.showAndWait();

	        if (confirmation.isPresent() && confirmation.get() == javafx.scene.control.ButtonType.OK) {
	            boolean success = theDatabase.deleteUser(email);

	            if (success) {
	                ViewAdminHome.alertEmailSent.setContentText(
	                    "User with email " + email + " was successfully deleted."
	                );
	                ViewAdminHome.alertEmailSent.showAndWait();
	            } else {
	                ViewAdminHome.alertEmailError.setContentText(
	                    "No user found with email " + email + "."
	                );
	                ViewAdminHome.alertEmailError.showAndWait();
	            }
	        } else {
	            // User cancelled deletion
	            System.out.println("Deletion cancelled by admin.");
	        }
	    }
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listUsers() {
		
		try {
	        // Get list of User objects from the database
	        java.util.List<User> users = theDatabase.getUserListFull(); // This should return List<User>
	        
	        if (users == null || users.isEmpty()) {
	            ViewAdminHome.alertNotImplemented.setTitle("User List");
	            ViewAdminHome.alertNotImplemented.setHeaderText("No Users Found");
	            ViewAdminHome.alertNotImplemented.setContentText("There are currently no users in the system.");
	            ViewAdminHome.alertNotImplemented.showAndWait();
	            return;
	        }

	        // Build a string with all user details
	        StringBuilder userList = new StringBuilder();
	        for (User user : users) {
	            userList.append("Username: ").append(user.getUserName())
	                    .append("\nEmail: ").append(user.getEmailAddress())
	                    .append("\nName: ").append(user.getFirstName())
	                    .append(" ").append(user.getLastName());

	            // Only add preferred name line if it exists
	            if (user.getPreferredFirstName() != null && !user.getPreferredFirstName().isEmpty()) {
	                userList.append("\nPreferred Name: ").append(user.getPreferredFirstName());
	            }

	            // Roles
	            userList.append("\nRoles: ");
	            if (user.getAdminRole()) userList.append("Admin ");
	            if (user.getNewRole1()) userList.append("Role1 ");
	            if (user.getNewRole2()) userList.append("Role2 ");

	            userList.append("\n\n\n\n\n");
	        }

	        // Create a scrollable TextArea to display the users
	        javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(userList.toString());
	        textArea.setEditable(false);
	        textArea.setWrapText(true);
	        textArea.setMinWidth(600);
	        textArea.setMinHeight(400);

	        // Create a new Alert and set the TextArea as its content
	        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
	        alert.setTitle("User List");
	        alert.setHeaderText("Current Users");
	        alert.getDialogPane().setContent(textArea);
	        alert.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

	        // Make the alert resizable
	        alert.getDialogPane().setPrefSize(650, 450);
	        alert.setResizable(true);

	        alert.showAndWait();

	    } catch (Exception e) {
	        System.out.println("\n*** ERROR ***: Unable to list users");
	        e.printStackTrace();
	        ViewAdminHome.alertNotImplemented.setTitle("*** ERROR ***");
	        ViewAdminHome.alertNotImplemented.setHeaderText("Database Issue");
	        ViewAdminHome.alertNotImplemented.setContentText("Unable to retrieve user list.");
	        ViewAdminHome.alertNotImplemented.showAndWait();
	    }
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
	    // Call the FSM recognizer
	    String errorMsg = EmailAddressRecognizer.checkEmailAddress(emailAddress);

	    if (!errorMsg.isEmpty()) {
	        // Show recognizer’s detailed message
	        ViewAdminHome.alertEmailError.setContentText(errorMsg);
	        ViewAdminHome.alertEmailError.showAndWait();
	        return true; // invalid
	    }

	    return false; // valid
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
=======
package guiAdminHome;

import database.Database;
import entityClasses.User;

/*******
 * <p> Title: GUIAdminHomePage Class. </p>
 * 
 * <p> Description: The Java/FX-based Admin Home Page.  This class provides the controller actions
 * basic on the user's use of the JavaFX GUI widgets defined by the View class.
 * 
 * This page contains a number of buttons that have not yet been implemented.  WHen those buttons
 * are pressed, an alert pops up to tell the user that the function associated with the button has
 * not been implemented. Also, be aware that What has been implemented may not work the way the
 * final product requires and there maybe defects in this code.
 * 
 * The class has been written assuming that the View or the Model are the only class methods that
 * can invoke these methods.  This is why each has been declared at "protected".  Do not change any
 * of these methods to public.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2025 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2025-08-17 Initial version
 *  
 */

public class ControllerAdminHome {
	
	/*-*******************************************************************************************

	User Interface Actions for this page
	
	This controller is not a class that gets instantiated.  Rather, it is a collection of protected
	static methods that can be called by the View (which is a singleton instantiated object) and 
	the Model is often just a stub, or will be a singleton instantiated object.
	
	*/

	// Reference for the in-memory database so this package has access
	private static Database theDatabase = applicationMain.FoundationsMain.database;

	/**********
	 * <p> 
	 * 
	 * Title: performInvitation () Method. </p>
	 * 
	 * <p> Description: Protected method to send an email inviting a potential user to establish
	 * an account and a specific role. </p>
	 */
	protected static void performInvitation () {
		// Verify that the email address is valid - If not alert the user and return
		String emailAddress = ViewAdminHome.text_InvitationEmailAddress.getText();
		if (invalidEmailAddress(emailAddress)) {
			return;
		}
		
		// Check to ensure that we are not sending a second message with a new invitation code to
		// the same email address.  
		if (theDatabase.emailaddressHasBeenUsed(emailAddress)) {
			ViewAdminHome.alertEmailError.setContentText(
					"An invitation has already been sent to this email address.");
			ViewAdminHome.alertEmailError.showAndWait();
			return;
		}
		
		// Inform the user that the invitation has been sent and display the invitation code
		String theSelectedRole = (String) ViewAdminHome.combobox_SelectRole.getValue();
		String invitationCode = theDatabase.generateInvitationCode(emailAddress,
				theSelectedRole);
		String msg = "Code: " + invitationCode + " for role " + theSelectedRole + 
				" was sent to: " + emailAddress;
		System.out.println(msg);
		ViewAdminHome.alertEmailSent.setContentText(msg);
		ViewAdminHome.alertEmailSent.showAndWait();
		
		// Update the Admin Home pages status
		ViewAdminHome.text_InvitationEmailAddress.setText("");
		ViewAdminHome.label_NumberOfInvitations.setText("Number of outstanding invitations: " + 
				theDatabase.getNumberOfInvitations());
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: manageInvitations () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void manageInvitations () {
		System.out.println("\n*** WARNING ***: Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Manage Invitations Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Manage Invitations Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: setOnetimePassword () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void setOnetimePassword () {
		System.out.println("\n*** WARNING ***: One-Time Password Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("One-Time Password Issue");
		ViewAdminHome.alertNotImplemented.setContentText("One-Time Password Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: deleteUser () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void deleteUser() {
		System.out.println("\n*** WARNING ***: Delete User Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.setTitle("*** WARNING ***");
		ViewAdminHome.alertNotImplemented.setHeaderText("Delete User Issue");
		ViewAdminHome.alertNotImplemented.setContentText("Delete User Not Yet Implemented");
		ViewAdminHome.alertNotImplemented.showAndWait();
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: listUsers () Method. </p>
	 * 
	 * <p> Description: Protected method that is currently a stub informing the user that
	 * this function has not yet been implemented. </p>
	 */
	protected static void listUsers() {
		/*
		try {
	        // Get list of User objects from the database
	        java.util.List<User> users = theDatabase.getUserListFull(); // This should return List<User>
	        
	        if (users == null || users.isEmpty()) {
	            ViewAdminHome.alertNotImplemented.setTitle("User List");
	            ViewAdminHome.alertNotImplemented.setHeaderText("No Users Found");
	            ViewAdminHome.alertNotImplemented.setContentText("There are currently no users in the system.");
	            ViewAdminHome.alertNotImplemented.showAndWait();
	            return;
	        }

	        // Build a string with all user details
	        StringBuilder userList = new StringBuilder();
	        for (User user : users) {
	            userList.append("Username: ").append(user.getUserName())
	                    .append(", Email: ").append(user.getEmailAddress())
	                    .append(", Name: ").append(user.getPreferredFirstName())
	                    .append(" ").append(user.getLastName())
	                    .append(", Roles: ");
	            
	            if (user.getAdminRole()) userList.append("Admin ");
	            if (user.getNewRole1()) userList.append("Role1 ");
	            if (user.getNewRole2()) userList.append("Role2 ");
	            
	            userList.append("\n");
	        }

	        // Show the users in an alert
	        ViewAdminHome.alertNotImplemented.setTitle("User List");
	        ViewAdminHome.alertNotImplemented.setHeaderText("Current Users");
	        ViewAdminHome.alertNotImplemented.setContentText(userList.toString());
	        ViewAdminHome.alertNotImplemented.showAndWait();

	    } catch (Exception e) {
	        System.out.println("\n*** ERROR ***: Unable to list users");
	        e.printStackTrace();
	        ViewAdminHome.alertNotImplemented.setTitle("*** ERROR ***");
	        ViewAdminHome.alertNotImplemented.setHeaderText("Database Issue");
	        ViewAdminHome.alertNotImplemented.setContentText("Unable to retrieve user list.");
	        ViewAdminHome.alertNotImplemented.showAndWait();
	    }
		*/
		
		try {
	        // Get list of User objects from the database
	        java.util.List<User> users = theDatabase.getUserListFull(); // This should return List<User>
	        
	        if (users == null || users.isEmpty()) {
	            ViewAdminHome.alertNotImplemented.setTitle("User List");
	            ViewAdminHome.alertNotImplemented.setHeaderText("No Users Found");
	            ViewAdminHome.alertNotImplemented.setContentText("There are currently no users in the system.");
	            ViewAdminHome.alertNotImplemented.showAndWait();
	            return;
	        }

	        // Build a string with all user details
	        StringBuilder userList = new StringBuilder();
	        for (User user : users) {
	            userList.append("Username: ").append(user.getUserName())
	                    .append(", Email: ").append(user.getEmailAddress())
	                    .append(", Name: ").append(user.getPreferredFirstName())
	                    .append(" ").append(user.getLastName())
	                    .append(", Roles: ");
	            
	            if (user.getAdminRole()) userList.append("Admin ");
	            if (user.getNewRole1()) userList.append("Role1 ");
	            if (user.getNewRole2()) userList.append("Role2 ");
	            
	            userList.append("\n");
	        }

	        // Create a scrollable TextArea to display the users
	        javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(userList.toString());
	        textArea.setEditable(false);
	        textArea.setWrapText(true);
	        textArea.setMinWidth(600);
	        textArea.setMinHeight(400);

	        // Create a new Alert and set the TextArea as its content
	        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
	        alert.setTitle("User List");
	        alert.setHeaderText("Current Users");
	        alert.getDialogPane().setContent(textArea);
	        alert.getDialogPane().setMinHeight(javafx.scene.layout.Region.USE_PREF_SIZE);

	        // Make the alert resizable
	        alert.getDialogPane().setPrefSize(650, 450);
	        alert.setResizable(true);

	        alert.showAndWait();

	    } catch (Exception e) {
	        System.out.println("\n*** ERROR ***: Unable to list users");
	        e.printStackTrace();
	        ViewAdminHome.alertNotImplemented.setTitle("*** ERROR ***");
	        ViewAdminHome.alertNotImplemented.setHeaderText("Database Issue");
	        ViewAdminHome.alertNotImplemented.setContentText("Unable to retrieve user list.");
	        ViewAdminHome.alertNotImplemented.showAndWait();
	    }
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: addRemoveRoles () Method. </p>
	 * 
	 * <p> Description: Protected method that allows an admin to add and remove roles for any of
	 * the users currently in the system.  This is done by invoking the AddRemoveRoles Page. There
	 * is no need to specify the home page for the return as this can only be initiated by and
	 * Admin.</p>
	 */
	protected static void addRemoveRoles() {
		guiAddRemoveRoles.ViewAddRemoveRoles.displayAddRemoveRoles(ViewAdminHome.theStage, 
				ViewAdminHome.theUser);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: invalidEmailAddress () Method. </p>
	 * 
	 * <p> Description: Protected method that is intended to check an email address before it is
	 * used to reduce errors.  The code currently only checks to see that the email address is not
	 * empty.  In the future, a syntactic check must be performed and maybe there is a way to check
	 * if a properly email address is active.</p>
	 * 
	 * @param emailAddress	This String holds what is expected to be an email address
	 */
	protected static boolean invalidEmailAddress(String emailAddress) {
		if (emailAddress.length() == 0) {
			ViewAdminHome.alertEmailError.setContentText(
					"Correct the email address and try again.");
			ViewAdminHome.alertEmailError.showAndWait();
			return true;
		}
		return false;
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performLogout () Method. </p>
	 * 
	 * <p> Description: Protected method that logs this user out of the system and returns to the
	 * login page for future use.</p>
	 */
	protected static void performLogout() {
		guiUserLogin.ViewUserLogin.displayUserLogin(ViewAdminHome.theStage);
	}
	
	/**********
	 * <p> 
	 * 
	 * Title: performQuit () Method. </p>
	 * 
	 * <p> Description: Protected method that gracefully terminates the execution of the program.
	 * </p>
	 */
	protected static void performQuit() {
		System.exit(0);
	}
}
>>>>>>> cc8a1ad (hope this don't break it):TP1/TP1 Source Code/TP1 Base code/src/guiAdminHome/ControllerAdminHome.java
