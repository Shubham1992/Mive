package in.mive.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mive.R;

/**
 * Created by Shubham on 11/22/2015.
 */
public class ContactActivity extends Activity{
    ImageView imageViewbck;
    private Button mailusbtn, callusbtn;
    TextView tvfrgtpwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_activity);

        imageViewbck = (ImageView) findViewById(R.id.imgbckHome);
        imageViewbck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvfrgtpwd = (TextView) findViewById(R.id.tvfrgtpwd);
        Intent intent = getIntent();
        String pwdtxt = intent.getStringExtra("frgtpwdtxt");
        if(pwdtxt != null)
        {
            tvfrgtpwd.setText(pwdtxt);
            tvfrgtpwd.setVisibility(View.VISIBLE);
        }
        mailusbtn = (Button)findViewById(R.id.mailus);
        callusbtn = (Button)findViewById(R.id.callus);
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

    }


}
