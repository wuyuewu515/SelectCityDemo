package com.wyw.selectcitydemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 10001: {
                    CityInfoBean cityInfoBean = (CityInfoBean) data.getSerializableExtra("key_city");
                    Toast.makeText(this, cityInfoBean.getName(), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
