package com.nakardo.atableview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nakardo.atableview.foundation.NSIndexPath;
import com.nakardo.atableview.internal.ATableViewAdapter;
import com.nakardo.atableview.internal.ATableViewCellClickListener;
import com.nakardo.atableview.internal.ATableViewPlainFooterDrawable;
import com.nakardo.atableview.protocol.ATableViewDataSource;
import com.nakardo.atableview.protocol.ATableViewDelegate;
import com.nakardo.atableview.utils.DrawableUtils;
import com.nakardo.atableview.view.ATableViewCell.ATableViewCellSeparatorStyle;

import java.util.ArrayList;

import me.huanghai.shanghanlun_android.R;

@SuppressLint("NewApi")
public class ATableView extends ListView {
    private static final ATableViewStyle DEFAULT_STYLE = ATableViewStyle.Plain;

    // UIView
    private int mBackgroundColor = -1;

    private ATableViewCellSeparatorStyle mSeparatorStyle = ATableViewCellSeparatorStyle.SingleLine;
    private int mSeparatorColor = -1;
    private ATableViewStyle mStyle = DEFAULT_STYLE;
    private boolean mAllowsSelection = true;
    private boolean mAllowsMultipleSelection = false;
    private ATableViewDataSource mDataSource;
    private ATableViewDelegate mDelegate = new ATableViewDelegate();

    // for pinned header
    public View mHeaderView;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private boolean mDrawFlag = true;
    private ATableViewAdapter mAdapter;

    public enum ATableViewStyle {
        Plain, Grouped
    }

    ;

    public void enableHeaderView(boolean enable) {
        if (mHeaderView == null)
            return;
        int vis = enable ? View.VISIBLE : View.GONE;
        mHeaderView.setVisibility(vis);
    }

    // next 2 func is for pinned header
    public void setHeaderView(View view) {
        mHeaderView = view;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (null != mHeaderView) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mMeasuredWidth = mHeaderView.getMeasuredWidth();
            mMeasuredHeight = mHeaderView.getMeasuredHeight();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (null != mHeaderView && mDrawFlag) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }

    // TODO: this func should be removed.
    public void setStyle(ATableViewStyle s) {
        init(s);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (null != mHeaderView) {
            mHeaderView.layout(0, 0, mMeasuredWidth, mMeasuredHeight);
            controlPinnedHeader(getFirstVisiblePosition());
        }
    }

    public void controlPinnedHeader(int position) {
        if (null == mHeaderView) {
            return;
        }

        int alpha = 100;
        int pinnedHeaderState = mAdapter.getPinnedHeaderState(position);
        switch (pinnedHeaderState) {
            case PinnedHeaderAdapter.PINNED_HEADER_GONE:
                mDrawFlag = false;
                break;

            case PinnedHeaderAdapter.PINNED_HEADER_VISIBLE:
                mAdapter.configurePinnedHeader(mHeaderView, position, alpha);
                mDrawFlag = true;
                mHeaderView.layout(0, 0, mMeasuredWidth, mMeasuredHeight);
                break;

            case PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP:
                mAdapter.configurePinnedHeader(mHeaderView, position, alpha);
                mDrawFlag = true;

                // 移动位置
                View topItem = getChildAt(0);

                if (null != topItem) {
                    int bottom = topItem.getBottom();
                    int height = mHeaderView.getHeight();

                    int y;
                    if (bottom < height) {
                        y = bottom - height;
                    } else {
                        y = 0;
                    }

                    if (mHeaderView.getTop() != y) {
                        mHeaderView.layout(0, y, mMeasuredWidth, mMeasuredHeight
                                + y);
                    }

                }
                break;
        }

    }

    public interface PinnedHeaderAdapter {

        public static final int PINNED_HEADER_GONE = 0;

        public static final int PINNED_HEADER_VISIBLE = 1;

        public static final int PINNED_HEADER_PUSHED_UP = 2;

        int getPinnedHeaderState(int position);

        void configurePinnedHeader(View headerView, int position, int alpaha);
    }

