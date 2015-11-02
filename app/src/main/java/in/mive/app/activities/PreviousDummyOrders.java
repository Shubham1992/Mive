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

import in.mive.app.helperclasses.JSONParser;
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
    private JSONParser jsonParser;
    private ProgressDialog pDialog;
    private int days;
    private String payment;
    private JSONArray arraysellerIds;
    private String sortby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.previous_orders);
        scv= (ScrollView) findViewById(R.id.scrollViewPreviousOrder);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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



        private ProgressDialog pDialog;

        private JSONObject jsonObjectresult;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PreviousDummyOrders.this);
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
                params.put("sellers",arraysellerIds);
                params.put("sortby", sortby);



            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("params", params.toString());



            jsonParser = new JSONParser();
            jsonObjectresult = jsonParser.makeHttpRequest(urlseedummyorders, "POST", params);



            return null;        }
        List<Map> resultListcat1;
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();



            JSONArray orderReslt = jsonObjectresult.optJSONArray("results");
            String count = jsonObjectresult.optString("count");
            if(Integer.parseInt(count)<=0)
            {
                TextView tvNoPrev = new TextView(PreviousDummyOrders.this);
                tvNoPrev.setText("No Orders Yet");
                return;
            }

            for (int i= 0; i < orderReslt.length() ; i++)
            {   View prevOrderTab = inflater.inflate(R.layout.previous_order_tab, null);

                TextView tvOrderId = (TextView) prevOrderTab.findViewById(R.id.tvOrderId);
                TextView tvOrderDate = (TextView) prevOrderTab.findViewById(R.id.tvOrderDate);
                TextView tvOrderAmount = (TextView) prevOrderTab.findViewById(R.id.tvOrderAmount);
                TextView tvPlaced = (TextView) prevOrderTab.findViewById(R.id.tvPlaced);
                TextView tvProcessing = (TextView) prevOrderTab.findViewById(R.id.tvProcessing);
                TextView tvDelivered = (TextView) prevOrderTab.findViewById(R.id.tvdelivered);
                TextView tvPaymentMode = (TextView)prevOrderTab.findViewById(R.id.tvPaymentMode);
                TextView tvSellerName = (TextView)prevOrderTab.findViewById(R.id.tvsellername);

                tvPlaced.setBackgroundColor(Color.parseColor("#d3d3d3"));
                tvProcessing.setBackgroundColor(Color.parseColor("#d3d3d3"));
                tvDelivered.setBackgroundColor(Color.parseColor("#d3d3d3"));

                JSONObject  objectOrder = null;
                try {

                    objectOrder = orderReslt.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String orderId = objectOrder.optString("order_id");
                String orderDate = objectOrder.optString("deliveryTime");
                String orderAmount = objectOrder.optString("subtotal");
                String status = objectOrder.optString("status");
                String paymntMode = objectOrder.optString("payment_mode");
                JSONObject jsonObjectSeller = objectOrder.optJSONObject("seller");

                String sellername = jsonObjectSeller.optString("nameOfSeller");
                tvSellerName.setText(sellername);

                tvOrderId.setText(orderId);
                tvOrderDate.setText(orderDate);
                tvOrderAmount.setText("Rs. "+orderAmount);
                tvPaymentMode.setText(paymntMode);

                Log.e("status", status);
                if(status.equalsIgnoreCase("PLACED"))
                {
                    tvPlaced.setBackgroundColor(Color.parseColor("#21bdba"));
                }
                else if(status.equalsIgnoreCase("PROCESSING"))
                {
                    tvPlaced.setBackgroundColor(Color.parseColor("#21bdba"));
                    tvProcessing.setBackgroundColor(Color.parseColor("#21bdba"));
                }
                else if(status.equalsIgnoreCase("DELIVERED"))
                {Log.e("inside", "all blue");
                    tvPlaced.setBackgroundColor(Color.parseColor("#21bdba"));
                    tvProcessing.setBackgroundColor(Color.parseColor("#21bdba"));
                    tvDelivered.setBackgroundColor(Color.parseColor("#21bdba"));
                }
                //#21bdba



                layout.addView(prevOrderTab);

            }


        }


    }
    private class SendUpdateDataAddToCart extends AsyncTask<Void, Void, Void>
    {
        private String urlupdatecart =  "http://www.mive.in/api/seeorders/";  //"http://postcatcher.in/catchers/55e75fce1ab1c60300001dee";

        private JSONObject jsonObj;

        HttpParams myParams = new BasicHttpParams();
        JSONObject json;
        HttpClient client = new DefaultHttpClient(myParams);
        private JSONObject jsonObjectresult;
        private List<HashMap> itemsList;
       /* HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);*/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PreviousDummyOrders.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);


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
                SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
                int restoreduserid = prefs.getInt("userId",0);
                Log.e("retrieved id", "" + restoreduserid);


                params.put("userId",""+restoreduserid);
                JSONObject jsonItems = new JSONObject();
                for (int i = 0; i < itemsList.size(); i++)
                {
                    HashMap<String,String> hashMap = new HashMap();

                    hashMap.put("qty", "" + itemsList.get(i).get("qty").toString());

                    if(itemsList.get(i).get("itemId") != null)
                        hashMap.put("itemId",""+itemsList.get(i).get("itemId").toString());


                    JSONObject objOneItem = new JSONObject(hashMap.toString());
                    jsonItems.put(""+(i+1),objOneItem);

                }

                Log.e("json update", jsonItems.toString());
                params.put("items",jsonItems);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("params", params.toString());



            jsonParser = new JSONParser();
            jsonObjectresult = jsonParser.makeHttpRequest(urlupdatecart, "POST", params);
            return null;
        }
        List<Map> resultList;
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(jsonObjectresult != null)
                Log.e("result", jsonObjectresult.toString());
            UpdateDummyItemListDTO.getInstance().setItemlist(new ArrayList<HashMap>());




          /*  btnPlusQuantity.setEnabled(true);
            btnMinusQuantity.setEnabled(true);*/
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

/*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
    finish();}
}
