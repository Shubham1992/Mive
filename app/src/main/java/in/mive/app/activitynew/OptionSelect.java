package in.mive.app.activitynew;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mive.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import in.mive.app.activities.ContactFragment;
import in.mive.app.activities.FAQFragment;
import in.mive.app.activities.HelpNSupport;
import in.mive.app.activities.LoginActivity;
import in.mive.app.activities.PreviousOrders;
import in.mive.app.helperclasses.ServiceHandler;
import in.mive.app.imageloader.ImageLoader;
import in.mive.app.imageupload.InvoiceUploadActivity;
import in.mive.app.layouthelper.InflateStores;
import in.mive.app.layouthelper.InflateStoresintoDrawer;
import in.mive.app.savedstates.JSONDTO;

/**
 * Created by Shubham on 10/30/2015.
 */
public class OptionSelect extends Activity {

    FrameLayout layoutMive , layoutInvoice, layoutAnalytics;
    private int userId;
    private Intent intent;
    private Fragment fragmenthelp = null, fragmentCstm = null, fragmentContact= null, fragmentFAQ = null;
    private JSONObject jsonObjuser;
    TextView tvusername;
    Button btnHome, btFruits, btVeg, btHelp, btContact, btnPrevOrders, btnCustCat, btFaq;
    ImageView imguser , userSetting;
    LinearLayout layoutStoreList;
    private Button btnInvoiceSubmit;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.option_select);

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

        mDrawerToggle = new ActionBarDrawerToggle(OptionSelect.this, mDrawerLayout,
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

        layoutMive = (FrameLayout) findViewById(R.id.miveOrder);
        layoutInvoice = (FrameLayout) findViewById(R.id.invoice);
        layoutAnalytics = (FrameLayout) findViewById(R.id.analytics);
        layoutInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OptionSelect.this, DummyStoreSelectionActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);

            }
        });
        layoutMive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OptionSelect.this, StoreselectionActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
        layoutAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


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

    void setUserInDrawer(JSONObject objuser)
    {
        tvusername = (TextView) findViewById(R.id.tvUserName);
        tvusername.setText(objuser.optString("nameOfInstitution"));
        imguser = (ImageView) findViewById(R.id.imguser);

        int loader = R.drawable.tomato;
        ImageLoader imgLoader = new ImageLoader(OptionSelect.this);
        imgLoader.DisplayImage("http://www.mive.in/" + objuser.optString("profilephotourl").toString(), loader, imguser);



        layoutStoreList = (LinearLayout) findViewById(R.id.storelistcontainer);
        InflateStoresintoDrawer inflateStoresintoDrawer = new InflateStoresintoDrawer();
        inflateStoresintoDrawer.inflateStoreTabs(OptionSelect.this, layoutStoreList, JSONDTO.getInstance().getJsonUser());

        btnCustCat= (Button) findViewById(R.id.btCustCat);
        btnCustCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



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



            }
        });


        btFruits= (Button) findViewById(R.id.btFruits);
        btFruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

            }
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


                       /* if (fragmentFAQ != null) {
                            Fragment fr = fragmentFAQ;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }
                        if (fragmentContact != null) {
                            Fragment fr = fragmentContact;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }
                        if (fragmenthelp != null) {
                            Fragment fr = fragmenthelp;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }
                        Intent intent = new Intent(OptionSelect.this, InvoiceUploadActivity.class);
                        mDrawerLayout.closeDrawers();
                        SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
                        int restoreduserid = prefs.getInt("userId", 0);
                        intent.putExtra("userId", restoreduserid);
                        mDrawerLayout.closeDrawers();
                        startActivity(intent);
*/

                        layoutInvoice.performClick();
                    }
                });


                btnPrevOrders = (Button) findViewById(R.id.btPreviousOrders);
                btnPrevOrders.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (fragmentFAQ != null) {
                            Fragment fr = fragmentFAQ;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }
                        if (fragmentContact != null) {
                            Fragment fr = fragmentContact;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }
                        if (fragmenthelp != null) {
                            Fragment fr = fragmenthelp;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }
                        Intent intent = new Intent(OptionSelect.this, PreviousOrders.class);
                        mDrawerLayout.closeDrawers();
                        SharedPreferences prefs = getSharedPreferences("userIdPref", MODE_PRIVATE);
                        int restoreduserid = prefs.getInt("userId", 0);
                        intent.putExtra("userId", restoreduserid);
                        mDrawerLayout.closeDrawers();
                        startActivity(intent);

                    }
                });


                btHelp = (Button) findViewById(R.id.btHelp);
                btHelp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (fragmenthelp != null) {
                            Fragment fr = fragmenthelp;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }
                        if (fragmentContact != null) {
                            Fragment fr = fragmentContact;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }
                        if (fragmentFAQ != null) {
                            Fragment fr = fragmentFAQ;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }
                        fragmenthelp = new HelpNSupport();
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

                        if (fragmentContact != null) {
                            Fragment fr = fragmentContact;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }
                        if (fragmenthelp != null) {
                            Fragment fr = fragmenthelp;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }
                        if (fragmentFAQ != null) {
                            Fragment fr = fragmentFAQ;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }

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

                        if (fragmenthelp != null) {
                            Fragment fr = fragmenthelp;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }

                        if (fragmentFAQ != null) {
                            Fragment fr = fragmentFAQ;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }

                        if (fragmentContact != null) {
                            Fragment fr = fragmentContact;
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fr).commit();
                        }
                        fragmentFAQ = new FAQFragment();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.add(R.id.mainframe, fragmentFAQ).commit();

                        mDrawerLayout.closeDrawers();
                        // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);


                    }
                });
                userSetting = (ImageView) findViewById(R.id.setting);
                userSetting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        PopupMenu popupMenu = new PopupMenu(OptionSelect.this, view);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()) {
                                    case R.id.item_logout:
                                        SharedPreferences.Editor editor = getSharedPreferences("userIdPref", MODE_PRIVATE).edit();
                                        editor.putInt("userId", 0);
                                        editor.commit();

                                        Intent intent = new Intent(OptionSelect.this, LoginActivity.class);
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


    private class GetData extends AsyncTask<Void, Void, Void>
    {

        private String urlUser="http://www.mive.in/api/user/";
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
            String jsonStrUser = sh.makeServiceCall(urlUser + userId, ServiceHandler.GET);





            //Log.d("Response: ", "> " + jsonStrcat1);
            Log.d("Response User: ", "> " + jsonStrUser);
            //          Log.d("Response cart: ", "> " + jsonStrCart);


            if (jsonStrUser != null) {
                try {

                    jsonObjuser = new JSONObject(jsonStrUser);


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

            JSONDTO.getInstance().setJsonUser(jsonObjuser);

         setUserInDrawer(jsonObjuser);

        }

    }
    }
