// Store.java
package Store;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Store extends Application {
    private ProductManager productManager;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        productManager = new ProductManager();

        primaryStage.setTitle("Product Manager");
        primaryStage.setResizable(false);

        MenuBar menuBar = new MenuBar();
        Menu productsMenu = new Menu("Products");
        MenuItem addMenuItem = new MenuItem("Add");
        MenuItem searchMenuItem = new MenuItem("Search");
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem exitMenuItem = new MenuItem("Exit");

        addMenuItem.setOnAction(e -> productManager.showAddProductScene(primaryStage));
        searchMenuItem.setOnAction(e -> productManager.showSearchProductsScene(primaryStage));
        deleteMenuItem.setOnAction(e -> productManager.showDeleteProductScene(primaryStage));
        exitMenuItem.setOnAction(e -> primaryStage.close());

        productsMenu.getItems().addAll(addMenuItem, searchMenuItem, deleteMenuItem, exitMenuItem);
        menuBar.getMenus().add(productsMenu);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(menuBar);

        Scene scene = new Scene(mainLayout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Add project information
        Label projectInfoLabel = new Label("This project made by\n" +
            "Eyad Ahmad Babkier\n" +
            "Student Number: 441000998\n" +
            "Group: 2\n" +
            "Email: s441000998@st.uqu.edu.sa\n" +
            "Abdurahmman Khamis Alzahrani\n" +
            "Student Number: 441005040\n" +
            "Group: 2\n" +
            "Email: s441005040@st.uqu.edu.sa");
        projectInfoLabel.setPadding(new Insets(10));
        mainLayout.setCenter(projectInfoLabel);
    }
}
