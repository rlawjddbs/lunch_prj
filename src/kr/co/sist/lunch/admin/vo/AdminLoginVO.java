package kr.co.sist.lunch.admin.vo;

public class AdminLoginVO {
	private String id, pass;
	
	public AdminLoginVO(String id, String pass) {
		this.id = id;
		this.pass = pass;
	} // AdminLoginVO

	public String getId() {
		return id;
	} // getId

	public String getPass() {
		return pass;
	} // getPass
	
	
	
} // class
