package in.mive.app.imageupload;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import in.mive.app.activitynew.OptionSelect;
import in.mive.app.savedstates.JSONDTO;


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
    private boolean isTotalPicked;
    private int orientation;
    private Bitmap rotatedBitmap;
    private String sellerName;
    private EditText ettotal;
    TextView paid, unpaid;
    String paymentStatus = "paid";
    private String toatlprice;
    ImageView imageViewBckHome;
    TextView titleActnBar;
    FrameLayout layoutImgCntnr;
    ImageView imageViewInvoiceUpload;
    String totalOfInvoice = "0.0";
    private String invoiceonly;

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
        layoutImgCntnr = (FrameLayout) findViewById(R.id.frameImageContainer);
        imageViewBckHome = (ImageView) findViewById(R.id.imgbckHome);
        imageViewBckHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleActnBar = (TextView) findViewById(R.id.titleActionBar);
        imageViewInvoiceUpload = (ImageView) findViewById(R.id.imageViewInvoiceUpload);



		// Changing action bar background color

		// Receiving the data from previous activity
		Intent i = getIntent();
        SharedPreferences prefs = getSharedPreferences("userIdPref", Context.MODE_PRIVATE);
        int restoreduserid = prefs.getInt("userId", 0);
		// image or video path that is captured in previous activity
		filePath = i.getStringExtra("filePath");

        sellerId = i.getStringExtra("sellerId");
        dummyCartId = i.getStringExtra("dummycartId");
        userId = ""+restoreduserid;


        sellerName = i.getStringExtra("sellerName");
        toatlprice = i.getStringExtra("price");
        invoiceonly = i.getStringExtra("invoiceOnly");

        if(toatlprice != null)
        {
            ettotal.setText(toatlprice);
        }

        titleActnBar.setText(sellerName);


		// boolean flag to identify the media type, image or video
		boolean isImage = i.getBooleanExtra("isImage", false);


        imageViewInvoiceUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadActivity.this, InvoiceUploadActivity.class);
                // intent.putExtra("catId", catId);
                SharedPreferences prefs = getSharedPreferences("userIdPref", Context.MODE_PRIVATE);
                int restoreduserid = prefs.getInt("userId", 0);
                intent.putExtra("userId", restoreduserid);
                intent.putExtra("dummycartId", JSONDTO.getInstance().getJsonUser().optJSONObject("dummycart").optString("dummycart_id"));

                intent.putExtra("sellername", sellerName);
                intent.putExtra("sellerId", "" + sellerId);
                intent.putExtra("urlDummy", true);
                intent.putExtra("invoiceOnly", invoiceonly);
                startActivity(intent);
            }
        });


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
           // layoutImgCntnr.setVisibility(View.GONE);
			/*Toast.makeText(getApplicationContext(),
					"Sorry, file path is missing!", Toast.LENGTH_LONG).show();*/
		}

		btnUpload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// uploading the file to server

                if(ettotal.getText().length() < 1)
                {Toast.makeText(UploadActivity.this, "No Amount Added",Toast.LENGTH_SHORT ).show();
                    return;}

                btnUpload.setEnabled(false);
                btnUpload.setText("Uploading...");
                orderDate.setEnabled(false);
				new UploadFileToServer().execute();
			}
		});


        orderDate = (TextView) findViewById(R.id.tvDate);


        orderMsg = (EditText) findViewById(R.id.etOrderMsg);
        getTodaysDate(orderDate);

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
                paid.setBackgroundColor(Color.parseColor("#e8e8e8"));
                paid.setTextColor(Color.BLACK);

                unpaid.setBackgroundResource(R.drawable.greyselectorborder);
                unpaid.setTextColor(Color.parseColor("#e8e8e8"));
            }
        });


        unpaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentStatus = "unpaid";
                unpaid.setBackgroundColor(Color.parseColor("#e8e8e8"));
                unpaid.setTextColor(Color.BLACK);

                paid.setBackgroundResource(R.drawable.greyselectorborder);
                paid.setTextColor(Color.parseColor("#e8e8e8"));
            }
        });
        //by default unpaid
        unpaid.performClick();

	}


    void getTodaysDate(TextView orderDate)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        //get current date time with Date()
        Date date = new Date();
        System.out.println(dateFormat.format(date));

        //get current date time with Calendar()
        Calendar cal = Calendar.getInstance();
        System.out.println(dateFormat.format(cal.getTime()));
        orderDate.setText(dateFormat.format(cal.getTime()));
        dateSelected = dateFormat.format(cal.getTime());

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
           // matrix.postRotate(90);
            rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            imgPreview.setImageBitmap(rotatedBitmap);
            imgPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UploadActivity.this, FullscreenInvoice.class);
                    intent.putExtra("filepath", filePath);
                    startActivity(intent);
                }
            });
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

        String date = ""+year+"-"+monthfinal+"-"+datefinal;

        dateSelected = date;

       orderDate.setText(date);

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



				// Extra parameters if you want to pass to server
				entity.addPart("sellerId", new StringBody(""+3));
				entity.addPart("dummycartId", new StringBody(""+2));
                entity.addPart("userId", new StringBody(""+1));
                entity.addPart("deliveryTime", new StringBody(dateSelected));
                if(filePath != null)
                {
                    File sourceFile = new File(filePath);
                    entity.addPart("image", new FileBody(sourceFile));
                }

                entity.addPart("invoiceonly", new StringBody(invoiceonly));

                if(orderMsg.getText() != null && orderMsg.getText().length() > 0)
                   entity.addPart("orderMsg", new StringBody(orderMsg.getText().toString()));

                else
                    entity.addPart("orderMsg", new StringBody("No message"));

                entity.addPart("payment", new StringBody(paymentStatus));
                if(ettotal.getText() != null)
                  totalOfInvoice = ettotal.getText().toString();
                entity.addPart("total", new StringBody(totalOfInvoice));


                Log.e("dummycartId", dummyCartId);
				Log.e("userId", userId);
				Log.e("sellerId", sellerId);
                Log.e("deliveryTime", dateSelected);
                Log.e("orderMsg", orderMsg.getText().toString());
                Log.e("invoiceonly", invoiceonly);
                Log.e("payment", paymentStatus);
                Log.e("total", totalOfInvoice);
                if(filePath != null)
                Log.e("image", filePath);


                totalSize = entity.getContentLength();
                Log.e("entity length", ""+entity.getContentLength());

                httppost.setEntity(entity);

				// Making server call
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();

                Log.e("respnse invoice", response.toString());
                Log.e("response complete", r_entity.toString());


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

            Intent intent = new Intent(UploadActivity.this, OptionSelect.class);
            intent.putExtra("id", Integer.parseInt(userId));
            startActivity(intent);

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