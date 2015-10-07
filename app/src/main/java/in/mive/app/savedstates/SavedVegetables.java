package in.mive.app.savedstates;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Shubham on 8/25/2015.
 */
public class SavedVegetables {


        public  static SavedVegetables savedVegetables =new SavedVegetables();

        private SavedVegetables()
        {

        }
        public static SavedVegetables getobj()
        {
            return savedVegetables;
        }


        public List<Map> getList() {
            return list;
        }

        public void setList(List<Map> list) {
            this.list = list;
            Log.e("vegetables list", list.toString());
        }

        private List<Map> list=new ArrayList<Map>();




    }


