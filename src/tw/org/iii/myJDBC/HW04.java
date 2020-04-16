package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/*		20180826PM1 getConnection V3 00:42:22
 * 
 * 		建立連線 (透過字串) 第三招	[推薦!!]
 * 		getConnection(String url, Properties info)
 * 			=> properties
 * 				=> https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-configuration-properties.html
 * 				=> 看一下 Properties and Descriptions (可設定的key)
 * 			=> 彈性更大，自訂屬性設定
 * 		Properties
 * 		=> https://docs.oracle.com/javase/7/docs/api/java/util/Properties.html
 * 		=> 實作Map介面 (key, value)
 * 		=> 都是字串
 * 		=> setProperty：設定
 * 		=> getProperty：取值
 * 
 * 		注意 import 的 package => java.sql.Connection
 */

public class HW04 {

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		
		//	利用Properties儲存屬性 (同HashMap用法)
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		
		//	SQL Command
		String sqlcommand = "INSERT INTO 02_hw02_01 (name, tel, birthday) values ('Mary', '3333888', '2012-11-28')";
		
		//	使用的是 java.sql.Connection
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			//	建立連線 (透過字串)
			conn = DriverManager.getConnection(url, prop);
			//	產生 PreparedStatement ==> 回傳 PreparedStatement物件實體
			pstmt = conn.prepareStatement(sqlcommand);
			pstmt.executeUpdate();	// 執行插入的資料庫指令
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException 51 : " + e);
		} finally {
			/*	用戶端的關閉連線 (不同的伺服器特性不同，例如mysql session操作結束後會自動關閉)
			 * 	有些為了效能，當有連續多次的連線時，會使用相同connection => 保持持續性的連接
			 * 	mysql 要下指令他就會保持持續性的連接
			 * 	所以要注意伺服器的特性
			 *  關閉也可使用自動關閉語法(像串流)
			 */
			try {
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
				System.out.println("順利關閉連線資源62");
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("SQL關閉連線錯誤65" + e1);
			}
		}
	}

}
