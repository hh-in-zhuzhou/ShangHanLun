package me.huanghai.searchController;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nakardo.atableview.view.ATableView;

import java.util.Map;
import java.util.regex.Pattern;

import me.huanghai.shanghanlun_android.MainActivity;
import me.huanghai.shanghanlun_android.R;

/**
 * Created by hh on 16/3/24.
 */
public class LittleTableViewWindow extends LittleWindow {
    private View view;
    private ViewGroup mGroup;

    private String fang;
    private SpannableStringBuilder attributedString;

    private Rect rect;
    private String tag = "littleWindow";

    @Override
    public String getSearchString() {
        return fang;
    }

    @Override
    public void show(FragmentManager manager) {
        super.show(manager);
        SingletonData.getInstance().littleWindowStack.add(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        SingletonData.getInstance().littleWindowStack.remove(this);
    }

    public void setAttributedString(SpannableStringBuilder attributedString) {
        this.attributedString = attributedString;
    }

    public LittleTableViewWindow() {
    }

    public void setFang(String fang) {
        SingletonData single = SingletonData.getInstance();
        Map<String, String> fangDict = single.getFangAliasDict();
        String right = fangDict.get(fang);
        if (right == null) {
            right = fang;
        }
        this.fang = right;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public boolean isInFangContext() {
        SpannableStringBuilder builder = attributedString;
        ClickableSpan[] spans = builder.getSpans(0,
                builder.length(), ClickableSpan.class);
        Map<String, String> dict = SingletonData.getInstance().getFangAliasDict();
        if (spans.length > 0) {
            ClickableSpan span = spans[0];
            int start = builder.getSpanStart(span);
            int end = builder.getSpanEnd(span);
            String unit = builder.subSequence(start, end)
                    .toString();
            String left = dict.get(unit);
            if (left == null) {
                left = unit;
            }
            if (SingletonData.getInstance().getAllFang().contains(left)
                    && start > 0
                    && builder.toString().substring(start - 1, start).equals("、")
                    && builder.charAt(0) != '*') {
                return true;
            }
        }

        return false;
    }

    protected boolean onlyShowRelatedContent() {
        SingletonData single = SingletonData.getInstance();
        Map<String, String> fangDict = single.getFangAliasDict();
        String right = fang;
        if (single.littleWindowStack.size() > 0) {
            LittleWindow window = single.littleWindowStack.get(single.littleWindowStack.size() - 1);
            String text_ = window.getSearchString();
            String text = fangDict.get(text_);
            if (text == null) {
                text = text_;
            }
            if (text.equals(right) && isInFangContext()) {
                return true;
            }
        } else {
            ShowFragment showFragment = single.curFragment;
            if (showFragment != null && showFragment.getIsContentOpen() == true) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mGroup = (ViewGroup) getActivity().getWindow().getDecorView();
        int density = (int) mGroup.getResources().getDisplayMetrics().density;
        int screenHeight = mGroup.getHeight();
        int screenWidth = mGroup.getWidth();
        int margin = Math.min(50, screenWidth / 18);
        int border = margin;
        FrameLayout.LayoutParams arrowParams = new FrameLayout.LayoutParams(border, border);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (rect == null) {
            rect = new Rect();
        }
        int midY = rect.top + rect.height() / 2;
        int midX = rect.left + rect.width() / 2;
        int screenMidX = screenWidth / 2;
        int screenMidy = screenHeight / 2;

        int offset = 0;
        int direction = ArrowView.UP;
        if (midY < screenMidy) {
            view = inflater.inflate(R.layout.show_fang, null);
            params.setMargins(margin,
                    rect.top + rect.height() + border, margin, margin);
            arrowParams.setMargins(
                    midX - border / 2,
                    rect.top + rect.height() + offset,
                    screenWidth - midX - border / 2,
                    screenHeight - rect.top - rect.height() - border - offset);
            direction = ArrowView.UP;
        } else {
            view = inflater.inflate(R.layout.show_fang_2, null);
            offset = 1;
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
                    rect.top - border - offset,
                    screenWidth - midX - border / 2,
                    screenHeight - rect.top + offset);
            direction = ArrowView.DOWN;
        }

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
        final String s = fang != null ? fang : "";
        final ShowFang showFang = new ShowFang(s, onlyShowRelatedContent());
        SingletonData.getInstance().pushShowFang(showFang);
        tableView.setDataSource(showFang.getDataSource());
        tableView.setDelegate(showFang.getDelegate());
        tableView.enableHeaderView(true);

        ArrowView arrow = (ArrowView) view.findViewById(R.id.arrow);
        arrow.setDirection(direction);
        arrow.setLayoutParams(arrowParams);

        btn = (Button) view.findViewById(R.id.leftbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFang.putCopyStringsToClipboard();
                Toast.makeText(SingletonData.getInstance().curActivity, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
            }
        });

        btn = (Button) view.findViewById(R.id.rightbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("title", s);
                intent.putExtra("isFang", "false");
                getActivity().startActivity(intent);
            }
        });

        LinearLayout wrapper = (LinearLayout) view.findViewById(R.id.wrapper);
        wrapper.setLayoutParams(params);

        mGroup.addView(view);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        mGroup.removeView(view);
        super.onDestroyView();
    }


}
