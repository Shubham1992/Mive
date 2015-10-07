package in.mive.app.savedstates;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shubham on 8/21/2015.
 */
public class UpdateItemListDTO {

    private  static UpdateItemListDTO itemListDTO = new UpdateItemListDTO();
    private UpdateItemListDTO(){}

    public List<HashMap> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<HashMap> itemlist) {
        this.itemlist = itemlist;
        Log.e("update list cart", itemlist.toString());

    }

    private List<HashMap> itemlist=new ArrayList<HashMap>();

    public static UpdateItemListDTO getInstance()
    {
        return itemListDTO;
    }



}
