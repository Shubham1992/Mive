package in.mive.app.savedstates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shubham on 8/21/2015.
 */
public class ItemListDTO {

    private  static  ItemListDTO itemListDTO = new ItemListDTO();
    private ItemListDTO(){}

    public List<Map> getItemlist() {
        return itemlist;
    }

    public void setItemlist(List<Map> itemlist) {
        this.itemlist = itemlist;
    }

    private List<Map> itemlist=new ArrayList<Map>();

    public static ItemListDTO getInstance()
    {
        return itemListDTO;
    }



}
