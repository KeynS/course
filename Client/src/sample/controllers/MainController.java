package sample.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import sample.Connect;
import sample.Product;

public class MainController {
    final int widthWindow = 195; // Ширина окна
    final int heightWindow = 280; // Длина окна
    final int leftMargine = 55; // Отступ сбоку
    final int topMargine = 20; // Отступ сверху
    final int buttonMargine = 15; // Отступ от картинки до кнопки
    final int withWindow = 40; // Отступ от одного окна до другого по вертикали
    final int margineWindow = 35; // отступ по вертикали от одного окна к другому
    final int btnHeight = 45; // Длина кнопик

    private List<Product> products = new ArrayList<>();

    private ObservableList<String> choice = FXCollections.observableArrayList("Популярные", "Новые", "Дорогие", "Дешёвые");


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane paneScroll;

    @FXML
    private ComboBox<String> choiceBox;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchLine;

    private void createTable(List<Product> list) {
        int value = list.size();
        int columns = (int)Math.ceil(value/4.0f);
        int size = ((topMargine * 2 + heightWindow * columns + btnHeight * columns + buttonMargine * columns + margineWindow * columns) - 30);
        paneScroll.getChildren().clear();
        paneScroll.setPrefHeight(size < 636? 636 : size);

        if(choiceBox.getValue().equals("Популярные")){
            Collections.sort(list, new Comparator<Product>() {
                public int compare(Product o1, Product o2) {
                    return Boolean.valueOf(o2.isTrend()).compareTo(Boolean.valueOf(o1.isTrend()));
                }
            });
        }
        else if(choiceBox.getValue().equals("Новые")){
            Collections.sort(list, new Comparator<Product>() {
                public int compare(Product o1, Product o2) {
                    return Integer.valueOf(o2.getId()).compareTo(Integer.valueOf(o1.getId()));
                }
            });
        }
        else if(choiceBox.getValue().equals("Дорогие")){
            Collections.sort(list, new Comparator<Product>() {
                public int compare(Product o1, Product o2) {
                    return Double.valueOf(o2.getPrice()).compareTo(Double.valueOf(o1.getPrice()));
                }
            });
        }
        else if(choiceBox.getValue().equals("Дешёвые")){
            Collections.sort(list, new Comparator<Product>() {
                public int compare(Product o1, Product o2) {
                    return Double.valueOf(o1.getPrice()).compareTo(Double.valueOf(o2.getPrice()));
                }
            });
        }

        int index = 0;

        for(int i=0; i < columns; i++){
            for(int j=0; j < 4; j++){
                if(value == index) return;
                Pane createPane = new Pane();
                paneScroll.getChildren().add(createPane);
                createPane.setPrefWidth(widthWindow);
                createPane.setPrefHeight(heightWindow);
                createPane.setLayoutX(leftMargine + widthWindow * j + withWindow * j);
                createPane.setLayoutY(topMargine + heightWindow * i + buttonMargine * i + margineWindow * i + btnHeight * i);
                createPane.setStyle("-fx-border-radius:25px;-fx-background-radius:25px;-fx-border-color:#000000;");

                ImageView createImage = new ImageView();
                URI uri = null;
                try {
                    uri = new URI("file:///" + list.get(index).getUrl());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                if(uri != null) {
                    createImage.setImage(new Image(uri.toString()));
                    createImage.setPreserveRatio(true);
                    createImage.setFitHeight(140);
                    createImage.setFitWidth(140);
                    createImage.setY(2);
                    createImage.setX((widthWindow - createImage.getFitWidth()) / 2.0f);

                    createPane.getChildren().add(createImage);
                }

                Text title = new Text(list.get(index).getName());
                title.setWrappingWidth(190.0f);
                title.setX(2.5f);
                title.setY(155);
                title.setTextAlignment(TextAlignment.CENTER);

                createPane.getChildren().add(title);

                if(list.get(index).isTrend()){

                    Label createLabel = new Label("Trend");

                    createLabel.setPrefWidth(75);
                    createLabel.setPrefHeight(35);
                    createLabel.setLayoutX(110);
                    createLabel.setLayoutY(10);
                    createLabel.getStyleClass().add("trendLabel");

                    createPane.getChildren().add(createLabel);
                }

                Text description = new Text();

                String shortDesc;
                if(list.get(index).getDesc().length() > 90) {
                    shortDesc = list.get(index).getDesc().substring(0, 90) + "...";
                    Tooltip tooltip = new Tooltip(list.get(index).getDesc());
                    tooltip.setShowDelay(Duration.millis(660));
                    Tooltip.install(description, tooltip);
                    description.setCursor(Cursor.HAND);
                }
                else
                    shortDesc = list.get(index).getDesc();

                description.setText(shortDesc);
                description.setWrappingWidth(195.0f);
                description.setY(195);
                description.setTextAlignment(TextAlignment.CENTER);
                description.setStyle("-fx-fill: #8f8e8c;");

                createPane.getChildren().add(description);

                String[] strPrice = String.valueOf(list.get(index).getPrice()).split("\\.");
                Text price = new Text(strPrice[0] + " руб." + (strPrice[1].equals("0")? "" : " " + strPrice[1] + " коп."));
                price.setWrappingWidth(195.0f);
                price.setY(260);
                price.setTextAlignment(TextAlignment.CENTER);
                price.setStyle("-fx-font-size: 18px;-fx-fill: #4465ee;");

                createPane.getChildren().add(price);

                Button createButton = new Button();
                paneScroll.getChildren().add(createButton);
                createButton.setPrefWidth(widthWindow);
                createButton.setPrefHeight(btnHeight);
                createButton.setLayoutX(leftMargine + widthWindow * j + withWindow * j);
                createButton.setLayoutY(topMargine + heightWindow * (i+1) + buttonMargine * (i+1) + margineWindow * i + btnHeight * i);
                createButton.getStyleClass().add("buttonLogin");
                createButton.getStyleClass().add("paneShadow");
                createButton.setText("В корзину");
                for(int k=0; k < MenuController.getBasket().size(); k++) {
                    if (MenuController.getBasket().get(k).getId() == list.get(index).getId()) {
                        createButton.setText("Добавлено");
                        createButton.setStyle("-fx-background-color: #000000;");
                        break;
                    }
                }
                createButton.setCursor(Cursor.HAND);

                int finalIndex = index;
                createButton.setOnAction(actionEvent -> {
                    int id = list.get(finalIndex).getId();
                    if(createButton.getText().equals("В корзину")) {
                        MenuController.getBasket().add(MenuController.getBasket().size(), list.get(finalIndex));
                        MenuController.getBasket().get(MenuController.getBasket().size() - 1).setCount(1);
                        createButton.setText("Добавлено");
                        createButton.setStyle("-fx-background-color: #000000;");
                        if(MenuController.getBasketControl().getText().equals("Корзина")){
                            MenuController.getBasketControl().setText("Корзина (1)");
                        }
                        else{
                            int startNum = MenuController.getBasketControl().getText().indexOf('(')+1;
                            int stopNum = MenuController.getBasketControl().getText().indexOf(')');
                            if(startNum != -1 && stopNum != -1) {
                                int val = Integer.parseInt(MenuController.getBasketControl().getText().substring(startNum, stopNum)) + 1;

                                MenuController.getBasketControl().setText("Корзина (" + val + ")");
                            }
                        }
                    }
                    else if(createButton.getText().equals("Добавлено")){
                        createButton.setStyle("");
                        createButton.setText("В корзину");
                        for(int k=0; k < MenuController.getBasket().size(); k++) {
                            if (MenuController.getBasket().get(k).getId() == id) {
                                MenuController.getBasket().remove(k);
                                break;
                            }
                        }
                        int startNum = MenuController.getBasketControl().getText().indexOf('(')+1;
                        int stopNum = MenuController.getBasketControl().getText().indexOf(')');
                        if(startNum != -1 && stopNum != -1) {
                            int val = Integer.parseInt(MenuController.getBasketControl().getText().substring(startNum, stopNum)) - 1;
                            if(val == 0)
                                MenuController.getBasketControl().setText("Корзина");
                            else
                                MenuController.getBasketControl().setText("Корзина (" + val + ")");
                        }
                    }
                });

                index++;
            }
        }
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

    private void openCatalog(){
        getData();
        if(!products.isEmpty()) {

            List<Product> temp = new ArrayList<>();
            for(Product i : products) {
                if (i.getCount() != 0) {
                    if(MenuController.numCatalog == 1 && i.getCategoryId() == 1)
                        temp.add(i);
                    else if(MenuController.numCatalog == 2 && i.getCategoryId() == 2)
                        temp.add(i);
                    else if(MenuController.numCatalog == 3 && i.getCategoryId() == 3)
                        temp.add(i);
                    else if(MenuController.numCatalog == 4 && i.getCategoryId() == 4)
                        temp.add(i);
                    else if(MenuController.numCatalog == 5 && i.getCategoryId() == 5)
                        temp.add(i);
                    else if(MenuController.numCatalog == 0)
                        temp.add(i);
                }
            }
            createTable(temp);
        }
    }

    @FXML
    void initialize() {
        choiceBox.getItems().addAll(choice);
        choiceBox.valueProperty().addListener((ob, oldV, newV) -> {
            openCatalog();
        });
        choiceBox.setValue("Популярные");

        searchButton.setOnAction(actionEvent -> {
            getData();
            if(!products.isEmpty()) {

                List<Product> temp = new ArrayList<>();
                for(Product i : products) {
                    if(!searchLine.getText().isEmpty() && i.getName().toLowerCase().contains(searchLine.getText().toLowerCase())) {
                        temp.add(i);
                    }
                    else if(searchLine.getText().isEmpty())
                        temp.add(i);
                }
                createTable(temp);
            }
        });
    }
}
