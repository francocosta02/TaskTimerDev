package com.CoDevelop.tasktimer;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class getListadoOperacion extends MainActivity {


    Connection connect;
    String ConnectionResult = "";
    Boolean isSucess = false;


    public List<String> getListado(String e) {



        List<String> data = null;
        data = new ArrayList<String>();
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



    public void guardarHora() {



        List<Map<String, String>> data = null;
        data = new ArrayList<Map<String, String>>();
        try {

            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.conectionclass();



            if (connect != null) {

                String query = "INSERT INTO APP_PRD_TIEMPOSTOTALES VALUES ('12345678910', '005:17:23', '006:17:54')";
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
                /*while (resultSet.next()) {

                    Map<String, String> dtname = new HashMap<String, String>();
                    dtname.put("codCli", resultSet.getString("CODIGO"));
                    dtname.put("nomCli", resultSet.getString("NOMBRE"));

                    data.add(dtname);


                }*/
                ConnectionResult = "Success";
                isSucess = true;
                connect.close();

            } else {
                ConnectionResult = "Failed";

            }


        } catch (SQLException throwables) {

            throwables.printStackTrace();
        }


    }



}