package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/*		20180826PM1 SQL Command Query 01:09:00
 * 		
 * 		查詢後回傳的資料如何處理
 * 		=> 透過pointer指標把每一列的資料讀進來
 * 		=> 欄位標題，可以更換也可以計算有幾個欄位
 */

public class HW08 {

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		
		// 利用 Properties 儲存屬性
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		
		// SQL Command
		// => 回傳的會依據指令要求的欄位順序
		// => 也同樣可以使用SQL的函數呼叫計算欄位
		//		SELECT COUNT(*) FROM 02_hw02_01;
		// String query = "SELECT * FROM 02_hw02_01";
		String query = "SELECT id, name AS Fname, tel, birthday FROM 02_hw02_01 WHERE id = 2";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(url, prop);
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			
			if (rs.next()) { // 若為true，則代表有查到資料
				/*	4 columns (column index)
				 * 	用 getString 因為是通過通訊協定，所以基本上資料都是字串型別
				 * 	除非是要運算在使用 int 等其他型別
				 */
//				String c1 = rs.getString(1);
//				String c2 = rs.getString(2);
//				String c3 = rs.getString(3);
//				String c4 = rs.getString(4);
				
				// 改用 column label 維護性較高
				// 這裡的 Label 是搭配查詢出來的結果：若 SQL Command 有另外設 Label，則要與它相同
				String c1 = rs.getString("id");
				String c2 = rs.getString("Fname");
				String c3 = rs.getString("tel");
				String c4 = rs.getString("birthday");
				System.out.println(c1 + " : " + c2 + " : " + c3 + " : " + c4);
			}
			System.out.println("查詢指令執行OK");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("資料庫錯誤");
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("關閉資料庫連線錯誤");
			}
		}
	}

}
