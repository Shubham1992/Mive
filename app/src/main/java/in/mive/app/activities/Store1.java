package in.mive.app.activities;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mive.R;

/**
 * Created by Shubham on 10/12/2015.
 */
public class Store1 extends FragmentActivity {
    LinearLayout cntnr;
    Button btncart;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.store1);

        cntnr = (LinearLayout) findViewById(R.id.storeContainer);
        btncart = (Button) findViewById(R.id.btnCrt);

        fragment = new Store1Fragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.storeContainer, fragment).commit();
   }
}
