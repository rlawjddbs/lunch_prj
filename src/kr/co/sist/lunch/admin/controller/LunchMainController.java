package kr.co.sist.lunch.admin.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import kr.co.sist.lunch.admin.model.LunchAdminDAO;
import kr.co.sist.lunch.admin.view.LunchAddView;
import kr.co.sist.lunch.admin.view.LunchDetailView;
import kr.co.sist.lunch.admin.view.LunchMainView;
import kr.co.sist.lunch.admin.vo.CalcVO;
import kr.co.sist.lunch.admin.vo.FlagVO;
import kr.co.sist.lunch.admin.vo.LunchDetailVO;
import kr.co.sist.lunch.admin.vo.LunchVO;
import kr.co.sist.lunch.admin.vo.OrderVO;

public class LunchMainController extends WindowAdapter implements ActionListener, MouseListener, Runnable {

	private LunchMainView lmv; 
	private LunchAdminDAO la_dao;
	
	private String orderNum;
	private String lunchName;
	private int selectedRow;
	
	private Thread threadOrdering;
	private Map<String, FlagVO> map;
	
	
	// mouseClicked()���� Ȱ���� ����Ŭ�� Ƚ���� ����� ���� �� �ʱ�ȭ
	public static final int DBL_CLICK = 2;

	public LunchMainController(LunchMainView lmv) {
		this.lmv = lmv;
		la_dao = LunchAdminDAO.getInstance();
		setLunch();
		orderNum = "";
		map = new HashMap<String, FlagVO>();
	} // LunchMainController
	
	private void msgCenter(Component parentComponent, String message) {
		JOptionPane.showMessageDialog(parentComponent, message);
	} // msgCenter
	
	@Override
	public void actionPerformed(ActionEvent ae) {

		if(ae.getSource() == lmv.getJbtAddLunch()) { // ���ö� �߰� ��ư
			new LunchAddView(lmv, this);
		} // end if 
		
		if(ae.getSource() == lmv.getJcbMonth()) { // ���� ������ ���� ����
			setDay();
		} // end if
		
		if(ae.getSource() == lmv.getJbtCalcOrder()) { // ���� ��ư�� Ŭ���Ǿ��� ���
			searchCalc();
		} // end if
		
		if(ae.getSource() == lmv.getJmOrderRemove()) {
			// ���� ���°� 'N'�� ���¿����� ����
			JTable jt = lmv.getJtOrder();
			if( ((String)jt.getValueAt(selectedRow, 10) ).equals("N")) {
				
				switch(JOptionPane.showConfirmDialog(lmv, "["+orderNum+"] ["+lunchName+"] �ֹ������� �����Ͻðڽ��ϱ�?")) {
				case JOptionPane.OK_OPTION:
					
					try {
						
						if(la_dao.deleteOrder(orderNum)) {
							msgCenter(lmv, orderNum+" �ֹ��� ���� �Ǿ����ϴ�.");
							// �ֹ� ���̺� ����
							searchOrder();
						} else {
							msgCenter(lmv, orderNum+" �ֹ��� �������� �ʾҽ��ϴ�.");
						} // end else
						
					} catch (SQLException e) {
						msgCenter(lmv, "DB���� ������ �߻��߽��ϴ�.");
						e.printStackTrace();
					} // end catch
					
				} // end switch
				
			} else {
				msgCenter(lmv, "���ۿϷ�� ���ö��� ������ �� �����ϴ�.");
			} // end else
			
			JPopupMenu jp = lmv.getJpOrderMenu();
			jp.setVisible(false); // �˾� �޴� ����
			
		} // end if
		
		// �׼� �̺�Ʈ�� �߻��� ���� �ּҰ� JmOrderStatus �� �� �̺�Ʈó��
		if(ae.getSource() == lmv.getJmOrderStatus()) { 
			DefaultTableModel dtm = lmv.getDtmOrder();
			
			// ���� ���°� 'N'�� ���¿����� ����
			JTable jt = lmv.getJtOrder();
			if(((String)jt.getValueAt(selectedRow, 10)).equals("N")) {
				
//				dtm.get
				switch(JOptionPane.showConfirmDialog(lmv, "["+orderNum+lunchName+"]���� ���ö��� �ϼ� �Ǿ����ϱ�?")) {
				case JOptionPane.OK_OPTION:
					// DB Table�� �ش� ���ڵ� ����
					try {
						if(la_dao.updateStatus(orderNum)) { 
							jt.setValueAt("Y", selectedRow, 10); // ���̺��� ���� ���� (DB�� �ݿ����� ���� ����)
							JOptionPane.showMessageDialog(lmv, "���ö� ������ �Ϸ�Ǿ����ϴ�.");
						} else { // ���º�ȯ ����
							JOptionPane.showMessageDialog(lmv, "���ö� ���ۻ��� ��ȯ�� �����߽��ϴ�.");
						} // end else
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(lmv, "DB���� ������ �߻��߽��ϴ�.");
						e.printStackTrace();
					} // end catch
				} // end switch
			} else {
				JOptionPane.showMessageDialog(lmv, "�̹� ������ �Ϸ�� ���ö� �Դϴ�.");
			} // end else
			
			JPopupMenu jp = lmv.getJpOrderMenu();
			jp.setVisible(false); // �˾� �޴� ����
			
		} // end if	
		
	} // actionPerformed

