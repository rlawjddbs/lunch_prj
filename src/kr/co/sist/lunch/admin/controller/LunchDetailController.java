package kr.co.sist.lunch.admin.controller;

import java.awt.FileDialog;
import java.awt.HeadlessException;
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
import kr.co.sist.lunch.admin.view.LunchDetailView;
import kr.co.sist.lunch.admin.vo.LunchUpdateVO;

public class LunchDetailController extends WindowAdapter implements ActionListener {

	private LunchDetailView ldv;
	private LunchMainController lmc;
	private LunchAdminDAO la_dao;
	private String originalImg;
	
	private String uploadImg;
	
	public LunchDetailController(LunchDetailView ldv, LunchMainController lmc, String originalImg) {
		this.ldv = ldv;
		this.lmc = lmc;
		la_dao = LunchAdminDAO.getInstance();
		this.originalImg = originalImg;
		uploadImg="";
	} // LunchDetailController
	
	@Override
	public void windowClosing(WindowEvent we) {
		ldv.dispose();
	} // windowClosing

	private void removeLunch(String code) {
		
			try {
				// 도시락 삭제
				if(la_dao.deleteLunch(code)) {
					// 도시락 리스트를 갱신한다.
					lmc.setLunch(); 
					// 삭제한 도시락의 이미지 파일을 삭제한다.
					File file = new File("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/admin/img/"+originalImg);
					File rmFile = new File(file.getAbsolutePath()); // 큰 파일
					File rmFile2 = new File(file.getParent() + "/s_" + file.getName()); // 작은 파일
					
					rmFile.delete();
					rmFile2.delete();
					
					
					JOptionPane.showMessageDialog(ldv, "도시락이 삭제 되었습니다.");
				} else {
					JOptionPane.showMessageDialog(ldv, "도시락이 존재 하지 않습니다.");
				} // end else
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(ldv, "DB에서 문제가 발생했습니다.");
				e.printStackTrace();
			} // end catch
		
	} // removeLunch
	
