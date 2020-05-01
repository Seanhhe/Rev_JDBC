package tw.org.iii.myJDBC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*		20180901AM1 JDBC
 * 		將農委會Open Data資料下載至本機端的資料庫
 * 		https://data.coa.gov.tw/Service/OpenData/DataFileService.aspx?UnitId=151
 * 
 * 		設計流程：
 * 		1.	寫出基本架構
 * 		2.	分割問題
 * 		
 * 		**下載完成後資料庫顯示亂碼 => 字元編碼問題
 * 		MAMP\conf\mysql => 修改mysql組態檔
 * 		WorkBranch\在資料庫列表點選板手符號\即可設定編碼
 * 		https://dev.mysql.com/doc/refman/8.0/en/charset-applications.html
 */

/*		01. 編號 (int) = id
 *		02. 林區 (varchar(15)) = field
 *		03. 林道名稱 (varchar(15)) = forestRoadName
 *		04. 原編碼 (varchar(8)) = originId
 *		05. 公路專線 (varchar(15)) = roadLine
 *		06. 林道規格 (varchar(15)) = forestRoadSpec
 *		07. 林道種類 (varchar(15)) = forestRoadType
 *		08. 開設年份 (varchar(10)) = buildYear
 *		09. 縣市 (varchar(15)) = county
 *		10. 鄉鎮 (varchar(15)) = township
 *		11. 村里 (varchar(27)) = village
 *		12. 林道途經聚落 (varchar(25)) = passingSettlement
 *		13. 事業區林班 (varchar(50)) = forestClass
 *		14. 起點 (varchar(35)) = startLocation
 *		15. 終點 (varchar(30)) = endLocation
 *		16. 起點X坐標 (varchar(15)) = startXcoord
 *		17. 起點Y坐標 (varchar(15)) = startYcoord
 *		18. 終點X坐標 (varchar(15)) = endXcoord
 *		19. 終點Y坐標 (varchar(15)) = endYcoord
 *		20. 車行長度 (float) = vehicleTravelLength
 *		21. 步行長度 (float) = walkingLength
 *		22. 中斷長度 (float) = breakLength
 *		23. 總長度 (float) = totalLength
 *		24. A.C鋪面 (float) = acPaving
 *		25. P.C鋪面 (float) = pcPaving
 *		26. 碎石面或土石鋪面 (float) = gravelSoilPaving
 *		27. 集水區 (varchar(20)) = catchment
 *		28. 子集水區 (varchar(20)) = subcatchment
 *		29. 進入林班地里程 (varchar(30)) = mileage
 *		30. 備註 (varchar(45)) = remarks
 *	
 *	(共30個欄位)
 *	
 *	中文名稱 | 型別 | 欄位名稱
 * 
 */

public class HW13 {
	
	private static String fetchOpenData() {
		//	撈資料
		String result = null;
		String link = "https://data.coa.gov.tw/Service/OpenData/DataFileService.aspx?UnitId=151";
		
		try {
			//	要有遠端的URL; 但他只是個字串，要呼叫其他的動作/物件去啟動 (就像執行緒也要start)
			URL url = new URL(link);
			//	建立連線
			HttpsURLConnection urlConn = (HttpsURLConnection) url.openConnection();
			urlConn.connect();	// 啟動連線
			
			//	接收資料 (串流是以byte為單位，目前的資料是文字，所以使用 BufferedReader )
			//	InputStream-->InputStreamReader-->BufferedReader
			BufferedReader bufReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));
			
