package me.huanghai.shanghanlun_android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableView.ATableViewStyle;
import com.nakardo.atableview.view.ATableViewCell;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSelectionStyle;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellStyle;

import java.util.ArrayList;

public class UnitFragment extends Fragment {

    private String[] dataStrings = {"汉制一两约为 15.625克", "汉制一两为 24铢",
            "汉制一铢为 0.65克", "汉制一升约为 200毫升", "汉制一合为 20毫升", "杏仁一枚约为 0.4克",
            "1石=四钧＝29760克", "1钧=三十斤＝7440克", "1斤=248克", "1斤=16两", "1斤=液体250毫升",
            "1两=15.625克", "1两=24铢", "1升=液体200毫升", "1合=20毫升", "1圭=0.5克",
            "1龠=10毫升", "1撮=2克", "1方寸匕=金石类2.74克", "1方寸匕=药末约2克", "1方寸匕=草木类药末约1克",
            "半方寸匕=一刀圭=一钱匕=1.5克", "一钱匕=1.5-1.8克", "一铢=100个黍米的重量", "一分=3.9-4.2克",
            "梧桐子大约为 黄豆大", "蜀椒一升=50克", "葶力子一升=60克", "吴茱萸一升=50克", "五味子一升=50克",
            "半夏一升=130克", "虻虫一升=16克", "附子大者1枚=20-30克", "附子中者1枚=15克",
            "强乌头1枚小者=3克", "强乌头1枚大者=5-6克", "杏仁大者10枚=4克", "栀子10枚平均15克",
            "瓜蒌大小平均1枚=46克", "枳实1枚约14.4克", "石膏鸡蛋大1枚约40克", "厚朴1尺约30克",
            "竹叶一握约12克", "1斛=10斗＝20000毫升", "1斗=10升＝2000毫升", "1升=10合＝200毫升",
            "1合=2龠＝20毫升", "1龠=5撮＝10毫升", "1撮=4圭＝2毫升", "1圭=0.5毫升",
            "1引=10丈＝2310厘米", "1丈=10尺＝231厘米", "1尺=10寸＝23.1厘米", "1寸=10分＝2.31厘米",
            "1分=0.231厘米"};
    protected View view;
    protected ClearEditText searchEditText;
    protected ATableView tableView;
    protected TextView numTips;
    protected ArrayAdapter<String> lvAdapter;
    protected ArrayList<SpannableStringBuilder> dataToShow;
    protected ArrayList<SpannableStringBuilder> coloredData;
    protected ArrayList<String> fangStrings;
    protected Boolean isSearch = true;
    protected String searchTextString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.activity_main, null);
        searchEditText = (ClearEditText) view.findViewById(R.id.searchEditText);
        numTips = (TextView) view.findViewById(R.id.numTips);
        searchEditText.setNumTips(numTips);
        searchEditText.setVisibility(View.GONE);
        TextView textView = new TextView(getActivity().getApplicationContext());
        textView.setText("汉制单位");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(22);
        textView.setGravity(Gravity.CENTER);
        FrameLayout layout = (FrameLayout) view.findViewById(R.id.titlebar);
        layout.addView(textView);

        tableView = (ATableView) view.findViewById(R.id.tableview);
        tableView.init(ATableViewStyle.Plain);
        tableView.setDataSource(new SampleATableViewDataSource());
        tableView.setDelegate(new SampleATableViewDelegate());

        return view;
    }

    public class SampleATableViewDataSource extends ATableViewDataSource {

        @Override
        public ATableViewCell cellForRowAtIndexPath(ATableView tableView,
                                                    NSIndexPath indexPath) {
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
            cell.getTextLabel().setText(dataStrings[indexPath.getRow()]);
            cell.getTextLabel().setMaxLines(10);
            // set detail text. careful, detail text is not present on every
            // cell style.
            // null references are not as neat as in obj-c.

            return cell;
        }

        @Override
        public int numberOfRowsInSection(ATableView tableView, int section) {
            // TODO Auto-generated method stub
            return dataStrings.length;
        }

    }

    public class SampleATableViewDelegate extends ATableViewDelegate {

    }

    public void putStringToClipboard(String text) {
        Context context = getActivity().getApplicationContext();
        ClipboardManager cbm = (ClipboardManager) context
                .getSystemService(context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("shanghanlun", text);
        cbm.setPrimaryClip(clipData);
    }
}
