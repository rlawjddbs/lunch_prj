package kr.co.sist.lunch.user.vo;

public class OrderAddVO {
	private String orderName, phone, ipAdress, lunchCode, request;
	private int quan;
	
	public OrderAddVO(String orderName, String phone, String ipAdress, String lunchCode, int quan, String request) {
		this.orderName = orderName;
		this.phone = phone;
		this.ipAdress = ipAdress;
		this.lunchCode = lunchCode;
		this.quan = quan;
		this.request = request;
	} // OrderAddVO
	
	public String getOrderName() {
		return orderName;
	} // getOrderName
	public String getPhone() {
		return phone;
	} // getPhone
	public String getIpAdress() {
		return ipAdress;
	} // getIpAdress
	public String getLunchCode() {
		return lunchCode;
	} // getLunchCode
	public int getQuan() {
		return quan;
	} // getQuan
	public String getRequest() {
		return request;
	} // getRequest
	
} // class
