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
			String[] fileNames = lunchImageList(); // Ŭ���̾�Ʈ�� ���� �̹����� üũ�Ͽ�
			lunchImageSend(fileNames); // ������ ���� ���� �̹����� ���� ��
		} catch (IOException e) {
			e.printStackTrace();
		} // end catch
		
		setLunchList(); // JTable�� �����Ѵ�.
	} // LunchClientController

	
	
	/**
	 * ���ö� ����� ��ȸ�Ͽ� JTable�� �����ϴ� ��.
	 * Ư������ 25�� �̻��̶�� 24�� ������ ����ϰ� �������� ... ���� �ٿ��� �����Ѵ�. 
	 */
	private void setLunchList() {
		// ���ö� ��� ��ȸ
		try {
			List<LunchListVO> list = lc_dao.selectLunchList();
			
			DefaultTableModel dtm = lcv.getDtmLunchList();
			dtm.setRowCount(0);
			
			Object[] rowData = null;
			
			LunchListVO llvo = null;
			String spec = "";
			
			// ��ȣ(count)�� ���ٸ� ���� for ����, �ִٸ� ���� for���� �̿��ϸ� ����.
			for(int i = 0; i < list.size(); i++) {
				llvo = list.get(i);
				
				// ���̺� �߰��� �����͸� �迭�� ����
				rowData = new Object[5];
				rowData[0] = new Integer(i + 1); // ��ȣ
				rowData[1] = new ImageIcon("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/user/img/s_"+llvo.getImg()); // �̹���
				rowData[2] = llvo.getLunchCode(); // ���ö� �ڵ�
				rowData[3] = llvo.getLunchName(); // ���ö� �̸�
				spec = llvo.getLunchSpec();
				
				if( spec.length() > 25 ) {
					spec = spec.substring(0, 24)+"...";
				} // end if

				rowData[4] = spec; // ���ö��� Ư����
				
				// ������ �����͸� ���̺� �߰�
				dtm.addRow(rowData);
				
			} // end for 
			
			
			
		} catch (SQLException e) {
			msgCenter(lcv, "���ö� ����� ��ȸ�ϴ� �� DB���� ������ �߻��߽��ϴ�.");
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
			
			// Ŭ���̾�Ʈ view ���� ���ö� ��� ���� Ŭ���ϸ� Frame �� Title�� �ٲ��.
			if(lcv.getJtp().getSelectedIndex() == 0) {
				lcv.setTitle("���� ���ö� �ֹ�");
			} // end if
			// Ŭ���̾�Ʈ view ���� �ֹ� ��� ��ȸ ���� Ŭ���ϸ� Frame �� Title�� �ٲ��.
			if(lcv.getJtp().getSelectedIndex() == 1) {
				lcv.setTitle("���� ���ö� �ֹ� ��ȸ");
			} // end if
			
		} // end if
		
		if( me.getSource() == lcv.getJtLunch()) { // �̺�Ʈ�� �߻��� �ּҰ� JTable ������ ��쿡�� ����
			switch(me.getClickCount()) {
			case DBL_CLICK:
				JTable jt = lcv.getJtLunch();
				String lunch_code = (String)jt.getValueAt(jt.getSelectedRow(), 2);
				// ���ö��� �� ���� ��ȸ
				try {
					LunchDetailVO ldvo = lc_dao.selectDetailLunch( lunch_code );
					if ( ldvo == null ) { // �ڵ�� ��ȸ�� ����� ���� ��
						msgCenter(lcv, "[ "+lunch_code+" ]�� ��ȸ�Ǵ� ���ö��� �����ϴ�.");
					} else { // �ڵ�� ��ȸ�� ����� ���� ��
						new LunchOrderDetailView(lcv, ldvo);
					} // end else
				} catch (SQLException se) {
					msgCenter(lcv, "������ ��ȸ �� ������ �߻��߽��ϴ�.");
					se.printStackTrace();
				} // end catch
			} //end switch
		} // end if
	} // mouseClicked
	
	/**
	 * �ֹ��ڸ�� ��ȭ��ȣ�� ������ ���ö� ��ȸ
	 */
	private void searchLunchOrder() {
		JTextField jtfName = lcv.getJtfName();		 
		String name = jtfName.getText().trim();
		
		if(name.equals("")) {
			msgCenter(lcv, "�ֹ��ڸ��� �ʼ� �Է�!!!");
			jtfName.setText("");
			jtfName.requestFocus();
			return;
		} // end if
		
		JTextField jtfTel = lcv.getJtfTel();
		String tel = jtfTel.getText().trim();
		
		if(tel.equals("")) {
			msgCenter(lcv, "��ȭ��ȣ�� �ʼ� �Է»��� �Դϴ�.");
			jtfTel.setText("");
			jtfTel.requestFocus();
			return; 
		} // end if
		
		try {
			
			// �Է°��� ����Ͽ� DB Table ��ȸ
			List<OrderListVO> list = lc_dao.selectOrderList(new OrderInfoVO(name, tel));
			
			// list�� ����� OrderListVO �ν��Ͻ��� JTable �� ���
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
				msgCenter(lcv, "�Էµ� ������ ��ȸ�� ����� �����ϴ�.");
			} else {
				lcv.setTitle("���� ���ö� - ��ȸ : [ "+name+" ] ���� �ֹ���Ȳ �Դϴ�.");
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
		
		// �ֹ������ȸ ���� �ֹ��ڸ� �Է¶����� ���͸� ���� ��
		if(ae.getSource() == lcv.getJtfName()) {
			lcv.getJtfTel().requestFocus();
		} // end if
		
		if(ae.getSource() == lcv.getJbtSearch() || ae.getSource() == lcv.getJtfTel()) {
			// �ֹ��� ���ö� ��� ��ȸ
			searchLunchOrder();
		} // end if
		
	} // actionPerformed
	

	/**
	 * ������ �̹����� ������ ���� �̹����� �޴� ��.
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
				dos.writeUTF(fileNames[i]); // ������ ����
			} // end for

			dos.flush(); // �������� ����
			
			dis = new DataInputStream(socket.getInputStream());
			// ������ �������� ���� ���� ����
			int fileCnt = dis.readInt();
			System.out.println("Ŭ���̾�Ʈ "+ fileCnt + "�� ����");
			
			String fileName = "";
			int fileSize = 0;
			int fileLen = 0;
			
			FileOutputStream fos = null;
			for( int i = 0; i < fileCnt; i ++) {
				
				// ���޹��� ���������� ����
				fileSize = dis.readInt();

				// ���ϸ� �ޱ�
				fileName = dis.readUTF();
				fos = new FileOutputStream("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/user/img/"+fileName);
				
				
				byte[] readData = new byte[512];
				
				
				while( fileSize > 0 ) {
					// �������� ������ ���� ������ �о�鿩
					fileLen = dis.read(readData); 
					// ������ ���Ϸ� ���
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
	 * ���ö� �̹��� ����Ʈ
	 */
	private String[] lunchImageList() {
		String[] fileList = null;
		String path = "C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/user/img/";
		
		File dir = new File(path);
//		fileList = dir.list(); // ������ ����Ʈ�� ����
		
		// ū �̹��� (s_�� ���� ����)�� �迭�� ��������.
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
		String[] fileNames = lunchImageList(); // lunchImageList() �޼��带 ������ ����� fileNames ������ ��´�.
		try {
			lunchImageSend(fileNames);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // LunchClientController �⺻ ������
	
	
} // class
