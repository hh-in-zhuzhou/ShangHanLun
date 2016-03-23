package me.huanghai.searchController;

import com.nakardo.atableview.foundation.NSIndexPath;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HH2SectionData {
    private int section;
    private String header;
    private ArrayList<DataItem> data;

    public HH2SectionData(JSONObject d, int section_) throws JSONException {
        section = section_;
        header = d.getString("header");

        data = new ArrayList<DataItem>();
        JSONArray array = d.getJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
            DataItem item = new DataItem();
//			System.out.println(array.getString(i));

            item.setText(array.getString(i));
            item.setIndexPath(NSIndexPath.indexPathForRowInSection(i, section_));
            data.add(item);
        }
    }

    public HH2SectionData(String[] d, int section_, String header_) {
        section = section_;
        header = header_;

        data = new ArrayList<DataItem>();
        for (int i = 0; i < d.length; i++) {
            DataItem item = new DataItem();
//			System.out.println(array.getString(i));

            item.setText(d[i]);
            item.setIndexPath(NSIndexPath.indexPathForRowInSection(i, section_));
            data.add(item);
        }
    }

    public HH2SectionData(ArrayList<DataItem> d, int section_, String header_) {
        section = section_;
        header = header_;
        data = d;
    }

    public ArrayList<DataItem> getData() {
        return data;
    }

    public String getHeader() {
        return header;
    }

    public int getSection() {
        return section;
    }
}
