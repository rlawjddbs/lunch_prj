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
		// String 자료형의 uploadImg 변수를 null에서 empty 값으로 초기화
		uploadImg = "";
	} // LunchAddController

	@Override
	public void windowClosing(WindowEvent we) {
		lav.dispose();
	} // windowClosing 
	
	/**
	 * 입력값과 이미지를 선택한 후 유효성 검증(이미지, 도시락명, 가격, 특장점이 입력 되었는지 확인)
	 */
	private void addLunch() {
		
		if(uploadImg.equals("")) {
			JOptionPane.showMessageDialog(lav, "도시락 이미지를 선택해주세요.");
			return;
		} // end if
		
		JTextField jtfName = lav.getJtfLunchName();
		JTextField jtfPrice = lav.getJtfLunchPrice();
		JTextArea jtaSpec = lav.getJtaLunchSpec();
		
		if(jtfName.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(lav, "도시락 이름은 필수 입력사항 입니다.");
			jtfName.setText("");
			jtfName.requestFocus();
			return;
		} // end if
		
		
		if(jtfPrice.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(lav, "도시락의 가격은 필수 입력사항 입니다.");
			jtfPrice.setText("");
			jtfPrice.requestFocus();
			return;
		} // end if
		
		int price = 0;
		try {
			price = Integer.parseInt(jtfPrice.getText().trim());
		} catch(NumberFormatException nfe) {
			JOptionPane.showMessageDialog(lav, "도시락의 가격은 숫자만 입력 가능합니다.");
			jtfPrice.setText("");
			jtfPrice.requestFocus();
			return;
		} // end catch
		
		if(jtaSpec.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(lav, "도시락의 상세설명은 필수 입력사항 입니다.");
			jtaSpec.setText("");
			jtaSpec.requestFocus();
			return;
		} // end if
		
		File file = new File(uploadImg);
		
		
		LunchAddVO lavo = new LunchAddVO(jtfName.getText().trim(), file.getName(), jtaSpec.getText().trim(), 
				price);
		// ↑ 생성된 인스턴스를 DB에 추가 ↓
		
		try {
			LunchAdminDAO.getInstance().insertLunch(lavo); // DB에 추가
			// 이미지를 사용하는 폴더로 복사 (스트림 필요)
			uploadImg(file);
			// 파일 리스트에 새로운 파일명을 추가한다.
			LunchAdminMain.lunchImageList.add(file.getName());
			// 리스트 갱신
			lmc.setLunch();
			// 다음 도시락의 입력을 편하게 하기 위해서 입력 폼 초기화
			jtfName.setText("");
			jtfPrice.setText("");
			jtaSpec.setText("");
			
			lav.getJlLunchImg().setIcon(new ImageIcon("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/admin/img/no_img.jpg"));
			
			JOptionPane.showMessageDialog(lav, "도시락이 추가되었습니다.");
			
		} catch(IOException ioe) {
			JOptionPane.showMessageDialog(lav, "파일 업로드 실패");
		} catch (SQLException se) {
			JOptionPane.showMessageDialog(lav, "DB에서 문제가 발생했습니다.");
			se.printStackTrace();
		} // end catch
		
	} // addLunch
	
	/**
	 * 큰 이미지명을 가진 File 객체를 입력하여 업로드 폴더에 큰 이미지(m1_l1.gif)와
	 *  s_가 붙은 작은 이미지(s_m1_l1.gif)를 업로드 한다.
	 * @param file
	 */
	private void uploadImg(File file) throws IOException{
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			// 큰 이미지를 업로드
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
			
		// 작은 이미지를 업로드
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
		
		FileDialog fdOpen = new FileDialog(lav, "도시락 이미지 선택", FileDialog.LOAD);
		fdOpen.setVisible(true);
		
		String path = fdOpen.getDirectory();
		String name = fdOpen.getFile();
		
		boolean flag = false;
		if ( path != null ) {
			String[] extFlag = {"jpg","gif","jpeg","png","bmp"};
			
			for(String ext : extFlag ) {
				
				if(name.toLowerCase().endsWith(ext)) { // 업로드 가능 확장자
					flag = true;
				} // end if
				
			} // end for

			if(flag) {
				uploadImg = path + name;
				lav.getJlLunchImg().setIcon(new ImageIcon(uploadImg));
			} else {
				JOptionPane.showMessageDialog(lav, name + "은 이미지가 아닙니다.");
			} // end else
			
		} // end if
	} // setImg
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		// 이미지 버튼
		if (ae.getSource() == lav.getJbImg()) {
			setImg();
		} // end if
		
		// 추가 버튼
		if (ae.getSource() == lav.getJbAdd()) {
			addLunch();
		} // end if

		// 닫기 버튼
		if (ae.getSource() == lav.getJbEnd()) {
			lav.dispose();
		} // end if
	} // actionPerformed
	
	
	
} // class
