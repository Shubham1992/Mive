package in.mive.app.layouthelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mive.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.mive.app.activities.MainActivity;
import in.mive.app.imageupload.InvoiceUploadActivity;
import in.mive.app.savedstates.JSONDTO;
import in.mive.app.savedstates.SavedSellerIds;
import in.mive.app.savedstates.SavedSellerProductsMap;

/**
 * Created by Shubham on 10/17/2015.
 */
public class InflateDummyStores {
    LayoutInflater inflater;
    List<HashMap> list;
    HashMap map;
    int imgcnt=0;
    public void inflateStoreTabs(final Context context , ViewGroup layout, JSONObject jsonObject)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        map = new HashMap();
        JSONArray jsonArray = jsonObject.optJSONArray("dummyvendors");

        List<HashMap<String, String>> list = new ArrayList();

        for (int i = 0; i < jsonArray.length(); i++) {

            //get a particular vendor
            JSONObject objCategories = jsonArray.optJSONObject(i);
            JSONObject objectSeller = objCategories.optJSONObject("seller");
            final String catId = objCategories.optString("dummyvendor_id");

            JSONArray arrayProducts = objCategories.optJSONArray("products");
            Log.e("products dummy", arrayProducts.toString());



            map.put(catId, arrayProducts);


            final String name = objectSeller.optString("nameOfSeller");
            final  String sellerId = objectSeller.optString("seller_id");

            HashMap hashMap = new HashMap();
            hashMap.put("sellerId", sellerId);
            list.add(hashMap);

            View viewStoreTab = inflater.inflate(R.layout.store_tab, layout, false);
            TextView tvName = (TextView) viewStoreTab.findViewById(R.id.tvStoreName);
            tvName.setText(name);
            Typeface custom_font = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
            tvName.setTypeface(custom_font);
            //ImageView imageView = (ImageView) viewStoreTab.findViewById(R.id.storeImage);
           // final LinearLayout linearLayoutCont = (LinearLayout) viewStoreTab.findViewById(R.id.optnSkipinvoicecontainer);
          //  Button buttonOrder = (Button) viewStoreTab.findViewById(R.id.order);
          //  Button buttonInvoice = (Button) viewStoreTab.findViewById(R.id.invoice);

            viewStoreTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("catId", catId);
                    intent.putExtra("sellername", name);
                    intent.putExtra("sellerId",""+sellerId);
                    intent.putExtra("urlDummy",true );
                    context.startActivity(intent);
                }
            });

            imgcnt = i %3 ;
            Log.e("image cnt", "" + imgcnt);
           /* if (imgcnt ==0)
            imageView.setBackgroundResource(R.drawable.sb1);

            else if (imgcnt == 1)
                imageView.setBackgroundResource(R.drawable.sb2);

            else if (imgcnt == 2)
                imageView.setBackgroundResource(R.drawable.sb3);



            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (linearLayoutCont.getVisibility() == View.VISIBLE)
                        linearLayoutCont.setVisibility(View.GONE);
                    else
                        linearLayoutCont.setVisibility(View.VISIBLE);
                }
            });
            buttonOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("catId", catId);
                    intent.putExtra("sellername", name);
                    intent.putExtra("sellerId",""+sellerId);
                    intent.putExtra("urlDummy",true );
                    context.startActivity(intent);
                }
            });
            buttonInvoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, InvoiceUploadActivity.class);
                    intent.putExtra("catId", catId);
                    SharedPreferences prefs = context.getSharedPreferences("userIdPref", Context.MODE_PRIVATE);
                    int restoreduserid = prefs.getInt("userId", 0);
                    intent.putExtra("userId", restoreduserid);
                    intent.putExtra("dummycartId", JSONDTO.getInstance().getJsonUser().optJSONObject("dummycart").optString("dummycart_id"));

                    intent.putExtra("sellername", name);
                    intent.putExtra("sellerId", "" + sellerId);
                    intent.putExtra("urlDummy", true);
                    context.startActivity(intent);
                }
            });*/

            layout.addView(viewStoreTab);
        }

        SavedSellerIds.getInstance().setList(list);
       /* if(SavedSellerProductsMap.getInstance().getProductMap() !=  null
                && SavedSellerProductsMap.getInstance().getProductMap().size() < 1)*/
            SavedSellerProductsMap.getInstance().setProductMap(map);
    }
}
