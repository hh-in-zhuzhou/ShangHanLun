package me.huanghai.shanghanlun_android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.view.ATableView;

import me.huanghai.searchController.DataItem;
import me.huanghai.searchController.HH2SectionData;
import me.huanghai.searchController.ShowFragment;
import me.huanghai.searchController.SingletonData;

//import android.support.v4.app.FragmentTransaction;
//import android.R.integer;
//import android.app.ActionBar.LayoutParams;


public class MainFragment extends ShowFragment implements ActionSheet.ActionSheetListener {

    public MainFragment() {
        super(SingletonData.getInstance().getContent());
        // TODO Auto-generated constructor stub
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setSearchText(String str) {
        searchText = str;
        if (str != null && isNumeric(str)) {
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
                if (SingletonData.getInstance().getIsSimple() == false) {
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
            }
            return;
        }
        super.setSearchText(str);
    }

    @Override
    public void clickRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
        // super.clickRowAtIndexPath(tableView, indexPath);
        DataItem item = data.get(indexPath.getSection()).getData()
                .get(indexPath.getRow());
        String[] fangList = item.getFangList();
        String[] menuList = new String[fangList.length + 3];
        menuList[0] = "拷贝本条";
        menuList[1] = "拷贝本章全部内容";
        menuList[2] = "拷贝全部结果";
        for (int i = 0; i < fangList.length; i++) {
            menuList[3 + i] = "查看“" + fangList[i] + "”";
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
        if (index < 3) {
            DataItem item = data.get(curIndexPath.getSection()).getData()
                    .get(curIndexPath.getRow());
            if (index == 0) {
                putStringToClipboard(item.getAttributedText().toString());
            } else if (index == 1) {
                HH2SectionData section = data.get(curIndexPath.getSection());
                StringBuilder string = new StringBuilder(section.getHeader());
                string.append("\n");
                for (DataItem d : section.getData()) {
                    string.append(d.getAttributedText().toString());
                    string.append("\n");
                }
                putStringToClipboard(string.toString());
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
                    putStringToClipboard(string.toString());
                }
            }
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
