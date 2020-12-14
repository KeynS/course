package sample.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.util.Callback;
import sample.Connect;
import sample.Product;
import sample.User;

public class DeleteController {

    private ObservableList<Product> products = FXCollections.observableArrayList();

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
    private TableColumn<Product, String> descriptionColumn;

    @FXML
    private TableColumn<Product, Boolean> trendColumn;

    @FXML
    private TableColumn<Product, Integer> idCategoryColumn;

    @FXML
    private TableColumn<Product, String> urlColumn;

    @FXML
    private Button addButton;

    @FXML
    void sendInformation(ActionEvent event) {
        if(tableInformation.getSelectionModel().getSelectedItem() == null)
            return;

        Connect.send("deleteProducts");
        Connect.send("" + tableInformation.getSelectionModel().getSelectedItem().getId());

        if(Connect.get().equals("#code1"))
            tableInformation.getItems().remove(tableInformation.getSelectionModel().getSelectedIndex());

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
    }
}

