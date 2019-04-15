package kr.co.sist.lunch.user.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import kr.co.sist.lunch.admin.controller.LunchLoginController;
import kr.co.sist.lunch.user.model.LunchClientDAO;
import kr.co.sist.lunch.user.view.LunchClientView;
import kr.co.sist.lunch.user.view.LunchOrderDetailView;
import kr.co.sist.lunch.user.vo.LunchDetailVO;
import kr.co.sist.lunch.user.vo.LunchListVO;
import kr.co.sist.lunch.user.vo.OrderInfoVO;
import kr.co.sist.lunch.user.vo.OrderListVO;

public class LunchClientController extends WindowAdapter implements ActionListener, MouseListener{

	private LunchClientView lcv;
	private LunchClientDAO lc_dao;
	public static final int DBL_CLICK = 2;
	
	
	
	public LunchClientController(LunchClientView lcv) {
		this.lcv = lcv;
		lc_dao = LunchClientDAO.getInstance();
		
		try {
			String[] fileNames = lunchImageList(); // 클라이언트가 가진 이미지를 체크하여
			lunchImageSend(fileNames); // 서버로 보내 없는 이미지를 받은 후
		} catch (IOException e) {
			e.printStackTrace();
		} // end catch
		
		setLunchList(); // JTable을 갱신한다.
	} // LunchClientController

	
	
	/**
	 * 도시락 목록을 조회하여 JTable에 설정하는 일.
	 * 특장점이 25자 이상이라면 24자 까지만 출력하고 나머지는 ... 으로 붙여서 설정한다. 
	 */
	private void setLunchList() {
		// 도시락 목록 조회
		try {
			List<LunchListVO> list = lc_dao.selectLunchList();
			
			DefaultTableModel dtm = lcv.getDtmLunchList();
			dtm.setRowCount(0);
			
			Object[] rowData = null;
			
			LunchListVO llvo = null;
			String spec = "";
			
			// 번호(count)가 없다면 개선 for 문을, 있다면 기존 for문을 이용하면 좋다.
			for(int i = 0; i < list.size(); i++) {
				llvo = list.get(i);
				
				// 테이블에 추가할 데이터를 배열로 생성
				rowData = new Object[5];
				rowData[0] = new Integer(i + 1); // 번호
				rowData[1] = new ImageIcon("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/user/img/s_"+llvo.getImg()); // 이미지
				rowData[2] = llvo.getLunchCode(); // 도시락 코드
				rowData[3] = llvo.getLunchName(); // 도시락 이름
				spec = llvo.getLunchSpec();
				
				if( spec.length() > 25 ) {
					spec = spec.substring(0, 24)+"...";
				} // end if

				rowData[4] = spec; // 도시락의 특장점
				
				// 생성된 데이터를 테이블에 추가
				dtm.addRow(rowData);
				
			} // end for 
			
			
			
		} catch (SQLException e) {
			msgCenter(lcv, "도시락 목록을 조회하는 중 DB에서 문제가 발생했습니다.");
			e.printStackTrace();
		} // end catch
	} // setLunchList
	
	private void msgCenter(Component comp, String msg) {
		JOptionPane.showMessageDialog(comp, msg);
	} // msgCenter
	
	
	@Override
	public void windowClosing(WindowEvent we) {
		lcv.dispose();
	} // windowClosing
	
	@Override
	public void windowClosed(WindowEvent we) {
		System.exit( JFrame.ABORT );
	} // windowClosed
	
	
	@Override
	public void mouseClicked(MouseEvent me) {
		
		if(me.getSource() == lcv.getJtp()) {
			
			// 클라이언트 view 에서 도시락 목록 탭을 클릭하면 Frame 의 Title이 바뀐다.
			if(lcv.getJtp().getSelectedIndex() == 0) {
				lcv.setTitle("재찬 도시락 주문");
			} // end if
			// 클라이언트 view 에서 주문 목록 조회 탭을 클릭하면 Frame 의 Title이 바뀐다.
			if(lcv.getJtp().getSelectedIndex() == 1) {
				lcv.setTitle("재찬 도시락 주문 조회");
			} // end if
			
		} // end if
		
		if( me.getSource() == lcv.getJtLunch()) { // 이벤트가 발생된 주소가 JTable 내부일 경우에만 실행
			switch(me.getClickCount()) {
			case DBL_CLICK:
				JTable jt = lcv.getJtLunch();
				String lunch_code = (String)jt.getValueAt(jt.getSelectedRow(), 2);
				// 도시락의 상세 정보 조회
				try {
					LunchDetailVO ldvo = lc_dao.selectDetailLunch( lunch_code );
					if ( ldvo == null ) { // 코드로 조회된 결과가 없을 때
						msgCenter(lcv, "[ "+lunch_code+" ]로 조회되는 도시락이 없습니다.");
					} else { // 코드로 조회된 결과가 있을 때
						new LunchOrderDetailView(lcv, ldvo);
					} // end else
				} catch (SQLException se) {
					msgCenter(lcv, "상세정보 조회 중 문제가 발생했습니다.");
					se.printStackTrace();
				} // end catch
			} //end switch
		} // end if
	} // mouseClicked
	
