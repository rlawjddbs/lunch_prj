package kr.co.sist.lunch.admin.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import kr.co.sist.lunch.admin.controller.LunchAddController;
import kr.co.sist.lunch.admin.controller.LunchMainController;

/**
 *	도시락 테이블에서 더블클릭된 도시락의 상세 정보를 출력하고 수정, 삭제를 할 수 있는 창
 * @author owner
 */
@SuppressWarnings("serial")
public class LunchAddView extends JDialog {
	
	private JLabel jlLunchImg;
	private JTextField jtfLunchName,  jtfLunchPrice;
	private JTextArea jtaLunchSpec;
	private JButton jbImg, jbAdd, jbEnd;
	
	
	public LunchAddView(LunchMainView lmv, LunchMainController lmc) {
		super(lmv, "도시락 정보 추가", true); // 부모 프레임, 다이얼로그 타이틀, 모달 여부(true & false)
		
		// DB에서 조회한 결과를 Component에서 채운다.
		ImageIcon iiLunch = new ImageIcon("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/admin/img/no_img.jpg");
		jlLunchImg = new JLabel(iiLunch);
		
		jtfLunchName = new JTextField();
		jtfLunchPrice = new JTextField();

		jtaLunchSpec = new JTextArea();

		jbImg = new JButton("이미지선택");
		jbAdd = new JButton("추가");
		jbEnd = new JButton("닫기");
		
		JScrollPane jspTaSpec = new JScrollPane(jtaLunchSpec);
		
		// 수동배치(FlowLayout)를 위해 레이아웃 기본값 해제
		setLayout(null);
		
		// 다이얼로그 타이틀 라벨
		JLabel jlDetailTitle = new JLabel("도시락 상세정보");
		jlDetailTitle.setFont(new Font("Dialog", Font.BOLD, 25));
		
		// 각 TF에 붙여줄 라벨
		JLabel jlLunchName = new JLabel("도시락명");
		JLabel jlLunchPrice = new JLabel("가격");
		JLabel jlLunchSpec= new JLabel("특장점");
		
		
		
		// 배치
		jlDetailTitle.setBounds(10, 20, 250, 30);
		jlLunchImg.setBounds(10, 65, 244, 220);
		jbImg.setBounds(80, 300, 100, 30);
		
		jlLunchName.setBounds(270, 65, 80, 25);
		jlLunchPrice.setBounds(270, 95, 80, 25);
		jlLunchSpec.setBounds(270, 125, 80, 25);
		
		jtfLunchName.setBounds(340, 65, 185, 25);
		jtfLunchName.setBackground(Color.WHITE);
		
		jtfLunchPrice.setBounds(340, 95, 185, 25);
		jtfLunchPrice.setBackground(Color.WHITE);
		
		jspTaSpec.setBounds(340, 125, 185, 200);
		
		jbAdd.setBounds(340, 330, 90, 30);
		jbEnd.setBounds(435, 330, 90, 30);
		
		
		// 추가
		add(jlDetailTitle);
		add(jlLunchImg);
		add(jbImg);
		add(jlLunchName);
		add(jlLunchPrice);
		add(jlLunchSpec);
		add(jtfLunchName);
		add(jtfLunchPrice);
		add(jspTaSpec);
		add(jbAdd);
		add(jbEnd);
		
		// 이벤트 등록
		LunchAddController lac = new LunchAddController(this, lmc);
		jbImg.addActionListener(lac);
		jbAdd.addActionListener(lac);
		jbEnd.addActionListener(lac);
		addWindowListener(lac);
		
		
		setBounds(lmv.getX()+100, lmv.getY()+50, 550, 500);
		setVisible(true);
		setResizable(false);
		// EXIT_ON_CLOSE 는 Frame을 닫을 때 사용하며
		// 다이얼로그를 종료할 때는 DISPOSE_ON_CLOSE 를 사용해야 한다.
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
	} // LunchDetailView


	public JLabel getJlLunchImg() {
		return jlLunchImg;
	} // getJlLunchImg


	public JTextField getJtfLunchName() {
		return jtfLunchName;
	} // getJtfLunchName


	public JTextField getJtfLunchPrice() {
		return jtfLunchPrice;
	} // getJtfLunchPrice


	public JTextArea getJtaLunchSpec() {
		return jtaLunchSpec;
	} // getJtaLunchSpec


	public JButton getJbImg() {
		return jbImg;
	} // getJbImg


	public JButton getJbAdd() {
		return jbAdd;
	} // getJbAdd


	public JButton getJbEnd() {
		return jbEnd;
	} // getJbEnd

	
} // class
