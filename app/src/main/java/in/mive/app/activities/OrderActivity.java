package in.mive.app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.mive.R;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import in.mive.app.helperclasses.JSONParser;
import in.mive.app.savedstates.JSONDTO;

/**
 * Created by Shubham on 9/8/2015.
 */
public class OrderActivity extends Activity implements DatePickerDialog.OnDateSetListener, com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

TextView tvsubtotal, tvtotal, tvtoday , tvTmrw , tvSomeOther, tvDateSelected;
    Button btnPlaceorder;
    private JSONParser jsonParser;
    private String dateSelected =null;
    boolean isDatePicked = false;
    String sellerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        tvsubtotal = (TextView) findViewById(R.id.tvsubtotal);
        tvtotal = (TextView)findViewById(R.id.tvtotal);
        tvtoday = (TextView)findViewById(R.id.tvToday);
        tvTmrw = (TextView)findViewById(R.id.tvTmrw);
        tvSomeOther = (TextView)findViewById(R.id.tvSomeOther);
        tvDateSelected = (TextView)findViewById(R.id.tvDateSelected);
        btnPlaceorder = (Button)findViewById(R.id.btnplaceOrder);

        Intent intent =getIntent();
        int price = intent.getIntExtra("price", 0);
        sellerId = intent.getStringExtra("sellerId");

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
        tvsubtotal.setText("Rs. "+price);


        tvtotal.setText("Rs. "+price);

        tvtoday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSomeOther.setBackgroundResource(R.drawable.dayselectorborder);
                tvTmrw.setBackgroundResource(R.drawable.dayselectorborder);
                tvtoday.setBackgroundResource(R.drawable.dayselectedbck);
                tvtoday.setTextColor(Color.WHITE);
                tvTmrw.setTextColor(Color.parseColor("#21bdba"));
                tvSomeOther.setTextColor(Color.parseColor("#21bdba"));
                Date dNow = new Date();
                SimpleDateFormat ft =
                        new SimpleDateFormat("yyyy-MM-dd");

                tvDateSelected.setText("" + ft.format(dNow));
                isDatePicked=true;

            }
        });

        tvTmrw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSomeOther.setBackgroundResource(R.drawable.dayselectorborder);
                tvtoday.setBackgroundResource(R.drawable.dayselectorborder);
                tvTmrw.setBackgroundResource(R.drawable.dayselectedbck);
                tvTmrw.setTextColor(Color.WHITE);
                tvtoday.setTextColor(Color.parseColor("#21bdba"));
                tvSomeOther.setTextColor(Color.parseColor("#21bdba"));
                Date dt = new Date( );
                Calendar c = Calendar.getInstance();
                c.setTime(dt);
                c.add(Calendar.DATE, 1);
                dt = c.getTime();
                SimpleDateFormat ft =
                        new SimpleDateFormat ("yyyy-MM-dd");

                tvDateSelected.setText(""+ ft.format(dt));
                isDatePicked=true;
            }
        });

        tvSomeOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvtoday.setBackgroundResource(R.drawable.dayselectorborder);
                tvTmrw.setBackgroundResource(R.drawable.dayselectorborder);
                tvSomeOther.setBackgroundResource(R.drawable.dayselectedbck);
                tvSomeOther.setTextColor(Color.WHITE);
                tvtoday.setTextColor(Color.parseColor("#21bdba"));
                tvTmrw.setTextColor(Color.parseColor("#21bdba"));

              /* final AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                final DatePicker picker = new DatePicker(OrderActivity.this);
                picker.setCalendarViewShown(true);

                builder.setTitle("Select Delivery Date");
                builder.setView(picker);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int d = picker.getDayOfMonth();
                       int m = picker.getMonth()+1;
                       int y = picker.getYear();
                      dateSelected = ""+y+"-"+m+"-"+d;
                        tvDateSelected.setText(dateSelected);
                        isDatePicked=true;

                    }
                });

                builder.show();*/

                Calendar now = Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd
                        = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                        OrderActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                //dpd.show(OrderActivity.this, "Datepickerdialog");
                dpd.show(getFragmentManager(), "Pick Date");


            }
        });

        btnPlaceorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(isDatePicked == true)
                new SendDataAddToCart().execute();

            else
                Toast.makeText(OrderActivity.this, "Select a Delivery Date", Toast.LENGTH_SHORT).show();


            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog, int i, int i1, int i2) {
        String date = ""+i+"-"+(i1+1)+"-"+i2;
        dateSelected = ""+i+"-"+i1+"-"+i2;
        tvDateSelected.setText(date);
        isDatePicked=true;
    }

    private class SendDataAddToCart extends AsyncTask<Void, Void, Void>
    {
        private String urlMakeOrder = "http://www.mive.in/api/makeorder/";//"http://postcatcher.in/catchers/55f1f07178098e03000010f6";
        private JSONObject jsonObj;
        private ProgressDialog pDialog;
        HttpParams myParams = new BasicHttpParams();
        JSONObject json;
        HttpClient client = new DefaultHttpClient(myParams);
        private JSONObject jsonObjectresult;
        //HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(OrderActivity.this);
            pDialog.setMessage("Getting Products...");
            pDialog.setCancelable(false);


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            HttpConnectionParams.setConnectionTimeout(myParams, 10000);
            Log.e("inside", "service handler");
            // Making a request to urlMakeOrder and getting response
            // get he list fo items here and put it in json
           // itemsList = ItemListDTO.getInstance().getProductMap();

            // Building Parameters
            JSONObject params= null;
            params = new JSONObject();
            try {
                SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
                int restoreduserid = prefs.getInt("userId", 0);
                Log.e("retrieved id", "" + restoreduserid);
                String restoredcartid = JSONDTO.getInstance().getJsonUser().optJSONObject("cart").optString("cart_id");

                params.put("cartId", restoredcartid);
                params.put("userId", restoreduserid);
                params.put("deliveryTime", tvDateSelected.getText().toString());
                params.put("orderMsg", "some test message");
                params.put("sellerId",sellerId );




            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("params", params.toString());

            jsonParser = new JSONParser();

            jsonObjectresult = jsonParser.makeHttpRequest(urlMakeOrder, "POST", params);
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
            Log.e("make order result", jsonObjectresult.toString());

            if(! jsonObjectresult.optString("status").toString().equalsIgnoreCase("success"))
            {
                Toast.makeText(OrderActivity.this, "Oops..Something went wrong. Try again in some time",Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent =new Intent(OrderActivity.this, PreviousOrders.class);

            SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
            int restoreduserid = prefs.getInt("userId", 0);
            intent.putExtra("userId", restoreduserid);

            btnPlaceorder.setEnabled(false);
            startActivity(intent);



            finish();
            Toast.makeText(OrderActivity.this, "order successfull",Toast.LENGTH_SHORT).show();
        }

    }

}
