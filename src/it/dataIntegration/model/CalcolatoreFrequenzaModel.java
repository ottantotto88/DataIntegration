package it.dataIntegration.model;

import it.dataIntegration.controller.CalcolatoreFrequenzaController;

import java.sql.*;

public class CalcolatoreFrequenzaModel {
    private CalcolatoreFrequenzaController calcolatoreFrequenzaController;
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;

    public CalcolatoreFrequenzaModel(CalcolatoreFrequenzaController calcolatoreFrequenzaController) {
        this.calcolatoreFrequenzaController = calcolatoreFrequenzaController;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/properties_values?"
                    + "user=sqluser&password=sqluserpw&serverTimezone=UTC");
            statement = connection.createStatement();
            preparedStatement = connection
                    .prepareStatement("INSERT INTO properties_values.property_value VALUES (default , ?,?,?)");

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void insertProperty(String property, String value) throws SQLException {
        preparedStatement.setInt(1,1);
        preparedStatement.setString(2, property);
        preparedStatement.setString(3, value );
        preparedStatement.executeUpdate();
        System.out.println("sto inserendo");
    }

}
