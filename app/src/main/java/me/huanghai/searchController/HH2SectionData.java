package me.huanghai.searchController;

import com.nakardo.atableview.foundation.NSIndexPath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HH2SectionData {
    private int section;
    private String header;
    private List<? extends DataItem> data;


    public HH2SectionData(List<? extends DataItem> d, int section_, String header_) {
        section = section_;
        header = header_;
        data = d;
    }

    public List<? extends DataItem> getData() {
        return data;
    }

    public String getHeader() {
        return header;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }
}
