package in.mive.app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import in.mive.app.helperclasses.ServiceHandler;
import in.mive.app.imageloader.ImageLoader;
import in.mive.app.savedstates.ButtonDTO;
import in.mive.app.savedstates.CartItemListDTO;
import in.mive.app.savedstates.ItemListDTO;
import in.mive.app.savedstates.JSONDTO;
import in.mive.app.savedstates.UpdateItemListDTO;

/**
 * Created by shubham on 8/21/2015.
 */
public class CartActivity extends Activity {
    String PRODUCTID;
    private String productId;

    LayoutInflater inflater;
    List<Map> itemlist;
    LinearLayout layoutItemList;
    private int c=0;
    private int realS;
    private JSONObject jsonObjAllItems;
    private JSONObject jsonObjEachItems;
    private String urlprdct = null;
    private int qty=0;
    private  Button btnSubmitCart;
    private int totpayable;
    JSONObject eachItem;
    private int loadingprdctnmbr=0;
    int sizeofcartlist=0;
    private JSONParser jsonParser;
    private ProgressDialog pDialog;
    private AsyncTask<Void, Void, Void> updatetsk;
    private TextView  tvtotal;
boolean enabledBtn= true;
    private View viewCategoryCart;
    private JSONArray jsonArraySeecrt;
    private JSONArray jsonArrayItems;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);
        JSONObject jsonProduct = JSONDTO.getInstance().getJsonProductscat1();
