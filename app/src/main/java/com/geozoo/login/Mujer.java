package com.geozoo.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ramiromadraiga.login.R;

public class Mujer extends Activity implements View.OnClickListener {
    TextView SI;
    ImageButton btnMensaje, btnLupa, btnCorazon, btnSOS, btnGrupo;
    Button Enviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mujer);
        btnMensaje = (ImageButton)findViewById(R.id.btnMensaje);
        btnLupa = (ImageButton)findViewById(R.id.btnLupa);
        btnCorazon = (ImageButton)findViewById(R.id.btnCorazon);
        btnSOS = (ImageButton)findViewById(R.id.btnSOS);
        btnGrupo = (ImageButton)findViewById(R.id.btnGrupo);

        btnMensaje.setOnClickListener(this);
        btnLupa.setOnClickListener(this);
        btnCorazon.setOnClickListener(this);
        btnSOS.setOnClickListener(this);
        btnGrupo.setOnClickListener(this);

        SI=(TextView) findViewById(R.id.sesionnI);
        Bundle bundle = getIntent().getExtras();
        if (null !=bundle){
            String s = bundle.getString("usuario");
            SI.setText(SI.getText().toString()+" "+s);
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

    public void llamar(String tel) {
        try {


            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel)));

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()){
            case R.id.btnMensaje:
                EnviarMensaje("+524434183099", "Auxilio! Puedo estar en peligro, esta es mi ubicacion actual.");
                break;

            case R.id.btnLupa:
                i=new Intent(this,Login.class);
                startActivity(i);
                break;

            case R.id.btnCorazon:
                i=new Intent(this,BluetoothActivity.class);
                startActivity(i);
                break;

            case R.id.btnSOS:
                llamar("4433766870");

                break;

            case R.id.btnGrupo:
                i=new Intent(this,Grupo.class);
                startActivity(i);
                break;
            default:
                break;
        }

    }
}