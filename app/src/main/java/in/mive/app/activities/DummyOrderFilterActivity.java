package in.mive.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import in.mive.app.savedstates.SavedSellerIds;

/**
 * Created by Admin-PC on 11/4/2015.
 */
public class DummyOrderFilterActivity extends Activity
{
	LayoutInflater inflater;
	ViewGroup sellerContainer;
	// userId dayFilter  paymentFilter  sortBy
	String userId ;
	String days;
	private String paymentFilter;
	private String sortBy = "date";
	TextView tvall , tvpaid , tvunpaid;
	TextView tvdate, tvSeller, tvSubTotal, tvStatus;
	EditText etDays;
	Button apply;
    List<HashMap<String,String>> listToSubmit = new ArrayList<>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_filter);
		apply = (Button) findViewById(R.id.applyfilter);
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		sellerContainer = (ViewGroup) findViewById(R.id.storeFilterContainer);

		JSONObject jsonObject = JSONDTO.getInstance().getJsonUser();

		SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
		int restoreduserid = prefs.getInt("userId", 0);
		userId = ""+restoreduserid;
		days = "100";
		paymentFilter = "all";
		sortBy = "date";



		inflateSellers(jsonObject);

		settingListeners();

		apply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                Intent intent = new Intent(DummyOrderFilterActivity.this, PreviousDummyOrders.class);
                SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
                int restoreduserid = prefs.getInt("userId", 0);
                intent.putExtra("userId", restoreduserid);
                intent.putExtra("sortBy", sortBy);
                intent.putExtra("paymentFilter", paymentFilter);



                intent.putExtra("dayFilter" , Integer.parseInt(etDays.getText().toString()));
				startActivity(intent);
                finish();
			}
		});

	}

	private void settingListeners()
	{
		tvall = (TextView) findViewById(R.id.all);
		tvpaid = (TextView) findViewById(R.id.paid);
		tvunpaid = (TextView) findViewById(R.id.unpaid);
		tvdate= (TextView) findViewById(R.id.datefilter);
		tvSeller = (TextView) findViewById(R.id.sellerfilter);
		tvStatus = (TextView) findViewById(R.id.statusfilter);
		tvSubTotal = (TextView) findViewById(R.id.subtotalfilter);
		etDays = (EditText) findViewById(R.id.dayset);




		tvall.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					tvall.setBackgroundResource(R.drawable.dayselectedbck);
					tvpaid.setBackgroundResource(R.drawable.dayselectorborder);
					tvunpaid.setBackgroundResource(R.drawable.dayselectorborder);
					paymentFilter = "all";
				}
			});
		tvpaid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				tvpaid.setBackgroundResource(R.drawable.dayselectedbck);
				tvall.setBackgroundResource(R.drawable.dayselectorborder);
				tvunpaid.setBackgroundResource(R.drawable.dayselectorborder);
				paymentFilter = "paid";
			}
		});
		tvunpaid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				tvunpaid.setBackgroundResource(R.drawable.dayselectedbck);
				tvpaid.setBackgroundResource(R.drawable.dayselectorborder);
				tvall.setBackgroundResource(R.drawable.dayselectorborder);
				paymentFilter = "unpaid";
			}
		});

		tvdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sortBy = "date";
				tvdate.setBackgroundColor(Color.parseColor("#e8e8e8"));
				tvSubTotal.setBackgroundColor(Color.WHITE);
				tvSeller.setBackgroundColor(Color.WHITE);
				tvStatus.setBackgroundColor(Color.WHITE);

			}
		});
		tvSeller.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sortBy = "seller";
				tvdate.setBackgroundColor(Color.WHITE);
				tvSubTotal.setBackgroundColor(Color.WHITE);
				tvSeller.setBackgroundColor(Color.parseColor("#e8e8e8"));
				tvStatus.setBackgroundColor(Color.WHITE);
				}
		});
		tvStatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sortBy = "status";

				tvdate.setBackgroundColor(Color.WHITE);
				tvSubTotal.setBackgroundColor(Color.WHITE);
				tvSeller.setBackgroundColor(Color.WHITE);
				tvStatus.setBackgroundColor(Color.parseColor("#e8e8e8"));}
		});
		tvSubTotal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				sortBy = "subtotal";

				tvdate.setBackgroundColor(Color.WHITE);
				tvSubTotal.setBackgroundColor(Color.parseColor("#e8e8e8"));
				tvSeller.setBackgroundColor(Color.WHITE);
				tvStatus.setBackgroundColor(Color.WHITE);}
		});
	}

	private void inflateSellers(JSONObject jsonObject)
	{
		JSONArray jsonArray = jsonObject.optJSONArray("dummyvendors");

		List<HashMap<String, String>> list = new ArrayList();

		for (int i = 0; i < jsonArray.length(); i++) {

			//get a particular vendor
			JSONObject objCategories = jsonArray.optJSONObject(i);
			JSONObject objectSeller = objCategories.optJSONObject("seller");


			final String name = objectSeller.optString("nameOfSeller");
			final  String sellerId = objectSeller.optString("seller_id");

			HashMap hashMap = new HashMap();
			hashMap.put("sellerId", sellerId);
			list.add(hashMap);

			View viewStoreTab = inflater.inflate(R.layout.store_tab_for_filter, sellerContainer, false);
			final TextView tvName = (TextView) viewStoreTab.findViewById(R.id.sellername);
			tvName.setText(name);
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, String> map = new HashMap();
                    map.put("sellerId", sellerId);
                    listToSubmit.add(map);
                    tvName.setBackgroundColor(Color.parseColor("#e8e8e8"));
                    SavedSellerIds.getInstance().setList(listToSubmit);
                }
            });




			sellerContainer.addView(viewStoreTab);
		}

	}
}
