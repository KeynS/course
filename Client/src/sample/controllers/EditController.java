package sample.controllers;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import sample.Connect;
import sample.Product;

public class EditController {

    private ObservableList<Product> products = FXCollections.observableArrayList();

    final private FileChooser fileChooser = new FileChooser();

    final private ObservableList<String> choice = FXCollections.observableArrayList("Компьютеры", "Ноутбуки", "Периферия", "Аксессуары", "Разное");


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Pane paneTable;

    @FXML
    private TableView<Product> tableInformation;

    @FXML
    private TableColumn<Product, Integer> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, Double> priceColumn;

    @FXML
    private TableColumn<Product, Integer> valueColumn;

    @FXML
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private TableColumn<Product, Boolean> trendColumn;

    @FXML
    private TableColumn<Product, Integer> idCategoryColumn;

    @FXML
    private TableColumn<Product, String> urlColumn;

    @FXML
    private Button editButton;

    @FXML
    private Pane paneEdit;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField countField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ImageView plusButton;

    @FXML
    private ImageView minusButton;

    @FXML
    private TextField urlField;

    @FXML
    private Button reviewButton;

    @FXML
    private ComboBox<String> selectBox;

    @FXML
    private Button addButton;

    @FXML
    private CheckBox trendSelect;

    final private ObservableList<String> informationSelect = FXCollections.observableArrayList("Компьютеры", "Ноутбуки", "Периферия", "Аксессуары", "Разное");

    private Product productSelect;

    @FXML
    void editStart(ActionEvent event) {
        if(tableInformation.getSelectionModel().getSelectedItem() == null)
            return;

        productSelect = tableInformation.getSelectionModel().getSelectedItem();

        paneTable.setVisible(false);
        nameField.setText(productSelect.getName());
        priceField.setText(String.valueOf(productSelect.getPrice()));
        countField.setText(String.valueOf(productSelect.getCount()));
        selectBox.setValue(informationSelect.get(productSelect.getCategoryId()-1));
        descriptionArea.setText(productSelect.getDesc());
        urlField.setText(productSelect.getUrl());
        trendSelect.setSelected(productSelect.isTrend());
        paneEdit.setVisible(true);
    }

    @FXML
    void sendInformation(ActionEvent event) {
        boolean error = false;
        if(nameField.getText().isEmpty()) {
            nameField.setStyle("-fx-border-color: #d90421;");
            error = true;
        }

        if(priceField.getText().isEmpty()) {
            priceField.setStyle("-fx-border-color: #d90421;");
            error = true;
        }

        if(countField.getText().isEmpty()) {
            countField.setStyle("-fx-border-color: #d90421;");
            error = true;
        }

        if(selectBox.getValue().equals("Выберите пункт")) {
            selectBox.setStyle("-fx-border-color: #d90421;");
            error = true;
        }

        if(descriptionArea.getText().isEmpty()){
            descriptionArea.setStyle("-fx-border-color: #d90421;");
            error = true;
        }

        if(error)
            return;

        Connect.send("editProduct");
        Connect.send(String.valueOf(productSelect.getId())); // id
        Connect.send(nameField.getText());
        Connect.send(priceField.getText());
        Connect.send(countField.getText());
        Connect.send(selectBox.getValue());
        Connect.send(descriptionArea.getText());
        Connect.send(urlField.getText());
        Connect.send("" + trendSelect.isSelected());
        String status = Connect.get();

        if(status.equals("#code1")) {
            getData();
            tableInformation.setItems(products);
        }
        paneEdit.setVisible(false);
        paneTable.setVisible(true);
    }

    private void clearError(){
        nameField.setStyle("");
        priceField.setStyle("");
        countField.setStyle("");
        selectBox.setStyle("");
        descriptionArea.setStyle("");
    }

    private boolean intToBoolean(int input) {
        if((input==0)||(input==1)) {
            return input!=0;
        }else {
            throw new IllegalArgumentException("Входное значение может быть равно только 0 или 1 !");
        }
    }

    void getData() {
        products.clear();
        Connect.send("viewAllProduct");
        String message = Connect.get();
        if(message.equals("#code1")){
            return;
        }
        String[] productsStr = message.split("\n");
        for(String productStr : productsStr){
            String[] product = productStr.split(";");
            products.add(new Product(Integer.parseInt(product[0]),product[1],Double.parseDouble(product[2]),
                    Integer.parseInt(product[3]),product[4], intToBoolean(Integer.parseInt(product[5])), Integer.parseInt(product[6]),
                    product[7]));
        }
    }


