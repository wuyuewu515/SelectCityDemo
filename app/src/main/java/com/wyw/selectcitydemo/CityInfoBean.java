package com.wyw.selectcitydemo;

import com.google.gson.annotations.Expose;
import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

import java.io.Serializable;

/**
 * 项目名称：SelectCityDemo
 * 类描述：城市实体对象
 * 创建人：伍跃武
 * 创建时间：2017/12/4 13:40
 */
public class CityInfoBean extends BaseIndexPinyinBean implements Serializable{
    @Expose
    protected String id;

    @Expose
    protected String name;


    private boolean isTop;//是否是最上面的 不需要被转化成拼音的


    public String getId() {
        return id;
    }

    public CityInfoBean setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public CityInfoBean setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getTarget() {
        return name;
    }


    public boolean isTop() {
        return isTop;
    }

    public CityInfoBean setTop(boolean top) {
        isTop = top;
        return this;
    }

    @Override
    public CityInfoBean setBaseIndexTag(String baseIndexTag) {
        super.setBaseIndexTag(baseIndexTag);
        return this;
    }

    @Override
    public boolean isNeedToPinyin() {
        return !isTop;
    }


    @Override
    public boolean isShowSuspension() {
        return !isTop;
    }
}
