package com.geozoo.login;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ramiromadraiga.login.R;


/**
 * Created by WILLY on 15/09/2016.
 */

public class Registrar extends Activity implements OnClickListener{
    private EditText user, pass;
    private Button  mRegister;
    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    private static final String REGISTER_URL = "http://172.20.10.4/mujer/register.php";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        user = (EditText)findViewById(R.id.username);
        pass = (EditText)findViewById(R.id.password);
        mRegister = (Button)findViewById(R.id.register);
        mRegister.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        new CreateUser().execute();

    }

    class CreateUser extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Registrar.this);
            pDialog.setMessage("Creando usuario...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        String username = user.getText().toString();
        String password = pass.getText().toString();
        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            int success;


            try {
                // Building Parameters
                List params = new ArrayList();
                params.add(new BasicNameValuePair("user", username));
                params.add(new BasicNameValuePair("pw", password));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                Log.d("Registering attempt", json.toString());
                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Log.d("User Created!", json.toString());
                    finish();
                    return json.getString(TAG_MESSAGE);
                }else{
                    Log.d("Registering Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(Registrar.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }
}