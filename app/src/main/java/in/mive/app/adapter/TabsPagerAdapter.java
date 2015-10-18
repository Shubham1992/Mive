package in.mive.app.adapter;

import in.mive.app.activities.CustomCatFragment;
import in.mive.app.activities.FruitsFragment;
import in.mive.app.activities.VegetablesFragment;
import in.mive.app.activities.AllCategoryFragment;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	private String[] tabslist = { "All Products", " Vegetables ", "   Fruits   ", " Customized " };
	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public android.support.v4.app.Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new AllCategoryFragment();
		case 1:
			// Movies fragment activity
			return new VegetablesFragment();
		case 2:
			// Games fragment activity
			return new FruitsFragment();
		case 3:
				// Games fragment activity
			return new CustomCatFragment();
		}

		return null;
	}
public CharSequence getPageTitle(int position) { return tabslist[position];}
	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 4;
	}

}
