package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/*		20180826PM1 SQL command 刪除語法	00:56:00
 * 
 */

public class HW06 {

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		
		//	利用 Properties物件實體儲存屬性
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		
		//	SQL command
		//String del = "DELETE FROM 02_hw02_01 where id = 3";
		String del = "DELETE FROM 02_hw02_01 where name = 'Mary'";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DriverManager.getConnection(url, prop);
			pstmt = conn.prepareStatement(del);
			pstmt.execute();
			System.out.println("刪除指令執行OK");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException : " + e);
		} finally {
			try {
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
				System.out.println("連線資源關閉OK");
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("關閉連線錯誤");
			}
		}
	}

}
