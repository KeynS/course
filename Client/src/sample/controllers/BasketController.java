package sample.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;
import sample.Connect;
import sample.Product;
import sample.User;

import javax.swing.*;

public class BasketController {

    private ObservableList<Product> oList;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
    private Button delButton;

    @FXML
    private TextField valueField;

    @FXML
    private Button changeItem;

    @FXML
    private Button clearFocus;

    @FXML
    private Button payButton;

    @FXML
    private ToggleGroup amountGroup;

    @FXML
    private Text textPrice;

    @FXML
    private RadioButton cardRadio;

    @FXML
    private RadioButton payRadio;

    @FXML
    private TextField addressField;

    @FXML
    private DatePicker calendarField;

    @FXML
    private TextField cardField;

    @FXML
    private TextField hourField;

    @FXML
    private TextField minuteField;

    @FXML
    private Text textError;

    @FXML
    void sendInformation(ActionEvent event) {
        textError.setVisible(false);
        boolean error = false;
        if(cardRadio.isSelected() && (cardField.getText().isEmpty() || !cardField.getText().matches("^[0-9]{4} [0-9]{4} [0-9]{4} [0-9]{4}$"))) {
            cardField.setStyle("-fx-border-color: #d90421;");
            error = true;
        }

        if(addressField.getText().isEmpty() || addressField.getText().length() < 6) {
            addressField.setStyle("-fx-border-color: #d90421;");
            error = true;
        }

        if(calendarField.getValue() == null) {
            calendarField.setStyle("-fx-border-color: #d90421;");
            error = true;
        }

        if(hourField.getText().isEmpty() || hourField.getText().length() != 2 || hourField.getText().charAt(0) == '0' || Integer.parseInt(hourField.getText()) < 10 ||  Integer.parseInt(hourField.getText()) > 23) {
            hourField.setStyle("-fx-border-color: #d90421;");
            error = true;
        }

        if(minuteField.getText().isEmpty() || minuteField.getText().length() != 2 || hourField.getText().charAt(0) != '0' && Integer.parseInt(hourField.getText()) > 59){
            minuteField.setStyle("-fx-border-color: #d90421;");
            error = true;
        }

        if(error)
            return;

        Connect.send("buy");
        Connect.send("" + oList.size());
        for(Product i : oList){
            Connect.send(""+i.getId());
            Connect.send(""+i.getCount());
            Connect.send(""+i.getPrice());
        }

        if(cardRadio.isSelected()){
            Connect.send(cardField.getText());
        } else {
            Connect.send("none");
        }

        Connect.send(addressField.getText());
        Connect.send(calendarField.getValue().toString());
        Connect.send(hourField.getText());
        Connect.send(minuteField.getText());


        int kol = Integer.parseInt(Connect.get());
        if(kol != 0) {
            textError.setVisible(true);
            String result = "";
            for (int i = 0; i < kol; i++) {
                if(result.isEmpty())
                    result += "Товар с id " + Connect.get() + " закзано больше чем имеется на складе. На складе: " + Connect.get();
            }
            textError.setText(result);
            return;
        }

        textPrice.setText("0 руб. 0 коп.");
        cardRadio.setDisable(true);
        cardField.setDisable(true);
        calendarField.setDisable(true);
        payRadio.setDisable(true);
        addressField.setDisable(true);
        hourField.setDisable(true);
        minuteField.setDisable(true);
        payButton.setDisable(true);

        for(int i=0; i < oList.size(); i++){
            int spent = Integer.parseInt(String.valueOf(oList.get(i).getPrice()).substring(0, String.valueOf(oList.get(i).getPrice()).indexOf("."))) * oList.get(i).getCount();
            User.spent += spent;
            User.bonus += (int) (spent * 0.1);
            User.purchased += oList.get(i).getCount();
        }

        for(int i=0; i < oList.size(); i++){
            tableInformation.getItems().remove(i);
        }
        MenuController.getBasketControl().setText("Корзина");
        oList.clear();
        MenuController.getBasket().clear();
        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Заказ выполнен.\nВам будет отправлено уведомление на почту");

    }

    private void clearError(){
        cardField.setStyle("");
        addressField.setStyle("");
        calendarField.setStyle("");
        minuteField.setStyle("");
        hourField.setStyle("");
    }


