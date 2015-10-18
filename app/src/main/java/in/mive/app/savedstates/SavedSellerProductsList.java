package in.mive.app.savedstates;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shubham on 10/18/2015.
 */
public class SavedSellerProductsList {
    private  static SavedSellerProductsList itemListDTO = new SavedSellerProductsList();
    private SavedSellerProductsList(){}

    public List<HashMap> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<HashMap> itemlist) {
        this.itemlist = itemlist;
        Log.e("update list cart", itemlist.toString());

    }

    private List<HashMap> itemlist=new ArrayList<HashMap>();

    public static SavedSellerProductsList getInstance()
    {
        return itemListDTO;
    }


}
