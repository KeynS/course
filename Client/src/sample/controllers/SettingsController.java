package sample.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import sample.Connect;
import sample.Product;
import sample.User;
import sample.logProduct;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsController {

    private ObservableList<logProduct> oList = FXCollections.observableArrayList();

    @FXML
    private Text textPurchased;

    @FXML
    private Text textSpent;

    @FXML
    private Text textReceived;

    @FXML
    private Text textDate;

    @FXML
    private TableView<logProduct> tableInformation;

    @FXML
    private TextField codeField;

    @FXML
    private RadioButton informationSelect1;

    @FXML
    private ToggleGroup informationGroup;

    @FXML
    private RadioButton informationSelect2;

    @FXML
    private RadioButton runSelect1;

    @FXML
    private ToggleGroup runInformation;

    @FXML
    private RadioButton runSelect2;

    @FXML
    private Text textInformation;

    @FXML
    private Button btnOk;

    @FXML
    private Button btnRecode;

    @FXML
    private Text textCodeErr;
    @FXML
    private TableColumn<logProduct, Integer> columnID;

    @FXML
    private TableColumn<logProduct, String> columnName;

    @FXML
    private TableColumn<logProduct, Integer> columnValue;

    @FXML
    private TableColumn<logProduct, Double> columnCash;

    @FXML
    private TableColumn<logProduct, Integer> columnBonus;

    @FXML
    private TableColumn<logProduct, String> columnDate;

    @FXML
    void radioSelect(ActionEvent event) throws IOException {
        Properties property = new Properties();
        FileInputStream fis = new FileInputStream("src/sample/configs/settings.properties");
        property.load(fis);
        FileOutputStream fout = new FileOutputStream("src/sample/configs/settings.properties");

        if(runSelect1.isSelected()) {
            property.setProperty("runSetting", "1");
        }
        else if(runSelect2.isSelected()) {
            property.setProperty("runSetting", "0");
        }

        if(informationSelect1.isSelected()) {
            property.setProperty("informationSetting", "1");
        }
        else if(informationSelect2.isSelected()) {
            property.setProperty("informationSetting", "0");
        }


        property.save(fout, null);
        fout.close();
        fis.close();
    }


    @FXML
    void btnClickOk(ActionEvent event) throws IOException {
        Connect.send("codeCheck");
        Connect.send(User.email);
        Connect.send(codeField.getText());
        String get = Connect.get();

        if(get.equals("code#1")) {
            textInformation.setVisible(false);
            codeField.setVisible(false);
            btnOk.setVisible(false);
            btnRecode.setVisible(false);
            textCodeErr.setVisible(false);
            User.activated = true;
            MenuController.getVerficationState().setImage(new Image("sample/resource/img/checked.png"));
            Tooltip tooltip = new Tooltip("Ваш аккаунт подтверждён.");
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(MenuController.getVerficationState(), tooltip);
        }
        else if(get.equals("code#2")){
            textCodeErr.setText("Код указан неверно");
            textCodeErr.setVisible(true);
        }
        else {
            textCodeErr.setText("Возникла ошибка при отправке");
            textCodeErr.setVisible(true);
        }
    }

    @FXML
    void btnClickRecode(ActionEvent event) throws IOException {
        Connect.send("recode");
        Connect.send(User.email);
        String get = Connect.get();

        if(get.equals("code#1")) {
            btnRecode.setDisable(true);
            textCodeErr.setVisible(false);
            Tooltip tooltip = new Tooltip("Отправлять повторный код можно 1 раз в минуту");
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(btnRecode, tooltip);
            Thread newThread = new Thread(new Runnable() {
                public void run() {
                    for(int i=0; i < 60; i++) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    btnRecode.setDisable(false);
                    btnRecode.setTooltip(null);
                }
            });
            newThread.start();
        }
        else {
            textCodeErr.setText("Возникла ошибка при отправке");
            textCodeErr.setVisible(true);
        }
    }

    @FXML
    void initialize() {
        Connect.send("getuserlog");
        Connect.send(User.email);
        String[] message = Connect.get().split("\n");
        if(!message[0].isEmpty()) {
            for (int i = 0; i < message.length; i++) {
                String[] text = message[i].split(";");
                oList.add(new logProduct(Integer.parseInt(text[0]), text[1], Integer.parseInt(text[2]), Double.parseDouble(text[3]), text[4]));
            }
        }

        columnID.setCellValueFactory(new PropertyValueFactory<logProduct, Integer>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<logProduct, String>("name"));
        columnName.setCellFactory(new Callback<TableColumn<logProduct, String>, TableCell<logProduct, String>>() {
            @Override
            public TableCell<logProduct, String> call(TableColumn<logProduct, String> logProductStringTableColumn) {
                return new TableCell<logProduct, String>() {
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
        columnValue.setCellValueFactory(new PropertyValueFactory<logProduct, Integer>("value"));
        columnCash.setCellValueFactory(new PropertyValueFactory<logProduct, Double>("cash"));
        columnBonus.setCellValueFactory(new PropertyValueFactory<logProduct, Integer>("bonus"));
        columnDate.setCellValueFactory(new PropertyValueFactory<logProduct, String>("date"));

        tableInformation.setItems(oList);

        if(MenuController.runSel == 1)
            runSelect1.setSelected(true);
        else
            runSelect2.setSelected(true);

        if(MenuController.infSel == 1)
            informationSelect1.setSelected(true);
        else
            informationSelect2.setSelected(true);


        if(!User.activated){
            textInformation.setVisible(true);
            codeField.setVisible(true);
            btnOk.setVisible(true);
            btnRecode.setVisible(true);
        }

        textPurchased.setText(String.valueOf(User.purchased));
        textSpent.setText(String.valueOf(User.spent));
        textReceived.setText(String.valueOf(User.bonus));
        textDate.setText(User.date);
        tableInformation.setPlaceholder(new Label("Информации о покупках ещё нет"));
    }

}
