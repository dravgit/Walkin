/**
 * 
 */
package com.cyp.walkin.cyp.utils;

public class ShoppePara {
	private String posNo;  
	private String shoppeNo; 
	private String shopNo; 
	/**
	 * 
	 */
	public ShoppePara() {
		// TODO Auto-generated constructor stub
	}
	
	public ShoppePara(String posNo, String shoppeNo, String shopNo) {
		super();
		this.posNo = posNo;
		this.shoppeNo = shoppeNo;
		this.shopNo = shopNo;
	}

	public String getPosNo() {
		return posNo;
	}
	public void setPosNo(String posNo) {
		this.posNo = posNo;
	}
	public String getShoppeNo() {
		return shoppeNo;
	}
	public void setShoppeNo(String shoppeNo) {
		this.shoppeNo = shoppeNo;
	}
	public String getShopNo() {
		return shopNo;
	}
	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}
	
}
