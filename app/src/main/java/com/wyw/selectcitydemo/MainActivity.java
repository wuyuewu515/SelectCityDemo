package com.wyw.selectcitydemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.wyw.selectcitydemo.meituan.CommonAdapter;
import com.wyw.selectcitydemo.meituan.HeaderRecyclerAndFooterWrapperAdapter;
import com.wyw.selectcitydemo.meituan.ViewHolder;
import com.wyw.selectcitydemo.utils.CharacterParser;
import com.wyw.selectcitydemo.utils.CollectionUtils;
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
public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    protected static final String INDEX_STRING_HISTORY = "历史记录";
    protected static final String INDEX_STRING_HOT = "热门城市";
    protected static final String INDEX_STRING_LOCAL = "当前城市";
    protected static final int MSG_SEARCH = 1000;

    public static String CITY_KEY = "CITY_HISTORY";
    public static final String CITY = "key_city";


    @BindView(R.id.rv_content)
    RecyclerView mCityRcv;
    @BindView(R.id.indexBar)
    IndexBar mIndexBar;
    @BindView(R.id.tvSideBarHint)
    TextView mTvSideBarHint;
    @BindView(R.id.tv_selectCity_back)
    ImageView ivSelectCityBack;
    @BindView(R.id.etd_search)
    EditTextWithDel etdSearch;


    private Activity mActivity;
    protected LinearLayoutManager mManager;
    protected CharDecoration mDecoration;

    private List<CityInfoBean> historyCitys = new ArrayList<>();
    protected List<BaseIndexPinyinBean> mBodyDatas = new ArrayList<>();
    protected List<BaseIndexPinyinBean> mDatas = new ArrayList<>();
    private CityHeaderBean localHeader = new CityHeaderBean();
    private CityHeaderBean hotHeader = new CityHeaderBean();
    private CityHeaderBean HistoryHeader = new CityHeaderBean();
    private FilterAdapter filterAdapter;

    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAdapter;
    private SharedPreUtils sharedPreUtils;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SEARCH: {
                    if (msg.obj != null) {
                        String filter = msg.obj.toString();
                        filter(filter);
                    }
                }
                break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //阻止软键盘弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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

        etdSearch.addTextChangedListener(this);
        mCityRcv.setLayoutManager(mManager = new LinearLayoutManager(mActivity));
        mCityRcv.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                HideKeyboard(etdSearch);
                return false;
            }
        });
        mBodyDatas.add(new CityInfoBean().setName("上海").setId("0131"));
        mBodyDatas.add(new CityInfoBean().setName("上饶").setId("0135"));
        mBodyDatas.add(new CityInfoBean().setName("北京").setId("0111"));
        mBodyDatas.add(new CityInfoBean().setName("广州").setId("0144"));
        mBodyDatas.add(new CityInfoBean().setName("深圳").setId("0344"));
        mBodyDatas.add(new CityInfoBean().setName("苏州").setId("0532"));
        mBodyDatas.add(new CityInfoBean().setName("无锡").setId("0232"));
        mBodyDatas.add(new CityInfoBean().setName("本溪").setId("0232"));
        mBodyDatas.add(new CityInfoBean().setName("承德").setId("0232"));
        mBodyDatas.add(new CityInfoBean().setName("益阳").setId("0232"));
        mBodyDatas.add(new CityInfoBean().setName("黄冈").setId("0232"));
        mBodyDatas.add(new CityInfoBean().setName("莆田").setId("0232"));
        mBodyDatas.add(new CityInfoBean().setName("临汾").setId("0232"));
        mBodyDatas.add(new CityInfoBean().setName("五常").setId("0232"));
        mBodyDatas.add(new CityInfoBean().setName("酒泉").setId("0232"));
        mBodyDatas.add(new CityInfoBean().setName("玉门").setId("0232"));
        mBodyDatas.add(new CityInfoBean().setName("吴县").setId("0232"));
        mBodyDatas.add(new CityInfoBean().setName("常熟").setId("0232"));
        mBodyDatas.add(new CityInfoBean().setName("吴江").setId("0232"));

        //indexbar初始化
        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(mManager);

        if (mBodyDatas.size() == 0) {
            return;
        }

        ivSelectCityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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

        filterAdapter = new FilterAdapter(mActivity, mBodyDatas);
        mHeaderAdapter = new HeaderRecyclerAndFooterWrapperAdapter(filterAdapter) {
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

        /**
         * 初始化赋值
         */
        mDatas.addAll(mBodyDatas);
        mDecoration = new CharDecoration(mActivity, mDatas);
        mDecoration.setmDatas(mDatas);

        mCityRcv.setAdapter(mHeaderAdapter);

        /**
         * 初始化过滤条件
         */
        filter(null);
    }

    /**
     * 核心方法：1、通过过滤条件初始化数据；2、通过过滤条件控制辅助View；3、应用数据到核心显示控件
     *
     * @param filter
     */
    public void filter(String filter) {
        if (StringUtils.isEmpty(filter)) {//无过滤条件
            mDatas.add(0, localHeader);
            mDatas.add(1, hotHeader);
            mDatas.add(2, HistoryHeader);

            mHeaderAdapter.setHeaderView(0, R.layout.meituan_item_header_local, localHeader);
            mHeaderAdapter.setHeaderView(1, R.layout.meituan_item_header, hotHeader);
            mHeaderAdapter.setHeaderView(2, R.layout.meituan_history_header, HistoryHeader);

            mCityRcv.addItemDecoration(mDecoration);
            mIndexBar.setVisibility(View.VISIBLE);
        } else {//过滤条件有效，开始初始化过滤条件
            mDatas.remove(localHeader);
            mDatas.remove(hotHeader);
            mDatas.remove(HistoryHeader);

            mHeaderAdapter.clearHeaderView();

            mCityRcv.removeItemDecoration(mDecoration);
            mIndexBar.setVisibility(View.GONE);
        }

        /**
         * 应用过滤条件到各个View上
         */
        mIndexBar.setmSourceDatas(mDatas).invalidate();
        mIndexBar.getDataHelper().sortSourceDatas(mBodyDatas);
        filterAdapter.setFilter(filter);
        mHeaderAdapter.notifyDataSetChanged();
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
                HideKeyboard(etdSearch);
                finish();

            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        //移除队列中的搜索消息
        mHandler.removeMessages(MSG_SEARCH);
        Message msg = mHandler.obtainMessage(MSG_SEARCH, editable.toString());
        mHandler.sendMessageDelayed(msg, 200);

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

    class FilterAdapter extends RecyclerView.Adapter<CityViewHolder> {
        Context context;
        List<BaseIndexPinyinBean> datas = new ArrayList<>();
        List<BaseIndexPinyinBean> mDatas = null;

        private CityFilter cityFilter;

        public FilterAdapter(Context context, List<BaseIndexPinyinBean> data) {
            this.context = context;
            this.mDatas = data;
            this.datas.addAll(mDatas);
            cityFilter = new CityFilter();
        }

        @Override
        public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CityViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.item_shebao_city_layout, parent, false));

        }

        @Override
        public void onBindViewHolder(CityViewHolder holder, int position) {
            BaseIndexPinyinBean bean = datas.get(position);
            holder.itemView.setTag(R.id.cb_item_tag, bean);
            holder.itemView.setOnClickListener(MainActivity.this);
            holder.tvCity.setText(bean.getTarget());
            holder.tvTag.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        public void setFilter(String filter) {
            if (TextUtils.isEmpty(filter)) {
                datas.clear();
                datas.addAll(mDatas);
            } else {
                //从过滤掉的数据中去除上面三区域的数据
                List<BaseIndexPinyinBean> temp = CollectionUtils.filter(mDatas, cityFilter.setFilter(filter));
                temp.remove(localHeader);
                temp.remove(hotHeader);
                temp.remove(HistoryHeader);
                datas.clear();
                datas.addAll(temp);
            }

            notifyDataSetChanged();
        }
    }

    /**
     * 城市筛选
     */
    static class CityFilter implements CollectionUtils.ListFilter<BaseIndexPinyinBean> {
        protected String filter;
        CharacterParser characterParser = new CharacterParser();

        @Override
        public boolean filter(BaseIndexPinyinBean element) {
            if (StringUtils.isEmpty(filter)) {
                return true;
            } else if (StringUtils.isEmpty(element.getTarget())) {
                return false;
            } else {
                if (element.getTarget().contains(filter) ||
                        characterParser.getSelling(element.getTarget()).startsWith(filter)) {
                    return true;
                }
                return false;
            }
        }

        public CityFilter setFilter(String filter) {
            this.filter = filter;

            return this;
        }
    }

    // 隐藏虚拟键盘
    public static void HideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

}
