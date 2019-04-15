package kr.co.sist.lunch.user.vo;

public class LunchDetailVO {
	private String lunchCode, lunchName, spec, img;
	private int price;
	
	public LunchDetailVO(String lunchCode, String lunchName, String spec, String img, int price) {
		this.lunchCode = lunchCode;
		this.lunchName = lunchName;
		this.spec = spec;
		this.img = img;
		this.price = price;
	} // LunchDetailVO

	public String getLunchCode() {
		return lunchCode;
	} // getLunchCode

	public String getLunchName() {
		return lunchName;
	} // getLunchName

	public String getSpec() {
		return spec;
	} // getSpec

	public String getImg() {
		return img;
	} // getImg

	public int getPrice() {
		return price;
	} // getPrice

	@Override
	public String toString() {
		return "LunchDetailVO [lunchCode=" + lunchCode + ", lunchName=" + lunchName + ", spec=" + spec + ", img=" + img
				+ ", price=" + price + "]";
	} // toString
	
} // class
