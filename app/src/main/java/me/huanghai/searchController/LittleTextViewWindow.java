package me.huanghai.searchController;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
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
import android.widget.ScrollView;
import android.widget.Toast;

import com.nakardo.atableview.uikit.UILabel;

import java.util.Map;

import me.huanghai.shanghanlun_android.MainActivity;
import me.huanghai.shanghanlun_android.R;

/**
 * Created by hh on 16/3/24.
 */
public class LittleTextViewWindow extends LittleWindow {

    private View view;
    private ViewGroup mGroup;

    private String yao;
    private SpannableStringBuilder attributedString;

    private Rect rect;
    private String tag = "littleWindow";

    public String getSearchString() {
        return yao;
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

    public LittleTextViewWindow() {
    }

    public void setYao(String yao) {
        SingletonData single = SingletonData.getInstance();
        Map<String, String> fangDict = single.getYaoAliasDict();
        String right = fangDict.get(yao);
        if (right == null) {
            right = yao;
        }
        this.yao = right;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public boolean isInYaoContext() {
        SpannableStringBuilder builder = attributedString;
        ClickableSpan[] spans = builder.getSpans(0,
                builder.length(), ClickableSpan.class);
        Map<String, String> dict = SingletonData.getInstance().getYaoAliasDict();
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
            if (SingletonData.getInstance().getAllYao().contains(left)
                    && start > 0
                    && builder.toString().substring(start - 1, start).equals("、")) {
                return true;
            }
        }

        return false;
    }

    protected SpannableStringBuilder getSpanString(String s) {
        SpannableStringBuilder spanString = new SpannableStringBuilder();
        SingletonData single = SingletonData.getInstance();
        Map<String, String> yaoDict = single.getYaoAliasDict();
        String right = yaoDict.get(s);
        if (right == null) {
            right = s;
        }

        if (!onlyShowRelatedFang()) {
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
            if (spanString.length() == 0) {
                spanString.append(Helper.renderText("$r{药物未找到资料}"));
            }
            spanString.append("\n\n");
        }

        int count = 0;
        int index = 0;
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
            if (index > 0) {
                spanString.append("\n\n");
            }
            spanString.append(sec.getHeader()
                    + " 凡" + count + "方");
            if (count > 0) {
                spanString.append("\n");
                spanString.append(spanIn);
            }
            index++;
        }
        return spanString;
    }

    protected boolean onlyShowRelatedFang() {
        SingletonData single = SingletonData.getInstance();
        Map<String, String> yaoDict = single.getYaoAliasDict();
        String right = yao;
        if (single.littleWindowStack.size() > 0) {
            LittleWindow window = single.littleWindowStack.get(single.littleWindowStack.size() - 1);
            String text_ = window.getSearchString();
            String text = yaoDict.get(text_);
            if (text == null) {
                text = text_;
            }
            if (text.equals(right) && isInYaoContext()) {
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

        int screenHeight = mGroup.getHeight();
        int screenWidth = mGroup.getWidth();
        int margin = Math.min(50, screenWidth / 18);
        int border = margin;
        FrameLayout.LayoutParams arrowParams = new FrameLayout.LayoutParams(border, border);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        int midY = rect.top + rect.height() / 2;
        int midX = rect.left + rect.width() / 2;
        int offset = 4;
        int direction = ArrowView.UP;
        if (midY < screenHeight / 2) {
            view = inflater.inflate(R.layout.show_yao, null);
            params.setMargins(margin,
                    rect.top + rect.height() + border, margin, margin);
            arrowParams.setMargins(
                    midX - border / 2,
                    rect.top + rect.height() + offset,
                    screenWidth - midX - border / 2,
                    screenHeight - rect.top - rect.height() - border - offset);
            direction = ArrowView.UP;
        } else {
            view = inflater.inflate(R.layout.show_yao_2, null);
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


        Button btn = (Button) view
                .findViewById(R.id.maskbtnYao);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dismiss();
            }
        });

        final UILabel textView = (UILabel) view
                .findViewById(R.id.textview);
        textView.setText("未找到");
        final String s = yao != null ? yao : "";

        textView.setText(getSpanString(s));
        textView.setMovementMethod(LocalLinkMovementMethod
                .getInstance());
        ArrowView arrow = (ArrowView) view.findViewById(R.id.arrow);
        arrow.setDirection(direction);
        arrow.setLayoutParams(arrowParams);

        btn = (Button) view.findViewById(R.id.leftbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.putStringToClipboard(textView.getText().toString());
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
                intent.putExtra("yaoZheng", "true");
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
