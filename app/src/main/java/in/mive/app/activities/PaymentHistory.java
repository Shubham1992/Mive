package in.mive.app.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mive.R;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Shubham on 11/21/2015.
 */
public class PaymentHistory extends Activity {
    RelativeLayout layoutEditablePrevOut;
    TextView tvprvout;
    ImageView imageViewbck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paymenthistory);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(getResources().getColor(R.color.my_statusbar_color));
        }

        imageViewbck = (ImageView) findViewById(R.id.imgbckHome);
        imageViewbck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        layoutEditablePrevOut = (RelativeLayout) findViewById(R.id.editablePrvOut);
        tvprvout = (TextView) findViewById(R.id.tvprvout);
        layoutEditablePrevOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog( tvprvout);
            }
        });


    }

    private void createDialog(final TextView tvprvout) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        /*final EditText edittext = new EditText(PaymentHistory.this);
        edittext.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);*/
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View alrteditview = inflater.inflate(R.layout.edittextalert, null);
        final EditText editText = (EditText) alrteditview.findViewById(R.id.editalert);

        alert.setView(alrteditview);
        alert.setCancelable(true);






        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int whichButton) {
                //What ever you want to do with the value


                String YouEditTextValue = editText.getText().toString();
                if (editText.getText() != null && editText.getText().length() > 0) {
                    tvprvout.setText(NumberFormat.getNumberInstance(new Locale("en", "in")).format(Float.parseFloat(YouEditTextValue)));
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String YouEditTextValue = editText.getText().toString();
                    if (editText.getText() != null && editText.getText().length() > 0) {
                        tvprvout.setText(NumberFormat.getNumberInstance(Locale.UK).format(Float.parseFloat(YouEditTextValue)));

                    }
                    handled = true;
                }
                return handled;
            }
        });

        alert.show();
        (new Handler()).postDelayed(new Runnable() {

            public void run() {
//              ((EditText) findViewById(R.id.et_find)).requestFocus();
//

//              InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//              imm.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT);

                editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                editText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));

            }
        }, 100);


    }
}
