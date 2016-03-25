package me.huanghai.searchController;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingletonData {
    private View mask;
    private static SingletonData data = null;
    private ArrayList<HH2SectionData> content;
    private ArrayList<HH2SectionData> fang;
    private String[] yao;
    private ArrayList<HH2SectionData> yaoData;
    protected boolean isSimple = true; // 控制是否独用398条或者全文。
    protected boolean showJinkui = true; // 控制是否显示金匮内容。
    protected Map<String, String> yaoAliasDict;
    protected Map<String, String> fangAliasDict;
    protected List<String> allYao;
    protected List<String> allFang;

    public ShowFragment curFragment;
    public Activity curActivity;
    public List<LittleWindow> littleWindowStack = new ArrayList<>();

    // status
    public boolean isSeeingContextInSearchMode = false;

    public List<String> getAllYao() {
        return allYao;
    }

    public List<String> getAllFang() {
        return allFang;
    }

    public Map<String, String> getYaoAliasDict() {
        return yaoAliasDict;
    }

    public Map<String, String> getFangAliasDict() {
        return fangAliasDict;
    }

    private ArrayList<ShowFang> showFangList = new ArrayList<ShowFang>();

    public View getMask() {
        return mask;
    }

    public void setMask(View mask) {
        this.mask = mask;
    }

    public boolean hasShowFang() {
        return showFangList.size() == 1;
    }

    public int getShowFangNum() {
        return showFangList.size();
    }

    public ShowFang getShowFang() {
        return showFangList.get(showFangList.size() - 1);
    }

    public void pushShowFang(ShowFang showFang) {
        showFangList.add(showFang);
    }

    public void popShowFang() {
        showFangList.remove(showFangList.size() - 1);
    }

    public boolean getIsSimple() {
        return isSimple;
    }

    public void setSimple(boolean simple) {
        isSimple = simple;
    }

    public boolean getShowJinkui() {
        return showJinkui;
    }

    public void setShowJinkui(boolean show) {
        showJinkui = show;
    }

    public ArrayList<HH2SectionData> getContent() {
        return content;
    }

    public ArrayList<HH2SectionData> getFang() {
        return fang;
    }

    public String[] getYao() {
        return yao;
    }

    public ArrayList<HH2SectionData> getYaoData() {
        return yaoData;
    }

    private void initAlias() {
        // 先定义别名
        yaoAliasDict = new HashMap<String, String>() {
            {
                put("术", "白术");
                put("朮", "白术");
                put("白朮", "白术");
                put("桂", "桂枝");
                put("桂心", "桂枝");
                put("肉桂", "桂枝");
                put("白芍药", "芍药");
                put("枣", "大枣");
                put("枣膏", "大枣");
                put("生姜汁", "生姜");

                put("生地黄", "地黄");
                put("干地黄", "地黄");
                put("生地", "地黄");
                put("熟地", "地黄");
                put("生地黄汁", "地黄");
                put("地黄汁", "地黄");

                put("甘遂末", "甘遂");
                put("茵陈蒿末", "茵陈蒿");
                put("大附子", "附子");
                put("粉", "白粉");
                put("白蜜", "蜜");
                put("食蜜", "蜜");
                put("杏子", "杏仁");
                put("葶苈", "葶苈子");
                put("香豉", "豉");
                put("肥栀子", "栀子");
                put("生狼牙", "狼牙");
                put("干苏叶", "苏叶");
                put("清酒", "酒");
                put("白酒", "酒");

                put("艾叶", "艾");
                put("乌扇", "射干");
                put("代赭石", "赭石");
                put("代赭", "赭石");
                put("煅灶下灰", "煅灶灰");
                put("干苏叶", "苏叶");

                put("蛇床子仁", "蛇床子");
                put("牡丹皮", "牡丹");
                put("小麦汁", "小麦");
                put("小麦粥", "小麦");
                put("麦粥", "小麦");
                put("大麦粥", "大麦");
                put("大麦粥汁", "大麦");

                put("葱白", "葱");

                put("赤硝", "赤消");
                put("硝石", "赤消");
                put("消石", "赤消");
                put("芒消", "芒硝");

                put("法醋", "苦酒");
                put("大猪胆", "猪胆汁");
                put("大猪胆汁", "猪胆汁");
                put("鸡子白", "鸡子");

                put("太一禹余粮", "禹余粮");
                put("妇人中裈近隐处取烧作灰", "中裈灰");
                put("石苇", "石韦");
                put("灶心黄土", "灶中黄土");
                put("瓜子", "瓜瓣");

                put("括蒌根", "栝楼根");
                put("瓜蒌根", "栝楼根");
                put("括蒌实", "栝楼实");
                put("瓜蒌实", "栝楼实");
            }
        };
        fangAliasDict = new HashMap<String, String>() {
            {
                put("人参汤", "理中汤");
                put("芪芍桂酒汤", "黄芪芍药桂枝苦酒汤");
                put("膏发煎", "猪膏发煎");
            }
        };
    }

    public void savePreferences() {
        SharedPreferences pref = MyApplication.getAppContext()
                .getSharedPreferences("shanghan", Context.MODE_PRIVATE);
        Editor editor = pref.edit();
        editor.putBoolean("isSimple", isSimple);
        editor.putBoolean("showJinkui", showJinkui);
        editor.commit();
    }

    private SingletonData() {
        SharedPreferences pref = MyApplication.getAppContext()
                .getSharedPreferences("shanghan", Context.MODE_PRIVATE);
        isSimple = pref.getBoolean("isSimple", true);
        showJinkui = pref.getBoolean("showJinkui", true);

        initAlias();
        reReadContent(); // 先读取条文
        reReadFang(); // 再读取方药

        allFang = new ArrayList<String>();
        for (HH2SectionData sec : fang) {
            for (DataItem item : sec.getData()) {
                String s = item.getFangList()[0];
                String s2 = fangAliasDict.get(s);
                if (s2 == null) {
                    s2 = s;
                }
                allFang.add(s2);
            }
        }

        // 再读取药物列表
        String string = FucUtil.readFile(MyApplication.getAppContext(),
                "yao.txt");
        yao = string.split("[\n\r]*-----[\n\r]*");
        for (int i = 0; i < yao.length; i++) {
            String tmp = yao[i];
            yao[i] = String.format("%d、%s", i + 1, tmp);
        }
        yaoData = new ArrayList<HH2SectionData>();
        yaoData.add(new HH2SectionData(yao, 0, "伤寒金匮所有药物"));

        allYao = new ArrayList<String>();
        for (HH2SectionData sec : yaoData) {
            for (DataItem item : sec.getData()) {
                String s = item.getYaoList()[0];
                String s2 = yaoAliasDict.get(s);
                if (s2 == null) {
                    s2 = s;
                }
                allYao.add(s2);
            }
        }
    }

    public void reReadContent() {
        // 先读取条文
        content = null;
        String string = FucUtil.readFile(MyApplication.getAppContext(),
                isSimple ? "shangHan_data_simple.json" : "shangHan_data.json");
        content = new ArrayList<HH2SectionData>();
        try {
            JSONArray array = new JSONArray(string);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                HH2SectionData d = new HH2SectionData(object, i);
                content.add(d);
            }
            if (showJinkui) {
                int start = content.size();
                string = FucUtil.readFile(MyApplication.getAppContext(),
                        "jinKui_data.json");
                array = new JSONArray(string);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    HH2SectionData d = new HH2SectionData(object, start + i);
                    content.add(d);
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void reReadFang() {
        // 再读取方药
        fang = null;
        String string = FucUtil.readFile(MyApplication.getAppContext(),
                "shangHan_fang.json");
        fang = new ArrayList<HH2SectionData>();
        try {
            JSONArray array = new JSONArray(string);
            JSONObject object = new JSONObject();
            object.put("header", "伤寒论方");
            object.put("data", array);
            fang.add(new HH2SectionData(object, 0));

            if (showJinkui) {
                string = FucUtil.readFile(MyApplication.getAppContext(),
                        "jinKui_fang.json");
                array = new JSONArray(string);
                object = new JSONObject();
                object.put("header", "金匮要略方");
                object.put("data", array);
                fang.add(new HH2SectionData(object, 1));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static SingletonData getInstance() {
        if (data == null) {
            data = new SingletonData();
        }
        return data;
    }
}
