package in.mive.app.layouthelper;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mive.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import in.mive.app.savedstates.SavedSellerProductsList;

/**
 * Created by Shubham on 10/17/2015.
 */
public class InflateStores {
    LayoutInflater inflater;
    List<HashMap> list;
    public void inflateStoreTabs(Context context , ViewGroup layout, JSONObject jsonObject)
    {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list = new ArrayList<>();
        JSONArray jsonArray = jsonObject.optJSONArray("categories");

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject objCategories = jsonArray.optJSONObject(i);
            JSONObject objectSeller = objCategories.optJSONObject("seller");
            String catId = objCategories.optString("categoryvendor_id");

            JSONArray arrayProducts = objCategories.optJSONArray("products");
            Log.e("products", arrayProducts.toString());


            HashMap map = new HashMap();
            map.put(catId, arrayProducts);
            list.add(map);

            String name = objectSeller.optString("nameOfSeller");
            View viewStoreTab = inflater.inflate(R.layout.store_tab, layout, false);
            TextView tvName = (TextView) viewStoreTab.findViewById(R.id.tvStoreName);

            tvName.setText(name);
            layout.addView(viewStoreTab);
        }

        SavedSellerProductsList.getInstance().setItemlist(list);
    }
}
