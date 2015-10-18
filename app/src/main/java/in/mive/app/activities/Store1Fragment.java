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

import in.mive.app.helperclasses.GetProductsComponentFromJson;
import in.mive.app.helperclasses.RVAdapter;
import in.mive.app.helperclasses.ServiceHandler;
import in.mive.app.savedstates.SavedVegetables;

public class Store1Fragment extends Fragment {

	private RecyclerView rvVeg;
    private int scrolledDistance=0;
    private ProgressDialog pDialog;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.store_fragment, container, false);

		rvVeg = (RecyclerView) rootView.findViewById(R.id.rvstore1);
		LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
		rvVeg.setLayoutManager(layoutManager);

        int size=SavedVegetables.getobj().getList().size();
        Log.e("size", "" + size);
        if(size<=0)

        {
            new  GetCategories().execute();
        }
        else
        {
            RVAdapter adapter = new RVAdapter(SavedVegetables.getobj().getList(),getActivity());
            rvVeg.setAdapter(adapter);
        }
        return rootView;
	}
    private class GetCategories extends AsyncTask<Void, Void, Void>
    {
        private  String url = "http://www.mive.in/api/product/category/1/?format=json";
        private JSONObject jsonObj;

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

            Log.d("Response: ", "> " + jsonStr);

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
            SavedVegetables.getobj().setList(resultList);


            RVAdapter adapter = new RVAdapter(resultList,getActivity());
            rvVeg.setAdapter(adapter);

        }

    }


}
