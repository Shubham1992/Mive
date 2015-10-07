package in.mive.app;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin-PC on 8/10/2015.
 */


/*{"product_id":1,"name":"tamaatar",
"description":"dfdf","category":"http://www.mive.in/api/category/1/?format=json","popularityIndex":4,"unit":"kg",
"priceType":"custom rates","pricePerUnit":123,"origin":"chomu","maxAvailableUnits":1000,"qualityRemarks":"cc",
"isPerishable":false,"seller":null,"coverphotourl":"media/rees52-4-wheel-robotic-platform_H0Y439P.jpg"},*/


public class GetCustomProductsComponentFromJson
{
	public List<Map> getComponent(Context context, JSONObject jsonObject)
	{List<Map> productList=new ArrayList();

		JSONArray productResults = jsonObject.optJSONArray("results");

		JSONObject jsonProdctList = productResults.optJSONObject(0);
		JSONArray arrProduct = jsonProdctList.optJSONArray("product");
		JSONObject productObj = null;
		Log.e("custom prod", arrProduct.toString());
		for (int i = 0; i < arrProduct.length(); i++)
		{

			try
			{
				productObj = arrProduct.getJSONObject(i);
			} catch (JSONException e)
			{
				e.printStackTrace();
			}

			String productName = productObj.optString("name");
			String productRate = productObj.optString("pricePerUnit");
			String coverPhotoUrl = productObj.optString("coverphotourl");
			int productId = productObj.optInt("product_id");
			int priceperunit =productObj.optInt("pricePerUnit");
			String availableQty = productObj.optString("unit");

			HashMap productMap=new HashMap();
			productMap.put("productName",productName);
			productMap.put("productRate",productRate);
			productMap.put("productImage",coverPhotoUrl);
			productMap.put("productId",productId);
			productMap.put("pricePerUnit",priceperunit);
			productMap.put("availableQty",availableQty);

			productList.add(productMap);
			//Log.e("Product map", productMap.toString());
		}




		return productList;
	}

}
