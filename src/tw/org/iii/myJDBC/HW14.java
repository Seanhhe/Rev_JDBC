package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/*		20180904AM2	存取資料庫的資料
 * 
 */

public class HW14 {

	public HW14() {
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		String sqlcmd = "SELECT COUNT(*) AS count FROM forestroadlist";
		
		try {
			conn = DriverManager.getConnection(url, prop);
			pstmt = conn.prepareStatement(sqlcmd);
			result = pstmt.executeQuery();
			result.next();	//	從查詢到的資料最前面一筆開始往後讀取。
			
			//	取回傳值
			int numberCounts = result.getInt("count");	//	取得回傳筆數
			String number = result.getString("count");	//	取得回傳筆數，並存成字串。
			System.out.println("numberCounts : " + numberCounts);
			System.out.println("number : " + number);
			//System.out.println(result.getInt(1));	//	用column index(從1開始)
			
			//	資料分頁處裡
			int rpp = 20;	//	每頁看幾筆
			int page = 2;	//	第幾頁
			int start = (page - 1) * rpp;	//	起始位置
			
			// 試試看不重新修改pstmt=conn.prepareStatement(sqlcmd)，能否成功執行新的sql指令
			result = pstmt.executeQuery("SELECT id, forestRoadName FROM forestroadlist LIMIT " + start + ", " + rpp);
			while(result.next()) {
				int id = result.getInt("id");
				String forestRoadName = result.getString("forestRoadName");
				System.out.println(id + " : " + forestRoadName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("資料庫發生錯誤");
		} finally {
			try {
				if (result != null) result.close();
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("關閉連線錯誤");
			}
		}
		
	}
	
	public static void main(String[] args) {
		new HW14();
	}

}
