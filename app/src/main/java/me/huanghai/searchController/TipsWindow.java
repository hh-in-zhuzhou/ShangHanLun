package me.huanghai.searchController;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Pattern;

import me.huanghai.shanghanlun_android.R;

/**
 * Created by hh on 16/3/28.
 */
public class TipsWindow extends LittleWindow {
    ViewGroup mGroup;
    View view;
    ClickLink clickLink;
    boolean isShowing;
    TextView textView;

    public boolean isShowing() {
        return isShowing;
    }

    public void setIsShowing(boolean isShowing) {
        this.isShowing = isShowing;
    }

    public void setClickLink(ClickLink clickLink) {
        this.clickLink = clickLink;
    }

    @Override
    public void show(FragmentManager manager) {
        super.show(manager);
        isShowing = true;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isShowing = false;
        mGroup.removeView(view);
    }

    @Override
    public String getTagName() {
        return "tipsWindow";
    }

    @Override
    public void setSearchText(String searchText) {
        super.setSearchText(searchText);
        if (textView != null) {
            textView.setText(getTipsString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGroup = (ViewGroup) getActivity().getWindow().getDecorView();
        view = inflater.inflate(R.layout.tips_window, null);
        textView = (TextView) view.findViewById(R.id.tipsview);
        textView.setMovementMethod(LocalLinkMovementMethod
                .getInstance());
        textView.setText(getTipsString());
        mGroup.addView(view);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected SpannableStringBuilder getTipsString() {
        char c = searchText.contains("y") ? 'y' : 'f';
        StringBuilder builder = new StringBuilder();
        String header = c == 'f' ? "所有方剂：" : "所有药物：";
        List<String> groups = c == 'f' ? SingletonData.getInstance().allFang
                : SingletonData.getInstance().allYao;
        String cls = c == 'f' ? "f" : "u";
        builder.append("$r{" + header + "}\n");

        String[] units = searchText.split(c == 'y' ? "y" : "f");
        Pattern p = Pattern.compile(units.length > 0 ? units[units.length - 1] : ".");
        for (String str :
                groups) {
            if (p.matcher(str).find()) {
                builder.append("$" + cls + "{" + str + "}，");
            }
        }
        SpannableStringBuilder span = Helper.renderText(builder.toString(), clickLink);
        return span;
    }
}
