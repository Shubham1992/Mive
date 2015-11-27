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
	ViewGroup statuscontainer, sortbyoptncontainer;
	// userId dayFilter  paymentFilter  sortBy
	String userId ;
	String days;
	private String paymentFilter = "all";
	private String sortBy = "date";
	TextView tvall , tvpaid , tvunpaid;
	TextView tvdate, tvSeller, tvSubTotal, tvStatus;
	EditText etDays;
	Button apply, clear;
	TextView toggleseller, toggledays, togglestatus, togglesortby;
    List<HashMap<String,String>> listToSubmit = new ArrayList<>();
	ImageView imageViewbck;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_filter);
		apply = (Button) findViewById(R.id.applyfilter);
        clear = (Button) findViewById(R.id.clearfilter);

		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		sellerContainer = (ViewGroup) findViewById(R.id.storeFilterContainer);
		statuscontainer = (ViewGroup) findViewById(R.id.statuscontainer);
		sortbyoptncontainer = (ViewGroup) findViewById(R.id.sortbyoptnscntainer);
		imageViewbck = (ImageView) findViewById(R.id.imgbckHome);
		imageViewbck.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(DummyOrderFilterActivity.this, PreviousDummyOrders.class);
				SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
				int restoreduserid = prefs.getInt("userId", 0);
				intent.putExtra("userId", restoreduserid);
				intent.putExtra("sortBy", sortBy);
				intent.putExtra("paymentFilter", paymentFilter);


				intent.putExtra("dayFilter", Integer.parseInt(etDays.getText().toString()));
				startActivity(intent);
				finish();
			}
		});

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


                intent.putExtra("dayFilter", Integer.parseInt(etDays.getText().toString()));
                startActivity(intent);
                finish();
            }
        });
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
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
		toggleseller = (TextView) findViewById(R.id.toggleseller);
		toggledays = (TextView) findViewById(R.id.toggledays);
		togglestatus = (TextView) findViewById(R.id.togglestatus);
		togglesortby = (TextView) findViewById(R.id.togglesortby);


		toggleseller.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

                sellerContainer.setVisibility(View.VISIBLE);
                sortbyoptncontainer.setVisibility(View.GONE);
                statuscontainer.setVisibility(View.GONE);
                etDays.setVisibility(View.GONE);

                setBackColorAll();
                view.setBackgroundColor(Color.WHITE);
				setTextColorWhite();
				toggleseller.setTextColor(Color.BLACK);

            }
		});
		toggledays.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                sellerContainer.setVisibility(View.GONE);
                sortbyoptncontainer.setVisibility(View.GONE);
                statuscontainer.setVisibility(View.GONE);
                etDays.setVisibility(View.VISIBLE);

				setBackColorAll();
                view.setBackgroundColor(Color.WHITE);
				setTextColorWhite();
				toggledays.setTextColor(Color.BLACK);

			}
		});
		togglestatus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                sellerContainer.setVisibility(View.GONE);
                sortbyoptncontainer.setVisibility(View.GONE);
                statuscontainer.setVisibility(View.VISIBLE);
                etDays.setVisibility(View.GONE);

				setBackColorAll();
                view.setBackgroundColor(Color.WHITE);
				setTextColorWhite();
				togglestatus.setTextColor(Color.BLACK);
			}
		});

		togglesortby.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                sellerContainer.setVisibility(View.GONE);
                sortbyoptncontainer.setVisibility(View.VISIBLE);
                statuscontainer.setVisibility(View.GONE);
                etDays.setVisibility(View.GONE);

				setBackColorAll();
                view.setBackgroundColor(Color.WHITE);
				setTextColorWhite();
				togglesortby.setTextColor(Color.BLACK);
			}
		});


		tvall.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {

					tvall.setBackgroundColor(Color.parseColor("#e8e8e8"));
					tvpaid.setBackgroundColor(Color.parseColor("#ffffff"));
					tvunpaid.setBackgroundColor(Color.parseColor("#ffffff"));
					paymentFilter = "all";
				}
			});
		tvpaid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				tvpaid.setBackgroundColor(Color.parseColor("#e8e8e8"));
				tvall.setBackgroundColor(Color.parseColor("#ffffff"));
				tvunpaid.setBackgroundColor(Color.parseColor("#ffffff"));
				paymentFilter = "paid";
			}
		});
		tvunpaid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				tvunpaid.setBackgroundColor(Color.parseColor("#e8e8e8"));
				tvpaid.setBackgroundColor(Color.parseColor("#ffffff"));
				tvall.setBackgroundColor(Color.parseColor("#ffffff"));
				paymentFilter = "unpaid";
			}
		});

		tvdate.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				sortBy = "date";
				tvdate.setBackgroundColor(Color.parseColor("#e8e8e8"));
				tvSubTotal.setBackgroundColor(Color.WHITE);
				tvSeller.setBackgroundColor(Color.WHITE);
				tvStatus.setBackgroundColor(Color.WHITE);

			}
		});
		tvSeller.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
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

	private void setTextColorWhite()
	{
		togglesortby.setTextColor(Color.parseColor("#ffffff"));
		toggleseller.setTextColor(Color.parseColor("#ffffff"));
		toggledays.setTextColor(Color.parseColor("#ffffff"));
		togglestatus.setTextColor(Color.parseColor("#ffffff"));
	}

	private void setBackColorAll()
	{
		togglesortby.setBackgroundColor(Color.parseColor("#403c3c"));
		toggleseller.setBackgroundColor(Color.parseColor("#403c3c"));
		toggledays.setBackgroundColor(Color.parseColor("#403c3c"));
		togglestatus.setBackgroundColor(Color.parseColor("#403c3c"));

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

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(DummyOrderFilterActivity.this, PreviousDummyOrders.class);
		SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
		int restoreduserid = prefs.getInt("userId", 0);
		intent.putExtra("userId", restoreduserid);
		intent.putExtra("sortBy", sortBy);
		intent.putExtra("paymentFilter", paymentFilter);


		intent.putExtra("dayFilter", Integer.parseInt(etDays.getText().toString()));
		startActivity(intent);
		finish();

	}
}