			//	讀資料; 是文字資料，需要一列一列接上
			//	參考：http://puremonkey2010.blogspot.com/2011/11/java-string-stringbuffer-stringbuilder.html
			String tempLine;	// 暫存讀進來的那一列
			StringBuffer sbuf = new StringBuffer();	// 一列一列接上
			while((tempLine = bufReader.readLine()) != null) {
				/*	為什麼寫while迴圈
				 * 	來源資料(頁面原始碼)有換列格式(農委會openData)
				 * 	一般來說，如果是單列無換列格式，可不用寫while迴圈，一個readLine()就結束。
				 * 	複習：串流下載的是檔案 (原始碼)
				 */ 
				sbuf.append(tempLine);	//	tempLine 一列一列的append到sbuf裡
			}
			bufReader.close();
			result = sbuf.toString();	//	把裝完東西的 sbuf 轉 String 輸出。
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("URL異常");
			// return null;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("串流異常");
			// return null;
		}
		return result;
	}
	
	//	把資料放到資料庫
	private static void toMyDB(String json) {
		//	1.	先解析JSON格式=>解析成功才能入到資料庫	(將JSON String解析為JSON Array)
		JSONArray root = new JSONArray(json);
		System.out.println("是否解析成功？ " + root.length());	// 確認是否有解析成功
		
		//	3.	建立資料庫連線
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String jdbcUrl = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		String sqlCmd = "INSERT INTO forestroadlist (id, field, forestRoadName, originId, roadLine, forestRoadSpec, forestRoadType, buildYear, county, township, village, passingSettlement, forestClass, startLocation, endLocation, startXcoord, startYcoord, endXcoord, endYcoord, vehicleTravelLength, walkingLength, breakLength, totalLength, acPaving, pcPaving, gravelSoilPaving, catchment, subcatchment, mileage, remarks) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		//	2.	開始解析，用for迴圈尋訪陣列中每個JSON Object物件
		try {
			conn = DriverManager.getConnection(jdbcUrl, prop);
			pstmt = conn.prepareStatement(sqlCmd);
			String originId = "";
			String roadLine = "";
			String remarks = "";
			Float gravelSoilPaving;
			for (int i = 0; i < root.length(); i++) {
			//for (int i = 0; i < 10; i++) {
				//	把每個陣列內的資料轉成JSON物件
				JSONObject row = root.getJSONObject(i); // 從jsonArray[i]讀出物件jsonObject
				int id = row.getInt("編號");
				String field = row.getString("林區");
				String forestRoadName = row.getString("林道名稱");
				//	特殊判斷，避免直接 getString 讀出 null 而出現錯誤
				if (row.isNull("原編碼")) {
					originId = null;
					//originId = "null";	// 方可正常寫入MySQL的varchar()
				} else {
					originId = row.getString("原編碼");
				}
//				System.out.println(id + " : " + forestRoadName + " : " + originId);
				if (row.isNull("公路專線")) {
					roadLine = null;
				}else {
					roadLine = row.getString("公路專線");
				}
				String forestRoadSpec = row.getString("林道規格");
				String forestRoadType = row.getString("林道種類");
				String buildYear = row.getString("開設年份");
				String county = row.getString("縣市");
				String township = row.getString("鄉鎮");
				String village = row.getString("村里");
				String passingSettlement = row.getString("林道途經聚落");
				String forestClass = row.getString("事業區林班");
				String startLocation = row.getString("起點");
				String endLocation = row.getString("終點");
				String startXcoord = row.getString("起點X坐標");
				String startYcoord = row.getString("起點Y坐標");
				String endXcoord = row.getString("終點X坐標");
				String endYcoord = row.getString("終點Y坐標");
				Float vehicleTravelLength = row.getFloat("車行長度");
				Float walkingLength = row.getFloat("步行長度");
				Float breakLength = row.getFloat("中斷長度");
				Float totalLength = row.getFloat("總長度");
				Float acPaving = row.getFloat("A.C鋪面");
				Float pcPaving = row.getFloat("P.C鋪面");
				if (row.isNull("碎石面或土石鋪面")) {
					gravelSoilPaving = (float)0;
				}else {
					gravelSoilPaving = row.getFloat("碎石面或土石鋪面");
				}
				
				String catchment = row.getString("集水區");
				String subcatchment = row.getString("子集水區");
				String mileage = row.getString("進入林班地里程");
				if (row.isNull("備註")) {
					remarks = null;
				} else {
					remarks = row.getString("備註");
				}
				
				
				//	4. 設定 pstmt參數(preparedStatement) 對應的問號
				pstmt.setInt(1, id);
				pstmt.setString(2, field);
				pstmt.setString(3, forestRoadName);
				pstmt.setString(4, originId);
				pstmt.setString(5, roadLine);
				pstmt.setString(6, forestRoadSpec);
				pstmt.setString(7, forestRoadType);
				pstmt.setString(8, buildYear);
				pstmt.setString(9, county);
				pstmt.setString(10, township);
				pstmt.setString(11, village);
				pstmt.setString(12, passingSettlement);
				pstmt.setString(13, forestClass);
				pstmt.setString(14, startLocation);
				pstmt.setString(15, endLocation);
				pstmt.setString(16, startXcoord);
				pstmt.setString(17, startYcoord);
				pstmt.setString(18, endXcoord);
				pstmt.setString(19, endYcoord);
				pstmt.setFloat(20, vehicleTravelLength);
				pstmt.setFloat(21, walkingLength);
				pstmt.setFloat(22, breakLength);
				pstmt.setFloat(23, totalLength);
				pstmt.setFloat(24, acPaving);
				pstmt.setFloat(25, pcPaving);
				pstmt.setFloat(26, gravelSoilPaving);
				pstmt.setString(27, catchment);
				pstmt.setString(28, subcatchment);
				pstmt.setString(29, mileage);
				pstmt.setString(30, remarks);
				
				//	5. 執行SQL Command
				pstmt.executeUpdate();	// .execute()也可以
				System.out.println("Loading toMyDB(" + i + ") OK");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("資料庫載入資料錯誤");
		} catch (JSONException e1) {
			e1.printStackTrace();
			//	若某筆資料有問題的不算 continue !
			System.out.println("某筆資料有問題" + e1);
		} finally {
			try {
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("關閉連線錯誤");
			}
			//	完成時間
			
		}
		
	}
	
	public static void main(String[] args) {
		//	步驟一：從 open data 網站下載JSON格式的資料
		String source = fetchOpenData();
		//System.out.println(source);	// 測試資料是否OK
		if (source != null) {
			toMyDB(source);
		}else {
			System.out.println("Data not found");
		}
	}

}
