package in.mive.app.activities;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mive.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import in.mive.app.EndlessRecyclerOnScrollListener;
import in.mive.app.GetProductsComponentFromJson;
import in.mive.app.RVAdapter;
import in.mive.app.ServiceHandler;
import in.mive.app.savedstates.SavedFruits;
import in.mive.app.savedstates.SavedVegetables;

public class FruitsFragment extends Fragment {

	private RecyclerView rvFruits;
	int scrolledDistance=0;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_fruits, container, false);
		rvFruits = (RecyclerView) rootView.findViewById(R.id.rvFruits);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		rvFruits.setLayoutManager(layoutManager);
		rvFruits.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
			@Override
			public void onLoadMore(int current_page) {
				// do something...

			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				if (scrolledDistance > 40) {
					getActivity().getActionBar().hide();
					scrolledDistance = 0;
				} else if (scrolledDistance < -40) {
					getActivity().getActionBar().show();
					scrolledDistance = 0;
				}

				if ((dy > 0) || (dy < 0)) {
					scrolledDistance += dy;
				}
			}
		});



		int size= SavedFruits.getobj().getList().size();
		Log.e("size", "" + size);
		if(size<=0)

		{
			new  GetCategories().execute();
		}
		else
		{
			RVAdapter adapter = new RVAdapter(SavedFruits.getobj().getList(),getActivity());
			rvFruits.setAdapter(adapter);
		}
		return rootView;
	}
	private class GetCategories extends AsyncTask<Void, Void, Void>
	{
		private  String url = "http://www.mive.in/api/product/category/2/?format=json";
		private JSONObject jsonObj;
		private ProgressDialog pDialog;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Getting Products...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();
			Log.e("inside", "service handler");
			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

			Log.d("Response fruits: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {

					jsonObj = new JSONObject(jsonStr);


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
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();

			GetProductsComponentFromJson getProductsComponentFromJson =new GetProductsComponentFromJson();

			resultList = getProductsComponentFromJson.getComponent(getActivity(), jsonObj);
			SavedFruits.getobj().setList(resultList);


			RVAdapter adapter = new RVAdapter(resultList,getActivity());
			rvFruits.setAdapter(adapter);

		}

	}

}
