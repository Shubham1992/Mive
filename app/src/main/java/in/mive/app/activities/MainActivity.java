package in.mive.app.activities;

import dmax.dialog.SpotsDialog;
import in.mive.app.helperclasses.GetProductsComponentFromJson;
import in.mive.app.helperclasses.GetProductsComponentFromJsonArray;
import in.mive.app.helperclasses.RVAdapter;
import in.mive.app.helperclasses.RVAdapterDummy;
import in.mive.app.helperclasses.ServiceHandler;

import in.mive.app.imageloader.ImageLoader;
import in.mive.app.imageupload.InvoiceUploadActivity;
import in.mive.app.imageupload.UploadActivity;
import in.mive.app.layouthelper.InflateStoresintoDrawer;
import in.mive.app.layouthelper.RecyclerViewProductsInflator;
import in.mive.app.savedstates.ButtonDTO;
import in.mive.app.savedstates.CartItemListDTO;
import in.mive.app.savedstates.JSONDTO;
import in.mive.app.savedstates.SavedSellerProductsMap;
import in.mive.app.savedstates.SavedTopLayout;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.github.yasevich.endlessrecyclerview.EndlessRecyclerView;
import com.mive.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity
		 {

	private ViewPager viewPager;

	private ActionBar actionBar;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
    private AlertDialog pDialog;
    private LinearLayout vpContainer;
	// Tab titles

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;
    RelativeLayout layoutslider;
    private int id;
    private JSONObject jsonObjuser;
    boolean loggedIn=false;
    private int pos;
    FrameLayout fl ;
    private Fragment fragmenthelp = null, fragmentCstm = null, fragmentContact= null, fragmentFAQ = null;
    Button btncart;
    Intent intnt;
    RecyclerView rvSearch;
             LinearLayout rvProducts;
    String searchText;
    TextView tvNoreslt;
    private PagerSlidingTabStrip tabs;
    ViewGroup mainframe;
    String catId;
    private List<Map> products;
    String sellername;
    private Button btnInvoiceSubmit;
    private boolean isUrlDummy;
    private String sellerId;
    private LinearLayoutManager layoutManager, layoutManager2;
    private TextView totalAmountFinal;
    private TextView skipbutton;

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)


    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


       // requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_main);

        rvSearch = (RecyclerView) findViewById(R.id.rvsearch);
        rvProducts = (LinearLayout) findViewById(R.id.rvProduct);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        layoutManager2 = new LinearLayoutManager(MainActivity.this);
        totalAmountFinal = (TextView) findViewById(R.id.totalAmountFinal);
        skipbutton = (TextView) findViewById(R.id.skipbutton);


        rvSearch.setLayoutManager(layoutManager);
       // rvProducts.setLayoutManager(layoutManager2);



        tvNoreslt = (TextView) findViewById(R.id.tvNoReslt);
        intnt = getIntent();



        id = intnt.getIntExtra("id", 0);
        isUrlDummy = intnt.getBooleanExtra("urlDummy", false);

        catId = intnt.getStringExtra("catId");
        sellername = intnt.getStringExtra("sellername");
        sellerId = intnt.getStringExtra("sellerId");

        loggedIn = intnt.getBooleanExtra("loggedIn", false);
        pos = intnt.getIntExtra("pos", 0);
        Log.e("id= ", "" + id);


        skipbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UploadActivity.class);

                SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
                int restoreduserid = prefs.getInt("userId", 0);
                intent.putExtra("userId", restoreduserid);
                intent.putExtra("dummycartId", JSONDTO.getInstance().getJsonUser().optJSONObject("dummycart").optString("dummycart_id"));
                intent.putExtra("sellerId", sellerId);


                intent.putExtra("sellerName", sellername);

                startActivity(intent);
            }
        });



        mainframe = (ViewGroup) findViewById(R.id.mainframe);
        SavedTopLayout.getInstance().setSavedTopLayout(mainframe);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(getResources().getColor(R.color.my_statusbar_color));
        }



        ActionBar ab = getActionBar();
        ab.setTitle(sellername);
        //ab.setSubtitle("sub-title");

        GetProductsComponentFromJsonArray fromJsonArray = new GetProductsComponentFromJsonArray();
        products = fromJsonArray.getComponent(MainActivity.this, SavedSellerProductsMap.getInstance().getProductMap().get(catId));


        if(isUrlDummy)
        {

            RecyclerViewProductsInflator  viewProductsInflator = new RecyclerViewProductsInflator();
            viewProductsInflator.inflateProducts(products, MainActivity.this , rvProducts, totalAmountFinal);


        }

        else
        {

        }
        // getActivity() will hand over the context to the method
        // if you call this inside an activity, simply replace getActivity() by "this"
        if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
        else {
            // we have internet connection, so it is save to connect to the internet here




            new GetCartData().execute();
            setUserInDrawer(JSONDTO.getInstance().getJsonUser());
        }





        //
        fl = (FrameLayout) findViewById(R.id.mainframe);




        hideFragments();

        // Initilization
		// viewPager = (ViewPager) findViewById(R.id.pager);
	    //	actionBar = getActionBar();



		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		//TextView tvSlider= (TextView) findViewById(R.id.tvSlider);

        layoutslider = (RelativeLayout) findViewById(R.id.layoutSlider);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.drawericon, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);

            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		/*viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
              //  actionBar.setSelectedNavigationItem(position);
                Log.e("text", "");
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });*/


    }

