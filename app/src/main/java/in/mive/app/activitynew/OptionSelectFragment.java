package in.mive.app.activitynew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mive.R;

/**
 * Created by Shubham on 10/30/2015.
 */
public class OptionSelectFragment extends Fragment {

    FrameLayout layoutMive , layoutInvoice, layoutAnalytics;
    private int userId;
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.option_select, container, false);
        layoutInvoice = (FrameLayout) rootView.findViewById(R.id.invoice);
        layoutInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        return rootView;
    }
}
