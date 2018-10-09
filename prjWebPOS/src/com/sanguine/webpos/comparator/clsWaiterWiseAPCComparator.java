/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;


import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsPOSAPCReport;

/**
 *
 * @author ajjim
 */
public class clsWaiterWiseAPCComparator implements Comparator<clsPOSAPCReport>
{

    private List<Comparator<clsPOSAPCReport>> listComparators;

    @SafeVarargs
    public clsWaiterWiseAPCComparator(Comparator<clsPOSAPCReport>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsPOSAPCReport o1, clsPOSAPCReport o2)
    {
        for (Comparator<clsPOSAPCReport> comparator : listComparators)
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