    private void setupFooterView(int lastRowHeight) {

        // closes #12, add footer for plain style tables in order to make the
        // effect of repeating
        // rows across table height.
        if (mStyle == ATableViewStyle.Plain) {
            final View footerView = new FrameLayout(getContext());

            // add listener to resize after layout has been completed.
            getViewTreeObserver().addOnGlobalLayoutListener(
                    new ViewTreeObserver.OnGlobalLayoutListener() {
                        public void onGlobalLayout() {
                            getViewTreeObserver().removeGlobalOnLayoutListener(
                                    this);

                            int footerHeight = getHeight()
                                    - getInternalAdapter().getContentHeight();
                            AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                                    AbsListView.LayoutParams.MATCH_PARENT,
                                    footerHeight > 0 ? footerHeight : 0);
                            footerView.setLayoutParams(params);
                        }
                    });
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
                footerView.setBackground(new ATableViewPlainFooterDrawable(
                        this, lastRowHeight));
            } else {
                footerView
                        .setBackgroundDrawable(new ATableViewPlainFooterDrawable(
                                this, lastRowHeight));
            }
            addFooterView(footerView);
        }
    }

    private void setupBackgroundDrawable() {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk > android.os.Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(DrawableUtils.getTableBackgroundDrawable(this));
        } else {
            setBackgroundDrawable(DrawableUtils
                    .getTableBackgroundDrawable(this));
        }
    }

    private int getSelectionMode() {

        // well, this is just a workaround since we've two variables in ios and
        // only one in android
        // to define selection enabled and multiple selection.
        int choiceMode = CHOICE_MODE_SINGLE;
        if (mAllowsMultipleSelection)
            choiceMode = CHOICE_MODE_MULTIPLE;

        return choiceMode;
    }

    private void clearSelectedRows() {
        clearChoices();
        requestLayout();
    }

    public ATableView(ATableViewStyle style, Context context) {
        super(context);
        init(style);
    }

    public void init(ATableViewStyle style) {
        mStyle = style;

        setSelector(android.R.color.transparent);
        setChoiceMode(getSelectionMode());
        setDivider(null);
        // here: replace 0 to 1.
        setDividerHeight(0);
        setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        setScrollingCacheEnabled(false);

        setupBackgroundDrawable();
    }

    public ATableView(Context context) {
        super(context);
        // init(ATableViewStyle.Plain);
    }

    public ATableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // init(ATableViewStyle.Plain);
    }

    public ATableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // init(ATableViewStyle.Plain);
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int resId) {
        mBackgroundColor = resId;
        setupBackgroundDrawable();
    }

    public ATableViewCellSeparatorStyle getSeparatorStyle() {
        return mSeparatorStyle;
    }

    public void setSeparatorStyle(ATableViewCellSeparatorStyle separatorStyle) {
        mSeparatorStyle = separatorStyle;
    }

    public int getSeparatorColor() {
        return mSeparatorColor;
    }

    public void setSeparatorColor(int resId) {
        mSeparatorColor = resId;
    }

    public ATableViewStyle getStyle() {
        return mStyle;
    }

    public boolean getAllowsSelection() {
        return mAllowsSelection;
    }

    public void setAllowsSelection(boolean allowsSelection) {
        mAllowsSelection = allowsSelection;

        if (mAllowsSelection)
            setChoiceMode(getSelectionMode());
        else
            setChoiceMode(CHOICE_MODE_NONE);

        clearSelectedRows();
    }

    public boolean getAllowsMultipleSelection() {
        return mAllowsMultipleSelection;
    }

    public void setAllowsMultipleSelection(boolean allowsMultipleSelection) {
        mAllowsMultipleSelection = allowsMultipleSelection;

        if (mAllowsSelection) {
            setChoiceMode(getSelectionMode());
            clearSelectedRows();
        }
    }

    public NSIndexPath getIndexPathForSelectedRow() {
        NSIndexPath indexPath = null;

        int position = getCheckedItemPosition();
        if (position != INVALID_POSITION) {
            indexPath = getInternalAdapter().getIndexPath(position);
        }

        return indexPath;
    }

    public NSIndexPath[] getIndexPathsForSelectedRows() {
        NSIndexPath[] indexPaths = null;

        SparseBooleanArray checkedList = getCheckedItemPositions();
        if (checkedList != null) {
            ArrayList<NSIndexPath> indexPathList = new ArrayList<NSIndexPath>();

            ATableViewAdapter adapter = getInternalAdapter();
            for (int i = 0; i < adapter.getCount(); i++) {
                if (checkedList.get(i)) {
                    indexPathList.add(adapter.getIndexPath(i));
                }
            }

            indexPaths = indexPathList.toArray(new NSIndexPath[indexPathList
                    .size()]);
        }

        return indexPaths;
    }

    public ATableViewDataSource getDataSource() {
        return mDataSource;
    }

    public void setDataSource(ATableViewDataSource dataSource) {
        mDataSource = dataSource;
    }

    public ATableViewDelegate getDelegate() {
        return mDelegate;
    }

    public void setDelegate(ATableViewDelegate delegate) {
        mDelegate = delegate;
    }

    public ATableViewAdapter getInternalAdapter() {

        // fixes bugs for tables which includes headers or footers.
        ATableViewAdapter adapter = null;
        if (getAdapter() instanceof HeaderViewListAdapter) {
            HeaderViewListAdapter headerAdapter = (HeaderViewListAdapter) getAdapter();
            adapter = (ATableViewAdapter) headerAdapter.getWrappedAdapter();
        } else {
            adapter = (ATableViewAdapter) getAdapter();
        }

        return adapter;
    }

    public void reloadData() {
        ATableViewAdapter adapter = getInternalAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            clearSelectedRows();
        }
    }

    public boolean needPinnedHeader() {
        return mStyle == ATableViewStyle.Plain;
    }

    @Override
    protected void onAttachedToWindow() {
        mAdapter = new ATableViewAdapter(this);

        // TODO we should handle the case last row is
        // ListView.LayoutParams.WRAP_CONTENT, to get its height.
        // setup footer for plain tables to complete its height with empty rows.
        // ATTENSION: below has a bug which cause listview dead for no move
        // except be killed.
        // so I commented it.
        // setupFooterView(mAdapter.getLastRowHeight());

        if (needPinnedHeader()) {
            // TO DO: here can remove to PinnedView class.
            View HeaderView = LayoutInflater.from(getContext()).inflate(
                    R.layout.pinnedheader, this, false);
            Resources res = getResources();
            Rect padding = new Rect();
            padding.left = (int) res
                    .getDimension(R.dimen.atv_plain_section_header_padding_left);
            padding.right = (int) res
                    .getDimension(R.dimen.atv_plain_section_header_padding_right);

            // set background for plain style.
            // HeaderView
            // .setBackgroundResource(R.drawable.plain_header_background);
            HeaderView.setBackgroundColor(Color.argb(178, 204, 204, 204));
            TextView label = (TextView) HeaderView.findViewById(R.id.textLabel);
            label.setTextColor(Color.BLUE);
            HeaderView.setPadding(padding.left, padding.top, padding.right,
                    padding.bottom);
            ListView.LayoutParams params = new ListView.LayoutParams(
                    ListView.LayoutParams.MATCH_PARENT,
                    mAdapter.getHeaderFooterRowHeight(0, false));
            HeaderView.setLayoutParams(params);
            HeaderView.requestLayout();

            setHeaderView(HeaderView);
        }
        // setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setAdapter(mAdapter);
        setOnScrollListener(mAdapter);
        setOnItemClickListener(new ATableViewCellClickListener(this));
        super.onAttachedToWindow();
    }

