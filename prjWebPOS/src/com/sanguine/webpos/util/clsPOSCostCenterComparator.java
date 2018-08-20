

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import com.sanguine.webpos.bean.clsPOSCostCenterWiseSalesReportBean;


/**
 *
 * @author ajjim
 */
public class clsPOSCostCenterComparator implements Comparator<clsPOSCostCenterWiseSalesReportBean>
{

    private List<Comparator<clsPOSCostCenterWiseSalesReportBean>> listComparators;

    @SafeVarargs
    public clsPOSCostCenterComparator(Comparator<clsPOSCostCenterWiseSalesReportBean>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsPOSCostCenterWiseSalesReportBean o1, clsPOSCostCenterWiseSalesReportBean o2)
    {
        for (Comparator<clsPOSCostCenterWiseSalesReportBean> comparator : listComparators)
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