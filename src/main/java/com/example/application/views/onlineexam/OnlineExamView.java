package com.example.application.views.onlineexam;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@PageTitle("Online Exam")
@Route(value = " ")
@RouteAlias(value = "")
public class OnlineExamView extends VerticalLayout {
    private final LoginForm login = new LoginForm();
    private final Button redirect = new Button("Admin Login");
    private String enroll;
    private String ekey;
    public OnlineExamView(){
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Enter Credentials");
        i18n.getForm().setUsername("Enrollment Number");
        i18n.getForm().setPassword("Exam Key");
        i18n.getForm().setSubmit("Proceed");
        i18n.getForm().setForgotPassword("Forgot Exam key");
        i18n.getErrorMessage().setTitle("Login failed");
        i18n.getErrorMessage().setMessage("Incorrect Enrollment Number or Exam Key");
        login.setI18n(i18n);
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        //login.setAction("home");
        login.addLoginListener(event -> {
            enroll = event.getUsername();
            ekey = event.getPassword();
            if (authenticate(enroll, ekey)) {
                VaadinSession.getCurrent().setAttribute("key", enroll);
                UI.getCurrent().navigate("ExamDetailsView");
            } else {
                Notification.show("Invalid Enrollment Number or Key");
                login.setError(true);
                //UI.getCurrent().navigate(" ");
            }
        });
        redirect.addClickListener(buttonClickEvent -> {
            UI.getCurrent().navigate("/admin");
        });
        add(new H1("Online Examination"), login, redirect);
    }

    //    public void onLogin(LoginForm.LoginEvent event) {
//        String enroll = event.getUsername();
//        String key = event.getPassword();
//        // Perform any necessary actions with the username and password values
//    }
    private boolean authenticate(String username, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM students WHERE enroll = ? AND ekey = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            Notification.show("Database error");
            return false;
        }
    }

}

@Route(value = "/admin")
class LoginAdmin extends VerticalLayout {
    private final LoginForm login = new LoginForm();
    private final Button redirect = new Button("Student Login");
    private String enroll;
    private String ekey;
    public LoginAdmin(){
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Enter Credentials");
        i18n.getForm().setUsername("Admin ID");
        i18n.getForm().setPassword("Password");
        i18n.getForm().setSubmit("Proceed");
        i18n.getForm().setForgotPassword("Forgot Password");
        i18n.getErrorMessage().setTitle("Login failed");
        i18n.getErrorMessage().setMessage("Incorrect Admin ID or Password");
        login.setI18n(i18n);
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        //login.setAction("home");
        login.addLoginListener(event -> {
            enroll = event.getUsername();
            ekey = event.getPassword();
            if (authenticate(enroll, ekey)) {
                VaadinSession.getCurrent().setAttribute("key", enroll);
                UI.getCurrent().navigate("adminHome");
            } else {
                Notification.show("Invalid Admin ID or Password");
                login.setError(true);
                //UI.getCurrent().navigate(" ");
            }
        });
        redirect.addClickListener(buttonClickEvent -> {
//            ProgressBar progressBar = new ProgressBar();
//            progressBar.setIndeterminate(true);
//            progressBar.setVisible(true);
//            Div progressBarLabel = new Div();
//            progressBarLabel.setText("Loading.....");
//
//            Dialog dialog = new Dialog();
//            dialog.add(progressBarLabel, progressBar);
//            dialog.open();
//
//            Timer timer = new Timer();
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    dialog.close();
//                    timer.cancel(); // cancel the timer so it won't execute again
//                }
//            }, 5000); // 5000 milliseconds = 5 seconds
//            //UI.getCurrent().getPage().setLocation(" ");
//
//            dialog.open();
//
//            // Perform some long running operation
//            // ...
//
//            // Redirect to the new page after the operation is complete
//            UI.getCurrent().getPage().setLocation(" ");
            UI.getCurrent().navigate(" ");
        });
        add(new H1("Admin Login"), login, redirect);
    }

    //    public void onLogin(LoginForm.LoginEvent event) {
//        String enroll = event.getUsername();
//        String key = event.getPassword();
//        // Perform any necessary actions with the username and password values
//    }
    private boolean authenticate(String username, String password) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM admin WHERE id = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            Notification.show("Database error");
            return false;
        }
    }

}

