package com.sanguine.webpos.bean;

public class clsPOSItemDtlForTax
{
	 private String itemCode;
	    
	    private String itemName;
	    
	    private double amount;
	    
	    private double discAmt;
	    
	    private double discPer;

	    public String getItemCode()
	    {
	        return itemCode;
	    }

	    public void setItemCode(String itemCode)
	    {
	        this.itemCode = itemCode;
	    }

	    public String getItemName()
	    {
	        return itemName;
	    }

	    public void setItemName(String itemName)
	    {
	        this.itemName = itemName;
	    }

	    public double getAmount()
	    {
	        return amount;
	    }

	    public void setAmount(double amount)
	    {
	        this.amount = amount;
	    }

	    public double getDiscAmt()
	    {
	        return discAmt;
	    }

	    public void setDiscAmt(double discAmt)
	    {
	        this.discAmt = discAmt;
	    }

	    public double getDiscPer()
	    {
	        return discPer;
	    }

	    public void setDiscPer(double discPer)
	    {
	        this.discPer = discPer;
	    }
	    
	    

}
