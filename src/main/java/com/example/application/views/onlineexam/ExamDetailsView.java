package com.example.application.views.onlineexam;


import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@PageTitle("Exam Details")
@Route("/ExamDetailsView")
class ExamDetailsView extends Div {
    String ins1 = "Please read the following instructions carefully before beginning, Section A, B and C contains 5 questions each. You will have 30 minutes to complete this test. Please read each question carefully and select the best answer from the options provided. Once you have answered all the questions click the submit button to submit your answers. Each question carries 4 marks.";
    private Button but1 = new Button("Start");
    private H1 head = new H1("Exam Details");
    private H1 head1 = new H1("Inside Whle");


    public ExamDetailsView() throws SQLException, ClassNotFoundException {

        //------------- Help button---------------
        Dialog helpDialog = new Dialog();
        TextField Name = new TextField("Name: ");
        TextField enrol = new TextField("Enrollment Number: ");
        TextField depatment = new TextField("Department: ");
        TextArea des = new TextArea("Query: ");
        des.setHeight("200px");
        des.setWidth("300px");
        Button submitQueryButton = new Button("Submit Query", event -> {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO request (namee, enrolll, department, query) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, Name.getValue());
                statement.setString(2, enrol.getValue());
                statement.setString(3, depatment.getValue());
                statement.setString(4, des.getValue());
                statement.executeUpdate();
                Dialog err = new Dialog();
                err.add(new Text("Query sent to admin"));
                err.open();
                err.setCloseOnOutsideClick(true);
            } catch (SQLException e) {
                e.printStackTrace();
                Notification.show("Error sending query").setDuration(50);
            }
        });

        VerticalLayout content = new VerticalLayout(Name, enrol, depatment, des, submitQueryButton);
        helpDialog.add(content);
        Button helpButton = new Button("Help", event -> helpDialog.open());
        helpButton.getElement().getStyle().set("position", "fixed");
        helpButton.getElement().getStyle().set("top", "10px");
        helpButton.getElement().getStyle().set("right", "10px");

        //----------------------------------------

        Details d1 = new Details("Instructions", new Text(ins1));
        d1.setOpened(true);

        setClassName("exam-details-view");

        String username = (String) VaadinSession.getCurrent().getAttribute("key");

        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM students WHERE enroll = ?");
        statement.setString(1, username);
        ResultSet rs = statement.executeQuery();

        head.getElement().getStyle().set("text-align", "center");

        VerticalLayout leftLayout = new VerticalLayout();
        leftLayout.setClassName("left-layout");

        VerticalLayout rightLayout = new VerticalLayout();
        rightLayout.setClassName("right-layout");

        if (rs.next()) {
            String name = rs.getString("name");
            Span name1 = new Span("Name - " + name);
            String enroll = rs.getString("enroll");
            Span enroll1 = new Span("Enrollment no. - " + enroll);
            String department = rs.getString("department");
            Span department1 = new Span("Department - " + department);
            String eligible = rs.getString("eligible");
            if (eligible.equals("f")) {
                eligible = "No";
                but1.setEnabled(false);
                Dialog err = new Dialog();
                err.add(new Text("You are not eligible for the exam. Kindly contact the admin"));
                err.open();
                err.setCloseOnOutsideClick(true);
            }
            else {
                eligible = "Yes";
            }
            Span eligible1 = new Span("Eligible - " + eligible);
            leftLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, name1, enroll1, department1, eligible1);
            leftLayout.add(name1, enroll1, department1, eligible1);
            rightLayout.add(d1, but1);
        } else {
            Notification.show("User not found").setDuration(50);
        }
        but1.addClickListener(event -> {UI.getCurrent().navigate("ques");});
        add(head, new HorizontalLayout(leftLayout, rightLayout), helpButton);
    }
}