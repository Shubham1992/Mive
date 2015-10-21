package in.mive.app.savedstates;

import org.json.JSONObject;

/**
 * Created by Shubham on 10/18/2015.
 */
public class SavedJSONUser {
   private static  SavedJSONUser savedJSONUser = new SavedJSONUser();

    public JSONObject getObj() {
        return obj;
    }

    public void setObj(JSONObject obj) {
        this.obj = obj;
    }

    private JSONObject obj;
    private SavedJSONUser(){}

    public static SavedJSONUser getInstance()
    {
        return  savedJSONUser;
    }

}
