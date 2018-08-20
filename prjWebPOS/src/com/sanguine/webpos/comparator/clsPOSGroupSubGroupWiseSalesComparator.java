/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.sanguine.webpos.bean.clsPOSGroupSubGroupWiseSales;

/**
 *
 * @author ajjim
 */
public class clsPOSGroupSubGroupWiseSalesComparator implements Comparator<clsPOSGroupSubGroupWiseSales>
{

    private List<Comparator<clsPOSGroupSubGroupWiseSales>> listComparators;

    @SafeVarargs
    public clsPOSGroupSubGroupWiseSalesComparator(Comparator<clsPOSGroupSubGroupWiseSales>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsPOSGroupSubGroupWiseSales o1, clsPOSGroupSubGroupWiseSales o2)
    {
        for (Comparator<clsPOSGroupSubGroupWiseSales> comparator : listComparators)
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
