package in.mive.app.savedstates;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shubham on 11/24/2015.
 */
public class SavedOrderId
{
    private static SavedOrderId savedOrderId = new SavedOrderId();
    private SavedOrderId(){}

    public List<HashMap<String, String>> getListOfOrderId() {
        return listOfOrderId;
    }

    public void setListOfOrderId(List<HashMap<String, String>> listOfOrderId) {
        Log.e("Saved orderid", listOfOrderId.toString());
        this.listOfOrderId = listOfOrderId;
    }

    private List<HashMap<String, String >> listOfOrderId = new ArrayList<>();

    public static SavedOrderId getInstance()
    {
        return savedOrderId;
    }
}
