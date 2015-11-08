package in.mive.app.layouthelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mive.R;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.mive.app.activities.CartActivity;
import in.mive.app.activities.DummyCartActivity;
import in.mive.app.helperclasses.JSONParser;
import in.mive.app.imageloader.ImageLoader;
import in.mive.app.savedstates.ItemListDTO;
import in.mive.app.savedstates.JSONDTO;

/**
 * Created by Shubham on 11/7/2015.
 */
public class RecyclerViewProductsInflator {
    LayoutInflater inflater;
    Context context;
    private List<Map> itemsList;
    private JSONParser jsonParser;
    HashMap<String, String> productIdMap = new HashMap<>();
    HashMap<String, String> qtyMap = new HashMap<>();
    HashMap<String, String> ppuMap = new HashMap<>();
    float totalAmount= 0.0F;
    TextView tvtotal;


    public  void inflateProducts(List<Map> products, final Context context, ViewGroup layout ,TextView tvTotal)
    {
        this.context = context;
        this.tvtotal =tvTotal;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        for (int i = 0; i < products.size(); i++)
        {
            View itemView =    inflater.inflate(R.layout.dumy_cards2, layout , false);
            TextView tvproductName= (TextView) itemView.findViewById(R.id.tvProductName);
             tvproductName.setText(products.get(i).get("productName").toString());
            ImageView imgProductImage = (ImageView) itemView.findViewById(R.id.imgProductPhoto);
            int loader = R.drawable.tomato;
            ImageLoader imgLoader = new ImageLoader(context);
            imgLoader.DisplayImage("http://www.mive.in/" + products.get(i).get("productImage").toString(), loader, imgProductImage);

            EditText editTextQty = (EditText) itemView.findViewById(R.id.editTextQty);
            EditText editTextPPU = (EditText) itemView.findViewById(R.id.etPricePerItemTotal);
            EditText editTextAmount = (EditText) itemView.findViewById(R.id.editTextAmount);

            String productId =  products.get(i).get("productId").toString();


            setTextListeners(editTextQty, editTextPPU, editTextAmount, productId);


            layout.addView(itemView);
        }
    }

