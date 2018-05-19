package me.huanghai.shanghanlun_android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableViewCell;

import java.util.List;

import me.huanghai.searchController.DataItem;
import me.huanghai.searchController.HH2SectionData;
import me.huanghai.searchController.Helper;
import me.huanghai.searchController.ShowFragment;
import me.huanghai.searchController.SingletonData;

public class MainFragment extends ShowFragment implements ActionSheet.ActionSheetListener {

    // 这4成员变量用来实现查看上下文功能
    ATableViewCell lastCell;
    String lastSearchText;
    int lastPostion;
    int lastTop;

    public MainFragment() {
        super();
        // TODO Auto-generated constructor stub
        resetData(SingletonData.getInstance().getContent());
        delegate = new SubDelegate();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setSearchText(String str) {
        searchText = str;
        if (str != null && Helper.isNumeric(str)) {
            data = dataBak;
            if (tableView != null) {
                isContentOpen = true;
                tableView.reloadData();
                tableView.enableHeaderView(isContentOpen);
                int n = Integer.valueOf(str);
                n = n > 398 ? 398 : n;
                n = n < 1 ? 1 : n;

                int num = 0;
                int i = 0;// 太阳篇前面有8章
                if (SingletonData.getInstance().getShowShanghan() > 1) {
                    for (int j = 0; j < 8; i++) {
                        HH2SectionData sd = data.get(j);
                        num += 1 + sd.getData().size();
                        j++;
                    }
                    i = 8;
                }
                System.out.println("num=" + num + ", n=" + n);
                int count = 0;
                while (count < n) {
                    HH2SectionData sd = data.get(i);
                    int tmp = sd.getData().size();
                    System.out.println("count + tmp =" + (count + tmp));
                    if (count + tmp < n) {
                        count += tmp;
                        num += 1 + tmp;
                    } else {
                        num += n - count;
                        break;
                    }
                    i++;
                }
                tableView.setSelectionFromTop(num, (int) (44 * tableView
                        .getResources().getDisplayMetrics().density));
//                tableView.smoothScrollToPositionFromTop(num, (int) (44 * tableView
//                        .getResources().getDisplayMetrics().density));
            }
            return;
        }
        super.setSearchText(str);
        SingletonData.getInstance().isSeeingContextInSearchMode = false;
    }

    @Override
    public void clickRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
        // super.clickRowAtIndexPath(tableView, indexPath);
        Log.e("----->","clickRow");
        DataItem item = data.get(indexPath.getSection()).getData()
                .get(indexPath.getRow());
        List<String> fangList = item.getFangList();
        boolean strIsNull = Helper.strLengh(searchText) == 0;
        String[] menuList = new String[fangList.size() + (strIsNull ? 3 : 4)];
        menuList[0] = "拷贝本条";
        menuList[1] = "拷贝本章全部内容";
        menuList[2] = "拷贝全部结果";
        for (int i = 0; i < fangList.size(); i++) {
            menuList[3 + i] = "查看“" + fangList.get(i) + "”";
        }
        if (!strIsNull) {
            menuList[fangList.size() + 3] = "查看上下文";
        }

        ActionSheet.createBuilder(getActivity(), getFragmentManager())
                .setCancelButtonTitle("取消").setOtherButtonTitles(menuList)
                .setCancelableOnTouchOutside(true)
                .setListener(MainFragment.this).show();
    }

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        // TODO Auto-generated method stub
        DataItem item = data.get(curIndexPath.getSection()).getData()
                .get(curIndexPath.getRow());
        if (index < 3) {
            if (index == 0) {
                Helper.putStringToClipboard(item.getAttributedText().toString());
            } else if (index == 1) {
                HH2SectionData section = data.get(curIndexPath.getSection());
                StringBuilder string = new StringBuilder(section.getHeader());
                string.append("\n");
                for (DataItem d : section.getData()) {
                    string.append(d.getAttributedText().toString());
                    string.append("\n");
                }
                Helper.putStringToClipboard(string.toString());
            } else if (index == 2) {
                StringBuilder string = new StringBuilder("宋板伤寒论");
                if (searchText != null) {
                    string.append(" 搜索“" + searchText + "”结果：\n");
                }
                for (HH2SectionData sec : data) {
                    string.append("\n");
                    string.append(sec.getHeader());
                    string.append("\n");
                    for (DataItem d : sec.getData()) {
                        string.append(d.getAttributedText().toString());
                        string.append("\n");
                    }
                }
                Helper.putStringToClipboard(string.toString());
            }
            return;
        } else {
            if (Helper.strLengh(searchText) > 0 && index == actionSheet.getOtherButtonTitles().length - 1) {
                NSIndexPath ip = item.getIndexPath();
                int top = lastCell.getTop();
                lastTop = top;
                lastSearchText = searchText;
                lastPostion = tableView.getPositionForView(lastCell);
                setSearchText("");
                searchEditText.setText("");
                tableView.reloadData();

                int pos = 0;
                for (int i = 0; i < ip.getSection(); i++) {
                    pos += data.get(i).getData().size() + 1;
                }
                pos += 1 + ip.getRow();
                tableView.setSelectionFromTop(pos, top);
                SingletonData.getInstance().isSeeingContextInSearchMode = true;
                Toast.makeText(getActivity(), "按后退键可返回之前的搜索", Toast.LENGTH_SHORT).show();

                return;
            }
            Intent intent = new Intent(getActivity(), MainActivity.class);
            String fang = actionSheet.getOtherButtonTitle(index);
            fang = fang.substring(3, fang.length() - 1);
            intent.putExtra("title", fang);
            intent.putExtra("isFang", "true");
            getActivity().startActivity(intent);
        }
    }

    public void goBack() {
        searchEditText.setText(lastSearchText);
//        setSearchText(lastSearchText);
        tableView.reloadData();
        tableView.setSelectionFromTop(lastPostion, lastTop);

    }

    private class SubDelegate extends SampleATableViewDelegate {
        @Override
        public void postDidSelectCell(ATableView tableView, ATableViewCell cell, NSIndexPath indexPath) {
            super.postDidSelectCell(tableView, cell, indexPath);
            lastCell = cell;
        }
    }
}
