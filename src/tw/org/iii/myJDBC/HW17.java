package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/*		20180901PM1
 * 		帳戶驗證(可用於帳號密碼驗證)通過後，建立該帳戶的物件實體
 * 
 * 		基本流程
 * 		1.	連線資料庫
 * 		2.	檢查name, tel
 * 		3.	驗證通過後印出並產生物件實體
 * 
 * 		注意程式設計的邏輯
 */

public class HW17 {

	public HW17() {
		String name = "Sean";
		String tel = "1231234";
		//String birthday = "1981-02-07";
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, prop);
			Member member = null;
			if ((member = checkMember(name, tel, conn)) != null) {
				System.out.println("歡迎登入：" + member.name + " !");
			} else {
				System.out.println("登入失敗");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("資料庫發生錯誤");
		} finally {
			try {
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("關閉連線錯誤");
			}
		}
	}
	
	static Member checkMember(String name, String tel, Connection conn) throws SQLException {
		String sqlcmd = "SELECT * FROM 02_hw02_01 WHERE name = ? AND tel = ?";
		//	如果要比對密碼，應先找到該帳號，再撈出密碼(非明碼)比對。
		PreparedStatement pstmt = conn.prepareStatement(sqlcmd);
		pstmt.setString(1, name);
		pstmt.setString(2, tel);
		ResultSet result = pstmt.executeQuery();
		if (result.next()) {
			return new Member(result.getString("name"), result.getString("tel"));
		} else {
			return null;
		}
	}
	
	public static void main(String[] args) {
		new HW17();
	}
}
	
class Member {
	String name, tel;
	public Member(String name, String tel) {
		this.name = name;
		this.tel = tel;
	}
}


