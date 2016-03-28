package me.huanghai.searchController;

import android.text.style.ClickableSpan;
import android.widget.TextView;

import com.nakardo.atableview.uikit.UILabel;

/**
 * Created by hh on 16/3/27.
 */
public interface ClickLink {
    public void clickYaoLink(TextView tv, ClickableSpan clickableSpan);
    public void clickFangLink(TextView tv, ClickableSpan clickableSpan);
}
