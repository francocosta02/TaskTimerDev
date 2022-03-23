package com.CoDevelop.tasktimer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    int iniciado=0;
    int userID = 0;
    String horainiciodb;
    String ordenProduccion = "";
    String ConnectionResult = "";
    String motivodePausaSeleccionado = "";
    String [] actividad = new String[]{"Corte", "Trenzado", "Prensado", "Inspeccion"};
    String [] ListaMotivoPausa = new String[]{"Descanzo", "Seteo de Maquina", "Reparacion"};

    Button btnScanOP;
    Button btnValidar;
    Button btnStartStop;

    TextView tvTimer;

    boolean timeStarted = false;
    boolean isSucess = false;

    EditText etCodOperario;
    public static EditText etOrdenProduccion;

    Spinner spinnerActividad;

    Timer timer;
    TimerTask timerTask;
    double time = 0.0;
    Connection connect;

    GregorianCalendar calendar = new GregorianCalendar();
    SimpleDateFormat formated = new SimpleDateFormat("HH:mm:ss");
    Date horaInicioProceso = new Date();
    Date horaInicioDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //--Set up SPINNNER
        spinnerActividad = (Spinner) findViewById(R.id.spinnerActividad);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, actividad);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActividad.setAdapter(adapter);
        spinnerActividad.setEnabled(false);


        etCodOperario = (EditText) findViewById(R.id.etCodOperario);
        etOrdenProduccion = (EditText) findViewById(R.id.etOrdenProduccion);


        btnScanOP = (Button) findViewById(R.id.btnScanOP);
        btnValidar = (Button) findViewById(R.id.btnValidar);
        btnStartStop = (Button) findViewById(R.id.btnStartStop);

        btnScanOP.setOnClickListener(this);
        btnStartStop.setOnClickListener(this);
        btnValidar.setOnClickListener(this);
        spinnerActividad.setOnItemSelectedListener(this);


        //--Timer

        btnStartStop = (Button) findViewById(R.id.btnStartStop);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        timer = new Timer();

    }



    public void resetTapped(View v){

        AlertDialog.Builder resetAlert = new AlertDialog.Builder(this);
        resetAlert.setTitle("Resetear tiempo");
        resetAlert.setMessage("Esta seguro que desea reiniciar el contador?");
        resetAlert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (timerTask != null){

                    timerTask.cancel();
                    btnStartStop.setText("COMENZAR");
                    time = 0.0;
                    timeStarted=false;
                    tvTimer.setText(formatTime(0,0,0));



                }
            }
        });
        resetAlert.setNeutralButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Do nothing
              }


            }
        );

        resetAlert.show();

    }

    public void StartStopTapped(MainActivity v){

        if(timeStarted==false){

            timeStarted=true;
            btnStartStop.setText("STOP");
            
            startTimer();

        }
        else{

            timeStarted=false;
            btnStartStop.setText("COMENZAR");
            timerTask.cancel();

            Toast.makeText(this, "El tiempo es: " + getTimerText() , Toast.LENGTH_SHORT).show();

        }

    }

    private void startTimer() {

        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time ++;
                        tvTimer.setText(getTimerText());
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }


    private String getTimerText() {

        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60 ;
        int minutes = ((rounded % 86400) % 3600) / 60 ;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);

    }

    private String formatTime(int seconds, int minutes, int hours) {

        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }

    //----------------------------CODIGO TIMER ------------------------------------

    public void onClick(View view) {



        if (view == btnScanOP) {

            startActivities(new Intent[]{new Intent(getApplicationContext(), scanner.class)});

        } else if (view == btnStartStop) {


                switch (iniciado) {
                    case 0: // BOTON COMENZAR

//-----------------------CHEQUEAR FORMATO DE OP INGRESADA------------------------------
                        if (etOrdenProduccion.getText().length() != 11){

                            AlertDialog.Builder alertaOP = new AlertDialog.Builder(this);
                            alertaOP.setTitle("Atención!");
                            alertaOP.setMessage("Debe ingresar un n° de Orden de Producción válido para comenzar.");
                            alertaOP.show();

                }
                else {
//-------------------------------------------------------------------------------------

                            horaInicioProceso = calendar.getTime();
                            String horaInicio = formated.format(horaInicioProceso);


                            ordenProduccion = etOrdenProduccion.getText().toString();

                            //guardarHoraInicioProceso(ordenProduccion, horaInicio); //graba inicio de accion.
                            //Toast.makeText(this, "Hora de Inicio: " + horaInicio, Toast.LENGTH_SHORT).show();
                            StartStopTapped(this);
                            iniciado = 1; // transformar a boton Pausar
                            btnStartStop.setText("PAUSAR");
                            etOrdenProduccion.setEnabled(false);
                            etCodOperario.setEnabled(false);
                            spinnerActividad.setEnabled(false);
            }

                    break;
                    case 1: //BOTON PAUSAR


 //-------------------------CUADRO DE OPCIONES DE PAUSA-------------------------------//

                        AlertDialog.Builder motivoPausa = new AlertDialog.Builder(this);
                        motivoPausa.setTitle("Seleccione motivo de la Pausa:");
                        motivoPausa.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}});

                        motivoPausa.setItems(ListaMotivoPausa, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String motivodePausaSeleccionado = String.valueOf(i);

                                //DEBERIA GRABAR HORA DE INICIO DE PAUSA ACA

                            }

                        });
                        motivoPausa.show();
