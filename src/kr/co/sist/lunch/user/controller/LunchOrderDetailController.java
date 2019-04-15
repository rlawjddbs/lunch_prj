package kr.co.sist.lunch.user.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import kr.co.sist.lunch.user.model.LunchClientDAO;
import kr.co.sist.lunch.user.view.LunchOrderDetailView;
import kr.co.sist.lunch.user.vo.OrderAddVO;

import static javax.swing.JOptionPane.showMessageDialog;

public class LunchOrderDetailController extends WindowAdapter implements ActionListener{

	private LunchOrderDetailView lodv;
	private String lunchCode;
	
	
	public LunchOrderDetailController(LunchOrderDetailView lodv, String lunchCode) {
		this.lodv = lodv;
		this.lunchCode = lunchCode;
	} // LunchOrderDetailController

	private void lunchOrder() {
		JTextField jtfOrderName = lodv.getJtfOrderName();
		String name = jtfOrderName.getText().trim();
		
		if(name.equals("")) {
			showMessageDialog(lodv, "주문자명은 필수 입력");
			jtfOrderName.setText("");
			jtfOrderName.requestFocus();
			return;
		} // end if
		
		JTextField jtfTel = lodv.getJtfPhone();
		String tel = jtfTel.getText().trim();
		
		if(tel.equals("")) {
			showMessageDialog(lodv, "전화번호는 필수 입력");
			jtfTel.setText("");
			jtfTel.requestFocus();
		} // end if
		
		// 전화번호는 3-4자 이하-4이하 총 13자 이하
		String[] arrTel = tel.split("-");
		
		try {
			if(arrTel.length == 3) { // 제대로 된 전화번호
				if( arrTel[0].length() != 3 || !( arrTel[1].length() > 2 && arrTel[1].length() <5) || arrTel[2].length() != 4) {
					showMessageDialog(lodv, "전화번호의 자릿수가 잘못되었습니다.");
				} // end if
				for(int i = 0; i < arrTel.length; i ++) {
					Integer.parseInt(arrTel[i]); // 숫자로만 되어있는지 확인
				} // end for
				
				///////// 주문 수행 /////////
				printOrder();
			} else {
				showMessageDialog(lodv, "전화번호의 형식이 올바르지 않습니다.");
				return;
			} // end else
		} catch(NumberFormatException nfe) {
			showMessageDialog(lodv, "전화번호에 문자열이 들어있습니다.");
			return;
		} // end catch
		
	} // lunchOrder
	
	/**
	 * 주문사항을 보여주고 주문을 할 것인지 처리.
	 */
	private void printOrder() {
		
		JTextArea jtaReceipt = new JTextArea(30, 14);
		jtaReceipt.setEditable(false);
		JScrollPane jspReceipt = new JScrollPane(jtaReceipt);
		StringBuilder data = new StringBuilder();
		try {
			data
			.append("---------------------------------------------------------------\n")
			.append("\t\t재찬 도시락\n")
			.append("\t\t현금(소득공제)\n")
			.append("소래포구점(본점)\n")
			.append("대표 : 이재찬 201-11-11212\n")
			.append("---------------------------------------------------------------\n")
			.append("도시락 명 : ").append(lodv.getJtfLunchName().getText()).append("(").append(lunchCode).append(")\n")
			.append("---------------------------------------------------------------\n")
			.append("수량 : ").append( lodv.getJbQuan().getSelectedItem()).append("개\n")
			.append("---------------------------------------------------------------\n")
			.append("결제 금액 : ").append(lodv.getJtfTotalPrice().getText()).append("원\n")
			.append("---------------------------------------------------------------\n")
			.append("주문자 명 : ").append(lodv.getJtfOrderName().getText()).append("\n")
			.append("---------------------------------------------------------------\n")
			.append("전화번호 : ").append(lodv.getJtfPhone().getText()).append("\n")
			.append("---------------------------------------------------------------\n")
			.append("ip address : ").append(InetAddress.getLocalHost().getHostAddress()).append("\n")
			.append("---------------------------------------------------------------\n")
			.append("요청 사항 : ").append(lodv.getJtaRequest().getText()).append("\n")
			.append("\n")
			.append("\n")
			.append("위의 정보로 도시락을 주문 하시겠습니까?\n")
			.append("\n")
			.append("\n")
			.append("---------------------------------------------------------------\n");
			
			jtaReceipt.setText(data.toString());
			
			switch(JOptionPane.showConfirmDialog(lodv, jtaReceipt)) {
			case JOptionPane.OK_OPTION:
				OrderAddVO oavo = new OrderAddVO(
						lodv.getJtfOrderName().getText(), 
						lodv.getJtfPhone().getText(), 
						InetAddress.getLocalHost().getHostAddress(), 
						lunchCode, 
						lodv.getJbQuan().getSelectedIndex()+1,
						lodv.getJtaRequest().getText()
						);
				try {
					LunchClientDAO.getInstance().insertOrder(oavo);
					showMessageDialog(lodv, "도시락 주문이 완료 되었습니다. \n항상 최선을 다하는 재찬 도시락이 되겠습니다.\n 감사합니다.");
					//주문이 완료되었으므로 주문창을 닫는다.
					lodv.dispose();
				} catch (SQLException e) {
					e.printStackTrace();
				} // end catch
				
				
			} //end switch		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} // end catch
		
			
		
	} // printOrder
	
	@Override
	public void windowClosing(WindowEvent we) {
		lodv.dispose();
	} // windowClosing

	@Override
	public void actionPerformed(ActionEvent ae) {
		
		if(ae.getSource() == lodv.getJbQuan()) { // 수량 선택시 전체 가격을 보여준다.
			
			try {
				int price = Integer.parseInt(lodv.getJtfLunchPrice().getText());
				int quan = ((Integer)lodv.getJbQuan().getSelectedItem()).intValue();
				
				lodv.getJtfTotalPrice().setText(String.valueOf( price * quan ));
				
			} catch(NumberFormatException nfe) {
				showMessageDialog(lodv, "가격은 정수여야 합니다.");
			} // end catch
			
		} // end if
		
		if(ae.getSource() == lodv.getJbEnd()) { // 종료
			lodv.dispose();
		} // end if
		
		if(ae.getSource() == lodv.getJbOrder()) { // 주문
			lunchOrder();
		} // end if
		
	} // actionPerformed
	
	
} // class
