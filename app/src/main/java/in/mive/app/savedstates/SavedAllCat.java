package in.mive.app.savedstates;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by shubham on 8/16/2015.
 */
public class SavedAllCat {

  public  static SavedAllCat savedAllCat =new SavedAllCat();

    private SavedAllCat()
    {

    }
   public static SavedAllCat getobj()
    {
        return savedAllCat;
    }


    public List<Map> getList() {
        return list;
    }

    public void setList(List<Map> list) {
        this.list = list;
        Log.e("list", list.toString());
    }

    private List<Map> list=new ArrayList<Map>();




}
