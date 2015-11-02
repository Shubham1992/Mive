package in.mive.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
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

import in.mive.app.imageupload.InvoiceUploadActivity;
import in.mive.app.savedstates.JSONDTO;

/**
 * Created by Admin-PC on 11/4/2015.
 */
public class DummyOrderFilterActivity extends Activity
{
	LayoutInflater inflater;
	ViewGroup sellerContainer;
	@Override
	public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState)
	{
		super.onCreate(savedInstanceState, persistentState);
		setContentView(R.layout.order_filter);
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		sellerContainer = (ViewGroup) findViewById(R.id.storeFilterContainer);

		JSONObject jsonObject = JSONDTO.getInstance().getJsonUser();

		inflateSellers(jsonObject);

	}

	private void inflateSellers(JSONObject jsonObject)
	{
		JSONArray jsonArray = jsonObject.optJSONArray("dummyvendors");

		List<HashMap<String, String>> list = new ArrayList();

		for (int i = 0; i < jsonArray.length(); i++) {

			//get a particular vendor
			JSONObject objCategories = jsonArray.optJSONObject(i);
			JSONObject objectSeller = objCategories.optJSONObject("seller");
			final String catId = objCategories.optString("dummyvendor_id");

			JSONArray arrayProducts = objCategories.optJSONArray("products");
			Log.e("products dummy", arrayProducts.toString());





			final String name = objectSeller.optString("nameOfSeller");
			final  String sellerId = objectSeller.optString("seller_id");

			HashMap hashMap = new HashMap();
			hashMap.put("sellerId", sellerId);
			list.add(hashMap);

			View viewStoreTab = inflater.inflate(R.layout.store_tab_for_filter, sellerContainer, false);
			TextView tvName = (TextView) viewStoreTab.findViewById(R.id.sellername);
			tvName.setText(name);




			sellerContainer.addView(viewStoreTab);
		}

	}
}
