package in.mive.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


import com.mive.R;

/**
 * Created by Shubham on 8/27/2015.
 */
public class SplashActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       /* ImageView img = (ImageView) findViewById(R.id.img);
        TextView tvMive = (TextView)findViewById(R.id.tvMive);*/
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                // close this activity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        }, 3000);


        Animation anim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.zoomin);
        // img.startAnimation(anim);

        Animation animation =AnimationUtils.loadAnimation(SplashActivity.this, R.anim.textmove);
        //tvMive.startAnimation(animation);

    }

    @Override
    public void finish() {
        super.finish();

    }
}
