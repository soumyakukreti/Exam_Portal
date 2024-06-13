package com.example.application.views.onlineexam;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@PageTitle("end page")
@Route(value = "/hello")
public class lastpg extends VerticalLayout{
    private Button button;
    private H1 h1;
    private H1 name;
    private H1 enroll;
    private H1 sub;
    private H1 score;
    private Dialog dialog = new Dialog();
    lastpg() throws SQLException {
        String username = (String) VaadinSession.getCurrent().getAttribute("key");

        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM result WHERE enroll = ?");
        statement.setString(1, username);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            String name = rs.getString("name");
            String name1 = "Name - " + name;
            TextField textField = new TextField();
            textField.setReadOnly(true);
            textField.setLabel("Name");
            textField.setValue(name);
            //name1.getStyle().set("font-size","15px");
            String enroll = rs.getString("enroll");
            TextField textfield1 = new TextField();
            textfield1.setReadOnly(true);
            textfield1.setLabel("Enrollment Number");
            textfield1.setValue(enroll);
            H2 enroll1 = new H2("Enrollment no. - " + enroll);
            enroll1.getStyle().set("font-size","15px");
            String department = rs.getString("department");
            TextField textfield2 = new TextField();
            textfield2.setReadOnly(true);
            textfield2.setLabel("Department");
            textfield2.setValue(department);
            H2 department1 = new H2("Department - " + department);
            department1.getStyle().set("font-size","15px");
            int eligible = rs.getInt("score");
            String marks = eligible +"/60";
            TextField textfield3 = new TextField();
            textfield3.setReadOnly(true);
            textfield3.setLabel("Marks");
            textfield3.setValue(marks);
            //marks.getStyle().set("font-size","15px");
            dialog.setHeaderTitle("Score");
            dialog.add(textField);
            dialog.add(textfield1);
            dialog.add(textfield2);
            dialog.add(textfield3);
        }
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Button button = new Button("View score");
        H1 h1 = new H1("Your test has been submitted successfully.");
        h1.getStyle().set("font-size","20px");
        add(h1, button);


        Button closer = new Button("Close", event -> dialog.close());
        Element dialogElement = dialog.getElement();
        dialogElement.getStyle().set("text-align", "right");

        dialog.getFooter().add(closer);
        button.addClickListener(event -> dialog.open());

        add(h1,button);
    }
}
