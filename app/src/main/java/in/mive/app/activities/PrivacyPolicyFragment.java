package in.mive.app.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mive.R;

/**
 * Created by Shubham on 8/25/2015.
 */
public class PrivacyPolicyFragment extends Fragment {

    TextView privpoltoggle, termcondtoggle;
    LinearLayout privpolcont, termcondcont;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.privacypolicy, container, false);
        privpoltoggle = (TextView) rootView.findViewById(R.id.privpoltoggle);
        termcondtoggle = (TextView) rootView.findViewById(R.id.termcondtoggle);

        privpolcont = (LinearLayout) rootView.findViewById(R.id.privpolcntnr);
        termcondcont = (LinearLayout) rootView.findViewById(R.id.termcondcntnr);

        privpoltoggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(privpolcont.getVisibility() == View.VISIBLE)
                {
                    privpolcont.setVisibility(View.GONE);
                }
                else if(privpolcont.getVisibility() == View.GONE)
                {
                    privpolcont.setVisibility(View.VISIBLE);
                }
            }
        });

        termcondtoggle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(termcondcont.getVisibility() == View.VISIBLE)
                {
                    termcondcont.setVisibility(View.GONE);
                }
                else if(termcondcont.getVisibility() == View.GONE)
                {
                    termcondcont.setVisibility(View.VISIBLE);
                }
            }
        });


        return rootView;
    }

}
