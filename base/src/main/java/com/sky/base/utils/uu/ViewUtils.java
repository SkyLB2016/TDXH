package com.sky.base.utils.uu;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sky.base.R;


/**
 * Created by SKY on 2017/9/29 16:55.
 * 控件工具类
 */
public class ViewUtils {
    /**
     * 修改 控件内的字体
     *
     * @param root 父布局view
     * @param path 字体路径
     * @param act
     */
    public static void modifyTypeface(ViewGroup root, String path, Activity act) {
        //path是字体路径
        Typeface type = Typeface.createFromAsset(act.getAssets(), path);
        for (int i = 0; i < root.getChildCount(); i++) {
            View childAt = root.getChildAt(i);
            if (childAt instanceof TextView) ((TextView) childAt).setTypeface(type);
            else if (childAt instanceof Button) ((Button) childAt).setTypeface(type);
            else if (childAt instanceof EditText) ((EditText) childAt).setTypeface(type);
            else if (childAt instanceof ViewGroup) modifyTypeface((ViewGroup) childAt, path, act);
        }
    }

    /**
     * 获取当前页面的焦点
     *
     * @param et 所要获取焦点的控件
     */
    public static void getFocus(EditText et) {
        et.setFocusable(true);
        et.setSelection(et.getText().length());
        et.startAnimation(AnimationUtils.loadAnimation(et.getContext(), R.anim.anim_shake));
        et.requestFocus();
    }
}
