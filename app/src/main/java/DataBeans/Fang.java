package DataBeans;

import java.util.List;

import me.huanghai.searchController.DataItem;

/**
 * Created by hh on 2016/12/23.
 */

public class Fang extends DataItem {

    String name;
    int yaoCount;
    float drinkNum;
    String makeWay;

    List<YaoUse> standardYaoList;
    List<YaoUse> extraYaoList;
    List<YaoUse> helpYaoList;

    class YaoUse{
        int YaoID;
        String showName;
        String suffix;
        float weight;
        float maxWeight;
        String extraProcess;
        String amount;
    }
}
