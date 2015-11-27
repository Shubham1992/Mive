package in.mive.app.savedstates;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shubham on 11/3/2015.
 */
public class SavedSellerIds {
    private static SavedSellerIds savedSellerIds = new SavedSellerIds();

    private SavedSellerIds(){}

    public List<HashMap<String, String>> getList()
    {
        return list;
    }

    public void setList(List<HashMap<String, String>> list)
    {
        this.list = list;
    }

    private List<HashMap<String, String>> list = new ArrayList<>();

    public List<HashMap<String, String>> getListComplete() {
        return listComplete;
    }

    public void setListComplete(List<HashMap<String, String>> listComplete) {
        this.listComplete = listComplete;
    }

    private List<HashMap<String, String>> listComplete = new ArrayList<>();

    public static SavedSellerIds getInstance()
    {
        return  savedSellerIds;
    }

}
