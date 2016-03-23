package me.huanghai.searchController;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.uikit.UILabel;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.huanghai.shanghanlun_android.R;

public class DataItem {
    private String text;
    private SpannableStringBuilder attributedText;
    private NSIndexPath indexPath;
    private String[] fangList;
    private String[] yaoList;

    private Rect parentTextViewRect = new Rect();

    void setText(String text_) {
        text = text_;
        // TODO:compute other property;
        attributedText = renderText(text);
        fangList = getFangNameList(text);
        yaoList = getYaoNameList(text);
    }

    public DataItem getCopy() {
        DataItem item = new DataItem();
        item.setPureText(text);
        item.setAttributedText(new SpannableStringBuilder(attributedText));
        item.setIndexPath(indexPath);
        item.setFangList(fangList);
        item.setYaoList(yaoList);

        return item;
    }

    private void setPureText(String text_) {
        text = text_;
    }

    public void setFangList(String[] fang) {
        fangList = fang;
    }

    public void setYaoList(String[] yao) {
        yaoList = yao;
    }

    public String getText() {
        return text;
    }

    public SpannableStringBuilder getAttributedText() {
        return attributedText;
    }

    public void setAttributedText(SpannableStringBuilder builder) {
        attributedText = builder;
    }

    public NSIndexPath getIndexPath() {
        return indexPath;
    }

    void setIndexPath(NSIndexPath indexPath_) {
        indexPath = indexPath_;
    }

    public String[] getFangList() {
        return fangList;
    }

    public String[] getYaoList() {
        return yaoList;
    }

    public DataItem() {

    }

    protected int getItemIndex(String text) {
        String numString = text.substring(0, text.indexOf("、"));
        return Integer.parseInt(numString) - 1;
    }

    public static String[] getFangNameList(String textString) {
        ArrayList<Integer> allPos = getAllSubStringPos(textString, "$f");

        // TODO: to avoid duplicate strings.
        String[] resStrings = new String[allPos.size()];

        int i = 0;
        for (int pos : allPos) {
            int curEnd = textString.substring(pos).indexOf("}");
            String cut = textString.substring(pos + 3, pos + curEnd);
            resStrings[i] = cut;
            i++;
        }
        return resStrings;
    }

    public static String[] getYaoNameList(String textString) {
        ArrayList<Integer> allPos = getAllSubStringPos(textString, "$u");

        // TODO: to avoid duplicate strings.
        String[] resStrings = new String[allPos.size()];

        int i = 0;
        for (int pos : allPos) {
            int curEnd = textString.substring(pos).indexOf("}");
            String cut = textString.substring(pos + 3, pos + curEnd);
            resStrings[i] = cut;
            i++;
        }
        return resStrings;
    }

