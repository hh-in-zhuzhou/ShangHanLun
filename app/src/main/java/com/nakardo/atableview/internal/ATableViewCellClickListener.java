package com.nakardo.atableview.internal;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.view.ATableView;
import com.nakardo.atableview.view.ATableViewCell;

public class ATableViewCellClickListener implements OnItemClickListener {
    private ATableView mTableView;

    public ATableViewCellClickListener(ATableView tableView) {
        mTableView = tableView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
//		Log.d("log:", String.format("pos:%d", pos));
        // make sure we're clicking a cell, do not send callbacks for header or footer rows.
        ATableView tableView = (ATableView) view.getParent();
        ATableViewDelegate delegate = mTableView.getDelegate();
        if (view instanceof ATableViewCell) {

            // don't throw up selection events if it's not allowed by the table configuration.
            if (tableView.getAllowsSelection()) {
                delegate.didSelectRowAtIndexPath(mTableView, mTableView.getInternalAdapter().getIndexPath(pos));
            }
        } else {
            if (mTableView.needPinnedHeader() && adapter.getFirstVisiblePosition() == pos && mTableView.mHeaderView.getVisibility() != View.GONE) {
                return;
            }
            delegate.clickHeaderForSection(mTableView, view, mTableView.getInternalAdapter().getSection(pos));
        }
    }
}