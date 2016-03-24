package me.huanghai.shanghanlun_android;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RadioGroup;

public class MainActivity extends Activity {

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
