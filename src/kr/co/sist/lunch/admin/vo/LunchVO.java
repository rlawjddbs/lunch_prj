package kr.co.sist.lunch.admin.vo;

public class LunchVO {

	private String lunchCode, lunchName, img;
	private int price;
	public LunchVO(String lunchCode, String lunchName, String img, int price) {
		this.lunchCode = lunchCode;
		this.lunchName = lunchName;
		this.img = img;
		this.price = price;
	} // LunchVO
	
	public String getLunchCode() {
		return lunchCode;
	} // getLunchCode
	
	public String getLunchName() {
		return lunchName;
	} // getLunchName
	
	public String getImg() {
		return img;
	} // getImg
	
	public int getPrice() {
		return price;
	} // getPrice

	@Override
	public String toString() {
		return "LunchVO [lunchCode=" + lunchCode + ", lunchName=" + lunchName + ", img=" + img + ", price=" + price
				+ "]";
	} // toString

	
	
	
} // class