    @FXML
    void initialize() {
        getData();
        idColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        nameColumn.setCellFactory(new Callback<TableColumn<Product, String>, TableCell<Product, String>>() {
            @Override
            public TableCell<Product, String> call(TableColumn<Product, String> productStringTableColumn) {
                return new TableCell<Product, String>() {
                    private Text text;

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if ( text != null ) {
                            text.textProperty().unbind();
                        }
                        if ( empty || item == null ) {
                            setGraphic(null);
                        } else {
                            if ( text == null ) {
                                text = new Text();
                                text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
                            }
                            text.textProperty().bind(itemProperty());
                            setGraphic(text);
                        }
                    }
                };
            }
        });
        priceColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("count"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("desc"));
        descriptionColumn.setCellFactory(new Callback<TableColumn<Product, String>, TableCell<Product, String>>() {
            @Override
            public TableCell<Product, String> call(TableColumn<Product, String> productStringTableColumn) {
                return new TableCell<Product, String>() {
                    private Text text;

                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if ( text != null ) {
                            text.textProperty().unbind();
                        }
                        if ( empty || item == null ) {
                            setGraphic(null);
                        } else {
                            if ( text == null ) {
                                text = new Text();
                                text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
                            }
                            text.textProperty().bind(itemProperty());
                            setGraphic(text);
                        }
                    }
                };
            }
        });
        trendColumn.setCellValueFactory(new PropertyValueFactory<Product, Boolean>("trend"));
        idCategoryColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("categoryId"));
        urlColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("url"));
        tableInformation.setItems(products);

        selectBox.getItems().addAll(choice);
        selectBox.setValue("Выберите пункт");
        fileChooser.setTitle("Выберите картинку в формте jpg или png");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"));

        reviewButton.setOnAction(actionEvent -> {
            urlField.clear();
            Stage stage = (Stage) reviewButton.getScene().getWindow();
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                urlField.setText(file.toString());
            }
        });

        plusButton.setOnMouseClicked(mouseEvent -> {
            if(countField.getText().isEmpty()){
                countField.setText("1");
            }
            else{
                countField.setText(String.valueOf(Integer.parseInt(countField.getText())+1));
            }
        });

        minusButton.setOnMouseClicked(mouseEvent -> {
            if(countField.getText().isEmpty()){
                countField.setText("0");
            }
            else{
                countField.setText(String.valueOf(Integer.parseInt(countField.getText())-1));
            }
        });

        countField.textProperty().addListener((ov,oldV,newV)->{
            if(!newV.isEmpty() && !newV.matches("^[0-9]+$")) {
                if(oldV.isEmpty())
                    countField.setText("");
                else
                    countField.setText(oldV);
            }
            else if(!newV.isEmpty() && (newV.matches("^[0-9]+$") && (Integer.parseInt(newV) < 0 || Integer.parseInt(newV) > 1000))) {
                countField.setText(oldV);
            }
        });

        descriptionArea.textProperty().addListener((ov,oldV,newV)-> {
            if(!newV.isEmpty() && descriptionArea.getStyleClass().get(4).equals("notext")){
                descriptionArea.getStyleClass().remove(4);
                descriptionArea.getStyleClass().add("text");
            }else if(newV.isEmpty()){
                descriptionArea.getStyleClass().remove(4);
                descriptionArea.getStyleClass().add("notext");
            }
            else if(!newV.isEmpty() && newV.length() > 500){
                descriptionArea.setText(oldV);
            }
            else if(!newV.isEmpty() && newV.charAt(newV.length()-1) == '\n'){
                descriptionArea.setText(oldV);
            }
        });
        nameField.focusedProperty().addListener((ov, oldV, newV) -> {
            if(newV)
                clearError();
        });

        urlField.textProperty().addListener((ov, oldV, newV) -> {
            if(!newV.isEmpty()){
                Tooltip tooltip = new Tooltip();
                ImageView image = new ImageView();
                URI uri = null;
                try {
                    uri = new URI("file:///" + newV.replace("\\", "/"));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                if(uri != null) {
                    image.setImage(new Image(uri.toString()));
                    image.setPreserveRatio(true);
                    image.setFitHeight(200);
                    image.setFitWidth(200);

                }
                tooltip.setGraphic(image);
                tooltip.setShowDelay(Duration.millis(650));
                tooltip.setShowDuration(Duration.minutes(1));

                Tooltip.install(urlField, tooltip);
            }
            else{
                Tooltip.install(urlField, null);
            }
        });

        nameField.textProperty().addListener((ov,oldV,newV) ->{
            if(!newV.isEmpty() && newV.length() > 50)
                nameField.setText(oldV);
        });

        priceField.textProperty().addListener((ov, oldV, newV) ->{
            if(!newV.isEmpty() && !newV.matches("^[0-9]{1,5}[\\.]{0,1}[0-9]{0,2}$")){
                priceField.setText(oldV);
            }
        });
        priceField.focusedProperty().addListener((ov, oldV, newV) -> {
            if(newV)
                clearError();
        });

        countField.focusedProperty().addListener((ov, oldV, newV) -> {
            if(newV)
                clearError();
        });

        selectBox.focusedProperty().addListener((ov, oldV, newV) -> {
            if(newV)
                clearError();
        });

        descriptionArea.focusedProperty().addListener((ov, oldV, newV) -> {
            if(newV)
                clearError();
        });
    }
}