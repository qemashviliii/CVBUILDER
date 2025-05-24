package com.example.cvbuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.File;
import java.sql.*;


public class Menu extends Application {

    //JDBC connection
    private static String url = "jdbc:postgresql://localhost:5432/CVBuilderApplication";
    private static String user = "postgres";
    private static String password = "Grafikaferwera12.";
    private static Connection connection;
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }




    private Stage primaryStage;
    //Function to check if user exists in database
    private boolean isRegistered(String username,String email){
        String checkQuery = "SELECT 1 FROM Accounts WHERE username = ? OR email = ? ";
        try (Connection connection1 = getConnection();
             PreparedStatement statement = connection1.prepareStatement(checkQuery)) {

            statement.setString(1, username);
            statement.setString(2, email);

            ResultSet rs = statement.executeQuery();
            connection1.close();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public void start(Stage primaryStage) {

        this.primaryStage=primaryStage;
         ShowMenu();
         this.primaryStage.show();

    }

    // Start Page
    private void ShowMenu() {
        //CSS
        Label titleLabel = new Label("Main Menu");
        titleLabel.getStyleClass().add("label-title");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        loginButton.getStyleClass().add("button");
        registerButton.getStyleClass().add("button");

        loginButton.setOnAction(e -> ShowLoginPage());
        registerButton.setOnAction(e -> ShowRegisterPage());

        VBox buttonBox = new VBox(10, loginButton, registerButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox form = new VBox(20, titleLabel, buttonBox);
        form.setAlignment(Pos.CENTER);
        form.getStyleClass().add("login-container");

        StackPane root = new StackPane(form);
        root.setPrefSize(400, 250);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("menu.css").toExternalForm());

        primaryStage.setScene(scene);
    }
  //Function to login in your CV
    private void ShowLoginPage() {
        //CSS
        Label titleLabel = new Label("Login");
        titleLabel.getStyleClass().add("label-title");

        TextField userField = new TextField();
        userField.setPromptText("Username Or Email");
        userField.getStyleClass().add("text-field");

        TextField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("password-field");

        Button loginButton = new Button("Login");
        Button menuButton = new Button("Go Back");
        loginButton.getStyleClass().add("button");
        menuButton.getStyleClass().add("button");

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("label-error");

        HBox buttonBox = new HBox(10, loginButton, menuButton);
        buttonBox.setAlignment(Pos.CENTER);



        VBox form = new VBox(15, titleLabel, userField, passwordField, buttonBox, messageLabel);
        form.setAlignment(Pos.CENTER);
        form.getStyleClass().add("login-container");

        StackPane root = new StackPane(form);
        root.setPrefSize(400, 300);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());



        menuButton.setOnAction(e -> ShowMenu());
        //Login Function
        loginButton.setOnAction(e -> {
            String user = userField.getText();
            String password = passwordField.getText();

            if (user.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please fill fields");
                return;
            }

            String query = "SELECT 1 FROM Accounts WHERE (username = ? OR email = ?) AND password = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, user);
                stmt.setString(2, user);
                stmt.setString(3, password);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        messageLabel.setText("Login successful");
                        ShowCVPage();
                    } else {
                        messageLabel.setText("Invalid Input");
                    }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                messageLabel.setText("Error..");
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();




    }
 //Function to Register your account
    private void ShowRegisterPage() {
        //CSS

        Label titleLabel = new Label("Register Account");
        titleLabel.getStyleClass().add("label-title");

        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("label-error");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("text-field");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("password-field");

        Button registerButton = new Button("Register");
        Button menuButton = new Button("Back to Menu");

        registerButton.getStyleClass().add("button");
        menuButton.getStyleClass().add("button");

        HBox buttonBox = new HBox(10, registerButton, menuButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox form = new VBox(15, titleLabel, usernameField, emailField, passwordField, buttonBox,messageLabel);
        form.setAlignment(Pos.CENTER);
        form.getStyleClass().add("login-container");

        StackPane root = new StackPane(form);
        root.setPrefSize(400, 350);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        menuButton.setOnAction(e -> ShowMenu());

  //Main Register function
        registerButton.setOnAction(e->{
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please fill fields");
                return;
            }

            if (isRegistered(username, email)) {
                messageLabel.setText("This User or Email is already used");
                return;
            }


           //Inserting your inputs in database
            String insertQuery = "INSERT INTO Accounts (username, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement statement = getConnection().prepareStatement(insertQuery)){
                statement.setString(1,username);
                statement.setString(2,email);
                statement.setString(3,password);
                statement.executeUpdate();
                messageLabel.setText("Added successfully");
                getConnection().close();
                ShowCVPage();
            }catch (SQLException exception){
                exception.printStackTrace();
                messageLabel.setText("Oopss.. Something went wrong");
            }
        });





        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void ShowCVPage() {
     //creating fields and labels
        Label titleLabel = new Label("CV BUILDER");
        titleLabel.getStyleClass().add("cv-title");

        Label fullNameLabel = new Label("FULL NAME:");
        TextField fullNameField = new TextField();

        Label addressLabel = new Label("ADDRESS:");
        TextField addressField = new TextField();

        Label contactLabel = new Label("CONTACT:");
        TextField contactField = new TextField();

        Label emailLabel = new Label("EMAIL:");
        TextField emailField = new TextField();

        //image upload
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);

        Button selectImageButton = new Button("Upload Image");
        selectImageButton.getStyleClass().add("button");

        selectImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                try {
                    Image image = new Image(selectedFile.toURI().toString());
                    imageView.setImage(image);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        HBox imageBox = new HBox(10, selectImageButton, imageView);
        imageBox.setAlignment(Pos.CENTER_LEFT);

        Label skillsLabel = new Label("SKILLS");
        TextField skill1Field = new TextField();
        TextField skill2Field = new TextField();

        Label workExpLabel = new Label("WORK EXPERIENCE:");
        TextField workExpField = new TextField();

        Label collegeLabel = new Label("COLLEGE/UNIVERSITY:");
        TextField collegeField = new TextField();

        Label qual1Title = new Label("QUALIFICATION 1:");
        TextField qualAField = new TextField();

        Label qual2Title = new Label("QUALIFICATION 2:");
        TextField qualBField = new TextField();

        Button generateButton = new Button("CREATE");
        generateButton.getStyleClass().add("button");
         //placement of buttons, fields, labels

        GridPane leftPane = new GridPane();
        leftPane.setVgap(10);
        leftPane.setHgap(10);
        leftPane.add(fullNameLabel, 0, 0);
        leftPane.add(fullNameField, 1, 0);
        leftPane.add(addressLabel, 0, 1);
        leftPane.add(addressField, 1, 1);
        leftPane.add(contactLabel, 0, 2);
        leftPane.add(contactField, 1, 2);
        leftPane.add(emailLabel, 0, 3);
        leftPane.add(emailField, 1, 3);
        leftPane.add(imageBox, 0, 4, 2, 1);
        leftPane.getStyleClass().add("cv-left");

        GridPane rightPane = new GridPane();
        rightPane.setVgap(10);
        rightPane.setHgap(10);
        rightPane.add(skillsLabel, 0, 0);
        rightPane.add(skill1Field, 0, 1);
        rightPane.add(skill2Field, 0, 2);
        rightPane.add(workExpLabel, 0, 3);
        rightPane.add(workExpField, 1, 3);
        rightPane.add(collegeLabel, 0, 4);
        rightPane.add(collegeField, 1, 4);
        rightPane.add(qual1Title, 0, 5);
        rightPane.add(qualAField, 1, 5);
        rightPane.add(qual2Title, 0, 6);
        rightPane.add(qualBField, 1, 6);
        rightPane.getStyleClass().add("cv-right");
        HBox content = new HBox(30, leftPane, rightPane);
        content.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20, titleLabel, content, generateButton);
        layout.setAlignment(Pos.CENTER);
        layout.getStyleClass().add("cv-container");

        Scene scene = new Scene(layout, 900, 500);
        scene.getStylesheets().add(getClass().getResource("cv.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }




    public static void main(String[] args) {
        launch(args);
    }


}
