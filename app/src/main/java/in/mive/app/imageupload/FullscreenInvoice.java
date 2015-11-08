package in.mive.app.imageupload;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mive.R;

/**
 * Created by Shubham on 11/7/2015.
 */
public class FullscreenInvoice extends Activity{
    ImageView imageView ;
    TextView done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_invoice);

        imageView = (ImageView) findViewById(R.id.fullscreenimage);
        done = (TextView) findViewById(R.id.donebtn);
        Intent intent = getIntent();
        String filepath = intent.getStringExtra("filepath");

        // bimatp factory
        BitmapFactory.Options options = new BitmapFactory.Options();

        // down sizing image as it throws OutOfMemory Exception for larger
        // images
        options.inSampleSize = 8;

        final Bitmap bitmap = BitmapFactory.decodeFile(filepath, options);
        imageView.setImageBitmap(bitmap);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
