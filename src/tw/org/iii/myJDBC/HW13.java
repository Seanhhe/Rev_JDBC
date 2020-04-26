package tw.org.iii.myJDBC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;

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
		//	1.	先解析JSON格式=>解析成功才能入到資料庫
		JSONArray root = new JSONArray(json);
		System.out.println(root.length());	// 確認是否有解析成功
		
		
	}
	
	public static void main(String[] args) {
		//	步驟一：從 open data 網站下載JSON格式的資料
		String source = fetchOpenData();
		System.out.println(source);	// 測試資料是否OK
		
	}

}
