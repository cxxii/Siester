//package org.cxxii.gui;
//
//import javafx.application.Application;
//import javafx.beans.property.SimpleDoubleProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXMLLoader;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//import org.cxxii.search.FuzzySearch;
//
//import javax.naming.directory.SearchResult;
//
//
//public class Gui extends Application {
//
//    //private BackendService backendService = new BackendService();
//    private TableView<Long> tableView;
//    private ObservableList<Long> searchResults;
//
//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("Siester");
//
//        // Create a BorderPane as the main layout
//        BorderPane borderPane = new BorderPane();
//
//        // Top: MenuBar
//        MenuBar menuBar = new MenuBar();
//        menuBar.getStyleClass().add("menu-bar");
//        Menu menuFile = new Menu("File");
//        Menu menuEdit = new Menu("Edit");
//        Menu menuTools = new Menu("Tools");
//        Menu menuHelp = new Menu("Help");
//        menuBar.getMenus().addAll(menuFile, menuEdit, menuTools, menuHelp);
//        borderPane.setTop(menuBar);
//
//        // Create a HBox for the search bar
//        HBox searchBar = new HBox();
//        searchBar.setPadding(new Insets(10));
//        searchBar.setSpacing(5);
//
//        // TextField for search input
//        TextField searchField = new TextField();
//        searchField.setPromptText("Search...");
//
//        // Button for triggering search
//        Button searchButton = new Button("Search");
//
//        // Add search field and button to the HBox
//        searchBar.getChildren().addAll(searchField, searchButton);
//
//        // Create a VBox to hold the MenuBar and the SearchBar
//        VBox topContainer = new VBox();
//        topContainer.getChildren().addAll(menuBar, searchBar);
//
//        // Set the VBox to the top of the BorderPane
//        borderPane.setTop(topContainer);
//
//        // Left: TreeView for Media
//        TreeItem<String> rootItem = new TreeItem<>("Media");
//        rootItem.setExpanded(true);
//        TreeItem<String> audioItem = new TreeItem<>("Audio");
//        TreeItem<String> videoItem = new TreeItem<>("Video");
//        TreeItem<String> programsItem = new TreeItem<>("Programs");
//        rootItem.getChildren().addAll(audioItem, videoItem, programsItem);
//        TreeView<String> treeView = new TreeView<>(rootItem);
//        treeView.getStyleClass().add("tree-view");
//        treeView.setPrefWidth(150);
//        borderPane.setLeft(treeView);
//
//        // Center: TableView for search results
//        tableView = new TableView<>();
//        tableView.getStyleClass().add("table-view");
//        TableColumn<Long, String> qualityColumn = new TableColumn<>("Quality");
//        TableColumn<Long, String> nameColumn = new TableColumn<>("Name");
//        TableColumn<Long, String> typeColumn = new TableColumn<>("Type");
//        TableColumn<Long, String> sizeColumn = new TableColumn<>("Size");
//        TableColumn<Long, String> speedColumn = new TableColumn<>("Speed");
//        TableColumn<Long, String> bitrateColumn = new TableColumn<>("Bitrate");
//        tableView.getColumns().addAll(qualityColumn, nameColumn, typeColumn, sizeColumn, speedColumn, bitrateColumn);
//        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        borderPane.setCenter(tableView);
//
//        // Bottom: Downloads TableView and ProgressBar
//        VBox bottomBox = new VBox();
//        bottomBox.getStyleClass().add("vbox");
//        bottomBox.setPadding(new Insets(10));
//        bottomBox.setSpacing(5);
//
//        TableView<Download> downloadsTable = new TableView<>();
//        downloadsTable.getStyleClass().add("table-view");
//        TableColumn<Download, String> fileNameColumn = new TableColumn<>("Name");
//        TableColumn<Download, String> statusColumn = new TableColumn<>("Status");
//        TableColumn<Download, Double> progressColumn = new TableColumn<>("Progress");
//        TableColumn<Download, String> speedColumnD = new TableColumn<>("Speed");
//        downloadsTable.getColumns().addAll(fileNameColumn, statusColumn, progressColumn, speedColumnD);
//        downloadsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//
//        ProgressBar progressBar = new ProgressBar(0);
//        progressBar.getStyleClass().add("progress-bar");
//
//        bottomBox.getChildren().addAll(downloadsTable, progressBar);
//        borderPane.setBottom(bottomBox);
//
//        // Initialize search results list
//        searchResults = FXCollections.observableArrayList();
//        tableView.setItems(searchResults);
//
//        // Set cell value factories
//        qualityColumn.setCellValueFactory(cellData -> cellData.getValue().qualityProperty());
//        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
//        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
//        sizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty());
//        speedColumn.setCellValueFactory(cellData -> cellData.getValue().speedProperty());
//        bitrateColumn.setCellValueFactory(cellData -> cellData.getValue().bitrateProperty());
//
//        // Add event handler for search button
//        searchButton.setOnAction(event -> {
//            String query = searchField.getText();
//            if (!query.isEmpty()) {
//                performSearch(query);
//            }
//        });
//
//        // Create and set the scene
//        Scene scene = new Scene(borderPane, 800, 600);
//
//        // Ensure the path is correct and not null
//        String cssPath = getClass().getResource("/style.css").toExternalForm();
//        if (cssPath != null) {
//            scene.getStylesheets().add(cssPath);
//        } else {
//            System.out.println("CSS file not found");
//        }
//
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private void performSearch(String query) {
//        // Call the backend search method and update the table view
//        searchResults.clear();
//        searchResults.addAll(FuzzySearch.fuzzySearchFiles(query));
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}