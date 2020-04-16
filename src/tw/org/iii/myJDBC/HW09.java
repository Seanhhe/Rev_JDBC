package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/*		20180826PM2 SQL Command Query 00:03:00
 * 		Interface PreparedStatement => 可用來對付 SQL Injection
 * 		https://docs.oracle.com/javase/7/docs/api/java/sql/PreparedStatement.html
 */

public class HW09 {

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		
		//	利用 Properties儲存屬性
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		
		//	SQL Command
		String query = "INSERT INTO 02_hw02_01 (name, tel, birthday) VALUES (?, ?, ?)";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		int intResult = 0;
		try {
			conn = DriverManager.getConnection(url, prop);
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, "Mary" + (int)(Math.random()*50));
			pstmt.setString(2, "080108" + (int)(Math.random()*100));
			pstmt.setString(3, "2008-08-" + (int)(Math.random()*10));
			
			intResult = pstmt.executeUpdate();
			
			System.out.println("資料庫已更新：" + intResult);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("資料庫錯誤");
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
