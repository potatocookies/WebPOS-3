/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsPOSVoidBillDtl;

/**
 *
 * @author ajjim
 */
public class clsVoidBillComparator implements Comparator<clsPOSVoidBillDtl>
{

    private List<Comparator<clsPOSVoidBillDtl>> listComparators;

    @SafeVarargs
    public clsVoidBillComparator(Comparator<clsPOSVoidBillDtl>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsPOSVoidBillDtl o1, clsPOSVoidBillDtl o2)
    {
        for (Comparator<clsPOSVoidBillDtl> comparator : listComparators)
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
