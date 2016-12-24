package me.huanghai.searchController;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DataBeans.Fang;
import DataBeans.Yao;
import me.huanghai.shanghanlun_android.FangFragment;
import me.huanghai.shanghanlun_android.MainFragment;
import me.huanghai.shanghanlun_android.TabController;

public class SingletonData {
    private View mask;
    private static SingletonData data = null;
    private ArrayList<HH2SectionData> content;
    private ArrayList<HH2SectionData> fang;
    private String[] yao;
    private ArrayList<HH2SectionData> yaoData;

    public final int Show_Shanghan_None = 0;
    public final int Show_Shanghan_398 = 1;
    public final int Show_Shanghan_AllSongBan = 2;

    public final int Show_Jinkui_None = 0;
    public final int Show_Jinkui_Default = 1;

    protected int showShanghan = Show_Shanghan_398;
    protected int showJinkui = Show_Jinkui_Default;
    protected Map<String, String> yaoAliasDict;
    protected Map<String, String> fangAliasDict;
    protected List<String> allYao;
    protected List<String> allFang;

    public ShowFragment curFragment;
    public Activity curActivity;
    public TipsWindow curTipsWindow;
    public List<LittleWindow> littleWindowStack = new ArrayList<>();

    public Gson gson = new Gson();

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

    public int getShowShanghan() {
        return showShanghan;
    }

    public void setShowShanghan(int showShanghan) {
        this.showShanghan = showShanghan;
    }

    public int getShowJinkui() {
        return showJinkui;
    }

    public void setShowJinkui(int showJinkui) {
        this.showJinkui = showJinkui;
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
        editor.putInt("showShanghan", showShanghan);
        editor.putInt("showJinkui", showJinkui);
        editor.commit();
    }

    private SingletonData() {
        SharedPreferences pref = MyApplication.getAppContext()
                .getSharedPreferences("shanghan3.1", Context.MODE_PRIVATE);
        showShanghan = pref.getInt("showShanghan", Show_Shanghan_398);
        showJinkui = pref.getInt("showJinkui", Show_Jinkui_Default);

        initAlias();
        reReadData();

        allFang = new ArrayList<String>();
        for (HH2SectionData sec : fang) {
            List<? extends DataItem> d = sec.getData();
            Log.e("-d->",d.get(0).toString());
            for (DataItem item : d) {
                String s = item.getFangList().get(0);
                String s2 = fangAliasDict.get(s);
                if (s2 == null) {
                    s2 = s;
                }
                allFang.add(s2);
            }
        }

        // 再读取药物列表
        String string = FucUtil.readFile(MyApplication.getAppContext(),"yao.json");
        yaoData = new ArrayList<HH2SectionData>();
        List<Yao> tmp = gson.fromJson(string, new TypeToken<List<Yao>>() {}.getType());
        yaoData.add(new HH2SectionData(tmp, 0, "伤寒金匮所有药物"));

        allYao = new ArrayList<String>();
        for (HH2SectionData sec : yaoData) {
            for (DataItem item : sec.getData()) {
                String s = item.getYaoList().get(0);
                String s2 = yaoAliasDict.get(s);
                if (s2 == null) {
                    s2 = s;
                }
                allYao.add(s2);
            }
        }
    }

    public void reReadData(){
        reReadContent();
        reReadFang();
    }

    public void reReadData(final Activity activity){
        final ProgressBar bar = Helper.showProgressBar(activity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                reReadData();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Helper.removeFormWindow(activity, bar);
                        TabController tab = (TabController) activity;
                        for (Fragment frag : tab.fragments) {
                            if (frag instanceof MainFragment) {
                                ((MainFragment) frag).resetData(SingletonData.getInstance().getContent());
                            } else if (frag instanceof FangFragment) {
                                ((FangFragment) frag).resetData(SingletonData.getInstance().getFang());
                            }
                        }
                    }
                });

            }
        }).start();
    }

    public void reReadContent() {
        // 先读取条文
        content = null;
        String string = FucUtil.readFile(MyApplication.getAppContext(), "shangHan_data.json");
        content = new ArrayList<>();
        if (showShanghan != Show_Shanghan_None) {
            List<HH2SectionData> tmp = gson.fromJson(string, new TypeToken<List<HH2SectionData>>() {
            }.getType());
            if (showShanghan == Show_Shanghan_398){
                tmp = tmp.subList(8, 8+10);
            }
            content.addAll(tmp);
        }

        if (showJinkui != Show_Jinkui_None) {
            string = FucUtil.readFile(MyApplication.getAppContext(),
                    "jinKui_data.json");
            List<HH2SectionData> jinkui = gson.fromJson(string, new TypeToken<List<HH2SectionData>>() {}.getType());
            content.addAll(jinkui);
        }
    }

    public void reReadFang() {
        // 再读取方药
        fang = new ArrayList<HH2SectionData>();
        String string = FucUtil.readFile(MyApplication.getAppContext(),
                "shangHan_fang.json");
        List<Fang> tmp = gson.fromJson(string, new TypeToken<List<Fang>>() {}.getType());
        fang.add(new HH2SectionData(tmp, 0, "伤寒论方"));

        if (showJinkui != Show_Jinkui_None) {
            string = FucUtil.readFile(MyApplication.getAppContext(),
                    "jinKui_fang.json");
            List<Fang> jinkui = gson.fromJson(string, new TypeToken<List<Fang>>() {}.getType());
            fang.add(new HH2SectionData(jinkui, 1, "金匮要略方"));
        }

    }

    public static SingletonData getInstance() {
        if (data == null) {
            data = new SingletonData();
        }
        return data;
    }
}
