package in.mive.app.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.mive.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import in.mive.app.GetCustomProductsComponentFromJson;
import in.mive.app.RVAdapter;
import in.mive.app.ServiceHandler;
import in.mive.app.savedstates.SavedCustCat;
import in.mive.app.savedstates.SavedFruits;
import in.mive.app.savedstates.SavedVegetables;

/**
 * Created by Shubham on 9/14/2015.
 */
public class CustomCatActivity extends Activity {

    private RecyclerView rvCstm;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customcat);

        rvCstm = (RecyclerView)findViewById(R.id.rvCstmCat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvCstm.setLayoutManager(layoutManager);


        Intent intent =getIntent();
        userId = intent.getIntExtra("userId", 0);
        int size= SavedCustCat.getobj().getList().size();
        Log.e("size", "" + size);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(size<=0)

        {
            new  GetCategories().execute();
        }
        else
        {
            RVAdapter adapter = new RVAdapter(SavedCustCat.getobj().getList(),this);
            rvCstm.setAdapter(adapter);
        }
    }



    private class GetCategories extends AsyncTask<Void, Void, Void>
    {
        private  String url = "http://www.mive.in/api/user/customcategory/"+userId+"/?format=json";
        private JSONObject jsonObj;
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(CustomCatActivity.this);
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

            resultList = getProductsComponentFromJson.getComponent(CustomCatActivity.this, jsonObj);
            SavedCustCat.getobj().setList(resultList);


            RVAdapter adapter = new RVAdapter(resultList,CustomCatActivity.this );
            rvCstm.setAdapter(adapter);

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }
}
