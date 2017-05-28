package com.geozoo.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ramiromadraiga.login.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Juanix on 28/5/2017.
 */

public class BluetoothActivity extends Activity{
    protected static final String TAG = "BLUETOOTH";
    UUID uuid = UUID.fromString("0001101-0000-1000-8000-00805F9B34FB");
    String dStarted = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
    String dFinished = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
    TextView txtViewParaPruebas, txtViewValores;
    BluetoothAdapter bluetooth;
    Button btnBuscarDispositivos;
    private final static int REQUEST_ENABLE_BT = 1,DISCOVERY_REQUEST = 1;
    ArrayList list;
    ListView listaDisp;
    ArrayAdapter mArrayAdapter;
    String messageReceived = "No recibido";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        bluetooth = BluetoothAdapter.getDefaultAdapter();
        listaDisp = (ListView) findViewById(R.id.listaDisp);
        btnBuscarDispositivos = (Button) findViewById(R.id.btnBuscarDispositivos);
        txtViewParaPruebas=(TextView)findViewById(R.id.txtViewParaPruebas);
        btnBuscarDispositivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*activaBluetooth();
                deviceList.clear();
                list = new ArrayList();
                buscarDispositivos();
                muestraDialogoDeAnillo();*/
                ConectaPorDefecto();
            }
        });

        listaDisp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //txtViewParaPruebas.setText(String.valueOf(deviceList.get(0)));
                //txtViewParaPruebas.setText(String.valueOf(position));

                connectToServerSocket(deviceList.get(position),uuid);


            }
        });

        activaBluetooth();

    }

    public void activaBluetooth(){
        //Se empieza por saber si se tiene el bluetooth
        if (bluetooth == null) {
            // Device does not support Bluetooth
        }
        //Si se tiene bluetooth, éste se manda encender pidiendo permiso primeramente
        if (!bluetooth.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void ponerAlDescubierto(){
        startActivityForResult(
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE),
                DISCOVERY_REQUEST);
    }


    private static final int ENABLE_BLUETOOTH = 1;

    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        if (requestCode == ENABLE_BLUETOOTH)
            if (resultCode == RESULT_OK) {
                // Bluetooth has been enabled, initialize the UI.
            }

        /**
         * Listing 16-4: Monitoring discoverability request approval
         */
        if (requestCode == DISCOVERY_REQUEST) {
            if (resultCode == RESULT_CANCELED) {
                Log.d(TAG, "Discovery cancelled by user");
            }
        }

    }

    private ArrayList<BluetoothDevice> deviceList =
            new ArrayList<BluetoothDevice>();

    private void buscarDispositivos() {
        registerReceiver(discoveryResult,
                new IntentFilter(BluetoothDevice.ACTION_FOUND));

        if (bluetooth.isEnabled() && !bluetooth.isDiscovering())
            deviceList.clear();
        bluetooth.startDiscovery();


        BroadcastReceiver discoveryMonitor = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (dStarted.equals(intent.getAction())) {
// Discovery has started.
                    Log.d(TAG, "Comenzó la búsqueda...");
                    Toast.makeText(getApplicationContext(), "Buscando dispositivos Bluetooth", Toast.LENGTH_SHORT).show();
                }
                else if (dFinished.equals(intent.getAction())) {
// Discovery has completed.
                    Log.d(TAG, "Búsqueda completa.");
                    Toast.makeText(getApplicationContext(), "Finalizó la búsqueda", Toast.LENGTH_SHORT).show();
                    llenaListaDisp();


                }
            }
        };
        registerReceiver(discoveryMonitor,
                new IntentFilter(dStarted));
        registerReceiver(discoveryMonitor,
                new IntentFilter(dFinished));
    }

    BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String remoteDeviceName =
                    intent.getStringExtra(BluetoothDevice.EXTRA_NAME);

            BluetoothDevice remoteDevice =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            deviceList.add(remoteDevice);
            list.add(remoteDeviceName);
            Log.d(TAG, "Discovered " + remoteDeviceName);
        }
    };

    private BluetoothSocket transferSocket;

    private UUID startServerSocket(BluetoothAdapter bluetooth) {

        String name = "Servidor Bluetooth";

        try {
            final BluetoothServerSocket btserver =
                    bluetooth.listenUsingRfcommWithServiceRecord(name, uuid);

            Thread acceptThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        // Block until client connection established.
                        BluetoothSocket serverSocket = btserver.accept();
                        // Start listening for messages.
                        StringBuilder incoming = new StringBuilder();
                        listenForMessages(serverSocket, incoming);
                        // Add a reference to the socket used to send messages.
                        transferSocket = serverSocket;
                    } catch (IOException e) {
                        Log.e("BLUETOOTH", "Server connection IO Exception", e);
                    }
                }
            });
            acceptThread.start();
        } catch (IOException e) {
            Log.e("BLUETOOTH", "Socket listener IO Exception", e);
        }
        return uuid;
    }

    private void connectToServerSocket(BluetoothDevice device, UUID uuid) {
        try{
            final BluetoothDevice knownDevice = bluetooth.getRemoteDevice("20:16:09:22:41:92");
            BluetoothSocket clientSocket = knownDevice.createRfcommSocketToServiceRecord(uuid);

            // Block until server connection accepted.
            clientSocket.connect();
            txtViewParaPruebas.setText("Conectado!");


            // Start listening for messages.
            StringBuilder incoming = new StringBuilder();

            //Para recibir datos desde un dispositivo bluetooth descomentar la siguiente línea y modificar
            //el método listenForMessages de acuerdo a lo que tenga que hacer la aplicación
            listenForMessages(clientSocket, incoming);

            // Add a reference to the socket used to send messages.
            transferSocket = clientSocket;


            /*btnDetenido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage(transferSocket, "S");
                }
            });*/

        } catch (IOException e) {
            Log.e("BLUETOOTH", "Blueooth client I/O Exception", e);
            txtViewParaPruebas.setText("No conectado");
        }
    }

    private void ConectaPorDefecto(){
        try{
            //"20:16:09:22:41:92"          HC-06
            //"D0:51:62:57:61:B4"          Xperia Tipo
            final BluetoothDevice knownDevice = bluetooth.getRemoteDevice("D0:51:62:57:61:B4");
            final BluetoothSocket clientSocket = knownDevice.createRfcommSocketToServiceRecord(uuid);

            // Block until server connection accepted.
            clientSocket.connect();
            txtViewParaPruebas.setText("Conectado!");


            // Start listening for messages.
            final StringBuilder incoming = new StringBuilder();

            //Para recibir datos desde un dispositivo bluetooth descomentar la siguiente línea y modificar
            //el método listenForMessages de acuerdo a lo que tenga que hacer la aplicación
            Thread th1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    listenForMessages(clientSocket, incoming);
                }
            }
            );
            th1.start();

            // Add a reference to the socket used to send messages.
            transferSocket = clientSocket;


            /*btnDetenido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage(transferSocket, "S");
                }
            });*/

        } catch (IOException e) {
            Log.e("BLUETOOTH", "Blueooth client I/O Exception", e);
            txtViewParaPruebas.setText("No conectado");
        }
    }
    private void sendMessage(BluetoothSocket socket, String message) {
        OutputStream outStream;

        try {
            outStream = socket.getOutputStream();

            // Add a stop character.
            byte[] byteArray = (message + " ").getBytes();
            byteArray[byteArray.length - 1] = 0;

            outStream.write(byteArray);
            txtViewParaPruebas.setText("Mensaje Enviado: "+message);
        } catch (IOException e) {
            Log.e(TAG, "Message send failed.", e);
            txtViewParaPruebas.setText("El mensaje no se envió");
        }
    }

    private boolean listening = false;
    private void listenForMessages(BluetoothSocket socket,
                                   StringBuilder incoming) {
        listening = true;
        final String mi = "jfnd";
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        String result = "";

        try {
            InputStream instream = socket.getInputStream();
            int bytesRead = -1;
            while (listening) {
                bytesRead = instream.read(buffer);
                if (bytesRead != -1) {

                    result = "";
                    while ((bytesRead == bufferSize) &&(buffer[bufferSize-1] != 0)){
                        result = result + new String(buffer, 0, bytesRead - 1);
                        bytesRead = instream.read(buffer);

                    }
                    result = result + new String(buffer, 0, bytesRead - 1);
                    incoming.append(result+" ");
                    final String enviar = result;
                    Log.d(TAG, result);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(Integer.parseInt(enviar.toString().replace(" ", "")) > 90){
                                EnviarMensaje("+524434183099", "Riesgo de salud de la mujer X");
                            }
                            txtViewParaPruebas.setText(enviar);
                        }
                    });
                    Log.d("BytesRead:::::    ",String.valueOf(socket.getInputStream()));

                }

            }
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Message received failed.", e);
        }
        finally {
        }
    }

    private void EnviarMensaje (String Numero, String Mensaje){
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(Numero,null,Mensaje,null,null);

            Toast.makeText(getApplicationContext(), "Mensaje Enviado.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Mensaje no enviado, datos incorrectos.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void llenaListaDisp(){
        mArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList);
        //listaDispSinc.setAdapter(mArrayAdapter);
        listaDisp.setAdapter(mArrayAdapter);
        //txtViewParaPruebas.setText("Tamaño lista: "+deviceList.size());
        //list.clear();
        bluetooth.cancelDiscovery();
    }

    private void muestraDialogoDeAnillo(){
        final ProgressDialog ringProgressDialog = ProgressDialog.show(getApplicationContext(), "Espere", "Buscando dispositivos bluetooth...", true);
        ringProgressDialog.setCancelable(true);//Con esto se especifica si el dialogo es removible de la pantalla

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ringProgressDialog.dismiss();
            }
        }).start();
    }
}