	/**
	 * ��, ��, �� ������ �����ͼ� ����
	 */
	private void searchCalc() {
		int selYear = ((Integer)lmv.getJcbYear().getSelectedItem()).intValue();
		int selMonth = ((Integer)lmv.getJcbMonth().getSelectedItem()).intValue();
		int selDay = ((Integer)lmv.getJcbDay().getSelectedItem()).intValue();
		
		StringBuilder searchDate = new StringBuilder();
		searchDate.append(selYear).append("-").append(selMonth).append("-").append(selDay);
		
		
		try {
			// ������ ������ ��ȸ ����� �޾Ƽ� JTable�� ���
			List<CalcVO> list = la_dao.selectCalc( searchDate.toString() );
			// JTable�� �����͸� �߰��ϴ� �ڵ� �ۼ�
			

			Object[] rowData = null;
			CalcVO cvo = null;
			
			lmv.getDtmCalc().setRowCount(0);
			for( int i = 0; i < list.size(); i++ ) {
				cvo = list.get(i);
				
				rowData = new Object[4];
				
				rowData[0] = new Integer(i + 1);
				rowData[1] = cvo.getLunchName() + " ("+cvo.getLunchCode() + ") ";
				rowData[2] = new Integer(cvo.getTotal());
				rowData[3] = new Integer(cvo.getPrice());
				
				
				lmv.getDtmCalc().addRow(rowData);
			} // end for
			
			// �����Ͱ� ���� ������ " �Ǹŵ� ���ö��� �����ϴ�" ���
			if(list.isEmpty()) {
				JOptionPane.showMessageDialog(lmv, searchDate.toString() + "�Ͽ��� �Ǹŵ� ���ö��� �����ϴ�.");
			} // end if
			
		} catch (SQLException e) {
			e.printStackTrace();
		} // end catch
		
	} // searchCalc
	
	
	/**
	 * 	���� ���õǸ� �ش���� �ش���� ���������� ����
	 */
	private void setDay() {
		int selYear = ((Integer)lmv.getJcbYear().getSelectedItem()).intValue();
		int selMonth = ((Integer)lmv.getJcbMonth().getSelectedItem()).intValue();
		
		// ������ �� ���
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, selYear);
		cal.set(Calendar.MONTH, selMonth-1);
		
		int lastDay = cal.getActualMaximum(Calendar.DATE);
		int nowDay = cal.get(Calendar.DAY_OF_MONTH);
		
