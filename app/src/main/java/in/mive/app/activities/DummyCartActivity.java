package in.mive.app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
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
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.mive.app.helperclasses.JSONParser;
import in.mive.app.helperclasses.ServiceHandler;
import in.mive.app.imageloader.ImageLoader;
import in.mive.app.imageupload.UploadActivity;
import in.mive.app.savedstates.ButtonDTO;
import in.mive.app.savedstates.CartItemListDTO;
import in.mive.app.savedstates.DummyCartItemListDTO;
import in.mive.app.savedstates.ItemListDTO;
import in.mive.app.savedstates.JSONDTO;
import in.mive.app.savedstates.UpdateDummyItemListDTO;
import in.mive.app.savedstates.UpdateItemListDTO;

/**
 * Created by shubham on 8/21/2015.
 */
public class DummyCartActivity extends Activity {
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
    private float qty=0;
    private  Button btnSubmitCart;
    private int totpayable;
    JSONObject eachItem;
    private int loadingprdctnmbr=0;
    int sizeofcartlist=0;
    private JSONParser jsonParser;
    private ProgressDialog pDialog;
    private AsyncTask<Void, Void, Void> updatetsk;

    boolean enabledBtn= true;
    private View viewCategoryCart;
    private JSONArray jsonArraySeecrt;
    private JSONArray jsonArrayItems;
    String userId;
    private float pricePerUnit = 0;
    private List<HashMap<String, String >> listofItems = new ArrayList<>();
    private float totalAmount;
    ImageView bckhome; Button buttonUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);
        JSONObject jsonProduct = JSONDTO.getInstance().getJsonProductscat1();