//	public boolean onTouchEvent(MotionEvent ev) {
//		if (needPinnedHeader() && ev.getAction() == MotionEvent.ACTION_DOWN
//				&& mHeaderView != null
//				&& mHeaderView.getVisibility() != View.GONE) {
//			int x = (int) ev.getX();
//			int y = (int) ev.getY();
//			Rect r = new Rect();
//			mHeaderView.getLocalVisibleRect(r);
//			if (r.contains(x, y)) {
//				// Log.d("contains", "...");
//				mDelegate.clickHeaderForSection(ATableView.this, null,
//						mAdapter.getCurSection());
//				return true;
//			}
//		}
//		return super.onTouchEvent(ev);
//	}

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (needPinnedHeader() && ev.getAction() == MotionEvent.ACTION_DOWN
                && mHeaderView != null
                && mHeaderView.getVisibility() != View.GONE) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            Rect r = new Rect();
            mHeaderView.getLocalVisibleRect(r);
            if (r.contains(x, y)) {
                // Log.d("contains", "...");
                mDelegate.clickHeaderForSection(ATableView.this, null,
                        mAdapter.getCurSection());
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

//    @Override
//    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
//                                   int scrollY, int scrollRangeX, int scrollRangeY,
//                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
//        // 实现的本质就是在这里动态改变了maxOverScrollY的值
//        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
//                scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
//                isTouchEvent);
//    }
}
