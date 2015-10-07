package in.mive.app.savedstates;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Admin-PC on 9/24/2015.
 */
public class SavedTopLayout
{

	private static SavedTopLayout savedTopLayout = new SavedTopLayout();
	private SavedTopLayout(){};

	private ViewGroup vg;

	public static  SavedTopLayout getInstance()
	{
		return savedTopLayout;
	}

	public void setSavedTopLayout(ViewGroup  vg)
	{
		this.vg = vg;
		Log.e("view ", vg.toString());
	}

	public ViewGroup getSavedTopLayout()
	{
		return vg;
	}
}
