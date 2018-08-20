/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

	
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsPOSBillItemDtlBean;

/**
 *
 * @author ajjim
 */

public class clsPOSBillComparator implements Comparator<clsPOSBillItemDtlBean>
{

    private List<Comparator<clsPOSBillItemDtlBean>> listComparators;

    @SafeVarargs
    public clsPOSBillComparator(Comparator<clsPOSBillItemDtlBean>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsPOSBillItemDtlBean o1, clsPOSBillItemDtlBean o2)
    {
        for (Comparator<clsPOSBillItemDtlBean> comparator : listComparators)
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