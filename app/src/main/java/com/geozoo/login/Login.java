package com.geozoo.login;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ramiromadraiga.login.R;

/**
 * Created by WILLY on 15/09/2016.
 */

public class Login extends Activity implements OnClickListener {

    private EditText user, pass;
    private Button mSubmit;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    //Aqui iria el link del dominio donde almacenariamos nuesro web service en el host,
    // pero por problemas de conexion de intenet esto por hoy no fue posible durante el evento HackMorelia 2017.
    private static final String LOGIN_URL = "http://172.20.10.4/mujer/login.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        mSubmit = (Button) findViewById(R.id.login);
        mSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.login:
                new AttemptLogin().execute();
                break;

            default:
                break;
        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Accediendo...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            int success;
            String username = user.getText().toString();
            String password = pass.getText().toString();
            try {

                List params = new ArrayList();
                params.add(new BasicNameValuePair("username", username));
                params.add(new BasicNameValuePair("password", password));

                Log.d("request!", "starting");
                JSONObject json = jsonParser.makeHttpRequest(LOGIN_URL, "POST",
                        params);
                Log.d("Login attempt", json.toString());

                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Login exitoso!", json.toString());
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(Login.this);
                    Editor edit = sp.edit();
                    edit.putString("username", username);
                    edit.commit();

                    Intent i = new Intent(Login.this, Mujer.class);
                    i.putExtra("usuario",user.getText().toString());
                    finish();
                    startActivity(i);


                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Login falló!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
    }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null) {
                Toast.makeText(Login.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}