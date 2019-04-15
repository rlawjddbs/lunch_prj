package kr.co.sist.lunch.admin.vo;

public class FlagVO {

	private String orderNum;
	private boolean flag;
	
	public FlagVO(String orderNum, boolean flag) {
		this.orderNum = orderNum;
		this.flag = flag;
	} // FlagVO

	public String getOrderNum() {
		return orderNum;
	} // getOrderNum

	public boolean getFlag() {
		return flag;
	} // getFlag

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

} // class
