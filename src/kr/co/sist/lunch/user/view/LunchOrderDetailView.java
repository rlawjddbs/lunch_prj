package kr.co.sist.lunch.user.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import kr.co.sist.lunch.user.controller.LunchOrderDetailController;
import kr.co.sist.lunch.user.vo.LunchDetailVO;

@SuppressWarnings("serial")
public class LunchOrderDetailView extends JDialog{

	private JLabel jlLunchImg;
	private JTextField jtfLunchName, jtfLunchPrice, jtfOrderName, jtfPhone, jtfTotalPrice; 
	private JTextArea jtaLunchSpec, jtaRequest;
	private JButton jbOrder, jbEnd;
	private JComboBox<Integer> jbQuan;
	
	public LunchOrderDetailView(LunchClientView lcv, LunchDetailVO ldvo) {
		super(lcv, "���ö� �� ����", true); // �θ� ������, ���̾�α� Ÿ��Ʋ, ��� ����(true & false)
		
		JLabel jlblDetailTitle = new JLabel("���ö� �� ����");
		jlblDetailTitle.setFont(new Font("Dialog", Font.BOLD, 25));
		jlblDetailTitle.setBounds(10, 25, 244, 30);
		
		
		// DB���� ��ȸ�� ����� Component���� ä���.
		
		ImageIcon iiLunch = new ImageIcon("C:/dev/workspace/lunch_prj/src/kr/co/sist/lunch/user/img/" + ldvo.getImg());
		jlLunchImg = new JLabel(iiLunch);
		
		jtfLunchName = new JTextField( ldvo.getLunchName() );
		jtfLunchPrice = new JTextField( String.valueOf(ldvo.getPrice()) );
		jtfPhone = new JTextField();
		jtfTotalPrice = new JTextField( String.valueOf(ldvo.getPrice()) );
		jtfOrderName = new JTextField();
		

		jtaLunchSpec = new JTextArea( ldvo.getSpec() );
		jtaLunchSpec.setEditable(false);
		jtaLunchSpec.setBackground(Color.WHITE);

		jtaRequest = new JTextArea(20, 10);
		
		
		// DB���� ��ȸ�� �� ����
		jbOrder = new JButton("�ֹ�");
		jbEnd = new JButton("�ݱ�");
		
		JScrollPane jspTaSpec = new JScrollPane(jtaLunchSpec);
		JScrollPane jspTaRequest = new JScrollPane(jtaRequest);
		jspTaRequest.setBorder(new TitledBorder("��û����"));
		
		
		// ������ġ(FlowLayout)�� ���� ���̾ƿ� �⺻�� ����
		setLayout(null);
		
		// ���̾�α� Ÿ��Ʋ ��
		JLabel jlDetailTitle = new JLabel("���ö� ������");
		jlDetailTitle.setFont(new Font("Dialog", Font.BOLD, 25));
		
		// �� TF�� �ٿ��� ��
		JLabel jlLunchName = new JLabel("���ö���");
		JLabel jlLunchPrice = new JLabel("����");
		JLabel jlLunchQuan = new JLabel("����");
		JLabel jlOrderName = new JLabel("�ֹ��ڸ�");
		JLabel jlOrderTel = new JLabel("��ȭ��ȣ");
		JLabel jlLunchTotalPrice= new JLabel("�Ѱ���");
		JLabel jlLunchSpec= new JLabel("Ư����");
		
		DefaultComboBoxModel<Integer> dlmQuan = new DefaultComboBoxModel<Integer>();
		for( int i = 1; i < 11; i++) {
			dlmQuan.addElement(new Integer(i));
		} // end for
		jbQuan = new JComboBox<Integer>( dlmQuan );
		
		
		// ��ġ
		jlDetailTitle.setBounds(10, 20, 250, 30);
		jlLunchImg.setBounds(10, 65, 244, 220);
		
		jlLunchName.setBounds(270, 65, 80, 25);
		jlLunchPrice.setBounds(270, 95, 80, 25);
		jlLunchQuan.setBounds(270, 125, 80, 25);
		jlLunchTotalPrice.setBounds(270, 155, 80, 25);
		jlOrderName.setBounds(270, 185, 80, 25);
		jlOrderTel.setBounds(270, 215, 80, 25);
		jlLunchSpec.setBounds(270, 245, 80, 25);
		
		
		jtfLunchName.setBounds(340, 65, 185, 25);
		jtfLunchName.setBackground(Color.WHITE);
		jtfLunchName.setEditable(false);
		
		jtfLunchPrice.setBounds(340, 95, 185, 25);
		jtfLunchPrice.setEditable(false);
		jtfLunchPrice.setBackground(Color.WHITE);
		
		jbQuan.setBounds(340, 125, 185, 25);
		
		jtfTotalPrice.setBounds(340, 155, 185, 25);
		jtfTotalPrice.setEditable(false);
		jtfTotalPrice.setBackground(Color.WHITE);
		
		jtfOrderName.setBounds(340, 185, 185, 25);
		jtfPhone.setBounds(340, 215, 185, 25);
		
		jspTaSpec.setBounds(340, 245, 185, 100);
		jspTaRequest.setBounds(10, 293, 260, 100);
		
		jbOrder.setBounds(342, 360, 80, 30);
		jbEnd.setBounds(442, 360, 80, 30);
		
		
		// �߰�
		add(jlblDetailTitle);		add(jlLunchImg);
		add(jlLunchName);		add(jtfLunchName);
		add(jlLunchPrice);		add(jtfLunchPrice);
		add(jlLunchQuan);		add(jbQuan);
		add(jlLunchTotalPrice);		add(jtfTotalPrice);
		add(jlOrderName);		add(jtfOrderName);
		add(jlOrderTel);		add(jtfPhone);
		add(jlLunchSpec);		add(jspTaSpec);	
		add(jspTaRequest);
		
		add(jbOrder);
		add(jbEnd);
		
		LunchOrderDetailController lodc = new LunchOrderDetailController(this, ldvo.getLunchCode());
		addWindowListener(lodc);
		
		jbQuan.addActionListener( lodc );
		jbOrder.addActionListener( lodc );
		jbEnd.addActionListener( lodc );
		
		setBounds(lcv.getX()+100, lcv.getY()+50, 550, 450);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	} // LunchOrderDetalView

	public JLabel getJlLunchImg() {
		return jlLunchImg;
	}

	public JTextField getJtfLunchName() {
		return jtfLunchName;
	}

	public JTextField getJtfLunchPrice() {
		return jtfLunchPrice;
	}

	public JTextField getJtfOrderName() {
		return jtfOrderName;
	}

	public JTextField getJtfPhone() {
		return jtfPhone;
	}

	public JTextField getJtfTotalPrice() {
		return jtfTotalPrice;
	}

	public JTextArea getJtaLunchSpec() {
		return jtaLunchSpec;
	}

	public JButton getJbOrder() {
		return jbOrder;
	}

	public JButton getJbEnd() {
		return jbEnd;
	}

	public JComboBox<Integer> getJbQuan() {
		return jbQuan;
	}
	
	public JTextArea getJtaRequest() {
		return jtaRequest;
	}
	
	
} // LunchOrderDetailView
