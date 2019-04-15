package kr.co.sist.lunch.user.vo;

public class OrderListVO {

   private String lunchName, orderDate;
      
   private int quan;

   public OrderListVO(String lunchName, String orderDate, int quan) {
      super();
      this.lunchName = lunchName;
      this.orderDate = orderDate;
      this.quan = quan;
   }

   public String getLunchName() {
      return lunchName;
   }

   public String getOrderDate() {
      return orderDate;
   }

   public int getQuan() {
      return quan;
   }

   @Override
   public String toString() {
      return "OrderListVO [lunchName=" + lunchName + ", inputDate=" + orderDate + ", quan=" + quan + "]";
   }
   
   
} // class