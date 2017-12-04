package com.wyw.selectcitydemo;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

import java.util.List;

/**
 * 介绍：美团城市列表 HeaderView Bean
 * 作者：zhangxutong
 * 邮箱：mcxtzhang@163.com
 * 主页：http://blog.csdn.net/zxt0601
 * 时间： 2016/11/28.
 */

public class CityHeaderBean extends BaseIndexPinyinBean {
    private List<CityInfoBean> cityList;
    //悬停ItemDecoration显示的Tag
    private String suspensionTag;

    public CityHeaderBean() {
    }

    public CityHeaderBean(List<CityInfoBean> cityList, String suspensionTag, String indexBarTag) {
        this.cityList = cityList;
        this.suspensionTag = suspensionTag;
        this.setBaseIndexTag(indexBarTag);
    }

    public List<CityInfoBean> getCityList() {
        return cityList;
    }

    public CityHeaderBean setCityList(List<CityInfoBean> cityList) {
        this.cityList = cityList;
        return this;
    }

    public CityHeaderBean setSuspensionTag(String suspensionTag) {
        this.suspensionTag = suspensionTag;
        setBaseIndexTag(String.valueOf(suspensionTag.charAt(0)));
        return this;
    }

    @Override
    public String getTarget() {
        return suspensionTag;
    }

    @Override
    public boolean isNeedToPinyin() {
        return false;
    }

    @Override
    public String getSuspensionTag() {
        return suspensionTag;
    }

}
