package in.mive.app.savedstates;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by shubham on 8/21/2015.
 */
public class JSONDTO {

    private static  JSONDTO jsondto = new JSONDTO();
    private JSONDTO(){}

    private JSONObject jsonProductscat1 = null;

    public JSONObject getJsonProductscat2() {
        return jsonProductscat2;
    }

    public void setJsonProductscat2(JSONObject jsonProductscat2) {
        this.jsonProductscat2 = jsonProductscat2;
    }

    public JSONObject getJsonProductscat3() {
        return jsonProductscat3;
    }

    public void setJsonProductscat3(JSONObject jsonProductscat3) {
        this.jsonProductscat3 = jsonProductscat3;
    }

    private JSONObject jsonProductscat2 = null;
    private JSONObject jsonProductscat3 = null;

    public JSONObject getJsonCart() {
        return jsonCart;
    }

    public void setJsonCart(JSONObject jsonCart) {
        this.jsonCart = jsonCart;
    }

    private JSONObject jsonCart = null;
    private JSONObject jsonUser = null;

    public static JSONDTO getInstance()
    {
        return jsondto;
    }


    public JSONObject getJsonProductscat1() {
        return jsonProductscat1;
    }

    public void setJsonProductscat1(JSONObject jsonProductscat1) {
        this.jsonProductscat1 = jsonProductscat1;
        Log.e("user", jsonProductscat1.toString());}

    public JSONObject getJsonUser() {
        return jsonUser;
    }

    public void setJsonUser(JSONObject jsonUser) {
        this.jsonUser = jsonUser;
        if(jsonUser != null)
            Log.e("user", jsonUser.toString());
    }


}
