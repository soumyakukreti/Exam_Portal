package com.example.application.views.onlineexam;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Route(value = "/ques")
class DBMSTestView extends VerticalLayout {
    private ScheduledFuture<?> scheduledFuture;
    private AtomicInteger remainingTime;
    private Label timerLabel;
    int score = 0;
    public DBMSTestView() throws SQLException {


        //Add Heading
        Label heading = new Label("DBMS Examination");
        heading.getStyle().set("font-size", "40px");

        // Create sections A, B, and C tabs
        Tabs tabs = new Tabs(new Tab("Section A"), new Tab("Section B"), new Tab("Section C"));
        tabs.setOrientation(Tabs.Orientation.HORIZONTAL);

        // Create accordion components for each section
        Accordion sectionAAccordion = createSectionAAccordion();
        Accordion sectionBAccordion = createSectionBAccordion();
        Accordion sectionCAccordion = createSectionCAccordion();

        // Create submit button
        Button submitButton = new Button("Submit");
        submitButton.addClickListener (event -> {
            // create confirmation dialog box
            Dialog confirmationDialog = new Dialog();
            confirmationDialog.setCloseOnEsc(true);
            confirmationDialog.setCloseOnOutsideClick(true);
            confirmationDialog.add(new Text("Are you sure you want to submit?"));

            // create "yes" button on confirmation dialog box
            Button yesButton = new Button("Yes", eventYes -> {
              try {
                  saveToDatabase(score);
              }

              catch (PSQLException e) {
                  confirmationDialog.close();
                  e.printStackTrace();
                  Dialog err = new Dialog();
                  err.add(new Text("Multiple submissions not permitted"));
                  err.open();
                  err.setCloseOnOutsideClick(true);

              }
              catch (SQLException e) {
                  e.printStackTrace();
                  Dialog err = new Dialog();
                  err.add(new Text("Error saving test. Contact admin"));
                  err.open();
                  err.setCloseOnOutsideClick(true);
              }
              UI.getCurrent().navigate("hello");
              confirmationDialog.close();

            });

            // create "no" button on confirmation dialog box
            Button noButton = new Button("No", eventNo -> confirmationDialog.close());

            // changed colours of buttons
            yesButton.getStyle().set("background-color", "#007bff");
            yesButton.getStyle().set("color", "white");
            noButton.getStyle().set("background-color", "white");
            noButton.getStyle().set("color", "#007bff");

            // add "yes" and "no" buttons to horizontal layout
            HorizontalLayout buttonsLayout = new HorizontalLayout();
            buttonsLayout.add(yesButton, noButton);
            confirmationDialog.add(buttonsLayout);

            confirmationDialog.open();
        });

        //Add help box
        Dialog helpDialog = new Dialog();
        TextField Name = new TextField("Name: ");
        TextField enroll = new TextField("Enrollment Number: ");
        TextField depatment = new TextField("Department: ");
        TextArea des = new TextArea("Query: ");
        des.setHeight("200px");
        des.setWidth("300px");
        Button submitQueryButton = new Button("Submit Query", event -> {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO request (namee, enrolll, department, query) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, Name.getValue());
                statement.setString(2, enroll.getValue());
                statement.setString(3, depatment.getValue());
                statement.setString(4, des.getValue());
                statement.executeUpdate();
                Dialog err = new Dialog();
                err.add(new Text("Query sent to admin"));
                err.open();
                err.setCloseOnOutsideClick(true);
            } catch (SQLException e) {
                e.printStackTrace();
                Dialog err = new Dialog();
                err.add(new Text("Error sending query"));
                err.open();
                err.setCloseOnOutsideClick(true);
            }
        });

        VerticalLayout content = new VerticalLayout(Name, enroll, depatment, des, submitQueryButton);
        helpDialog.add(content);
        Button helpButton = new Button("Help", event -> helpDialog.open());
        helpButton.getElement().getStyle().set("position", "fixed");
        helpButton.getElement().getStyle().set("top", "10px");
        helpButton.getElement().getStyle().set("right", "10px");


        submitButton.getElement().getStyle().set("position", "fixed");
        submitButton.getElement().getStyle().set("bottom", "150px");
        submitButton.getElement().getStyle().set("right", "10px");

        // Add the accordion components to their respective tabs
        tabs.addSelectedChangeListener(event -> {
            switch (event.getSelectedTab().getLabel()) {
                case "Section A" -> {
                    add(sectionAAccordion);
                    remove(sectionBAccordion, sectionCAccordion);
                }
                case "Section B" -> {
                    add(sectionBAccordion);
                    remove(sectionAAccordion, sectionCAccordion);
                }
                case "Section C" -> {
                    add(sectionCAccordion);
                    remove(sectionAAccordion, sectionBAccordion);
                }
                default -> {
                }
            }
        });

        // Select the first tab by default
        tabs.setSelectedIndex(0);

        // Add the components to the layout
        VerticalLayout contentLayout = new VerticalLayout();
        add(heading, helpButton, tabs, submitButton);


        //timer
        // Add timer label
        timerLabel = new Label();
        timerLabel.getStyle().set("font-size", "18px");

        // Add timer
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        remainingTime = new AtomicInteger(2 * 60); // 2 minutes
        scheduledFuture = executorService.scheduleAtFixedRate(() -> {
            // Update remaining time
            int time = remainingTime.decrementAndGet();

            // Display remaining time
            getUI().ifPresent(ui -> ui.access(() -> {
                timerLabel.setText("Time remaining: " + time + " seconds");

                // Display dialog box when time is up
                if (time == 0) {
                    Dialog dialog = new Dialog();
                    dialog.add(new Text("Time is up!"));
                    dialog.open();
                    scheduledFuture.cancel(false);
                    executorService.shutdown();
                    try{
                        saveToDatabase(score);
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                        Dialog err = new Dialog();
                        err.add(new Text("Error submitting test. Contact admin"));
                        err.open();
                        err.setCloseOnOutsideClick(true);
                    }
                    UI.getCurrent().navigate("/hello");
                    dialog.close();
                }
            }));
        }, 0, 1, TimeUnit.SECONDS); // schedule to run every 1 second

