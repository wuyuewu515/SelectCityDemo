package com.wyw.selectcitydemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 项目名称：SelectCityDemo
 * 类描述：
 * 创建人：伍跃武
 * 创建时间：2017/12/4 14:43
 */
public class LaunchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.luach_layout_activity);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_select)
    public void onViewClicked() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, 10001);
    }
}
