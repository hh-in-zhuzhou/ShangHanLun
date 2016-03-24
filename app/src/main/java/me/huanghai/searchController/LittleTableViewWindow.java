package me.huanghai.searchController;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.nakardo.atableview.view.ATableView;

import java.util.List;

import me.huanghai.shanghanlun_android.R;

/**
 * Created by hh on 16/3/24.
 */
public class LittleTableViewWindow extends Fragment {
    private View view;
    private ViewGroup mGroup;
    private String yao;
    private Rect rect;
    private String tag = "littleWindow";

    public LittleTableViewWindow() {
    }

    public LittleTableViewWindow(String s, Rect rect_) {
        yao = s;
        rect = rect_;
    }

    public void show(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.addToBackStack(tag);
        ft.commit();
    }

    public void dismiss() {
        getFragmentManager().popBackStack();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.show_fang, null);
        mGroup = (ViewGroup) getActivity().getWindow().getDecorView();

        Context context = getActivity();
        WindowManager wmManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wmManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        // layout_是整个页面的根framelayout
        // 这里的main布局是在inflate中加入的哦，以前都是直接this.setContentView()的吧？呵呵
        // 该方法返回的是一个View的对象，是布局中的根
        Button btn = (Button) view.findViewById(R.id.maskbtn);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dismiss();
                SingletonData.getInstance().popShowFang();
            }
        });

        ATableView tableView = (ATableView) view
                .findViewById(R.id.showfang);
        tableView.init(ATableView.ATableViewStyle.Plain);
        String s = yao != null ? yao : "";
        ShowFang showFang = new ShowFang(s);
        SingletonData.getInstance().pushShowFang(showFang);
        tableView.setDataSource(showFang.getDataSource());
        tableView.setDelegate(showFang.getDelegate());
        tableView.enableHeaderView(true);
//                        tableView.setBackgroundResource(R.drawable.round_win);
        // 下面我们要考虑了，我怎样将我的layout加入到PopupWindow中呢？？？很简单

        // 设置layout在PopupWindow中显示的位置

        ArrowView arrow = (ArrowView) view.findViewById(R.id.arrow);

        int border = 50;
        FrameLayout.LayoutParams arrowParams = new FrameLayout.LayoutParams(border, border);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = 50;
        int screenHeight = mGroup.getHeight();
        int screenWidth = mGroup.getWidth();
        int midY = rect.top + rect.height() / 2;
        int midX = rect.left + rect.width() / 2;
        if (midY < screenHeight / 2.0) {
            params.setMargins(margin,
                    rect.top + rect.height() + border, margin, margin);
            arrowParams.setMargins(
                    midX - border / 2,
                    rect.top + rect.height() + 3,
                    screenWidth - midX - border / 2,
                    screenHeight - rect.top - rect.height() - border - 3);
            arrow.setDirection(ArrowView.UP);
        } else {
            params.gravity = Gravity.BOTTOM;
            Rect dispRect = new Rect();
            mGroup.getWindowVisibleDisplayFrame(dispRect);
            int top = dispRect.top;
            params.setMargins(
                    margin,
                    top + 8,
                    margin,
                    (screenHeight - rect.top) + border);
            arrowParams.setMargins(
                    midX - border / 2,
                    rect.top - border - 3,
                    screenWidth - midX - border / 2,
                    screenHeight - rect.top + 3);
            arrow.setDirection(ArrowView.DOWN);
        }
        tableView.setLayoutParams(params);
        arrow.setLayoutParams(arrowParams);
        mGroup.addView(view);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        mGroup.removeView(view);
        super.onDestroyView();
    }
}
