package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*		20180826PM1 getConnection v2 00:38:22
 * 
 * 		建立連線 (透過字串) 第二招
 * 		=> getConnetion(String url, String user, String password)
 * 
 * 		建立連線 => 取得Connection的物件實體
 * 		
 * 		注意import的package => java.sql.Connection
 */

public class HW_03 {

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		//	將帳號與密碼獨立寫出
		String user = "root";
		String password = "root";
		
		// SQL Command
		String sqlcommand = "INSERT INTO 02_hw02_01 (name, tel, birthday) values ('Jolin', '7778888', '2015-03-28')";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		// 使用的是 java.sql.Connection
		try {
			// 建立連線 (透過字串)
			conn = DriverManager.getConnection(url, user, password);
			// 產生preparedStatement ==> 回傳preparedStatement的物件實體
			pstmt = conn.prepareStatement(sqlcommand);
			pstmt.executeUpdate();
			System.out.println("Connection OK");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e);
		} finally {
			//	關閉連線，釋放資源
			try {
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
				//	用戶端的關閉連線 (不同伺服器特性不同，例如mysql session操作結束後會自動關閉)
				//	有些為了效能，當有連續多次的連線時，會使用相同connection => 保持持續性的連接
				//	mysql 要下指令他就會保持持續性的連接
				//	所以要注意伺服器的特性
				//	關閉也可使用自動關閉語法(像串流)
			} catch (SQLException e) {
				System.out.println("SQLException = " + e);
			}
		}
	}

}
