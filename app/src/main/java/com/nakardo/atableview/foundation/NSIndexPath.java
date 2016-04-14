package com.nakardo.atableview.foundation;

public class NSIndexPath {
    private int mSection;
    private int mRow;

    private NSIndexPath(int row, int section) {
        mRow = row;
        mSection = section;
    }

    public static NSIndexPath indexPathForRowInSection(int row, int section) {
        return new NSIndexPath(row, section);
    }

    public int getSection() {
        return mSection;
    }

    public int getRow() {
        return mRow;
    }

    @Override
    public String toString() {
        return "[" + mRow + ", " + mSection + "]";
    }

    @Override
    public boolean equals(Object o) {
        NSIndexPath ip = (NSIndexPath) o;
        return mRow == ip.getRow() && mSection == ip.getSection();
    }

    @Override
    public int hashCode() {
        String str = mSection + "," + mRow;
        return str.hashCode();
    }
}
