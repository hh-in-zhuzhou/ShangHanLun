package me.huanghai.searchController;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.nakardo.atableview.uikit.UILabel;

import java.util.Map;

import me.huanghai.shanghanlun_android.R;

/**
 * Created by hh on 16/3/24.
 */
public class LittleTextViewWindow extends Fragment {

    private View view;
    private ViewGroup mGroup;
    private String yao;
    private Rect rect;
    private String tag = "littleWindow";

    public LittleTextViewWindow() {
    }

    public LittleTextViewWindow(String s, Rect rect_) {
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
        view = inflater.inflate(R.layout.show_yao, null);
        mGroup = (ViewGroup) getActivity().getWindow().getDecorView();
        mGroup.addView(view);
        Button btn = (Button) view
                .findViewById(R.id.maskbtnYao);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dismiss();
            }
        });

        UILabel textView = (UILabel) view
                .findViewById(R.id.textview);
        textView.setText("未找到");
        String s = yao != null ? yao : "";
        SpannableStringBuilder spanString = new SpannableStringBuilder();
        SingletonData single = SingletonData.getInstance();
        Map<String, String> yaoDict = single.getYaoAliasDict();
        String right = yaoDict.get(s);
        if (right == null) {
            right = s;
        }
        for (HH2SectionData sec : single.getYaoData()) {
            for (DataItem item : sec.getData()) {
                String yao__ = item.getYaoList()[0];
                String left = yaoDict.get(yao__);
                if (left == null) {
                    left = yao__;
                }
                if (left.equals(right)) {
                    spanString.append(item.getAttributedText());
                }
            }
        }

        int count = 0;
        for (HH2SectionData sec : SingletonData.getInstance()
                .getFang()) {
            SpannableStringBuilder spanIn = new SpannableStringBuilder();
            count = 0;
            for (DataItem item : sec.getData()) {
                for (String string : item.getYaoList()) {
                    String left = yaoDict.get(string);
                    if (left == null) {
                        left = string;
                    }
                    if (left.equals(right)) {
                        count++;
                        spanIn.append(Helper.renderText("$f{"
                                + item.getFangList()[0] + "}、"));
                        break;
                    }
                }
            }
            if (count > 0) {
                spanString.append("\n\n" + sec.getHeader()
                        + " 凡" + count + "方\n");
                spanString.append(spanIn);
            }
        }

        textView.setText(spanString);
        textView.setMovementMethod(LocalLinkMovementMethod
                .getInstance());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = 50;
        int screenHeight = mGroup.getHeight();
        int mid = rect.top + rect.height() / 2;
        if (mid < screenHeight / 2.0) {
            params.setMargins(margin,
                    rect.top + rect.height() + 20, margin, margin);
        } else {
            params.gravity = Gravity.BOTTOM;
            Rect dispRect = new Rect();
            mGroup.getWindowVisibleDisplayFrame(dispRect);
            int top = dispRect.top;
            params.setMargins(
                    margin,
                    top + 8,
                    margin,
                    (screenHeight - rect.top) + 20);

        }
        ScrollView scroll = (ScrollView) view
                .findViewById(R.id.maskscroll);
        scroll.setLayoutParams(params);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        mGroup.removeView(view);
        super.onDestroyView();
    }
}