    @FXML
    void initialize() {
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
        oList = FXCollections.observableArrayList(MenuController.getBasket());
        tableInformation.setItems(oList);

        if(!oList.isEmpty()){
            double price = 0.0f;
            for(Product i : oList){
                price += i.getPrice() * i.getCount();
            }
            String[] text = String.valueOf(price).split("\\.");
            if(text[1].length() > 2)
                text[1] = text[1].substring(0,2);
            textPrice.setText(text[0] + " руб. " + text[1] + " коп.");
            if(User.activated) {
                cardRadio.setDisable(false);
                cardField.setDisable(false);
                calendarField.setDisable(false);
                payRadio.setDisable(false);
                addressField.setDisable(false);
                hourField.setDisable(false);
                minuteField.setDisable(false);
                payButton.setDisable(false);
            }
        }

        clearFocus.setOnAction(actionEvent -> {
            if(tableInformation.getSelectionModel().getSelectedItem() != null)
                tableInformation.getSelectionModel().select(null);
        });

        delButton.setOnAction(actionEvent -> {
            if(tableInformation.getSelectionModel().getSelectedItem() != null) {
                MenuController.getBasket().remove(tableInformation.getSelectionModel().getSelectedIndex());
                tableInformation.getItems().remove(tableInformation.getSelectionModel().getSelectedIndex());
                if(MenuController.getBasket().size() == 0) {
                    MenuController.getBasketControl().setText("Корзина");
                    textPrice.setText("0 руб. 0 коп.");
                    cardRadio.setDisable(true);
                    cardField.setDisable(true);
                    calendarField.setDisable(true);
                    payRadio.setDisable(true);
                    addressField.setDisable(true);
                    hourField.setDisable(true);
                    minuteField.setDisable(true);
                    payButton.setDisable(true);
                }
                else {
                    MenuController.getBasketControl().setText("Корзина (" + MenuController.getBasket().size() + ")");
                    double price = 0.0f;
                    for(Product i : oList){
                        price += i.getPrice() * i.getCount();
                    }
                    String[] text = String.valueOf(price).split("\\.");
                    if(text[1].length() > 2)
                        text[1] = text[1].substring(0,2);
                    textPrice.setText(text[0] + " руб. " + text[1] + " коп.");
                }
            }
        });

        changeItem.setOnAction(actionEvent -> {
            if(tableInformation.getSelectionModel().getSelectedItem() != null) {
                if(!valueField.getText().isEmpty()) {
                    tableInformation.getSelectionModel().getSelectedItem().setCount(Integer.parseInt(valueField.getText()));
                    oList.removeAll(oList);
                    oList = FXCollections.observableArrayList(MenuController.getBasket());
                    tableInformation.setItems(oList);

                    double price = 0.0f;
                    for(Product i : oList){
                        price += i.getPrice() * i.getCount();
                    }
                    String[] text = String.valueOf(price).split("\\.");
                    if(text[1].length() > 2)
                        text[1] = text[1].substring(0,2);
                    textPrice.setText(text[0] + " руб. " + text[1] + " коп.");
                }
            }
        });

        tableInformation.getSelectionModel().selectedItemProperty().addListener((ov, oldV, newV) -> {
            if(newV == null){
                clearFocus.setVisible(false);
                changeItem.setVisible(false);
                valueField.setVisible(false);
                delButton.setVisible(false);
            }
            else{
                clearFocus.setVisible(true);
                changeItem.setVisible(true);
                valueField.setVisible(true);
                delButton.setVisible(true);
            }
        });

        valueField.textProperty().addListener((ov, oldV, newV) -> {
            if(!newV.isEmpty() && !newV.matches("^[0-9]{0,3}$"))
                valueField.setText(oldV);
            else if(!newV.isEmpty() && newV.charAt(0) == '0')
                valueField.setText(oldV);
        });

        cardField.textProperty().addListener((ov, oldV, newV) -> {
            clearError();
            if(!newV.isEmpty() && !newV.matches("^[0-9\\s+]{0,19}$"))
                cardField.setText(oldV);
            else if(!newV.isEmpty() && newV.charAt(newV.length()-1) == ' ' && (newV.length() != 5 && newV.length() != 10 && newV.length() != 15))
                cardField.setText(oldV);
            else if(!newV.isEmpty() && (newV.length() == 4 || newV.length() == 9 || newV.length() == 14) && oldV.length() < newV.length())
                cardField.setText(newV += " ");
        });

        cardField.focusedProperty().addListener((observableValue, s, t1) -> {
            clearError();
        });

        addressField.focusedProperty().addListener((observableValue, s, t1) -> {
            clearError();
        });

        hourField.textProperty().addListener((ov, oldV, newV) -> {
            clearError();
            if(!newV.isEmpty() && !newV.matches("^[0-9]{0,2}$"))
                hourField.setText(oldV);
        });

        calendarField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            clearError();
        });

        hourField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            clearError();
        });

        minuteField.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            clearError();
        });

        minuteField.textProperty().addListener((ov, oldV, newV) -> {
            if(!newV.isEmpty() && !newV.matches("^[0-9]{0,2}$"))
                minuteField.setText(oldV);
        });

        payRadio.setOnAction(actionEvent -> {
            cardField.setVisible(false);
        });

        cardRadio.setOnAction(actionEvent -> {
            cardField.setVisible(true);
        });

        calendarField.setDayCellFactory(new Callback<DatePicker, DateCell>() {
            @Override
            public DateCell call(DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || item.compareTo(today) < 0 || item.compareTo(today) == 0);
                    }
                };
            }
        });
    }
}
