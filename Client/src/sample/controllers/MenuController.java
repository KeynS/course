package sample.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import sample.Connect;
import sample.Product;
import sample.User;

import javax.tools.Tool;

public class MenuController {

    private double xOffset;
    private double yOffset;

    public static int infSel,runSel;

    public static int numCatalog;

    boolean isHideButton = false;

    private static ImageView verficationControl = null;

    private static Button basketControl = null;

    private static List<Product> basketInfo = new ArrayList<>();

    @FXML
    private ImageView imageAvatar;

    @FXML
    private Line lineDown;

    @FXML
    private ImageView checkAccount;

    @FXML
    private ImageView imageOut;

    @FXML
    private Text textName;

    @FXML
    private Text hideButton;

    @FXML
    private Text closeButton;

    @FXML
    private Text rollUpButton;

    @FXML
    private Pane loadPane;

    @FXML
    private Button btnCatalog;

    @FXML
    private Pane menuPane;

    @FXML
    private Button btnSettings;

    @FXML
    private Button allButton;

    @FXML
    private Button computerButton;

    @FXML
    private Button notebookButton;

    @FXML
    private Button peripheryButton;

    @FXML
    private Button accessoriesButton;

    @FXML
    private ImageView addItemButton;

    @FXML
    private ImageView deleteItemButton;

    @FXML
    private Button otherButton;

    @FXML
    private ImageView editItemButton;

    private ObservableList<Button> catalogButtons;

    @FXML
    private Button btnBasket;

    @FXML
    private Button btnStatistic;

    public static ImageView getVerficationState() { return verficationControl; }

    public static Button getBasketControl() { return basketControl; }

    public static List<Product> getBasket() { return basketInfo; }

    private void setAnimation(double milsec, Node t, double x, double y, boolean visible) {
        TranslateTransition st = new TranslateTransition (Duration.millis(milsec), t);
        st.setToX(x);
        st.setToY(y);
        st.setInterpolator(Interpolator.EASE_IN);
        st.setOnFinished(event -> {
            t.setCacheHint(CacheHint.QUALITY);
            if(visible){
                imageOut.setVisible(true);
                imageAvatar.setVisible(true);
                if(User.role.equals("admin")) {
                    deleteItemButton.setVisible(true);
                    editItemButton.setVisible(true);
                    addItemButton.setVisible(true);
                }
                else
                    checkAccount.setVisible(true);
            }
        });
        t.setCacheHint(CacheHint.SPEED);
        st.play();
    }

    private void readInformation() throws IOException {
        Properties property = new Properties();
        FileInputStream fis = new FileInputStream("src/sample/configs/settings.properties");
        property.load(fis);
        if(!property.isEmpty()) {
            runSel = Integer.parseInt(property.getProperty("runSetting"));
            infSel = Integer.parseInt(property.getProperty("informationSetting"));
        }

        fis.close();
    }

