package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/*		20180826PM1 getConnection V4 00:52:28
 * 
 * 		第三招再加上自動關閉
 * 		=> 為什麼可以使用 autoclose => 因 Connection 有實作autocloseable介面
 * 		=> autoclose可以寫兩段用分號;來區隔
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

public class HW05 {

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		
		//	利用Properties儲存屬性
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		
		//	SQL Command
		String sqlcommand = "INSERT INTO 02_hw02_01 (name, tel, birthday) values ('Obama', '6666777', '1970-11-28')";
		
		//	使用的是 java.sql.Connection
		try (	Connection conn = DriverManager.getConnection(url, prop);
				PreparedStatement pstmt = conn.prepareStatement(sqlcommand)
				) {
					pstmt.executeUpdate();
					System.out.println("插入指令執行成功");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQLException : " + e);
		}
	}

}
