package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.json.JSONStringer;
import org.json.JSONWriter;

/*		20180901AM2 JDBC
 * 		
 * 		從資料庫讀取資料並以JSON格式輸出 (package org.json)
 * 		參考API：https://stleary.github.io/JSON-java/index.html?org/json/package-summary.html
 * 		=> JSONStringer ()
 * 		=> JSONWriter (I/O)
 * 
 * 		設計程式觀念：
 * 		=> 使用JSON前先思考為何要JSON？
 * 			團隊開發、網際網路、程式需求
 */

public class HW15 {
	public HW15() {
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet result = null;
		String sqlcmd = null;
		
		try {
			conn = DriverManager.getConnection(url, prop);
			sqlcmd = "SELECT * FROM forestroadlist ORDER BY id DESC";	//	LIMIT 0, 10
			pstmt = conn.prepareStatement(sqlcmd);
			result = pstmt.executeQuery();
			
			//	如何輸出JSON？	利用org.json
			//	JSON 最後輸出也是字串	=> 利用 JSONStringer
//			JSONStringer js = new JSONStringer();	//	源頭：代表整個JSON格式
//			JSONWriter jw = js.array();		//	如果最外圈的格式是陣列 => 由array產生，傳回JSONWriter => writer就可以拿來做輸出的動作
//			jw.endArray();	//	處理完畢要結束array
			
//			JSONWriter jw = js.array();	//	開始放入JSON陣列
//			jw.object();	//	開始放入JSON物件
//				jw.key("key1").value("value1");
//				jw.key("key2").value("value2");
//			jw.endObject();	//	結束JSON物件
//			jw.endArray();	//	結束JSON陣列
//			System.out.println("jw : " + jw);
			
			//	------分隔線------
			JSONStringer js = new JSONStringer();
			JSONWriter jw = js.array();
			
//			if (result.next()) {	//	也可以這樣寫
			while (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("forestRoadName");
				String startLocation = result.getString("startLocation");
//				System.out.println("測試：" + id + " : " + name + " : " + startLocation);
				
				jw.object();
					jw.key("編號").value(id);
					jw.key("林道名稱").value(name);
					jw.key("起始入口").value(startLocation);
				jw.endObject();
			}
			js.endArray();
			System.out.println("js: " + js);
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
	
	public static void main(String[] args) {
		new HW15();
	}

}
