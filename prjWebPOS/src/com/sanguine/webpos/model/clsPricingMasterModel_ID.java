package com.sanguine.webpos.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class clsPricingMasterModel_ID implements Serializable
{

//Variable Declaration
	@Column(name = "longPricingId")
	private Long	longPricingId;

	

	public clsPricingMasterModel_ID()
	{
	}

	public clsPricingMasterModel_ID(long longPricingId)
	{
		this.longPricingId = longPricingId;		
	}

//Setter-Getter Methods
	public long getLongPricingId()
	{
		return longPricingId;
	}

	public void setLongPricingId(long longPricingId)
	{
		this.longPricingId = longPricingId;
	}


//HashCode and Equals Funtions
	@Override
	public boolean equals(Object obj)
	{
		clsPricingMasterModel_ID objModelId = (clsPricingMasterModel_ID) obj;
		if (this.longPricingId.equals(objModelId.getLongPricingId()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	
	@Override
	public int hashCode()
	{
		return this.longPricingId.hashCode();
	}

}
