package in.mive.app.activities;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
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
public class ContactFragment extends Fragment {
    private Button mailusbtn, callusbtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.contact, container, false);
       /* Button btnContct = (Button) rootView.findViewById(R.id.btnContct);
        btnContct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(),"ContactFragment details",Toast.LENGTH_SHORT).show();
            }
        });*/
        mailusbtn = (Button)rootView.findViewById(R.id.mailus);
        callusbtn = (Button)rootView.findViewById(R.id.callus);
        mailusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"admin@mive.in"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject goes here");

                startActivity(Intent.createChooser(intent, ""));
            }
        });
        callusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "09555666095"));
                startActivity(intent);
            }
        });


        return rootView;
    }

}
