package me.huanghai.searchController;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.internal.ATableViewHeaderFooterCell;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.uikit.UILabel;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSelectionStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellStyle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.huanghai.shanghanlun_android.ClearEditText;
import me.huanghai.shanghanlun_android.R;

public class ShowFragment extends Fragment implements TextWatcher, View.OnClickListener {
    protected String title;
    protected String searchText = null;
    protected String bookName;
    protected List<HH2SectionData> data;
    protected List<HH2SectionData> dataBak;

    protected View view;
    protected ClearEditText searchEditText;
    protected ATableView tableView;
    protected TextView numTips;
    protected TipsWindow tipsWindow;
    protected View titleTextView = null;

    protected Button selectBtn;
    protected LinearLayout selectbtnsWrapper;
    protected Button selectAllBtn;
    protected Button copyItemsBtn;
    protected Set<NSIndexPath> selectedItemsSet = new HashSet<>();

    protected int totalNum;
    protected boolean isContentOpen;
    protected NSIndexPath curIndexPath;
    protected SampleATableViewDataSource dataSource = new SampleATableViewDataSource();
    protected SampleATableViewDelegate delegate = new SampleATableViewDelegate();

    protected Set<NSIndexPath> selectedRows = new HashSet<>();

    public ShowFragment() {
    }

    public void resetData(List<HH2SectionData> d) {
        data = d;
        dataBak = d;
    }

    @Override
    public void onResume() {
        super.onResume();
        SingletonData.getInstance().curFragment = this;
        if (searchEditText != null) {
            Log.e("onResume:", "is not null '" + searchText + "'");
            if (searchText != null && searchText.length() > 0 && !searchText.equals(searchEditText.getText().toString())) {
                Log.e("onResume:", "refresh tableView");
                searchEditText.setText(data.size() > 0 ? searchText : "");
            }
        } else {
            Log.e("onResume:", "is null");
            setSearchText("");
        }
    }

    @Override
    public void onPause() {
        if (tipsWindow != null && tipsWindow.isShowing()) {
            tipsWindow.dismiss();
        }
        super.onPause();
    }

    boolean isOnSelectMode() {
        return selectBtn.getText().toString().equals("取消");
    }

    protected void togglePastMode() {
        boolean startSelect = selectBtn.getText().equals("选择");
        selectBtn.setText(startSelect ? "取消" : "选择");
        searchEditText.setVisibility(startSelect ? View.GONE : View.VISIBLE);
        selectbtnsWrapper.setVisibility(startSelect ? View.VISIBLE : View.GONE);
        if (titleTextView != null) {
            titleTextView.setVisibility(startSelect ? View.GONE : View.VISIBLE);
            searchEditText.setVisibility(View.GONE);
            numTips.setVisibility(View.GONE);
        }
        tableView.setAllowsMultipleSelection(startSelect);
        selectedRows.clear();
    }

