package in.mive.app.activities;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mive.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import in.mive.app.helperclasses.EndlessRecyclerOnScrollListener;
import in.mive.app.helperclasses.GetCustomProductsComponentFromJson;
import in.mive.app.helperclasses.RVAdapter;
import in.mive.app.helperclasses.ServiceHandler;
import in.mive.app.savedstates.ButtonDTO;
import in.mive.app.savedstates.SavedCustCat;

public class CustomCatFragment extends Fragment {

	private RecyclerView rvcstm;
	private int scrolledDistance =0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_custom, container, false);
		rvcstm = (RecyclerView) rootView.findViewById(R.id.rvcstm);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		rvcstm.setLayoutManager(layoutManager);
        Button btnCart = ButtonDTO.getInstance().getBtn();
		rvcstm.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
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


		int size= SavedCustCat.getobj().getList().size();
		Log.e("size", "" + size);
		if(size<=0)

		{
			new  GetCategories().execute();
		}
		else
		{
			RVAdapter adapter = new RVAdapter(SavedCustCat.getobj().getList(),getActivity(), false);
			rvcstm.setAdapter(adapter);
		}
		return rootView;
	}
	private class GetCategories extends AsyncTask<Void, Void, Void>
	{
		SharedPreferences prefs = getActivity().getSharedPreferences("userIdPref", getActivity().MODE_PRIVATE);
		int restoreduserid = prefs.getInt("userId",0);

		private  String url = "http://www.mive.in/api/user/customcategory/"+restoreduserid+"/?format=json";
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

			GetCustomProductsComponentFromJson getProductsComponentFromJson =new GetCustomProductsComponentFromJson();

			resultList = getProductsComponentFromJson.getComponent(getActivity(), jsonObj);
			SavedCustCat.getobj().setList(resultList);


			RVAdapter adapter = new RVAdapter(resultList,getActivity(),false);
			rvcstm.setAdapter(adapter);

		}

	}

}
