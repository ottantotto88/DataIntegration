package it.dataIntegration.model;

import it.dataIntegration.controller.CalcolatoreFrequenzaController;

import java.sql.*;

public class CalcolatoreFrequenzaModel {
    private CalcolatoreFrequenzaController calcolatoreFrequenzaController;
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatementArg;
    private PreparedStatement preparedStatementProp;
    public CalcolatoreFrequenzaModel(CalcolatoreFrequenzaController calcolatoreFrequenzaController) {
        this.calcolatoreFrequenzaController = calcolatoreFrequenzaController;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/bda2018?"
                    + "user=sqluser&password=sqluserpw&serverTimezone=UTC");
            statement = connection.createStatement();


            preparedStatementProp = connection
                    .prepareStatement("INSERT INTO properties VALUES (default , ?,?,?)");

            preparedStatementArg = connection
                    .prepareStatement("INSERT INTO argomenti  VALUES(default, ? ,NOW());", Statement.RETURN_GENERATED_KEYS);


        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void insertProperty(int argomento, String property, String value) throws SQLException {
        preparedStatementProp.setInt(1,argomento);
        preparedStatementProp.setString(2, property);
        preparedStatementProp.setString(3, value );
        preparedStatementProp.executeUpdate();

    }

    public int insertArgomento(String argomento) throws SQLException {
        int idArg = 0;
        preparedStatementArg.setString(1,argomento);
        preparedStatementArg.executeUpdate();
        ResultSet rs = preparedStatementArg.getGeneratedKeys();
        if(rs.next())
            idArg = rs.getInt(1);
        return idArg;

    }

}
