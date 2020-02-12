package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/*		20180826PM1	建立JDBC URL連線
 * 		有些伺服器會保持持續性的連接，所以要瞭解到伺服器的特性。
 * 
 * 		步驟：
 * 		註冊 Driver 實作物件
 * 		建立連線 => 取得Connection的物件實體
 * 		關閉 Connection 實作物件
 * 
 * 		注意：要正確import的package => java.sql.Connection
 */

public class HW02 {

	public static void main(String[] args) {
		/*	預設port 可省略(:3306) / iii (資料庫名)
		 * 	port號要注意是否正確
		 * 	https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
		 * 	url格式各資料庫大同小異，注意差異部分即可
		 * 	
		 * 	MySQL的JDBC URL撰寫方式如下
		 * 	jdbc:mysql://主機名稱:連接埠/資料庫名稱?serverTimezone=CST&參數=值&參數=值
		 * 	
		 * 	jdbc:mysql://localhost:3306/Local_Instance_MYSQL80?user=root&password=root
		 * 
		 * 	有時候會將JDBC URL寫在XML設定檔中，此時參數值不能直接在XML寫 & 符號，而必須改寫為&amp;替代字元。如下：
		 * 	jdbc:mysql://localhost:3306/Local_Instance_MYSQL80?user=root&passqord=root&
		 * 	useUnicode=true&amp;characterEncoding=UTF8
		 */
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=CST&user=root&password=root";
		
		//	SQL Command
		String sqlcommand = "insert into 02_hw02_01 (name, tel, birthday)" + 
											"values ('Jay', '1234567', '1981-01-01')";
		
		//	使用的是 java.sql.Connection
		try {
			//	建立連線 (透過字串)
			Connection conn = DriverManager.getConnection(url);
			//	產生 statement 物件才可執行SQL ==> 回傳 statement 物件實體
			Statement stmt = conn.createStatement();
			
			stmt.execute(sqlcommand); // 執行SQL指令
			
			conn.close();
			/*	用戶端的關閉 (不同的伺服器特性不同，例如mysql session操作結束後會自動關閉)
			 * 	有些為了效能，當有連續多次的連線時，會使用相同connection => 保持持續性的連接
			 * 	mysql 要下指令它就會保持持續性的連接
			 * 	所以要注意伺服器的特性
			 * 	關閉也可使用自動關閉語法 (像串流)
			 */
			System.out.println("Connection OK");
			
		} catch (SQLException e) {
			e.printStackTrace();
//			System.out.println("SQLException");
		}
	}

}
