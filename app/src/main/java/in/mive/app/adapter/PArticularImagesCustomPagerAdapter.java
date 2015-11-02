package in.mive.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.mive.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.mive.app.imageloader.ImageLoader;

public class PArticularImagesCustomPagerAdapter extends PagerAdapter
{

	Context mContext;
	LayoutInflater mLayoutInflater;
	JSONArray jsarr;


	public PArticularImagesCustomPagerAdapter(Context context, JSONArray jsarr) {
		mContext = context;
		mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.jsarr=jsarr;

		Log.e("inside", "adapter");
	}

	@Override
	public int getCount() {
		return jsarr.length();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == ((LinearLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container,final int position) {
		JSONObject imgobj=null;
		try
		{

			imgobj = jsarr.getJSONObject(position);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		String imgUrl=imgobj.optString("geturl");

		Log.e("link",imgUrl);
		View itemView = mLayoutInflater.inflate(R.layout.img_invoice_tab, container, false);

		ImageView imageView = (ImageView) itemView.findViewById(R.id.img_invoice);

		int loaderimg = R.drawable.tomato;
		ImageLoader loader=new ImageLoader(mContext);
			Log.d("Image url", imgUrl);
			loader.DisplayImage("http://www.mive.in/"+imgUrl , loaderimg, imageView);

		container.addView(itemView);

		return itemView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((LinearLayout) object);
	}
}
