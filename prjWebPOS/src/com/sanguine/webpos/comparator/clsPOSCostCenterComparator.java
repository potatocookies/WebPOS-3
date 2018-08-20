

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsPOSCostCenterBean;

/**
 *
 * @author ajjim
 */
public class clsPOSCostCenterComparator implements Comparator<clsPOSCostCenterBean>
{

    private List<Comparator<clsPOSCostCenterBean>> listComparators;

    @SafeVarargs
    public clsPOSCostCenterComparator(Comparator<clsPOSCostCenterBean>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override	
    public int compare(clsPOSCostCenterBean o1, clsPOSCostCenterBean o2)
    {
        for (Comparator<clsPOSCostCenterBean> comparator : listComparators)
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