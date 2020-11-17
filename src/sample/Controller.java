package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    Canvas canvas;
    @FXML
    Label resultLabel;
    @FXML
    Label cityLabel;

    Connection connection;
    Boolean gameActive = false;
    List<City> listCities = new ArrayList<>(60);

    static double gameResult;
    double pos_x=0.0,pos_y=0.0;
    int counter=0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gameResult=0.0;
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Image image = new Image("file:src/sample/woj.png");
        gc.drawImage(image,0,0);
    }
    @FXML
    public void handleClick(){
        if(gameActive) {
            canvas.setOnMousePressed(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {}});
            canvas.setOnMouseReleased(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {

                    if(counter<20){
                        cityLabel.setText((counter+1) + ": " + listCities.get(1).name);
                        pos_x = listCities.get(0).pos_x;
                        pos_y = listCities.get(0).pos_y;
                        listCities.remove(0);
                    }
                    else{
                        gameActive = false;
                    }
                    double x = event.getX();
                    double y = event.getY();
                    double res = countResult(x,y,pos_x,pos_y);
                    gameResult+=res;
                    resultLabel.setText("Wynik: " + String.format("%.2f",gameResult));
                    //label.setText(x + " " + y);
                }
            });
            counter++;
        }
        else{
            Stage stage = new Stage();
            try {
                Parent root = FXMLLoader.load(getClass().getResource("startView.fxml"));
                stage.setScene(new Scene(root,300,300));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    public void handleStart(){
        gameActive = true;
        String urldb = "jdbc:mysql://localhost:3306/geoguesser?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        String user = "root";
        String passwd = "1234";
        try {
            connection = DriverManager.getConnection(urldb,user,passwd);
            System.out.println("Connection successful");
            game();
        } catch (SQLException e) {
            System.out.println("Error with connection");
            e.printStackTrace();
        }
    }

    public void game(){

        try {
            Statement statement = connection.createStatement();
            String s = "SELECT * FROM cities;";//'" + currentID + "'";
            ResultSet cities = statement.executeQuery(s);
            while(cities.next()){
                listCities.add(new City(cities.getInt("id"),cities.getString("name"),cities.getDouble("pos_x"),cities.getDouble("pos_y")));
            }
            Collections.shuffle(listCities);
            cityLabel.setText((counter+1) + ": " + listCities.get(0).name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double countResult(double x, double y, double pos_x, double pos_y){
        double d = Math.sqrt((x-pos_x)*(x-pos_x)+(y-pos_y)*(y-pos_y));
        return d;
    }

}
