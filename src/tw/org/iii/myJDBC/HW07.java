package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/*		20180826PM1 SQL Command Update 00:56:00
 * 
 */

public class HW07 {

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		
		//	利用 Properties 物件實體儲存屬性
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		
		// SQL Command
		//String del = "DELETE FROM 02_hw02_01 where name = 'Mary'";
		String update = "UPDATE 02_hw02_01  SET name = 'Sean' where name = 'Mary'";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DriverManager.getConnection(url, prop);
			pstmt = conn.prepareStatement(update);
			pstmt.executeUpdate();
			System.out.println("資料變動更新OK");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException : " + e);
		} finally {
			try {
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
				System.out.println("關閉連線錯誤");
			}
		}
	}

}