	public void chgImg() {

		FileDialog fdOpen = new FileDialog(ldv, "도시락 이미지 선택", FileDialog.LOAD);
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
				ldv.getJlLunchImg().setIcon(new ImageIcon(uploadImg));
			} else {
				JOptionPane.showMessageDialog(ldv, name + "은 이미지가 아닙니다.");
			} // end else
			
		} // end if
		
	} // chgImg
	
	
	/**
	 * 도시락 코드, 도시락명, 이미지, 가격, 특장점을 입력받아 코드에 해당하는 도시락의 정보를 변경
	 * @return
	 */
	private void modifyLunch() {
		// 유효성 검증 : 도시락명, 가격, 특장점이 ""가 아닐 때, 가격은 숫자인지,
		// 이미지가 변경되었다면 변경된 이미지명 뒤에 s_가 붙은 이미지가 존재하는 것인지 판단
		JTextField tfName = ldv.getJtfLunchName(); 
		JTextField tfPrice = ldv.getJtfLunchPrice(); 
		JTextArea taSpec = ldv.getJtaLunchSpec(); 
		
		if (tfName.getText().trim().equals("")) {
			tfName.setText("");
			JOptionPane.showMessageDialog(ldv, "변경할 도시락명을 입력해주세요.");
			tfName.setText("");
			tfName.requestFocus();
			return;
		} // end if
		
		if (tfPrice.getText().trim().equals("")) {
			tfPrice.setText("");
			JOptionPane.showMessageDialog(ldv, "변경할 도시락 가격을 입력해주세요.");
			tfPrice.setText("");
			tfPrice.requestFocus();
			return;
		} // end if
		
		if (taSpec.getText().trim().equals("")) {
			taSpec.setText("");
			JOptionPane.showMessageDialog(ldv, "변경할 도시락 특장점을 입력해주세요.");
			taSpec.setText("");
			taSpec.requestFocus();
			return;
		} // end if
		
		int price = 0;
		try {
			price = Integer.parseInt(tfPrice.getText().trim());
		} catch(NumberFormatException nfe) {
			JOptionPane.showMessageDialog(ldv, "도시락 가격은 숫자만 입력 가능합니다.");
			tfPrice.setText("");
			tfPrice.requestFocus();
			return;
		} // end catch
		
		// 선택한 이미지와 동일한 이름의 이미지가 s_를 가지지 않은 경우 이미지 작업이 되어있지
		// 않은 경우
		if ( !uploadImg.equals("") ) {
			File tempFile = new File(uploadImg); // 큰 이미지
			File smallFile = new File(tempFile.getParent()+"/s_"+tempFile.getName()); // 작은 이미지
			
			if( !smallFile.exists() ) {
				JOptionPane.showMessageDialog(ldv, "선택하신 파일은 작은 이미지가 존재하지 않습니다.");
				uploadImg = "";
				return;
			} // end if
		} // end if
		
		StringBuilder updateMsg = new StringBuilder();
		updateMsg
		.append("수정정보 \n").append("도시락 명 : ")
		.append( tfName.getText()).append("\n")
		.append("가격 : ").append(tfPrice.getText()).append("\n")
		.append("특장점 : ").append(taSpec.getText()).append("\n")
		.append("로 도시락의 정보가 변경 됩니다. 변경하시겠습니까?");
		
		switch(JOptionPane.showConfirmDialog(ldv, updateMsg.toString())) {
		case JOptionPane.OK_OPTION:
			// 변경된 값으로 VO를 생성하고
			File file = new File(uploadImg);
			LunchUpdateVO luvo = 
			new LunchUpdateVO(ldv.getJtfLunchCode().getText(),
					tfName.getText(), file.getName(), taSpec.getText(), price);
			
			try {
				
				if(la_dao.updateLunch(luvo)) { // DB Table에서 update를 수행
					JOptionPane.showMessageDialog(ldv, "도시락 정보가 변경되었습니다.");
					// 변경한 이미지가 존재하는 경우
					if( !uploadImg.equals("") ) { 
						try {
							//이전 이미지를 삭제한 후
							File original = new File("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/admin/img/"+originalImg);
							File smallSizeImg = new File(original.getParent() + "/s_" + original.getName()); // 작은 파일
							System.out.println(original.getParent() + "/s_" + original.getName());
							original.delete();
							smallSizeImg.delete();
							
							
							// 변경한 이미지를 지정한 폴더에 업로드 한다.
							uploadImg(file);
						} catch (IOException e) {
							e.printStackTrace();
						} // end catch
					} // end if
					lmc.setLunch(); // 부모창의 도시락 리스트를 변경한다.
				} else {
					JOptionPane.showMessageDialog(ldv, "도시락 정보가 변경되지 않았습니다.");
				} // end else
				
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(ldv, "DB에서 문제가 발생했습니다.");
				e.printStackTrace();
			} // end catch
			
			break;
		} // end switch
		
	} // modifyLunch
	
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
	
	
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		
		// 수정
		if(ae.getSource() == ldv.getJbUpdate()) { 
			
			modifyLunch();
			
		} // end if
		
		// 삭제
		if(ae.getSource() == ldv.getJbDelete()) {
			StringBuilder deleteMsg = new StringBuilder();
			deleteMsg
			.append(ldv.getJtfLunchName().getText()).append("(")
			.append(ldv.getJtfLunchCode().getText()).append(")을(를) 삭제하시겠습니까?");
			
			switch(JOptionPane.showConfirmDialog(ldv, deleteMsg.toString())) {
			case JOptionPane.OK_OPTION:
				System.out.println( ldv.getJlLunchImg().getIcon() );
				removeLunch( ldv.getJtfLunchCode().getText());
			} // end switch
		} // end if
		
		// 이미지 변경
		if(ae.getSource() == ldv.getJbImg()) {
			chgImg();
		} // end if
		
		// 종료
		if(ae.getSource() == ldv.getJbEnd()) {
			ldv.dispose();
		} // end if
		
	} // actionPerformed
	
	
	
	
} // class
