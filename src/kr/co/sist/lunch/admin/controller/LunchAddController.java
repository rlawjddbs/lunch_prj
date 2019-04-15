package kr.co.sist.lunch.admin.controller;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import kr.co.sist.lunch.admin.model.LunchAdminDAO;
import kr.co.sist.lunch.admin.run.LunchAdminMain;
import kr.co.sist.lunch.admin.view.LunchAddView;
import kr.co.sist.lunch.admin.vo.LunchAddVO;

public class LunchAddController extends WindowAdapter implements ActionListener{

	private LunchAddView lav;
	private LunchMainController lmc;
	private String uploadImg;
	
	public LunchAddController(LunchAddView lav, LunchMainController lmc) {
		this.lav = lav;
		this.lmc = lmc;
		// String �ڷ����� uploadImg ������ null���� empty ������ �ʱ�ȭ
		uploadImg = "";
	} // LunchAddController

	@Override
	public void windowClosing(WindowEvent we) {
		lav.dispose();
	} // windowClosing 
	
	/**
	 * �Է°��� �̹����� ������ �� ��ȿ�� ����(�̹���, ���ö���, ����, Ư������ �Է� �Ǿ����� Ȯ��)
	 */
	private void addLunch() {
		
		if(uploadImg.equals("")) {
			JOptionPane.showMessageDialog(lav, "���ö� �̹����� �������ּ���.");
			return;
		} // end if
		
		JTextField jtfName = lav.getJtfLunchName();
		JTextField jtfPrice = lav.getJtfLunchPrice();
		JTextArea jtaSpec = lav.getJtaLunchSpec();
		
		if(jtfName.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(lav, "���ö� �̸��� �ʼ� �Է»��� �Դϴ�.");
			jtfName.setText("");
			jtfName.requestFocus();
			return;
		} // end if
		
		
		if(jtfPrice.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(lav, "���ö��� ������ �ʼ� �Է»��� �Դϴ�.");
			jtfPrice.setText("");
			jtfPrice.requestFocus();
			return;
		} // end if
		
		int price = 0;
		try {
			price = Integer.parseInt(jtfPrice.getText().trim());
		} catch(NumberFormatException nfe) {
			JOptionPane.showMessageDialog(lav, "���ö��� ������ ���ڸ� �Է� �����մϴ�.");
			jtfPrice.setText("");
			jtfPrice.requestFocus();
			return;
		} // end catch
		
		if(jtaSpec.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(lav, "���ö��� �󼼼����� �ʼ� �Է»��� �Դϴ�.");
			jtaSpec.setText("");
			jtaSpec.requestFocus();
			return;
		} // end if
		
		File file = new File(uploadImg);
		
		
		LunchAddVO lavo = new LunchAddVO(jtfName.getText().trim(), file.getName(), jtaSpec.getText().trim(), 
				price);
		// �� ������ �ν��Ͻ��� DB�� �߰� ��
		
		try {
			LunchAdminDAO.getInstance().insertLunch(lavo); // DB�� �߰�
			// �̹����� ����ϴ� ������ ���� (��Ʈ�� �ʿ�)
			uploadImg(file);
			// ���� ����Ʈ�� ���ο� ���ϸ��� �߰��Ѵ�.
			LunchAdminMain.lunchImageList.add(file.getName());
			// ����Ʈ ����
			lmc.setLunch();
			// ���� ���ö��� �Է��� ���ϰ� �ϱ� ���ؼ� �Է� �� �ʱ�ȭ
			jtfName.setText("");
			jtfPrice.setText("");
			jtaSpec.setText("");
			
			lav.getJlLunchImg().setIcon(new ImageIcon("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/admin/img/no_img.jpg"));
			
			JOptionPane.showMessageDialog(lav, "���ö��� �߰��Ǿ����ϴ�.");
			
		} catch(IOException ioe) {
			JOptionPane.showMessageDialog(lav, "���� ���ε� ����");
		} catch (SQLException se) {
			JOptionPane.showMessageDialog(lav, "DB���� ������ �߻��߽��ϴ�.");
			se.printStackTrace();
		} // end catch
		
	} // addLunch
	
	/**
	 * ū �̹������� ���� File ��ü�� �Է��Ͽ� ���ε� ������ ū �̹���(m1_l1.gif)��
	 *  s_�� ���� ���� �̹���(s_m1_l1.gif)�� ���ε� �Ѵ�.
	 * @param file
	 */
	private void uploadImg(File file) throws IOException{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			// ū �̹����� ���ε�
			fis = new FileInputStream(file);
			byte[] readData = new byte[512];
			
			int len = 0;
			String uploadPath = "C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/admin/img/";
			fos = new FileOutputStream(uploadPath+file.getName());
			
			while( (len=fis.read(readData)) != -1 ) {
				fos.write(readData, 0, len);
				fos.flush();
			} // end while
			
			fis.close();
			fos.close();
			
		// ���� �̹����� ���ε�
			fis = new FileInputStream(file.getParent()+"/s_"+file.getName());
			
			len = 0;
			fos = new FileOutputStream(uploadPath+"s_"+file.getName());
			
			while( (len=fis.read(readData)) != -1 ) {
				fos.write(readData, 0, len);
				fos.flush();
			} // end while
			
			
		} finally {
			if ( fis != null ) { fis.close(); } // end if
			if ( fos != null ) { fos.close(); } // end if
		} // end finally
		
	} // uploadImg
	
	private void setImg() {
		
		FileDialog fdOpen = new FileDialog(lav, "���ö� �̹��� ����", FileDialog.LOAD);
		fdOpen.setVisible(true);
		
		String path = fdOpen.getDirectory();
		String name = fdOpen.getFile();
		
		boolean flag = false;
		if ( path != null ) {
			String[] extFlag = {"jpg","gif","jpeg","png","bmp"};
			
			for(String ext : extFlag ) {
				
				if(name.toLowerCase().endsWith(ext)) { // ���ε� ���� Ȯ����
					flag = true;
				} // end if
				
			} // end for

			if(flag) {
				uploadImg = path + name;
				lav.getJlLunchImg().setIcon(new ImageIcon(uploadImg));
			} else {
				JOptionPane.showMessageDialog(lav, name + "�� �̹����� �ƴմϴ�.");
			} // end else
			
		} // end if
	} // setImg
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		// �̹��� ��ư
		if (ae.getSource() == lav.getJbImg()) {
			setImg();
		} // end if
		
		// �߰� ��ư
		if (ae.getSource() == lav.getJbAdd()) {
			addLunch();
		} // end if

		// �ݱ� ��ư
		if (ae.getSource() == lav.getJbEnd()) {
			lav.dispose();
		} // end if
	} // actionPerformed
	
	
	
} // class
