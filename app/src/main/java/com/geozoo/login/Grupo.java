package com.geozoo.login;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.util.ArrayList;
        import java.util.List;

        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.client.ClientProtocolException;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import android.annotation.SuppressLint;
        import android.app.Activity;
        import android.content.pm.ActivityInfo;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;

        import com.ramiromadraiga.login.R;

public class Grupo extends Activity{

    private EditText dni;
    private EditText nombre;
    private EditText telefono;
    private EditText email;
    //private Button insertar;
    private Button mostrar;
    //private Button update;
    //private Button eliminar;
    private ImageButton mas;
    private ImageButton menos;
    private int posicion=0;
    private List<Usuarios> listaPersonas;
    private Usuarios personas;
    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_grupo);

        listaPersonas=new ArrayList<Usuarios>();
        dni=(EditText)findViewById(R.id.dni);
        nombre=(EditText)findViewById(R.id.nombre);
        telefono=(EditText)findViewById(R.id.telefono);

        //Mostramos los datos de la persona por pantalla.
        mostrar=(Button)findViewById(R.id.mostrar);
        mostrar.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new Mostrar().execute();
            }
        });

        mas=(ImageButton)findViewById(R.id.mas);
        mas.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(!listaPersonas.isEmpty()){
                    if(posicion>=listaPersonas.size()-1){
                        posicion=listaPersonas.size()-1;
                        mostrarPersona(posicion);
                    }else{
                        posicion++;

                        mostrarPersona(posicion);
                    }
                }
            }

        });
        //Se mueve por nuestro ArrayList mostrando el objeto anterior
        menos=(ImageButton)findViewById(R.id.menos);

        menos.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(!listaPersonas.isEmpty()){
                    if(posicion<=0){
                        posicion=0;
                        mostrarPersona(posicion);
                    }
                    else{
                        posicion--;
                        mostrarPersona(posicion);
                    }
                }
            }
        });
    }


    private String mostrar(){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://172.20.10.4/mujer/selectAllJSON.php");
        String resultado="";
        HttpResponse response;
        try {
            response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream instream = entity.getContent();
            resultado= convertStreamToString(instream);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultado;
    }

    private String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private boolean filtrarDatos(){
        listaPersonas.clear();
        String data=mostrar();
        if(!data.equalsIgnoreCase("")){
            JSONObject json;
            try {
                json = new JSONObject(data);
                JSONArray jsonArray = json.optJSONArray("username");
                for (int i = 0; i < jsonArray.length(); i++) {
                    personas=new Usuarios();
                    JSONObject jsonArrayChild = jsonArray.getJSONObject(i);
                    personas.setId(jsonArrayChild.optString("id"));
                    personas.setUsername(jsonArrayChild.optString("username"));
                    personas.setPassword(jsonArrayChild.optString("password"));
                    listaPersonas.add(personas);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    private void mostrarPersona(final int posicion){
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Usuarios personas=listaPersonas.get(posicion);
                nombre.setText(personas.getUsername());
                dni.setText(personas.getId());
                //telefono.setText(personas.getPassword());

            }
        });
    }
    class Mostrar extends AsyncTask<String,String,String>{
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            if(filtrarDatos())mostrarPersona(posicion);
            return null;
        }
    }


}