//        JSONArray jsonArrayProducts = jsonProduct.optJSONArray("results");
        jsonParser =new JSONParser();
        btnSubmitCart = (Button) findViewById(R.id.btnSubmitCart);
        tvtotal = (TextView) findViewById(R.id.tvtotal);

        btnSubmitCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                intent.putExtra("price", totpayable);
                startActivity(intent);
                finish();

            }
        });
        ActionBar  actionBar = getActionBar();
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
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        itemlist = ItemListDTO.getInstance().getItemlist();

        layoutItemList = (LinearLayout) findViewById(R.id.linear_layout_cart);

        int realsize = Integer.parseInt(ButtonDTO.getInstance().getBtn().getText().toString());

        if(realsize <= 0)
        {
            View noItemView = inflater.inflate(R.layout.no_item_in_cart, null);
            Button btnadd = (Button) noItemView.findViewById(R.id.btnAdd);
            btnadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   finish();
                   /* Intent intent = new Intent(CartActivity.this, MainActivity.class);
                   // intent.putExtra("id",intUser);
                    startActivity(intent);*/


                }
            });
            layoutItemList.addView(noItemView);


        }
        else {
        	// btnSubmitCart.setVisibility(View.VISIBLE);
           // new GetItemListData().execute();
			new GetseeCartData().execute();

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

	//get see cart
	private class GetseeCartData extends AsyncTask<Void, Void, Void>
	{



        @Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(CartActivity.this);
			pDialog.setMessage("Creating your Cart...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();
			// Making a request to url and getting response

			String restoredcartid = JSONDTO.getInstance().getJsonUser().optJSONObject("cart").optString("cart_id");

			// Log.e("retrieved id", "" + restoredcartid);

            try {
                userId= JSONDTO.getInstance().getJsonUser().getString("user_id").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String urlseecrt = "http://www.mive.in/api/seecart/?userId="+userId;

			String jsonStr = sh.makeServiceCall(urlseecrt, ServiceHandler.GET);



			// Log.d("Response cartitems: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {
					Log.e("json see crt", jsonStr.toString());
                     jsonArraySeecrt = new JSONArray(jsonStr);
                    Log.e("json array see", jsonArraySeecrt.toString());


					// JSONDTO.getInstance().setJsonProductscat1(jsonObj);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}


			return null;
		}
		List<Map> resultList;
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.cancel();

        for (int i= 0; i <jsonArraySeecrt.length(); i++)
        {
            JSONObject jsonObject = jsonArraySeecrt.optJSONObject(i);
             jsonArrayItems = jsonObject.optJSONArray("items");
            JSONObject sellerObj = jsonObject.optJSONObject("seller");
            String nameOfSeller = sellerObj.optString("nameOfSeller");
            final String sellerId = sellerObj.optString("seller_id");

          //  new GetItemListData().execute();

            View  viewCategryOfItems = inflater.inflate(R.layout.category_cart_holder, layoutItemList, false);
            LinearLayout linearLayoutCategryOfItems = (LinearLayout) viewCategryOfItems.findViewById(R.id.prdct_holder);
            Button buttonPlaceOrdr = (Button)viewCategryOfItems.findViewById(R.id.btnPlcOrdr);
            TextView textViewStoreTitle = (TextView) viewCategryOfItems.findViewById(R.id.storetitle);
            textViewStoreTitle.setText(nameOfSeller);

            buttonPlaceOrdr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                    intent.putExtra("price", totpayable);
                    intent.putExtra("sellerId", sellerId);
                    startActivity(intent);
                }
            });
            getItemsCart(linearLayoutCategryOfItems);

            layoutItemList.addView(viewCategryOfItems);
        }


		}

	}




    void getItemsCart(ViewGroup layoutCategoryHolder)
    {
        if (pDialog.isShowing())
        pDialog.cancel();

        Log.e("arritems", jsonArrayItems.toString());
        JSONArray arritems =jsonArrayItems; // jsonObjAllItems.optJSONArray("results");
        sizeofcartlist = arritems.length();
        List<HashMap> l = new ArrayList<>();/* = CartItemListDTO.getInstance().getProductMap();*/
        Log.e("list cart item", l.toString());
        for (int i=0 ; i< arritems.length(); i++)
        {

            try {

                eachItem = arritems.getJSONObject(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("product", eachItem.optJSONObject("product"));
                Log.e("product in cart", eachItem.optJSONObject("product").toString());
                map.put("units", eachItem.optString("qtyInUnits"));
                map.put("cartItemId", eachItem.optString("cartitem_id"));

                l.add(map);



            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        CartItemListDTO.getInstance().setItemlist(l);
       // new GetEachProductData().execute();
        getEachPrdct(layoutCategoryHolder);




    }


    void getEachPrdct(ViewGroup layoutCatholder)
    {
        Log.e("qty value in list", CartItemListDTO.getInstance().getItemlist().get(loadingprdctnmbr).get("units").toString());
        qty = Integer.parseInt(CartItemListDTO.getInstance().getItemlist().get(loadingprdctnmbr).get("units").toString());
        Log.e("qty in getprdct", "" + qty);



        try {
            jsonObjEachItems = new JSONObject( CartItemListDTO.getInstance().getItemlist().get(loadingprdctnmbr).get("product").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }



        Log.e("size of cartlist "+sizeofcartlist,""+ loadingprdctnmbr);
        if (pDialog != null && pDialog.isShowing() )
        {
            pDialog.dismiss();

        }


        View cartItmView = inflater.inflate(R.layout.cards_cart,null);
        TextView tvProductName = (TextView) cartItmView.findViewById(R.id.tvProductName);
        final TextView tvQuantitySelected = (TextView) cartItmView.findViewById(R.id.tvQuantitySelected);
        ImageView productImage = (ImageView)cartItmView.findViewById(R.id.imgProductPhoto);
        final TextView btnPlusQuantity = (TextView)cartItmView.findViewById(R.id.btnPlusQuantity);
        final TextView btnMinusQuantity = (TextView)cartItmView.findViewById(R.id.btnMinusQuantity);
        final TextView tvPrice = (TextView)cartItmView.findViewById(R.id.tvPricePerUnit);
        final TextView tvSelectedQuantity =(TextView)cartItmView.findViewById(R.id.tvAvailableQuantity);
        final TextView tvUnitType =(TextView)cartItmView.findViewById(R.id.tvUnitType);


        //PRODUCTID = itemlist.get(c).get("id").toString();
        tvQuantitySelected.setText(""+qty);
        final int ppu = jsonObjEachItems.optInt("pricePerUnit");
        int totPricePeritem = ppu * qty;

//            final String itemId = jsonObjEachItems.optString("product_id").toString();

        //...
        Log.e("loading nm", ""+loadingprdctnmbr);
        //..
        if(loadingprdctnmbr >=sizeofcartlist)
            return;
        //...

        final String itemId = CartItemListDTO.getInstance().getItemlist().get(loadingprdctnmbr).get("cartItemId").toString();
        Log.e("cart item id", itemId);
        //
        final List<HashMap> l = CartItemListDTO.getInstance().getItemlist();
        //l.get(loadingprdctnmbr).put("itemId",jsonObjEachItems.optInt("product_id"));
        l.get(loadingprdctnmbr).put("itemId",itemId);

        tvPrice.setText("Rs. "+totPricePeritem);
        totpayable +=totPricePeritem;

        tvtotal.setText("Rs. "+totpayable);

        tvProductName.setText(jsonObjEachItems.optString("name"));
        int loader = R.drawable.tomato;
        ImageLoader imgLoader = new ImageLoader(CartActivity.this);
        imgLoader.DisplayImage("http://www.mive.in/" + jsonObjEachItems.optString("coverphotourl"), loader, productImage);


        tvP = btnPlusQuantity;
        tvM =btnMinusQuantity;

        btnPlusQuantity.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            { if(!enabledBtn)
            {
                //btnPlusQuantity.setAlpha(0.5F);


                return;
            }

                enabledBtn = false;
                //btnPlusQuantity.setAlpha(1);

                boolean flagSame = false;
                int qnt = Integer.parseInt(tvQuantitySelected.getText().toString());
                qnt++;

                tvQuantitySelected.setText("" + qnt);
                // check if itemList has a map that alredy has a value of "id" same as current id.
                // If yes then just add the units else create new map and add in list
                //Log.e("real size", ""+realS);

                updateCart(itemId,""+qnt);
                int totPricePeritem = ppu * qnt;
                tvPrice.setText("Rs. "+totPricePeritem);
                totpayable +=ppu;

                if(totpayable > 0 )
                {
                    btnSubmitCart.setEnabled(true);
                    btnSubmitCart.setAlpha(1);
                }

                tvtotal.setText("Rs. " + totpayable);



            }
        });
        btnMinusQuantity.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                if(!enabledBtn)
                {
                    //    btnMinusQuantity.setAlpha(0.5F);
                    return;
                }

                enabledBtn = false;
                //  btnMinusQuantity.setAlpha(1);


                boolean flagRemove = true;
                int qnt = Integer.parseInt(tvQuantitySelected.getText().toString());

                if (qnt >= 1)
                { qnt--;

                    tvQuantitySelected.setText("" + qnt);

                    updateCart(itemId,""+qnt);
                    int totPricePeritem = ppu * qnt;
                    tvPrice.setText("Rs. "+totPricePeritem);
                    totpayable -=ppu;

                    if(totpayable <= 0 )
                    {
                        btnSubmitCart.setEnabled(false);
                        btnSubmitCart.setAlpha(0.6F);
                    }
                    tvtotal.setText("Rs. "+totpayable);
                }
                else if(qnt == 0)
                {
                    // recreate();
                }


            }


        });



        // Log.e("id in cart",productId);
        // LinearLayout linearLayoutPrdctholder = (LinearLayout) viewCategoryCart.findViewById(R.id.prdct_holder);

        layoutCatholder.addView(cartItmView);
        loadingprdctnmbr++;
        Log.e("loading nm", ""+loadingprdctnmbr);
        if(loadingprdctnmbr < sizeofcartlist)
        {

           getEachPrdct(layoutCatholder);
        }
        else
        {
           loadingprdctnmbr = 0;
            return; // CartItemListDTO.getInstance().setProductMap(new ArrayList<HashMap>());
        }
    }
	//see item api

    TextView tvP , tvM;
    // Async class to get data from server

    // update the cart in background
    private class SendUpdateDataAddToCart extends AsyncTask<Void, Void, Void>
    {
        private String urlupdatecart =  "http://www.mive.in/api/updatecart/";  //"http://postcatcher.in/catchers/55e75fce1ab1c60300001dee";

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
            pDialog = new ProgressDialog(CartActivity.this);
            pDialog.setMessage("Updating...");
            pDialog.setCancelable(false);


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            HttpConnectionParams.setConnectionTimeout(myParams, 10000);
            Log.e("inside", "service handler update async");
            // Making a request to urlupdatecart and getting response
            // get he list fo items here and put it in json
            itemsList = UpdateItemListDTO.getInstance().getItemlist();
            Log.e("list update",itemsList.toString());


            // Building Parameters
            JSONObject params= null;
            params = new JSONObject();
            try {
                SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
                int restoreduserid = prefs.getInt("userId",0);
                Log.e("retrieved id", "" + restoreduserid);


                params.put("cartId",JSONDTO.getInstance().getJsonUser().optJSONObject("cart").optString("cart_id"));
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
            UpdateItemListDTO.getInstance().setItemlist(new ArrayList<HashMap>());

            enabledBtn=true;



          /*  btnPlusQuantity.setEnabled(true);
            btnMinusQuantity.setEnabled(true);*/
        }

    }


    private int getItemListSize()
    {
        for (int i=0; i<itemlist.size(); i++)
        {
            if(Integer.parseInt(itemlist.get(i).get("units").toString())!=0)
            {   realS++;

            }

        }
        return realS;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();

    }

    //update the cart item list whenever user changes the quantity
    void updateCart(String itemId, String updatedQty)
    {   Log.e("item id "+itemId, updatedQty);
        List<HashMap> l = CartItemListDTO.getInstance().getItemlist();
        for (int i=0; i<l.size();i++)
        {
            String id = l.get(i).get("itemId").toString();
            if(id.trim().equalsIgnoreCase(itemId.trim())) {Log.e("inside check", "true");
                 l.get(i).put("units",updatedQty);
                    break;
                }

        }
        CartItemListDTO.getInstance().setItemlist(l);
        prepareUpdate(l);
    }

    void prepareUpdate(List<HashMap> l)
    {
        List<HashMap> listUpdate = UpdateItemListDTO.getInstance().getItemlist();
        for (int i = 0; i<l.size(); i++)
        {
            HashMap map = new HashMap<String , String>();
            map.put("qty",l.get(i).get("units"));

            if(l.get(i).get("itemId") != null)
                map.put("itemId",l.get(i).get("itemId"));

            listUpdate.add(map);
        }
    UpdateItemListDTO.getInstance().setItemlist(listUpdate);
         if(updatetsk != null)
         {  updatetsk.cancel(true);
            // UpdateItemListDTO.getInstance().setProductMap(new ArrayList<HashMap>());
         }
          updatetsk = new SendUpdateDataAddToCart();
        updatetsk.execute();
    }

}
