package me.huanghai.shanghanlun_android;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.internal.ATableViewCellAccessoryView.ATableViewCellAccessoryType;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSelectionStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellStyle;

import me.huanghai.searchController.SingletonData;

public class SettingsFragment extends Fragment {

    private ATableView tableView;
    private String version = "3.1 beta 6";

    private String[] about = {
            "当前版本",
            "版本特点：",
            "最后更新时间",
            "请联系作者",
            "欢迎进qq群",
            "官网",
            "检查有无新版发布",
    };
    private String[] aboutInfo = {
            version,
            "修正android 8下无法点出菜单bug",
            "2018-1-14",
            "23891995@qq.com",
            "464024993",
            "http://www.zzjapp.cn",
            "=>"
    };
    private String[] teach = {
            "1.输入1-398的数字，不会进行筛选，而是直接滑动定位到该条文",
            "2.输入多个关键词，需要以空格隔开，比如“甘草  大枣”，意为查询同时包含甘草和大枣的",
            "3.紧挨关键词前或后输入\"-\"，意为不包含该关键字。",
            "4.可用 # 或 . (英文符号)代替一个不好打的字",
            "5.搜索框输入f或者y将弹出辅助输入框"
    };
    private String[] thx = {
            "1、感谢“炙甘草”老师，一直以来无私的帮助我，并帮助一起校对了金匮要略",
            "2、感谢苏方达，是他提供的宋板伤寒论全部的内容，以及帮助校对和整理",
            "3、感谢群里的“微笑一生”朋友，是他帮助整理的本经和别录内容，才使得新版本能够早早的出炉。",
            "4、感谢联系过我的朋友，以及群里的各位朋友，你们的厚爱和支持，给了我莫大的支持和帮助。"
    };
    private String[] toggleShanghan = {
            "不显示伤寒论",
            "只显示398条",
            "显示完整宋板伤寒论"
    };
    private String[] toggleJinkui = {
            "不显示金匮要略",
            "显示默认版金匮要略"
    };
    private String[][] data = {
            about,
            toggleShanghan,
            toggleJinkui,
            teach,
            thx
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.settings, null);
        tableView = (ATableView) view.findViewById(R.id.tableview);
        tableView.init(ATableViewStyle.Grouped);
        tableView.setDataSource(new SampleATableViewDataSource());
        tableView.setDelegate(new SampleATableViewDelegate());
        int showShanghan = SingletonData.getInstance().getShowShanghan();
        int showJinkui = SingletonData.getInstance().getShowJinkui();

        return view;
    }

    public class SampleATableViewDataSource extends ATableViewDataSource {

        @Override
        public ATableViewCell cellForRowAtIndexPath(ATableView tableView,
                                                    NSIndexPath indexPath) {
            // TODO Auto-generated method stub
            final String cellIdentifier = "CellReuse";

            // ATableViewCellStyle.Default, Subtitle, Value1 & Value2 supported.
            ATableViewCellStyle style = ATableViewCellStyle.Value1;

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
            cell.getTextLabel().setText(data[indexPath.getSection()][indexPath.getRow()]);
            cell.getTextLabel().setMaxLines(10);
            cell.setAccessoryType(ATableViewCellAccessoryType.None);
            if (indexPath.getSection() == 0) {
                cell.getDetailTextLabel().setText(aboutInfo[indexPath.getRow()]);
            } else if (indexPath.getSection() == 1 && indexPath.getRow() == SingletonData.getInstance().getShowShanghan()) {
                cell.setAccessoryType(ATableViewCellAccessoryType.Checkmark);
                cell.getDetailTextLabel().setText("");
            } else if(indexPath.getSection() == 2 && indexPath.getRow() == SingletonData.getInstance().getShowJinkui()){
                cell.setAccessoryType(ATableViewCellAccessoryType.Checkmark);
                cell.getDetailTextLabel().setText("");
            } else {
                cell.getDetailTextLabel().setText("");
            }
            // set detail text. careful, detail text is not present on every
            // cell style.
            // null references are not as neat as in obj-c.

            return cell;
        }

        @Override
        public int numberOfRowsInSection(ATableView tableView, int section) {
            // TODO Auto-generated method stub
            return data[section].length;
        }

        public int numberOfSectionsInTableView(ATableView tableView) {
            return data.length;
        }

        public String titleForHeaderInSection(ATableView tableView, int section) {
            String[] header = {"关于", "关于伤寒论内容", "关于金匮要略内容", "使用说明", "致谢（不分顺序）"};
            return header[section];
        }

    }

    public class SampleATableViewDelegate extends ATableViewDelegate {
        public void didSelectRowAtIndexPath(ATableView tableView,
                                            NSIndexPath indexPath) {
            tableView.clearChoices();
            tableView.requestLayout();
            if (indexPath.getSection() == 0) {
                int row = indexPath.getRow();
                int qqRow = 4;
                if (row == qqRow) {
                    putStringToClipboard("464024993");
                    Toast.makeText(getActivity(), "群号已复制到剪贴板", Toast.LENGTH_SHORT).show();
                } else if (row == qqRow+1) {
                    final Uri uri = Uri.parse("http://www.zzjapp.cn");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                } else if (row == qqRow-1) {
                    putStringToClipboard("23891995");
                    Toast.makeText(getActivity(), "作者qq号已复制到剪贴板", Toast.LENGTH_SHORT).show();
                } else if (row == qqRow+2) {
                    final Uri uri = Uri.parse("http://www.zzjapp.cn/cur_version.php?ver=" + version);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            } else if (indexPath.getSection() == 1) {
                boolean changed = false;
                if (SingletonData.getInstance().getShowShanghan() != indexPath.getRow()) {
                    changed = true;
                }
                SingletonData.getInstance().setShowShanghan(indexPath.getRow());
                SingletonData.getInstance().savePreferences();
                tableView.reloadData();
                if (changed) {
                    refreshContent();
                }
            } else if(indexPath.getSection() == 2){
                boolean changed = false;
                if (SingletonData.getInstance().getShowJinkui() != indexPath.getRow()) {
                    changed = true;
                }
                SingletonData.getInstance().setShowJinkui(indexPath.getRow());
                SingletonData.getInstance().savePreferences();
                tableView.reloadData();
                if (changed) {
                    refreshContent();
                }
            }
        }
    }

    private void refreshContent(){
        SingletonData.getInstance().reReadData(getActivity());
    }

    private void putStringToClipboard(String text) {
        // Context context = getActivity().getApplicationContext();
        Context context = getActivity();
        ClipboardManager cbm = (ClipboardManager) context
                .getSystemService(context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("shangHanLun", text);
        cbm.setPrimaryClip(clipData);
    }
}
