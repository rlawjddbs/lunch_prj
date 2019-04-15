package kr.co.sist.lunch.admin.view;

import java.awt.BorderLayout;
import java.util.Calendar;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import kr.co.sist.lunch.admin.controller.LunchMainController;

@SuppressWarnings("serial")
public class LunchMainView extends JFrame{
	private JTabbedPane jtb;
	private DefaultTableModel dtmLunch, dtmOrder, dtmCalc; // 메뉴, 주문, 정산
	private JButton jbtAddLunch, jbtCalcOrder;
	private JComboBox<Integer> jcbYear, jcbMonth, jcbDay;
	private JTable jtLunch, jtOrder, jtCalc; // 메뉴, 주문, 정산
	
	private Calendar cal;
	
	public static String adminId;
	
	private DefaultComboBoxModel<Integer> cbmYear, cbmMonth, cbmDay;
	
	private JPopupMenu jpOrderMenu;
	private JMenuItem jmOrderRemove, jmOrderStatus;
	
	
	public LunchMainView(String adminName) {
		super("도시락 관리 [ 로그인 계정 : "+adminName+" ]");
		
		cal = Calendar.getInstance();
		
		// 날짜 정보를 구할 수 있는 GregorianCalendar 클래스(Calendar 클래스 상속받음) 인스턴스화
		
		jtb = new JTabbedPane();
		// 도시락
		String[] lunchColumns = {"번호","도시락코드","이미지","도시락명","가격"};
		dtmLunch = new DefaultTableModel(lunchColumns, 4) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			} // innerClass
		};
		
		jtLunch = new JTable( dtmLunch ) {
			@Override
			public Class getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			} // innerClass
		};
		
		// 도시락 테이블 컬럼별 넓이 설정(view 전체 프레임의 넓이 800px, 이미지의 크기 122 x 110px)
		jtLunch.getColumnModel().getColumn(0).setPreferredWidth(40);
		jtLunch.getColumnModel().getColumn(1).setPreferredWidth(120);
		jtLunch.getColumnModel().getColumn(2).setPreferredWidth(125);
		jtLunch.getColumnModel().getColumn(3).setPreferredWidth(265);
		jtLunch.getColumnModel().getColumn(4).setPreferredWidth(220);
		
		// 도시락 테이블 높이 설정(110px)
		jtLunch.setRowHeight(110);
		
		// 정산
		String[] calcColumns = {"번호","도시락명(도시락코드)","수량","가격"};
		dtmCalc = new DefaultTableModel(calcColumns, 4) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			} // isCellEditable
		};
		JTable jtCalc = new JTable( dtmCalc );
		// 정산 테이블 컬럼 넓이 설정 (전체 800px)
		jtCalc.getColumnModel().getColumn(0).setPreferredWidth(100);
		jtCalc.getColumnModel().getColumn(1).setPreferredWidth(400);
		jtCalc.getColumnModel().getColumn(2).setPreferredWidth(150);
		jtCalc.getColumnModel().getColumn(3).setPreferredWidth(150);
