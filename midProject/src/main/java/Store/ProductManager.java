// ProductManager.java
package Store;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Date;

public class ProductManager {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String USER = "projectDB";
    private static final String PASS = "asdqwe123zxc";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/productsdb_alzahrani_babkier";

    public void showAddProductScene(Stage primaryStage) {
        Stage addProductStage = new Stage();
        addProductStage.initModality(Modality.APPLICATION_MODAL);
        addProductStage.setTitle("Add a Product");
        addProductStage.setResizable(false);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label typeLabel = new Label("Type:");
        ChoiceBox<String> typeChoiceBox = new ChoiceBox<>();
        typeChoiceBox.getItems().addAll("SMARTPHONE", "COVER", "POWERBANK");

        Label modelLabel = new Label("Model:");
        TextField modelTextField = new TextField();

        Label priceLabel = new Label("Price:");
        TextField priceTextField = new TextField();

        Label countLabel = new Label("Count:");
        Slider countSlider = new Slider(0, 10, 0);
        countSlider.setShowTickLabels(true);
        countSlider.setShowTickMarks(true);
        countSlider.setMajorTickUnit(1);
        countSlider.setBlockIncrement(1);

        Label deliveryDateLabel = new Label("Delivery Date:");
        DatePicker deliveryDatePicker = new DatePicker();

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
        	boolean isValid = true;
        	// Validate type field
            if (typeChoiceBox.getValue() == null) {
                typeLabel.setTextFill(Color.RED);
                isValid = false;
            } else {
                typeLabel.setTextFill(Color.BLACK);
            }
            
         // Validate model field
            if (modelTextField.getText().isEmpty()) {
                modelLabel.setTextFill(Color.RED);
                isValid = false;
            } else {
                modelLabel.setTextFill(Color.BLACK);
            }

            // Validate price field
            if (priceTextField.getText().isEmpty()) {
                priceLabel.setTextFill(Color.RED);
                isValid = false;
            } else {
                try {
                    Float.parseFloat(priceTextField.getText());
                    priceLabel.setTextFill(Color.BLACK);
                } catch (NumberFormatException ex) {
                    priceLabel.setTextFill(Color.RED);
                    isValid = false;
                }
            }

            // Validate count field
            if (countSlider.getValue() == 0) {
                countLabel.setTextFill(Color.RED);
                isValid = false;
            } else {
                countLabel.setTextFill(Color.BLACK);
            }

            // Validate delivery date field
            if (deliveryDatePicker.getValue() == null) {
                deliveryDateLabel.setTextFill(Color.RED);
                isValid = false;
            } else {
                deliveryDateLabel.setTextFill(Color.BLACK);
            }
            
            String type = typeChoiceBox.getValue();
            String model = modelTextField.getText();
            double price = Double.parseDouble(priceTextField.getText());
            int count = (int) countSlider.getValue();
            Date deliveryDate = java.sql.Date.valueOf(deliveryDatePicker.getValue());
            
            if (isValid) {
            	if (insertProduct(type, model, price, count, deliveryDate)) {
                    showAlert("Success", "Product added successfully.", Alert.AlertType.INFORMATION);
                    modelTextField.clear();
                    priceTextField.clear();
                    countSlider.setValue(0);
                    deliveryDatePicker.setValue(null);
                } else {
                    showAlert("Error", "Failed to add product.", Alert.AlertType.ERROR);
                }
            }
           
        });

        gridPane.add(typeLabel, 0, 0);
        gridPane.add(typeChoiceBox, 1, 0);
        gridPane.add(modelLabel, 0, 1);
        gridPane.add(modelTextField, 1, 1);
        gridPane.add(priceLabel, 0, 2);
        gridPane.add(priceTextField, 1, 2);
        gridPane.add(countLabel, 0, 3);
        gridPane.add(countSlider, 1, 3);
        gridPane.add(deliveryDateLabel, 0, 4);
        gridPane.add(deliveryDatePicker, 1, 4);
        gridPane.add(saveButton, 1, 5);

        Scene scene = new Scene(gridPane, 300, 250);
        addProductStage.setScene(scene);
        addProductStage.showAndWait();
    }

    public void showSearchProductsScene(Stage primaryStage) {
        TableView<Product> table = new TableView<>();

        TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Product, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));

        TableColumn<Product, String> modelColumn = new TableColumn<>("Model");
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Product, Float> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> countColumn = new TableColumn<>("Count");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        TableColumn<Product, String> deliveryDateColumn = new TableColumn<>("Delivery Date");
        deliveryDateColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));

        table.getColumns().addAll(idColumn, typeColumn, modelColumn, priceColumn, countColumn, deliveryDateColumn);

        TextField searchField = new TextField();
        searchField.setPromptText("Search criteria");

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchProducts(searchField.getText(), table));

        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> getAllProducts(table));

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(searchField, searchButton, table, refreshButton);

        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Fetch all products initially
        getAllProducts(table);
    }
    
    public void getAllProducts(TableView<Product> table) {
        ObservableList<Product> productList = FXCollections.observableArrayList();

        try {
            // Establish a connection to the database
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);

            String sql = "SELECT * FROM ProductsTBL_abdurahmman_eyad";
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String type = resultSet.getString("Type");
                String model = resultSet.getString("Model");
                float price = resultSet.getFloat("Price");
                int count = resultSet.getInt("Count");
                Date deliveryDate = resultSet.getDate("DeliveryDate");

                Product product = new Product(id, type, model, price, count, deliveryDate);
                productList.add(product);
            }

            table.setItems(productList);

            // Close the database connection
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchProducts(String searchCriteria, TableView<Product> table) {
        ObservableList<Product> productList = FXCollections.observableArrayList();

        try {
            // Establish a connection to the database
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);

            String sql = "SELECT * FROM ProductsTBL_abdurahmman_eyad WHERE Type LIKE ? OR Model LIKE ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + searchCriteria + "%");
            statement.setString(2, "%" + searchCriteria + "%");

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String type = resultSet.getString("Type");
                String model = resultSet.getString("Model");
                float price = resultSet.getFloat("Price");
                int count = resultSet.getInt("Count");
                Date deliveryDate = resultSet.getDate("DeliveryDate");

                Product product = new Product(id, type, model, price, count, deliveryDate);
                productList.add(product);
            }

            table.setItems(productList);

            // Close the database connection
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDeleteProductScene(Stage primaryStage) {
        Stage deleteProductStage = new Stage();
        deleteProductStage.initModality(Modality.APPLICATION_MODAL);
        deleteProductStage.setTitle("Delete a Product");
        deleteProductStage.setResizable(false);

        Label idLabel = new Label("Product ID:");
        TextField idTextField = new TextField();

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            int id = Integer.parseInt(idTextField.getText());
            if (idExists(id)) {
                if (showConfirmationDialog("Confirmation", "Are you sure you want to delete this product?")) {
                    if (deleteProduct(id)) {
                        showAlert("Success", "Product deleted successfully.", Alert.AlertType.INFORMATION);
                        idTextField.clear();
                    } else {
                        showAlert("Error", "Failed to delete product.", Alert.AlertType.ERROR);
                    }
                } else {
                    showAlert("Cancel", "Deletion canceled.", Alert.AlertType.WARNING);
                }
            } else {
                errorLabel.setText("Product ID does not exist.");
            }
        });

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(10);
        vbox.getChildren().addAll(idLabel, idTextField, errorLabel, deleteButton);

        Scene scene = new Scene(vbox, 300, 150);
        deleteProductStage.setScene(scene);
        deleteProductStage.showAndWait();
    }

    private boolean validateInput(String type, String model, double price, int count, Date deliveryDate) {
        // Validation logic
        return true;
    }

    private boolean insertProduct(String type, String model, double price, int count, Date deliveryDate) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            PreparedStatement stmt = conn.prepareStatement("INSERT INTO productstbl_abdurahmman_eyad (Type, Model, Price, Count, DeliveryDate) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, type);
            stmt.setString(2, model);
            stmt.setDouble(3, price);
            stmt.setInt(4, count);
            stmt.setDate(5, new java.sql.Date(deliveryDate.getTime()));

            int rowsAffected = stmt.executeUpdate();

            stmt.close();
            conn.close();

            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateTableView(TableView<Product> tableView) {
        tableView.getItems().clear();

        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM productstbl_abdurahmman_eyad");

            while (rs.next()) {
                int id = rs.getInt("ID");
                String type = rs.getString("Type");
                String model = rs.getString("Model");
                double price = rs.getDouble("Price");
                int count = rs.getInt("Count");
                Date deliveryDate = rs.getDate("DeliveryDate");

                Product product = new Product(id, type, model, price, count, deliveryDate);
                tableView.getItems().add(product);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean idExists(int id) {
        // Check if the ID exists in the database
        return true;
    }

    private boolean deleteProduct(int id) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            PreparedStatement stmt = conn.prepareStatement("DELETE FROM productstbl_abdurahmman_eyad WHERE ID = ?");
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();

            stmt.close();
            conn.close();

            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        return alert.showAndWait().orElse(cancelButton) == confirmButton;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public class Product {
    	private int id;
        private String type;
        private String model;
        private double price;
        private int count;
        private Date deliveryDate;

        public Product(int id, String type, String model, double price, int count, Date deliveryDate) {
        	this.id = id;
            this.type = type;
            this.model = model;
            this.price = price;
            this.count = count;
            this.deliveryDate = deliveryDate;
        }

        // Getters and setters for the fields

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public Date getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(Date deliveryDate) {
            this.deliveryDate = deliveryDate;
        }
    }
}
