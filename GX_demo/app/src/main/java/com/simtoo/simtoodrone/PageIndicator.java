package com.simtoo.simtoodrone;

import android.support.v4.view.ViewPager;

/**
 * 创建者     张涛
 * 创建时间   2017/12/13 15:45
 * 描述	      ${TODO}
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public interface PageIndicator extends ViewPager.OnPageChangeListener {
    void setViewPager(ViewPager view);

    void setCurrentItem(int item);


    void notifyDataSetChanged();
}
