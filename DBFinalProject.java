import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

import oracle.jdbc.OracleTypes;
import oracle.jdbc.OracleCallableStatement;

public class DBFinalProject extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    // Method that fills out the table with all the rows of a simple select
    private void fillDefault(PreparedStatement statement, Connection connection, ResultSet resultSet, TableView table) {
        try {
            String selectSQL = "SELECT * FROM charities";
            statement = connection.prepareStatement(selectSQL);
            resultSet = statement.executeQuery();
            table.getItems().clear();
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String motto = resultSet.getString("motto");
                String category = resultSet.getString("category");
                String description = resultSet.getString("description");
                int score = resultSet.getInt("score");
                int expenses = resultSet.getInt("total_expenses");
                String leader = resultSet.getString("leader");
                int leaderCompensation = resultSet.getInt("leader_compensation");
                String size = resultSet.getString("charity_size");
                double leaderCompPerc = resultSet.getDouble("leader_compensation_percentage");
                ImageView logoImage = new ImageView(new Image(resultSet.getString("logo_image")));
                logoImage.setFitWidth(100);
                logoImage.setFitHeight(100);
                table.getItems().add(new Charities(id, name, motto, category, description, score, expenses,
                        leader, leaderCompensation, size, leaderCompPerc, logoImage));
                System.out.println("Added " + name);
            }
        }
        catch (SQLException e) {
            System.out.println("Oops, there's a problem with select.");
            e.printStackTrace();
        }
    }

    // Method that returns all the rows sorted (descending) by a provided parameter
    private ResultSet getDataSorted(PreparedStatement statement, Connection connection, String parameter) {
        ResultSet resultSet = null;
        try {
            String selectSQL = "SELECT * FROM charities order by " + parameter + " desc";
            statement = connection.prepareStatement(selectSQL);
            resultSet = statement.executeQuery();
        }
        catch (SQLException e) {
            System.out.println("Oops, there's a problem with select.");
            e.printStackTrace();
        }

        return resultSet;
    }

    @Override
    public void start(Stage primaryStage) {
        // Properties for connection
        String connectionURL = "jdbc:oracle:thin:@localhost:1521:XE";
        String username = "system";
        String password = "THANKUNEXT";
        ResultSet resultSet = null;

        // Tables for charities and log
        TableView table = new TableView();
        table.setEditable(true);

        TableView table2 = new TableView();
        table2.setEditable(true);

        // Columns of charities table
        TableColumn<Charities, String> idColumn = new TableColumn<>("id");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Charities, String> nameColumn = new TableColumn<>("name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Charities, String> mottoColumn = new TableColumn<>("motto");
        mottoColumn.setCellValueFactory(new PropertyValueFactory<>("motto"));

        TableColumn<Charities, String> categoryColumn = new TableColumn<>("category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Charities, String> descriptionColumn = new TableColumn<>("description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Charities, Integer> scoreColumn = new TableColumn<>("score");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));

        TableColumn<Charities, Integer> expensesColumn = new TableColumn<>("total_expenses");
        expensesColumn.setCellValueFactory(new PropertyValueFactory<>("total_expenses"));

        TableColumn<Charities, String> leaderColumn = new TableColumn<>("leader");
        leaderColumn.setCellValueFactory(new PropertyValueFactory<>("leader"));

        TableColumn<Charities, Integer> leaderCompensationColumn = new TableColumn<>("leader_compensation");
        leaderCompensationColumn.setCellValueFactory(new PropertyValueFactory<>("leader_compensation"));

        TableColumn<Charities, String> sizeColumn = new TableColumn<>("charity_size");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("charity_size"));

        TableColumn<Charities, Double> leaderCompPercColumn = new TableColumn<>("leader_compensation_percentage");
        leaderCompPercColumn.setCellValueFactory(new PropertyValueFactory<>("leader_compensation_percentage"));

        TableColumn<Charities, ImageView> logoImageColumn = new TableColumn<>("logo_image");
        logoImageColumn.setCellValueFactory(new PropertyValueFactory<>("logo_image"));

        table.getColumns().addAll(idColumn, nameColumn, mottoColumn, categoryColumn,
                descriptionColumn, scoreColumn, expensesColumn, leaderColumn,
                leaderCompensationColumn, sizeColumn, leaderCompPercColumn, logoImageColumn);

        // Columns of log table
        TableColumn<CharitiesLog, Integer> logIdColumn = new TableColumn<>("id");
        logIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<CharitiesLog, LocalDate> logDateColumn = new TableColumn<>("operation_date");
        logDateColumn.setCellValueFactory(new PropertyValueFactory<>("operation_date"));

        TableColumn<CharitiesLog, String> logActionColumn = new TableColumn<>("action");
        logActionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));

        TableColumn<CharitiesLog, String> logActionAuthorColumn = new TableColumn<>("action_author");
        logActionAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("action_author"));

        TableColumn<CharitiesLog, String> logOldIdColumn = new TableColumn<>("old_id");
        logOldIdColumn.setCellValueFactory(new PropertyValueFactory<>("old_id"));

        TableColumn<CharitiesLog, String> logNewIdColumn = new TableColumn<>("new_id");
        logNewIdColumn.setCellValueFactory(new PropertyValueFactory<>("new_id"));

        TableColumn<CharitiesLog, String> logOldNameColumn = new TableColumn<>("old_name");
        logOldNameColumn.setCellValueFactory(new PropertyValueFactory<>("old_name"));

        TableColumn<CharitiesLog, String> logNewNameColumn = new TableColumn<>("new_name");
        logNewNameColumn.setCellValueFactory(new PropertyValueFactory<>("new_name"));

        TableColumn<CharitiesLog, String> logOldMottoColumn = new TableColumn<>("old_motto");
        logOldMottoColumn.setCellValueFactory(new PropertyValueFactory<>("old_motto"));

        TableColumn<CharitiesLog, String> logNewMottoColumn = new TableColumn<>("new_motto");
        logNewMottoColumn.setCellValueFactory(new PropertyValueFactory<>("new_motto"));

        TableColumn<CharitiesLog, String> logOldCategoryColumn = new TableColumn<>("old_category");
        logOldCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("old_category"));

        TableColumn<CharitiesLog, String> logNewCategoryColumn = new TableColumn<>("new_category");
        logNewCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("new_category"));

        TableColumn<CharitiesLog, String> logOldDescriptionColumn = new TableColumn<>("old_description");
        logOldDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("old_description"));

        TableColumn<CharitiesLog, String> logNewDescriptionColumn = new TableColumn<>("new_description");
        logNewDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("new_description"));

        TableColumn<CharitiesLog, Integer> logOldScoreColumn = new TableColumn<>("old_score");
        logOldScoreColumn.setCellValueFactory(new PropertyValueFactory<>("old_score"));

        TableColumn<CharitiesLog, Integer> logNewScoreColumn = new TableColumn<>("new_score");
        logNewScoreColumn.setCellValueFactory(new PropertyValueFactory<>("new_score"));

        TableColumn<CharitiesLog, Integer> logOldExpensesColumn = new TableColumn<>("old_expenses");
        logOldExpensesColumn.setCellValueFactory(new PropertyValueFactory<>("old_expenses"));

        TableColumn<CharitiesLog, Integer> logNewExpensesColumn = new TableColumn<>("new_expenses");
        logNewExpensesColumn.setCellValueFactory(new PropertyValueFactory<>("new_expenses"));

        TableColumn<CharitiesLog, String> logOldLeaderColumn = new TableColumn<>("old_leader");
        logOldLeaderColumn.setCellValueFactory(new PropertyValueFactory<>("old_leader"));

        TableColumn<CharitiesLog, String> logNewLeaderColumn = new TableColumn<>("new_leader");
        logNewLeaderColumn.setCellValueFactory(new PropertyValueFactory<>("new_leader"));

        TableColumn<CharitiesLog, Integer> logOldLeaderCompColumn = new TableColumn<>("old_leader_comp");
        logOldLeaderCompColumn.setCellValueFactory(new PropertyValueFactory<>("old_leader_comp"));

        TableColumn<CharitiesLog, Integer> logNewLeaderCompColumn = new TableColumn<>("new_leader_comp");
        logNewLeaderCompColumn.setCellValueFactory(new PropertyValueFactory<>("new_leader_comp"));

        TableColumn<CharitiesLog, String> logOldSizeCompColumn = new TableColumn<>("old_charity_size");
        logOldSizeCompColumn.setCellValueFactory(new PropertyValueFactory<>("old_charity_size"));

        TableColumn<CharitiesLog, String> logNewSizeCompColumn = new TableColumn<>("new_charity_size");
        logNewSizeCompColumn.setCellValueFactory(new PropertyValueFactory<>("new_charity_size"));

        TableColumn<CharitiesLog, Double> logOldLeaderCompPcntgCompColumn = new TableColumn<>("old_leader_comp_percntg");
        logOldLeaderCompPcntgCompColumn.setCellValueFactory(new PropertyValueFactory<>("old_leader_comp_percntg"));

        TableColumn<CharitiesLog, Double> logNewLeaderCompPcntgCompColumn = new TableColumn<>("new_leader_comp_percntg");
        logNewLeaderCompPcntgCompColumn.setCellValueFactory(new PropertyValueFactory<>("new_leader_comp_percntg"));

        TableColumn<CharitiesLog, ImageView> logOldLogoColumn = new TableColumn<>("old_logo_image");
        logOldLogoColumn.setCellValueFactory(new PropertyValueFactory<>("old_logo_image"));

        TableColumn<CharitiesLog, ImageView> logNewLogoColumn = new TableColumn<>("new_logo_image");
        logNewLogoColumn.setCellValueFactory(new PropertyValueFactory<>("new_logo_image"));

        table2.getColumns().addAll(logIdColumn, logDateColumn, logActionColumn, logActionAuthorColumn,
                logOldIdColumn, logNewIdColumn, logOldNameColumn, logNewNameColumn, logOldMottoColumn,
                logNewMottoColumn, logOldCategoryColumn, logNewCategoryColumn, logOldDescriptionColumn,
                logNewDescriptionColumn, logOldScoreColumn, logNewScoreColumn, logOldExpensesColumn,
                logNewExpensesColumn, logOldLeaderColumn, logNewLeaderColumn, logOldLeaderCompColumn,
                logNewLeaderCompColumn, logOldSizeCompColumn, logNewSizeCompColumn, logOldLeaderCompPcntgCompColumn,
                logNewLeaderCompPcntgCompColumn, logOldLogoColumn, logNewLogoColumn);

        Connection connection = null;
        PreparedStatement statement = null;

        // All buttons
        Button showDefault = new Button("Show Default");

        Button sortByScore = new Button("Sort by Score");
        Button sortByLC = new Button("Sort by Leader Compensation");

        Button groupByCategory = new Button("Group by Category");
        Button groupBySize = new Button("Group by Size");

        Button openLogTable = new Button("See Log");
        Button openRegularTable = new Button("See Charities Table");

        Button filter = new Button("Filter");

        Button insertCharity = new Button("Add new");
        Button updateCharity = new Button("Update charity");
        Button deleteCharity = new Button("Delete charity");

        Button add = new Button("Add charity to table");
        Button update = new Button("Update charity");
        Button delete = new Button("Delete charity");

        Button graphExpenses = new Button("Graph for Expenses");
        Button graphLeaderComp = new Button("Graph for Leader Compensation");

        // Horizontal box with most buttons
        HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.setPadding(new Insets(10, 0, 0, 10));
        hbox.getChildren().addAll(showDefault, sortByScore, sortByLC, groupByCategory, groupBySize, graphExpenses,
                graphLeaderComp, insertCharity, updateCharity, deleteCharity, openLogTable);
        hbox.setAlignment(Pos.CENTER);

        // Another horizontal box
        HBox hbox2 = new HBox();
        hbox2.setSpacing(5);
        hbox2.setPadding(new Insets(10, 0, 0, 10));
        hbox2.getChildren().addAll(openRegularTable);
        hbox2.setAlignment(Pos.CENTER);

        // Vertical box for the filtering
        VBox filtering = new VBox();
        filtering.setSpacing(5);
        filtering.setPadding(new Insets(0, 10, 10, 0));
        filtering.setAlignment(Pos.CENTER);

        // 2 horizontal boxes responsible for filtering by category and size respectively
        HBox categoryBox = new HBox();
        Label categoryLabel = new Label("Category");
        TextField categoryField = new TextField();
        categoryBox.setPadding(new Insets(10, 0, 0, 10));
        categoryBox.setSpacing(5);
        categoryBox.getChildren().addAll(categoryLabel, categoryField);
        categoryBox.setAlignment(Pos.CENTER);

        HBox sizeBox = new HBox();
        Label sizeLabel = new Label("Size(Either big, medium or small)");
        TextField sizeField = new TextField();
        sizeBox.setPadding(new Insets(10, 0, 0, 10));
        sizeBox.setSpacing(5);
        sizeBox.getChildren().addAll(sizeLabel, sizeField);
        sizeBox.setAlignment(Pos.CENTER);

        filtering.getChildren().addAll(categoryBox, sizeBox, filter);

        // Horizontal boxes for (almost) each parameter, used in add, update, delete sections
        HBox idForm = new HBox();
        Label idLabel = new Label("ID");
        TextField idField = new TextField();
        idForm.setPadding(new Insets(10, 0,0 ,10));
        idForm.setSpacing(5);
        idForm.getChildren().addAll(idLabel, idField);
        idForm.setAlignment(Pos.CENTER);

        HBox nameForm = new HBox();
        Label nameLabel = new Label("Name");
        TextField nameField = new TextField();
        nameForm.setPadding(new Insets(10, 0,0 ,10));
        nameForm.setSpacing(5);
        nameForm.getChildren().addAll(nameLabel, nameField);
        nameForm.setAlignment(Pos.CENTER);

        HBox mottoForm = new HBox();
        Label mottoLabel = new Label("Motto");
        TextField mottoField = new TextField();
        mottoForm.setPadding(new Insets(10, 0,0 ,10));
        mottoForm.setSpacing(5);
        mottoForm.getChildren().addAll(mottoLabel, mottoField);
        mottoForm.setAlignment(Pos.CENTER);

        HBox categoryForm = new HBox();
        Label catLabel = new Label("Category");
        TextField catField = new TextField();
        categoryForm.setPadding(new Insets(10, 0,0 ,10));
        categoryForm.setSpacing(5);
        categoryForm.getChildren().addAll(catLabel, catField);
        categoryForm.setAlignment(Pos.CENTER);

        HBox descriptionForm = new HBox();
        Label descLabel = new Label("Description");
        TextField descField = new TextField();
        descriptionForm.setPadding(new Insets(10, 0,0 ,10));
        descriptionForm.setSpacing(5);
        descriptionForm.getChildren().addAll(descLabel, descField);
        descriptionForm.setAlignment(Pos.CENTER);

        HBox scoreForm = new HBox();
        Label scoreLabel = new Label("Score");
        TextField scoreField = new TextField();
        scoreForm.setPadding(new Insets(10, 0,0 ,10));
        scoreForm.setSpacing(5);
        scoreForm.getChildren().addAll(scoreLabel, scoreField);
        scoreForm.setAlignment(Pos.CENTER);

        HBox expensesForm = new HBox();
        Label expensesLabel = new Label("Expenses");
        TextField expensesField = new TextField();
        expensesForm.setPadding(new Insets(10, 0,0 ,10));
        expensesForm.setSpacing(5);
        expensesForm.getChildren().addAll(expensesLabel, expensesField);
        expensesForm.setAlignment(Pos.CENTER);

        HBox leaderForm = new HBox();
        Label leaderLabel = new Label("Leader");
        TextField leaderField = new TextField();
        leaderForm.setPadding(new Insets(10, 0,0 ,10));
        leaderForm.setSpacing(5);
        leaderForm.getChildren().addAll(leaderLabel, leaderField);
        leaderForm.setAlignment(Pos.CENTER);

        HBox leaderCompForm = new HBox();
        Label leaderCompLabel = new Label("Leader Compensation");
        TextField leaderCompField = new TextField();
        leaderCompForm.setPadding(new Insets(10, 0,0 ,10));
        leaderCompForm.setSpacing(5);
        leaderCompForm.getChildren().addAll(leaderCompLabel, leaderCompField);
        leaderCompForm.setAlignment(Pos.CENTER);

        HBox logoForm = new HBox();
        Label logoLabel = new Label("Logo Image");
        TextField logoField = new TextField();
        logoForm.setPadding(new Insets(10, 0,0 ,10));
        logoForm.setSpacing(5);
        logoForm.getChildren().addAll(logoLabel, logoField);
        logoForm.setAlignment(Pos.CENTER);

        // Establishing a connection
        try {
            connection = DriverManager.getConnection(connectionURL, username, password);
            System.out.println("Connected!");
        }
        catch (SQLException e){
            System.out.println("Oops, there's a problem with connection.");
            e.printStackTrace();
        }

        // Filling the table
        fillDefault(statement, connection, resultSet, table);

        // Display the default table
        showDefault.setOnMouseClicked(e -> {
            try {
                Connection connection1 = DriverManager.getConnection(connectionURL, username, password);
                fillDefault(statement, connection1, resultSet, table);
            }
            catch (SQLException e1) {
                System.out.println("Oops");
                e1.printStackTrace();
            }
        });

        // Sends to the page with the log table
        openLogTable.setOnMouseClicked(e -> {
            try {
                Connection connection1 = DriverManager.getConnection(connectionURL, username, password);
                PreparedStatement stmt = connection1.prepareStatement("select * from charities_log");
                ResultSet rs = stmt.executeQuery();
                table2.getItems().clear();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    LocalDate operationDate = rs.getDate("operation_date").toLocalDate();
                    String action = rs.getString("action");
                    String actionAuthor = rs.getString("action_author");
                    String oldId = rs.getString("old_id");
                    String newId = rs.getString("new_id");
                    String oldName = rs.getString("old_name");
                    String newName = rs.getString("new_name");
                    String oldMotto = rs.getString("old_motto");
                    String newMotto = rs.getString("new_motto");
                    String oldCategory = rs.getString("old_category");
                    String newCategory = rs.getString("new_category");
                    String oldDescription = rs.getString("old_description");
                    String newDescription = rs.getString("new_description");
                    int oldScore = rs.getInt("old_score");
                    int newScore = rs.getInt("new_score");
                    int oldExpenses = rs.getInt("old_total_expenses");
                    int newExpenses = rs.getInt("new_total_expenses");
                    String oldLeader = rs.getString("old_leader");
                    String newLeader = rs.getString("new_leader");
                    int oldLeaderComp = rs.getInt("old_leader_comp");
                    int newLeaderComp = rs.getInt("new_leader_comp");
                    String oldSize = rs.getString("old_charity_size");
                    String newSize = rs.getString("new_charity_size");
                    double oldLeaderCompPcntg = rs.getDouble("old_leader_comp_percntg");
                    double newLeaderCompPcntg = rs.getDouble("new_leader_comp_percntg");
                    System.out.println(newLeaderCompPcntg);
                    ImageView oldLogo;
                    if (rs.getString("old_logo_image") != null) {
                        oldLogo = new ImageView(new Image(rs.getString("old_logo_image")));
                    }
                    else
                        oldLogo = null;

                    ImageView newLogo;
                    if (rs.getString("new_logo_image") != null) {
                        newLogo = new ImageView(new Image(rs.getString("new_logo_image")));
                    }
                    else
                        newLogo = null;

                    if (oldLogo != null) {
                        oldLogo.setFitHeight(100);
                        oldLogo.setFitWidth(100);
                    }

                    if (newLogo != null) {
                        newLogo.setFitWidth(100);
                        newLogo.setFitHeight(100);
                    }

                    table2.getItems().add(new CharitiesLog(id, operationDate, action, actionAuthor,
                            oldId, newId, oldName, newName, oldMotto, newMotto,
                            oldCategory, newCategory, oldDescription, newDescription,
                            oldScore, newScore, oldExpenses, newExpenses, oldLeader,
                            newLeader, oldLeaderComp, newLeaderComp, oldSize, newSize,
                            oldLeaderCompPcntg, newLeaderCompPcntg, oldLogo, newLogo));
                }
            }
            catch (SQLException e1) {
                System.out.println("Oops, there's a problem with log table");
                e1.printStackTrace();
            }

            VBox vbox2 = new VBox();
            vbox2.getChildren().add(table2);
            vbox2.setAlignment(Pos.BOTTOM_CENTER);

            BorderPane borderPane = new BorderPane();
            borderPane.setBottom(vbox2);
            if (hbox2.getChildren().contains(openRegularTable)) {

            }
            else {
                hbox2.getChildren().add(openRegularTable);
            }
            borderPane.setTop(hbox2);

            Scene scene2 = new Scene(borderPane, 1400, 700);
            primaryStage.setScene(scene2);
        });

        // Filter by category and/or size
        filter.setOnMouseClicked(e -> {
            try {
                Connection connection1 = DriverManager.getConnection(connectionURL, username, password);
                String categoryString = categoryField.getText();
                String sizeString = sizeField.getText();
                if ((categoryString == null || categoryString.length() == 0) &&
                        (sizeString == null || sizeString.length() == 0)) {

                }
                else {
                    StringBuilder filteredQuery = new StringBuilder("select * from charities where ");
                    if (categoryString != null && categoryString.length() != 0) {
                        filteredQuery.append("upper(category) = '" + categoryString.toUpperCase() + "' ");

                        if (sizeString != null && sizeString.length() != 0) {
                            filteredQuery.append("and upper(charity_size) = '" + sizeString.toUpperCase() + "' ");
                        }
                    } else if (sizeString != null && sizeString.length() != 0) {
                        filteredQuery.append("upper(charity_size) = '" + sizeString.toUpperCase() + "' ");
                    }

                    PreparedStatement preparedStatement = connection1.prepareStatement(filteredQuery.toString());
                    ResultSet rs = preparedStatement.executeQuery();

                    if (!rs.isBeforeFirst()) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Result");
                        if (categoryString != null && categoryString.length() != 0) {
                            if (sizeString != null && sizeString.length() != 0) {
                                alert.setHeaderText("Charities with provided category and size do not exist");
                                alert.setContentText("Make sure you enter a valid category and one of the three valid sizes(big, medium or small)");
                            } else {
                                alert.setHeaderText("Charities with provided category do not exist");
                                alert.setContentText("Make sure you enter a valid category");
                            }
                        } else if (sizeString != null && sizeString.length() != 0) {
                            alert.setHeaderText("Incorrect size given");
                            alert.setContentText("Make sure you enter either big, small or medium");
                        }

                        alert.showAndWait();
                    } else {
                        table.getItems().clear();
                        while (rs.next()) {
                            String id = rs.getString("id");
                            String name = rs.getString("name");
                            String motto = rs.getString("motto");
                            String category = rs.getString("category");
                            String description = rs.getString("description");
                            int score = rs.getInt("score");
                            int expenses = rs.getInt("total_expenses");
                            String leader = rs.getString("leader");
                            int leaderCompensation = rs.getInt("leader_compensation");
                            String size = rs.getString("charity_size");
                            double leaderCompPerc = rs.getDouble("leader_compensation_percentage");
                            ImageView logoImage = new ImageView(new Image(rs.getString("logo_image")));
                            logoImage.setFitWidth(100);
                            logoImage.setFitHeight(100);
                            table.getItems().add(new Charities(id, name, motto, category, description, score, expenses,
                                    leader, leaderCompensation, size, leaderCompPerc, logoImage));
                        }
                    }
                }
            }
            catch (SQLException e1) {
                System.out.println("Sorry, smth went wrong");
                e1.printStackTrace();
            }
        });

        // The following four methods use the stored procedures from our custom packages in our database
        // Sorts by score
        sortByScore.setOnMouseClicked(e -> {
            try {
                Connection connection1 = DriverManager.getConnection(connectionURL, username, password);
                CallableStatement stmt = connection1.prepareCall("begin sorting_procedures.sort_by_score(?); end;");
                stmt.registerOutParameter(1, OracleTypes.CURSOR);
                stmt.execute();
                ResultSet rs = ((OracleCallableStatement)stmt).getCursor(1);
                table.getItems().clear();
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String motto = rs.getString("motto");
                    String category = rs.getString("category");
                    String description = rs.getString("description");
                    int score = rs.getInt("score");
                    int expenses = rs.getInt("total_expenses");
                    String leader = rs.getString("leader");
                    int leaderCompensation = rs.getInt("leader_compensation");
                    String size = rs.getString("charity_size");
                    double leaderCompPerc = rs.getDouble("leader_compensation_percentage");
                    ImageView logoImage = new ImageView(new Image(rs.getString("logo_image")));
                    logoImage.setFitWidth(100);
                    logoImage.setFitHeight(100);
                    table.getItems().add(new Charities(id, name, motto, category, description,
                            score, expenses, leader, leaderCompensation, size, leaderCompPerc,
                            logoImage));
                }
            }
            catch (SQLException e1) {
                System.out.println("A problem occured");
                e1.printStackTrace();
            }
        });

        // Sorts by leader compensation
        sortByLC.setOnMouseClicked(e -> {
            try {
                Connection connection1 = DriverManager.getConnection(connectionURL, username, password);
                CallableStatement stmt = connection1.prepareCall("begin sorting_procedures.sort_by_lc(?); end;");
                stmt.registerOutParameter(1, OracleTypes.CURSOR);
                stmt.execute();
                ResultSet rs = ((OracleCallableStatement)stmt).getCursor(1);
                table.getItems().clear();
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String motto = rs.getString("motto");
                    String category = rs.getString("category");
                    String description = rs.getString("description");
                    int score = rs.getInt("score");
                    int expenses = rs.getInt("total_expenses");
                    String leader = rs.getString("leader");
                    int leaderCompensation = rs.getInt("leader_compensation");
                    String size = rs.getString("charity_size");
                    double leaderCompPerc = rs.getDouble("leader_compensation_percentage");
                    ImageView logoImage = new ImageView(new Image(rs.getString("logo_image")));
                    logoImage.setFitWidth(100);
                    logoImage.setFitHeight(100);
                    table.getItems().add(new Charities(id, name, motto, category, description,
                            score, expenses, leader, leaderCompensation, size, leaderCompPerc,
                            logoImage));
                }
            }
            catch (SQLException e1) {
                System.out.println("A problem occured");
                e1.printStackTrace();
            }
        });

        // Groups by category
        groupByCategory.setOnMouseClicked(e -> {
            try {
                Connection connection1 = DriverManager.getConnection(connectionURL, username, password);
                CallableStatement stmt = connection1.prepareCall("begin grouping_procedures.groupByCategory(?); end;");
                stmt.registerOutParameter(1, OracleTypes.CURSOR);
                stmt.execute();
                ResultSet rs = ((OracleCallableStatement)stmt).getCursor(1);
                table.getItems().clear();
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String motto = rs.getString("motto");
                    String category = rs.getString("category");
                    String description = rs.getString("description");
                    int score = rs.getInt("score");
                    int expenses = rs.getInt("total_expenses");
                    String leader = rs.getString("leader");
                    int leaderCompensation = rs.getInt("leader_compensation");
                    String size = rs.getString("charity_size");
                    double leaderCompPerc = rs.getDouble("leader_compensation_percentage");
                    ImageView logoImage = new ImageView(new Image(rs.getString("logo_image")));
                    logoImage.setFitWidth(100);
                    logoImage.setFitHeight(100);
                    table.getItems().add(new Charities(id, name, motto, category, description,
                            score, expenses, leader, leaderCompensation, size, leaderCompPerc,
                            logoImage));
                }
            }
            catch (SQLException e1) {
                System.out.println("A problem occured");
                e1.printStackTrace();
            }
        });

        // Groups by size
        groupBySize.setOnMouseClicked(e -> {
            try {
                Connection connection1 = DriverManager.getConnection(connectionURL, username, password);
                CallableStatement stmt = connection1.prepareCall("begin grouping_procedures.groupBySize(?); end;");
                stmt.registerOutParameter(1, OracleTypes.CURSOR);
                stmt.execute();
                ResultSet rs = ((OracleCallableStatement)stmt).getCursor(1);
                table.getItems().clear();
                while (rs.next()) {
                    String id = rs.getString("id");
                    String name = rs.getString("name");
                    String motto = rs.getString("motto");
                    String category = rs.getString("category");
                    String description = rs.getString("description");
                    int score = rs.getInt("score");
                    int expenses = rs.getInt("total_expenses");
                    String leader = rs.getString("leader");
                    int leaderCompensation = rs.getInt("leader_compensation");
                    String size = rs.getString("charity_size");
                    double leaderCompPerc = rs.getDouble("leader_compensation_percentage");
                    ImageView logoImage = new ImageView(new Image(rs.getString("logo_image")));
                    logoImage.setFitWidth(100);
                    logoImage.setFitHeight(100);
                    table.getItems().add(new Charities(id, name, motto, category, description,
                            score, expenses, leader, leaderCompensation, size, leaderCompPerc,
                            logoImage));
                }
            }
            catch (SQLException e1) {
                System.out.println("A problem occured");
                e1.printStackTrace();
            }
        });

        // Container for table
        VBox vbox = new VBox();
        vbox.getChildren().add(table);

        // Sends to the insert page
        insertCharity.setOnMouseClicked(e -> {
            BorderPane pane3 = new BorderPane();

            VBox insertForm1 = new VBox();
            insertForm1.setPadding(new Insets(10, 0, 0, 10));
            insertForm1.setSpacing(5);
            insertForm1.getChildren().addAll(idForm, nameForm, mottoForm, categoryForm,
                    descriptionForm);

            VBox insertForm2 = new VBox();
            insertForm2.setPadding(new Insets(10, 0, 0, 10));
            insertForm2.setSpacing(5);
            insertForm2.getChildren().addAll(scoreForm, expensesForm, leaderForm,
                    leaderCompForm, logoForm);

            BorderPane.setAlignment(insertForm2, Pos.CENTER);
            BorderPane.setMargin(insertForm2, new Insets(0, 100 ,0 ,0));

            HBox hbox3 = new HBox();
            hbox3.setSpacing(5);
            hbox3.setPadding(new Insets(10, 0, 0, 10));
            hbox3.getChildren().addAll(openRegularTable, updateCharity, deleteCharity);
            hbox3.setAlignment(Pos.CENTER);

            pane3.setTop(hbox3);
            pane3.setLeft(insertForm1);
            pane3.setRight(insertForm2);
            pane3.setCenter(add);
            pane3.setBottom(vbox);
            Scene scene3 = new Scene(pane3, 1400, 700);
            primaryStage.setScene(scene3);
        });

        // Inserts a new charity
        add.setOnMouseClicked(e -> {
            try {
                Connection connection1 = DriverManager.getConnection(connectionURL, username, password);
                String id = idField.getText();

                PreparedStatement preparedStatement2 = connection1.prepareStatement("select * from charities where id = '" + id + "'");
                ResultSet rs = preparedStatement2.executeQuery();

                if (rs.isBeforeFirst()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Insert warning");
                    alert.setHeaderText("A charity with the provided id already exists");
                    alert.setContentText("Please make sure you provide a unique id");

                    idField.clear();
                    nameField.clear();
                    mottoField.clear();
                    catField.clear();
                    descField.clear();
                    scoreField.clear();
                    expensesField.clear();
                    leaderField.clear();
                    leaderCompField.clear();
                    logoField.clear();

                    alert.showAndWait();
                } else {
                    String name = nameField.getText();
                    String motto = mottoField.getText();
                    String category = catField.getText();
                    String description = descField.getText();
                    int score = Integer.parseInt(scoreField.getText());
                    int expenses = Integer.parseInt(expensesField.getText());
                    String leader = leaderField.getText();
                    int leaderComp = Integer.parseInt(leaderCompField.getText());
                    double leaderCompPercntg = (double) leaderComp / expenses;
                    String size;

                    if (expenses <= 3500000)
                        size = "Small";
                    else if (expenses <= 13500000)
                        size = "Medium";
                    else
                        size = "Big";

                    ImageView logo = new ImageView(new Image(logoField.getText()));
                    logo.setFitHeight(100);
                    logo.setFitWidth(100);

                    idField.clear();
                    nameField.clear();
                    mottoField.clear();
                    catField.clear();
                    descField.clear();
                    scoreField.clear();
                    expensesField.clear();
                    leaderField.clear();
                    leaderCompField.clear();
                    logoField.clear();

                    PreparedStatement preparedStatement = connection1.
                            prepareStatement("insert into charities(id, name, motto, category, " +
                                    "description, score, total_expenses, leader, leader_compensation, logo_image) " +
                                    " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    preparedStatement.setString(1, id);
                    preparedStatement.setString(2, name);
                    preparedStatement.setString(3, motto);
                    preparedStatement.setString(4, category);
                    preparedStatement.setString(5, description);
                    preparedStatement.setInt(6, score);
                    preparedStatement.setInt(7, expenses);
                    preparedStatement.setString(8, leader);
                    preparedStatement.setInt(9, leaderComp);
                    preparedStatement.setString(10, logoField.getText());

                    preparedStatement.executeQuery();

                    table.getItems().add(new Charities(id, name, motto, category, description, score,
                            expenses, leader, leaderComp, size, leaderCompPercntg, logo));
                }
            }
            catch(Exception e1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Insert error");
                alert.setHeaderText("Insufficient or invalid information given");
                alert.setContentText("Please make sure your information is valid");

                alert.showAndWait();
            }
        });

        // Sends to the update page
        updateCharity.setOnMouseClicked(e -> {
            BorderPane pane3 = new BorderPane();

            VBox insertForm1 = new VBox();
            insertForm1.setPadding(new Insets(10, 0, 0, 10));
            insertForm1.setSpacing(5);
            insertForm1.getChildren().addAll(idForm);

            VBox insertForm2 = new VBox();
            insertForm2.setPadding(new Insets(10, 0, 0, 10));
            insertForm2.setSpacing(5);
            insertForm2.getChildren().addAll(scoreForm, expensesForm, leaderForm,
                    leaderCompForm);

            BorderPane.setAlignment(insertForm2, Pos.CENTER);
            BorderPane.setMargin(insertForm2, new Insets(0, 100 ,0 ,0));

            HBox hbox3 = new HBox();
            hbox3.setSpacing(5);
            hbox3.setPadding(new Insets(10, 0, 0, 10));
            hbox3.getChildren().addAll(openRegularTable, insertCharity, deleteCharity);
            hbox3.setAlignment(Pos.CENTER);

            pane3.setTop(hbox3);
            pane3.setLeft(insertForm1);
            pane3.setRight(insertForm2);
            pane3.setCenter(update);
            pane3.setBottom(vbox);
            Scene scene3 = new Scene(pane3, 1400, 700);
            primaryStage.setScene(scene3);
        });

        // Updates an existing charity
        update.setOnMouseClicked(e -> {
            try {
                Connection connection1 = DriverManager.getConnection(connectionURL, username, password);
                String id = idField.getText();

                idField.clear();

                PreparedStatement preparedStatement2 = connection1.prepareStatement("select * from charities where id = '" + id + "'");
                ResultSet rs = preparedStatement2.executeQuery();

                if (id == null || id.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Update warning");
                    alert.setHeaderText("You did not enter the id");
                    alert.setContentText("Please make sure you provide a valid id");
                    alert.showAndWait();
                }
                else if (!rs.isBeforeFirst()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Update warning");
                    alert.setHeaderText("A charity with the provided id does not exist");
                    alert.setContentText("Please make sure you provide a valid id");
                    alert.showAndWait();
                } else {
                    int score;
                    if (scoreField.getText() != null && !scoreField.getText().isEmpty()) {
                        score = Integer.parseInt(scoreField.getText());
                    } else
                        score = -1;

                    int expenses;
                    if (expensesField.getText() != null && !expensesField.getText().isEmpty()) {
                        expenses = Integer.parseInt(expensesField.getText());
                    } else
                        expenses = -1;

                    String leader = leaderField.getText();

                    int leaderComp;
                    if (leaderCompField.getText() != null && !leaderCompField.getText().isEmpty()) {
                        leaderComp = Integer.parseInt(leaderCompField.getText());
                    } else
                        leaderComp = -1;

                    scoreField.clear();
                    expensesField.clear();
                    leaderField.clear();
                    leaderCompField.clear();

                    if (id != null && (score >= 0 || expenses >= 0 || (leader != null && !leader.isEmpty()) || leaderComp >= 0)) {
                        StringBuilder query = new StringBuilder("update charities set ");

                        if (score >= 0) {
                            query.append("score = " + score + " ");
                            if (expenses >= 0) {
                                query.append(", total_expenses = " + expenses + " ");
                            }
                            if (leader != null && !leader.isEmpty()) {
                                query.append(", leader = '" + leader + "' ");
                            }
                            if (leaderComp >= 0) {
                                query.append(", leader_compensation = " + leaderComp + " ");
                            }
                        } else if (expenses >= 0) {
                            query.append("total_expenses = " + expenses + " ");
                            if (leader != null && !leader.isEmpty()) {
                                query.append(", leader = '" + leader + "' ");
                            }
                            if (leaderComp >= 0) {
                                query.append(", leader_compensation = " + leaderComp + " ");
                            }
                        } else if (leader != null && !leader.isEmpty()) {
                            query.append("leader = '" + leader + "' ");
                            if (leaderComp >= 0) {
                                query.append(", leader_compensation = " + leaderComp + " ");
                            }
                        } else if (leaderComp >= 0) {
                            query.append("leader_compensation = " + leaderComp + " ");
                        }

                        query.append("where id = '" + id + "'");

                        System.out.println(query.toString());

                        PreparedStatement preparedStatement = connection1.
                                prepareStatement(query.toString());

                        preparedStatement.executeQuery();

                        fillDefault(preparedStatement, connection1, resultSet, table);
                    }
                }
            }
            catch(Exception e1) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Update warning");
                alert.setHeaderText("Invalid data entered");
                alert.setContentText("Please make sure the data you entered is valid");

                alert.showAndWait();
            }
        });

        // Sends to the delete page
        deleteCharity.setOnMouseClicked(e -> {
            BorderPane pane3 = new BorderPane();

            HBox hbox3 = new HBox();
            hbox3.setSpacing(5);
            hbox3.setPadding(new Insets(10, 0, 0, 10));
            hbox3.getChildren().addAll(openRegularTable, insertCharity, updateCharity);
            hbox3.setAlignment(Pos.CENTER);

            pane3.setTop(hbox3);
            pane3.setLeft(idForm);
            pane3.setCenter(delete);
            pane3.setBottom(vbox);
            Scene scene3 = new Scene(pane3, 1400, 700);
            primaryStage.setScene(scene3);
        });

        // Deletes an existing charity
        delete.setOnMouseClicked(e -> {
            if (idField.getText() != null && !idField.getText().isEmpty()) {
                try {
                    Connection connection1 = DriverManager.getConnection(connectionURL, username, password);
                    String id = idField.getText();

                    idField.clear();

                    PreparedStatement preparedStatement2 = connection1.prepareStatement("select * from charities where id = '" + id + "'");
                    ResultSet rs = preparedStatement2.executeQuery();

                    if (!rs.isBeforeFirst()) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Delete warning");
                        alert.setHeaderText("A charity with the provided id does not exist");
                        alert.setContentText("Please make sure you provide a valid id");
                        alert.showAndWait();
                    }
                    else {
                        PreparedStatement preparedStatement = connection1.prepareStatement("delete from charities where id = '" + id + "'");
                        preparedStatement.executeQuery();

                        fillDefault(preparedStatement, connection1, resultSet, table);
                    }
                } catch (Exception e1) {
                    System.out.println("Something went wrong during delete");
                    e1.printStackTrace();
                }
            }
        });

        // Sends to a page with a pie chart, filled with data about expenses
        graphExpenses.setOnMouseClicked(e -> {
            try {
                Connection connection1 = DriverManager.getConnection(connectionURL, username, password);
                ResultSet rs = getDataSorted(statement, connection1, "total_expenses");
                PieChart pieChart = new PieChart();
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                int counter = 0;
                while (rs.next() && counter < 25) {
                    String name = rs.getString("name");
                    int expenses = rs.getInt("total_expenses");
                    pieChartData.add(new PieChart.Data(name, expenses));
                    counter++;
                }

                pieChart.setData(pieChartData);
                pieChart.setTitle("Top 25 Charities ordered by their total expenses");

                pieChart.getStylesheets().add("chart.css");

                BorderPane borderPane = new BorderPane();

                if (hbox2.getChildren().contains(openRegularTable)) {

                }
                else {
                    hbox2.getChildren().add(openRegularTable);
                }

                borderPane.setTop(hbox2);
                borderPane.setCenter(pieChart);

                Scene scene = new Scene(borderPane, 1400, 700);
                primaryStage.setScene(scene);

            }
            catch (Exception e1) {
                System.out.println("There was a problem when opening the graph");
                e1.printStackTrace();
            }
        });

        // Sends to a page with a pie chart, filled with data about leader compensation
        graphLeaderComp.setOnMouseClicked(e -> {
            try {
                Connection connection1 = DriverManager.getConnection(connectionURL, username, password);
                ResultSet rs = getDataSorted(statement, connection1, "leader_compensation");
                PieChart pieChart = new PieChart();
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                int counter = 0;
                while (rs.next() && counter < 25) {
                    String name = rs.getString("name");
                    int leaderCompensation = rs.getInt("leader_compensation");
                    PieChart.Data data = new PieChart.Data(name, leaderCompensation);
                    pieChartData.add(new PieChart.Data(name, leaderCompensation));
                    counter++;
                }

                pieChart.setData(pieChartData);
                pieChart.setTitle("Top 25 Charities ordered by their leaders' contribution");

                pieChart.getStylesheets().add("chart.css");

                BorderPane borderPane = new BorderPane();

                if (hbox2.getChildren().contains(openRegularTable)) {

                }
                else {
                    hbox2.getChildren().add(openRegularTable);
                }

                borderPane.setTop(hbox2);
                borderPane.setCenter(pieChart);

                Scene scene = new Scene(borderPane, 1400, 700);
                primaryStage.setScene(scene);
            }
            catch (Exception e1) {
                System.out.println("There was a problem when opening the graph");
                e1.printStackTrace();
            }
        });

        BorderPane borderPane = new BorderPane();
        BorderPane.setAlignment(hbox, Pos.CENTER);
        borderPane.setBottom(vbox);
        borderPane.setTop(hbox);
        borderPane.setCenter(filtering);

        Scene scene = new Scene(borderPane, 1400, 700);

        // Returns to the original view/page
        openRegularTable.setOnMouseClicked(e -> {
            hbox.getChildren().clear();
            hbox.getChildren().addAll(showDefault, sortByScore, sortByLC, groupByCategory, groupBySize, graphExpenses,
                    graphLeaderComp, insertCharity, updateCharity, deleteCharity, openLogTable);
            borderPane.setBottom(vbox);
            primaryStage.setScene(scene);
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Charities");
        primaryStage.show();
    }


    // Entity/ Object class for the Charities table
    public class Charities {
        private String id = null;
        private String name = null;
        private String motto = null;
        private String category = null;
        private String description = null;
        private int score = 0;
        private int total_expenses = 0;
        private String leader = null;
        private int leader_compensation = 0;
        private String charity_size = null;
        private double leader_compensation_percentage = 0;
        private ImageView logo_image = null;

        public Charities(String id, String name, String motto, String category, String description, int score, int total_expenses, String leader, int leader_compensation, String charity_size, double leader_compensation_percentage, ImageView logo_image) {
            this.id = id;
            this.name = name;
            this.motto = motto;
            this.category = category;
            this.description = description;
            this.score = score;
            this.total_expenses = total_expenses;
            this.leader = leader;
            this.leader_compensation = leader_compensation;
            this.charity_size = charity_size;
            this.leader_compensation_percentage = leader_compensation_percentage;
            this.logo_image = logo_image;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getMotto() {
            return motto;
        }

        public void setMotto(String motto) {
            this.motto = motto;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getTotal_expenses() {
            return total_expenses;
        }

        public void setTotal_expenses(int total_expenses) {
            this.total_expenses = total_expenses;
        }

        public String getLeader() {
            return leader;
        }

        public void setLeader(String leader) {
            this.leader = leader;
        }

        public int getLeader_compensation() {
            return leader_compensation;
        }

        public void setLeader_compensation(int leader_compensation) {
            this.leader_compensation = leader_compensation;
        }

        public String getCharity_size() {
            return charity_size;
        }

        public void setCharity_size(String charity_size) {
            this.charity_size = charity_size;
        }

        public double getLeader_compensation_percentage() {
            return leader_compensation_percentage;
        }

        public void setLeader_compensation_percentage(double leader_compensation_percentage) {
            this.leader_compensation_percentage = leader_compensation_percentage;
        }

        public ImageView getLogo_image() {
            return logo_image;
        }

        public void setLogo_image(ImageView logo_image) {
            this.logo_image = logo_image;
        }
    }

    // Entity/ Object class for the Log table
    public class CharitiesLog {
        private int id;
        private LocalDate operation_date;
        private String action;
        private String action_author;
        private String old_id;
        private String new_id;
        private String old_name;
        private String new_name;
        private String old_motto;
        private String new_motto;
        private String old_category;
        private String new_category;
        private String old_description;
        private String new_description;
        private int old_score;
        private int new_score;
        private int old_expenses;
        private int new_expenses;
        private String old_leader;
        private String new_leader;
        private int old_leader_comp;
        private int new_leader_comp;
        private String old_charity_size;
        private String new_charity_size;
        private double old_leader_comp_percntg;
        private double new_leader_comp_percntg;
        private ImageView old_logo_image;
        private ImageView new_logo_image;

        public CharitiesLog(int id, LocalDate operation_date, String action, String action_author, String old_id, String new_id, String old_name, String new_name, String old_motto, String new_motto, String old_category, String new_category, String old_description, String new_description, int old_score, int new_score, int old_expenses, int new_expenses, String old_leader, String new_leader, int old_leader_comp, int new_leader_comp, String old_charity_size, String new_charity_size, double old_leader_comp_percntg, double new_leader_comp_percntg, ImageView old_logo_image, ImageView new_logo_image) {
            this.id = id;
            this.operation_date = operation_date;
            this.action = action;
            this.action_author = action_author;
            this.old_id = old_id;
            this.new_id = new_id;
            this.old_name = old_name;
            this.new_name = new_name;
            this.old_motto = old_motto;
            this.new_motto = new_motto;
            this.old_category = old_category;
            this.new_category = new_category;
            this.old_description = old_description;
            this.new_description = new_description;
            this.old_score = old_score;
            this.new_score = new_score;
            this.old_expenses = old_expenses;
            this.new_expenses = new_expenses;
            this.old_leader = old_leader;
            this.new_leader = new_leader;
            this.old_leader_comp = old_leader_comp;
            this.new_leader_comp = new_leader_comp;
            this.old_charity_size = old_charity_size;
            this.new_charity_size = new_charity_size;
            this.old_leader_comp_percntg = old_leader_comp_percntg;
            this.new_leader_comp_percntg = new_leader_comp_percntg;
            this.old_logo_image = old_logo_image;
            this.new_logo_image = new_logo_image;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public LocalDate getOperation_date() {
            return operation_date;
        }

        public void setOperation_date(LocalDate operation_date) {
            this.operation_date = operation_date;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getAction_author() {
            return action_author;
        }

        public void setAction_author(String action_author) {
            this.action_author = action_author;
        }

        public String getOld_id() {
            return old_id;
        }

        public void setOld_id(String old_id) {
            this.old_id = old_id;
        }

        public String getNew_id() {
            return new_id;
        }

        public void setNew_id(String new_id) {
            this.new_id = new_id;
        }

        public String getOld_name() {
            return old_name;
        }

        public void setOld_name(String old_name) {
            this.old_name = old_name;
        }

        public String getNew_name() {
            return new_name;
        }

        public void setNew_name(String new_name) {
            this.new_name = new_name;
        }

        public String getOld_motto() {
            return old_motto;
        }

        public void setOld_motto(String old_motto) {
            this.old_motto = old_motto;
        }

        public String getNew_motto() {
            return new_motto;
        }

        public void setNew_motto(String new_motto) {
            this.new_motto = new_motto;
        }

        public String getOld_category() {
            return old_category;
        }

        public void setOld_category(String old_category) {
            this.old_category = old_category;
        }

        public String getNew_category() {
            return new_category;
        }

        public void setNew_category(String new_category) {
            this.new_category = new_category;
        }

        public String getOld_description() {
            return old_description;
        }

        public void setOld_description(String old_description) {
            this.old_description = old_description;
        }

        public String getNew_description() {
            return new_description;
        }

        public void setNew_description(String new_description) {
            this.new_description = new_description;
        }

        public int getOld_score() {
            return old_score;
        }

        public void setOld_score(int old_score) {
            this.old_score = old_score;
        }

        public int getNew_score() {
            return new_score;
        }

        public void setNew_score(int new_score) {
            this.new_score = new_score;
        }

        public int getOld_expenses() {
            return old_expenses;
        }

        public void setOld_expenses(int old_expenses) {
            this.old_expenses = old_expenses;
        }

        public int getNew_expenses() {
            return new_expenses;
        }

        public void setNew_expenses(int new_expenses) {
            this.new_expenses = new_expenses;
        }

        public String getOld_leader() {
            return old_leader;
        }

        public void setOld_leader(String old_leader) {
            this.old_leader = old_leader;
        }

        public String getNew_leader() {
            return new_leader;
        }

        public void setNew_leader(String new_leader) {
            this.new_leader = new_leader;
        }

        public int getOld_leader_comp() {
            return old_leader_comp;
        }

        public void setOld_leader_comp(int old_leader_comp) {
            this.old_leader_comp = old_leader_comp;
        }

        public int getNew_leader_comp() {
            return new_leader_comp;
        }

        public void setNew_leader_comp(int new_leader_comp) {
            this.new_leader_comp = new_leader_comp;
        }

        public String getOld_charity_size() {
            return old_charity_size;
        }

        public void setOld_charity_size(String old_charity_size) {
            this.old_charity_size = old_charity_size;
        }

        public String getNew_charity_size() {
            return new_charity_size;
        }

        public void setNew_charity_size(String new_charity_size) {
            this.new_charity_size = new_charity_size;
        }

        public double getOld_leader_comp_percntg() {
            return old_leader_comp_percntg;
        }

        public void setOld_leader_comp_percntg(double old_leader_comp_percntg) {
            this.old_leader_comp_percntg = old_leader_comp_percntg;
        }

        public double getNew_leader_comp_percntg() {
            return new_leader_comp_percntg;
        }

        public void setNew_leader_comp_percntg(double new_leader_comp_percntg) {
            this.new_leader_comp_percntg = new_leader_comp_percntg;
        }

        public ImageView getOld_logo_image() {
            return old_logo_image;
        }

        public void setOld_logo_image(ImageView old_logo_image) {
            this.old_logo_image = old_logo_image;
        }

        public ImageView getNew_logo_image() {
            return new_logo_image;
        }

        public void setNew_logo_image(ImageView new_logo_image) {
            this.new_logo_image = new_logo_image;
        }
    }
}