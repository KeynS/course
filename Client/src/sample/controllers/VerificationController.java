package sample.controllers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import sample.Connect;
import sample.User;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerificationController {

    private boolean isEye = false;

    private double xOffset;
    private double yOffset;
    private AtomicBoolean isName = new AtomicBoolean(false);
    private AtomicBoolean isSurname = new AtomicBoolean(false);
    private AtomicBoolean isNumber = new AtomicBoolean(false);
    private AtomicBoolean isEmail = new AtomicBoolean(false);
    private AtomicBoolean isPassword = new AtomicBoolean(false);
    private AtomicBoolean isRepassword = new AtomicBoolean(false);

    @FXML
    private AnchorPane MenuPane;

    @FXML
    private Hyperlink SignUpButton;

    @FXML
    private Text captionText;

    @FXML
    private Text closeButton;

    @FXML
    private Text rollUpButton;

    @FXML
    private Pane registrationPane;

    @FXML
    private TextField surnameField;

    @FXML
    private Text textErr;

    @FXML
    private TextField nameField;

    @FXML
    private TextField numberField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repasswordField;

    @FXML
    private Text captionRegistration;

    @FXML
    private Button registrationButton;

    @FXML
    private Hyperlink SignInURL;

    @FXML
    private Pane authorizationPane;

    @FXML
    private Button SignInButton;

    @FXML
    private Hyperlink restorePasswordUrl;

    @FXML
    private TextField SignEmailField;

    @FXML
    private Text forgotText;

    @FXML
    private PasswordField SignPasswordField;

    @FXML
    private TextField SignPasswordNoShieldField;

    @FXML
    private ImageView eyeGlassImage;

    @FXML
    private Text textError;


    private boolean checkRegex(String text, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if(matcher.find())
            return true;

        return false;
    }

    private void setAnimation(int sec, Node t, double x, double y) {
        TranslateTransition  st = new TranslateTransition (Duration.seconds(sec), t);
        st.setToX(x);
        st.setToY(y);
        st.setInterpolator(Interpolator.EASE_IN);
        st.setOnFinished(event -> {
            t.setCacheHint(CacheHint.QUALITY);
        });
        t.setCacheHint(CacheHint.SPEED);
        st.play();
    }

    @FXML
    private void clickAuthorization(MouseEvent event) {
        setAnimation(1, captionText, 0, 0);
        setAnimation(1, authorizationPane, 0, 0);
        setAnimation(1, SignUpButton, 0, 0);
        setAnimation(1, registrationPane, 0, 0);
    }

    @FXML
    private void clickRegistration(MouseEvent events) {

        SignEmailField.setBorder(new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.NONE, new CornerRadii(25), BorderWidths.DEFAULT)));
        SignPasswordField.setBorder(new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.NONE, new CornerRadii(25), BorderWidths.DEFAULT)));
        SignPasswordNoShieldField.setBorder(new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.NONE, new CornerRadii(25), BorderWidths.DEFAULT)));
        textError.setVisible(false);

        setAnimation(1, captionText, 0, -200);
        setAnimation(1, authorizationPane, -470, 0);
        setAnimation(1, SignUpButton, 0, 100);
        setAnimation(1, registrationPane, -430, 0);
    }

    private void changeErrorField(String text){
        textError.setText(text);
        textError.setVisible(true);
        SignEmailField.setBorder(new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.SOLID, new CornerRadii(25), BorderWidths.DEFAULT)));
        SignPasswordField.setBorder(new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.SOLID, new CornerRadii(25), BorderWidths.DEFAULT)));
        SignPasswordNoShieldField.setBorder(new Border(new BorderStroke(Color.RED,
                BorderStrokeStyle.SOLID, new CornerRadii(25), BorderWidths.DEFAULT)));
    }

    private boolean intToBoolean(int input) {
        if((input==0)||(input==1)) {
            return input!=0;
        }else {
            throw new IllegalArgumentException("Входное значение может быть равно только 0 или 1 !");
        }
    }

    private void showNextScene() throws IOException {
        Parent root = null;
        root = FXMLLoader.load(getClass().getResource("../fxml/menu.fxml"));
        root.setStyle("-fx-background-color: rgb(255, 255, 255), rgb(255, 255, 255);");
        Stage stage = new Stage();
        Scene scene = new Scene(root, 1280, 760, Color.TRANSPARENT);
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();

        Stage stages = (Stage) closeButton.getScene().getWindow();
        stages.close();
    }

    @FXML
    private void buttonRegistration(ActionEvent event) throws IOException, ParseException {
        if(!registrationButton.isDisable()){
            Connect.send("registration");
            Connect.send(surnameField.getText());
            Connect.send(nameField.getText());
            Connect.send(numberField.getText());
            Connect.send(emailField.getText());
            Connect.send(passwordField.getText());
            String get = Connect.get();

            if(get.equals("code#1")){
                emailField.setStyle("-fx-border-color: #d90421;");
                isEmail.set(false);
                registrationButton.setDisable(true);
                textErr.setVisible(true);
                textErr.setText("Данный эмайл уже зарегистрирован");
            }
            else if(get.equals("code#2")){
                numberField.setStyle("-fx-border-color: #d90421 ;");
                isNumber.set(false);
                registrationButton.setDisable(true);
                textErr.setVisible(true);
                textErr.setText("Данный телефон уже зарегистрирован");
            }
            else if(get.equals("code#3")){
                java.util.Date utilDate = new java.util.Date();
                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                String date = sqlDate.toString();
                new User(emailField.getText(),nameField.getText(), surnameField.getText(), numberField.getText(), 0, date, 0, 0, "user", false);
                showNextScene();
            }
        }
    }

    @FXML
    private void buttonAuthorization(ActionEvent event) throws IOException, ParseException {
        Connect.send("authorization");
        Connect.send(SignEmailField.getText());
        if(!isEye)
            Connect.send(SignPasswordField.getText());
        else
            Connect.send(SignPasswordNoShieldField.getText());

        String get = Connect.get();
        if(get.equals("code#1"))
            changeErrorField("Ошибка базы данных");
        else if(get.equals("code#2"))
            changeErrorField("Неверный эмайл или пароль");
        else {
            String[] arr = get.split(";");
            new User(arr[0],arr[1], arr[2], arr[3], Integer.parseInt(arr[4]), arr[5], Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), arr[8], intToBoolean(Integer.parseInt(arr[9])));

            showNextScene();
        }
    }

    @FXML
    void clickEye(MouseEvent event) {
        if(isEye) {
            eyeGlassImage.setImage(new Image("sample/resource/img/closed_eye.png"));
            SignPasswordField.setVisible(true);
            SignPasswordNoShieldField.setVisible(false);
            SignPasswordField.setText(SignPasswordNoShieldField.getText());
            isEye = false;
        }
        else{
            eyeGlassImage.setImage(new Image("sample/resource/img/eye.png"));
            SignPasswordField.setVisible(false);
            SignPasswordNoShieldField.setVisible(true);
            SignPasswordNoShieldField.setText(SignPasswordField.getText());
            isEye = true;
        }
    }

    private void registrationProperty(TextField element, String regex, AtomicBoolean isVar, String tool){
        element.focusedProperty().addListener((ov,oldV,newV) -> {
            if(newV) {
                if(element.getText().isEmpty())
                    element.setStyle("-fx-border-color: #4465ee");
            }
            else{
                if(element.getText().isEmpty()) {
                    element.setStyle("fx-border-width: 0;");
                    element.setTooltip(new Tooltip(tool));
                }
            }
        });

        element.textProperty().addListener((ov,oldV,newV) ->{
            if(newV.isEmpty()){
                element.setStyle("-fx-border-color: #4465ee");
                isVar.set(false);
                element.setTooltip(null);
            }
            else{
                if(!regex.isEmpty() && checkRegex(newV, regex))
                {
                    element.setStyle("-fx-border-color: #06c90a;");
                    isVar.set(true);

                    if(!repasswordField.getText().isEmpty() && repasswordField.getText().equals(passwordField.getText()))
                        repasswordField.setStyle("-fx-border-color: #06c90a;");

                    element.setTooltip(null);
                }
                else if(regex.isEmpty() && newV.equals(passwordField.getText())) {
                    element.setStyle("-fx-border-color: #06c90a;");
                    isVar.set(true);
                    element.setTooltip(null);
                }
                else{
                    element.setStyle("-fx-border-color: #d90421;");

                    if(!repasswordField.getText().isEmpty() && !repasswordField.getText().equals(passwordField.getText()))
                        repasswordField.setStyle("-fx-border-color: #d90421;");

                    isVar.set(false);
                    element.setTooltip(new Tooltip(tool));
                }

                if(isSurname.get() && isName.get() && isEmail.get() && isNumber.get() && isPassword.get() && isRepassword.get())
                    registrationButton.setDisable(false);
                else
                    registrationButton.setDisable(true);
            }
        });
    }

    @FXML
    void initialize() {
        registrationProperty(surnameField, "^([А-Я]{1}[а-я]{2,13})$", isSurname, "Пример: Иванов");
        registrationProperty(nameField, "^([А-Я]{1}[а-яё]{2,13})$", isName, "Пример: Иван");
        registrationProperty(numberField, "^\\+375 ([0-9\\s]{12})$", isNumber, "Пример: +375 29 55 35 350");
        registrationProperty(emailField, "^([A-z0-9\\.\\-\\_]{3,16}\\@[a-z]{2,8}\\.[a-z]{2,5})$", isEmail, "Пример: example@gmail.com");
        registrationProperty(passwordField, "^([A-z0-9\\.\\_\\-]){6,16}$", isPassword, "Пароль должен быть от 6 до 16 символов\nИ можете содержать только:\nцифры, латинские символы верхнего и нижнего регистра");
        registrationProperty(repasswordField, "", isRepassword, "Пароль и повторный пароль должны совпадать");

        closeButton.setOnMouseClicked(mouseEvent -> {
            Stage stages = (Stage) closeButton.getScene().getWindow();
            stages.close();

        });

        rollUpButton.setOnMouseClicked(mouseEvent -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.setIconified(true);
        });

        SignPasswordField.textProperty().addListener( (ov,oldV,newV) -> {
            if (!newV.trim().isEmpty())
                eyeGlassImage.setVisible(true);
            else
                eyeGlassImage.setVisible(false);
        });

        SignPasswordNoShieldField.textProperty().addListener( (ov,oldV,newV) -> {
            if (!newV.trim().isEmpty())
                eyeGlassImage.setVisible(true);
            else
                eyeGlassImage.setVisible(false);
        });

        SignEmailField.focusedProperty().addListener((ov,oldV,newV) -> {
            if(newV) {
                SignPasswordField.setBorder(new Border(new BorderStroke(Color.RED,
                        BorderStrokeStyle.NONE, new CornerRadii(25), BorderWidths.DEFAULT)));
                SignPasswordNoShieldField.setBorder(new Border(new BorderStroke(Color.RED,
                        BorderStrokeStyle.NONE, new CornerRadii(25), BorderWidths.DEFAULT)));
                textError.setVisible(false);
            }
        });

        SignPasswordField.focusedProperty().addListener((ov,oldV,newV) -> {
            if(newV) {
                SignEmailField.setBorder(new Border(new BorderStroke(Color.RED,
                        BorderStrokeStyle.NONE, new CornerRadii(25), BorderWidths.DEFAULT)));
                SignPasswordNoShieldField.setBorder(new Border(new BorderStroke(Color.RED,
                        BorderStrokeStyle.NONE, new CornerRadii(25), BorderWidths.DEFAULT)));
                textError.setVisible(false);
            }
        });

        SignPasswordNoShieldField.focusedProperty().addListener((ov,oldV,newV) -> {
            if(newV) {
                SignEmailField.setBorder(new Border(new BorderStroke(Color.RED,
                        BorderStrokeStyle.NONE, new CornerRadii(25), BorderWidths.DEFAULT)));
                SignPasswordField.setBorder(new Border(new BorderStroke(Color.RED,
                        BorderStrokeStyle.NONE, new CornerRadii(25), BorderWidths.DEFAULT)));
                textError.setVisible(false);
            }
        });

        numberField.focusedProperty().addListener((ov, oldV, newV)->{
            if(newV && numberField.getText().length() == 0){
                numberField.setText("+375 ");
            }
            else if(!newV && numberField.getText().equals("+375 ")) {
                numberField.setText("");
                numberField.setStyle("fx-border-width: 0;");
            }
        });

        numberField.textProperty().addListener((ov, oldV, newV)->{
            if(numberField.focusedProperty().getValue() && (newV.length() < 5 || !newV.substring(0,5).equals("+375 ")))
                numberField.setText(oldV);
            else if(newV.length() > 5 && newV.substring(newV.length()-1).equals(" ")){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        numberField.setText(newV.substring(0, newV.length() - 1));
                    }
                });
            }
            else if(newV.length() > 5 && (!newV.substring(newV.length() - 1).matches("[0-9]")))
                numberField.setText(oldV);
            else if((newV.length() == 8 || newV.length() == 11 || newV.length() == 14))
                numberField.setText(oldV + ' ' + newV.substring(newV.length()-1));
            else if(numberField.getText().length() > 17)
                numberField.setText(oldV);
        });

        numberField.caretPositionProperty().addListener((ov,oldV,newV)->{
              if(numberField.getCaretPosition() != numberField.getText().length()){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        numberField.positionCaret( numberField.getText().length());
                    }
                });
            }
        });
    }
}
