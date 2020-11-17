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
    static Boolean gameActive = false;
    List<City> listCities = new ArrayList<>(60);

    static double gameResult;
    double pos_x=0.0,pos_y=0.0;
    static int counter=-1;
    long startTime,sTime,fTime;
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
                double duration;
                @Override
                public void handle(MouseEvent event) {
                    fTime = System.currentTimeMillis();
                    if(counter==0){
                    duration = (startTime - System.currentTimeMillis())/-1000.0;

                    }
                    else{
                    duration = (sTime - fTime)/-1000.0;
                    }
                    System.out.println(duration);

                    if(counter<19){
                        cityLabel.setText((counter+2) + ": " + listCities.get(1).name);
                        pos_x = listCities.get(0).pos_x;
                        pos_y = listCities.get(0).pos_y;
                        listCities.remove(0);
                        sTime = System.currentTimeMillis();
                    }
                    else{
                        gameActive = false;
                    }
                    double x = event.getX();
                    double y = event.getY();
                    double res = countResult(x,y,pos_x,pos_y,duration);
                    gameResult+=res;
                    resultLabel.setText("Wynik: " + String.format("%.2f",gameResult));
                    //label.setText(x + " " + y);
                }
            });
            counter++;
        }
        if(gameActive==false){
            Stage stage = new Stage();
            try {
                Parent root = FXMLLoader.load(getClass().getResource("startView.fxml"));
                stage.setScene(new Scene(root,300,290));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @FXML
    public void handleStart(){
        gameActive = true;
        gameResult=0.0;

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
        startTime = System.currentTimeMillis();
        System.out.println(startTime);
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
            cityLabel.setText((counter+2) + ": " + listCities.get(0).name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double countResult(double x, double y, double pos_x, double pos_y,double duration){
        double d = Math.sqrt((x-pos_x)*(x-pos_x)+(y-pos_y)*(y-pos_y));
        if(d>300 || duration > 5)
            return 0;
        else
        return 6000 - 10*d*(duration+1);
    }

}
