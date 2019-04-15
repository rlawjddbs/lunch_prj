package kr.co.sist.lunch.user.view;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import kr.co.sist.lunch.user.controller.LunchClientController;

@SuppressWarnings("serial")
public class LunchClientView extends JFrame {
	private JTabbedPane jtp;
	private DefaultTableModel dtmLunchList, dtmOrderList;
	private JTextField jtfName, jtfTel;
	private JButton jbtSearch;
	JTable jtLunch, jtOrder;
	
	public LunchClientView(){
		super("쌍용 도시락 주문");
		jtp = new JTabbedPane();
		
		// 도시락 테이블
		String[] lunchColumn = {"이름","이미지", "도시락 코드", "도시락명", "설명"};
		dtmLunchList = new DefaultTableModel(lunchColumn, 4) {
			@Override
			// 셀 편집 불가 설정
			public boolean isCellEditable(int row, int column) {
				return false;
			} // isCellEditable
		};
		jtLunch = new JTable(dtmLunchList) {
			@Override
			public Class<?> getColumnClass(int column) {
				return getValueAt(0, column).getClass();
			} // getColumnClass
		};

		jtLunch.getColumnModel().getColumn(0).setPreferredWidth(30);
		jtLunch.getColumnModel().getColumn(1).setPreferredWidth(180);
		jtLunch.getColumnModel().getColumn(2).setPreferredWidth(100);
		jtLunch.getColumnModel().getColumn(3).setPreferredWidth(120);
		jtLunch.getColumnModel().getColumn(4).setPreferredWidth(370);
		
		// 테이블 행 높이
		jtLunch.setRowHeight(110);
		
		// 주문현황
		String[] orderColumns = {"번호", "도시락명", "주문일자", "수량"};
		dtmOrderList = new DefaultTableModel(orderColumns, 4) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			} // isCellEditable
		};
		
		
		jtOrder = new JTable( dtmOrderList );
		
		jtOrder.getColumnModel().getColumn(0).setPreferredWidth(100);
		jtOrder.getColumnModel().getColumn(1).setPreferredWidth(250);
		jtOrder.getColumnModel().getColumn(2).setPreferredWidth(350);
		jtOrder.getColumnModel().getColumn(3).setPreferredWidth(100);
		
		jtOrder.setRowHeight(25);
		
		jtfName = new JTextField(10);
		jtfTel = new JTextField(10);
		
		jbtSearch = new JButton("조회");
		
		JScrollPane jspLunch = new JScrollPane(jtLunch);
		jspLunch.setBorder(new TitledBorder("도시락 목록"));
		
		jtp.add("도시락 목록", jspLunch);
		
		JPanel panelOrder = new JPanel();
		panelOrder.setLayout(new BorderLayout());
		
		JPanel panelOrderNorth = new JPanel();
		panelOrderNorth.setBorder(new TitledBorder("주문자 정보"));
		panelOrderNorth.add(new JLabel("주문자명"));
		panelOrderNorth.add(jtfName);
		panelOrderNorth.add(new JLabel("전화번호"));
		panelOrderNorth.add(jtfTel);
		panelOrderNorth.add(jbtSearch);

		JScrollPane jspOrder = new JScrollPane(jtOrder);
		jspOrder.setBorder(new TitledBorder("주문현황"));
		
		panelOrder.add("North", panelOrderNorth);
		panelOrder.add(jspOrder);
		
		jtp.addTab("주문목록조회", panelOrder);
		
		// 이벤트 등록을 위한 LunchClientController 객체 생성
		LunchClientController lcc = new LunchClientController(this);
		addWindowListener(lcc);
		
		jtLunch.addMouseListener(lcc);
		jbtSearch.addActionListener(lcc);
		
		jtfName.addActionListener(lcc);
		jtfTel.addActionListener(lcc);
		
		jtp.addMouseListener(lcc);
		
		add("Center", jtp);
		setBounds(100, 100, 800, 800);
		setVisible(true);
		
		
	} // LunchClientView

	public JTabbedPane getJtp() {
		return jtp;
	}

	public DefaultTableModel getDtmLunchList() {
		return dtmLunchList;
	}

	public DefaultTableModel getDtmOrderList() {
		return dtmOrderList;
	}

	public JTextField getJtfName() {
		return jtfName;
	}

	public JTextField getJtfTel() {
		return jtfTel;
	}

	public JButton getJbtSearch() {
		return jbtSearch;
	}

	public JTable getJtLunch() {
		return jtLunch;
	}

	public JTable getJtOrder() {
		return jtOrder;
	}
	
	
} // class
