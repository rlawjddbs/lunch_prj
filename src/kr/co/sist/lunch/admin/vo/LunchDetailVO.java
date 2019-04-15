package kr.co.sist.lunch.admin.vo;

public class LunchDetailVO {

	private String lunch_code, lunch_name, img, spec, input_date;
	private int price;

	public LunchDetailVO(String lunch_code, String lunch_name, String img, String spec, String input_date, int price) {
		this.lunch_code = lunch_code;
		this.lunch_name = lunch_name;
		this.img = img;
		this.spec = spec;
		this.input_date = input_date;
		this.price = price;
	} // LunchDetailVO

	public String getLunch_code() {
		return lunch_code;
	}

	public String getLunch_name() {
		return lunch_name;
	}

	public String getImg() {
		return img;
	}

	public String getSpec() {
		return spec;
	}

	public String getInput_date() {
		return input_date;
	}

	public int getPrice() {
		return price;
	}

	@Override
	public String toString() {
		return "LunchDetailVO [lunch_code=" + lunch_code + ", lunch_name=" + lunch_name + ", img=" + img + ", spec="
				+ spec + ", input_date=" + input_date + ", price=" + price + "]";
	}

	
	
	
	
} // class
