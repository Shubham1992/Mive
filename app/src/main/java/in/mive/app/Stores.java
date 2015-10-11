package in.mive.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.mive.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.mive.app.activities.CartActivity;
import in.mive.app.activities.MainActivity;
import in.mive.app.adapter.TabsPagerAdapter;
import in.mive.app.savedstates.ButtonDTO;
import in.mive.app.savedstates.CartItemListDTO;
import in.mive.app.savedstates.ItemListDTO;
import in.mive.app.savedstates.JSONDTO;

/**
 * Created by Shubham on 10/7/2015.
 */
public class Stores extends Activity {

LinearLayout layoutStore1;
Button btnCrt;
int id;
    private JSONObject jsonObjuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stores);


        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        layoutStore1 = (LinearLayout) findViewById(R.id.store1);
        layoutStore1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Stores.this , MainActivity.class);
                intent.putExtra("id",id);
                startActivity(intent);

            }
        });

        btnCrt = (Button) findViewById(R.id.btnCrt);
        btnCrt.setEnabled(false);
        btnCrt.setAlpha(.6F);

        ButtonDTO.getInstance().setBtn(btnCrt);
        btnCrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ItemListDTO.getInstance().setItemlist(new ArrayList<Map>());
                Intent intent = new Intent(Stores.this,CartActivity.class);
                startActivity(intent);

            }
        });
        new GetData().execute();
    }
    private class GetCartData extends AsyncTask<Void, Void, Void>
    {


        private String urlCart;


        // private ProgressDialog pDialog;
        private JSONObject jsonObjcart;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            /*pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Products...");
            pDialog.setCancelable(false);
            pDialog.show();*/

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
            // Dismiss the progress dialog
           /* if (pDialog.isShowing())
                pDialog.dismiss();
*/
            // class to return the json attributes in form of hashmap.
            // The map is set in DTO from where it can be accessed at all the fragments

            //btncart.setVisibility(View.VISIBLE);

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


                            List<HashMap> l = new ArrayList<>(); /*= CartItemListDTO.getInstance().getItemlist();*/
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

    private class GetData extends AsyncTask<Void, Void, Void>
    {

        private String urlUser="http://www.mive.in/api/user/";
        //private String urlCart = "http://www.mive.in/api/cart/cartitems/"+id+"/?format=json";

        private JSONObject jsonObjcat1;

        private JSONObject jsonObjcart;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
           /* pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Getting Products...");
            pDialog.setCancelable(false);
            pDialog.show();

*/        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            Log.e("inside", "service handler");
            // Making a request to urlcat1 and getting response

            //String jsonStrcat1 = sh.makeServiceCall(urlcat1, ServiceHandler.GET);

            sh = new ServiceHandler();
            String jsonStrUser = sh.makeServiceCall(urlUser + id, ServiceHandler.GET);


          /*  sh = new ServiceHandler();
            String jsonStrCart = sh.makeServiceCall(urlCart, ServiceHandler.GET);
*/



            //Log.d("Response: ", "> " + jsonStrcat1);
            Log.d("Response User: ", "> " + jsonStrUser);
            //          Log.d("Response cart: ", "> " + jsonStrCart);

//........
            /*if (jsonStrcat1 != null) {
				try {

					jsonObjcat1 = new JSONObject(jsonStrcat1);
                    JSONDTO.getInstance().setJsonProductscat1(jsonObjcat1);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the urlcat1");
			}*/
//...........

            if (jsonStrUser != null) {
                try {

                    jsonObjuser = new JSONObject(jsonStrUser);
                    JSONDTO.getInstance().setJsonUser(jsonObjuser);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the user");
            }
    /*        if (jsonStrCart != null) {
                try {

                    jsonObjcart = new JSONObject(jsonStrCart);
                    Log.e("cart ", jsonObjcart.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the urlcat1");
            }
*/

            return null;
        }
        List<Map> resultListcat1;
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            // class to return the json attributes in form of hashmap.
            // The map is set in DTO from where it can be accessed at all the fragments

			/*GetProductsComponentFromJson getProductsComponentFromJson =new GetProductsComponentFromJson();
            resultListcat1 = getProductsComponentFromJson.getComponent(MainActivity.this, jsonObjcat1);
			SavedAllCat.getobj().setList(resultListcat1);
*/


 new GetCartData().execute();

  /*          if(jsonObjcart.optString("count") != null) {
                Log.e("count", jsonObjcart.optString("count"));
                btncart.setText(jsonObjcart.optString("count"));
            }
  */          }

    }

}
