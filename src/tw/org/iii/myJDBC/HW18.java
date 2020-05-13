package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/*		20180901PM1	00:18:30
 * 		
 * 		搜尋到該筆資料後，修改ResultSet後，即時更新資料庫內的資料。
 * 		=>	不透過傳統取其ID之後下SQL更新語法的方式	(避免不停的傳送SQL command給資料庫)
 * 		=>	ResultSet.TYPE_FORWARD_ONLY
 * 			ResultSet.CONCUR_UPDATEABLE
 * 		<java.sql Interface ResultSet>
 * 
 * 		注意：Java有提供ResultSet這個方法，但是該Connection不見得有支援，
 * 		所以要先取得DB的metadata確認。
 */

public class HW18 {
	public HW18() {
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		String sqlcmd = "SELECT * FROM 02_hw02_01 WHERE id = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		
		try {
			conn = DriverManager.getConnection(url, prop);
			DatabaseMetaData metaData = conn.getMetaData();	//	取得管理資料庫的資訊、如資料表、欄位名、SQL語法等
			/*	詢問資料庫是否支援 ResultSet 的同步
			 * 	(若未支援則需查詢官方文件是否有參數可調整)
			 * 	https://docs.oracle.com/javase/7/docs/api/java/sql/Connection.html#getMetaData()
			 * 
			 * 	https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html
			 */
			boolean isOK = metaData.supportsResultSetConcurrency(
					ResultSet.TYPE_FORWARD_ONLY, 
					ResultSet.CONCUR_UPDATABLE);
			System.out.println("isOK : " + isOK);
			
			//	preparedStatement 也要指定參數
//			pstmt = conn.prepareStatement(
//						sqlcmd, ResultSet.TYPE_FORWARD_ONLY, 
//						ResultSet.CONCUR_UPDATABLE);
//			pstmt.setInt(1, 1);
//			result = pstmt.executeQuery();
//			result.next();
//			System.out.println("result.getString(\"name\") : " + result.getString("name"));
//			result.updateString("name", "Tsai Yi Lin");	//	設定要修改的欄位及內容。
//			result.updateRow();
//			System.out.println("name 欄位修改 OK");
			
			//	--------同時修改所有member的tel (不用重複下SQL語法)
			String sqlcmd02 = "SELECT * FROM 02_hw02_01";
			pstmt = conn.prepareStatement(sqlcmd02, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			result = pstmt.executeQuery();
//			while(result.next()) {
//				result.updateString("tel", "1234567890");
//				result.updateRow();
//				System.out.println("所有tel已修改");
//			}
			
			//	--------直接新增資料 (不用SQL語法)--------
//			result.moveToInsertRow();	//	指標移動到新增欄位的空白位置(資料表 最後一筆下面)
//			result.updateString("name", "hw18_2");
//			result.updateString("tel", "7654321");
//			result.updateString("birthday", "2000-01-01");
//			result.insertRow();		//	執行新增  (事實上是暫存??)
			
			//result.previous();	//	回到上一筆
			//result.deleteRow();	//	新增後不能馬上執行刪除 => 這裡砍的是新增前的最後一筆
			
			//--------目前ResultSet的指標位置--------
			result.last();	//	移動指標到最後一個欄位
			System.out.println(result.getRow() + " => 目前指標位置");	//	目前在最下面
			//result.previous();	//	回上一列
			//System.out.println(result.getRow());
			result.beforeFirst();	//	移到上面再重撈資料  ((標頭下的第一個欄位前Moves the cursor to the front of this ResultSet object, just before the first row.
			while(result.next()) {
				String id = result.getString("id");
				String name = result.getString("name");
				System.out.println(id + "：" + name);	//	顯示目前資料庫內的東西
				//result.deleteRow();	//	全砍 (最新增的資料若MySQL有apply後，就可以砍)
			}	//	離開迴圈時，是在沒有下一筆的位置
			
			System.out.println("--------_--------測試分隔線--------_--------");
			
			//result.deleteRow();		//	刪掉最新的那一筆
//			result.first();	//	移動指標到第一筆資料
//			System.out.println(result.getString("name"));
			result.last();	//	最後一筆資料
			System.out.println("id：" + result.getInt("id"));
//			result.deleteRow();		//	砍掉指標所在的資料，這裡是砍掉最後一筆。(被新增的那一筆)
			
//			result.absolute(2);
//			System.out.println("id：" + result.getInt("id"));
			//result.afterLast();		//	移動指標至最後一筆之後的那個空欄位，因為欄位值是null，若執行下一行就會出現SQLException
			//System.out.println(".afterLast()的ID：" + result.getInt("id"));
			//result.beforeFirst();		//	移動指標至第一筆之前的欄位，因為欄位值是null，所以執行下一行就會出現SQLException
			//System.out.println("result.beforeFirst()的ID : " + result.getInt("id"));
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("資料庫發生錯誤");
		} finally {
			try {
				if (result != null) result.close();
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("關閉連線發生錯誤");
			}
		}
		
	}
	
	public static void main(String[] args) {
		new HW18();
	}

}
