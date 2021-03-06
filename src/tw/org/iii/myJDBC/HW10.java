package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/*		20180826PM2 SQL Command Query 00:14:55
 * 		刪除語法練習
 * 		Interface PreparedStatement => 可用來對付 SQL Injection
 * 		https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html
 */

public class HW10 {

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		
		//	利用 Properties儲存屬性
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		
		//	SQL Command
		String delete = "DELETE FROM 02_hw02_01 WHERE id = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		int intResult = 0;
		try {
			conn = DriverManager.getConnection(url, prop);
			pstmt = conn.prepareStatement(delete);
			pstmt.setString(1, "5");
			intResult = pstmt.executeUpdate();
			if (intResult == 1) {
				System.out.println("資料刪除成功： " + intResult);
			} else {
				System.out.println("查無此筆資料： " + intResult);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("資料庫發生錯誤");
		} finally {
			try {
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("關閉連線錯誤");
			}
		}
	}

}
