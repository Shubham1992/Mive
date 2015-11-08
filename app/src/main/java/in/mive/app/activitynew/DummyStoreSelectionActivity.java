package in.mive.app.activitynew;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mive.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import in.mive.app.activities.CartActivity;
import in.mive.app.helperclasses.ServiceHandler;
import in.mive.app.layouthelper.InflateDummyStores;
import in.mive.app.layouthelper.InflateStores;
import in.mive.app.savedstates.ButtonDTO;
import in.mive.app.savedstates.CartItemListDTO;
import in.mive.app.savedstates.JSONDTO;

/**
 * Created by Shubham on 10/15/2015.
 */
public class DummyStoreSelectionActivity extends Activity {
    RecyclerView rvStores;
    private int userId;
    Button btnCrt;
    private JSONObject jsonObjuser;
    ViewGroup layoutContainerstore;
    ImageView imgHomeBck;
    private AlertDialog progressDialog;
    TextView tvTitleAppbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_dynamic);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        imgHomeBck = (ImageView) findViewById(R.id.imgbckHome);
        imgHomeBck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        layoutContainerstore = (ViewGroup) findViewById(R.id.cntainerStore);
        tvTitleAppbar = (TextView) findViewById(R.id.tvHeadingAppbar);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        tvTitleAppbar.setTypeface(custom_font);
        btnCrt = (Button) findViewById(R.id.btnCrt);
        btnCrt.setEnabled(false);
        btnCrt.setAlpha(.8F);
        btnCrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ItemListDTO.getInstance().setProductMap(new ArrayList<Map>());
                ButtonDTO.getInstance().setBtn(btnCrt);
                Intent intent1 = new Intent(DummyStoreSelectionActivity.this, CartActivity.class);
                startActivity(intent1);
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
        new GetData().execute();
    }

    private class GetData extends AsyncTask<Void, Void, Void>
    {

        private String urlUser="http://www.mive.in/api/user/";
        //private String urlCart = "http://www.mive.in/api/cart/cartitems/"+id+"/?format=json";



        @Override
        protected void onPreExecute() {
            progressDialog = new SpotsDialog(DummyStoreSelectionActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
               }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            Log.e("inside", "service handler");


            sh = new ServiceHandler();
            String jsonStrUser = sh.makeServiceCall(urlUser + userId, ServiceHandler.GET);





            //Log.d("Response: ", "> " + jsonStrcat1);
            Log.d("Response User: ", "> " + jsonStrUser);
            //          Log.d("Response cart: ", "> " + jsonStrCart);


            if (jsonStrUser != null) {
                try {

                    jsonObjuser = new JSONObject(jsonStrUser);


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

            if(progressDialog.isShowing())
                progressDialog.cancel();
            JSONDTO.getInstance().setJsonUser(jsonObjuser);

           // new GetCartData().execute();



            InflateDummyStores inflatedummyStores = new InflateDummyStores();
            inflatedummyStores.inflateStoreTabs(DummyStoreSelectionActivity.this,layoutContainerstore, jsonObjuser);

        }

    }
    private class GetCartData extends AsyncTask<Void, Void, Void>
    {


        private String urlCart;


        // private ProgressDialog pDialog;
        private JSONObject jsonObjcart;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            Log.e("inside", "service handler");
            // Making a request to urlcat1 and getting response
            String cid = JSONDTO.getInstance().getJsonUser().optJSONObject("cart").optString("cart_id");
            Log.e("id  of cart", cid);
            urlCart = "http://www.mive.in/api/cart/cartitems/"+cid+"/?format=json";


            String jsonStrCart = sh.makeServiceCall(urlCart, ServiceHandler.GET);



            Log.d("Response cart: ", "> " + jsonStrCart);


            if (jsonStrCart != null) {
                try {

                    jsonObjcart = new JSONObject(jsonStrCart);
                    Log.e("cart ", jsonObjcart.toString());
                    JSONDTO.getInstance().setJsonCart(jsonObjcart);

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


            if(jsonObjcart.optString("count") != null) {
                Log.e("count", jsonObjcart.optString("count"));
                btnCrt.setText(jsonObjcart.optString("count"));
                btnCrt.setEnabled(true);
                btnCrt.setAlpha(1);
                //.............
                JSONArray arritems = jsonObjcart.optJSONArray("results");
                //sizeofcartlist = arritems.length();

                JSONObject eachItem;

                //.........
                if(arritems != null)
                    for (int i=0 ; i< arritems.length(); i++)
                    {
                        eachItem = null;
                        try {

                            eachItem = arritems.getJSONObject(i);
                            HashMap<String, Object> map = new HashMap<String, Object>();

                            map.put("product", eachItem.optJSONObject("product"));
                            map.put("productId", eachItem.optJSONObject("product").optString("product_id"));

                            map.put("units", eachItem.optString("qtyInUnits"));
                            map.put("cartItemId", eachItem.optString("cartitem_id"));
                            //.........


                            List<HashMap> l = new ArrayList<>(); /*= CartItemListDTO.getInstance().getProductMap();*/
                            l.add(map);
                            CartItemListDTO.getInstance().setItemlist(l);

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }

                //............



            }
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new GetCartData().execute();
    }
}
