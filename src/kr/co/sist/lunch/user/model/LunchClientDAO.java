package kr.co.sist.lunch.user.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.co.sist.lunch.user.vo.LunchDetailVO;
import kr.co.sist.lunch.user.vo.LunchListVO;
import kr.co.sist.lunch.user.vo.OrderAddVO;
import kr.co.sist.lunch.user.vo.OrderInfoVO;
import kr.co.sist.lunch.user.vo.OrderListVO;
import oracle.jdbc.internal.OracleTypes;

/**
 *	���ö� �ֹ��ڿ� ���� DB ó��  
 * @author owner
 *
 */
public class LunchClientDAO {
	private static LunchClientDAO lc_dao;
	
	private LunchClientDAO() {
		// 1. ����̹� �ε�
		try {
			Class.forName("oracle.jdbc.OracleDriver");
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} // end catch
		
	} // LunchClientDAO
	
	public static LunchClientDAO getInstance() {
		if( lc_dao == null ) {
			lc_dao = new LunchClientDAO();
		} // end if
		return lc_dao;
	} // getInstance
	
	private Connection getConn() throws SQLException {
		Connection con = null;
		// 2. Connection ��� 
			String url = "jdbc:oracle:thin:@211.63.89.133:1521:orcl";
			String id = "scott";
			String pass = "tiger";
		
			con = DriverManager.getConnection(url, id, pass);
		return con;
	} // getConn
	
	/**
	 * DB�� �߰��� ��� ���ö� ��� ��ȸ
	 * @return
	 * @throws SQLException
	 */
	public List<LunchListVO> selectLunchList() throws SQLException{
		List<LunchListVO> list = new ArrayList<LunchListVO>();
		
		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
		// 1. ����̹� �ε� 
		// 2. Connection ���
			con = getConn();
		// 3. ������ ���� ��ü ���
			String selectLunch = 
					"SELECT LUNCH_CODE, LUNCH_NAME, IMG, SPEC FROM LUNCH ORDER BY LUNCH_CODE DESC";
			pstmt = con.prepareStatement(selectLunch);
		// 4. ���ε� ������ �� ���� (�� �ʿ� X)
		// 5. ���� ���� �� ��� ���
			rs = pstmt.executeQuery();
			
			LunchListVO llv = null;
			
			while( rs.next() ) {
				llv = new LunchListVO(rs.getString("IMG"), rs.getString("LUNCH_CODE"), rs.getString("LUNCH_NAME"), rs.getString("SPEC"));
				list.add(llv);
			} // end while
			
		} finally {
		// 6.
			if( rs != null ) { rs.close(); } // end if
			if( pstmt != null ) { pstmt.close(); } // end if
			if( con != null ) { con.close(); } // end if
		} // end finally
		
		return list;
	} // selectLunchList()
	
	public LunchDetailVO selectDetailLunch(String lunchCode) throws SQLException{
		LunchDetailVO ldvo = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
		// 1. ����̹� �ε�
		// 2. Connection ���
			con = getConn();
		// 3. ���� ���� ��ü ����
			String selectLunch = "SELECT IMG, LUNCH_NAME, SPEC, PRICE FROM LUNCH WHERE LUNCH_CODE=?";
			pstmt = con.prepareStatement(selectLunch);
		// 4. ���ε� ������ �� ����
			pstmt.setString(1, lunchCode);
		// 5. 
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				ldvo = new LunchDetailVO(lunchCode, rs.getString("lunch_name"), rs.getString("spec"), rs.getString("img"), rs.getInt("price"));
			} // end if
			
		} finally {
		// 6.
			if( rs != null ) { rs.close(); } // end if
			if( pstmt != null ) { pstmt.close(); } // end if
			if( con != null ) { con.close(); } // end if
		} // finally
		
		return ldvo;
	} // LunchDetailLunch
	
	public void insertOrder(OrderAddVO oavo) throws SQLException{
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
		// 1. ����̹� �ε�
		// 2. Connection ���
			con = getConn();
		// 3. ���� ���� ��ü ���
			String insertOrder = "insert into ordering(order_num, quan, order_name, phone, ip_address, lunch_code, request) values(order_code, ?, ?, ?, ?, ?, ?) ";
			pstmt = con.prepareStatement(insertOrder);
		// 4. ���ε� ���� �� ����
			pstmt.setInt(1, oavo.getQuan());
			pstmt.setString(2, oavo.getOrderName());
			pstmt.setString(3, oavo.getPhone());
			pstmt.setString(4, oavo.getIpAdress());
			pstmt.setString(5, oavo.getLunchCode());
			pstmt.setString(6, oavo.getRequest());
		// 5. ���� ���� �� ��� ���
			pstmt.executeUpdate();
		} finally {
		// 6.
			if( pstmt != null ) { pstmt.close(); } // end if
			if( con != null ) { con.close(); } // end if
		} // end finally
	} // insertOrder
	
	public List<OrderListVO> selectOrderList(OrderInfoVO oivo) throws SQLException{
		List<OrderListVO> list = new ArrayList<OrderListVO>();
		Connection con = null;
		CallableStatement cstmt = null;
		ResultSet rs = null;
		
		try {
		// 1. ����̹� �ε�
		// 2. Connection ���
			con = getConn();
		// 3. ���� ���� ��ü ���
			cstmt = con.prepareCall(" { call lunch_order_select(?,?,?) } ");
		// 4. ���ε� ������ �� ����
			
			//in parameter
			cstmt.setString(1, oivo.getOrderName());
			cstmt.setString(2, oivo.getOrderTel());
			//out parameter
			cstmt.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);
		// 5. ���� ���� ( ���ν��� ���� )
			cstmt.execute();
			// out parameter�� ����� �� �ڹ��� ����(rs)�� ����
			rs = (ResultSet)cstmt.getObject(3); // CallableStatement Ÿ���� ResultSet Ÿ������ ����ȯ
			
			OrderListVO olvo = null;
			
			while(rs.next()) { // ��ȸ�� �����( 1 �� )�� rs�� ����� �� ����
				// OrderListVO Ŭ������ ��ü�� ���Ӱ� �����Ͽ�
				olvo = new OrderListVO(rs.getString("lunch_name"), 
												rs.getString("order_date"), 
												rs.getInt("quan"));
				
				// ������ ��ü�� list�� �Ҵ��Ѵ�.
				list.add(olvo);
			} // end while
			
		} finally {
		// 6.
			if( rs != null ) { rs.close(); } // end if
			if( cstmt != null ) { cstmt.close(); } // end if
			if( con != null ) { con.close(); } // end if
		}
		return list;
	} // selectOrderList
	
} // class
