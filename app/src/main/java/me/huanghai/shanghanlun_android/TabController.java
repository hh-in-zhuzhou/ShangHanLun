package me.huanghai.shanghanlun_android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.huanghai.searchController.ShowFragment;
import me.huanghai.searchController.SingletonData;

public class TabController extends Activity {
    private FragmentManager fragmentManager;
    private RadioGroup radioGroup;
    // private ViewGroup mContainer;
    // private ViewPager viewPager;
    private static Map<Integer, Integer> fragmentsMap = new HashMap<Integer, Integer>();
    public static List<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
        SingletonData.getInstance().savePreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SingletonData.getInstance().curActivity = this;
    }

    @NonNull
    @Override
    public FragmentManager getFragmentManager() {
        return super.getFragmentManager();
    }

    // @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tabcontroller);

        View v = findViewById(R.id.backgroundmask);
        SingletonData.getInstance().setMask(v);

        // Notes.getInstance().init(this);
        fragments.add(FragmentFactory.getInstanceByIndex(R.id.firstContentTab));
        fragments.add(FragmentFactory.getInstanceByIndex(R.id.fangYaoTab));
        fragments.add(FragmentFactory.getInstanceByIndex(R.id.yaoTab));
        fragments.add(FragmentFactory.getInstanceByIndex(R.id.unitTab));
        fragments.add(FragmentFactory.getInstanceByIndex(R.id.settingsTab));

        fragmentsMap.put(new Integer(R.id.firstContentTab), new Integer(0));
        fragmentsMap.put(new Integer(R.id.fangYaoTab), new Integer(1));
        fragmentsMap.put(new Integer(R.id.yaoTab), new Integer(2));
        fragmentsMap.put(new Integer(R.id.unitTab), new Integer(3));
        fragmentsMap.put(new Integer(R.id.settingsTab), new Integer(4));

        // mContainer = (ViewGroup) findViewById(R.id.content);
        fragmentManager = getFragmentManager();
        radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
        radioGroup.check(R.id.firstContentTab);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = fragments.get(fragmentsMap
                .get(R.id.firstContentTab));
        transaction.replace(R.id.content, fragment);
        transaction.commit();

        radioGroup
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        FragmentTransaction transaction = fragmentManager
                                .beginTransaction();
                        // Fragment fragment = FragmentFactory
                        // .getInstanceByIndex(checkedId);
                        Fragment fragment = fragments.get(fragmentsMap
                                .get(checkedId));
                        transaction.replace(R.id.content, fragment);
                        transaction.commit();
                    }
                });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int index = fragmentManager.getBackStackEntryCount() - 1;
        if (index >= 0) {
            FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(index);
            String tag = entry.getName();
            if (tag != null && (tag.equals("littleWindow") || tag.equals("actionSheet"))) {
                return super.onKeyDown(keyCode, event);
            }
        }


        int fragId = radioGroup.getCheckedRadioButtonId();
        Fragment frag = fragments.get(fragmentsMap.get(fragId));
        if (keyCode == KeyEvent.KEYCODE_BACK && frag instanceof ShowFragment) {

            if (frag instanceof MainFragment && SingletonData.getInstance().isSeeingContextInSearchMode) {
                MainFragment fragment = (MainFragment) frag;
                fragment.goBack();
                return false;
            }

            ShowFragment fragment = (ShowFragment) frag;
            if (fragment.getIsContentOpen()) {
                fragment.setIsContentOpen(false);
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private static void setDefaultUncaughtExceptionHandler() {
        try {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(Thread t, Throwable e) {
//                     logger.error("Uncaught Exception detected in thread {}",
//                     t, e);
                    Log.e("Shanghanlun", "Uncaught Exception detected in thread {}" +
                            t.toString() + e.toString());
                    e.printStackTrace();
                }
            });
        } catch (SecurityException e) {
            // logger.error("Could not set the Default Uncaught Exception Handler",
            // e);
        }
    }

}