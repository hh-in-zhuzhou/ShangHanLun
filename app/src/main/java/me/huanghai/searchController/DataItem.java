package me.huanghai.searchController;

import android.text.SpannableStringBuilder;

import com.nakardo.atableview.foundation.NSIndexPath;

import java.util.ArrayList;

public class DataItem {
    private String text;
    private SpannableStringBuilder attributedText;
    private NSIndexPath indexPath;
    private String[] fangList;
    private String[] yaoList;

    void setText(String text_) {
        text = text_;
        // TODO:compute other property;
        attributedText = Helper.renderText(text);
        fangList = getFangNameList(text);
        yaoList = getYaoNameList(text);
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

    public void setFangList(String[] fang) {
        fangList = fang;
    }

    public void setYaoList(String[] yao) {
        yaoList = yao;
    }

    public String getText() {
        return text;
    }

    public SpannableStringBuilder getAttributedText() {
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

    public String[] getFangList() {
        return fangList;
    }

    public String[] getYaoList() {
        return yaoList;
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
            resStrings[i] = cut;
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
            resStrings[i] = cut;
            i++;
        }
        return resStrings;
    }



}
