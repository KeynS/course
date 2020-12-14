package sample.controllers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import sample.Connect;

import javax.swing.*;

public class StatisticController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private PieChart pieCategory;

    @FXML
    private PieChart pieId;

    final private ObservableList<String> choice = FXCollections.observableArrayList("Компьютеры", "Ноутбуки", "Периферия", "Аксессуары", "Разное");

    @FXML
    private Button saveButton;

    @FXML
    void sendInformation(ActionEvent event) throws IOException {
        FileOutputStream fos=new FileOutputStream("statistics.txt");
        if(fos != null) {
            fos.write("Статистика продаж товаров по категоряим \r\n".getBytes());

            for (int i = 0; i < pieCategoryList.size(); i++) {
                fos.write((pieCategoryList.get(i).getName() + ": " + (int)pieCategoryList.get(i).getPieValue() + " товаров продано; \r\n").getBytes());
            }

            fos.write("\r\n".getBytes());
            fos.write("Статистика продаж товаров по id \r\n".getBytes());
            int value = 0;
            for (int i = 0; i < pieIdList.size(); i++) {
                value += pieIdList.get(i).getPieValue();
                fos.write(("ID товара: " + pieIdList.get(i).getName() + ": " + (int)pieIdList.get(i).getPieValue() + " продано; \r\n").getBytes());
            }

            fos.write(("\r\nВсего товаров продано: " + value).getBytes());
            fos.close();
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Файл с статистикой продаж сохранён");
        }
    }

    private ObservableList<PieChart.Data> pieIdList = FXCollections.observableArrayList();

    private ObservableList<PieChart.Data> pieCategoryList = FXCollections.observableArrayList();

    @FXML
    void initialize() {

        Connect.send("spros");
        String[] sql = Connect.get().split("\n");
        for(int i = 0; i < sql.length; i++){
            String[] prod = sql[i].split(";");
            pieIdList.add(new PieChart.Data(prod[0],Integer.parseInt(prod[1])));
        }

        pieId.setData(pieIdList);

        Connect.send("statistic");
        for(int i = 0; i < 5; i++){
            pieCategoryList.add(new PieChart.Data(choice.get(i),Integer.parseInt(Connect.get())));
        }
        pieCategory.setData(pieCategoryList);

    }
}