    protected void renderItemNumber(SpannableStringBuilder builder) {
        String string = builder.toString();
        if (!string.contains("、")
                || !isNumeric(string.substring(0, string.indexOf("、")))) {
            return;
        }
        ForegroundColorSpan numSpan = new ForegroundColorSpan(Color.BLUE);
        builder.setSpan(numSpan, 0, string.indexOf("、"),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public SpannableStringBuilder renderText(String textString) {
        // System.out.println(textString);
        SpannableStringBuilder res = new SpannableStringBuilder(textString);
        // ArrayList<Integer> allPos = getAllSubStringPos(textString, "$");
        int pos = 0;
        while ((pos = textString.indexOf("$")) >= 0) {
            int endPos = textString.indexOf("}");
            // 看"}"之前还有几个"$"
            int m = 1; // 1个 "}"
            int n = getAllSubStringPos(textString.substring(pos, endPos), "$")
                    .size();
            while (n > m) {
                for (int i = 0; i < n - m; i++) {
                    endPos += textString.substring(endPos + 1).indexOf("}") + 1;
                }
                m = n;
                n = getAllSubStringPos(textString.substring(pos, endPos), "$")
                        .size();
            }

            String strClass = textString.substring(pos + 1, pos + 2);
            int color = getColoredTextByStrClass(strClass);
            ForegroundColorSpan someSpan = new ForegroundColorSpan(color);
            res.setSpan(someSpan, pos + 3, endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (strClass.equals("a") || strClass.equals("w")) {
                res.setSpan(new RelativeSizeSpan(0.7f), pos + 3, endPos,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (strClass.equals("u")) {
                res.setSpan(new ClickableSpan() {

                    @Override
                    public void onClick(View widget) {
                        // TODO Auto-generated method stub
                        // if (SingletonData.getInstance().hasShowFang()) {
                        // return;
                        // }

                        // ShowFang showFang = new ShowFang("xyz");
                        // SingletonData.getInstance().pushShowFang(showFang);

                        UILabel tv = (UILabel) widget;
                        String s = tv
                                .getText()
                                .subSequence(tv.getSelectionStart(),
                                        tv.getSelectionEnd()).toString();
                        System.out.println("tapped:" + s);
                        getTextRect(this, (TextView) widget);

                        Context context = MyApplication.getAppContext();
                        WindowManager wmManager = (WindowManager) context
                                .getSystemService(Context.WINDOW_SERVICE);
                        Display display = wmManager.getDefaultDisplay();
                        DisplayMetrics metrics = new DisplayMetrics();
                        display.getMetrics(metrics);
                        // layout_是整个页面的根framelayout
                        final ViewGroup layout_ = (ViewGroup) SingletonData
                                .getInstance().getMask();
                        LayoutInflater inflater = (LayoutInflater) context
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        // 这里的main布局是在inflate中加入的哦，以前都是直接this.setContentView()的吧？呵呵
                        // 该方法返回的是一个View的对象，是布局中的根
                        final View layout = inflater.inflate(R.layout.show_yao,
                                null);

                        Button btn = (Button) layout
                                .findViewById(R.id.maskbtnYao);
                        btn.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                layout_.removeView(layout);
                                List<View> vs = SingletonData.getInstance()
                                        .getLittleWindows();
                                vs.remove(vs.size() - 1);
                            }
                        });

                        UILabel textView = (UILabel) layout
                                .findViewById(R.id.textview);
                        textView.setText("未找到");

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
                                    spanString.append(item.attributedText);
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
                                        spanIn.append(renderText("$f{"
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
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT);
                        int margin = 50;
                        // int num =
                        // SingletonData.getInstance().getShowFangNum();
                        if (parentTextViewRect.top < display.getHeight() / 2.0) {
                            params.setMargins(margin,
                                    parentTextViewRect.top + 20, margin, margin);
                        } else {
                            params.gravity = Gravity.BOTTOM;
                            params.setMargins(
                                    margin,
                                    margin,
                                    margin,
                                    (display.getHeight() - parentTextViewRect.top) + 20);

                        }
                        ScrollView scroll = (ScrollView) layout
                                .findViewById(R.id.maskscroll);
                        scroll.setLayoutParams(params);
                        scroll.setBackgroundResource(R.drawable.round_win);
                        layout_.addView(layout);
                        SingletonData.getInstance().getLittleWindows()
                                .add(layout);
                    }
                }, pos + 3, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (strClass.equals("f")) {
                res.setSpan(new ClickableSpan() {

                    @Override
                    public void onClick(View widget) {
                        // TODO Auto-generated method stub
                        // if (SingletonData.getInstance().hasShowFang()) {
                        // return;
                        // }
                        UILabel tv = (UILabel) widget;
                        String s = tv
                                .getText()
                                .subSequence(tv.getSelectionStart(),
                                        tv.getSelectionEnd()).toString();
                        System.out.println("tapped:" + s);
                        getTextRect(this, (TextView) widget);

                        Context context = MyApplication.getAppContext();
                        WindowManager wmManager = (WindowManager) context
                                .getSystemService(Context.WINDOW_SERVICE);
                        Display display = wmManager.getDefaultDisplay();
                        DisplayMetrics metrics = new DisplayMetrics();
                        display.getMetrics(metrics);
                        // layout_是整个页面的根framelayout
                        final ViewGroup layout_ = (ViewGroup) SingletonData
                                .getInstance().getMask();
                        LayoutInflater inflater = (LayoutInflater) context
                                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        // 这里的main布局是在inflate中加入的哦，以前都是直接this.setContentView()的吧？呵呵
                        // 该方法返回的是一个View的对象，是布局中的根
                        final ViewGroup layout = (ViewGroup) inflater.inflate(
                                R.layout.show_fang, null);
                        Button btn = (Button) layout.findViewById(R.id.maskbtn);
                        btn.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                layout_.removeView(layout);
                                SingletonData.getInstance().popShowFang();
                                List<View> vs = SingletonData.getInstance()
                                        .getLittleWindows();
                                vs.remove(vs.size() - 1);
                            }
                        });

                        ATableView tableView = (ATableView) layout
                                .findViewById(R.id.showfang);
                        tableView.init(ATableViewStyle.Plain);
                        ShowFang showFang = new ShowFang(s);
                        SingletonData.getInstance().pushShowFang(showFang);
                        tableView.setDataSource(showFang.getDataSource());
                        tableView.setDelegate(showFang.getDelegate());
                        tableView.enableHeaderView(true);
                        tableView.setBackgroundResource(R.drawable.round_win);
                        // 下面我们要考虑了，我怎样将我的layout加入到PopupWindow中呢？？？很简单

                        // 设置layout在PopupWindow中显示的位置

                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                LayoutParams.MATCH_PARENT,
                                LayoutParams.WRAP_CONTENT);
                        int margin = 50;
                        // int num =
                        // SingletonData.getInstance().getShowFangNum();

                        // int screenHeight = display.getHeight();
                        Point p = new Point();
                        display.getSize(p);
                        int screenHeight = p.y;
                        if (parentTextViewRect.top < screenHeight / 2.0) {
                            params.setMargins(margin,
                                    parentTextViewRect.top + 20, margin, margin);
                        } else {
                            params.gravity = Gravity.BOTTOM;
                            params.setMargins(
                                    margin,
                                    margin,
                                    margin,
                                    (screenHeight - parentTextViewRect.top) + 20);
                        }
                        tableView.setLayoutParams(params);
                        layout_.addView(layout);
                        SingletonData.getInstance().getLittleWindows()
                                .add(layout);
                    }
                }, pos + 3, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            res.replace(endPos, endPos + 1, "");
            res.replace(pos, pos + 3, "");
            textString = res.toString();
        }
        renderItemNumber(res);
        return res;
    }

    private void getTextRect(ClickableSpan clickedText, TextView parentTextView) {
        // Initialize global value
        // Initialize values for the computing of clickedText position
        SpannableString completeText = (SpannableString) (parentTextView)
                .getText();
        Layout textViewLayout = parentTextView.getLayout();

        double startOffsetOfClickedText = completeText
                .getSpanStart(clickedText);
        double endOffsetOfClickedText = completeText.getSpanEnd(clickedText);
        double startXCoordinatesOfClickedText = textViewLayout
                .getPrimaryHorizontal((int) startOffsetOfClickedText);
        double endXCoordinatesOfClickedText = textViewLayout
                .getPrimaryHorizontal((int) endOffsetOfClickedText);

        // Get the rectangle of the clicked text
        int currentLineStartOffset = textViewLayout
                .getLineForOffset((int) startOffsetOfClickedText);
        int currentLineEndOffset = textViewLayout
                .getLineForOffset((int) endOffsetOfClickedText);
        boolean keywordIsInMultiLine = currentLineStartOffset != currentLineEndOffset;
        textViewLayout.getLineBounds(currentLineStartOffset,
                this.parentTextViewRect);

        // Update the rectangle position to his real position on screen
        int[] parentTextViewLocation = {0, 0};
        parentTextView.getLocationOnScreen(parentTextViewLocation);

        double parentTextViewTopAndBottomOffset = (parentTextViewLocation[1]
                - parentTextView.getScrollY() + parentTextView
                .getCompoundPaddingTop());
        this.parentTextViewRect.top += parentTextViewTopAndBottomOffset;
        this.parentTextViewRect.bottom += parentTextViewTopAndBottomOffset;

        // In the case of multi line text, we have to choose what rectangle take
        if (keywordIsInMultiLine) {
            WindowManager wmManager = (WindowManager) MyApplication
                    .getAppContext().getSystemService(Context.WINDOW_SERVICE);
            int screenHeight = wmManager.getDefaultDisplay().getHeight();
            int dyTop = this.parentTextViewRect.top;
            int dyBottom = screenHeight - this.parentTextViewRect.bottom;
            boolean onTop = dyTop > dyBottom;

            if (onTop) {
                endXCoordinatesOfClickedText = textViewLayout
                        .getLineRight(currentLineStartOffset);
            } else {
                this.parentTextViewRect = new Rect();
                textViewLayout.getLineBounds(currentLineEndOffset,
                        this.parentTextViewRect);
                this.parentTextViewRect.top += parentTextViewTopAndBottomOffset;
                this.parentTextViewRect.bottom += parentTextViewTopAndBottomOffset;
                startXCoordinatesOfClickedText = textViewLayout
                        .getLineLeft(currentLineEndOffset);
            }

        }

        this.parentTextViewRect.left += (parentTextViewLocation[0]
                + startXCoordinatesOfClickedText
                + parentTextView.getCompoundPaddingLeft() - parentTextView
                .getScrollX());
        this.parentTextViewRect.right = (int) (this.parentTextViewRect.left
                + endXCoordinatesOfClickedText - startXCoordinatesOfClickedText);
    }

    private static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content))
                .getChildAt(0);
    }

