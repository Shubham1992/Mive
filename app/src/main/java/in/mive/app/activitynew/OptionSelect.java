package in.mive.app.activitynew;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.mive.R;

/**
 * Created by Shubham on 10/30/2015.
 */
public class OptionSelect extends Activity {

    FrameLayout layoutMive , layoutInvoice, layoutAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.option_select);

        layoutMive = (FrameLayout) findViewById(R.id.miveOrder);
        layoutInvoice = (FrameLayout) findViewById(R.id.invoice);
        layoutAnalytics = (FrameLayout) findViewById(R.id.analytics);
        layoutInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layoutMive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layoutAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }
}
