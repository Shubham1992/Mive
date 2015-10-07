package in.mive.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.mive.R;

import in.mive.app.activities.MainActivity;

/**
 * Created by Shubham on 10/7/2015.
 */
public class Stores extends Activity {

LinearLayout layoutStore1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stores);

        layoutStore1 = (LinearLayout) findViewById(R.id.store1);
        layoutStore1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Stores.this , MainActivity.class);

                startActivity(intent);

            }
        });
    }
}
