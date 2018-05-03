package com.sj.activity.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;

import com.sj.utils.Utils;


/**
 * <p>Fragment的基类</p>
 *
 * @author 张华洋
 * @name FragmentBase
 */
@Keep
public abstract class FragmentBase extends Fragment {

    protected ActivityBase mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (ActivityBase) context;
    }


    /**
     * 获取宿主Activity
     *
     * @return ActivityBase
     */
    protected ActivityBase getHoldingActivity() {
        return mActivity;
    }


    /**
     * 添加fragment
     *
     * @param fragment
     * @param frameId
     */
    protected void addFragment(FragmentBase fragment, @IdRes int frameId) {
        Utils.checkNotNull(fragment);
        getHoldingActivity().addFragment(fragment, frameId);

    }


    /**
     * 替换fragment
     *
     * @param fragment
     * @param frameId
     */
    protected void replaceFragment(FragmentBase fragment, @IdRes int frameId) {
        Utils.checkNotNull(fragment);
        getHoldingActivity().replaceFragment(fragment, frameId);
    }


    /**
     * 隐藏fragment
     *
     * @param fragment
     */
    protected void hideFragment(FragmentBase fragment) {
        Utils.checkNotNull(fragment);
        getHoldingActivity().hideFragment(fragment);
    }


    /**
     * 显示fragment
     *
     * @param fragment
     */
    protected void showFragment(FragmentBase fragment) {
        Utils.checkNotNull(fragment);
        getHoldingActivity().showFragment(fragment);
    }


    /**
     * 移除Fragment
     *
     * @param fragment
     */
    protected void removeFragment(FragmentBase fragment) {
        Utils.checkNotNull(fragment);
        getHoldingActivity().removeFragment(fragment);

    }


    /**
     * 弹出栈顶部的Fragment
     */
    protected void popFragment() {
        getHoldingActivity().popFragment();
    }

}
