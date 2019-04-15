package kr.co.sist.lunch.admin.view;

import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import kr.co.sist.lunch.admin.controller.LunchLoginController;

@SuppressWarnings("serial")
public class LunchLoginView extends JFrame{

	private JTextField jtfId;
	private JPasswordField jpfPass;
	private JButton jbtLogin;
	
	public LunchLoginView() {
		super("���ö� ������ �α���");
		jtfId = new JTextField();
		jpfPass = new JPasswordField();
		jbtLogin = new JButton("�α���");
		
		JLabel jlLoginTitle = new JLabel("������ �α���");
		jlLoginTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
		
		JLabel jlIdTitle = new JLabel("���̵�");
		JLabel jlPassTitle = new JLabel("��й�ȣ");
		
		setLayout(null);
		
		jlLoginTitle.setBounds(90, 10, 200, 40);
		jlIdTitle.setBounds(30, 60, 80, 20);
		jtfId.setBounds(90, 60, 100, 20);
		jlPassTitle.setBounds(30, 90, 80, 20);
		jpfPass.setBounds(90, 90, 100, 20);
		jbtLogin.setBounds(200, 60, 80, 50);
		
		
		add(jlLoginTitle);
		add(jlIdTitle);
		add(jtfId);
		add(jlPassTitle);
		add(jpfPass);
		add(jbtLogin);
		
		// �̺�Ʈ ó�� ���
		LunchLoginController llc = new LunchLoginController(this);
		addWindowFocusListener(llc);
		jtfId.addActionListener(llc);
		jpfPass.addActionListener(llc);
		jbtLogin.addActionListener(llc);
		
		setBounds(100, 100, 320, 250);
		setVisible(true);
		setResizable(false);
		
		jtfId.requestFocus();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
	}//LunchLoginView

	public JTextField getJtfId() {
		return jtfId;
	} // getJtfId

	public JPasswordField getJpfPass() {
		return jpfPass;
	} // getJpfPass

	public JButton getJbtLogin() {
		return jbtLogin;
	} // getJbtLogin
	
	
	
}//class
