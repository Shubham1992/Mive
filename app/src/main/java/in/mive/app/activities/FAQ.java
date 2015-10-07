package in.mive.app.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.mive.R;

/**
 * Created by Shubham on 8/25/2015.
 */
public class FAQ extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.help_n_support, container, false);
       /* Button btnContct = (Button) rootView.findViewById(R.id.btnContct);
        btnContct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"ContactFragment details",Toast.LENGTH_SHORT).show();
            }
        });*/
        return rootView;
    }

}
