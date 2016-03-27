package me.huanghai.searchController;

import android.app.Fragment;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.widget.Toast;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.uikit.UILabel;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSelectionStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellStyle;

import java.util.ArrayList;
import java.util.Map;

public class ShowFang {
    private String fangName;
    private ArrayList<ArrayList<SpannableStringBuilder>> data;
    private ArrayList<String> headers;

    private SampleATableViewDataSource ds = new SampleATableViewDataSource();
    private SampleATableViewDelegate dl = new SampleATableViewDelegate();

    public SampleATableViewDataSource getDataSource() {
        return ds;
    }

    public SampleATableViewDelegate getDelegate() {
        return dl;
    }

    public void putCopyStringsToClipboard() {
        if (data == null || data.size() == 0) {
            return;
        }
        StringBuilder string = new StringBuilder();
        int i = 0;
        for (ArrayList<SpannableStringBuilder> arr : data) {
            string.append("\n");
            string.append(headers.get(i));
            string.append("\n");
            for (SpannableStringBuilder d : arr) {
                string.append(d.toString());
                string.append("\n");
            }
            i++;
        }
        Helper.putStringToClipboard(string.toString());
    }

    public ShowFang(String fangName_, boolean onlyShowRelatedContent) {
        fangName = fangName_;
        SingletonData single = SingletonData.getInstance();
        data = new ArrayList<ArrayList<SpannableStringBuilder>>();
        headers = new ArrayList<String>();
        boolean found = false;
        Map<String, String> fangDict = single.getFangAliasDict();
        String right = fangDict.get(fangName_);
        if (right == null) {
            right = fangName_;
        }

        if (!onlyShowRelatedContent) {
            for (HH2SectionData sec : single.getFang()) {
                found = false;
                for (DataItem item : sec.getData()) {
                    String string = item.getFangList()[0];
                    String left = fangDict.get(string);
                    if (left == null) {
                        left = string;
                    }
                    if (left.equals(right)) {
                        headers.add(sec.getHeader());
                        ArrayList<SpannableStringBuilder> obj = new ArrayList<SpannableStringBuilder>();
                        obj.add(item.getAttributedText());
                        data.add(obj);
                        found = true;
                        break;
                    }
                    if (found) {
                        break;
                    }
                }
            }
        }

        ArrayList<SpannableStringBuilder> obj = null;
        for (HH2SectionData sec : single.getContent()) {
            obj = null;
            for (DataItem item : sec.getData()) {
                for (String string : item.getFangList()) {
                    String left = fangDict.get(string);
                    if (left == null) {
                        left = string;
                    }
                    if (left.equals(right)) {
                        if (obj == null) {
                            obj = new ArrayList<SpannableStringBuilder>();
                        }
                        obj.add(item.getAttributedText());
                        break;
                    }
                }
            }
            if (obj != null) {
                headers.add(sec.getHeader());
                data.add(obj);
            }
        }
    }

    public class SampleATableViewDataSource extends ATableViewDataSource {

        @Override
        public ATableViewCell cellForRowAtIndexPath(ATableView tableView,
                                                    NSIndexPath indexPath) {
            // TODO Auto-generated method stub
            final String cellIdentifier = "CellReuse2";

            // ATableViewCellStyle.Default, Subtitle, Value1 & Value2 supported.
            ATableViewCellStyle style = ATableViewCellStyle.Default;

            // reuse cells. if the table has different row types it will result
            // on performance issues.
            // Use ATableViewDataSourceExt on this cases.
            // please notice we ask the datasource for a cell instead the table
            // as we do on ios.
            ATableViewCell cell = dequeueReusableCellWithIdentifier(cellIdentifier);
            if (cell == null) {
                cell = new ATableViewCell(style, cellIdentifier,
                        MyApplication.getAppContext());
                // ATableViewCellSelectionStyle.Blue, Gray & None supported. It
                // defaults to Blue.
                cell.setSelectionStyle(ATableViewCellSelectionStyle.Blue);
            }

            // set title.
            ArrayList<SpannableStringBuilder> sd = data.get(indexPath
                    .getSection());
            UILabel label = cell.getTextLabel();
            label.setBackgroundColor(Color.WHITE);
            label.setText(sd.get(indexPath.getRow()));

            label.setMaxLines(320);
            label.setMovementMethod(LocalLinkMovementMethod.getInstance());

            return cell;
        }

        @Override
        public int numberOfRowsInSection(ATableView tableView, int section) {
            // TODO Auto-generated method stub
            return data.get(section).size();
        }

        public int numberOfSectionsInTableView(ATableView tableView) {
            return data.size();
        }

        public String titleForHeaderInSection(ATableView tableView, int section) {
            return headers.get(section);
        }

    }

    public class SampleATableViewDelegate extends ATableViewDelegate {

    }
}
