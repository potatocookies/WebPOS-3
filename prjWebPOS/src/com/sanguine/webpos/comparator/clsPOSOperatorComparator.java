/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsPOSOperatorDtl;



public class clsPOSOperatorComparator implements Comparator<clsPOSOperatorDtl>
{
     private List<Comparator<clsPOSOperatorDtl>> listComparators;

    @SafeVarargs
    public clsPOSOperatorComparator(Comparator<clsPOSOperatorDtl>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsPOSOperatorDtl o1, clsPOSOperatorDtl o2)
    {
        for (Comparator<clsPOSOperatorDtl> comparator : listComparators)
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


