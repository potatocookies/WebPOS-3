/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsPOSGroupSubGroupItemBean;

/**
 *
 * @author ajjim
 */
public class clsPOSGroupSubGroupComparator implements Comparator<clsPOSGroupSubGroupItemBean>
{

    private List<Comparator<clsPOSGroupSubGroupItemBean>> listComparators;

    @SafeVarargs
    public clsPOSGroupSubGroupComparator(Comparator<clsPOSGroupSubGroupItemBean>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsPOSGroupSubGroupItemBean o1, clsPOSGroupSubGroupItemBean o2)
    {
        for (Comparator<clsPOSGroupSubGroupItemBean> comparator : listComparators)
        {
            int result = comparator.compare(o1, o2);
            if (result != 0)
            {
                return result;
            }
        }
        return 0;
    }
}