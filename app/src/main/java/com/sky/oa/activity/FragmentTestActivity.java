package com.sky.oa.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.sky.base.ui.BaseActivity;
import com.sky.base.utils.LogUtils;
import com.sky.oa.R;
import com.sky.oa.databinding.ActivityFragmentBinding;
import com.sky.oa.fragment.MyFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2022/4/25 6:08 下午
 * @Version: 1.0
 */
public class FragmentTestActivity extends BaseActivity<ActivityFragmentBinding> {
    @Override
    protected ActivityFragmentBinding inflateBinding() {
        return ActivityFragmentBinding.inflate(getLayoutInflater());
    }

    List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list.add("第一个");
        list.add("第二个");
        list.add("第三个");
//        binding.tabs1.addTab(binding.tabs1.newTab().setText("jfsjlkdjfl"));

        binding.tabs1.addTab(binding.tabs1.newTab().setText(list.get(0)));
        binding.tabs1.addTab(binding.tabs1.newTab().setText(list.get(1)));
        binding.tabs1.addTab(binding.tabs1.newTab().setText(list.get(2)));
        binding.tabs1.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                LogUtils.i("name==" + tab.getPosition());
                changeFragment(list.get(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        changeFragment("第一个");
    }

    private void changeFragment(String tag) {
        FragmentManager manager = getSupportFragmentManager();//获取fragment的管理器
        Fragment fragment = manager.findFragmentByTag(tag);//获取已存在Fragment
        FragmentTransaction tran = manager.beginTransaction();//获取Fragment加载器
        if (fragment == null) {
            fragment = new MyFragment();
            Bundle argus = new Bundle();
            argus.putString("name", tag);
            fragment.setArguments(argus);
            tran.add(R.id.container, fragment, tag);
        } else {
            tran.show(fragment);
        }
        if (current != null)
            tran.hide(current);
        tran.commit();
        current = fragment;

        List<Fragment> fragments = manager.getFragments();
    }

    Fragment current;
}