		lmv.getCbmDay().removeAllElements(); // ���� �ʱ�ȭ �ϰ�
		for(int day = 1; day < lastDay+1; day++) {
			lmv.getCbmDay().addElement(day); // ������ ���� �����Ѵ�.
		}// end for
		
//		lmv.getCbmDay().setSelectedItem(new Integer(nowDay)); // ������ �����Ѵ�.
		
		
	} // setDay
	
	
	/**
	 * JTable�� DB���� ��ȸ�� ���ö� ������ �����ش�. 
	 */
	public void setLunch() { 
		DefaultTableModel dtmLunch = lmv.getDtmLunch();
		dtmLunch.setRowCount(0); // ���̺��� �����ϸ鼭 ������ ���� ����(4��)�� 0���� �����.
		
		try {
			// DB���� ���ö� ������ ��ȸ
			List<LunchVO> listLunch = la_dao.selectLunch();
			// JTable�� ��ȸ�� ������ ���.
			
			LunchVO lv = null;
			// �̹��� ��� ���� s_ �� �ٿ� ���� ����Ͽ� �̹����� �����ǵ��� ����
			String imgPath = "C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/admin/img/s_";
			
			Object[] rowData = null;
			
			for(int i = 0; i < listLunch.size(); i++) {
				lv = listLunch.get(i);
				
				// DTM�� �����͸� �߰��ϱ� ���� ������ �迭(Vector)�� ����
				rowData = new Object[5];
				rowData[0] = new Integer(i + 1); // rowData�� Ÿ���� Object �̹Ƿ� �� �ȿ� �� ������ Integer Ÿ���� Object Ÿ������ ����
				rowData[1] = lv.getLunchCode();
				rowData[2] = new ImageIcon(imgPath + lv.getImg()); ////////// �߿� /////////
				rowData[3] = lv.getLunchName();
				rowData[4] = new Integer( lv.getPrice() );
				
				// DTM�� �߰�
				dtmLunch.addRow( rowData );
				
			} // end for
			
			// �Էµ� ���ö��� ���� ��
			if ( listLunch.isEmpty() ) {
				JOptionPane.showMessageDialog(lmv, "�Էµ� ��ǰ�� �����ϴ�.");
			} // end if
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(lmv, "DB���� �����͸� �޾ƿ��� �� ������ �߻��߽��ϴ�.");
			e.printStackTrace();
		} // end catch
		
	} // setLunch
	
	@Override
	public void windowClosing(WindowEvent we) {
		lmv.dispose();
	} // windowClosing
	@Override
	public void windowClosed(WindowEvent we) {
		System.exit(0); // JVM�� ��� �ν��Ͻ��� �����Ѵ�.
	} // windowClosed
	
	
	private void searchOrder() {
		try {
			List<OrderVO> list = la_dao.selectOrderList();
			DefaultTableModel dtm = lmv.getDtmOrder();
			dtm.setRowCount(0); // �ʱ�ȭ
			
			Vector<Object> vec = new Vector<Object>();
			
			OrderVO ovo = null;
			FlagVO fvo = null;
			for(int i = 0; i < list.size(); i++) {
				ovo = list.get(i);
				vec = new Vector<Object>();
				
				vec.add(new Integer(i + 1));
				vec.add(ovo.getOrderNum());
				vec.add(ovo.getLunchCode());
				vec.add(ovo.getLunchName());
				vec.add(ovo.getOrderName());
				vec.add(ovo.getQuan());
				vec.add(ovo.getPrice());
				vec.add(ovo.getOrderDate());
				vec.add(ovo.getPhone());
				vec.add(ovo.getIpAddress());
				vec.add(ovo.getStatus());
//				vec.add(ovo.getRequest());
				
				fvo = new FlagVO(ovo.getOrderNum(), ovo.getRequest()!=null?true:false);
				map.put(ovo.getOrderNum(), fvo);
				
//				if(ovo.getRequest()!=null) {
//				} else {
//					fvo = new FlagVO(ovo.getOrderNum(), false);
//				} // end else
				
				dtm.addRow( vec );
				
			} // end for
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} // searchOrder
	
	
	private void checkRequest() {
		JTable jt = lmv.getJtOrder();
		JTextArea jtaTempRequest = new JTextArea(8, 30);
		jtaTempRequest.setEditable(false);
		JScrollPane jspTempRequest = new JScrollPane(jtaTempRequest);
		orderNum = (String)jt.getValueAt(jt.getSelectedRow(), 1);
		
		String orderName = (String)jt.getValueAt(jt.getSelectedRow(), 4);
		try {
			StringBuilder requestSb = new StringBuilder(LunchAdminDAO.getInstance().selectRequest(orderNum));
			
			if( !requestSb.toString().equals("") ) {
				jtaTempRequest.append(requestSb.toString());
				JOptionPane.showMessageDialog(lmv, jspTempRequest, "�ֹ���ȣ [ "+orderNum+" ] "+"[ "+orderName+" ]���� ��û����", JOptionPane.DEFAULT_OPTION);
				map.get(orderNum).setFlag(false);
			} else {
				msgCenter(lmv, "��û ���� ����");
			} // end else
//			System.out.println(selectRequest(orderNum));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	} // checkRequest
	
	

	@Override
	public void mouseClicked(MouseEvent me) {
		if(me.getSource() == lmv.getJtb()) {
			if( lmv.getJtb().getSelectedIndex() == 1) { // �ֹ� ��(�ι�° �� : �ֹ���)�� Ŭ���Ͽ��� ��
				
				// �ǽð����� DB�� ��ȸ�Ͽ� �ֹ���Ȳ�� ��� ����
				if(threadOrdering == null) { // �� ���ǹ��� ���� ��� �����尡 ��� ������ �����Ǿ� ���ԵǹǷ� �ѹ��� ��üȭ �� �� �ֵ��� �Ѵ�.
					threadOrdering = new Thread(this);
					threadOrdering.start();
				} // end if
				// ��������� �ֹ������� ��ȸ
				
				
			} // end if
		} // end if
		// ù��° �ǿ��� �̺�Ʈ�� �߻��Ͽ�����?
		
		// ���콺 ��Ŭ�� �̺�Ʈ
		// ���콺 ��Ŭ���� �߻��� ���� �ּҰ� JtOrder �̸鼭 ���콺 ��Ŭ���̾�� �Ѵ�.
		if(me.getSource() == lmv.getJtOrder() && me.getButton() == MouseEvent.BUTTON3) {
//			System.out.println("�ƿ� ��Ŭ��"+me.getX()+" / "+me.getY()); // ��Ŭ���� �Ͼ ������ ��ǥ�� ���� �� �ִ�.
			JTable jt = lmv.getJtOrder();
			
			// ���콺 �����Ͱ� Ŭ���Ǹ� ���̺��� Ŭ���� ���� �������� ��
			int r = jt.rowAtPoint(me.getPoint()); // r�� ������ ���� �ǹ�
	        if (r >= 0 && r < jt.getRowCount()) {
	        	jt.setRowSelectionInterval(r, r); // ������� ���� ������ ���� �����ϴ� ��
	        } else {
	        	jt.clearSelection();
	        } // end else
			
	        if(map.get(jt.getValueAt(jt.getSelectedRow(), 1)).getFlag()==true) {
	        	msgCenter(lmv, "�ش� �ֹ��� ��û������ �ֽ��ϴ�. ���� �ش��ֹ��� ����Ŭ�� �� ��û������ Ȯ�� �� �������ּ���.");
	        	return;
	        } // end if
	        // ������ ��(r)�� selectedRow ������ ��´�.
	        selectedRow = r;
	        
			JPopupMenu jp = lmv.getJpOrderMenu();
			jp.setLocation(me.getXOnScreen(), me.getYOnScreen());
			jp.setVisible(true);
			
			orderNum = (String)jt.getValueAt(jt.getSelectedRow(), 1);
			lunchName = (String)jt.getValueAt(jt.getSelectedRow(), 3)+" "+(String)jt.getValueAt(jt.getSelectedRow(), 4);
			
		} else {
			JPopupMenu jp = lmv.getJpOrderMenu();
			jp.setVisible(false);
		} // end else
		
		if(me.getSource() == lmv.getJtOrder() && me.getClickCount() == 2) {
			checkRequest();
		} // end if
		
		
		switch( me.getClickCount() ) {
		case DBL_CLICK :
			// ����Ŭ��(�̺�Ʈ)�� �߻��� ���� ���ö��� ��������?
			if ( me.getSource() == lmv.getJtLunch() ) {
				
				// ���ö� �ڵ�� DB Table�� �˻��Ͽ� �������� �����Ѵ�.
				JTable jt = lmv.getJtLunch();
				
				try {
					LunchDetailVO ldvo = 
							la_dao.selectDetailLunch((String)jt.getValueAt(jt.getSelectedRow(), 1));
					new LunchDetailView( lmv, ldvo, this );
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(lmv, "DB�۾� �� ������ �߻��߽��ϴ�.");
					e.printStackTrace();
				} // end catch
				
				
			} // end if
			
		} // end switch
		
	} // mouseClicked

	@Override
	public void run() {
		// 30�ʸ��� �ѹ��� ��ȸ ����
			try {
				while(true) {
					searchOrder();
					Thread.sleep(1000*30);
				} // end while
			} catch (InterruptedException e) {
				msgCenter(lmv, "�ֹ� ��ȸ �� ������ �߻��߽��ϴ�.");
				e.printStackTrace();
			} // end catch
	} // run
	
	
	///////////////////////////////// ���� ������� �ʴ� ���� �޼ҵ� ///////////////////////////////////
	@Override
	public void mousePressed(MouseEvent e) {} 
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	///////////////////////////////// �̻� ������� �ʴ� ���� �޼ҵ� ///////////////////////////////////

	
} // class
