package in.mive.app.activities;



import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mive.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import in.mive.app.helperclasses.EndlessRecyclerOnScrollListener;
import in.mive.app.helperclasses.GetProductsComponentFromJson;
import in.mive.app.helperclasses.RVAdapter;
import in.mive.app.helperclasses.ServiceHandler;
import in.mive.app.savedstates.SavedAllCat;

public class AllCategoryFragment extends Fragment implements SearchView.OnQueryTextListener {
private  RecyclerView rv;
    private LinearLayoutManager mLinearLayoutManager;
    int page = 1;
    private int size;
    private int maxPage;
    int scrolledDistance=0;
    LinearLayout l ;

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_all_cat, container, false);

        rv = (RecyclerView) rootView.findViewById(R.id.rv);

      final  LinearLayoutManager llm=new LinearLayoutManager(getActivity());
        final StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(llm);
        final boolean[] loading = {true};



        rv.setOnScrollListener(new EndlessRecyclerOnScrollListener(llm) {
            @Override
            public void onLoadMore(int current_page)
            {
                // do something...
                Log.v("last item", "Last Item Wow !");
                Log.e("maxx page", ""+maxPage);

                if (page <= maxPage) {
                    page++;
                    new GetCategories().execute();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (scrolledDistance > 40) {
                   getActivity().getActionBar().hide();
                    scrolledDistance = 0;
                } else if (scrolledDistance < -40 ) {
                    getActivity().getActionBar().show();
                    scrolledDistance = 0;
                }

                if(( dy>0) || (dy<0)) {
                    scrolledDistance += dy;
                }
            }
        });


        size= SavedAllCat.getobj().getList().size();



        Log.e("size", ""+size);

        if(size<=0)

        {
            new  GetCategories().execute();
        }
        else
        {
            RVAdapter adapter = new RVAdapter(SavedAllCat.getobj().getList(),getActivity() , false);
            rv.setAdapter(adapter);
             }
                return rootView;
	}







    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
       // logic goes here
        return false;
    }



    private class GetCategories extends AsyncTask<Void, Void, Void>
    {
        private  String url = "http://mive.in/api/product/?format=json&page="+page;
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

            String pgcount = jsonObj.optString("count");

            Log.e("pg count",pgcount);

            maxPage = Integer.parseInt(pgcount);
            maxPage = maxPage/20;
            resultList = getProductsComponentFromJson.getComponent(getActivity(), jsonObj);



            List<Map> bigList= SavedAllCat.getobj().getList();
            for (int i = 0; i < resultList.size(); i++) {
                bigList.add(resultList.get(i));
            }


            SavedAllCat.getobj().setList(bigList);

            RVAdapter adapter = new RVAdapter(bigList,getActivity(), false);
            rv.setAdapter(adapter);


        }

    }




}
