/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsPOSBillDtl;

/**
 *
 * @author ajjim
 */

public class clsPOSBillComplimentaryComparator implements Comparator<clsPOSBillDtl>
{

    private List<Comparator<clsPOSBillDtl>> listComparators;

    @SafeVarargs	
    public clsPOSBillComplimentaryComparator(Comparator<clsPOSBillDtl>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsPOSBillDtl o1, clsPOSBillDtl o2)
    {
        for (Comparator<clsPOSBillDtl> comparator : listComparators)
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