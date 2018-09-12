/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanguine.webpos.comparator;

import com.POSGlobal.controller.clsBillDtl;
import com.POSGlobal.controller.clsSalesFlashColumns;
import com.POSReport.controller.clsBillItemDtlBean;
import com.sanguine.webpos.bean.clsPOSSalesFlashColumns;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class clsSalesFlashComparator implements Comparator<clsPOSSalesFlashColumns>
{

    private List<Comparator<clsPOSSalesFlashColumns>> listComparators;

    @SafeVarargs
    public clsSalesFlashComparator(Comparator<clsPOSSalesFlashColumns>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsPOSSalesFlashColumns o1, clsPOSSalesFlashColumns o2)
    {
        for (Comparator<clsPOSSalesFlashColumns> comparator : listComparators)
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