package sample.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.Connect;

import javax.swing.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class AddController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField nameField;

    @FXML
    private TextField priceField;

    @FXML
    private TextField countField;

    @FXML
    private Button reviewButton;

    final FileChooser fileChooser = new FileChooser();

    private ObservableList<String> choice = FXCollections.observableArrayList("Компьютеры", "Ноутбуки", "Периферия", "Аксессуары", "Разное");

    @FXML
    private ComboBox<String> selectBox;

    @FXML
    private TextField urlField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ImageView plusButton;

    @FXML
    private ImageView minusButton;

    @FXML
    private Button addButton;

    @FXML
    private CheckBox trendSelect;


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

        Connect.send("createNewProduct");
        Connect.send(nameField.getText());
        Connect.send(priceField.getText());
        Connect.send(countField.getText());
        Connect.send(selectBox.getValue());
        Connect.send(descriptionArea.getText());
        Connect.send(urlField.getText());
        Connect.send("" + trendSelect.isSelected());
        String status = Connect.get();

        if(status.equals("#code1")) {
            nameField.clear();
            priceField.clear();
            countField.clear();
            selectBox.setValue("Выберите пункт");
            descriptionArea.clear();
            urlField.clear();
            trendSelect.setSelected(false);
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Товар добавлен в каталог");
        }
    }

    private void clearError(){
        nameField.setStyle("");
        priceField.setStyle("");
        countField.setStyle("");
        selectBox.setStyle("");
        descriptionArea.setStyle("");
    }

    @FXML
    void initialize() {
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

        descriptionArea.focusedProperty().addListener((ov, oldV, newV) -> {
            if(newV)
                clearError();
        });
    }

}

