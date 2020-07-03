package tw.org.iii.myJDBC;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;

/*		此為單一CLASS (單一類別)
 * 		儲存物件到資料庫並恢復
 * 		=>
 * 		物件序列化>二進制流。要儲存序列化物件，必需在資料庫設定blob欄位儲存
 * 		MySQL的blob欄位是用來儲存二進制資料的。可以直接用
 * 		pstmt.setObject()將物件儲存到資料庫中。
 * 		
 * 		要將物件恢復，首先讀出二進制資料。
 * 		ResultSet.getBlob()，
 * 		
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//import tw.org.iii.myJDBC.HW21_Model;


public class HW21_serialObjToDb {
	private static Connection conn;
	private PreparedStatement pstmt;
	
	static {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("資料庫驅動程式載入成功");
			String mysqlUrl = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
			Properties prop = new Properties();
			prop.setProperty("user", "root");
			prop.setProperty("password", "root");
			
			conn = DriverManager.getConnection(mysqlUrl, prop);
			System.out.println("資料庫連線成功");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("資料庫驅動程式載入失敗");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*		物件存入DB
	 * 		向資料庫中的資料表hw21中，插入多個 M 物件 (HW21_Model)
	 * 		id:int
	 * 		object:blob
	 * 		params:
	 * 			models: M 物件 list
	 */
	public void saveModelToDB(List<HW21_Model> models) {
		String sqlSave = "INSERT INTO hw21 (object) VALUES(?)";
		
		try {
			pstmt = conn.prepareStatement(sqlSave);
			for (int i = 0; i < models.size(); i++) {
				pstmt.setObject(1, models.get(i));
				pstmt.addBatch();		//	批次增加指令
			}
			pstmt.executeBatch();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			try {
				if (pstmt != null) pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("saveModelToDB 關閉連線錯誤");
			}
		}
		
	}
	
	/*		從資料庫讀出儲存的物件
	 * 		return:
	 * 			list: M 物件(HW21_Model)的列表
	 */
	
	/*		要將物件恢復，則首先需要讀出二進位制資料，讀出的方法是用
	 * 		ResultSet.getBlob()方法，然後用Blob對象的
	 * 		getBinaryStream()方法來獲得二進位制流物件，然後將該
	 * 		二進位制流物件作為引數構造帶緩衝區的流物件BufferedStream
	 * 		，然後用byte[]陣列從BufferedInputStream流中讀取
	 * 		二進位制資料，然後用該byte陣列來建構
	 * 		ByteArrayInputStream，然後用ByteArrayInputStream來
	 * 		建構ObjectInputStream，最後直接用ObjectInputStream物件
	 * 		的readObject方法讀出物件資料，並強制性轉化為原始的物件資料。
	 */
	
	public List<HW21_Model> getModel() {
		List<HW21_Model> list = new ArrayList<HW21_Model>();
		String sqlGet = "SELECT object FROM hw21";
		ResultSet result = null;
		InputStream is = null;
		BufferedInputStream bis = null;
		ObjectInputStream ois = null;
		
		try {
			pstmt = conn.prepareStatement(sqlGet);
			result = pstmt.executeQuery();
			
			while(result.next()) {
				Blob inBlob = result.getBlob("object");		//	取得blob物件
				is = inBlob.getBinaryStream();		//	取得二進制流物件
				bis = new BufferedInputStream(is);	//	帶緩衝區的流物件
				
				byte[] buff = new byte[(int) inBlob.length()];	//	一次讀取多少byte
				while(bis.read(buff, 0, buff.length) != -1) {
					ois = new ObjectInputStream(new ByteArrayInputStream(buff));
					HW21_Model model = (HW21_Model) ois.readObject();
					list.add(model);
				}
				
			}
		} catch (SQLException | IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null) ois.close();
				if (bis != null) bis.close();
				if (is != null) is.close();
				if (result != null) result.close();
				if (pstmt != null) pstmt.close();
			} catch (IOException | SQLException e) {
				e.printStackTrace();
				System.out.println("關閉連線發生錯誤");
			}
		}
		
		return list;
	}
}
