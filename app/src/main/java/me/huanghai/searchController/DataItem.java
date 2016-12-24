package me.huanghai.searchController;

import android.text.SpannableStringBuilder;

import com.nakardo.atableview.foundation.NSIndexPath;

import java.util.ArrayList;
import java.util.List;

public class DataItem {
    private int ID;
    private String text;
    private SpannableStringBuilder attributedText;
    private NSIndexPath indexPath;
    private List<String> fangList;
    private List<String> yaoList;

    void setText(String text_) {
        text = text_;
        // TODO:compute other property;
        attributedText = Helper.renderText(text);
//        fangList = getFangNameList(text);
//        yaoList = getYaoNameList(text);
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

    public void setFangList(List<String> fang) {
        fangList = fang;
    }

    public void setYaoList(List<String> yao) {
        yaoList = yao;
    }

    public String getText() {
        return text;
    }

    public SpannableStringBuilder getAttributedText() {
        if (attributedText != null) {
            return attributedText;
        }
        attributedText = Helper.renderText(text);
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

    public List<String> getFangList() {
        return fangList == null ? new ArrayList<String>() : fangList;
    }

    public List<String> getYaoList() {
        return yaoList == null ? new ArrayList<String>() : yaoList;
    }

    public DataItem() {

    }

    protected int getItemIndex(String text) {
        String numString = text.substring(0, text.indexOf("、"));
        return Integer.parseInt(numString) - 1;
    }

    public static String[] getFangNameList(String textString) {
        ArrayList<Integer> allPos = Helper.getAllSubStringPos(textString, "$f");

        // TODO: to avoid duplicate strings.
        String[] resStrings = new String[allPos.size()];

        int i = 0;
        for (int pos : allPos) {
            int curEnd = textString.substring(pos).indexOf("}");
            String cut = textString.substring(pos + 3, pos + curEnd);
            resStrings[i] = cut;//Helper.getAliasString(dict, cut);
            i++;
        }
        return resStrings;
    }

    public static String[] getYaoNameList(String textString) {
        ArrayList<Integer> allPos = Helper.getAllSubStringPos(textString, "$u");

        // TODO: to avoid duplicate strings.
        String[] resStrings = new String[allPos.size()];

        int i = 0;
        for (int pos : allPos) {
            int curEnd = textString.substring(pos).indexOf("}");
            String cut = textString.substring(pos + 3, pos + curEnd);
            resStrings[i] = cut;//Helper.getAliasString(dict, cut);
            i++;
        }
        return resStrings;
    }
}
