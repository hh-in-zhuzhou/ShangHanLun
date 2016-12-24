package DataBeans;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.huanghai.searchController.DataItem;
import me.huanghai.searchController.Helper;
import me.huanghai.searchController.SingletonData;

/**
 * Created by hh on 2016/12/23.
 */

public class Fang extends DataItem {

    public String name;
    int yaoCount;
    float drinkNum;
    String makeWay;

    List<YaoUse> standardYaoList;
    List<YaoUse> extraYaoList;
    List<YaoUse> helpYaoList;

    public boolean hasYao(final String yao){
        if (standardYaoList != null){
            return Helper.some(standardYaoList, new Helper.IBool<YaoUse>() {
                @Override
                public boolean isOK(YaoUse o) {
                    return isYaoEqual(o.showName, yao);
                }
            });
        }
        if (extraYaoList != null){
            return Helper.some(extraYaoList, new Helper.IBool<YaoUse>() {
                @Override
                public boolean isOK(YaoUse o) {
                    return isYaoEqual(o.showName, yao);
                }
            });
        }
        if (helpYaoList != null){
            return Helper.some(helpYaoList, new Helper.IBool<YaoUse>() {
                @Override
                public boolean isOK(YaoUse o) {
                    return isYaoEqual(o.showName, yao);
                }
            });
        }
        return false;
    }

    public int compare(Fang fang, String yao){
        YaoUse use1 = getYaoUseByYao(yao);
        YaoUse use2 = fang.getYaoUseByYao(yao);
        if (use1 == null){
            return 1;
        }
        if (use2 == null){
            return -1;
        }

        float left = Math.max(use1.weight, use1.maxWeight)/drinkNum;
        float right = Math.max(use2.weight, use2.maxWeight)/fang.drinkNum;
        if (left < right){
            return 1;
        }
        if (left > right){
            return -1;
        }

        return 0;
    }

    public YaoUse getYaoUseByYao(String yao){
        if (standardYaoList != null) {
            for (YaoUse use :
                    standardYaoList) {
                if (isYaoEqual(use.showName, yao)) {
                    return use;
                }
            }
        }
        if (extraYaoList != null) {
            for (YaoUse use :
                    extraYaoList) {
                if (isYaoEqual(use.showName, yao)) {
                    return use;
                }
            }
        }
        if (helpYaoList != null) {
            for (YaoUse use :
                    helpYaoList) {
                if (isYaoEqual(use.showName, yao)) {
                    return use;
                }
            }
        }
        return null;
    }

    public String getFangNameLinkWithYaoWeight(String yao){
        if (standardYaoList != null) {
            for (YaoUse use :
                    standardYaoList) {
                if (isYaoEqual(use.showName, yao)) {
                    return String.format(Locale.CHINA, "$f{%s}$w{(%s%.0f%s服)}，", name, use.amount, drinkNum, Helper.def(use.suffix, ""));
                }
            }
        }
        if (extraYaoList != null) {
            for (YaoUse use :
                    extraYaoList) {
                if (isYaoEqual(use.showName, yao)) {
                    return String.format(Locale.CHINA, "$f{%s}$w{(%s%.0f%s服)}，", name, use.amount, drinkNum, Helper.def(use.suffix, ""));
                }
            }
        }
        if (helpYaoList != null) {
            for (YaoUse use :
                    helpYaoList) {
                if (isYaoEqual(use.showName, yao)) {
                    return String.format(Locale.CHINA, "$f{%s}$w{(%s%.0f%s服)}，", name, use.amount, drinkNum, Helper.def(use.suffix, ""));
                }
            }
        }
        return "";
    }

    public boolean isYaoEqual(String yao1, String yao2){
        return getStandardYaoName(yao1).equals(getStandardYaoName(yao2));
    }

    public String getStandardYaoName(String yao){
        SingletonData single = SingletonData.getInstance();
        Map<String, String> yaoDict = single.getYaoAliasDict();
        String right = yaoDict.get(yao);
        if (right == null) {
            right = yao;
        }
        return right;
    }

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
