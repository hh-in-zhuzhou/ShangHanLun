package me.huanghai.shanghanlun_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableViewCell;

import java.util.ArrayList;
import java.util.List;

import me.huanghai.searchController.DataItem;
import me.huanghai.searchController.HH2SectionData;
import me.huanghai.searchController.Helper;
import me.huanghai.searchController.ShowFragment;
import me.huanghai.searchController.SingletonData;

public class FangFragment extends ShowFragment implements ActionSheet.ActionSheetListener {
    protected NSIndexPath curIndexPath;
    protected List<SpannableStringBuilder> linksStrings;

    public FangFragment() {
        super();
        // TODO Auto-generated constructor stub
        resetData(SingletonData.getInstance().getFang());
        isContentOpen = true;
        dataSource = new SubDataSource();
        delegate = new SubDelegate();
        linksStrings = new ArrayList<SpannableStringBuilder>();
        resetLinkStrings();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        searchEditText.setHint("输入搜索词，用空格隔开");

        return view;
    }

    public void setSearchText(String str) {
        super.setSearchText(str);
        resetLinkStrings();
    }

    public void resetLinkStrings() {
        for (int i = 0; i < data.size(); i++) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            List<DataItem> list = data.get(i).getData();
            for (DataItem item : list) {
                builder.append(Helper.renderText(String.format("$f{%s}，",
                        item.getFangList()[0])));
            }
            if (i < linksStrings.size()) {
                linksStrings.set(i, builder);
            } else {
                linksStrings.add(builder);
            }
        }
    }

    private String getFangName(String text) {
        return text.substring(text.indexOf("、") + 1, text.indexOf(" "));
    }

    @Override
    public void clickRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
        // super.clickRowAtIndexPath(tableView, indexPath);
        curIndexPath = indexPath;
        DataItem item = data.get(indexPath.getSection()).getData()
                .get(indexPath.getRow());
        String fang = item.getAttributedText().toString();
        fang = getFangName(fang);
        int num = isContentOpen ? 7 : 1;
        String[] menuList = new String[num];
        if (isContentOpen) {
            menuList[0] = "拷贝“" + fang + "”内容";
            menuList[1] = "拷贝全部结果";
            menuList[2] = "仅拷贝“" + fang + "”方名";
            menuList[3] = "拷贝全部结果的方名";
            menuList[4] = "仅拷贝“" + fang + "”配方";
            menuList[5] = "拷贝全部结果的配方";
            menuList[6] = "查找“" + fang + "”条文";
        } else {
            menuList[0] = "拷贝结果";
        }

        ActionSheet.createBuilder(getActivity(), getFragmentManager())
                .setCancelButtonTitle("取消").setOtherButtonTitles(menuList)
                .setCancelableOnTouchOutside(true)
                .setListener(FangFragment.this).show();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        // TODO Auto-generated method stub
        if (!isContentOpen) {
            Helper.putStringToClipboard(linksStrings.get(curIndexPath.getSection())
                    .toString());
            return;
        }
        String fang = actionSheet.getOtherButtonTitle(index);
        fang = fang.substring(3, fang.length() - 3);
        if (index == 6) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.putExtra("title", fang);
            intent.putExtra("isFang", "false");
            getActivity().startActivity(intent);
            return;
        }
        DataItem item = data.get(curIndexPath.getSection()).getData()
                .get(curIndexPath.getRow());
        String fangName = fang;
        fang = item.getAttributedText().toString();
        if (index == 0) {
            Helper.putStringToClipboard(item.getAttributedText().toString());
        } else if (index == 1) {
            StringBuilder string = new StringBuilder("伤寒论113方");
            if (searchText != null) {
                string.append(" 搜索“" + searchText + "”结果：\n");
            }
            for (HH2SectionData section : data) {
                for (DataItem d : section.getData()) {
                    string.append(d.getAttributedText().toString());
                    string.append("\n");
                }
            }
            Helper.putStringToClipboard(string.toString());
        } else if (index == 2) {
            Helper.putStringToClipboard(fangName);
        } else if (index == 3) {
            StringBuilder string = new StringBuilder("伤寒论113方");
            if (searchText != null) {
                string.append(" 搜索“" + searchText + "”结果：\n");
            }
            for (HH2SectionData section : data) {
                for (DataItem d : section.getData()) {
                    string.append(getFangName(d.getAttributedText().toString()));
                    string.append("\n");
                }
            }
            Helper.putStringToClipboard(string.toString());
        } else if (index == 4) {
            String[] list = fang.split("\n");
            Helper.putStringToClipboard(list[0]);
        } else {
            StringBuilder string = new StringBuilder("伤寒论113方");
            if (searchText != null) {
                string.append(" 搜索“" + searchText + "”结果：\n");
            }
            for (HH2SectionData section : data) {
                for (DataItem d : section.getData()) {
                    fang = d.getAttributedText().toString();
                    String[] list = fang.split("\n");
                    string.append(list[0]);
                    string.append("\n");
                }
            }
            Helper.putStringToClipboard(string.toString());
        }
    }

    public class SubDataSource extends SampleATableViewDataSource {
        public SpannableStringBuilder spannerbleTitleForHeaderInSection(
                ATableView tableView, int section) {
            SpannableStringBuilder builder = super
                    .spannerbleTitleForHeaderInSection(tableView, section);
            SpannableString append = new SpannableString(String.format(
                    "   凡%d方", data.get(section).getData().size()));
            append.setSpan(new ForegroundColorSpan(Color.BLUE), 0,
                    append.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(append);
            return builder;
        }

        public int numberOfRowsInSection(ATableView tableView, int section) {
            // TODO Auto-generated method stub
            HH2SectionData sd = data.get(section);
            return isContentOpen ? sd.getData().size() : 1;
        }

        public ATableViewCell cellForRowAtIndexPath(ATableView tableView,
                                                    NSIndexPath indexPath) {
            ATableViewCell cell = super.cellForRowAtIndexPath(tableView,
                    indexPath);
            if (isContentOpen) {
                return cell;
            }
            // SpannableStringBuilder builder = new SpannableStringBuilder();
            // List<DataItem> list = data.get(indexPath.getSection()).getData();
            // for (DataItem item : list) {
            // builder.append(item.renderText(String.format("$f{%s}，",
            // item.getFangList()[0])));
            // }
            // linksString.put(indexPath, builder);
            cell.getTextLabel().setText(
                    linksStrings.get(indexPath.getSection()));
            return cell;
        }
    }

    public class SubDelegate extends SampleATableViewDelegate {
        public void clickHeaderForSection(ATableView tableView, View view,
                                          int section) {
            // Log.d("log:", String.format("click header %d", section));
            super.clickHeaderForSection(tableView, view, section);
            tableView.enableHeaderView(true);
            if (!isContentOpen) {
                int top = 0;
                if (view != null) {
                    top = view.getTop();
                }
                tableView.setSelectionFromTop(section * 2, top);
            }
        }
    }
}
