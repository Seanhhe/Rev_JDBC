package tw.org.iii.myJDBC;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/*		20180901PM2
 * 		
 * 		BLOB
 * 		=>	65535 bytes
 * 		LONGBLOB
 * 		=>	4294967295 bytes (4Gib)
 * 		=>	沒有真的要放很多的話，不建議使用，因為會影響資料庫搜尋的速度
 * 
 * 		MySQL 儲存格式中，用 TEXT 或 BLOB 儲存長字串。
 * 		差異：
 * 			TEXT = 只能儲存字元資料；	BLOB = 可以保存二進位資料 (圖片)
 * 		缺點：
 * 			兩者在刪除資料後，仍會占用記憶體空間，長時間或大量執行刪除動作時，會產生大量空洞，
 * 			引發性能問題。
 * 		對策：
 * 			藉由定期 OPTIMIZE TABLE 把資料表進行磁碟重組，把空洞空間釋放以改善效能。
 * 			mysql> OPTIMIZE TABEL 資料表名稱
 */

/*		若遇到 PacketTooBigException，可直接修改my.ini文件中的
 * 		max_allowed_packet，從4M改為20M，存檔後重新啟動MySQL
 * 		服務即可。
 */

/*		切記：
 * 		Java物件要序列化 (implements Serializable)，才能透過網路傳送到資料庫儲存。
 * 		JDBC 連線的 pstmt 參數要以 setObject 去設定，MySQL端資料表欄位需設定 BLOB 或 MEDIUMBLOB 方可正確接收。
 */

public class HW19 {

	public HW19() {
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		Connection conn = null;
		PreparedStatement pstmt = null;
		int resultInt = 1;
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		String sqlcmd = "UPDATE hw19 SET img = ?, student = ?, id = ?";
		FileInputStream fin = null;
		
		Student s1 = new Student(90, 87, 100);
		
		try {
			conn = DriverManager.getConnection(url, prop);
			fin = new FileInputStream("dir01/wallpapersden.com_banff-national-park-canada-grass_7680x4320.jpg");
			
			pstmt = conn.prepareStatement(sqlcmd);
			
			//	讀圖片進來	(二進制串流)
			pstmt.setBinaryStream(1, fin);
			//	讀進物件
			pstmt.setObject(2, s1);
			pstmt.setInt(3, 1);
			pstmt.executeUpdate();
			resultInt = pstmt.executeUpdate();
			if (resultInt != 0) {
				System.out.println("儲存成功");
			}else {
				System.out.println("更新失敗");
				System.out.println("resultInt: " + resultInt);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("資料庫發生錯誤");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("找不到傳入的檔案");
		} finally {
			try {
				if (pstmt != null) pstmt.close();
				if (fin != null) fin.close();
				if (conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("關閉連線發生錯誤");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("IO Exception");
			}
		}
		
	}
	
	public static void main(String[] args) {
		new HW19();
	}

}

class Student implements Serializable {
	int ch, eng, math;
	public Student(int a, int b, int c) {
		this.ch = a;
		this.eng = b;
		this.math = c;
	}
	
	public int sumScore() {
		return ch + eng + math;
	}
}