    @Override
    public void onClick(View v) {
        if (v == selectBtn) {
            togglePastMode();
        } else if (v == selectAllBtn) {
            boolean startSelectAll = selectAllBtn.getText().equals("选择全部");
            selectAllBtn.setText(startSelectAll ? "反选全部" : "选择全部");
            int section = 0;
            for (HH2SectionData sec :
                    data) {
                int row = 0;
                for (DataItem item :
                        sec.getData()) {
                    if (startSelectAll) {
                        selectedRows.add(NSIndexPath.indexPathForRowInSection(row, section));
                    } else {
                        selectedRows.remove(NSIndexPath.indexPathForRowInSection(row, section));
                    }
                    row++;
                }
                section++;
            }
//            Log.e("selectAll", "size:" + selectedRows.size());
//            for (int i = 0; i < tableView.getChildCount(); i++) {
//                View cell = tableView.getChildAt(i);
//                if (cell instanceof ATableViewCell) {
//                    Log.e("cell", "start");
//                    if ((startSelectAll && !cell.isSelected()) ||
//                            (!startSelectAll && cell.isSelected())) {
//                        Log.e("cell", "perform click!");
//                        tableView.performItemClick(cell, i, tableView.getItemIdAtPosition(i));
//                    }
//                }
//            }
        } else if (v == copyItemsBtn) {
            if (selectedRows.size() == 0) {
                Toast.makeText(getActivity(), "您未选择要拷贝的条目", Toast.LENGTH_SHORT).show();
                return;
            }
            Object[] rows = selectedRows.toArray();
            java.util.Arrays.sort(rows, new Comparator<Object>() {
                @Override
                public int compare(Object lhs, Object rhs) {
                    NSIndexPath left = (NSIndexPath) lhs;
                    NSIndexPath right = (NSIndexPath) rhs;
                    if (left.getSection() != right.getSection()) {
                        return left.getSection() - right.getSection();
                    } else {
                        return left.getRow() - right.getRow();
                    }
                }
            });

            StringBuilder res = new StringBuilder();
            res.append("伤寒论\n");
            int lastSection = -1;
            for (Object ip :
                    rows) {
                NSIndexPath p = (NSIndexPath) ip;
                if (p.getSection() != lastSection) {
                    if (lastSection > -1) {
                        res.append("\n");
                    }
                    lastSection = p.getSection();
                    res.append(data.get(lastSection).getHeader());
                    res.append("\n");
                }
                res.append(data.get(lastSection).getData().get(p.getRow()).getAttributedText().toString());
                res.append("\n");
            }
            res.deleteCharAt(res.length() - 1);
            Helper.putStringToClipboard(res.toString());
            togglePastMode();
            Toast.makeText(getActivity(), "已拷贝到剪贴板", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String title = null;
        String yaoZheng = null;
        try {
            title = getActivity().getIntent().getExtras().getString("title");
            yaoZheng = getActivity().getIntent().getExtras().getString("yaoZheng");
        } catch (Exception e) {
            // TODO: handle exception
        }

        view = inflater.inflate(R.layout.activity_main, null);
        searchEditText = (ClearEditText) view.findViewById(R.id.searchEditText);
        numTips = (TextView) view.findViewById(R.id.numTips);
        selectBtn = (Button) view.findViewById(R.id.selectbtn);
        selectBtn.setOnClickListener(this);
//        selectAllBtn = (Button) view.findViewById(R.id.selectallbtn);
        copyItemsBtn = (Button) view.findViewById(R.id.copyitemsbtn);
//        selectAllBtn.setOnClickListener(this);
        copyItemsBtn.setOnClickListener(this);
        selectbtnsWrapper = (LinearLayout) view.findViewById(R.id.selectbtnswrapper);
        searchEditText.setNumTips(numTips);
        searchEditText.addTextChangedListener(ShowFragment.this);
        // title = "桂枝汤"; //这里有一个奇怪的问题，如果取消注释，会有奇怪的bug，原因不明
        if (title != null) {
            isContentOpen = true;
            boolean isYaoZheng = yaoZheng != null && yaoZheng.equals("true");
            if (isYaoZheng) {
                setYaoZheng(title);
            } else {
                setSearchText(String.format("f%s", title));
            }

            searchEditText.setVisibility(View.GONE);
            searchEditText.setNumTips(null);
            numTips.setVisibility(View.GONE);
            TextView textView = new TextView(getActivity()
                    .getApplicationContext());
            textView.setText(isYaoZheng ? String.format("%s药证", title) : title);
            textView.setTextSize(18);
            textView.setGravity(Gravity.CENTER);
            FrameLayout layout = (FrameLayout) view.findViewById(R.id.titlebar);
            layout.addView(textView);
            titleTextView = textView;
        }

        tableView = (ATableView) view.findViewById(R.id.tableview);
        tableView.init(ATableViewStyle.Plain);
        tableView.setDataSource(dataSource);
        tableView.setDelegate(delegate);
        tableView.enableHeaderView(isContentOpen);

        return view;
    }

    public boolean getIsContentOpen() {
        return isContentOpen;
    }

    public void setIsContentOpen(boolean open) {
        isContentOpen = open;
        tableView.reloadData();
    }

    public static List<String> getFangListUsesYao(String s) {
        Map<String, String> dict = SingletonData.getInstance().yaoAliasDict;
        String yao = Helper.getAliasString(dict, s);
        List<String> fangList = new ArrayList<>();
        for (HH2SectionData sec : SingletonData.getInstance().getFang()) {
            for (DataItem item :
                    sec.getData()) {
                for (String y :
                        item.getYaoList()) {
                    if (Helper.getAliasString(dict, y).equals(yao)) {
                        fangList.add(item.getFangList()[0].toString());
                        break;
                    }
                }
            }
        }
        return fangList;
    }

    public void setYaoZheng(final String s) {
        final List<String> fangList = getFangListUsesYao(s);
        final Map<String, String> fangDict = SingletonData.getInstance().fangAliasDict;
        List<HH2SectionData> res = Helper.searchText(dataBak, new DataItemCompare() {
            @Override
            public boolean useThisItem(DataItem item) {
                for (String fang : fangList) {
                    for (String fangInner :
                            item.getFangList()) {
                        if (fang.equals(Helper.getAliasString(fangDict, fangInner))) {
                            return true;
                        }
                    }
                }
                for (String yao :
                        item.getYaoList()) {
                    if (Helper.getAliasString(SingletonData.getInstance().yaoAliasDict, s).equals(yao)) {
                        return true;
                    }
                }
                return false;
            }
        });
        resetData(res);
    }

    public void setSearchText(String str) {
        searchText = str;
        // 搜索逻辑
        if (str == null || str.length() == 0 || Helper.isNumeric(str)) {
            data = dataBak;
            if (tableView != null) {
                tableView.reloadData();
            }
            return;
        }

        // 收集多关键字
        String[] keyWords_ = str.split(" ");
        List<String> keyWords = new ArrayList<String>();
        for (int i = 0; i < keyWords_.length; i++) {
            if (keyWords_[i].length() > 0) {
                keyWords.add(keyWords_[i]);
            }
        }

        // 开始筛选：
        totalNum = 0;
        ArrayList<HH2SectionData> array = new ArrayList<HH2SectionData>();
        Map<String, String> yaoDict = SingletonData.getInstance()
                .getYaoAliasDict();
        Map<String, String> fangDict = SingletonData.getInstance()
                .getFangAliasDict();
        List<String> allYao = SingletonData.getInstance().getAllYao();
        List<String> allFang = SingletonData.getInstance().getAllFang();
        for (int i = 0; i < dataBak.size(); i++) {
            HH2SectionData sd = dataBak.get(i);
            ArrayList<DataItem> arr = new ArrayList<DataItem>();
            for (int j = 0; j < sd.getData().size(); j++) {
                DataItem item = sd.getData().get(j);
                boolean found = true;
                boolean exclude = false;
                for (String text_ : keyWords) {

                    exclude = false;
                    String text = new String(text_);
                    // 判断是否有 - 前缀或后缀
                    if (text.startsWith("-") || text.endsWith("-")) {
                        text = text.replace("-", "");
                        exclude = true;
                    }

                    text = text.replace("#", ".");

                    int type = -1;
                    Map<String, String> dict = null;
                    String right = null;
                    List<String> allList = null;
                    if (text.contains("y")) {
                        type = 0;
                        dict = yaoDict;
                        allList = allYao;
                    } else if (text.contains("f")) {
                        type = 1;
                        dict = fangDict;
                        allList = allFang;
                    }
                    text = text.replace("y", "");
                    text = text.replace("f", "");

                    if (type >= 0) {
                        right = dict.get(text);
                        if (right == null) {
                            right = text;
                        }
                    }
                    // System.out.println("keywords:" + text_);
                    String sourceString = item.getAttributedText().toString();

                    if (text.length() > 0 && text.charAt(0) == '*') {
                        text = text.substring(1);
                    }
                    if (text.length() == 2 && text.charAt(1) == '*') {
                        text = text.substring(0, 1);
                    }
                    Pattern p = null;
                    try {
                        p = Pattern.compile(text);
                    } catch (Exception e) {
                        text = ".";
                        p = Pattern.compile(text);
                    }

                    // 开始匹配
                    if (type < 0) {
                        found = p.matcher(sourceString).find();
                    } else if (type >= 0) {
                        String[] list = type == 0 ? item.getYaoList()
                                : item.getFangList();
                        found = false;
                        for (String item_ : list) {
                            String it = dict.get(item_);
                            if (it == null) {
                                it = item_;
                            }
                            found = p.matcher(it).find();
                            if (found) {
                                break;
                            }
                        }
                    }
                    // 如果不匹配发生了一次，即表示不匹配
                    // 如此赋值是表示没有找到，继续找～（因为两者相等表示没找到）
                    if (found == exclude) {
                        found = exclude;
                        break;
                    }

                    // 如果找到了便染色
                    boolean onlyOnce = true;
                    if (found && !exclude) {
                        item = item.getCopy();
                        if (type < 0) {
                            for (int k = 0; k < text.length(); k++) {
                                if (text.charAt(k) != '.') {
                                    onlyOnce = false;
                                    break;
                                }
                            }
                            // 染色逻辑
                            Matcher m = p.matcher(sourceString);
                            SpannableStringBuilder builder = new SpannableStringBuilder(
                                    item.getAttributedText());
                            int lastPos = 0;
                            while (m.find()) {
                                int index = m.start() + lastPos;
                                int end = m.end() + lastPos;
                                if (index == end) {
                                    lastPos = end + 1;
                                    if (lastPos > sourceString.length() - 1) {
                                        break;
                                    }
                                    m = p.matcher(sourceString.substring(lastPos));
                                    continue;
                                }

                                builder.setSpan(
                                        new ForegroundColorSpan(Color.RED),
                                        index,
                                        end,
                                        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
                                if (onlyOnce) {
                                    break;
                                }
                                lastPos = end;
                                if (lastPos > sourceString.length() - 1) {
                                    break;
                                }
                                m = p.matcher(sourceString.substring(lastPos));
                            }
                            item.setAttributedText(builder);
                        } else if (type >= 0) {
                            SpannableStringBuilder builder = new SpannableStringBuilder(
                                    item.getAttributedText());
                            ClickableSpan[] spans = builder.getSpans(0,
                                    builder.length(), ClickableSpan.class);
                            for (ClickableSpan span : spans) {
                                int start = builder.getSpanStart(span);
                                int end = builder.getSpanEnd(span);
                                String unit = builder.subSequence(start, end)
                                        .toString();
                                String left = dict.get(unit);
                                if (left == null) {
                                    left = unit;
                                }
                                Pattern p_ = Pattern.compile(right);
                                if (p_.matcher(left).find()
                                        && allList.contains(left)) {
                                    ForegroundColorSpan someSpan = new ForegroundColorSpan(
                                            Color.RED);
                                    builder.setSpan(someSpan, start, end,
                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    item.setAttributedText(builder);
                                }
                            }
                        }
                    }
                }
                // 加入
                if (found != exclude) {
                    // System.out.println("found:"
                    // + item.getAttributedText().toString());
                    arr.add(item);
                    totalNum++;
                }
            }
            if (arr.size() > 0) {
                HH2SectionData d = new HH2SectionData(arr, sd.getSection(),
                        sd.getHeader());
                array.add(d);
            }
        }
        data = array;
        if (numTips != null) {
            numTips.setText(String.format("%d个结果", totalNum));
        }
        if (tableView != null) {
            tableView.reloadData();
        }
    }

    /*
     * ATableView DataSource
     */
    public class SampleATableViewDataSource extends ATableViewDataSource {

        @Override
        public ATableViewCell cellForRowAtIndexPath(final ATableView tableView,
                                                    final NSIndexPath indexPath) {
            // TODO Auto-generated method stub
            final String cellIdentifier = "CellReuse";

            // ATableViewCellStyle.Default, Subtitle, Value1 & Value2 supported.
            ATableViewCellStyle style = ATableViewCellStyle.Default;

            // reuse cells. if the table has different row types it will result
            // on performance issues.
            // Use ATableViewDataSourceExt on this cases.
            // please notice we ask the datasource for a cell instead the table
            // as we do on ios.
            ATableViewCell cell = dequeueReusableCellWithIdentifier(cellIdentifier);
            if (cell == null) {
                cell = new ATableViewCell(style, cellIdentifier, getActivity());
                // ATableViewCellSelectionStyle.Blue, Gray & None supported. It
                // defaults to Blue.
                cell.setSelectionStyle(ATableViewCellSelectionStyle.Blue);
            }

            // set title.
            HH2SectionData sd = data.get(indexPath.getSection());
            UILabel label = cell.getTextLabel();
            label.setText(sd.getData().get(indexPath.getRow())
                    .getAttributedText());

            label.setMaxLines(320);
            label.setMovementMethod(LocalLinkMovementMethod.getInstance());
            // label.setHighlightColor(Color.BLUE);
            // set detail text. careful, detail text is not present on every
            // cell style.
            // null references are not as neat as in obj-c.

            return cell;
        }

        @Override
        public int numberOfRowsInSection(ATableView tableView, int section) {
            // TODO Auto-generated method stub
            HH2SectionData sd = data.get(section);
            return isContentOpen ? sd.getData().size() : 0;
        }

        public int numberOfSectionsInTableView(ATableView tableView) {
            return data.size();
        }

        // public String titleForHeaderInSection(ATableView tableView, int
        // section) {
        // HH2SectionData sd = data.get(section);
        // return dataBak.size() == 1 ? null : sd.getHeader();
        // }

        public SpannableStringBuilder spannerbleTitleForHeaderInSection(
                ATableView tableView, int section) {
            HH2SectionData sd = data.get(section);
            String text = sd.getHeader();
            SpannableStringBuilder builder = new SpannableStringBuilder(text);
            builder.setSpan(new ForegroundColorSpan(Color.BLUE), 0,
                    text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (text.contains("$")) {
                int index = text.indexOf("$") + 1;
                builder.setSpan(new ForegroundColorSpan(Color.RED), index,
                        text.length(),
                        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.setSpan(new RelativeSizeSpan(0.55f), index,
                        text.length(),
                        SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
                builder.replace(index - 1, index, "\n");
            }

            return builder;
        }
    }

    /*
     * ATableView Delegate
     */
    public class SampleATableViewDelegate extends ATableViewDelegate {
        public int heightForHeaderInSection(ATableView tableView, int section) {
            // return dataBak.size() == 1 ?
            // ATableViewCell.LayoutParams.UNDEFINED
            // : 44;
            return 44;
        }

        public int heightForRowAtIndexPath(ATableView tableView,
                                           NSIndexPath indexPath) {
            return ATableViewCell.LayoutParams.UNDEFINED;
        }

//        @Override
//        public void willDisplayCellForRowAtIndexPath(ATableView tableView, ATableViewCell cell, NSIndexPath indexPath) {
//            if (isOnSelectMode()) {
//
//            }
//        }


        @Override
        public void postDidSelectCell(ATableView tableView, ATableViewCell cell, NSIndexPath indexPath) {
            if (isOnSelectMode()) {
                if (cell.isSelected()) {
                    selectedRows.add(indexPath);
                    Log.e("did select!", "size:" + selectedRows.size());
                } else {
                    selectedRows.remove(indexPath);
                    Log.e("did de select!", "size:" + selectedRows.size());
                }
            }
        }

        public void didSelectRowAtIndexPath(ATableView tableView,
                                            NSIndexPath indexPath) {
            curIndexPath = indexPath;
            hideKeyboard();
            if (isOnSelectMode()) {
//                selectedItemsSet.add(curIndexPath);
//                tableView.getCheckedItemPositions();
//                Log.e("didSelect", "inOnSelectMode");
                return;
            }
            clickRowAtIndexPath(tableView, indexPath);
            tableView.clearChoices();
            tableView.requestLayout();
        }

        public void clickHeaderForSection(ATableView tableView, View view,
                                          int section) {
            // Log.d("log:", String.format("click header %d", section));
            hideKeyboard();
            int top = 0;
            if (view != null) {
                top = view.getTop();
            }
            isContentOpen = !isContentOpen;
            tableView.enableHeaderView(isContentOpen);
            tableView.reloadData();
            if (isContentOpen) {
                tableView.setSelectionFromTop(getHeaderPosFromSection(section),
                        top);
            } else {
                tableView.setSelectionFromTop(section, top);
            }
        }

        private int getHeaderPosFromSection(int section) {
            int pos = 0;
            for (int s = 0; s < section; s++) {
                pos += data.get(s).getData().size() + 1;
            }
            return pos;
        }
    }

    public void clickRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
        HH2SectionData d = data.get(indexPath.getSection());
        DataItem item = d.getData().get(indexPath.getRow());
        Helper.putStringToClipboard(item.getAttributedText().toString());
        Toast.makeText(getActivity(), "本条已复制到剪贴板", Toast.LENGTH_SHORT).show();
    }

    public void hideKeyboard() {
        searchEditText.clearFocus();
        // getActivity().getWindow().setSoftInputMode(
        // WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
        // this is a valuable method.
        // imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),0);
        // tableView.requestFocus();
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(final CharSequence s, final int start, int before, final int count) {
        // TODO Auto-generated method stub
        Log.e("textChaned:", s + ",start=" + start + ",before=" + before + ",count=" + count);
        setSearchText(s.toString());
        String pre = s.toString().substring(0, searchEditText.getSelectionStart());
        Log.e("pre:", "'" + pre + "'");
        String[] words = pre.split(" ");
        if (words.length > 0 && pre.length() > 0 && pre.charAt(pre.length() - 1) != ' ') {
            for (String w :
                    words) {
                Log.e("words:", "'" + w + "'");
            }
            final String lastWord = words[words.length - 1];

            if (lastWord.contains("y") || lastWord.contains("f")) {

                if (tipsWindow == null || !tipsWindow.isShowing()) {
                    tipsWindow = new TipsWindow();
                }
                tipsWindow.setClickLink(new ClickLink() {
                    // 这里是点击下拉框后修改搜索框文字的方法
                    public void clickThis(TextView tv, ClickableSpan clickableSpan) {
                        processClickTips(tv, s, start, count, lastWord.contains("y") ? 'y' : 'f');
                    }

                    @Override
                    public void clickYaoLink(TextView tv, ClickableSpan clickableSpan) {
                        clickThis(tv, clickableSpan);
                    }

                    @Override
                    public void clickFangLink(TextView tv, ClickableSpan clickableSpan) {
                        clickThis(tv, clickableSpan);
                    }
                });
                tipsWindow.setSearchText(lastWord);

                if (!tipsWindow.isShowing) {
                    tipsWindow.show(getFragmentManager());
                }
            } else if (tipsWindow != null && tipsWindow.isShowing()) {
                tipsWindow.dismiss();
            }
        } else if (tipsWindow != null && tipsWindow.isShowing()) {
            tipsWindow.dismiss();
        }
    }

    protected void processClickTips(TextView tv, CharSequence s, int start, int count, char cls) {
        Log.e("click:textChaned:", s + ",start=" + start);
        boolean charInAdd = count > 0;
        String unit = tv
                .getText()
                .subSequence(tv.getSelectionStart(),
                        tv.getSelectionEnd()).toString();
        Log.e("tapped:", unit);
        int mid = charInAdd ? start + count : start;

        int pos = Math.min(searchEditText.getSelectionStart(), s.length());
        String firstWord = s.toString().substring(0, pos);
        Log.e("firstWord:", firstWord);
        int fPos = firstWord.lastIndexOf(cls);

        String first = s.toString().substring(0, fPos + 1);
        String end = s.toString().substring(mid);
        String search = first + unit + " " + end;
        Log.e("search->:", "first:" + first + ", end:" + end + ",search:" + search);
        searchEditText.setText(search);
        searchEditText.setSelection(first.length() + unit.length() + 1);
        setIsContentOpen(true);
        setSearchText(search);
        if (tipsWindow != null && tipsWindow.isShowing) {
            tipsWindow.dismiss();
        }
    }
}
