package me.huanghai.shanghanlun_android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;

import me.huanghai.searchController.MyApplication;
import me.huanghai.searchController.SingletonData;

public class MainActivity extends Activity {

    private FragmentManager fragmentManager;
    private RadioGroup radioGroup;
    private String isFang;

    // private ViewGroup mContainer;

    @Override
    protected void onResume() {
        super.onResume();
        SingletonData.getInstance().curActivity = this;
        Log.e("MainActivity", "onResume!!!!!");
    }

    // @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("MainActivity", "onCreate!!!!!");
        MyApplication.activity = this;
        SingletonData.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tabcontroller);

        isFang = getIntent().getExtras().getString("isFang");
        int tranId = R.id.firstContentTab;
        if (isFang != null && isFang.equals("true")) {
            tranId = R.id.fangYaoTab;
        }

        // mContainer = (ViewGroup) findViewById(R.id.content);
        fragmentManager = getFragmentManager();
        radioGroup = (RadioGroup) findViewById(R.id.rg_tab);
        radioGroup.check(tranId);
        radioGroup.setVisibility(ViewGroup.GONE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment fragment = FragmentFactory
                .getInstanceByIndex(tranId);
        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }

}
