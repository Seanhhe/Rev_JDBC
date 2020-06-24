package tw.org.iii.myJDBC;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/*		20180901PM2 00:38:00
 * 		
 * 		存取資料庫上的Object & img
 * 		=> BLOB & MEDIUMBLOB格式
 * 		=> 	BinaryStream
 * 
 * 		alt + shift + z => try-catch 快捷鍵
 * 
 * 		**補充 MDB
 * 		Access 連接 Java
 * 		=> 參考微軟官方文件(套用API)
 * 		=> 基本上與mysql很類似，buildpath/Class.forName
 * 		=> 操作方法與課堂上練習的mysql很像
 * 		=> Access: 屬本機檔案，所以沒有帳號密碼
 */

public class HW20 {

	public HW20() {
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		Connection conn = null;
		PreparedStatement pstmt = null;
		InputStream in = null, in2 = null;		// 輸入
		FileOutputStream fout = null; 	// 輸出
		ResultSet result = null;
		ObjectInputStream oin = null;
		Student s2;
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		String sqlcmd = "SELECT * FROM hw19 WHERE id = ?";
		
		try {
			conn = DriverManager.getConnection(url, prop);
			pstmt = conn.prepareStatement(sqlcmd);
			pstmt.setInt(1, 1);
			result = pstmt.executeQuery();
			
//			result.next();
//
//			//告訴 FileOutputStream 檔案的輸出位置及名稱。
//			fout = new FileOutputStream("dir2/hw20.png");
//			//	把資料庫的圖片讀進來
//			in = result.getBinaryStream("img");
//			byte[] buf = new byte[4096];	//	每讀一次暫存到這裡
//			int length;	//	讀進來的長度
//			while((length = in.read(buf)) != -1) {
//				fout.write(buf, 0, length);
//			}
//			fout.flush();
//			in.close();
			
			//------------------------------------
			
			//	student物件不能直接getObject轉型，因為資料庫欄位是使用BLOB格式
			//Student obj = (Student) result.getObject("student");
			//System.out.println("英文成績：" + obj.eng);	// 英文成績
			
			//	透過串接把物件取出
			//	通常物件儲存在資料庫無法用SQL查詢，但可保有物件特性，讀入程式中使用
			//in2 = result.getBinaryStream("student");
			//oin = new ObjectInputStream(in2);	//	出現錯誤EOFException
			//Student s2 = (Student) oin.readObject();
			//System.out.println("s2.eng：" + s2.eng);
			
			//----上述區塊不可行----下述區塊也無法正確讀出----
			while(result.next()) {
				s2 = null;
				Blob inBlob = result.getBlob("student"); // 取得Blob型態
				in2 = inBlob.getBinaryStream();
				BufferedInputStream bis = new BufferedInputStream(in2);
				
				byte[] buff = new byte[(int) inBlob.length()];
				while((bis.read(buff, 0, buff.length)) != -1 ) {
					oin = new ObjectInputStream(new ByteArrayInputStream(buff));
					s2 = (Student) oin.readObject();
				}
				System.out.println("s2.eng：" + s2.eng);
			}
			
			
			System.out.println("Export OK : 完成讀取物件");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("資料庫發生錯誤");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("檔案輸出入發生錯誤");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("oin = new ObjectInputStream(new ByteArrayInputStream(buff))有問題");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("ClassNotFound");
		} finally {
			try {
				if (fout != null) fout.close();
				if (in != null) in.close();
				if (in2 != null) in2.close();
				if (oin != null) oin.close(); 
				if (result != null) result.close();
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("關閉連線發生錯誤");
			}
		}
		
	}
	
	public static void main(String[] args) {
		new HW20();
	}

}
