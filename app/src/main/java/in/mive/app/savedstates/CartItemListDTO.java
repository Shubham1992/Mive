package in.mive.app.savedstates;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shubham on 8/21/2015.
 */
public class CartItemListDTO {

    private  static CartItemListDTO itemListDTO = new CartItemListDTO();
    private CartItemListDTO(){}

    public List<HashMap> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<HashMap> itemlist) {
        this.itemlist = itemlist;
        Log.e("list cart ", itemlist.toString());

    }

    private List<HashMap> itemlist=new ArrayList<HashMap>();

    public static CartItemListDTO getInstance()
    {
        return itemListDTO;
    }



}