void hideFragments()
{
    if (fragmenthelp != null) {
        Fragment fr = fragmenthelp;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(fr).commit();
    }

    if(fragmentFAQ !=null)
    {
        Fragment fr=fragmentFAQ;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(fr).commit();
    }

    if(fragmentContact !=null)
    {
        Fragment fr=fragmentContact;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(fr).commit();
    }
    //getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);




}



    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Associate searchable configuration with the SearchView
        MenuItem item = menu.findItem(R.id.action_search);

        View view = menu.findItem(R.id.action_cart).getActionView();


        btncart = (Button) view.findViewById(R.id.cart_count);

        btncart.setVisibility(View.GONE);

        ButtonDTO.getInstance().setBtn(btncart);
        Log.e("setting buton", btncart.toString());



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

            menu.findItem(R.id.action_search).setIcon(R.drawable.search100);

            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));

            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String s) {
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
                    //.........
                    searchText = s.trim();

                  //  rvSearch.setVisibility(View.VISIBLE);
                   // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

                   hideFragments();
                  new GetSearchedData().execute();
                    //..........
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {



                    return false;

                }

            });

        }
        return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
			/*case R.id.action_settings:
				return true;*/
            case R.id.action_search:

                return true;
            case R.id.action_cart:

                return true;

            default:
				return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(layoutslider);
	//	menu.findItem(R.id.action_settings).setVisible(!true);

        View view = menu.findItem(R.id.action_cart).getActionView();
        btncart = (Button) view.findViewById(R.id.cart_count);
        ButtonDTO.getInstance().setBtn(btncart);


        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) searchViewMenuItem.getActionView();
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.search100);




        return super.onPrepareOptionsMenu(menu);
	}

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else return false;
    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet connection.");
        builder.setMessage("You have no internet connection");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                recreate();
            }
        });

        return builder;
    }


    private class GetCartData extends AsyncTask<Void, Void, Void>
    {

        String id;

        private String urlCart;


       // private ProgressDialog pDialog;
        private JSONObject jsonObjcart;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new SpotsDialog(MainActivity.this);
            pDialog.setMessage("Loading Products");
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            Log.e("inside", "service handler");
            // Making a request to urlcat1 and getting response
            id = JSONDTO.getInstance().getJsonUser().optJSONObject("cart").optString("cart_id");
            Log.e("id  of cart", id);
            urlCart = "http://www.mive.in/api/cart/cartitems/"+id+"/?format=json";


            String jsonStrCart = sh.makeServiceCall(urlCart, ServiceHandler.GET);



            Log.d("Response cart: ", "> " + jsonStrCart);


            if (jsonStrCart != null) {
                try {

                    jsonObjcart = new JSONObject(jsonStrCart);
                    Log.e("cart ", jsonObjcart.toString());
                    JSONDTO.getInstance().setJsonCart(jsonObjcart);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the cart");
            }

            return null;
        }
        List<Map> resultListcat1;
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if ( pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            // class to return the json attributes in form of hashmap.
            // The map is set in DTO from where it can be accessed at all the fragments




            if(jsonObjcart.optString("count") != null) {
                Log.e("count", jsonObjcart.optString("count"));
                btncart.setText(jsonObjcart.optString("count"));

              //.............
            JSONArray arritems = jsonObjcart.optJSONArray("results");
                //sizeofcartlist = arritems.length();

               JSONObject eachItem;

                //.........
                if(arritems != null)
                for (int i=0 ; i< arritems.length(); i++)
                {
                    eachItem = null;
                    try {

                        eachItem = arritems.getJSONObject(i);
                        HashMap<String, Object> map = new HashMap<String, Object>();

                        map.put("product", eachItem.optJSONObject("product"));
                        map.put("productId", eachItem.optJSONObject("product").optString("product_id"));

                        map.put("units", eachItem.optString("qtyInUnits"));
                        map.put("cartItemId", eachItem.optString("cartitem_id"));
                       //.........


                        List<HashMap> l = new ArrayList<>(); /*= CartItemListDTO.getInstance().getProductMap();*/
                        l.add(map);
                        CartItemListDTO.getInstance().setItemlist(l);

                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }

                    //............



            }
            }

    }

    private class GetSearchedData extends AsyncTask<Void, Void, Void>
    {

        private String urlSearchTitle = "http://mive.in/api/search/title/"+searchText+"/?format=json";
        private String urlSearchDescription ="http://mive.in/api/search/description/"+searchText+"/?format=json";

        private JSONObject jsonObjSearchTitle;
        private JSONObject jsonObjSearchDescription;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new SpotsDialog(MainActivity.this);
            pDialog.setMessage("Getting Products...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            Log.e("inside search", "service handler");

            String jsonStrSearchTitle = sh.makeServiceCall(urlSearchTitle, ServiceHandler.GET);

            sh = new ServiceHandler();
            String jsonStrSearchDesc = sh.makeServiceCall(urlSearchDescription, ServiceHandler.GET);


            Log.d("Response Search: ", "> " + jsonStrSearchTitle);
            Log.d("Response Search: ", "> " + jsonStrSearchDesc);
            if (jsonStrSearchTitle != null) {
                try {

                    jsonObjSearchTitle = new JSONObject(jsonStrSearchTitle);
                    //JSONDTO.getInstance().setJsonUser(jsonObjuser);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the user");
            }

            if (jsonStrSearchDesc != null) {
                try {

                    jsonObjSearchDescription = new JSONObject(jsonStrSearchDesc);
                    //JSONDTO.getInstance().setJsonUser(jsonObjuser);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the user");
            }

            return null;
        }
        List<Map> resultListcat1;
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            Log.e("json search", jsonObjSearchTitle.toString());
            int countSearchTitle = Integer.parseInt(jsonObjSearchTitle.optString("count").toString());
            int countSearchDesc = Integer.parseInt(jsonObjSearchDescription.optString("count").toString());
            if (countSearchTitle + countSearchDesc <= 0)
            {
            tvNoreslt.setVisibility(View.VISIBLE);
                rvSearch.setVisibility(View.GONE);
                rvSearch.removeAllViews();
                return;
            }

            tvNoreslt.setVisibility(View.GONE);

            GetProductsComponentFromJson componentFromJson = new GetProductsComponentFromJson();
            List<Map> resltSearchTitleList = componentFromJson.getComponent(MainActivity.this, jsonObjSearchTitle);

            GetProductsComponentFromJson componentDescFromJson = new GetProductsComponentFromJson();
            List<Map> resltSearchDescList = componentDescFromJson.getComponent(MainActivity.this, jsonObjSearchDescription);


            List<Map> totalSearchList =new ArrayList<>();
            totalSearchList.addAll(resltSearchTitleList);
            totalSearchList.addAll(resltSearchDescList);



            RVAdapter adapter = new RVAdapter(totalSearchList,MainActivity.this , isUrlDummy);
            rvSearch.setAdapter(adapter);
            rvSearch.setVisibility(View.VISIBLE);
            /*for (int i=0; i<totalSearchList.size(); i++)
            {
                totalSearchList.remove(i);
                notifyAll();
            }*/

          }

    }


   TextView tvusername;
   Button btnHome, btFruits, btVeg, btHelp, btContact, btnPrevOrders, btnCustCat, btFaq;
    ImageView imguser , userSetting;
    LinearLayout layoutStoreList;

    //userSetting the details of user in the drawer
    void setUserInDrawer(JSONObject objuser)
    {
        tvusername = (TextView) findViewById(R.id.tvUserName);
        tvusername.setText(objuser.optString("nameOfInstitution"));
        imguser = (ImageView) findViewById(R.id.imguser);

        int loader = R.drawable.tomato;
        ImageLoader imgLoader = new ImageLoader(MainActivity.this);
        imgLoader.DisplayImage("http://www.mive.in/" + objuser.optString("profilephotourl").toString(), loader, imguser);



        layoutStoreList = (LinearLayout) findViewById(R.id.storelistcontainer);
        InflateStoresintoDrawer inflateStoresintoDrawer = new InflateStoresintoDrawer();
        inflateStoresintoDrawer.inflateStoreTabs(MainActivity.this, layoutStoreList, JSONDTO.getInstance().getJsonUser());

        btnCustCat= (Button) findViewById(R.id.btCustCat);
        btnCustCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rvSearch.setVisibility(View.GONE);

                if (fragmentFAQ != null) {
                    Fragment fr = fragmentFAQ;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if (fragmenthelp != null) {
                    Fragment fr = fragmenthelp;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if (fragmentCstm != null) {
                    Fragment fr = fragmentCstm;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if (fragmentContact != null) {
                    Fragment fr = fragmentContact;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }

                // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                viewPager.setCurrentItem(3);

                mDrawerLayout.closeDrawers();

            }
        });


        btFruits= (Button) findViewById(R.id.btFruits);
        btFruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvSearch.setVisibility(View.GONE);
                if(fragmenthelp !=null)
                {
                    Fragment fr=fragmenthelp;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if(fragmentCstm !=null)
                {
                    Fragment fr=fragmentCstm;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }

                if(fragmentContact !=null)
                {
                    Fragment fr=fragmentContact;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if(fragmentFAQ !=null)
                {
                    Fragment fr=fragmentFAQ;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
              //  actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                viewPager.setCurrentItem(2);

                mDrawerLayout.closeDrawers();
            }
        });

        btVeg= (Button) findViewById(R.id.btVegetables);
        btVeg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvSearch.setVisibility(View.GONE);
                if(fragmenthelp !=null)
                {
                    Fragment fr=fragmenthelp;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if(fragmentContact !=null)
                {
                    Fragment fr=fragmentContact;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if(fragmentFAQ !=null)
                {
                    Fragment fr=fragmentFAQ;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
               // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                viewPager.setCurrentItem(1);

                mDrawerLayout.closeDrawers();     }
        });


/*
        btElse= (Button) findViewById(R.id.btelse);
        btElse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rvSearch.setVisibility(View.GONE);}
        });*/
        btnInvoiceSubmit = (Button) findViewById(R.id.btInvoiceupload);
        btnInvoiceSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvSearch.setVisibility(View.GONE);

                if(fragmentFAQ !=null)
                {
                    Fragment fr=fragmentFAQ;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if(fragmentContact !=null)
                {
                    Fragment fr=fragmentContact;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if(fragmenthelp !=null)
                {
                    Fragment fr=fragmenthelp;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                Intent intent =new Intent(MainActivity.this, InvoiceUploadActivity.class);
                mDrawerLayout.closeDrawers();
                SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
                int restoreduserid = prefs.getInt("userId", 0);
                intent.putExtra("userId", restoreduserid);
                intent.putExtra("dummycartId", JSONDTO.getInstance().getJsonUser().optJSONObject("dummycart").optString("dummycart_id"));
                intent.putExtra("sellerId", sellerId);
                mDrawerLayout.closeDrawers();
                startActivity(intent);

            }
        });


        btnPrevOrders = (Button) findViewById(R.id.btPreviousOrders);
        btnPrevOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvSearch.setVisibility(View.GONE);

                if(fragmentFAQ !=null)
                {
                    Fragment fr=fragmentFAQ;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if(fragmentContact !=null)
                {
                    Fragment fr=fragmentContact;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if(fragmenthelp !=null)
                {
                    Fragment fr=fragmenthelp;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }

                Intent intent =new Intent(MainActivity.this, PreviousOrders.class);
                if(isUrlDummy)
                    intent = new Intent(MainActivity.this, PreviousDummyOrders.class);


                mDrawerLayout.closeDrawers();
                SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
                int restoreduserid = prefs.getInt("userId", 0);
                intent.putExtra("userId", restoreduserid);
                intent.putExtra("sortBy", "date");
                intent.putExtra("paymentFilter", "all");
                mDrawerLayout.closeDrawers();
                startActivity(intent);

            }
        });



        btHelp= (Button) findViewById(R.id.btHelp);
        btHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvSearch.setVisibility(View.GONE);
                if (fragmenthelp != null) {
                    Fragment fr = fragmenthelp;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if(fragmentContact !=null)
                {
                    Fragment fr=fragmentContact;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if(fragmentFAQ !=null)
                {
                    Fragment fr=fragmentFAQ;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                fragmenthelp = new HelpNSupport();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.mainframe, fragmenthelp).commit();
                rvProducts.setVisibility(View.GONE);
                mDrawerLayout.closeDrawers();
                //..
              //  actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

            }
        });

        btContact= (Button) findViewById(R.id.btContact);
        btContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvSearch.setVisibility(View.GONE);
                if(fragmentContact !=null)
                {
                    Fragment fr=fragmentContact;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if(fragmenthelp !=null)
                {
                    Fragment fr=fragmenthelp;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                if(fragmentFAQ !=null)
                {
                    Fragment fr=fragmentFAQ;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }

                fragmentContact = new ContactFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.mainframe, fragmentContact).commit();
                rvProducts.setVisibility(View.GONE);
                mDrawerLayout.closeDrawers();
               // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);



            }
        });

        btFaq = (Button) findViewById(R.id.btFAQ);
        btFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvSearch.setVisibility(View.GONE);
                if (fragmenthelp != null) {
                    Fragment fr = fragmenthelp;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }

                if(fragmentFAQ !=null)
                {
                    Fragment fr=fragmentFAQ;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }

                if(fragmentContact !=null)
                {
                    Fragment fr=fragmentContact;
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.remove(fr).commit();
                }
                fragmentFAQ = new FAQFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.mainframe, fragmentFAQ).commit();
                rvProducts.setVisibility(View.GONE);
                mDrawerLayout.closeDrawers();
               // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);


            }
        });
        userSetting = (ImageView) findViewById(R.id.setting);
        userSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_logout:
                                SharedPreferences.Editor editor = getSharedPreferences("userIdPref", MODE_PRIVATE).edit();
                                editor.putInt("userId", 0);
                                editor.commit();

                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();

            }
        });

    }



    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();

        /*if(vpContainer.getVisibility() == View.GONE)
        {vpContainer.setVisibility(View.VISIBLE);
            tvNoreslt.setVisibility(View.GONE);
           // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        rvSearch.setVisibility(View.GONE);


            rvSearch.removeAllViews();
        }
        else {
            finish();
        }*/
        finish();
    }
}
