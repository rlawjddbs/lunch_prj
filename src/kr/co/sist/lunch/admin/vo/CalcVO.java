package kr.co.sist.lunch.admin.vo;

public class CalcVO {
	private String lunchCode, lunchName;
	private int price, total;
	
	public CalcVO(String lunchCode, String lunchName, int price, int total) {
		this.lunchCode = lunchCode;
		this.lunchName = lunchName;
		this.price = price;
		this.total = total;
	} // CalcVO

	public String getLunchCode() {
		return lunchCode;
	} // getLunchCode

	public String getLunchName() {
		return lunchName;
	} // getLunchName

	public int getPrice() {
		return price;
	} // getPrice

	public int getTotal() {
		return total;
	} // getTotal

	@Override
	public String toString() {
		return "CalcVO [lunchCode=" + lunchCode + ", lunchName=" + lunchName + ", price=" + price + ", total=" + total
				+ "]";
	}

	
} // class
