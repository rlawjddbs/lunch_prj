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
	
	
	// mouseClicked()에서 활용할 더블클릭 횟수를 상수로 선언 및 초기화
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

		if(ae.getSource() == lmv.getJbtAddLunch()) { // 도시락 추가 버튼
			new LunchAddView(lmv, this);
		} // end if 
		
		if(ae.getSource() == lmv.getJcbMonth()) { // 월별 마지막 일자 변경
			setDay();
		} // end if
		
		if(ae.getSource() == lmv.getJbtCalcOrder()) { // 정산 버튼이 클릭되었을 경우
			searchCalc();
		} // end if
		
		if(ae.getSource() == lmv.getJmOrderRemove()) {
			// 제작 상태가 'N'인 상태에서만 동작
			JTable jt = lmv.getJtOrder();
			if( ((String)jt.getValueAt(selectedRow, 10) ).equals("N")) {
				
				switch(JOptionPane.showConfirmDialog(lmv, "["+orderNum+"] ["+lunchName+"] 주문정보를 삭제하시겠습니까?")) {
				case JOptionPane.OK_OPTION:
					
					try {
						
						if(la_dao.deleteOrder(orderNum)) {
							msgCenter(lmv, orderNum+" 주문이 삭제 되었습니다.");
							// 주문 테이블 갱신
							searchOrder();
						} else {
							msgCenter(lmv, orderNum+" 주문이 삭제되지 않았습니다.");
						} // end else
						
					} catch (SQLException e) {
						msgCenter(lmv, "DB에서 문제가 발생했습니다.");
						e.printStackTrace();
					} // end catch
					
				} // end switch
				
			} else {
				msgCenter(lmv, "제작완료된 도시락은 삭제할 수 없습니다.");
			} // end else
			
			JPopupMenu jp = lmv.getJpOrderMenu();
			jp.setVisible(false); // 팝업 메뉴 숨김
			
		} // end if
		
		// 액션 이벤트가 발생한 곳의 주소가 JmOrderStatus 일 때 이벤트처리
		if(ae.getSource() == lmv.getJmOrderStatus()) { 
			DefaultTableModel dtm = lmv.getDtmOrder();
			
			// 제작 상태가 'N'인 상태에서만 동작
			JTable jt = lmv.getJtOrder();
			if(((String)jt.getValueAt(selectedRow, 10)).equals("N")) {
				
//				dtm.get
				switch(JOptionPane.showConfirmDialog(lmv, "["+orderNum+lunchName+"]님의 도시락이 완성 되었습니까?")) {
				case JOptionPane.OK_OPTION:
					// DB Table의 해당 레코드 변경
					try {
						if(la_dao.updateStatus(orderNum)) { 
							jt.setValueAt("Y", selectedRow, 10); // 테이블의 값만 변경 (DB는 반영되지 않은 상태)
							JOptionPane.showMessageDialog(lmv, "도시락 제작이 완료되었습니다.");
						} else { // 상태변환 실패
							JOptionPane.showMessageDialog(lmv, "도시락 제작상태 변환이 실패했습니다.");
						} // end else
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(lmv, "DB에서 문제가 발생했습니다.");
						e.printStackTrace();
					} // end catch
				} // end switch
			} else {
				JOptionPane.showMessageDialog(lmv, "이미 제작이 완료된 도시락 입니다.");
			} // end else
			
			JPopupMenu jp = lmv.getJpOrderMenu();
			jp.setVisible(false); // 팝업 메뉴 숨김
			
		} // end if	
		
	} // actionPerformed

	/**
	 * 년, 월, 일 정보를 가져와서 정산
	 */
	private void searchCalc() {
		int selYear = ((Integer)lmv.getJcbYear().getSelectedItem()).intValue();
		int selMonth = ((Integer)lmv.getJcbMonth().getSelectedItem()).intValue();
		int selDay = ((Integer)lmv.getJcbDay().getSelectedItem()).intValue();
		
		StringBuilder searchDate = new StringBuilder();
		searchDate.append(selYear).append("-").append(selMonth).append("-").append(selDay);
		
		
		try {
			// 선택한 일자의 조회 결과를 받아서 JTable로 출력
			List<CalcVO> list = la_dao.selectCalc( searchDate.toString() );
			// JTable에 데이터를 추가하는 코드 작성
			

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
			
			// 데이터가 없는 날에는 " 판매된 도시락이 없습니다" 출력
			if(list.isEmpty()) {
				JOptionPane.showMessageDialog(lmv, searchDate.toString() + "일에는 판매된 도시락이 없습니다.");
			} // end if
			
		} catch (SQLException e) {
			e.printStackTrace();
		} // end catch
		
	} // searchCalc
	
	
	/**
	 * 	월이 선택되면 해당년의 해당월의 마지막날을 설정
	 */
	private void setDay() {
		int selYear = ((Integer)lmv.getJcbYear().getSelectedItem()).intValue();
		int selMonth = ((Integer)lmv.getJcbMonth().getSelectedItem()).intValue();
		
		// 마지막 날 얻기
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, selYear);
		cal.set(Calendar.MONTH, selMonth-1);
		
		int lastDay = cal.getActualMaximum(Calendar.DATE);
		int nowDay = cal.get(Calendar.DAY_OF_MONTH);
		
		lmv.getCbmDay().removeAllElements(); // 모델을 초기화 하고
		for(int day = 1; day < lastDay+1; day++) {
			lmv.getCbmDay().addElement(day); // 마지막 날을 설정한다.
		}// end for
		
