package me.huanghai.searchController;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * Created by hh on 16/3/25.
 */
public class LittleWindow extends Fragment {
    protected String searchText;

    public String getSearchString() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getTagName() {
        return "littleWindow";
    }

    public void show(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, getTagName());
        ft.addToBackStack(getTagName());
        ft.commit();

    }

    public void dismiss() {
        getFragmentManager().popBackStack();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();
    }
}
