package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/*		20180901AM2
 * 
 * 		帳號密碼登入_用程式端驗證帳號是否重複
 * 
 * 		1.	建立程式架構
 * 			連線資料庫-->
 * 			如果輸入的帳號資料沒有重複-->新增一筆資料
 * 			如果有重複-->告知資料重複
 * 			=> 判斷是否重複獨立成一個方法
 * 		2.	先將連線與判斷架構完成
 * 		3.	處理判別是否有重複的方法
 * 			a.	外面主程式已經開啟一個連線，該方法就繼續用那個連線
 * 				=>傳入參數connection
 * 			b.	方法內的例外要不要拋出?
 * 				=> 要throw Exception 而非在該程式內catch
 * 				=> 因為在裡面處理的話，而這個程式的true/false不就無法判斷了
 * 				=> 所以有例外應該拋出傳回主程式，讓主程式處理。
 * 		
 * 		注意：一般密碼設定不會使用名碼	=> 不安全
 */

public class HW16 {

	public HW16() {
		//	待會登入成功後要新增的資料
		String name = "john456", tel = "4321765";
		String birthday = "1981-01-06";
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		String sqlcmd = "INSERT INTO 02_hw02_01 (name, tel, birthday) VALUES (?, ?, ?)";
		
		try {
			conn = DriverManager.getConnection(url, prop);
			if (!isDataRepeat(name, tel, conn)) {
				pstmt = conn.prepareStatement(sqlcmd);
				pstmt.setString(1, name);
				pstmt.setString(2, tel);
				pstmt.setString(3, birthday);
				pstmt.executeUpdate();
				
				System.out.println("更新成功");
			} else {
				System.out.println("資料重複");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("資料庫連線錯誤");
		} finally {
			try {
				if (result != null) result.close();
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("關閉連線錯誤");
			}
		}
		
	}
	
	//	判斷使用者是否重複
	static boolean isDataRepeat(String name, String tel, Connection conn) throws SQLException {
		//	傳入account & conn 讓此方法可以存取資料庫
		//	為什麼拋出例外?	=> 如果這裡出現例外，然後處理完，那這個方法的回傳值true/false就沒用
		//	拋出讓上面的程式去接，才會達到我們要的效果	(拋出->有狀況)
		String sqlcmd2 = "SELECT COUNT(*) AS count FROM 02_hw02_01 WHERE name = ? AND tel = ?";
		//	如果有重複資料筆數就會大於零
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd2);
		pstmt.setString(1, name);
		pstmt.setString(2, tel);
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		int count = rs.getInt("count");	//	取得資料筆數以供比較
		System.out.println("count : " + count);
		return count != 0; //	回傳true/false
	}
	
	public static void main(String[] args) {
//		new HW16();
	}

}