    private void setTextListeners(final EditText editTextQty, final EditText editTextPPU,final EditText editTextAmount, final String productId)
    {



        editTextQty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (editTextPPU.getText() != null && editTextPPU.getText().length() > 0) {
                        int ppu = Integer.parseInt(editTextPPU.getText().toString());

                        if(editTextAmount.getText() != null && editTextAmount.getText().length() > 0)
                        {
                           int oldamt = Integer.parseInt(editTextAmount.getText().toString());
                            totalAmount = totalAmount - oldamt;
                        }
                        if(editTextQty.getText().length() > 0)
                        editTextAmount.setText("" + (ppu * Integer.parseInt(editTextQty.getText().toString())));

                        Log.e("Mive: ", "EdittextAmount " + editTextAmount.getText().toString());
                        Log.e("Mive: ", "Var Total Amount "+totalAmount);


                        totalAmount = totalAmount + Integer.parseInt(editTextAmount.getText().toString());
                        tvtotal.setText(""+totalAmount);

                        productIdMap.put("productId", "" + productId);
                        ppuMap.put("pricePerUnit", "" + ppu);
                        qtyMap.put("qty", editTextQty.getText().toString());



                    } else if (editTextAmount.getText() != null && editTextAmount.getText().length() > 0) {
                        int amount = Integer.parseInt(editTextAmount.getText().toString());
                        editTextPPU.setText("" + (amount / Integer.parseInt(editTextQty.getText().toString())));
                    }
                }
            }
        });

        editTextPPU.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if ( !b) {
                    if (editTextQty.getText() != null && editTextQty.getText().length() > 0) {
                        int qty = Integer.parseInt(editTextQty.getText().toString());

                        if(editTextAmount.getText() != null && editTextAmount.getText().length() > 0)
                        {
                            int oldamt = Integer.parseInt(editTextAmount.getText().toString());
                            totalAmount = totalAmount - oldamt;
                        }
                        if(editTextPPU.getText().length() > 0)
                        editTextAmount.setText("" + (qty * Integer.parseInt(editTextPPU.getText().toString())));

                        Log.e("Mive: ", "EdittextAmount " + editTextAmount.getText().toString());
                        Log.e("Mive: ", "Var Total Amount "+totalAmount);


                        totalAmount = totalAmount + Integer.parseInt(editTextAmount.getText().toString());
                        tvtotal.setText(""+totalAmount);



                    } else if (editTextAmount.getText() != null && editTextAmount.getText().length() > 0) {
                        int amount = Integer.parseInt(editTextAmount.getText().toString());
                        editTextQty.setText("" + (amount / Integer.parseInt(editTextPPU.getText().toString())));
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

                        int oldamt = Integer.parseInt(editTextAmount.getText().toString());
                        Log.e("Mive: ", "Var Old Amount "+oldamt);

                        totalAmount = totalAmount - oldamt;
                    }
                }
                else if ( !b) {



                    if (editTextQty.getText() != null && editTextQty.getText().length() > 0 )
                    {
                        if(editTextAmount.getText().length()>0) {
                            int qty = Integer.parseInt(editTextQty.getText().toString());
                            editTextPPU.setText("" + (Integer.parseInt(editTextAmount.getText().toString()) / qty));
                        }
                    }
                    else if (editTextPPU.getText() != null && editTextPPU.getText().length() > 0)
                    {
                        if(editTextAmount.getText().length()>0)
                        {
                            int ppu = Integer.parseInt(editTextPPU.getText().toString());
                            editTextQty.setText("" + (Integer.parseInt(editTextAmount.getText().toString()) / ppu));
                        }
                    }





                    Log.e("Mive: ", "Var Total Amount "+totalAmount);
                    Log.e("Mive: ", "EdittextAmount " + editTextAmount.getText().toString());


                    totalAmount = totalAmount + Integer.parseInt(editTextAmount.getText().toString());
                    tvtotal.setText(""+totalAmount);


                }

            }
        });

    }



    private class SendDataAddToCart extends AsyncTask<Void, Void, Void>
    {
        private String urladdtocart;


        private JSONObject jsonObj;
        private ProgressDialog pDialog;
        HttpParams myParams = new BasicHttpParams();
        JSONObject json;
        HttpClient client = new DefaultHttpClient(myParams);
        private JSONObject jsonObjectresult;
       /* HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);*/

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Getting Products...");
            pDialog.setCancelable(false);


        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            HttpConnectionParams.setConnectionTimeout(myParams, 10000);
            Log.e("inside", "service handler");
            // Making a request to urladdtocart and getting response
            // get he list fo items here and put it in json
            itemsList = ItemListDTO.getInstance().getItemlist();

            // Building Parameters
            JSONObject params= null;
            params = new JSONObject();
            try {
                SharedPreferences prefs = context.getSharedPreferences("userIdPref", context.MODE_PRIVATE);
                int restoreduserid = prefs.getInt("userId", 0);
                Log.e("retrieved id", "" + restoreduserid);


                String restoredDummyCartId = JSONDTO.getInstance().getJsonUser().optJSONObject("dummycart").optString("dummycart_id");

                params.put("dummycartId",restoredDummyCartId);


                params.put("userId",restoreduserid);
                JSONObject jsonItems = new JSONObject();
                for (int i = 0; i < itemsList.size(); i++)
                {
                    HashMap<String,String> hashMap = new HashMap();
                    hashMap.put("qty",itemsList.get(i).get("units").toString());
                    hashMap.put("productId",itemsList.get(i).get("id").toString());
                    hashMap.put("pricePerUnit", itemsList.get(i).get("pricePerUnit").toString());


                    JSONObject objOneItem = new JSONObject(hashMap.toString());
                    jsonItems.put(""+(i+1),objOneItem);

                }

                params.put("items",jsonItems);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("params", params.toString());



                jsonParser = new JSONParser();
                urladdtocart = "http://www.mive.in/api/addtodummycart/";
            jsonObjectresult = jsonParser.makeHttpRequest(urladdtocart, "POST", params);
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
            Log.e("result", jsonObjectresult.toString());


            ItemListDTO.getInstance().setItemlist(new ArrayList<Map>());



               Intent intent = new Intent(context,DummyCartActivity.class);

            intent.putExtra("isDummy", true);
            context.startActivity(intent);

        }

    }

}
