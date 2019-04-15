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
 *	���ö� ���̺��� ����Ŭ���� ���ö��� �� ������ ����ϰ� ����, ������ �� �� �ִ� â
 * @author owner
 */
@SuppressWarnings("serial")
public class LunchAddView extends JDialog {
	
	private JLabel jlLunchImg;
	private JTextField jtfLunchName,  jtfLunchPrice;
	private JTextArea jtaLunchSpec;
	private JButton jbImg, jbAdd, jbEnd;
	
	
	public LunchAddView(LunchMainView lmv, LunchMainController lmc) {
		super(lmv, "���ö� ���� �߰�", true); // �θ� ������, ���̾�α� Ÿ��Ʋ, ��� ����(true & false)
		
		// DB���� ��ȸ�� ����� Component���� ä���.
		ImageIcon iiLunch = new ImageIcon("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/admin/img/no_img.jpg");
		jlLunchImg = new JLabel(iiLunch);
		
		jtfLunchName = new JTextField();
		jtfLunchPrice = new JTextField();

		jtaLunchSpec = new JTextArea();

		jbImg = new JButton("�̹�������");
		jbAdd = new JButton("�߰�");
		jbEnd = new JButton("�ݱ�");
		
		JScrollPane jspTaSpec = new JScrollPane(jtaLunchSpec);
		
		// ������ġ(FlowLayout)�� ���� ���̾ƿ� �⺻�� ����
		setLayout(null);
		
		// ���̾�α� Ÿ��Ʋ ��
		JLabel jlDetailTitle = new JLabel("���ö� ������");
		jlDetailTitle.setFont(new Font("Dialog", Font.BOLD, 25));
		
		// �� TF�� �ٿ��� ��
		JLabel jlLunchName = new JLabel("���ö���");
		JLabel jlLunchPrice = new JLabel("����");
		JLabel jlLunchSpec= new JLabel("Ư����");
		
		
		
		// ��ġ
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
		
		
		// �߰�
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
		
		// �̺�Ʈ ���
		LunchAddController lac = new LunchAddController(this, lmc);
		jbImg.addActionListener(lac);
		jbAdd.addActionListener(lac);
		jbEnd.addActionListener(lac);
		addWindowListener(lac);
		
		
		setBounds(lmv.getX()+100, lmv.getY()+50, 550, 500);
		setVisible(true);
		setResizable(false);
		// EXIT_ON_CLOSE �� Frame�� ���� �� ����ϸ�
		// ���̾�α׸� ������ ���� DISPOSE_ON_CLOSE �� ����ؾ� �Ѵ�.
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
