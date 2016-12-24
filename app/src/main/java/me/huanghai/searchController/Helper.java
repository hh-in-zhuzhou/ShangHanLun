package me.huanghai.searchController;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.huanghai.shanghanlun_android.R;

/**
 * Created by hh on 16/3/24.
 */
public class Helper {
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

    public static boolean isNumeric(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
                return false;
        }
        return true;
    }

    public static void renderItemNumber(SpannableStringBuilder builder) {
        String string = builder.toString();
        if (!string.contains("、")
                || !isNumeric(string.substring(0, string.indexOf("、")))) {
            return;
        }
        ForegroundColorSpan numSpan = new ForegroundColorSpan(Color.BLUE);
        builder.setSpan(numSpan, 0, string.indexOf("、"),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public static SpannableStringBuilder renderText(String textString, final ClickLink clickLink) {
        SpannableStringBuilder res = new SpannableStringBuilder(textString);
        // ArrayList<Integer> allPos = getAllSubStringPos(textString, "$");
        int pos = 0;
        while ((pos = textString.indexOf("$")) >= 0) {
            int endPos = textString.indexOf("}");
            // 看"}"之前还有几个"$"
            int m = 1; // 1个 "}"
            int n = Helper.getAllSubStringPos(textString.substring(pos, endPos), "$")
                    .size();
            while (n > m) {
                for (int i = 0; i < n - m; i++) {
                    endPos += textString.substring(endPos + 1).indexOf("}") + 1;
                }
                m = n;
                n = Helper.getAllSubStringPos(textString.substring(pos, endPos), "$")
                        .size();
            }

            String strClass = textString.substring(pos + 1, pos + 2);

            if (strClass.equals("a") || strClass.equals("w")) {
                res.setSpan(new RelativeSizeSpan(0.7f), pos + 3, endPos,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (strClass.equals("u")) {
                res.setSpan(new ClickableSpan() {

                    @Override
                    public void onClick(View widget) {
                        // TODO Auto-generated method stub
                        clickLink.clickYaoLink((TextView) widget, this);
                    }
                }, pos + 3, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            if (strClass.equals("f")) {
                res.setSpan(new ClickableSpan() {

                    @Override
                    public void onClick(View widget) {
                        // TODO Auto-generated method stub
                        clickLink.clickFangLink((TextView) widget, this);
                    }
                }, pos + 3, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            // 把加颜色放到最后
            int color = getColoredTextByStrClass(strClass);
            ForegroundColorSpan someSpan = new ForegroundColorSpan(color);
            res.setSpan(someSpan, pos + 3, endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            res.replace(endPos, endPos + 1, "");
            res.replace(pos, pos + 3, "");
            textString = res.toString();
        }
        renderItemNumber(res);
        return res;
    }

    public static SpannableStringBuilder renderText(String textString) {
        // System.out.println(textString);
        return renderText(textString, new ClickLink() {
            @Override
            public void clickYaoLink(TextView tv, ClickableSpan clickableSpan) {
                Helper.closeKeyboard(SingletonData.getInstance().curActivity);
                String s = tv
                        .getText()
                        .subSequence(tv.getSelectionStart(),
                                tv.getSelectionEnd()).toString();
                System.out.println("tapped:" + s);
                Rect rect = getTextRect(clickableSpan, tv);
//                        callBack.onYaoCallBack(rect, s);
                LittleTextViewWindow window = new LittleTextViewWindow();
                window.setYao(s);
                window.setAttributedString(new SpannableStringBuilder(tv.getText()));
                window.setRect(rect);
                window.show(SingletonData.getInstance().curActivity.getFragmentManager());
            }

            @Override
            public void clickFangLink(TextView tv, ClickableSpan clickableSpan) {
                Helper.closeKeyboard(SingletonData.getInstance().curActivity);
                String s = tv
                        .getText()
                        .subSequence(tv.getSelectionStart(),
                                tv.getSelectionEnd()).toString();
                System.out.println("tapped:" + s);
                Rect rect = getTextRect(clickableSpan, tv);

                LittleTableViewWindow window = new LittleTableViewWindow();
                window.setFang(s);
                window.setAttributedString(new SpannableStringBuilder(tv.getText()));
                window.setRect(rect);
//                Context context = tv.getContext();
//                Activity activity = (Activity)context;
//                window.show(activity.getFragmentManager());
                window.show(SingletonData.getInstance().curActivity.getFragmentManager());
            }
        });
    }

    private static Rect getTextRect(ClickableSpan clickedText, TextView parentTextView) {
        // Initialize global value
        // Initialize values for the computing of clickedText position
        Rect rect = new Rect();
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
                rect);

        // Update the rectangle position to his real position on screen
        int[] parentTextViewLocation = {0, 0};
        parentTextView.getLocationOnScreen(parentTextViewLocation);

        double parentTextViewTopAndBottomOffset = (parentTextViewLocation[1]
                - parentTextView.getScrollY() + parentTextView
                .getCompoundPaddingTop());
        rect.top += parentTextViewTopAndBottomOffset;
        rect.bottom += parentTextViewTopAndBottomOffset;

        // In the case of multi line text, we have to choose what rectangle take
        if (keywordIsInMultiLine) {
            WindowManager wmManager = (WindowManager) MyApplication
                    .getAppContext().getSystemService(Context.WINDOW_SERVICE);
            int screenHeight = wmManager.getDefaultDisplay().getHeight();
            int dyTop = rect.top;
            int dyBottom = screenHeight - rect.bottom;
            boolean onTop = dyTop > dyBottom;

            if (onTop) {
                endXCoordinatesOfClickedText = textViewLayout
                        .getLineRight(currentLineStartOffset);
            } else {
                rect = new Rect();
                textViewLayout.getLineBounds(currentLineEndOffset,
                        rect);
                rect.top += parentTextViewTopAndBottomOffset;
                rect.bottom += parentTextViewTopAndBottomOffset;
                startXCoordinatesOfClickedText = textViewLayout
                        .getLineLeft(currentLineEndOffset);
            }

        }

        rect.left += (parentTextViewLocation[0]
                + startXCoordinatesOfClickedText
                + parentTextView.getCompoundPaddingLeft() - parentTextView
                .getScrollX());
        rect.right = (int) (rect.left
                + endXCoordinatesOfClickedText - startXCoordinatesOfClickedText);
        return rect;
    }

    public static int getColoredTextByStrClass(String strClass) {
        /*
         * $r{...} 红色警告
         * $n{...} 条文序号
         * $f{...} 方名
         * $a{...} 内嵌注释
         * $m{...} 药味总数
         * $s{...} 药煮法开头的“上...味"
         * $u{...} 药名
         * $w{...} 药量
         * 不允许嵌套使用 $q{...} 方名前缀（千金，外台）
		 * 不允许嵌套使用 $h{...} 隐藏的方名（暂时只用于标记方名)
		 */
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("r", Color.RED);
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
        Integer res = map.get(strClass);
        return res == null ? Color.BLACK : res;
    }

    public static int strLengh(String str) {
        if (str == null) {
            return 0;
        }
        return str.length();
    }

    public static String getAliasString(Map<String, String> dict, String key) {
        if (dict == null || key == null) {
            return null;
        }
        String str = dict.get(key);
        if (str == null) {
            return key;
        }
        return str;
    }

    public static void putStringToClipboard(String text) {
        // Context context = getActivity().getApplicationContext();
        Context context = MyApplication.getAppContext();
        ClipboardManager cbm = (ClipboardManager) context
                .getSystemService(context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("shangHanLun", text);
        cbm.setPrimaryClip(clipData);
    }

    public static List<HH2SectionData> searchText(List<HH2SectionData> data,
                                                  DataItemCompare compare) {
        List<HH2SectionData> res = new ArrayList<>();
        for (HH2SectionData sec :
                data) {
            ArrayList<DataItem> section = null;
            for (DataItem item :
                    sec.getData()) {
                if (compare.useThisItem(item)) {
                    if (section == null) {
                        section = new ArrayList<>();
                    }
                    section.add(item);
                }
            }
            if (section != null) {
                HH2SectionData d = new HH2SectionData(section, sec.getSection(),
                        sec.getHeader());
                res.add(d);
            }
        }
        return res;
    }

    // 新加：
    public interface IFilter<T> {
        boolean filter(T o);
    }

    public interface IMap<T, K> {
        K map(T o);
    }

    public interface IReduce<P,T> {
        P reduce(P o, T next);
    }

    public interface IForEach<T> {
        void forEachDo(T o);
    }

    public interface IForEachIdx<T> {
        void forEachDo(T o, int idx);
    }

    public interface IBool<T> {
        boolean isOK(T o);
    }

    public interface IFilterThenMap<T, K> {
        boolean filter(T o);

        K map(T o);
    }

    public interface IFilterThenForEach<T> {
        boolean filter(T o);

        void forEachDo(T o);
    }

    public interface IURLExist {
        void isExist(boolean exist);
    }

    public static <T> List<T> filter(List<T> array, IFilter<T> fun) {
        List<T> res = new ArrayList();
        for (T obj :
                array) {
            if (fun.filter(obj)) {
                res.add(obj);
            }
        }
        return res;
    }

    public static <T, K> List<K> map(List<T> array, IMap<T, K> fun) {
        List<K> res = new ArrayList<>();
        for (T obj :
                array) {
            res.add(fun.map(obj));
        }
        return res;
    }

    public static <T, K> List<K> filterThenMap(List<T> array, IFilterThenMap<T, K> fun) {
        List<K> res = new ArrayList<>();
        for (T obj :
                array) {
            if (fun.filter(obj)) {
                res.add(fun.map(obj));
            }
        }
        return res;
    }

    public static <T> void filterThenForEachDo(List<T> array, IFilterThenForEach<T> fun) {
        for (T obj :
                array) {
            if (fun.filter(obj)) {
                fun.forEachDo(obj);
            }
        }
    }

    public static <P,T> P reduce(List<T> array, P initValue, IReduce<P,T> fun) {
        P res = initValue;
        for (T obj :
                array) {
            res = fun.reduce(res, obj);
        }
        return res;
    }

    public static <T> List<T> uniq(List<T> array) {
        List<T> res = new ArrayList<>();
        for (T obj :
                array) {
            if (!res.contains(obj)) {
                res.add(obj);
            }
        }
        return res;
    }

    public static <T> void forEachDo(List<T> array, IForEach<T> fun) {
        for (T obj :
                array) {
            fun.forEachDo(obj);
        }
    }

    public static <T> void forEachDo(List<T> array, IForEachIdx<T> fun) {
        int i = 0;
        for (T obj :
                array) {
            fun.forEachDo(obj, i++);
        }
    }

    public static <T> boolean some(List<T> array, IBool<T> fun) {
        if (array == null){
            return false;
        }
        for (T obj :
                array) {
            if (fun.isOK(obj)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean every(List<T> array, IBool<T> fun) {
        if (array == null){
            return false;
        }
        for (T obj :
                array) {
            if (!fun.isOK(obj)) {
                return false;
            }
        }
        return true;
    }

    public static <T> int index(List<T> array, IBool<T> fun) {
        for (int i = 0; i < array.size(); i++) {
            if (fun.isOK(array.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /////////////////////----以下是功能方法

    public static List<String> split(String string, String sepa) {
        String[] list = string.split(sepa);
        return Arrays.asList(list);
    }

    public static String join(String[] texts, String sepa) {
        return join(Arrays.asList(texts), sepa);
    }

    public static String join(List<String> list, String sepa) {
        String res = "";
        for (String str :
                list) {
            res += str + sepa;
        }
        if (res.length() < sepa.length()) {
            return "";
        }
        return res.substring(0, res.length() - sepa.length());
    }

    public static <T> T def(T obj, T obj2) {
        if (obj == null) {
            return obj2;
        }
        return obj;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 获取主色
     *
     * @return
     */
//    public static int getMainColor() {
//        return ContextCompat.getColor(App.getAppContext(), R.color.colorPrimary);
//    }

    /**
     * 判断wifi
     *
     * @return
     */
//    public static boolean isWifiConnected() {
//        return isConnected(App.getAppContext(), ConnectivityManager.TYPE_WIFI);
//    }
//
//    public static boolean isMobileConnected() {
//        return isConnected(App.getAppContext(), ConnectivityManager.TYPE_MOBILE);
//    }
//
//    private static boolean isConnected(@NonNull Context context, int type) {
//        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            NetworkInfo networkInfo = connMgr.getNetworkInfo(type);
//            return networkInfo != null && networkInfo.isConnected();
//        } else {
//            return isConnected(connMgr, type);
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private static boolean isConnected(@NonNull ConnectivityManager connMgr, int type) {
//        Network[] networks = connMgr.getAllNetworks();
//        NetworkInfo networkInfo;
//        for (Network mNetwork : networks) {
//            networkInfo = connMgr.getNetworkInfo(mNetwork);
//            if (networkInfo != null && networkInfo.getType() == type && networkInfo.isConnected()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static boolean isConnected() {
//        ConnectivityManager connMgr = (ConnectivityManager) App.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        return (networkInfo != null && networkInfo.isConnected());
//    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth(Activity activity) {
        View v = activity.getWindow().getDecorView();
        return v.getWidth();
    }

    public static int getScreenHeight(Activity activity) {
        View v = activity.getWindow().getDecorView();
        return v.getHeight();
    }

//    public static boolean isCSVVersionOld(String ver) {
//        String curVer = DataCache.getInstance().csvVersion;
//        int curV = Integer.parseInt(curVer.replace(".", ""));
//        int v = Integer.parseInt(ver.replace(".", ""));
//        Log.e("----csv version---->", curV + " <? " + v);
//        return curV < v;
//    }

//    public static boolean isVersionOld(String ver) {
//        String curVer = getAppVersionName();
//        int curV = Integer.parseInt(curVer.replace(".", ""));
//        int v = Integer.parseInt(ver.replace(".", ""));
//        Log.e("----ver---->", curV + " <? " + v);
//        return curV < v;
//    }

//    public static String getAppVersionName() {
//        String versionName = "";
//        Context context = App.getAppContext();
//        try {
//            // ---get the package info---
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
//            versionName = pi.versionName;
//            if (versionName == null || versionName.length() <= 0) {
//                return "";
//            }
//        } catch (Exception e) {
//            Log.e("VersionInfo", "Exception", e);
//        }
//        return versionName;
//    }

    /**
     * 获取获取几天的日期
     *
     * @param days 如果为3，意思是3天前的日期
     * @return
     */
    public static String getDateString(int days) {
        long time = System.currentTimeMillis() - days * 24 * 3600 * 1000L;//long now = android.os.SystemClock.uptimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = new Date(time);
        String t1 = format.format(d1);
        return t1;
    }

    public static boolean isPostProcess(String process) {
        String[] list = {"专色", "其它", "压型", "压折", "压纹", "折页", "烫金", "粘合", "装订", "覆膜", "过UV"};
        for (String str :
                list) {
            if (str.equals(process)) {
                return true;
            }
        }
        return false;
    }

    public static <T extends Activity> ProgressBar showProgressBar(T context) {
        ProgressBar bar = new ProgressBar(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        bar.setLayoutParams(params);

        ViewGroup vg = (ViewGroup) context.getWindow().getDecorView();
        vg.addView(bar);
        return bar;
    }

    public static ProgressBar showProgress(Context context, FrameLayout layout) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        ProgressBar bar = new ProgressBar(context);
        bar.setLayoutParams(params);
        layout.addView(bar);
        return bar;
    }

    public static void addToWindow(Activity context, View view) {
        getWindow(context).addView(view);
    }

    public static void removeFormWindow(Activity context, View view) {
        getWindow(context).removeView(view);
    }

    public static <T extends Activity> ViewGroup getWindow(T context) {
        return (ViewGroup) context.getWindow().getDecorView();
    }

    public static <T extends Activity> void showPrice(T context, String price) {
        int index = price.indexOf("计算结果");
        index += 5;
        SpannableStringBuilder builder = new SpannableStringBuilder(price);
        if (price.contains("计算结果")) {
            builder.setSpan(new ForegroundColorSpan(Color.RED), index, price.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {

        }
        final Dialog alertDialog = new AlertDialog.Builder(context).
                setTitle("报价结果").
                setMessage(builder).
                setIcon(R.mipmap.ic_launcher).
                setPositiveButton("知道了", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                }).create();
        alertDialog.show();
    }

    public static <T extends Activity> void alert(T context, String title, String content) {
        final Dialog alertDialog = new AlertDialog.Builder(context).
                setTitle(title).
                setMessage(content).
                setIcon(R.mipmap.ic_launcher).
                setPositiveButton("知道了", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                }).create();
        alertDialog.show();
    }

    public static String getPriceInputTitle(String title) {
        title = title.replace(" ", "");
        title = title.replace("：", "");
        return title;
    }

    public static <T extends Activity> void closeKeyboard(T context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(context.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 分享功能
     *
     * @param context       上下文
     * @param activityTitle Activity的名字
     * @param msgTitle      消息标题
     * @param msgText       消息内容
     * @param imgPath       图片路径，不分享图片则传null
     */
    public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText,
                                String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

}
