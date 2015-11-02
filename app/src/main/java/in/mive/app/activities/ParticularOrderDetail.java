package in.mive.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mive.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import in.mive.app.adapter.PArticularImagesCustomPagerAdapter;
import in.mive.app.helperclasses.ServiceHandler;
import in.mive.app.imageloader.ImageLoader;
import in.mive.app.savedstates.JSONDTO;

/**
 * Created by Shubham on 11/4/2015.
 */
public class ParticularOrderDetail extends Activity {

    private String orderId;
    LayoutInflater layoutInflater;
    ViewPager layoutInvoiceImages;
    ImageView viewBckPress;

    PArticularImagesCustomPagerAdapter customPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.particular_order_details);
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        layoutInvoiceImages = (ViewPager) findViewById(R.id.imgContainer);
        viewBckPress = (ImageView) findViewById(R.id.imgbckHome);
        viewBckPress.setOnClickListener(new View.OnClickListener() {
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


        new GetOrderData().execute();
         }

    private class GetOrderData extends AsyncTask<Void, Void, Void>
    {

        String id;

        private String urlOrders;

        private ProgressDialog pDialog;
        // private ProgressDialog pDialog;
        private JSONObject jsonObjcart;
        String jsonOrderDetail;
        private JSONObject objectOrder;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ParticularOrderDetail.this);
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

            urlOrders = "http://www.mive.in/api/order/"+ orderId;



            jsonOrderDetail = sh.makeServiceCall(urlOrders, ServiceHandler.GET);
            try {
                objectOrder = new JSONObject(jsonOrderDetail);
            } catch (JSONException e) {
                e.printStackTrace();
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


            TextView tvOrderId = (TextView) findViewById(R.id.tvOrderId);
            TextView tvOrderDate = (TextView) findViewById(R.id.tvOrderDate);
            TextView tvOrderAmount = (TextView) findViewById(R.id.tvOrderAmount);

            TextView tvPaymentMode = (TextView)findViewById(R.id.tvPaymentMode);
            TextView tvPaymentStatus = (TextView)findViewById(R.id.tvPaymentStatus);
            TextView tvSellerName = (TextView)findViewById(R.id.tvsellername);


            final String orderId = objectOrder.optString("order_id");
            String orderDate = objectOrder.optString("deliveryTime");
            String orderAmount = objectOrder.optString("subtotal");


            String paymentStatus = objectOrder.optString("payment");
            JSONObject jsonObjectSeller = objectOrder.optJSONObject("seller");
            String sellername = jsonObjectSeller.optString("nameOfSeller");

            JSONArray jsonArrayInvoiceImages = objectOrder.optJSONArray("invoices");
            if(jsonArrayInvoiceImages.length() <= 0)
                layoutInvoiceImages.setVisibility(View.GONE);

            customPagerAdapter = new PArticularImagesCustomPagerAdapter(ParticularOrderDetail.this,jsonArrayInvoiceImages  );
            layoutInvoiceImages.setAdapter(customPagerAdapter);
            layoutInvoiceImages.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
            {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                {

                }

                @Override
                public void onPageSelected(int position)
                {}

                @Override
                public void onPageScrollStateChanged(int state)
                {

                }
            });



            /*for (int i =0 ; i < jsonArrayInvoiceImages.length(); i++)
            {
                JSONObject jsonObjectImagesInvoice = jsonArrayInvoiceImages.optJSONObject(i);
                String url = jsonObjectImagesInvoice.optString("geturl");

                View viewImages = layoutInflater.inflate(R.layout.img_invoice_tab, layoutInvoiceImages, false);
                ImageView imageViewInvoice = (ImageView) viewImages.findViewById(R.id.img_invoice);

                int loader = R.drawable.tomato;
                ImageLoader imgLoader = new ImageLoader(ParticularOrderDetail.this);
                imgLoader.DisplayImage("http://www.mive.in/" + url, loader, imageViewInvoice);

                layoutInvoiceImages.addView(viewImages);

            }*/

            tvSellerName.setText(sellername);
            tvPaymentStatus.setText(paymentStatus);
            tvOrderId.setText(orderId);
            tvOrderDate.setText(orderDate);
            tvOrderAmount.setText("Rs. "+orderAmount);


        }
    }


}
