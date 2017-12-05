package com.wyw.selectcitydemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.wyw.selectcitydemo.meituan.CommonAdapter;
import com.wyw.selectcitydemo.meituan.HeaderRecyclerAndFooterWrapperAdapter;
import com.wyw.selectcitydemo.meituan.ViewHolder;
import com.wyw.selectcitydemo.utils.SharedPreUtils;

import org.apache.commons.lang3a.exception.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 城市选择demo
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    protected static final String INDEX_STRING_HISTORY = "历史记录";
    protected static final String INDEX_STRING_HOT = "热门城市";
    protected static final String INDEX_STRING_LOCAL = "当前城市";
    public static String CITY_KEY = "CITY_HISTORY";
    public static final String CITY = "key_city";


    @BindView(R.id.rv_content)
    RecyclerView mCityRcv;
    @BindView(R.id.indexBar)
    IndexBar mIndexBar;
    @BindView(R.id.tvSideBarHint)
    TextView mTvSideBarHint;


    private Activity mActivity;
    protected CityAdapter cityAdapter;
    protected LinearLayoutManager mManager;
    protected CharDecoration mDecoration;

    private List<CityInfoBean> historyCitys = new ArrayList<>();
    protected List<BaseIndexPinyinBean> mBodyDatas = new ArrayList<>();
    protected List<BaseIndexPinyinBean> mDatas = new ArrayList<>();
    private CityHeaderBean localHeader = new CityHeaderBean();
    private CityHeaderBean hotHeader = new CityHeaderBean();
    private CityHeaderBean HistoryHeader = new CityHeaderBean();

    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAdapter;
    private SharedPreUtils sharedPreUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mActivity = this;
        sharedPreUtils = SharedPreUtils.getInstance().init(this);

        initData();
        dataBind();
    }

    private void initData() {
        //改SideBar文本颜色
        Class<? extends IndexBar> aClass = mIndexBar.getClass();
        try {
            Field field = aClass.getDeclaredField("mPaint");
            Class fieldClass = field.getType();
            Method method = fieldClass.getMethod("setColor", int.class);
            field.setAccessible(true);
            method.invoke(field.get(mIndexBar), getResources().getColor(R.color.font_gray_66));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCityRcv.setLayoutManager(mManager = new LinearLayoutManager(mActivity));
        mCityRcv.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
         //       InputTools.HideKeyboard(etdSearch);  //滑动隐藏键盘
                return false;
            }
        });
        mBodyDatas.add(new CityInfoBean().setName("上海").setId("0131"));
        mBodyDatas.add(new CityInfoBean().setName("北京").setId("0111"));
        mBodyDatas.add(new CityInfoBean().setName("广州").setId("0144"));
        mBodyDatas.add(new CityInfoBean().setName("深圳").setId("0344"));
        mBodyDatas.add(new CityInfoBean().setName("苏州").setId("0532"));
        mBodyDatas.add(new CityInfoBean().setName("无锡").setId("0232"));


        //indexbar初始化
        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager);

        if (mBodyDatas.size() == 0) {
            return;
        }
    }

    protected void dataBind() {

        String localCityName = getIntent().getStringExtra("local_city");
        //通过地理坐标获取当前城市
        localCityName = TextUtils.isEmpty(localCityName) ? "定位失败" : localCityName;
        CityInfoBean localCity = new CityInfoBean().setName(localCityName).setTop(true).setBaseIndexTag(INDEX_STRING_LOCAL);


        List<CityInfoBean> localCitys = new ArrayList<>();
        localCitys.add(localCity);
        localHeader.setCityList(localCitys).setSuspensionTag(INDEX_STRING_LOCAL);

        List<CityInfoBean> hotCitys = new ArrayList<>();
        hotCitys.add(new CityInfoBean().setName("上海").setId("0131"));
        hotCitys.add(new CityInfoBean().setName("北京").setId("0111"));
        hotCitys.add(new CityInfoBean().setName("广州").setId("0144"));
        hotCitys.add(new CityInfoBean().setName("深圳").setId("0344"));
        hotCitys.add(new CityInfoBean().setName("苏州").setId("0532"));
        hotCitys.add(new CityInfoBean().setName("无锡").setId("0232"));
        hotHeader.setCityList(hotCitys).setSuspensionTag(INDEX_STRING_HOT);

        //    historyCitys = JsonUtils.json2List(sharedPreUtils.getValue(CITY_KEY, ""), CityInfoBean.class);
        historyCitys.add(new CityInfoBean().setName("上海").setId("0131"));
        historyCitys.add(new CityInfoBean().setName("北京").setId("0111"));
        historyCitys.add(new CityInfoBean().setName("广州").setId("0144"));
        historyCitys.add(new CityInfoBean().setName("深圳").setId("0344"));
        HistoryHeader.setCityList(historyCitys).setSuspensionTag(INDEX_STRING_HISTORY);

        mDatas.add(localHeader);
        mDatas.add(hotHeader);
        mDatas.add(HistoryHeader);

        cityAdapter = new CityAdapter(mActivity, mBodyDatas);
        mHeaderAdapter = new HeaderRecyclerAndFooterWrapperAdapter(cityAdapter) {
            @Override
            protected void onBindHeaderHolder(ViewHolder holder, int headerPos, int layoutId, Object o) {
                final CityHeaderBean meituanHeaderBean = (CityHeaderBean) o;

                switch (layoutId) {
                    case R.layout.meituan_item_header_local: {
                        RecyclerView recyclerView = holder.getView(R.id.rvCity);
                        recyclerView.setAdapter(
                                new CommonAdapter<CityInfoBean>(mActivity, R.layout.item_local_city, meituanHeaderBean.getCityList()) {
                                    @Override
                                    public void convert(ViewHolder holder, final CityInfoBean cityName) {
                                        holder.setText(R.id.tv_city, cityName.getTarget());
                                    }
                                });
                        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
                    }
                    break;
                    case R.layout.meituan_item_header: {
                        //网格
                        RecyclerView recyclerView = holder.getView(R.id.rvCity);
                        recyclerView.setAdapter(
                                new CommonAdapter<CityInfoBean>(mActivity, R.layout.item_hot_city, meituanHeaderBean.getCityList()) {
                                    @Override
                                    public void convert(ViewHolder holder, final CityInfoBean cityName) {
                                        holder.setText(R.id.tv_city, cityName.getTarget());
                                        holder.itemView.setTag(R.id.cb_item_tag, cityName);
                                        holder.itemView.setOnClickListener(MainActivity.this);
                                    }
                                });
                        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
                    }
                    break;
                    case R.layout.meituan_history_header: { //历史记录

                        //网格
                        RecyclerView recyclerView = holder.getView(R.id.rvCity);
                        final TextView tv_noData = holder.getView(R.id.tv_nodata);
                        recyclerView.setAdapter(
                                new CommonAdapter<CityInfoBean>(mActivity, R.layout.item_hot_city, meituanHeaderBean.getCityList()) {
                                    @Override
                                    public void convert(ViewHolder holder, final CityInfoBean cityName) {
                                        if (meituanHeaderBean.getCityList().size() == 0) {
                                            tv_noData.setVisibility(View.VISIBLE);
                                        } else {
                                            tv_noData.setVisibility(View.GONE);
                                        }
                                        holder.setText(R.id.tv_city, cityName.getTarget());
                                        holder.itemView.setTag(R.id.cb_item_tag, cityName);
                                        holder.itemView.setOnClickListener(MainActivity.this);
                                    }
                                });
                        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 3));
                    }
                    break;
                    case R.layout.meituan_item_header_top:
                        CityHeaderBean meituanTopHeaderBean = (CityHeaderBean) o;
                        holder.setText(R.id.tvCurrent, meituanTopHeaderBean.getTarget());
                        break;
                    default:
                        break;
                }
            }
        };

        mHeaderAdapter.setHeaderView(0, R.layout.meituan_item_header_local, localHeader);
        mHeaderAdapter.setHeaderView(1, R.layout.meituan_item_header, hotHeader);
        mHeaderAdapter.setHeaderView(2, R.layout.meituan_history_header, HistoryHeader);

        mDecoration = new CharDecoration(mActivity, mDatas);
        mDecoration.setmDatas(mDatas);
        mCityRcv.addItemDecoration(mDecoration);
        mCityRcv.setAdapter(mHeaderAdapter);

        mDatas.clear();
        mDatas.add(localHeader);
        mDatas.add(hotHeader);
        mDatas.add(HistoryHeader);
        mDatas.addAll(mBodyDatas);
        mIndexBar.setmSourceDatas(mDatas).invalidate();
        mIndexBar.getDataHelper().sortSourceDatas(mBodyDatas);
        cityAdapter.setDatas(mBodyDatas);
        mHeaderAdapter.notifyDataSetChanged();
        mIndexBar.setmSourceDatas(mDatas)//设置数据
                .invalidate();
        mDecoration.setmDatas(mDatas);


    }

    @Override
    public void onClick(View view) {
        Object tag = view.getTag(R.id.cb_item_tag);
        if (tag != null && tag instanceof CityInfoBean) {
            CityInfoBean model = (CityInfoBean) tag;
            String cityName = model.getName();
            int lastIndex = StringUtils.lastIndexOf(cityName, "市");
            if (lastIndex > 0 && lastIndex < cityName.length()) {
                cityName = cityName.substring(0, lastIndex);
            }
            Intent intent = new Intent();

            intent.putExtra(CITY, model);
            String modelId = model.getId();


            if ("定位中".equalsIgnoreCase(cityName)) {//定位中
                Toast.makeText(mActivity, "正在定位中,请从列表中直接选择", Toast.LENGTH_SHORT);
            } else if (TextUtils.isEmpty(modelId)) {//暂不支持
                Toast.makeText(mActivity, "暂不支持当前城市", Toast.LENGTH_SHORT);
            } else {
                intent.putExtra(CITY, model);
                setResult(RESULT_OK, intent);
                //     InputTools.HideKeyboard(etdSearch);
                finish();

            }
        }
    }

    class CityAdapter extends RecyclerView.Adapter<ViewHolder> {
        Context context;

        List<BaseIndexPinyinBean> datas = new ArrayList<>();
        List<BaseIndexPinyinBean> mDatas = null;

        public CityAdapter(Context context, List<BaseIndexPinyinBean> data) {
            this.context = context;
            this.mDatas = data;
            this.datas.addAll(mDatas);
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CityViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_shebao_city_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BaseIndexPinyinBean bean = datas.get(position);
            holder.itemView.setTag(R.id.cb_item_tag, bean);
            holder.itemView.setOnClickListener(MainActivity.this);

            CityViewHolder cityViewHolder = (CityViewHolder) holder;
            cityViewHolder.tvCity.setText(bean.getTarget());
            cityViewHolder.tvTag.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }


        public CityAdapter setDatas(List<BaseIndexPinyinBean> datas) {
            this.mDatas = datas;
            this.datas.clear();
            this.datas.addAll(datas);
            return this;
        }
    }

    static class CityViewHolder extends ViewHolder {
        @BindView(R.id.tv_item_credit_city_tag)
        protected TextView tvTag;

        @BindView(R.id.tv_item_credit_city_name)
        protected TextView tvCity;

        public CityViewHolder(View view) {
            super(view.getContext(), view);
            ButterKnife.bind(this, view);
        }
    }
}
