package com.example.application.views.onlineexam;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


//---------------- Bean class for Query database----------
class Query {
    private String namee;
    private String enrolll;
    private String department;
    private String query;

    public Query(String namee, String enrolll, String department, String query) {
        this.namee = namee;
        this.enrolll = enrolll;
        this.department = department;
        this.query = query;
    }

    public String getNamee() {
        return namee;
    }

    public String getenroll() {
        return enrolll;
    }

    public String getDepartment() {
        return department;
    }

    public String getQuery() {
        return query;
    }
}
//------------------------------



//--------------- Bean class for Result database---------
class Result {
    private String namee;
    private String enrollll;
    private String department;
    private int score;

    public Result(String namee, String enrollll, String department, int score) {
        this.namee = namee;
        this.enrollll = enrollll;
        this.department = department;
        this.score = score;
    }

    public String getNamee() {
        return namee;
    }

    public String getEnrollll() {
        return enrollll;
    }

    public String getDepartment() {
        return department;
    }

    public int getScore() {
        return score;
    }
}
//------------------------------------------------------




@Route(value = "/adminHome")
public class AdminHome extends VerticalLayout {
    Grid<Query> grid = new Grid<>();
    Grid<Result> grid1 = new Grid<>();
    private final Button logout = new Button("Logout");
    AdminHome() throws SQLException {

        //-----------------Result Grid------------------------------------
        List<Result> records = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT name, enroll, department, score FROM result";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String namee = resultSet.getString("name");
                String enroll = resultSet.getString("enroll");
                String department = resultSet.getString("department");
                int marks = resultSet.getInt("score");

                records.add(new Result(namee, enroll, department, marks));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        grid1.setItems(records);
        grid1.addColumn(Result::getNamee).setHeader("Name");
        grid1.addColumn(Result::getEnrollll).setHeader("Enrollment Number");
        grid1.addColumn(Result::getDepartment).setHeader("Department");
        grid1.addColumn(Result::getScore).setHeader("Score");
        grid1.setAllRowsVisible(true);
        grid1.setSelectionMode(Grid.SelectionMode.MULTI);



        //-----------------Query Grid--------------------------------------------
        List<Query> students = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String sql = "SELECT namee, enrolll, department, query FROM request";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String namee = resultSet.getString("namee");
                String enroll = resultSet.getString("enrolll");
                String department = resultSet.getString("department");
                String query = resultSet.getString("query");

                students.add(new Query(namee, enroll, department, query));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        grid.setItems(students);
        grid.addColumn(Query::getNamee).setHeader("Name");
        grid.addColumn(Query::getenroll).setHeader("Enrollment Number");
        grid.addColumn(Query::getDepartment).setHeader("Department");
        grid.addColumn(Query::getQuery).setHeader("Query").setWidth("700px");
        grid.setAllRowsVisible(true);
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        //----------------------------------------------------------------

        logout.getElement().getStyle().set("position", "fixed");
        logout.getElement().getStyle().set("top", "35px");
        logout.getElement().getStyle().set("right", "35px");

        //------Logout button-------
        logout.addClickListener(event -> {
            Dialog confirmationDialog = new Dialog();
            confirmationDialog.setCloseOnEsc(true);
            confirmationDialog.setCloseOnOutsideClick(true);
            confirmationDialog.add(new Text("Are you sure you want to logout?"));

            // create "yes" button on confirmation dialog box
            Button yesButton = new Button("Yes", eventYes -> {
               UI.getCurrent().navigate("admin");
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

        //-------------- Adding Name of admin to title----------
        String name, ans = "";
        String username = (String) VaadinSession.getCurrent().getAttribute("key");
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT name FROM admin WHERE id = ?");
        statement.setString(1, username);
        ResultSet rs = statement.executeQuery();
        if (rs.next()) {
            name = rs.getString("name");
            ans = "Welcome " + name;
        }
        else {
            Dialog err = new Dialog();
            err.add(new Text("ID not found"));
            err.open();
            err.setCloseOnOutsideClick(true);
        }

        //------------------------------------------------------

        add(new H1(ans),new H2("Student's Results"),grid1, new H2("Student's Queries"), grid, logout);
    }
}
