package objectorientedprogramming;

import java.io.File;
import java.util.Optional;
import java.util.*;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.FileChooser;
import javafx.event.EventHandler;
import javafx.util.converter.IntegerStringConverter;

/**
 * Graphic user interface for the shopping app.
 * 
 * @author Laura Kanerva.
 */
public class App extends Application {
    private TextField itemInput;
    private TextField amountInput;
    private TableView tableView;
    private FileChooser fileChooser;
    private Button addButton;
    private Button removeButton;
    private Button clearButton;
    private HBox hbox;
    private VBox vbox;
    private Stage window;

    /**
     * Method initializes the whole window view.
     *
     * @param window The stage to be shown
     */
    @Override
    public void start(Stage window) {
        this.window = window;
        tableView = new TableView();
        tableView.setEditable(true);
        fileChooser = new FileChooser();

        createColumns();

        tableView.setPlaceholder(new Label("No items to display"));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        createInputFields();
        createButtons();
        createHBox();
        createVBox();

        Scene content = new Scene(vbox);

        window.setTitle("Shopping List");
        window.initStyle(StageStyle.DECORATED);
        window.setScene(content);
        window.show();
    }

    /**
     * Main method, prints author's name to the console and calls launch-method.
     *
     * @param args an array of command-line arguments for the application
     */
    public static void main(String [] args) {
        System.out.println("Author: Laura Kanerva");
        launch(args);
    }