    public int getColoredTextByStrClass(String strClass) {
        /*
         * $n{...} 条文序号 $f{...} 方名 $a{...} 内嵌注释 $m{...} 药味总数 $s{...}
		 * 药煮法开头的“上...味" $u{...} 药名 $w{...} 药量 不允许嵌套使用 $q{...} 方名前缀（千金，外台）
		 * 不允许嵌套使用 $h{...} 隐藏的方名（暂时只用于标记方名)
		 */
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("n", Color.BLUE);
        map.put("f", Color.BLUE);
        map.put("a", Color.GRAY);
        map.put("m", Color.rgb(255, 0, 255));
        map.put("s", Color.argb(230, 0, 128, 255));
        map.put("u", Color.rgb(77, 0, 255));
        map.put("v", Color.rgb(77, 0, 255));
        map.put("w", Color.rgb(0, 128, 0));
        map.put("q", Color.rgb(61, 200, 120));
        map.put("h", Color.TRANSPARENT);
        return map.get(strClass);
    }

    public static ArrayList<Integer> getAllSubStringPos(String text,
                                                        String subStr) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        int curLoc = 0;
        int end = text.length();
        while (curLoc < end && text.substring(curLoc).contains(subStr)) {
            String curStr = text.substring(curLoc);
            int index = curStr.indexOf(subStr);
            Integer foundOne = Integer.valueOf(curLoc + index);
            res.add(foundOne);
            curLoc = foundOne + subStr.length();
        }
        return res;
    }

    public boolean isNumeric(String string) {
        Boolean hasDot = false;
        for (int i = 0; i < string.length(); i++) {
            // System.out.println(str.charAt(i));
            if (string.charAt(i) == '.' && hasDot == false) {
                hasDot = true;
                if (i < string.length() - 1) {
                    continue;
                } else {
                    return false;
                }
            } else if (string.charAt(i) == '.' && hasDot == true) {
                return false;
            }
            if (!Character.isDigit(string.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