	/**
	 * 주문자명과 전화번호를 가지고 도시락 조회
	 */
	private void searchLunchOrder() {
		JTextField jtfName = lcv.getJtfName();		 
		String name = jtfName.getText().trim();
		
		if(name.equals("")) {
			msgCenter(lcv, "주문자명은 필수 입력!!!");
			jtfName.setText("");
			jtfName.requestFocus();
			return;
		} // end if
		
		JTextField jtfTel = lcv.getJtfTel();
		String tel = jtfTel.getText().trim();
		
		if(tel.equals("")) {
			msgCenter(lcv, "전화번호는 필수 입력사항 입니다.");
			jtfTel.setText("");
			jtfTel.requestFocus();
			return; 
		} // end if
		
		try {
			
			// 입력값을 사용하여 DB Table 조회
			List<OrderListVO> list = lc_dao.selectOrderList(new OrderInfoVO(name, tel));
			
			// list에 저장된 OrderListVO 인스턴스를 JTable 에 출력
			DefaultTableModel dtmOrderList = lcv.getDtmOrderList();
			dtmOrderList.setRowCount(0);
			
			String[] rowData = null;
			
			OrderListVO olvo = null;
			for(int i = 0; i < list.size(); i++) {
				olvo = list.get(i);
				rowData = new String[4];
				rowData[0] = String.valueOf(i + 1);
				rowData[1] = olvo.getLunchName();
				rowData[2] = olvo.getOrderDate();
				rowData[3] = String.valueOf(olvo.getQuan());
				
				dtmOrderList.addRow(rowData);
				
			} // end for
			
			if( list.isEmpty() ) {
				msgCenter(lcv, "입력된 정보로 조회된 결과가 없습니다.");
			} else {
				lcv.setTitle("재찬 도시락 - 조회 : [ "+name+" ] 님의 주문현황 입니다.");
			} // end else
			
			jtfName.setText("");
			jtfTel.setText("");
			jtfName.requestFocus();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} // end catch
		
	} // searchLunchOrder
	
	
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		// 주문목록조회 탭의 주문자명 입력란에서 엔터를 쳤을 때
		if(ae.getSource() == lcv.getJtfName()) {
			lcv.getJtfTel().requestFocus();
		} // end if
		
		if(ae.getSource() == lcv.getJbtSearch() || ae.getSource() == lcv.getJtfTel()) {
			// 주문한 도시락 목록 조회
			searchLunchOrder();
		} // end if
		
	} // actionPerformed
	

	/**
	 * 서버로 이미지를 보내고 없는 이미지는 받는 일.
	 * @param fileName
	 */
	private void lunchImageSend(String[] fileNames ) throws IOException{
		
		Socket socket = null;
		
		DataOutputStream dos = null;
		DataInputStream dis = null;
		
		try {
			socket = new Socket("211.63.89.133", 25000);
			dos = new DataOutputStream(socket.getOutputStream());
			dos.writeInt(fileNames.length);
			
			for(int i = 0; i < fileNames.length; i++) {
				dos.writeUTF(fileNames[i]); // 서버로 전송
			} // end for

			dos.flush(); // 목적지로 분출
			
			dis = new DataInputStream(socket.getInputStream());
			// 서버가 보내오는 파일 개수 저장
			int fileCnt = dis.readInt();
			System.out.println("클라이언트 "+ fileCnt + "개 받음");
			
			String fileName = "";
			int fileSize = 0;
			int fileLen = 0;
			
			FileOutputStream fos = null;
			for( int i = 0; i < fileCnt; i ++) {
				
				// 전달받을 파일조각의 개수
				fileSize = dis.readInt();

				// 파일명 받기
				fileName = dis.readUTF();
				fos = new FileOutputStream("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/user/img/"+fileName);
				
				
				byte[] readData = new byte[512];
				
				
				while( fileSize > 0 ) {
					// 서버에서 전송한 파일 조각을 읽어들여
					fileLen = dis.read(readData); 
					// 생성한 파일로 기록
					fos.write(readData, 0, fileLen);
					fos.flush();
					fileSize--;
				} // end while
				
			} // end for
			
			
		} finally {
			if(dos != null) {dos.close();} // end if
			if(socket != null) {socket.close();} // end if
		} // end finally
		
	} // lunchImageSend
	
	
	/**
	 * 도시락 이미지 리스트
	 */
	private String[] lunchImageList() {
		String[] fileList = null;
		String path = "C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/user/img/";
		
		File dir = new File(path);
//		fileList = dir.list(); // 파일의 리스트가 나옴
		
		// 큰 이미지 (s_가 붙지 않은)만 배열에 넣으세요.
		List<String> list = new ArrayList<String>();
		for(String f_name : dir.list() ) {
			if(!f_name.startsWith("s_")) {
				list.add(f_name);
			} // end if
		} // end for
		
		fileList = new String[list.size()];
		list.toArray( fileList );
		
		
		
		return fileList;
	} // lunchImageList
	
	
	
	

	////////////////// UnUsed Method //////////////////
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	/////////////////////////////////////////////////////

	public LunchClientController() {
		String[] fileNames = lunchImageList(); // lunchImageList() 메서드를 실행한 결과를 fileNames 변수에 담는다.
		try {
			lunchImageSend(fileNames);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // LunchClientController 기본 생성자
	
	
} // class
