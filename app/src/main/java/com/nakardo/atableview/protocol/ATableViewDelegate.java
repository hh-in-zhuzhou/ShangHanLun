package com.nakardo.atableview.protocol;

import android.view.View;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableViewCell;

import me.huanghai.shanghanlun_android.R;

public class ATableViewDelegate {
    public int heightForRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
        return (int) tableView.getResources().getDimension(R.dimen.atv_cell_default_row_height);
    }

    public void willDisplayCellForRowAtIndexPath(ATableView tableView, ATableViewCell cell, NSIndexPath indexPath) {
        return;
    }

    public int heightForHeaderInSection(ATableView tableView, int section) {
        return ATableViewCell.LayoutParams.UNDEFINED;
    }

    public int heightForFooterInSection(ATableView tableView, int section) {
        return ATableViewCell.LayoutParams.UNDEFINED;
    }

    public void didSelectRowAtIndexPath(ATableView tableView, NSIndexPath indexPath) {
        return;
    }

    //add by me at 2015-5-3
    public void clickHeaderForSection(ATableView tableView, View headerView, int section) {
        return;
    }

    public void accessoryButtonTappedForRowWithIndexPath(ATableView tableView, NSIndexPath indexPath) {
        return;
    }
}
