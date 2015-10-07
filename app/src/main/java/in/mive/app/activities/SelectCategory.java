package in.mive.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.mive.R;

/**
 * Created by shubham on 8/20/2015.
 */
public class SelectCategory extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_category);
      //  ImageView imgcstm = (ImageView) findViewById(R.id.imgcstm);
        ImageView imgfruits = (ImageView) findViewById(R.id.imgfruits);
        ImageView imgveg = (ImageView) findViewById(R.id.imgveg);
      /*  imgcstm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sentIntent(0);
            }
        });
        imgfruits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sentIntent(1);
            }
        });
        imgveg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sentIntent(2);
            }
        });*/
    }
    void sentIntent(int pos)
    {   Intent intOld = getIntent();
        int id = intOld.getIntExtra("id",0);
        boolean isl0ggedin = intOld.getBooleanExtra("loggedIn",false);
        Log.e("extras " , id +" "+isl0ggedin);

        Intent intent=new Intent(SelectCategory.this, MainActivity.class);
        intent.putExtra("pos",pos);
        intent.putExtra("id",id);
        intent.putExtra("loggedIn",isl0ggedin);
        startActivity(intent);
    }

}