    /**
     * Creates Amount and Item columns.
     */
    private void createColumns() {
        TableColumn<Item, Integer> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));

        amountColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        amountColumn.setOnEditCommit(
            new EventHandler<CellEditEvent<Item, Integer>>() {
                @Override
                public void handle(CellEditEvent<Item, Integer> t) {
                    ((Item) t.getTableView().getItems().get(t.getTablePosition().getRow())).setItem(t.getNewValue().toString());
                }
            }
        );

        TableColumn<Item, String> itemColumn = new TableColumn<>("Item");
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("item"));

        itemColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        itemColumn.setOnEditCommit(
            new EventHandler<CellEditEvent<Item, String>>() {
                @Override
                public void handle(CellEditEvent<Item, String> t) {
                    ((Item) t.getTableView().getItems().get(t.getTablePosition().getRow())).setItem(t.getNewValue());
                }
            }
        );

        tableView.getColumns().add(amountColumn);
        tableView.getColumns().add(itemColumn);
    }

    /**
     * Creates input fields to the window.
     */
    private void createInputFields() {
        //amount input
        amountInput = new TextField();
        amountInput.setPromptText("Amount");
        amountInput.setMinWidth(100);

        //item input
        itemInput = new TextField();
        itemInput.setPromptText("Item");
    }

    /**
     * Creates a horizontal box for input fields and buttons.
     */
    private void createHBox() {
        hbox = new HBox();
        hbox.setPadding(new Insets(10, 10, 10, 10));
        hbox.setSpacing(10);
        hbox.getChildren().addAll(amountInput, itemInput, addButton, removeButton, clearButton);
    }

    /**
     * Creates a vertical box for all app elements.
     */
    private void createVBox() {
        vbox = new VBox();
        vbox.getChildren().addAll(createMenuBar(window), tableView, hbox);
        vbox.setPadding(new Insets(0, 0, 10, 0));
    }

    /**
     * Creates buttons in the window.
     */
    private void createButtons() {
        addButton = new Button("Add item");
        addButton.setOnAction(e -> addButtonClicked());
        removeButton = new Button("Remove item");
        removeButton.setOnAction(e -> removeButtonClicked());
        clearButton = new Button("Clear all");
        clearButton.setOnAction(e -> tableView.getItems().clear());
    }

    /**
     * Creates a new item from the user input and clears the input fields.
     */
    private void addButtonClicked() {
        Item newItem = new Item();
        newItem.setAmount(Integer.parseInt(amountInput.getText()));
        newItem.setItem(itemInput.getText());
        tableView.getItems().add(newItem);
        amountInput.clear();
        itemInput.clear();
    }

    /**
     * Removes the selected item from the list.
     */
    private void removeButtonClicked() {
        tableView.getItems().remove(tableView.getSelectionModel().getSelectedItem());
    }

    /**
     * Writes shopping list content into a json table.
     *
     * @return table of JSONObjects
     */
    private JsonObject[] saveToJson() {
        ObservableList<Object> objs = tableView.getItems();
        JsonObject[] jsons = new JsonObject[objs.size()];

        int i = 0;
        for(Object o : objs) {
            Item item = (Item)o;
            JsonObject jo = new JsonObject();
            jo.add("item", item.getItem());
            jo.add("amount", item.getAmount());
            jsons[i++] = jo;
        }

        return jsons;
    }

    /**
     * Opens authorization dialog and lets user save their shopping list to Dropbox.
     */
    private void saveToDropbox() {
        DropboxConnector dbc = new DropboxConnector();
        String url = dbc.authorizeUser();

        TextArea textArea = new TextArea("1. Go to " + url + "\n\n2. Click \"Allow\" (you might have to log in first).\n\n3. Copy the authorization code.");
        textArea.setEditable(false);
        textArea.setWrapText(true);

        TextInputDialog authorizationDialog = new TextInputDialog();
        authorizationDialog.setTitle("Authorization");
        authorizationDialog.getDialogPane().setHeader(textArea);
        authorizationDialog.setContentText("Enter the authorization code here: ");
        Optional<String> code = authorizationDialog.showAndWait();
        dbc.logIn(code);

        TextInputDialog td = new TextInputDialog();
        td.setTitle("Save to Dropbox");
        td.setHeaderText("Enter filename:");
        Optional<String> fileName = td.showAndWait();
        if (fileName.isPresent()) {
            dbc.uploadFile(fileName, saveToJson());
        }
    }

    /**
     * Imports a list to the app.
     * 
     * @param file file to import
     */
    private void importList(File file) {
        JsonUtil jutil = new JsonUtil();
        ArrayList<JsonObject> jsons = jutil.readJson(file);

        for(JsonObject jo : jsons) {
            Item newItem = new Item();
            Map<String, Object> jsonMap = jo.getMap();
            Set keys = jsonMap.keySet();
            for (Object key : keys) {
                if (key.equals("item")) {
                    newItem.setItem(jsonMap.get(key).toString());
                } else {
                    newItem.setAmount(Integer.parseInt(jsonMap.get(key).toString()));
                }
            }
            tableView.getItems().add(newItem);
        }
    }

    /**
     * Creates a menubar on top of the window.
     *
     * @param window the stage where menubar is wanted
     * @return the complete menubar with all wanted elements
     */
    private MenuBar createMenuBar(Stage window) {
        // File menu
        Menu file = new Menu("File");
        MenuItem saveJson = new MenuItem("Save to JSON");
        saveJson.setOnAction(e -> {
            File selectedFile = fileChooser.showSaveDialog(window);
            if(selectedFile != null) {
                JsonUtil jutil = new JsonUtil();
                jutil.writeToJson(selectedFile, saveToJson());
            } else {
                System.out.println("Choose a file!");
            }
        });
        MenuItem saveDropbox = new MenuItem("Save to Dropbox");
        saveDropbox.setOnAction(e -> saveToDropbox());
        MenuItem importJson = new MenuItem("Import JSON file");
        importJson.setOnAction(e -> {
            tableView.getItems().clear();
            File fileToImport = fileChooser.showOpenDialog(window);
            if(fileToImport != null) {
                importList(fileToImport);
            } else {
                System.out.println("Choose a file!");
            }
        });
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> System.exit(0));
        file.getItems().addAll(saveJson, saveDropbox, importJson, separator, exit);

        // About menu
        Menu help = new Menu("Help");
        MenuItem howTo = new MenuItem("How to use this app");
        help.setOnAction(e -> showHelp());
        help.getItems().add(howTo);

        // The Menubar
        MenuBar menubar = new MenuBar();
        menubar.getMenus().addAll(file, help);

        return menubar;
    }

    /**
     * Shows the Help dialog with instructions on how to use the app.
     */
    private void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText("How to use this app");
        alert.setContentText("1. Enter the name and amount of a product you want to buy.\n\n2. Click \"Add item\".\n\n3. Repeat until your shopping list is complete.");

        alert.showAndWait();
    }

}