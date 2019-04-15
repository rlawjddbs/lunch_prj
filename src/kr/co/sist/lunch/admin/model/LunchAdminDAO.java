package kr.co.sist.lunch.admin.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.co.sist.lunch.admin.view.LunchMainView;
import kr.co.sist.lunch.admin.vo.AdminLoginVO;
import kr.co.sist.lunch.admin.vo.CalcVO;
import kr.co.sist.lunch.admin.vo.LunchAddVO;
import kr.co.sist.lunch.admin.vo.LunchDetailVO;
import kr.co.sist.lunch.admin.vo.LunchUpdateVO;
import kr.co.sist.lunch.admin.vo.LunchVO;
import kr.co.sist.lunch.admin.vo.OrderVO;

/**
 * @author owner
 *
 */
/**
 * @author owner
 *
 */
public class LunchAdminDAO {

	private static LunchAdminDAO la_dao;
	
	private LunchAdminDAO() {
		// 1.
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} // end catch
	} // LunchAdminDAO

	public static LunchAdminDAO getInstance() {
		if( la_dao == null ) {
			la_dao = new LunchAdminDAO();
		} // end if
		return la_dao;
	} // getInstance
	
	private Connection getConn() throws SQLException{
		// 2.
		String url = "jdbc:oracle:thin:@211.63.89.133:1521:orcl";
		String id = "scott";
		String pass = "tiger";
		Connection con = DriverManager.getConnection(url, id, pass);
		return con;
	} // Connection
	
	
	
	/**
	 * ID�� PASSWORD�� �Է¹޾� lunch_admin���� �̸��� ��ȸ�ϴ� ��
	 * @param alvo
	 * @return
	 * @throws SQLException
	 */
	public String login(AdminLoginVO alvo) throws SQLException{
		String adminName = "";

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
		// 1.
		// 2. 
			con = getConn();
		// 3.
			String selectName = "SELECT NAME FROM LUNCH_ADMIN WHERE ID=? AND PASS=?";
			pstmt = con.prepareStatement(selectName);
		// 4.
			pstmt.setString(1, alvo.getId());
			pstmt.setString(2, alvo.getPass());
		// 5.
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				adminName = rs.getString("NAME");
			} // end if
			
		} finally {
		// 6.
			if( rs != null ) { rs.close(); } // endif
			if( pstmt != null ) { pstmt.close(); } // endif
			if( con != null ) { con.close(); } // endif
		} // end finally
			
		return adminName;
	} // login
	
	/**
	 * �Էµ� ���ö��� �ڵ�, �̹���, ���ö���, �ܰ� ��ȸ
	 * @return
	 * @throws SQLException
	 */
	public List<LunchVO> selectLunch() throws SQLException{
		List<LunchVO> list = new ArrayList<LunchVO>();
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
		// 1.
		// 2.
			con = getConn();
		// 3.
			String selectAllLunch = "SELECT LUNCH_CODE, LUNCH_NAME, IMG, PRICE FROM LUNCH ORDER BY LUNCH_CODE DESC";
			pstmt = con.prepareStatement(selectAllLunch);
		// 4.
		// 5.
			rs = pstmt.executeQuery();
			
			LunchVO lv = null;
			
			while(rs.next()) {
				lv = new LunchVO(rs.getString("LUNCH_CODE"), rs.getString("LUNCH_NAME"), rs.getString("IMG"), rs.getInt("PRICE"));
				list.add(lv);
			} // end while
			
		} finally {
		// 6.
			if ( rs != null ) { rs.close(); } // end if
			if ( pstmt != null ) { pstmt.close(); } // end if
			if ( con != null ) { con.close(); } // end if
		} // end finally
		
		return list;
	} // selectLunch
	
	/**
	 * �ԷµǴ� �ڵ忡 ���� ���ö� �������� ��ȸ
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public LunchDetailVO selectDetailLunch(String code) throws SQLException{
		LunchDetailVO ldvo = null;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
		// 1.
		// 2.
			con = getConn();
		// 3.
			String selectLunch = "SELECT LUNCH_CODE, LUNCH_NAME, IMG, PRICE, SPEC, TO_CHAR(INPUT_DATE, 'YYYY-MM-DD') INPUT_DATE FROM LUNCH WHERE LUNCH_CODE=?";
			pstmt = con.prepareStatement(selectLunch);
		// 4.
			pstmt.setString(1, code);
		// 5.
			rs = pstmt.executeQuery();
			
			// �Էµ� �ڵ�� ��ȸ�� ���ڵ尡 ������ �� VO�� �����ϰ� �� �߰�
			if( rs.next() ) {
				ldvo = new LunchDetailVO(
						code,
						rs.getString("lunch_name"),
						rs.getString("img"),
						rs.getString("spec"),
						rs.getString("input_date"),
						rs.getInt("price")
						);
			} // end if
			
		} finally {
		// 6.
			if( rs!=null ) { rs.close(); } // end if
			if( pstmt!=null ) { pstmt.close(); } // end if
			if( con!=null ) { con.close(); } // end if
		} // end finally
		
		return ldvo;
	} // LunchDetailView
	
	
	
	/**
	 * ���ö� ������ �߰��ϴ� ��, INSERT �̹Ƿ� ResultSet �������̽��� ����.
	 * @param lav
	 * @throws SQLException
	 */
	public void insertLunch(LunchAddVO lav) throws SQLException{
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
		// 1.
		// 2.
			con = getConn();
		// 3.
			StringBuilder insertLunch = new StringBuilder();
			insertLunch
			.append("INSERT INTO LUNCH (LUNCH_CODE, LUNCH_NAME, IMG, PRICE, SPEC, ID) ")
			.append("values (lunch_code, ?,?,?,?,?)");
			
			pstmt = con.prepareStatement(insertLunch.toString());
			
		// 4. ���ε� ������ �� �ֱ�
			pstmt.setString(1, lav.getLunch_name());
			pstmt.setString(2, lav.getImg());
			pstmt.setInt(3, lav.getPrice());
			pstmt.setString(4, lav.getSpec());
			pstmt.setString(5, LunchMainView.adminId);
		// 5. ���� ���� �� ������
			pstmt.executeUpdate();
		} finally {
		// 6.
			if( pstmt != null ) { pstmt.close(); } // end if
			if( con != null ) { con.close(); } // end if
		} // end finally
	} // insertLunch
	
	public boolean deleteLunch(String code) throws SQLException{
		boolean flag = false;
		Connection con = null;
		PreparedStatement pstmt = null;
		
			try {
			// 1. ����̹� �ε�
			// 2. Connection ���
				con = getConn();
			// 3. ������ ���� ��ü ���
				String deleteQuery = "delete from lunch where lunch_code = ?";
				pstmt = con.prepareStatement(deleteQuery);
			// 4. ���ε� ������ �� ����
				pstmt.setString(1, code);
			// 5. ���� ���� �� ��� ���
				int cnt = pstmt.executeUpdate(); // ���� �� �� �� ��ȯ
				if( cnt == 1) { 
					flag = true;
				} // end if
			} finally {
			// 6. ���� ����
				if( pstmt != null ) { pstmt.close(); } // end if
				if( con != null ) { con.close(); } // end if
			}
		return flag;
	} // deleteLunch
	
	/**
	 * ���ö� �ڵ�, ���ö���, �̹���, ����, Ư������ �Է¹޾� ���ö� �ڵ忡 �ش��ϴ�
	 * ���ö��� ����. �̹����� ""��� �̹����� �������� �ʽ��ϴ�.
	 * @param luvo
	 * @return
	 * @throws SQLException
	 */
	public boolean updateLunch(LunchUpdateVO luvo) throws SQLException{
		boolean flag = false;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
		// 1. ����̹� �ε�
		// 2. Connection ���
			con = getConn();
		// 3. ���� ���� ��ü ���
			StringBuilder updateLunch = new StringBuilder();
			updateLunch
			.append("       update lunch ")
			.append("       set lunch_name=?, ")
			.append("       		price=?, spec=? ");
			
			if(!luvo.getImg().equals("")) { // luvo ��ü�� ����� �̹����� �����Ѵٸ�
				updateLunch.append(", img=? ");
			} // end if
			
			updateLunch.append(" where lunch_code = ?");
			pstmt = con.prepareStatement(updateLunch.toString());
		// 4. ���ε� ������ �� ����
			pstmt.setString(1, luvo.getLunch_name());
			pstmt.setInt(2, luvo.getPrice());
			pstmt.setString(3, luvo.getSpec());
			
			int index = 4;
			if (!luvo.getImg().equals("")) {
				pstmt.setString(index++, luvo.getImg());
			} // end if
			pstmt.setString(index, luvo.getLunch_code());
			
//			if (!luvo.getImg().equals("")) {
//				pstmt.setString(4, luvo.getImg());
//				pstmt.setString(5, luvo.getLunch_code());
//			} else {
//				pstmt.setString(4, luvo.getLunch_code());
//			} // end else
			
		// 5. ���� ���� �� ��� ���
			int cnt = pstmt.executeUpdate();
			if( cnt == 1) {
				flag = true;
			} // end if
			
		} finally {
			
		// 6.
			if( pstmt != null ) { pstmt.close(); } // end if
			if( con != null ) { con.close(); } // end if
			
		} //end finally
		
		return flag;
	} // updateLunch
	
	
	public List<CalcVO> selectCalc(String date) throws SQLException{
		List<CalcVO> list = new ArrayList<CalcVO>();
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
		// 1. ����̹� �ε�
		// 2. Connection ���
			con = getConn();
		// 3. ���� ���� ��ü ���
			
			StringBuilder selectCalc = new StringBuilder();
			selectCalc
			.append("	select l.lunch_name , l.lunch_code, sum(o.quan) total, sum(o.quan)*l.price price	")
			.append("	from lunch l, ordering o	")
			.append("	where o.lunch_code = l.lunch_code ")
			.append("   and to_char(o.order_date,'yyyy-mm-dd') = to_char(to_date(?,'yyyy-mm-dd'),'yyyy-mm-dd')	")
			.append(	"	and		o.status='Y'		")
			.append("	group by l.lunch_name, l.lunch_code, l.price	")
			.append("	order by l.lunch_code 	");
			
			pstmt = con.prepareStatement(selectCalc.toString());
		// 4. ���ε� ������ �� ����
			pstmt.setString(1,  date);
		// 5. ���� ���� �� ��� ���
			rs = pstmt.executeQuery();

			CalcVO cvo = null;
			while(rs.next()) {
				cvo = new CalcVO(rs.getString("lunch_code"), rs.getString("lunch_name"), rs.getInt("price"), rs.getInt("total"));
				list.add( cvo );
			} // end while
			
		} finally {
		// 6.
			if( rs != null ) { rs.close(); } // end if
			if( pstmt != null ) { pstmt.close(); } // end if
			if( con != null ) { con.close(); } // end if
		} //end finally
			
		return list;
	} // selectCalc
	
	
	/**
	 * ���� 13�� ������ �ֹ� ��Ȳ
	 * @return
	 * @throws SQLException
	 */
	public List<OrderVO> selectOrderList() throws SQLException{
		List<OrderVO> list = new ArrayList<OrderVO>();
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;	
		
		try {
		// 1. ����̹� �ε�
		// 2. Connection ���
			con = getConn();
		// 3. ���� ���� ��ü ���
			StringBuilder selectOrder = new StringBuilder();
			selectOrder
			.append("	select		o.order_num, l.lunch_code, l.lunch_name, o.order_name, o.quan, l.price * o.quan price, to_char(o.order_date, 'yyyy-mm-dd hh:mi:ss') order_date,	")
			.append("	o.phone, o.ip_address, o.status, o.request	")
			.append("	from		lunch l, ordering o		")
			.append("	where 	o.lunch_code = l.lunch_code	")
			.append("	and 		to_char(order_date, 'yyyy-mm-dd') = to_char(sysdate, 'yyyy-mm-dd')	")
//			.append("	and to_char(order_date, 'hh24') <= 13	")
			.append("	order by o.order_num asc	");
			
			pstmt = con.prepareStatement(selectOrder.toString());
		// 4. ���ε� ������ �� ����
		// 5. ���� ���� �� ��� ���
			rs = pstmt.executeQuery();
			
			OrderVO ovo = null;
			while( rs.next() ) {
				ovo = new OrderVO(rs.getString("order_num"), rs.getString("lunch_code"), 
								rs.getString("lunch_name"),	rs.getString("order_name"), 
								rs.getString("order_date"), rs.getString("phone"), 
								rs.getString("ip_address"), rs.getString("status"),
								rs.getInt("quan"), rs.getInt("price"), rs.getString("request"));
				list.add(ovo);
			} // end while
		} finally {
		// 6. ���� ����
			if( rs != null ) { rs.close(); } // end if
			if( pstmt != null ) { pstmt.close(); } // end if
			if( con != null ) { con.close(); } // end if
		} // end finally
		
		return list;
	} // selectOrderList
	
	/**
	 * ���ö� ���� �Ϸ� ������ ȣ��Ǿ� �ش� �ֹ� ���ö��� �ϼ� ���¸� �����ϴ� ��.
	 * @param orderNum
	 * @return
	 */
	public boolean updateStatus(String orderNum) throws SQLException{
		boolean flag = false;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
		// 1. ����̹� �ε�
		// 2. Connection ���
			con = getConn();
		// 3. ������ ������ü ���
			String updateOrder = "UPDATE ORDERING SET STATUS = 'Y' WHERE ORDER_NUM=?"; 
			pstmt = con.prepareStatement(updateOrder);
		// 4. ���ε� ������ �� ����
			pstmt.setString(1, orderNum);
		// 5. ���� ���� �� ��� ���
			int cnt = pstmt.executeUpdate();
			if ( cnt == 1 ) {
				flag = true;
			} // end if
		} finally {
		// 6. ���� ����
			if( pstmt != null ) { pstmt.close(); } // end if
			if( con != null ) { con.close(); } // end if
		} // end finally
		
		return flag;
	} // updateStatus
	
	
	
	/**
	 * �ֹ��� ���ö��� �����ϴ� ��.
	 * @param orderNum
	 * @return
	 */
	public boolean deleteOrder(String orderNum) throws SQLException{
		boolean flag = false;
		
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
		// 1. ����̹� �ε�
		// 2. Connection ���
			con = getConn();
		// 3. ������ ������ü ���
			String updateOrder = "DELETE FROM ORDERING WHERE ORDER_NUM=?"; 
			pstmt = con.prepareStatement(updateOrder);
		// 4. ���ε� ������ �� ����
			pstmt.setString(1, orderNum);
		// 5. ���� ���� �� ��� ���
			int cnt = pstmt.executeUpdate();
			if ( cnt == 1 ) {
				flag = true;
			} // end if
		} finally {
		// 6. ���� ����
			if( pstmt != null ) { pstmt.close(); } // end if
			if( con != null ) { con.close(); } // end if
		} // end finally
		
		return flag;
	} // deleteOrder
	
	public String selectRequest(String orderNum) throws SQLException{
		String request = "";
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
		// 1. ����̹� �ε�
		// 2. Connection ���
			con = getConn();
		// 3. ���� ���� ��ü ���
			String selectRequest = "SELECT REQUEST FROM ORDERING WHERE ORDER_NUM = ?";
			pstmt = con.prepareStatement(selectRequest);
		// 4. ���ε� ������ �� ����
			pstmt.setString(1, orderNum);
		// 5. ���� ���� �� ��� ���
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				request = rs.getString("request");
			} // end if
			
			if(rs.getString("request") == null) {
				request = "";
			} // end if
			
		} finally {
		// 6. ���� ����
			if ( rs != null ) { rs.close(); } // end if
			if ( pstmt != null ) { pstmt.close(); } // end if
			if ( con != null ) { con.close(); } // end if
		} // end finally
		
		
		
		return request;
	} // selectRequest
	
//	public boolean checkRequestOrder(String orderNum) throws SQLException{
//		boolean checkRequest = false;
//		
//		String request = "";
//		Connection con = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		
//		try {
//		// 1. ����̹� �ε�
//		// 2. Connection ���
//			con = getConn();
//		// 3. ���� ���� ��ü ���
//			String selectRequest = "SELECT REQUEST FROM ORDERING WHERE ORDER_NUM = ?";
//			pstmt = con.prepareStatement(selectRequest);
//		// 4. ���ε� ������ �� ����
//			pstmt.setString(1, orderNum);
//		// 5. ���� ���� �� ��� ���
//			rs = pstmt.executeQuery();
//			
//			if ( rs.next() ) {
//				checkRequest = rs.getString("request").isEmpty();
//			} // end if
//			
//		} finally {
//		// 6. ���� ����
//			if ( rs != null ) { rs.close(); } // end if
//			if ( pstmt != null ) { pstmt.close(); } // end if
//			if ( con != null ) { con.close(); } // end if
//		} // end finally
//		return checkRequest;
//	}
	
} // class