//-----------------------------------------------------------------------------------//


                    btnStartStop.setText("REANUDAR");
                        StartStopTapped(this);
                    iniciado = 2; //transformar a boton "REANUDAR"

                    break;
                    case 2: //Boton Reanudar

                            //DEBERIA GRABAR HORA FINAL DE PAUSA

                        btnStartStop.setText("PAUSAR");
                        iniciado = 1; //TRANSFORMA A BOTON PAUSA
                        StartStopTapped(this);
                        break;

            }



        } else if (view == btnValidar) {


            userID = Integer.parseInt(etCodOperario.getText().toString());

            if (userID < 100) {

                spinnerActividad.setEnabled(false);

            } else {

                spinnerActividad.setEnabled(true);

            }

        }
    }

    public void guardarHoraInicioProceso(String ordenProduccion,String horaInicioProceso) {

        try {

            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.conectionclass();



            if (connect != null) {

                String query = "INSERT INTO APP_PRD_TIEMPOSTOTALES VALUES ('" +ordenProduccion+"', '"+ horaInicioProceso+ "', '000:00:00')";
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
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

    public void guardarHoraFinProceso(String ordenProduccion,String horaFinProceso) {

        try {

            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.conectionclass();



            if (connect != null) {

                String query = "UPDATE APP_PRD_TIEMPOSTOTALES SET  ('" +ordenProduccion+"', '"+ horaInicioProceso+ "', '000:00:00')";
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);
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

    public void consultaOrden(String ordenProduccion) {

        try {

            ConnectionHelper connectionHelper = new ConnectionHelper();
            connect = connectionHelper.conectionclass();



            if (connect != null) {

                String query = "SELECT tiempo_ini FROM APP_PRD_TIEMPOSTOTALES WHERE id_op = '" +ordenProduccion+"'";
                Statement statement = connect.createStatement();
                ResultSet resultSet = statement.executeQuery(query);


                if (!resultSet.next() ) {

                    Toast.makeText(this, "No hay datos", Toast.LENGTH_SHORT).show();

                } else {



                        horainiciodb = resultSet.getString("tiempo_ini");


                        horaInicioDB = formated.parse(horainiciodb);

                        Toast.makeText(this, "la hora de inicio fue: "+ horaInicioDB, Toast.LENGTH_LONG).show();




                }



                ConnectionResult = "Success";
                isSucess = true;
                connect.close();


            } else {

                ConnectionResult = "Failed";
            }


        } catch (SQLException | ParseException throwables) {

            throwables.printStackTrace();
        }


    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        ordenProduccion = etOrdenProduccion.getText().toString();
        consultaOrden(ordenProduccion);

    }



    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}