//        JSONArray jsonArrayProducts = jsonProduct.optJSONArray("results");
        jsonParser =new JSONParser();
        bckhome = (ImageView) findViewById(R.id.imgbckHome);
        bckhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        buttonUpdate = (Button) findViewById(R.id.btnupdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendUpdateDataAddToCart().execute();
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
        inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        itemlist = ItemListDTO.getInstance().getItemlist();

        layoutItemList = (LinearLayout) findViewById(R.id.linear_layout_cart);

        int realsize = Integer.parseInt(ButtonDTO.getInstance().getBtn().getText().toString());

        new GetseeCartData().execute();
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


        private String jsonStr;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            if(pDialog == null || !pDialog.isShowing())
            {
                pDialog = new ProgressDialog(DummyCartActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response



            try {
                userId= JSONDTO.getInstance().getJsonUser().getString("user_id").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String urlseecrt = "http://www.mive.in/api/seedummycart/?userId="+userId;

             jsonStr = sh.makeServiceCall(urlseecrt, ServiceHandler.GET);



            // Log.d("Response cartitems: ", "> " + jsonStr);




            return null;
        }
        List<Map> resultList;
        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            layoutItemList.removeAllViews();

            if (pDialog.isShowing())
                pDialog.cancel();
            if(jsonStr == null)

            {
                buildDialog(DummyCartActivity.this).show();
                return;
            }

            if (jsonStr != null) {
                try {
                    Log.e("json see crt", jsonStr.toString());
                    jsonArraySeecrt = new JSONArray(jsonStr);
                    Log.e("json dummy  see", jsonArraySeecrt.toString());


                    // JSONDTO.getInstance().setJsonProductscat1(jsonObj);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }

            for (int i= 0; i <jsonArraySeecrt.length(); i++)
            {
                JSONObject jsonObject = jsonArraySeecrt.optJSONObject(i);
                jsonArrayItems = jsonObject.optJSONArray("items");
                JSONObject sellerObj = jsonObject.optJSONObject("seller");
                final String nameOfSeller = sellerObj.optString("nameOfSeller");
                final String sellerId = sellerObj.optString("seller_id");


                //  new GetItemListData().execute();

                View  viewCategryOfItems = inflater.inflate(R.layout.category_cart_holder, layoutItemList, false);
                LinearLayout linearLayoutCategryOfItems = (LinearLayout) viewCategryOfItems.findViewById(R.id.prdct_holder);
                Button buttonPlaceOrdr = (Button)viewCategryOfItems.findViewById(R.id.btnPlcOrdr);
                TextView textViewStoreTitle = (TextView) viewCategryOfItems.findViewById(R.id.storetitle);
                TextView tvCatTotal = (TextView)viewCategryOfItems.findViewById(R.id.textViewSubtotalCat);
                final TextView tvUpdate = (TextView) viewCategryOfItems.findViewById(R.id.tvUpdate);
                textViewStoreTitle.setText(nameOfSeller);



                getItemsCart(linearLayoutCategryOfItems);


                tvCatTotal.setText("" + totpayable);
                final int totCost = totpayable;
                buttonPlaceOrdr.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(DummyCartActivity.this, UploadActivity.class);
                        intent.putExtra("userId", userId);
                        intent.putExtra("price", "" + totCost);
                        intent.putExtra("sellerId", sellerId);
                        intent.putExtra("sellerName", nameOfSeller);
                        intent.putExtra("invoiceOnly", "no");
                        intent.putExtra("dummycartId", JSONDTO.getInstance().getJsonUser().optJSONObject("dummycart").optString("dummycart_id"));
                        intent.putExtra("isDummy", true);

                        Log.e("Mive: ", "total " + totCost);

                        startActivity(intent);
                    }
                });

                tvUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvUpdate.setEnabled(false);
                        tvUpdate.setAlpha(0.5F);
                        new SendUpdateDataAddToCart().execute();
                    }
                });
                totpayable=0;

                layoutItemList.addView(viewCategryOfItems);
            }


        }

    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet connection.");
        builder.setMessage("You have no internet connection");

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                recreate();
            }
        });

        return builder;
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
                map.put("cartItemId", eachItem.optString("dummycartitem_id"));
                map.put("pricePerUnit", eachItem.optString("pricePerUnit"));

                l.add(map);



            } catch (JSONException e) {
                e.printStackTrace();
            }



        }

        DummyCartItemListDTO.getInstance().setItemlist(l);
        // new GetEachProductData().execute();
        getEachPrdct(layoutCategoryHolder);




    }


    void getEachPrdct(ViewGroup layoutCatholder)
    {
        Log.e("qty value in list", DummyCartItemListDTO.getInstance().getItemlist().get(loadingprdctnmbr).get("units").toString());
        qty = Float.parseFloat(DummyCartItemListDTO.getInstance().getItemlist().get(loadingprdctnmbr).get("units").toString());
        pricePerUnit = Float.parseFloat(DummyCartItemListDTO.getInstance().getItemlist().get(loadingprdctnmbr).get("pricePerUnit").toString());
        Log.e("qty in getprdct", "" + qty);



        try {
            jsonObjEachItems = new JSONObject( DummyCartItemListDTO.getInstance().getItemlist().get(loadingprdctnmbr).get("product").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }



        Log.e("size of cartlist "+sizeofcartlist,""+ loadingprdctnmbr);
        if (pDialog != null && pDialog.isShowing() )
        {
            pDialog.dismiss();

        }


        View cartItmView = inflater.inflate(R.layout.dumy_cards_cart,null);
        TextView tvProductName = (TextView) cartItmView.findViewById(R.id.tvProductName);

        ImageView productImage = (ImageView)cartItmView.findViewById(R.id.imgProductPhoto);
        final ImageView deleteItemview = (ImageView) cartItmView.findViewById(R.id.deleteItem);
        final EditText etPrice = (EditText)cartItmView.findViewById(R.id.etPricePerItemTotal);
        final EditText etSelectedQuantity =(EditText)cartItmView.findViewById(R.id.editTextQty);
        final EditText editTextAmount =(EditText)cartItmView.findViewById(R.id.editTextAmount);




        //PRODUCTID = itemlist.get(c).get("id").toString();
        etSelectedQuantity.setText(""+qty);
        etPrice.setText(""+pricePerUnit);
        editTextAmount.setText(""+ (qty*pricePerUnit));
        totpayable += (qty*pricePerUnit);



       // predefined ppu for each product
        final float ppu = jsonObjEachItems.optInt("pricePerUnit");






        if(loadingprdctnmbr >=sizeofcartlist)
            return;
        //...

        final String itemId = DummyCartItemListDTO.getInstance().getItemlist().get(loadingprdctnmbr).get("cartItemId").toString();

        HashMap<String , String> map = new HashMap<String, String>();
        map.put("productId", "" + itemId);
        map.put("pricePerUnit", "" + pricePerUnit);
        map.put("qty", "" + qty);

        listofItems.add(map);

        deleteItemview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               /* listofItems.get(loadingprdctnmbr).put("qty", ""+0);
                new SendUpdateDataAddToCart().execute();*/
                Log.e("list of items before" , listofItems.toString());


                for (int i =0; i< listofItems.size(); i++)
                {
                    String tmpitemid = listofItems.get(i).get("productId").toString();
                    if(itemId.equals(tmpitemid))
                    {
                        listofItems.get(i).put("qty",""+0);
                    }
                }
                Log.e("listofitems delete" , listofItems.toString());

                new SendUpdateDataAddToCart().execute();
            }
        });

        setTextListeners(etSelectedQuantity, etPrice, editTextAmount, itemId);

        //tvPrice.setText("Rs. "+totPricePeritem);




        tvProductName.setText(jsonObjEachItems.optString("name"));
        int loader = R.drawable.tomato;
        ImageLoader imgLoader = new ImageLoader(DummyCartActivity.this);
        imgLoader.DisplayImage("http://www.mive.in/" + jsonObjEachItems.optString("coverphotourl"), loader, productImage);








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
    private void setTextListeners(final EditText editTextQty, final EditText editTextPPU,final EditText editTextAmount, final String productId)
    {



        editTextQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (editTextPPU.getText() != null && editTextPPU.getText().length() > 0)
                    {
                        float ppu = Float.parseFloat(editTextPPU.getText().toString());

                        if(editTextAmount.getText() != null && editTextAmount.getText().length() > 0)
                        {
                            float oldamt = Float.parseFloat(editTextAmount.getText().toString());
                            totalAmount = totalAmount - oldamt;
                        }
                        if(editTextQty.getText().length() > 0)
                            editTextAmount.setText("" + (ppu * Float.parseFloat(editTextQty.getText().toString())));

                        Log.e("Mive: ", "EdittextAmount " + editTextAmount.getText().toString());
                        Log.e("Mive: ", "Var Total Amount "+totalAmount);


  /*                      totalAmount = totalAmount + Integer.parseInt(editTextAmount.getText().toString());
                        tvtotal.setText(""+totalAmount);
*/
                        HashMap<String , String> map = new HashMap<String, String>();
                        map.put("productId", "" + productId);
                        map.put("pricePerUnit", "" + ppu);
                        map.put("qty", editTextQty.getText().toString());
                        removePrevious(listofItems, productId);
                        listofItems.add(map);
                        Log.e("List of items", listofItems.toString());


                    }
                    else if (editTextAmount.getText() != null && editTextAmount.getText().length() > 0)
                    {
                        float amount = Float.parseFloat(editTextAmount.getText().toString());
                        editTextPPU.setText("" + (amount / Float.parseFloat(editTextQty.getText().toString())));

                        HashMap<String , String> map = new HashMap<String, String>();
                        map.put("productId", "" + productId);
                        map.put("pricePerUnit", "" + editTextPPU.getText().toString());
                        map.put("qty", editTextQty.getText().toString());
                        removePrevious(listofItems, productId);
                        listofItems.add(map);
                        Log.e("List of items", listofItems.toString());

                    }
                }
            }
        });

        editTextPPU.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if ( !b) {
                    if (editTextQty.getText() != null && editTextQty.getText().length() > 0)
                    {
                        float qty = Float.parseFloat(editTextQty.getText().toString());

                        if(editTextAmount.getText() != null && editTextAmount.getText().length() > 0)
                        {
                            float oldamt = Float.parseFloat(editTextAmount.getText().toString());
                            totalAmount = totalAmount - oldamt;
                        }
                        if(editTextPPU.getText().length() > 0)
                            editTextAmount.setText("" + (qty * Float.parseFloat(editTextPPU.getText().toString())));

                        Log.e("Mive: ", "EdittextAmount " + editTextAmount.getText().toString());
                        Log.e("Mive: ", "Var Total Amount " + totalAmount);


                        /*totalAmount = totalAmount + Float.parseFloat(editTextAmount.getText().toString());
                        tvtotal.setText("" + totalAmount);
*/
                        HashMap<String , String> map = new HashMap<String, String>();
                        map.put("productId", "" + productId);
                        map.put("pricePerUnit", "" + editTextPPU.getText().toString());
                        map.put("qty", editTextQty.getText().toString());
                        removePrevious(listofItems, productId);
                        listofItems.add(map);
                        Log.e("List of items", listofItems.toString());


                    }
                    else if (editTextAmount.getText() != null && editTextAmount.getText().length() > 0)
                    {
                        float amount = Float.parseFloat(editTextAmount.getText().toString());
                        editTextQty.setText("" + (amount / Float.parseFloat(editTextPPU.getText().toString())));

                        HashMap<String , String> map = new HashMap<String, String>();
                        map.put("productId", "" + productId);
                        map.put("pricePerUnit", "" + editTextPPU.getText().toString());
                        map.put("qty", editTextQty.getText().toString());
                        removePrevious(listofItems, productId);
                        listofItems.add(map);
                        Log.e("List of items", listofItems.toString());
                    }
                }
            }
        });

        editTextAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b)
                {
                    if(editTextAmount.getText() != null && editTextAmount.getText().length() > 0)
                    {

                        float oldamt = Float.parseFloat(editTextAmount.getText().toString());
                        Log.e("Mive: ", "Var Old Amount "+oldamt);

                        totalAmount = totalAmount - oldamt;
                    }
                }
                else if ( !b) {



                    if (editTextQty.getText() != null && editTextQty.getText().length() > 0 )
                    {
                        if(editTextAmount.getText().length()>0) {
                            float qty = Float.parseFloat(editTextQty.getText().toString());

                            if(qty != 0)
                            {
                                editTextPPU.setText("" + (Float.parseFloat(editTextAmount.getText().toString()) / qty));
                                HashMap<String , String> map = new HashMap<String, String>();
                                map.put("productId", "" + productId);
                                map.put("pricePerUnit", "" + editTextPPU.getText().toString());
                                map.put("qty", editTextQty.getText().toString());
                                removePrevious(listofItems, productId);
                                listofItems.add(map);
                                Log.e("List of items", listofItems.toString());
                            }
                        }
                    }
                    else if (editTextPPU.getText() != null && editTextPPU.getText().length() > 0)
                    {
                        if(editTextAmount.getText().length()>0)
                        {
                            float ppu = Float.parseFloat(editTextPPU.getText().toString());
                            if(ppu != 0)
                            {
                                editTextQty.setText("" + (Float.parseFloat(editTextAmount.getText().toString()) / ppu));
                                HashMap<String , String> map = new HashMap<String, String>();
                                map.put("productId", "" + productId);
                                map.put("pricePerUnit", "" + editTextPPU.getText().toString());
                                map.put("qty", editTextQty.getText().toString());
                                removePrevious(listofItems, productId);
                                listofItems.add(map);
                                Log.e("List of items", listofItems.toString());
                            }
                        }
                    }





                  /*  Log.e("Mive: ", "Var Total Amount "+totalAmount);
                    Log.e("Mive: ", "EdittextAmount " + editTextAmount.getText().toString());


                    totalAmount = totalAmount + Float.parseFloat(editTextAmount.getText().toString());
                    tvtotal.setText(""+totalAmount);*/


                }

            }
        });

    }

    private void removePrevious(List<HashMap<String, String>> listofItems, String productId)
    {
      for (int i = 0; i< listofItems.size(); i++)
      {
          String tmpprdctid = listofItems.get(i).get("productId").toString();
          if(productId.equals(tmpprdctid))
          {
              listofItems.remove(i);
              return;
          }
      }
    }


    // update the cart in background
    private class SendUpdateDataAddToCart extends AsyncTask<Void, Void, Void>
    {
        private String urlupdatecart =  "http://www.mive.in/api/updatedummycart/";  //"http://postcatcher.in/catchers/55e75fce1ab1c60300001dee";

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
            pDialog = new ProgressDialog(DummyCartActivity.this);
            pDialog.setMessage("Updating...");
            pDialog.setCancelable(false);
            pDialog.show();


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            HttpConnectionParams.setConnectionTimeout(myParams, 10000);
            Log.e("inside", "service handler update async");
            // Making a request to urlupdatecart and getting response
            // get he list fo items here and put it in json
            itemsList = UpdateItemListDTO.getInstance().getItemlist();
            Log.e("list update", itemsList.toString());


            // Building Parameters
            JSONObject params = null;
            params = new JSONObject();
            try {
                SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
                int restoreduserid = prefs.getInt("userId", 0);
                Log.e("retrieved id", "" + restoreduserid);


                params.put("dummycartId", JSONDTO.getInstance().getJsonUser().optJSONObject("dummycart").optString("dummycart_id"));
                params.put("userId", "" + restoreduserid);
                JSONObject jsonItems = new JSONObject();
                for (int i = 0; i < listofItems.size(); i++)
                {
                    HashMap<String,String> hashMap = new HashMap();
                    hashMap.put("qty",listofItems.get(i).get("qty").toString());
                    hashMap.put("itemId",listofItems.get(i).get("productId").toString());
                    hashMap.put("itemId",listofItems.get(i).get("productId").toString());
                    hashMap.put("pricePerUnit", listofItems.get(i).get("pricePerUnit").toString());


                    JSONObject objOneItem = new JSONObject(hashMap.toString());
                    jsonItems.put(""+(i+1),objOneItem);

                }

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



            if(jsonObjectresult != null)
                Log.e("result", jsonObjectresult.toString());
            UpdateDummyItemListDTO.getInstance().setItemlist(new ArrayList<HashMap>());

            enabledBtn=true;
            listofItems = new ArrayList<>();
            new GetseeCartData().execute();



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
        List<HashMap> listUpdate = UpdateDummyItemListDTO.getInstance().getItemlist();
        for (int i = 0; i<l.size(); i++)
        {
            HashMap map = new HashMap<String , String>();
            map.put("qty",l.get(i).get("units"));

            if(l.get(i).get("itemId") != null)
                map.put("itemId",l.get(i).get("itemId"));

            listUpdate.add(map);
        }
        UpdateDummyItemListDTO.getInstance().setItemlist(listUpdate);
        if(updatetsk != null)
        {  updatetsk.cancel(true);
            // UpdateDummyItemListDTO.getInstance().setProductMap(new ArrayList<HashMap>());
        }
        updatetsk = new SendUpdateDataAddToCart();
        updatetsk.execute();
    }

}
