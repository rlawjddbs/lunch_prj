package kr.co.sist.lunch.admin.vo;

public class LunchAddVO {
	private String lunch_name, img, spec;
	private int price;

	public LunchAddVO(String lunch_name, String img, String spec, int price) {
		this.lunch_name = lunch_name;
		this.img = img;
		this.spec = spec;
		this.price = price;
	} // LunchAddVO

	
	
	public String getLunch_name() {
		return lunch_name;
	} // getLunch_name



	public String getImg() {
		return img;
	} // getImg



	public String getSpec() {
		return spec;
	} // getSpec



	public int getPrice() {
		return price;
	} // getPrice

	
	
} // class
