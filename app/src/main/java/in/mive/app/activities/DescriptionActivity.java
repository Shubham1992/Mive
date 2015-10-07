package in.mive.app.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mive.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import in.mive.app.ServiceHandler;
import in.mive.app.adapter.TabsPagerAdapter;
import in.mive.app.imageloader.ImageLoader;
import in.mive.app.savedstates.JSONDTO;

/**
 * Created by Shubham on 9/20/2015.
 */
public class DescriptionActivity extends Activity {
ImageView imgPrdct, imgbck; TextView desc , name, category, tvGrade, tvPrice, tvUnit, origin;
    String id;
    private ProgressDialog  pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description);
        imgPrdct = (ImageView) findViewById(R.id.imgProduct);
        desc = (TextView) findViewById(R.id.desctxt);
        name = (TextView) findViewById(R.id.name);
        category = (TextView) findViewById(R.id.category);
        tvGrade = (TextView) findViewById(R.id.tvgrade);
        tvPrice = (TextView) findViewById(R.id.tvprice);
        tvUnit = (TextView) findViewById(R.id.tvunit);
        origin = (TextView) findViewById(R.id.originname);
        imgbck = (ImageView) findViewById(R.id.imgbck);

        imgbck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent=getIntent();
        id = intent.getStringExtra("prdctId");

        new  GetProductDesc().execute();
    }

    private class GetProductDesc extends AsyncTask<Void, Void, Void>
    {

        private String urlProductDesc ="http://www.mive.in/api/product/"+id;
        private  JSONObject jsonObjPrdctDesc;
        //private String urlCart = "http://www.mive.in/api/cart/cartitems/"+id+"/?format=json";



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(DescriptionActivity.this);
            pDialog.setMessage("Getting Products...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            Log.e("inside", "service handler");
            // Making a request to urlcat1 and getting response

            //String jsonStrcat1 = sh.makeServiceCall(urlcat1, ServiceHandler.GET);

            sh = new ServiceHandler();
            String jsonStrPrdctDesc = sh.makeServiceCall(urlProductDesc, ServiceHandler.GET);





            //Log.d("Response: ", "> " + jsonStrcat1);
            Log.d("Response Desc: ", "> " + jsonStrPrdctDesc);


            if (jsonStrPrdctDesc != null) {
                try {

                    jsonObjPrdctDesc = new JSONObject(jsonStrPrdctDesc);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the user");
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

            int loader = R.drawable.tomato;
            ImageLoader imgLoader = new ImageLoader(DescriptionActivity.this);
            imgLoader.DisplayImage("http://www.mive.in/" + jsonObjPrdctDesc.optString("coverphotourl"), loader, imgPrdct);
           // imgPrdct.getLayoutParams().height = imgPrdct.getLayoutParams().width;

            name.setText(jsonObjPrdctDesc.optString("name"));
            desc.setText(jsonObjPrdctDesc.optString("description"));
            tvGrade.setText(jsonObjPrdctDesc.optString("grade"));
            tvPrice.setText(jsonObjPrdctDesc.optString("pricePerUnit"));
            tvUnit.setText(jsonObjPrdctDesc.optString("unit"));
            origin.setText(jsonObjPrdctDesc.optString("origin"));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
