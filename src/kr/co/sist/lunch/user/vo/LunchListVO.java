package kr.co.sist.lunch.user.vo;

public class LunchListVO {

	private String img, lunchCode, lunchName, lunchSpec;

	public LunchListVO(String img, String lunchCode, String lunchName, String lunchSpec) {
		super();
		this.img = img;
		this.lunchCode = lunchCode;
		this.lunchName = lunchName;
		this.lunchSpec = lunchSpec;
	} // LunchListVO

	public String getImg() {
		return img;
	} // getImg

	public String getLunchCode() {
		return lunchCode;
	} // getLunchCode

	public String getLunchName() {
		return lunchName;
	} // getLunchName

	public String getLunchSpec() {
		return lunchSpec;
	} // getLunchSpec

	@Override
	public String toString() {
		return "LunchListVO [img=" + img + ", lunchCode=" + lunchCode + ", lunchName=" + lunchName + ", lunchSpec="
				+ lunchSpec + "]";
	} // toString

	
} // class