//        add(timerLabel);
        timerLabel.getElement().getStyle().set("position", "fixed");
        timerLabel.getElement().getStyle().set("bottom", "150px");
        timerLabel.getElement().getStyle().set("left", "10px");
        add(timerLabel);
    }
    private void saveToDatabase(int score) throws SQLException {
        String name = "", enroll = "", department = "";
        String username = (String) VaadinSession.getCurrent().getAttribute("key");

        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT name, enroll, department FROM students WHERE enroll = ?");
        statement.setString(1, username);
        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            name = rs.getString("name");
            enroll = rs.getString("enroll");
            department = rs.getString("department");
        }

        statement = connection.prepareStatement("INSERT INTO result (name, enroll, department, score) VALUES (?, ?, ?, ?)");
        statement.setString(1, name);
        statement.setString(2, enroll);
        statement.setString(3, department);
        statement.setInt(4, score);
        statement.executeUpdate();

    }

    private Accordion createSectionAAccordion() {
        Accordion accordion = new Accordion();
        accordion.add("Question 1: What is the full form of DBMS?", createRadioGroup("Database Management System", "Data Backup Management System", "Data of Binary Management System", "Database Management Service", "Database Management System"));
        accordion.add("Question 2: What is a database?", createRadioGroup("Organized collection of data or information that can be accessed, updated, and managed", "Single data file on disk", "Database can only be accessed by one user at a time", "Data that is randomly accessed", "Database can only be accessed by one user at a time"));
        accordion.add("Question 3: Which of the following is not a type of database?", createRadioGroup("Hierarchical", "Network", "Distributed", "Decentralized", "Decentralized"));
        accordion.add("Question 4: Which of the following is not an example of DBMS", createRadioGroup("MySQL", "Microsoft Acess", "IBM DB2", "Google", "Google"));
        accordion.add("Question 5: Which of the following is a feature of DBMS?", createRadioGroup("Minimum Duplication and Redundancy of Data", "High Level of Security", "Single-user Access only", "Support ACID Property", "Single-user Access only"));
        return accordion;
    }

    private Accordion createSectionBAccordion() {
        Accordion accordion = new Accordion();
        accordion.add("Question 1: What is DBMS?", createRadioGroup("DBMS stores, modifies and retrieves data", "DBMS manages networks", "DBMS creates web applications", "DBMS manages documents","DBMS stores, modifies and retrieves data" ));
        accordion.add("Question 2: Which type of data can be stored in the database?", createRadioGroup("All of the above", "Numeric data", "Textual data", "Audio data", "All of the above" ));
        accordion.add("Question 3: Which of the following is not a function of the database?", createRadioGroup("Managing stored data", "Manipulating data", "Security for stored data", "Analysing code", "Analysing code"));
        accordion.add("Question 4: Which of the following is a function of the DBMS?", createRadioGroup("Storing data", "Providing multi-users access control", "Data Integrity", "All of the above", "All of the above"));
        accordion.add("Question 5: Which of the following is a component of the DBMS?", createRadioGroup("Data", "Data Languages", "Data Manager", "All of the above", "All of the above"));
        return accordion;
    }

    private Accordion createSectionCAccordion() {
        Accordion accordion = new Accordion();
        accordion.add("Question 1: Who created the first DBMS?", createRadioGroup("Edgar Frank Codd", "Charles Batchman", "James Gosling", "Grace Hopper", "Charles Batchman"));
        accordion.add("Question 2: In which of the following formats data is stored in the database management system?", createRadioGroup("Table", "Graph", "Tree", "List", "Table"));
        accordion.add("Question 3: What is information about data called?", createRadioGroup("Hyper data", "Tera data", "Meta data", "Relations", "Meta data"));
        accordion.add("Question 4: What does an RDBMS consist of?", createRadioGroup("Collection of Records", "Collection of Keys", "Collection of Tables", "Collection of Fields", "Collection of Tables"));
        accordion.add("Question 5: _________ is a set of one or more attributes taken collectively to uniquely identify a record.", createRadioGroup("Primary Key", "Foreign key", "Super key", "Candidate key", "Super key"));
        return accordion;

    }

    private RadioButtonGroup<String> createRadioGroup(String option1, String option2, String option3, String option4, String correctAnswer) {
        RadioButtonGroup<String> radioGroup = new RadioButtonGroup<>();
        radioGroup.setItems(option1, option2, option3, option4);
//        radioGroup.setValue(correctAnswer);
        radioGroup.setLabel("Select the correct answer:");
        radioGroup.addValueChangeListener(event -> {
            if (event.getValue().equals(correctAnswer)) {
                score += 4;
            }
        });
        return radioGroup;
    }
}
