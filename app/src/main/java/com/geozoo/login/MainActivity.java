package com.geozoo.login;

import android.content.Intent;


        import android.app.Activity;
//import android.icu.text.IDNA;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
        import android.widget.Toast;

import com.ramiromadraiga.login.R;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnLogin, btnRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Instanciación de los botone
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnRegistro = (Button)findViewById(R.id.btnRegistro);

        //Poniendo listeners a los botones
        btnLogin.setOnClickListener(this);
        btnRegistro.setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()){
            case R.id.btnLogin:
                i=new Intent(this,Login.class);
                startActivity(i);
                break;

            case R.id.btnRegistro:
                i=new Intent(this,Registrar.class);
                startActivity(i);
                break;

            default:
                break;
        }
    }


}
