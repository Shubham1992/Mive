package in.mive.app.imageupload;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.mive.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;


public class UploadActivity extends Activity implements DatePickerDialog.OnDateSetListener, com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {
	// LogCat tag
	private static final String TAG = InvoiceUploadActivity.class.getSimpleName();

	private ProgressBar progressBar;
	private String filePath = null;
	private TextView txtPercentage;
	private ImageView imgPreview;

	private Button btnUpload;
	long totalSize = 0;
    String sellerId;
    String userId;
    String dummyCartId;
    TextView orderDate;
    EditText orderMsg;
    private String dateSelected;
    private boolean isDatePicked;
    private int orientation;
    private Bitmap rotatedBitmap;
    private String sellerName;
    private EditText ettotal;
    TextView paid, unpaid;
    String paymentStatus = "paid";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		txtPercentage = (TextView) findViewById(R.id.txtPercentage);
		btnUpload = (Button) findViewById(R.id.btnUpload);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		imgPreview = (ImageView) findViewById(R.id.imgPreview);
        ettotal = (EditText) findViewById(R.id.ettotal);
        paid = (TextView) findViewById(R.id.paid);
        unpaid = (TextView) findViewById(R.id.unpaid);

		// Changing action bar background color
		ActionBar actionBar= getActionBar();

		// Receiving the data from previous activity
		Intent i = getIntent();

		// image or video path that is captured in previous activity
		filePath = i.getStringExtra("filePath");

        sellerId = i.getStringExtra("sellerId");
        dummyCartId = i.getStringExtra("dummycartId");
        userId = i.getStringExtra("userId");
        sellerName = i.getStringExtra("sellerName");
        actionBar.setTitle(sellerName);
		// boolean flag to identify the media type, image or video
		boolean isImage = i.getBooleanExtra("isImage", true);

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

		if (filePath != null) {
			// Displaying the image or video on the screen
			previewMedia(isImage);
		} else {
			Toast.makeText(getApplicationContext(),
					"Sorry, file path is missing!", Toast.LENGTH_LONG).show();
		}

		btnUpload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// uploading the file to server

                if(isDatePicked != true)
                    return;

                btnUpload.setEnabled(false);
                btnUpload.setText("Uploading...");
                orderDate.setEnabled(false);
				new UploadFileToServer().execute();
			}
		});
        orderDate = (TextView) findViewById(R.id.tvDate);
        orderMsg = (EditText) findViewById(R.id.etOrderMsg);
        orderDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd
                        = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                        UploadActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                //dpd.show(OrderActivity.this, "Datepickerdialog");
                dpd.show(getFragmentManager(), "Pick Date");
            }
        });

        paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentStatus = "paid";
                paid.setBackgroundColor(Color.parseColor("#21bdba"));
                paid.setTextColor(Color.WHITE);

                unpaid.setBackgroundResource(R.drawable.dayselectorborder);
                unpaid.setTextColor(Color.parseColor("#21bdba"));
            }
        });


        unpaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentStatus = "unpaid";
                unpaid.setBackgroundColor(Color.parseColor("#21bdba"));
                unpaid.setTextColor(Color.WHITE);

                paid.setBackgroundResource(R.drawable.dayselectorborder);
                paid.setTextColor(Color.parseColor("#21bdba"));
            }
        });
        //by default unpaid
        unpaid.performClick();

	}

	/**
	 * Displaying captured image/video on the screen
	 * */
	private void previewMedia(boolean isImage)  {
		// Checking whether captured media is image or video
		if (isImage) {
			imgPreview.setVisibility(View.VISIBLE);

			// bimatp factory
			BitmapFactory.Options options = new BitmapFactory.Options();

			// down sizing image as it throws OutOfMemory Exception for larger
			// images
			options.inSampleSize = 8;

			final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            ExifInterface exif = null;
            try {
                exif = new ExifInterface(filePath);
            } catch (IOException e) {
                e.printStackTrace();
                imgPreview.setImageBitmap(bitmap);
                return;
            }
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            imgPreview.setImageBitmap(rotatedBitmap);
        }
        else
        {
			imgPreview.setVisibility(View.GONE);

		}
	}



    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
    {

        String datefinal = null, monthfinal = null, yearfinal;
        monthOfYear = monthOfYear+1;
        if(dayOfMonth <10)
            datefinal = "0"+dayOfMonth;
        else
            datefinal = ""+dayOfMonth;

        if(monthOfYear < 10)
            monthfinal = "0"+monthOfYear;

        else
        monthfinal = ""+monthOfYear;

        String date = ""+year+"-"+datefinal+"-"+monthfinal;

        dateSelected = date;



       orderDate.setText(date);
        isDatePicked=true;
    }



    /**
	 * Uploading the file to server
	 * */
	private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
		@Override
		protected void onPreExecute() {
			// setting progress bar to zero
			progressBar.setProgress(0);
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			// Making progress bar visible
			progressBar.setVisibility(View.VISIBLE);

			// updating progress bar value
			progressBar.setProgress(progress[0]);

			// updating percentage value
			 txtPercentage.setText(String.valueOf(progress[0]) + "%");
             txtPercentage.setAlpha(0.5F);
		}

		@Override
		protected String doInBackground(Void... params) {
			return uploadFile();
		}

		@SuppressWarnings("deprecation")
		private String uploadFile() {
			String responseString = null;

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

			try {
				AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
						new AndroidMultiPartEntity.ProgressListener() {

							@Override
							public void transferred(long num) {
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});

				File sourceFile = new File(filePath);

				// Adding file data to http body
				entity.addPart("image", new FileBody(sourceFile));

				// Extra parameters if you want to pass to server
				entity.addPart("sellerId", new StringBody(sellerId));
				entity.addPart("dummycartId", new StringBody(dummyCartId));
                entity.addPart("userId", new StringBody(userId));
                entity.addPart("deliveryTime", new StringBody(dateSelected));

                if(orderMsg.getText() != null && orderMsg.getText().length() > 0)
                   entity.addPart("orderMsg", new StringBody(orderMsg.getText().toString()));

                else
                    entity.addPart("orderMsg", new StringBody("No message"));

                entity.addPart("payment", new StringBody(paymentStatus));
                entity.addPart("total", new StringBody(ettotal.getText().toString()));



                Log.e("dummycartid", dummyCartId);
				Log.e("userId", userId);
				Log.e("sellerId", sellerId);
                Log.e("date", dateSelected);
                Log.e("ordermsg", orderMsg.getText().toString());


				Log.e("makedummyorderparam", entity.toString());


                totalSize = entity.getContentLength();
				httppost.setEntity(entity);

				// Making server call
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					// Server response
					responseString = EntityUtils.toString(r_entity);
				} else {
					responseString = "Error occurred! Http Status Code: "
							+ statusCode;
				}

			} catch (ClientProtocolException e) {
				responseString = e.toString();
			} catch (IOException e) {
				responseString = e.toString();
			}

			return responseString;

		}

		@Override
		protected void onPostExecute(String result) {
			Log.e(TAG, "Response from server: " + result);

			// showing the server response in an alert dialog
			showAlert(result);

            btnUpload.setEnabled(true);
            orderDate.setEnabled(true);
            JSONObject jsonObject = null;
            try {

                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Toast.makeText(UploadActivity.this, "Response from server: " + result, Toast.LENGTH_SHORT).show();
            finish();

			super.onPostExecute(result);
		}

	}

	/**
	 * Method to show alert dialog
	 * */
	private void showAlert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setTitle("Response from Servers")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// do nothing
						finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

}