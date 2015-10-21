package in.mive.app.helperclasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.mive.R;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.EventListener;

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
import in.mive.app.activities.DescriptionActivity;
import in.mive.app.imageloader.ImageLoader;
import in.mive.app.savedstates.ButtonDTO;
import in.mive.app.savedstates.CartItemListDTO;
import in.mive.app.savedstates.ItemListDTO;
import in.mive.app.savedstates.JSONDTO;

/**
 * Created by Admin-PC on 8/10/2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ProductViewHolder> implements Filterable{


    List<Map> products=new ArrayList<Map>(); // products is a list that holds the products acquired from json
	List<Map> itemsList; // itemList holds items selected by user
    Context context;
    Button btnCart;
    JSONParser jsonParser = new JSONParser();
    private float dX, dY;
    ViewGroup drgbllayout;
    private int tempPosHolder;
    ViewGroup vgroup;
    public RVAdapter(List<Map> products, final Context context)
	{   Log.e("setting", "adapter");
        this.products=products;
        Log.e("products",products.toString());
        this.context=context;
        itemsList = ItemListDTO.getInstance().getItemlist();
        btnCart = ButtonDTO.getInstance().getBtn();



        //btnCart.setOnTouchListener(RVAdapter.this);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("list on click", itemsList.toString());
                ItemListDTO.getInstance().setItemlist(itemsList);

                //make http request here to send data to cart
                btnCart.setEnabled(false);
			new SendDataAddToCart().execute();



            }
        });

    }
    //.................

    @Override
    public Filter getFilter() {

        return null;
    }

    //.................

    @Override
	public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards, parent, false);
		ProductViewHolder pvh = new ProductViewHolder(v);
		return pvh;

	}
	Toast tAdd;
	Toast tRemove;
	@Override
	public void onBindViewHolder(final ProductViewHolder holder, final int position)
	{
		holder.tvproductName.setText(products.get(position).get("productName").toString());

		holder.tvproductPricePerUnit.setText("Rs. " + products.get(position).get("pricePerUnit").toString());

		holder.tvproductQuantityAvailable.setText(products.get(position).get("availableQty").toString());
		// Load image here
		// image_url - is image urladdtocart path
		// loader - loader image, will be shown before loading image

		int loader = R.drawable.tomato;
		ImageLoader imgLoader = new ImageLoader(context);
        imgLoader.DisplayImage("http://www.mive.in/" + products.get(position).get("productImage").toString(), loader, holder.imgProductImage);

        holder.imgProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(context, DescriptionActivity.class);
                intent.putExtra("prdctId",products.get(position).get("productId").toString() );
                context.startActivity(intent);
            }
        });

        tempPosHolder = position;
        holder.btnPlusQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flagSame = false;
                int qnt = Integer.parseInt(holder.tvproductQuantitySelected.getText().toString());
                qnt++;

                holder.tvproductQuantitySelected.setText("" + qnt);
                holder.rlCard.setBackgroundColor(Color.parseColor("#caf4f3"));

                /*ViewGroup vg = SavedTopLayout.getInstance().getSavedTopLayout();
                Log.e("view ", vg.toString());
*/
                SnackbarManager.show(
                        Snackbar.with(context) // context
                                .text(holder.tvproductName.getText().toString()+" added to cart") // text to display
                                .actionLabel("Save") // action button label
                                .actionColor(Color.parseColor("#21bdba"))
                                .eventListener(new EventListener() {
                                    @Override
                                    public void onShow(Snackbar snackbar) {
                                       // btnCart.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onShowByReplace(Snackbar snackbar) {

                                    }

                                    @Override
                                    public void onShown(Snackbar snackbar) {

                                    }

                                    @Override
                                    public void onDismiss(Snackbar snackbar) {
                                       // btnCart.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onDismissByReplace(Snackbar snackbar) {

                                    }

                                    @Override
                                    public void onDismissed(Snackbar snackbar) {

                                    }
                                })
                                .actionListener(new ActionClickListener() {
                                    @Override
                                    public void onActionClicked(Snackbar snackbar) {
                                        Log.d("clicked snack", "Undoing something");
                                        btnCart.performClick();
                                    }
                                }) // action button's ActionClickListener
                        , (Activity) context); // activity where it is displayed
                // check if itemList has a map that alredy has a value of "id" same as current id.
                // If yes then just add the units else create new map and add in list

                if (itemsList.size() <= 0)
                {  //setting item list with porducts that are selected
                    HashMap itemDetailMap = new HashMap();
                    itemDetailMap.put("id", products.get(position).get("productId"));
                    itemDetailMap.put("units", qnt);
                    qnt=0;
                    itemsList.add(itemDetailMap);
                    flagSame = true;
                }
                else
                {
                    for (int i = 0; i < itemsList.size(); i++)
                    {
                        if (itemsList.get(i).get("id").equals(products.get(position).get("productId")))
                        {
                            Log.e("same ", "true");
                            int changedUnit = Integer.parseInt(itemsList.get(i).get("units").toString());
                            changedUnit++;

                            itemsList.get(i).put("units", changedUnit);
                            changedUnit=0;
                            Log.e("id", itemsList.get(i).get("id").toString());
                            flagSame = true;
                            break;
                        }
                    }
                }


                if (!flagSame) {
                    HashMap itemDetailMap = new HashMap();
                    itemDetailMap.put("id", products.get(position).get("productId"));
                    itemDetailMap.put("units", qnt);
                    itemsList.add(itemDetailMap);

                }

                Log.e("items before setnum", itemsList.toString());
                ItemListDTO.getInstance().setItemlist(itemsList);




                setNumerOnFAB(itemsList, products.get(position).get("productId").toString());


            }
        });
        holder.btnMinusQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flagRemove = true;
                int qnt = Integer.parseInt(holder.tvproductQuantitySelected.getText().toString());
                if(qnt <= 1)
                {   holder.rlCard.setBackgroundColor(Color.parseColor("#ffffff"));}

                if (qnt >= 1)
                {   qnt--;
                    SnackbarManager.show(
                            Snackbar.with(context) // context
                                    .text(holder.tvproductName.getText().toString() + " deleted from cart") // text to display
                                    .actionLabel("Save") // action button label
                                    .actionColor(Color.parseColor("#21bdba"))
                                    .eventListener(new EventListener() {
                                        @Override
                                        public void onShow(Snackbar snackbar) {
                                           // btnCart.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onShowByReplace(Snackbar snackbar) {

                                        }

                                        @Override
                                        public void onShown(Snackbar snackbar) {

                                        }

                                        @Override
                                        public void onDismiss(Snackbar snackbar) {
                                           // btnCart.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onDismissByReplace(Snackbar snackbar) {

                                        }

                                        @Override
                                        public void onDismissed(Snackbar snackbar) {

                                        }
                                    })
                                    .actionListener(new ActionClickListener() {
                                        @Override
                                        public void onActionClicked(Snackbar snackbar) {
                                            Log.d("clicked snack", "Undoing something");
                                            btnCart.performClick();
                                        }
                                    }) // action button's ActionClickListener
                            , (Activity)context); // activity where it is displayed
                }

                holder.tvproductQuantitySelected.setText("" + qnt);
                // check if list has a map that alredy has a value of "id" same as current id.
                // If yes then just add the units else create new map and add in list


                for (int i = 0; i < itemsList.size(); i++) {
                    if (itemsList.get(i).get("id").equals(products.get(position).get("productId"))) {
                        Log.e("same ", "true");
                        int changedUnit = Integer.parseInt(itemsList.get(i).get("units").toString());

                        if (changedUnit >= 1) {
                            changedUnit--;

                            if (changedUnit == 0) {
                                if (itemsList.get(i) != null)
                                    itemsList.remove(i);
                                break;

                            } else {
                                itemsList.get(i).put("units", changedUnit);

                                Log.e("id", itemsList.get(i).get("id").toString());
                                flagRemove = false;
                                break;
                            }
                        } else {
                            if (itemsList.get(i) != null)
                                itemsList.remove(i);
                        }

                    }
                }


                Log.e("items list data", itemsList.toString());
                ItemListDTO.getInstance().setItemlist(itemsList);
                setNumerOnFAB(itemsList, products.get(position).get("productId").toString());
            }


        });

	}

    private void setNumerOnFAB(List<Map> itemsList , String currentId)
    { Log.e("cart items in main", CartItemListDTO.getInstance().getItemlist().toString());
        Log.e("item list", itemsList.toString());

        int c=0;


        Log.e("inside", "set button");
        Log.e("cart button text", btnCart.getText().toString());
        for (int i=0; i<itemsList.size(); i++)
        {
            if(Integer.parseInt(itemsList.get(i).get("units").toString())!=0)
            {
                    c++;
                    Log.e("raised c", ""+c);
            }
        }
        int crtcount =  Integer.parseInt(JSONDTO.getInstance().getJsonCart().optString("count"));
       /*if( chkIfAlreadyInCart(currentId))
                c--;*/
        Log.e("count", "" + crtcount + " " + c);

        btnCart.setText("" + (c + crtcount));
    }

    public  boolean chkIfAlreadyInCart(String currentId)
    {List<HashMap> cartItemsList = CartItemListDTO.getInstance().getItemlist();
Log.e("current id", currentId);

        for (int i = 0; i < cartItemsList.size(); i++) {
            Log.e("Product id in cart",cartItemsList.get(i).get("productId").toString() );
            if (currentId.equalsIgnoreCase(cartItemsList.get(i).get("productId").toString()))
            {
                return true;
            }
        }
        return false;
    }

    @Override
	public int getItemCount()
	{
		return products.size();
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);}


	public static class ProductViewHolder extends RecyclerView.ViewHolder {
		CardView cv;
		TextView tvproductName;
		TextView tvproductQuantityAvailable;
		TextView tvproductPricePerUnit;
		TextView tvproductQuantitySelected;
		TextView btnPlusQuantity;
		TextView btnMinusQuantity;
		ImageView imgProductImage;
        ViewGroup rlCard;

		ProductViewHolder(View itemView) {
			super(itemView);
			cv = (CardView)itemView.findViewById(R.id.cv);
            rlCard = (ViewGroup)itemView.findViewById(R.id.rlCard);
			tvproductName= (TextView) itemView.findViewById(R.id.tvProductName);
			tvproductPricePerUnit= (TextView) itemView.findViewById(R.id.tvPricePerUnit);
			tvproductQuantityAvailable= (TextView) itemView.findViewById(R.id.tvAvailableQuantity);
			tvproductQuantitySelected= (TextView) itemView.findViewById(R.id.tvQuantitySelected);
			imgProductImage=(ImageView)itemView.findViewById(R.id.imgProductPhoto);
			btnMinusQuantity=(TextView)itemView.findViewById(R.id.btnMinusQuantity);
			btnPlusQuantity=(TextView)itemView.findViewById(R.id.btnPlusQuantity);

		}
	}
    private class SendDataAddToCart extends AsyncTask<Void, Void, Void>
    {
        private String urladdtocart = "http://www.mive.in/api/addtocart/";
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
                int restoreduserid = prefs.getInt("userId",0);
                Log.e("retrieved id", "" + restoreduserid);

                String restoredcartid = JSONDTO.getInstance().getJsonUser().optJSONObject("cart").optString("cart_id");

                params.put("cartId",restoredcartid);
                params.put("userId",restoreduserid);
                JSONObject jsonItems = new JSONObject();
                for (int i = 0; i < itemsList.size(); i++)
                {
                    HashMap<String,String> hashMap = new HashMap();
                    hashMap.put("qty",itemsList.get(i).get("units").toString());
                    hashMap.put("productId",itemsList.get(i).get("id").toString());


                    JSONObject objOneItem = new JSONObject(hashMap.toString());
                    jsonItems.put(""+(i+1),objOneItem);

                }

                params.put("items",jsonItems);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("params", params.toString());



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
            btnCart.setEnabled(true);
            //........ changed

            ItemListDTO.getInstance().setItemlist(new ArrayList<Map>());

            Intent intent = new Intent(context,CartActivity.class);
            context.startActivity(intent);

        }

    }

}
