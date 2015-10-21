package in.mive.app.layouthelper;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mive.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import in.mive.app.activities.MainActivity;
import in.mive.app.savedstates.SavedSellerProductsMap;

/**
 * Created by Shubham on 10/17/2015.
 */
public class InflateStores {
    LayoutInflater inflater;
    List<HashMap> list;
    HashMap map;
    int imgcnt=0;
    public void inflateStoreTabs(final Context context , ViewGroup layout, JSONObject jsonObject)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        map = new HashMap();
        JSONArray jsonArray = jsonObject.optJSONArray("categories");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject objCategories = jsonArray.optJSONObject(i);
            JSONObject objectSeller = objCategories.optJSONObject("seller");
            final String catId = objCategories.optString("categoryvendor_id");

            JSONArray arrayProducts = objCategories.optJSONArray("products");
            Log.e("products", arrayProducts.toString());



            map.put(catId, arrayProducts);


            final String name = objectSeller.optString("nameOfSeller");
            View viewStoreTab = inflater.inflate(R.layout.store_tab, layout, false);
            TextView tvName = (TextView) viewStoreTab.findViewById(R.id.tvStoreName);
            ImageView imageView = (ImageView) viewStoreTab.findViewById(R.id.storeImage);
            imgcnt = i %3 ;
            Log.e("image cnt", ""+imgcnt);
            if (imgcnt ==0)
            imageView.setBackgroundResource(R.drawable.sb1);

            else if (imgcnt == 1)
                imageView.setBackgroundResource(R.drawable.sb2);

            else if (imgcnt == 2)
                imageView.setBackgroundResource(R.drawable.sb3);

            tvName.setText(name);
            viewStoreTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("catId", catId);
                    intent.putExtra("sellername", name);
                    context.startActivity(intent);
                }
            });

            layout.addView(viewStoreTab);
        }

        SavedSellerProductsMap.getInstance().setProductMap(map);
    }
}
