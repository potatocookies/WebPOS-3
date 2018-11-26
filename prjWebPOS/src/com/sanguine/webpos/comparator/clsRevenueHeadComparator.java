/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsRevenueHeadWiseSalesReportBean;


/**
 *
 * @author ajjim
 */
public class clsRevenueHeadComparator implements Comparator<clsRevenueHeadWiseSalesReportBean>
{

    private List<Comparator<clsRevenueHeadWiseSalesReportBean>> listComparators;

    @SafeVarargs
    public clsRevenueHeadComparator(Comparator<clsRevenueHeadWiseSalesReportBean>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsRevenueHeadWiseSalesReportBean o1, clsRevenueHeadWiseSalesReportBean o2)
    {
        for (Comparator<clsRevenueHeadWiseSalesReportBean> comparator : listComparators)
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