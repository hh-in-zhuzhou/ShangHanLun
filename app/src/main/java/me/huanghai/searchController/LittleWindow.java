package me.huanghai.searchController;

import android.app.Fragment;
import android.app.FragmentManager;

/**
 * Created by hh on 16/3/25.
 */
public abstract class LittleWindow extends Fragment {
    public abstract String getSearchString();

    public abstract void show(FragmentManager manager);

    public abstract void dismiss();
}