//		jtCalc = new JTable( dtmCalc );

		// 정산 테이블 높이 설정
		jtCalc.setRowHeight(25);
		
		
		// 주문
		String[] orderColumns = {"번호","주문번호","도시락코드","도시락명","주문자명",
				"수량", "가격", "주문일", "연락처", "주문자ip", "제작상태"};
		
		dtmOrder = new DefaultTableModel(orderColumns, 4) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			} // isCellEditable
		};
		
		jtOrder = new JTable(dtmOrder);
		
		//주문 테이블 컬럼 넓이 설정 : 전체 800
		jtOrder.getColumnModel().getColumn(0).setPreferredWidth(30);
		jtOrder.getColumnModel().getColumn(1).setPreferredWidth(105);
		jtOrder.getColumnModel().getColumn(2).setPreferredWidth(70);
		jtOrder.getColumnModel().getColumn(3).setPreferredWidth(70);
		jtOrder.getColumnModel().getColumn(4).setPreferredWidth(55);
		jtOrder.getColumnModel().getColumn(5).setPreferredWidth(30);
		jtOrder.getColumnModel().getColumn(6).setPreferredWidth(50);
		jtOrder.getColumnModel().getColumn(7).setPreferredWidth(130);
		jtOrder.getColumnModel().getColumn(8).setPreferredWidth(100);
		jtOrder.getColumnModel().getColumn(9).setPreferredWidth(100);
		jtOrder.getColumnModel().getColumn(10).setPreferredWidth(60);
		//테이블의 높이
		jtOrder.setRowHeight(23);
		
		
		
		jbtAddLunch = new JButton("도시락 추가");
		jbtCalcOrder = new JButton("정산");
		
		cbmYear = new DefaultComboBoxModel<Integer>();
		jcbYear = new JComboBox<Integer>( cbmYear );
		
		cbmMonth = new DefaultComboBoxModel<Integer>();
		jcbMonth = new JComboBox<Integer>( cbmMonth );
		
		cbmDay = new DefaultComboBoxModel<Integer>();
		jcbDay = new JComboBox<Integer>( cbmDay );
		
		
		
		JScrollPane jspLunch = new JScrollPane(jtLunch);
		jspLunch.setBorder(new TitledBorder("도시락 목록"));
		
		JScrollPane jspOrder = new JScrollPane(jtOrder);
		jspOrder.setBorder(new TitledBorder("주문 목록"));
		
		JScrollPane jspCalc = new JScrollPane(jtCalc);
		jspCalc.setBorder(new TitledBorder("정산"));
		
		
		
		// 첫 번째 탭에 들어갈 컴포넌트 배치
		JPanel jpLunch = new JPanel();
		jpLunch.setLayout(new BorderLayout());
		
		JPanel jpLunchNorth= new JPanel();
		jpLunchNorth.add(jbtAddLunch);
		jpLunch.add("Center",jspLunch);
		jpLunch.add("North",jpLunchNorth);
		
		jtb.add("도시락", jpLunch);
		
		
		// 두 번째 탭에 들어갈 컴포넌트 배치
		JPanel jpOrder = new JPanel();
		jpOrder.setLayout(new BorderLayout());
		jpOrder.add(jspOrder);
		jtb.add("주문", jpOrder);
		
		// 세 번째 탭에 들어갈 컴포넌트 배치
		JPanel jpCalc = new JPanel();
		jpCalc.setLayout(new BorderLayout());
		jpCalc.add(jspCalc);
		jtb.add("정산", jpCalc);
		
		JPanel jpCalcNorth = new JPanel();
		jpCalcNorth.setBorder(new TitledBorder("정산일자선택"));
		jpCalcNorth.add(jcbYear);
		jpCalcNorth.add(new JLabel("년"));
		jpCalcNorth.add(jcbMonth);
		jpCalcNorth.add(new JLabel("월"));
		jpCalcNorth.add(jcbDay);
		jpCalcNorth.add(new JLabel("일"));
		jpCalcNorth.add(jbtCalcOrder);
		
		jpCalc.add("North", jpCalcNorth);
		jpCalc.add("Center", jspCalc);
		jtb.addTab("정산", jpCalc);
		
		jpOrderMenu = new JPopupMenu();
		jmOrderStatus = new JMenuItem("제작완료");
		jmOrderRemove = new JMenuItem("주문삭제");

		jpOrderMenu.add(jmOrderRemove);
		jpOrderMenu.addSeparator(); // 구분선 추가
		jpOrderMenu.add(jmOrderStatus);
		
		
		
		add("Center", jtb);
		
		setYear(); // JCB Year 설정 - 현재년도 4년전까지
		setMonth(); // JCB Month 설정 - 월 1 ~ 12월
		setDay(); // JCB Day 설정 - 그 월의 마지막날
		
		// 이벤트 처리
		LunchMainController lmc = new LunchMainController(this);
		addWindowListener(lmc);
		
		// 탭에서 이벤트가 발생했을 때
		jtb.addMouseListener(lmc); 
		jtLunch.addMouseListener(lmc);
		jtOrder.addMouseListener(lmc);
		
		jbtAddLunch.addActionListener(lmc);
		jbtCalcOrder.addActionListener(lmc);
		
		jcbMonth.addActionListener(lmc);
		
		
		jmOrderRemove.addActionListener(lmc);
		jmOrderStatus.addActionListener(lmc);
		
		setBounds(100, 100, 800, 600);
		setVisible(true);
	} // LunchMainView

	
	private void setYear() { // 현재 년도 4년전까지 
		int year = cal.get(Calendar.YEAR);
		for(int temp = 0; temp < 4; temp++) {
			cbmYear.addElement(year-temp);
		} // end for
		
		cbmYear.setSelectedItem(new Integer(year));
	} // setYear
	
	private void setMonth() {
		int now_month = cal.get(Calendar.DAY_OF_MONTH)+1;
		for(int month = 1; month < 13; month++) {
			cbmMonth.addElement(month);
		}//end for
		jcbMonth.setSelectedItem(new Integer(now_month));
	} // setMonth
	
	private void setDay() {
		int lastDay = cal.getActualMaximum(Calendar.DATE);
		int nowDay = cal.get(Calendar.DAY_OF_MONTH);
		for(int day = 1; day < lastDay+1; day++) {
			cbmDay.addElement(day);
		}// end for
		cbmDay.setSelectedItem(new Integer(nowDay));
	} // setDay
	
	
	
	public JTabbedPane getJtb() {
		return jtb;
	} // getJtb

	public DefaultTableModel getDtmLunch() {
		return dtmLunch;
	} // getDtmLunch

	public DefaultTableModel getDtmOrder() {
		return dtmOrder;
	} // getDtmOrder

	public DefaultTableModel getDtmCalc() {
		return dtmCalc;
	} // getDtmCalc

	public JButton getJbtAddLunch() {
		return jbtAddLunch;
	} // getJbtAddLunch

	public JButton getJbtCalcOrder() {
		return jbtCalcOrder;
	} // getJbtCalcOrder

	public JComboBox<Integer> getJcbYear() {
		return jcbYear;
	}

	public JComboBox<Integer> getJcbMonth() {
		return jcbMonth;
	}

	public JComboBox<Integer> getJcbDay() {
		return jcbDay;
	}

	public JTable getJtLunch() {
		return jtLunch;
	}

	public JTable getJtOrder() {
		return jtOrder;
	}

	public JTable getJtCalc() {
		return jtCalc;
	}

	public DefaultComboBoxModel<Integer> getCbmYear() {
		return cbmYear;
	}

	public DefaultComboBoxModel<Integer> getCbmMonth() {
		return cbmMonth;
	}

	public DefaultComboBoxModel<Integer> getCbmDay() {
		return cbmDay;
	}


	public JPopupMenu getJpOrderMenu() {
		return jpOrderMenu;
	}


	public JMenuItem getJmOrderRemove() {
		return jmOrderRemove;
	}


	public JMenuItem getJmOrderStatus() {
		return jmOrderStatus;
	}
	
	
	
}//class
