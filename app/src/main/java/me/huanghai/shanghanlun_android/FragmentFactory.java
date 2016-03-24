package me.huanghai.shanghanlun_android;

import android.app.Fragment;

public class FragmentFactory {
    public static Fragment getInstanceByIndex(int index) {
        Fragment fragment = null;
        switch (index) {
            case R.id.firstContentTab:
                fragment = new MainFragment();
                break;
            case R.id.fangYaoTab:
                fragment = new FangFragment();
                break;
            case R.id.yaoTab:
                fragment = new YaoFragment();
                break;
            case R.id.unitTab:
                fragment = new UnitFragment();
                break;
            case R.id.settingsTab:
                fragment = new SettingsFragment();
                break;
        }
        return fragment;
    }
}  
