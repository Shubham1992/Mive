package in.mive.app.savedstates;

import android.util.Log;

import org.json.JSONArray;

import java.util.HashMap;

/**
 * Created by Shubham on 10/18/2015.
 */
public class SavedSellerProductsMap {
    private  static SavedSellerProductsMap itemListDTO = new SavedSellerProductsMap();
    private SavedSellerProductsMap(){}

    public HashMap<String, JSONArray> getProductMap() {
        return productMap;
    }

    public void setProductMap(HashMap<String, JSONArray> productMap) {
        this.productMap = productMap;
        Log.e("update list cart", productMap.toString());

    }

    private HashMap<String, JSONArray> productMap =new HashMap<String, JSONArray>();

    public static SavedSellerProductsMap getInstance()
    {
        return itemListDTO;
    }


}
