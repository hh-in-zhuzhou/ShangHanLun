package me.huanghai.shanghanlun_android;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableViewCell;

import me.huanghai.searchController.DataItem;
import me.huanghai.searchController.HH2SectionData;
import me.huanghai.searchController.Helper;
import me.huanghai.searchController.ShowFragment;
import me.huanghai.searchController.SingletonData;

public class YaoFragment extends ShowFragment {
    protected NSIndexPath curIndexPath;
    protected SpannableStringBuilder linksString;

    public YaoFragment() {
        super();
        // TODO Auto-generated constructor stub
        resetData(SingletonData.getInstance().getYaoData());
//        isContentOpen = true;
        dataSource = new SubDataSource();
        delegate = new SubDelegate();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        searchEditText.setHint("输入搜索词，用空格隔开");

        return view;
    }

    public void clickRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
        if (isContentOpen) {
            super.clickRowAtIndexPath(tableView, indexPath);
        } else {
            Helper.putStringToClipboard(linksString.toString());
            Toast.makeText(getActivity(), "已复制到剪贴板", Toast.LENGTH_SHORT).show();
        }
    }

    public class SubDataSource extends SampleATableViewDataSource {
        public SpannableStringBuilder spannerbleTitleForHeaderInSection(
                ATableView tableView, int section) {
            SpannableStringBuilder builder = super
                    .spannerbleTitleForHeaderInSection(tableView, section);
            SpannableString append = new SpannableString(String.format(
                    "   凡%d药", data.get(section).getData().size()));
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
//                cell.getTextLabel().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                return cell;
            }
            SpannableStringBuilder builder = new SpannableStringBuilder();
            for (HH2SectionData sec : data) {
                for (DataItem item : sec.getData()) {
                    builder.append(Helper.renderText(String.format("$u{%s}，",
                            item.getYaoList()[0])));
                }
            }
            linksString = builder;
            cell.getTextLabel().setText(builder);
//            cell.getTextLabel().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            return cell;
        }
    }

    public class SubDelegate extends SampleATableViewDelegate {
        public void clickHeaderForSection(ATableView tableView, View view,
                                          int section) {
            // Log.d("log:", String.format("click header %d", section));
            super.clickHeaderForSection(tableView, view, section);
            tableView.enableHeaderView(true);
        }
    }
}