//		lmv.getCbmDay().setSelectedItem(new Integer(nowDay)); // 오늘을 선택한다.
		
		
	} // setDay
	
	
	/**
	 * JTable에 DB에서 조회한 도시락 정보를 보여준다. 
	 */
	public void setLunch() { 
		DefaultTableModel dtmLunch = lmv.getDtmLunch();
		dtmLunch.setRowCount(0); // 테이블을 생성하면서 설정한 행의 개수(4개)를 0개로 만든다.
		
		try {
			// DB에서 도시락 정보를 조회
			List<LunchVO> listLunch = la_dao.selectLunch();
			// JTable에 조회한 정보를 출력.
			
			LunchVO lv = null;
			// 이미지 경로 끝에 s_ 를 붙여 작은 썸네일용 이미지가 지정되도록 설정
			String imgPath = "C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/admin/img/s_";
			
			Object[] rowData = null;
			
			for(int i = 0; i < listLunch.size(); i++) {
				lv = listLunch.get(i);
				
				// DTM에 데이터를 추가하기 위한 일차원 배열(Vector)을 생성
				rowData = new Object[5];
				rowData[0] = new Integer(i + 1); // rowData의 타입은 Object 이므로 그 안에 들어갈 값또한 Integer 타입의 Object 타입으로 설정
				rowData[1] = lv.getLunchCode();
				rowData[2] = new ImageIcon(imgPath + lv.getImg()); ////////// 중요 /////////
				rowData[3] = lv.getLunchName();
				rowData[4] = new Integer( lv.getPrice() );
				
				// DTM에 추가
				dtmLunch.addRow( rowData );
				
			} // end for
			
			// 입력된 도시락이 없을 때
			if ( listLunch.isEmpty() ) {
				JOptionPane.showMessageDialog(lmv, "입력된 제품이 없습니다.");
			} // end if
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(lmv, "DB에서 데이터를 받아오는 중 문제가 발생했습니다.");
			e.printStackTrace();
		} // end catch
		
	} // setLunch
	
	@Override
	public void windowClosing(WindowEvent we) {
		lmv.dispose();
	} // windowClosing
	@Override
	public void windowClosed(WindowEvent we) {
		System.exit(0); // JVM의 모든 인스턴스를 종료한다.
	} // windowClosed
	
	
	private void searchOrder() {
		try {
			List<OrderVO> list = la_dao.selectOrderList();
			DefaultTableModel dtm = lmv.getDtmOrder();
			dtm.setRowCount(0); // 초기화
			
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
				JOptionPane.showMessageDialog(lmv, jspTempRequest, "주문번호 [ "+orderNum+" ] "+"[ "+orderName+" ]님의 요청사항", JOptionPane.DEFAULT_OPTION);
				map.get(orderNum).setFlag(false);
			} else {
				msgCenter(lmv, "요청 사항 없음");
			} // end else
//			System.out.println(selectRequest(orderNum));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	} // checkRequest
	
	

	@Override
	public void mouseClicked(MouseEvent me) {
		if(me.getSource() == lmv.getJtb()) {
			if( lmv.getJtb().getSelectedIndex() == 1) { // 주문 탭(두번째 탭 : 주문탭)을 클릭하였을 때
				
				// 실시간으로 DB를 조회하여 주문현황을 계속 갱신
				if(threadOrdering == null) { // 본 조건문이 없을 경우 쓰레드가 계속 새로이 생성되어 돌게되므로 한번만 객체화 될 수 있도록 한다.
					threadOrdering = new Thread(this);
					threadOrdering.start();
				} // end if
				// 현재까지의 주문사항을 조회
				
				
			} // end if
		} // end if
		// 첫번째 탭에서 이벤트가 발생하였는지?
		
		// 마우스 우클릭 이벤트
		// 마우스 우클릭이 발생한 곳의 주소가 JtOrder 이면서 마우스 우클릭이어야 한다.
		if(me.getSource() == lmv.getJtOrder() && me.getButton() == MouseEvent.BUTTON3) {
//			System.out.println("아오 우클뤽"+me.getX()+" / "+me.getY()); // 우클릭이 일어난 지점의 좌표를 얻을 수 있다.
			JTable jt = lmv.getJtOrder();
			
			// 마우스 포인터가 클릭되면 테이블에서 클릭한 행을 가져오는 일
			int r = jt.rowAtPoint(me.getPoint()); // r은 선택한 행을 의미
	        if (r >= 0 && r < jt.getRowCount()) {
	        	jt.setRowSelectionInterval(r, r); // 시작행과 끝행 사이의 행을 선택하는 일
	        } else {
	        	jt.clearSelection();
	        } // end else
			
	        if(map.get(jt.getValueAt(jt.getSelectedRow(), 1)).getFlag()==true) {
	        	msgCenter(lmv, "해당 주문은 요청사항이 있습니다. 먼저 해당주문을 더블클릭 후 요청사항을 확인 후 진행해주세요.");
	        	return;
	        } // end if
	        // 선택한 행(r)을 selectedRow 변수에 담는다.
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
			// 더블클릭(이벤트)이 발생한 곳이 도시락탭 내부인지?
			if ( me.getSource() == lmv.getJtLunch() ) {
				
				// 도시락 코드로 DB Table을 검색하여 상세정보를 전달한다.
				JTable jt = lmv.getJtLunch();
				
				try {
					LunchDetailVO ldvo = 
							la_dao.selectDetailLunch((String)jt.getValueAt(jt.getSelectedRow(), 1));
					new LunchDetailView( lmv, ldvo, this );
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(lmv, "DB작업 중 문제가 발생했습니다.");
					e.printStackTrace();
				} // end catch
				
				
			} // end if
			
		} // end switch
		
	} // mouseClicked

	@Override
	public void run() {
		// 30초마다 한번씩 조회 수행
			try {
				while(true) {
					searchOrder();
					Thread.sleep(1000*30);
				} // end while
			} catch (InterruptedException e) {
				msgCenter(lmv, "주문 조회 중 문제가 발생했습니다.");
				e.printStackTrace();
			} // end catch
	} // run
	
	
	///////////////////////////////// 이하 사용하지 않는 구현 메소드 ///////////////////////////////////
	@Override
	public void mousePressed(MouseEvent e) {} 
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	///////////////////////////////// 이상 사용하지 않는 구현 메소드 ///////////////////////////////////

	
} // class