    private void setLoadPane(String url){
        try {
            loadPane.getChildren().clear();
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource(url));
            loadPane.getChildren().add(newLoadedPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setButtonActvie(Button btn, boolean active){
        btn.getStyleClass().remove(3);
        if(active)
            btn.getStyleClass().add("active");
        else
            btn.getStyleClass().add("nonactive");
    }

    private void openBtnScene(boolean settingBtn, boolean catalogBtn, boolean basketBtn, boolean statisticBtn){
        for(Button a : catalogButtons)
            setButtonActvie(a,false);

        setButtonActvie(btnCatalog, catalogBtn);
        setButtonActvie(btnSettings, settingBtn);
        setButtonActvie(btnBasket, basketBtn);
        setButtonActvie(btnStatistic, statisticBtn);
    }

    private void openBtnScene(boolean settingBtn, boolean catalogBtn, boolean basketBtn, boolean statisticBtn, int i){
        for(Button a : catalogButtons)
            setButtonActvie(a,false);

        setButtonActvie(btnCatalog, catalogBtn);
        setButtonActvie(btnSettings, settingBtn);
        setButtonActvie(catalogButtons.get(i), true);
        setButtonActvie(btnBasket, basketBtn);
        setButtonActvie(btnStatistic, statisticBtn);
    }

    private void showScene() throws IOException {

        Stage primaryStage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/sample.fxml"));
        primaryStage.setTitle("Основное меню");
        root.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color: rgb(255, 255, 255), rgb(255, 255, 255);" +
                "-fx-background-insets: 0, 0 1 1 0;");
        Scene sc = new Scene(root,960, 627, Color.TRANSPARENT);
        primaryStage.setScene(sc);

        sc.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = primaryStage.getX() - event.getScreenX();
                yOffset = primaryStage.getY() - event.getScreenY();
            }
        });
        sc.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() + xOffset);
                primaryStage.setY(event.getScreenY() + yOffset);
            }
        });

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setResizable(false);
        primaryStage.show();

        Stage stages = (Stage) closeButton.getScene().getWindow();
        stages.close();
    }

    @FXML
    void initialize() throws IOException {
        verficationControl = checkAccount;
        basketControl = btnBasket;
        catalogButtons = FXCollections.observableArrayList(allButton,computerButton,notebookButton,peripheryButton,accessoriesButton,otherButton);
        readInformation();

        if(User.role.equals("admin")){
            addItemButton.setVisible(true);
            deleteItemButton.setVisible(true);
            editItemButton.setVisible(true);
            checkAccount.setVisible(false);
            Tooltip tooltip = new Tooltip("Добавить товар");
            tooltip.setShowDelay(Duration.millis(660));
            Tooltip.install(addItemButton, tooltip);

            tooltip = new Tooltip("Удалить товар");
            tooltip.setShowDelay(Duration.millis(660));
            Tooltip.install(deleteItemButton, tooltip);

            tooltip = new Tooltip("Редактировать товар");
            tooltip.setShowDelay(Duration.millis(660));
            Tooltip.install(editItemButton, tooltip);

            btnStatistic.setVisible(true);
        }

        if(runSel == 1) {
            numCatalog = 0;
            setLoadPane("../fxml/main.fxml");
            setButtonActvie(btnCatalog, true);
            setButtonActvie(allButton, true);
        }
        else {
            setLoadPane("../fxml/settings.fxml");
            setButtonActvie(btnSettings, true);
        }

        hideButton.setOnMouseClicked(mouseEvent -> {
            if(!isHideButton){
                hideButton.setText("∨");
                isHideButton = true;
                textName.setText("Профиль");
                imageOut.setVisible(false);
                imageAvatar.setVisible(false);
                checkAccount.setVisible(false);
                deleteItemButton.setVisible(false);
                editItemButton.setVisible(false);
                addItemButton.setVisible(false);

                setAnimation(400,textName,0,-179, false);
                setAnimation(400,lineDown,0,-179, false);
                setAnimation(400,btnCatalog,0,-179, false);
                setAnimation(400,menuPane,0,-179, false);
                setAnimation(400,btnSettings,0,-179, false);
                setAnimation(400,addItemButton,0,-179, false);
                setAnimation(400,deleteItemButton,0,-179, false);
                setAnimation(400,editItemButton,0,-179, false);

            }
            else{
                hideButton.setText("∧");
                isHideButton = false;
                textName.setText(User.name + " " + User.surname);
                setAnimation(400,btnCatalog,0,0, false);
                setAnimation(400,menuPane,0,0, false);
                setAnimation(400,textName,0,0, false);
                setAnimation(400,lineDown,0,0, true);
                setAnimation(400,btnSettings,0,0, false);
                setAnimation(400,addItemButton,0,0, false);
                setAnimation(400,deleteItemButton,0,0, false);
                setAnimation(400,editItemButton,0,0, false);
            }
        });
        btnSettings.setOnAction(actionEvent -> {
            setLoadPane("../fxml/settings.fxml");
            openBtnScene(true,false,false, false);
        });

        btnCatalog.setOnAction(actionEvent -> {
            numCatalog = 0;
            setLoadPane("../fxml/main.fxml");
            openBtnScene(false,true,false, false, numCatalog);
            menuPane.setVisible(false);
        });

        allButton.setOnAction(actionEvent -> {
            numCatalog = 0;
            setLoadPane("../fxml/main.fxml");
            openBtnScene(false,true,false, false, numCatalog);
        });

        computerButton.setOnAction(actionEvent -> {
            numCatalog = 1;
            setLoadPane("../fxml/main.fxml");
            openBtnScene(false,true,false, false, numCatalog);
        });

        notebookButton.setOnAction(actionEvent -> {
            numCatalog = 2;
            setLoadPane("../fxml/main.fxml");
            openBtnScene(false,true,false, false, numCatalog);
        });

        peripheryButton.setOnAction(actionEvent -> {
            numCatalog = 3;
            setLoadPane("../fxml/main.fxml");
            openBtnScene(false,true,false, false, numCatalog);
        });

        accessoriesButton.setOnAction(actionEvent -> {
            numCatalog = 4;
            setLoadPane("../fxml/main.fxml");
            openBtnScene(false,true,false,false, numCatalog);
        });

        otherButton.setOnAction(actionEvent -> {
            numCatalog = 5;
            setLoadPane("../fxml/main.fxml");
            openBtnScene(false,true,false,false,numCatalog);
        });

        closeButton.setOnMouseClicked(mouseEvent -> {
            Stage stages = (Stage) closeButton.getScene().getWindow();
            stages.close();

        });

        rollUpButton.setOnMouseClicked(mouseEvent -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.setIconified(true);
        });

        textName.setText(User.name + " " + User.surname);
        Tooltip tooltip = new Tooltip(User.activated ? "Ваш аккаунт подтверждён." : "Ваш аккаунт не подтверждён");
        if(!User.activated)
            checkAccount.setImage(new Image("sample/resource/img/del.png"));

        tooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(checkAccount, tooltip);

        btnCatalog.setOnMouseEntered(mouseEvent -> {
            menuPane.setVisible(true);
        });

        btnCatalog.setOnMouseExited(mouseEvent -> {
            if((isHideButton && mouseEvent.getSceneY() < 164.0f || mouseEvent.getSceneY() > 464.0f) || (!isHideButton && mouseEvent.getSceneY() < 343.0f || mouseEvent.getSceneY() > 643.0f))
                menuPane.setVisible(false);
        });

        menuPane.setOnMouseExited(mouseEvent -> {
            menuPane.setVisible(false);
        });

        addItemButton.setOnMouseClicked(mouseEvent -> {
            setLoadPane("../fxml/add.fxml");
            openBtnScene(false,false,false, false);
        });

        deleteItemButton.setOnMouseClicked(mouseEvent -> {
            setLoadPane("../fxml/delete.fxml");
            openBtnScene(false,false,false, false);
        });

        editItemButton.setOnMouseClicked(mouseEvent -> {
            setLoadPane("../fxml/edit.fxml");
            openBtnScene(false,false,false, false);
        });

        btnBasket.setOnAction(actionEvent -> {
            setLoadPane("../fxml/basket.fxml");
            openBtnScene(false,false,true, false);
        });

        btnStatistic.setOnAction(actionEvent -> {
            setLoadPane("../fxml/statistic.fxml");
            openBtnScene(false,false,false, true);
        });

        if(infSel == 1){
            hideButton.setText("∨");
            isHideButton = true;
            textName.setText("Профиль");
            imageOut.setVisible(false);
            imageAvatar.setVisible(false);
            checkAccount.setVisible(false);
            deleteItemButton.setVisible(false);
            editItemButton.setVisible(false);
            addItemButton.setVisible(false);

            setAnimation(1,textName,0,-179, false);
            setAnimation(1,lineDown,0,-179, false);
            setAnimation(1,btnCatalog,0,-179, false);
            setAnimation(1,menuPane,0,-179, false);
            setAnimation(1,btnSettings,0,-179, false);
            setAnimation(1,addItemButton,0,-179, false);
            setAnimation(1,deleteItemButton,0,-179, false);
            setAnimation(1,editItemButton,0,-179, false);
        }

        imageOut.setOnMouseClicked(mouseEvent -> {
            try {
                showScene();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}