package com.CoDevelop.tasktimer;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class getListadoOperacion extends MainActivity {


    Connection connect;
    String ConnectionResult = "";
    Boolean isSucess = false;


    public List<String> getListado(String e) {



        List<String> data = null;
        data = new ArrayList<>();
        try {

            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.conectionclass();



            if (connect != null) {

                String query = "SELECT [detoperacion] FROM M_Operaciones WHERE idlinea ='"+e+"'";
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {



                    data.add(resultSet.getString("detoperacion"));

                }
                ConnectionResult = "Success";
                isSucess = true;
                connect.close();

            } else {
                ConnectionResult = "Failed";

            }


        } catch (SQLException throwables) {

            throwables.printStackTrace();
        }

        return data;
    }



}