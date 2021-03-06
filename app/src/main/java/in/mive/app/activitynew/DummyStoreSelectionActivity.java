package in.mive.app.activitynew;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.mive.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import in.mive.app.activities.CartActivity;
import in.mive.app.activities.ContactFragment;
import in.mive.app.activities.FAQFragment;
import in.mive.app.activities.HelpNSupport;
import in.mive.app.activities.LoginActivity;
import in.mive.app.activities.PaymentHistory;
import in.mive.app.activities.PreviousDummyOrders;
import in.mive.app.activities.PrivacyPolicyFragment;
import in.mive.app.helperclasses.ServiceHandler;
import in.mive.app.imageloader.ImageLoader;
import in.mive.app.layouthelper.InflateDummyStores;
import in.mive.app.layouthelper.InflateStoresintoDrawer;
import in.mive.app.savedstates.ButtonDTO;
import in.mive.app.savedstates.JSONDTO;
import in.mive.app.savedstates.SavedSellerIds;

/**
 * Created by Shubham on 10/15/2015.
 */
public class DummyStoreSelectionActivity extends Activity {
    RecyclerView rvStores;

    Button btnCrt;

    ViewGroup layoutContainerstore;
    ImageView imgHomeBck;

    TextView tvTitleAppbar;
    private int userId;
    private Intent intent;
    private Fragment fragmenthelp = null, fragmentCstm = null, fragmentContact= null, fragmentFAQ = null;
    private JSONObject jsonObjuser;
    TextView tvusername;
    Button btnHome, btFruits, btVeg, btHelp, btContact, btnPrevOrders, btnCustCat, btFaq;
    ImageView imguser ;TextView userSetting;
    LinearLayout layoutStoreList;
    private Button btnInvoiceSubmit;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private AlertDialog progressDialog;
    private JSONObject sellerIds;
    private Button btnPaymntHistry;
    private Button btPrivPolicy;
    private Fragment fragmentPrivPol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_dynamic);
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);
        imgHomeBck = (ImageView) findViewById(R.id.imgbckHome);
        imgHomeBck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        layoutContainerstore = (ViewGroup) findViewById(R.id.cntainerStore);
        tvTitleAppbar = (TextView) findViewById(R.id.tvHeadingAppbar);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        tvTitleAppbar.setTypeface(custom_font);
        btnCrt = (Button) findViewById(R.id.btnCrt);
        btnCrt.setEnabled(false);
        btnCrt.setAlpha(.8F);
        btnCrt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ItemListDTO.getInstance().setProductMap(new ArrayList<Map>());
                ButtonDTO.getInstance().setBtn(btnCrt);
                Intent intent1 = new Intent(DummyStoreSelectionActivity.this, CartActivity.class);
                startActivity(intent1);
            }
        });
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

        intent = getIntent();
        userId= intent.getIntExtra("id", 0);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(DummyStoreSelectionActivity.this, mDrawerLayout,
                R.drawable.drawericon, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle("Mive");

            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle("Mive");

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);




        new GetData().execute();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


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


    private class GetData extends AsyncTask<Void, Void, Void>
    {

        private String urlUser="http://www.mive.in/api/user/";
        private String jsonStrUser;
        //private String urlCart = "http://www.mive.in/api/cart/cartitems/"+id+"/?format=json";



        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new SpotsDialog(DummyStoreSelectionActivity.this);
            progressDialog.setMessage("Loading..");
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            Log.e("inside", "service handler");


            sh = new ServiceHandler();
            jsonStrUser = sh.makeServiceCall(urlUser + userId, ServiceHandler.GET);




            //Log.d("Response: ", "> " + jsonStrcat1);
            Log.d("Response User: ", "> " + jsonStrUser);
            //          Log.d("Response cart: ", "> " + jsonStrCart);





            return null;
        }
        List<Map> resultListcat1;

        @Override
        protected void onPostExecute(Void result) {

            if(jsonStrUser == null)

            {
                if(progressDialog.isShowing())
                {
                    progressDialog.dismiss();
                }
                buildDialog(DummyStoreSelectionActivity.this).show();
                return;
            }

            if (jsonStrUser != null) {
                try {


                    jsonObjuser = new JSONObject(jsonStrUser);
                    Log.e("json user dmystre", jsonObjuser.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the user");
            }


            JSONDTO.getInstance().setJsonUser(jsonObjuser);

            setUserInDrawer(jsonObjuser);

            setSellerIds(jsonObjuser);
            new GetDataSellerNames().execute();

        }

    }

    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Couldn't load data.");
        builder.setMessage("Check internet connection");

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                recreate();
            }
        });

        return builder;
    }

    void setUserInDrawer(JSONObject objuser)
    {
        if(objuser ==  null)
        {
            Toast.makeText(DummyStoreSelectionActivity.this, "Couldn't load data. Retry", Toast.LENGTH_SHORT).show();
            return;
        }

        tvusername = (TextView) findViewById(R.id.tvUserName);
        tvusername.setText(objuser.optString("nameOfInstitution"));
        imguser = (ImageView) findViewById(R.id.imguser);

        int loader = R.drawable.tomato;
        ImageLoader imgLoader = new ImageLoader(DummyStoreSelectionActivity.this);
        imgLoader.DisplayImage("http://www.mive.in/" + objuser.optString("profilephotourl").toString(), loader, imguser);



        layoutStoreList = (LinearLayout) findViewById(R.id.storelistcontainer);
        InflateStoresintoDrawer inflateStoresintoDrawer = new InflateStoresintoDrawer();
        inflateStoresintoDrawer.inflateStoreTabs(DummyStoreSelectionActivity.this, layoutStoreList, JSONDTO.getInstance().getJsonUser());







/*
        btElse= (Button) findViewById(R.id.btelse);
        btElse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                rvSearch.setVisibility(View.GONE);}
        });*/

        btnHome = (Button) findViewById(R.id.btHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                clearFragments();
                layoutContainerstore.setVisibility(View.VISIBLE);


                mDrawerLayout.closeDrawers();


            }
        });


        btnPrevOrders = (Button) findViewById(R.id.btPreviousOrders);
        btnPrevOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearFragments();
                Intent  intent = new Intent(DummyStoreSelectionActivity.this, PreviousDummyOrders.class);

                mDrawerLayout.closeDrawers();
                SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
                int restoreduserid = prefs.getInt("userId", 0);
                intent.putExtra("userId", restoreduserid);
                intent.putExtra("sortBy", "date");
                intent.putExtra("paymentFilter", "all");

                startActivity(intent);

            }
        });

        btnPaymntHistry = (Button) findViewById(R.id.btPaymntHistory);
        btnPaymntHistry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                clearFragments();
                Intent  intent = new Intent(DummyStoreSelectionActivity.this, PaymentHistory.class);

                mDrawerLayout.closeDrawers();
                SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
                int restoreduserid = prefs.getInt("userId", 0);
                intent.putExtra("userId", restoreduserid);
                startActivity(intent);

            }
        });


        btHelp = (Button) findViewById(R.id.btHelp);
        btHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearFragments();
                layoutContainerstore.setVisibility(View.GONE);
                fragmenthelp = new FAQFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.mainframe, fragmenthelp).commit();

                mDrawerLayout.closeDrawers();
                //..
                //  actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

            }
        });

        btContact = (Button) findViewById(R.id.btContact);
        btContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearFragments();
                layoutContainerstore.setVisibility(View.GONE);
                fragmentContact = new ContactFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.mainframe, fragmentContact).commit();

                mDrawerLayout.closeDrawers();
                // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);


            }
        });

        btFaq = (Button) findViewById(R.id.btFAQ);
        btFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              clearFragments();
                layoutContainerstore.setVisibility(View.GONE);
                fragmentFAQ = new FAQFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.mainframe, fragmentFAQ).commit();

                mDrawerLayout.closeDrawers();
                // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);


            }
        });
        btPrivPolicy = (Button) findViewById(R.id.btPrivPolicy);
        btPrivPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clearFragments();
                layoutContainerstore.setVisibility(View.GONE);
                fragmentPrivPol = new PrivacyPolicyFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.mainframe, fragmentPrivPol).commit();

                mDrawerLayout.closeDrawers();
                // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);


            }
        });
        userSetting = (TextView) findViewById(R.id.setting);
        userSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = getSharedPreferences("userIdPref", MODE_PRIVATE).edit();
                editor.putInt("userId", 0);
                editor.commit();

                Intent intent = new Intent(DummyStoreSelectionActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

                /*PopupMenu popupMenu = new PopupMenu(DummyStoreSelectionActivity.this, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_logout:

                                return true;
                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.popup_menu);
                popupMenu.show();*/

            }
        });

    }

    void clearFragments()
    {
        if (fragmenthelp != null) {
            Fragment fr = fragmenthelp;
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(fr).commit();
            fragmenthelp =  null;
        }

        if (fragmentFAQ != null) {
            Fragment fr = fragmentFAQ;
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(fr).commit();
            fragmentFAQ =  null;
        }

        if (fragmentContact != null) {
            Fragment fr = fragmentContact;
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(fr).commit();
            fragmentContact =  null;
        }
        if (fragmentPrivPol != null) {
            Fragment fr = fragmentPrivPol;
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(fr).commit();
            fragmentPrivPol =  null;
        }
    }
    public void setSellerIds(JSONObject jsonObject)
    {

        JSONArray jsonArray = jsonObject.optJSONArray("dummyvendors");

        List<HashMap<String, String>> list = new ArrayList();

        for (int i = 0; i < jsonArray.length(); i++) {

            //get a particular vendor
            JSONObject objCategories = jsonArray.optJSONObject(i);
            JSONObject objectSeller = objCategories.optJSONObject("seller");

            final  String sellerId = objectSeller.optString("seller_id");

            HashMap hashMap = new HashMap();
            hashMap.put("sellerId", sellerId);
            list.add(hashMap);


        }

        SavedSellerIds.getInstance().setList(list);
        SavedSellerIds.getInstance().setListComplete(list);

    }



    private class GetDataSellerNames extends AsyncTask<Void, Void, Void>
    {

        private String urlUser="http://www.mive.in/api/user/";
        private JSONObject jsonObjuser2;
        //private String urlCart = "http://www.mive.in/api/cart/cartitems/"+id+"/?format=json";



        @Override
        protected void onPreExecute() {


            super.onPreExecute();
               }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            Log.e("inside", "service handler");


            sh = new ServiceHandler();
           // String jsonStrUser = sh.makeServiceCall(urlUser + userId, ServiceHandler.GET);







                    jsonObjuser2 = jsonObjuser;





            return null;
        }
        List<Map> resultListcat1;
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(progressDialog.isShowing())
                progressDialog.cancel();
            JSONDTO.getInstance().setJsonUser(jsonObjuser2);

           // new GetCartData().execute();



            InflateDummyStores inflatedummyStores = new InflateDummyStores();
            inflatedummyStores.inflateStoreTabs(DummyStoreSelectionActivity.this,layoutContainerstore, jsonObjuser2);

        }

    }


    @Override
    public void onBackPressed()
    {
        if (fragmenthelp != null)
        {
        Fragment fr = fragmenthelp;
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(fr).commit();
            fragmenthelp= null;
        layoutContainerstore.setVisibility(View.VISIBLE);
            return;
        }

        if (fragmentFAQ != null) {
            Fragment fr = fragmentFAQ;
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(fr).commit();
            layoutContainerstore.setVisibility(View.VISIBLE);
            fragmentFAQ = null;
            return;
        }

        if (fragmentContact != null) {
            Fragment fr = fragmentContact;
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(fr).commit();
            fragmentContact = null;
            layoutContainerstore.setVisibility(View.VISIBLE);
            return;
        }

        if (fragmentPrivPol != null) {
            Fragment fr = fragmentPrivPol;
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.remove(fr).commit();
            fragmentPrivPol = null;
            layoutContainerstore.setVisibility(View.VISIBLE);
            return;
        }

        else if(layoutContainerstore.getVisibility() == View.VISIBLE)
        {
            finish();
        }

    }
}
