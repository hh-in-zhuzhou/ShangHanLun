package me.huanghai.shanghanlun_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;

public class MainActivity extends FragmentActivity {

    private FragmentManager fragmentManager;
    private RadioGroup radioGroup;
    private String isFang;

    // private ViewGroup mContainer;

    // @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tabcontroller);

        isFang = getIntent().getExtras().getString("isFang");
        int tranId = R.id.firstContentTab;
        if (isFang.equals("true")) {
            tranId = R.id.fangYaoTab;
        }

        // mContainer = (ViewGroup) findViewById(R.id.content);
        fragmentManager = getSupportFragmentManager();
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
