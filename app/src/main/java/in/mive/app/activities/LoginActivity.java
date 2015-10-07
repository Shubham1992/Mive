package in.mive.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mive.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import in.mive.app.JSONParser;


/**
 * Created by shubham on 11-08-2015.
 */
public class LoginActivity extends Activity {
    private EditText etPhone;
    private EditText etPassword;
    private Button btnLogin;
    String phone;
    String password;
    JSONParser jsonParser = new JSONParser();
    private String url = "http://www.mive.in/api/login/?format=json";
    public static final String prefUserId = "userIdPref";
    HttpResponse response;
    private Button btnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);
        etPhone= (EditText) findViewById(R.id.etLoginPhone);
        etPassword= (EditText) findViewById(R.id.etLoginPassword);
        btnLogin=(Button)findViewById(R.id.btnLogin);
        btnSkip= (Button) findViewById(R.id.btnskip);



        SharedPreferences prefs = getSharedPreferences(prefUserId , MODE_PRIVATE);
        int restoreduserid = prefs.getInt("userId",0);
        Log.e("retrieved id", "" + restoreduserid);

        if(restoreduserid != 0)
        {
            Intent intent=new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("id",restoreduserid);
            intent.putExtra("loggedIn", true);

            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    btnLogin.setText("Logging in...");
                    btnLogin.setEnabled(false);
                    btnLogin.setAlpha((float) 0.5);

        btnSkip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        startActivity(intent);

                    }
                });
        if(!isConnected(LoginActivity.this)) buildDialog(LoginActivity.this).show();
        else {
                    // we have internet connection, so it is save to connect to the internet here
                    new GetData().execute();
                }


        //        new GetData().execute();

            }
        });


    }
    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet connection.");
        builder.setMessage("You have no internet connection");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                recreate();
            }
        });

        return builder;
    }

    private class GetData extends AsyncTask<Void, Void, Void>
    {
        private String url = "http://www.mive.in/api/login?format=json";
        private JSONObject jsonObj;
        private ProgressDialog pDialog;
        HttpParams myParams = new BasicHttpParams();
        JSONObject json;
        HttpClient client = new DefaultHttpClient(myParams);
       /* HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);*/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Getting Products...");
            pDialog.setCancelable(false);


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            HttpConnectionParams.setConnectionTimeout(myParams, 10000);
            Log.e("inside", "service handler");
            // Making a request to url and getting response
            phone = etPhone.getText().toString();

            password = etPassword.getText().toString();


            // Building Parameters
            JSONObject params=new JSONObject();


            try {
                params.put("mobile",phone);
                params.put("password",password);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            json = jsonParser.makeHttpRequest(url,
                    "POST", params);

            return null;
        }
        List<Map> resultList;
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();



            String strReslt=json.optString("result");
            int intUser=json.optInt("user");

            if(strReslt.equals("true"))
            {
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("id",intUser);
                intent.putExtra("loggedIn", true);

                SharedPreferences.Editor editor = getSharedPreferences(prefUserId, MODE_PRIVATE).edit();
                editor.putInt("userId", intUser);
                editor.commit();




                startActivity(intent);
                finish();

            }
            else
            {
                btnLogin.setText("Login");
                btnLogin.setEnabled(true);
                btnLogin.setAlpha(1);
                Toast.makeText(LoginActivity.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
