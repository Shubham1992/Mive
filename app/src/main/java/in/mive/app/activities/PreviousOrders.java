package in.mive.app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import in.mive.app.helperclasses.ServiceHandler;
import in.mive.app.savedstates.JSONDTO;

/**
 * Created by Shubham on 9/11/2015.
 */
public class PreviousOrders extends Activity {
    int userId;
    LayoutInflater  inflater;
    ScrollView scv;

    LinearLayout layout;
    private int index = 0;

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

        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) findViewById(R.id.previousOrderContainer);
        //........

        // if you call this inside an activity, simply replace getActivity() by "this"
        if(!isConnected(PreviousOrders.this)) buildDialog(PreviousOrders.this).show();
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

    //..........

    private class GetOrderData extends AsyncTask<Void, Void, Void>
    {

        String id;

        private String urlPrevOrders;

        private ProgressDialog pDialog;
        // private ProgressDialog pDialog;
        private JSONObject jsonObjcart;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PreviousOrders.this);
            pDialog.setMessage("Just a moment...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            Log.e("inside", "service handler");
            // Making a request to urlcat1 and getting response
            id = JSONDTO.getInstance().getJsonUser().optJSONObject("cart").optString("cart_id");
            Log.e("id  of cart", id);
            urlPrevOrders = "http://www.mive.in/api/user/orders/"+userId+"/?format=json";


            String jsonStrCart = sh.makeServiceCall(urlPrevOrders, ServiceHandler.GET);



            Log.d("Response orders: ", "> " + jsonStrCart);


            if (jsonStrCart != null) {
                try {

                    jsonObjcart = new JSONObject(jsonStrCart);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the cart");
            }

            return null;
        }
        List<Map> resultListcat1;
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();



            JSONArray orderReslt = jsonObjcart.optJSONArray("results");
            String count = jsonObjcart.optString("count");
            if(Integer.parseInt(count)<=0)
            {
                TextView tvNoPrev = new TextView(PreviousOrders.this);
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

            scv.fullScroll(ScrollView.FOCUS_DOWN);
        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

/*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
    finish();}
}
