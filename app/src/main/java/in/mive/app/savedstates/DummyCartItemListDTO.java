package in.mive.app.savedstates;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by shubham on 8/21/2015.
 */
public class DummyCartItemListDTO {

    private  static DummyCartItemListDTO itemListDTO = new DummyCartItemListDTO();
    private DummyCartItemListDTO(){}

    public List<HashMap> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<HashMap> itemlist) {
        this.itemlist = itemlist;
        Log.e("list cart ", itemlist.toString());

    }

    private List<HashMap> itemlist=new ArrayList<HashMap>();

    public static DummyCartItemListDTO getInstance()
    {
        return itemListDTO;
    }



}
