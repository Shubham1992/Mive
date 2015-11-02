package in.mive.app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mive.R;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import in.mive.app.helperclasses.JSONParser;
import in.mive.app.helperclasses.JSONParserStringReturn;
import in.mive.app.savedstates.SavedSellerIds;
import in.mive.app.savedstates.UpdateDummyItemListDTO;

/**
 * Created by Shubham on 9/11/2015.
 */
public class PreviousDummyOrders extends Activity {
    int userId;
    LayoutInflater  inflater;
    ScrollView scv;

    LinearLayout layout;
    private int index = 0;
    private JSONParserStringReturn jsonParser;
    private ProgressDialog pDialog;
    private int days;
    private String payment;
    private JSONArray arraysellerIds;
    private String sortby;
    Button filterBtn;
    private ImageView bckHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previous_orders_dummy);
        scv= (ScrollView) findViewById(R.id.scrollViewPreviousOrder);
        filterBtn = (Button) findViewById(R.id.btnFilter);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreviousDummyOrders.this, DummyOrderFilterActivity.class);


                startActivity(intent);

            }
        });
        bckHome = (ImageView) findViewById(R.id.imgbckHome);
        bckHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(getResources().getColor(R.color.my_statusbar_color));
        }
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        days = intent.getIntExtra("dayFilter", 50);
        payment = intent.getStringExtra("paymentFilter");
        try {
            arraysellerIds = new JSONArray(SavedSellerIds.getInstance().getList().toString());
            Log.e("array selelrid", arraysellerIds.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sortby = intent.getStringExtra("sortBy");

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) findViewById(R.id.previousOrderContainer);
        //........

        // if you call this inside an activity, simply replace getActivity() by "this"
        if(!isConnected(PreviousDummyOrders.this)) buildDialog(PreviousDummyOrders.this).show();
        else {
            // we have internet connection, so it is save to connect to the internet here

            new GetOrderData().execute();
        }

       /* new GetOrderData().execute();*/


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }
    //.......

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

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

    //..........

    private class GetOrderData extends AsyncTask<Void, Void, Void>
    {
        private String urlseedummyorders =  "http://www.mive.in/api/seeorders/";  //"http://postcatcher.in/catchers/55e75fce1ab1c60300001dee";



        HttpParams myParams = new BasicHttpParams();
        JSONObject json;
        HttpClient client = new DefaultHttpClient(myParams);



        private AlertDialog pDialog;

        private String jsonObjectresult;
        private JSONArray jsonArrayDummyOrders;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new SpotsDialog(PreviousDummyOrders.this);
            pDialog.setMessage("Just a moment...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            HttpConnectionParams.setConnectionTimeout(myParams, 10000);
            Log.e("inside", "service handler update async");
            // Making a request to urlseedummyorders and getting response
            // get he list fo items here and put it in json


            // Building Parameters
            JSONObject params= null;
            params = new JSONObject();
            try {

                Log.e("retrieved id", "" + userId);


                params.put("userId", "" + userId);
                params.put("days", days);

                params.put("payment",payment);
                params.put("sellers", arraysellerIds);
                params.put("sortby", sortby);



            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("params", params.toString());



            jsonParser = new JSONParserStringReturn();
            jsonObjectresult = jsonParser.makeHttpRequest(urlseedummyorders, "POST", params);
            try {

                jsonArrayDummyOrders = new JSONArray(jsonObjectresult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("Mive ", "json see order: "+jsonArrayDummyOrders.toString());


            return null;        }
        List<Map> resultListcat1;
        @Override
         protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();



            if(jsonArrayDummyOrders.length() <= 0)
            {
                TextView tvNoPrev = new TextView(PreviousDummyOrders.this);
                tvNoPrev.setText("No Orders Yet");
                return;
            }

            for (int i= 0; i < jsonArrayDummyOrders.length() ; i++)
            {   View prevOrderTab = inflater.inflate(R.layout.previous_dummy_order_tab, null);

                TextView tvOrderId = (TextView) prevOrderTab.findViewById(R.id.tvOrderId);
                TextView tvOrderDate = (TextView) prevOrderTab.findViewById(R.id.tvOrderDate);
                TextView tvOrderAmount = (TextView) prevOrderTab.findViewById(R.id.tvOrderAmount);

                TextView tvPaymentMode = (TextView)prevOrderTab.findViewById(R.id.tvPaymentMode);
                TextView tvPaymentStatus = (TextView)prevOrderTab.findViewById(R.id.tvPaymentStatus);
                TextView tvSellerName = (TextView)prevOrderTab.findViewById(R.id.tvsellername);



                JSONObject  objectOrder = null;
                try {

                    objectOrder = jsonArrayDummyOrders.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String orderId = objectOrder.optString("order_id");
                String orderDate = objectOrder.optString("deliveryTime");
                String orderAmount = objectOrder.optString("subtotal");
                String status = objectOrder.optString("status");
                String paymntMode = objectOrder.optString("payment_mode");
                String paymentStatus = objectOrder.optString("payment");
                JSONObject jsonObjectSeller = objectOrder.optJSONObject("seller");

                String sellername = jsonObjectSeller.optString("nameOfSeller");
                tvSellerName.setText(sellername);
                tvPaymentStatus.setText(paymentStatus);
                tvOrderId.setText(orderId);
                tvOrderDate.setText(orderDate);
                tvOrderAmount.setText("Rs. "+orderAmount);
                tvPaymentMode.setText(paymntMode);


                prevOrderTab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(PreviousDummyOrders.this, ParticularOrderDetail.class);
                        intent.putExtra("orderId", orderId);
                        startActivity(intent);
                    }
                });
                //#21bdba



                layout.addView(prevOrderTab);

            }


        }
            }




    @Override
    public void onBackPressed() {
        super.onBackPressed();

/*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
    finish();}
}
