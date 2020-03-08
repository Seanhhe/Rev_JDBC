package tw.org.iii.myJDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

/*	建立會員資料表練習	20200224
 * 	
 */

class mysqlFrame3 {
	Connection conn;
	Statement stmt;
	String jdbcURL = "jdbc:mysql://localhost:3306/";
	public mysqlFrame3() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "資料庫驅動程式載入失敗");
		}
		//	建立資料庫
		try {
			conn = DriverManager.getConnection(jdbcURL + "?serverTimezone=CST&user=root&password=root");
			stmt = conn.createStatement();
			stmt.executeUpdate("CREATE DATABASE hw_jdbc_member01"); // 新增資料庫CREATE DATABASE
			//conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			if (stmt != null) {
				JOptionPane.showMessageDialog(null, "資料庫已存在");
			}else {
				JOptionPane.showMessageDialog(null, "MySQL無法啟動");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "發生其他錯誤");
		}
		//	建立資料表
		try {
			conn = DriverManager.getConnection(jdbcURL + "hw_jdbc_member01" + "?serverTimezone=CST&user=root&password=root");
			stmt = conn.createStatement();
			String createTB = "CREATE TABLE personal_data(";
			createTB += "acc_id VARCHAR(10) PRIMARY KEY, "; //	帳號 (主鍵)
			createTB += "password VARCHAR(10), ";	// 密碼
			createTB += "date_join DATE, ";	// 日期
			createTB += "name VARCHAR(10), ";	// 姓名
			createTB += "gender TINYINT(1), ";	// 性別
			createTB += "age INT, ";	// 年齡
			/*	MySQL 官方文件指出目前不支援boolean型別,
			 * 	需要使用的話用tinyint(1)代替。
			 * 	如果你定義了布林型別,它會自動給你轉換成tinyint(1)。
			 * 	
			 * 	注意：只有TINYINT(1)才能在查詢時自動轉換成Boolean值，
			 * 		TINYINT(4)或其他數值皆不行。
			 * 
			 * 		測試BOOL是否會自動轉成TINYINT [實測MySQL 8.0 會自動轉換成 TINYINT(1)]
			 */
			createTB += "habbit1 BOOL, ";
			createTB += "habbit2 BOOL, ";
			createTB += "habbit3 BOOL, ";
			createTB += "habbit4 BOOL, ";
			createTB += "habbit5 BOOL, ";
			createTB += "education TINYINT, ";
			createTB += "home TINYINT)";
			
			stmt.execute(createTB); // 新增資料表 CREATE TABLE
			JOptionPane.showMessageDialog(null, "資料庫和資料表建立成功");
			stmt.close();
			conn.close();
			System.exit(0);
		} catch (SQLException e) {
			if (stmt != null) {
				JOptionPane.showMessageDialog(null, "資料表已存在");
				System.exit(0);
			}else {
				JOptionPane.showMessageDialog(null, "MySQL無法啟動");
				System.exit(0);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "發生其他錯誤");
			System.exit(0);
		}
	}
}

public class HW02_JDBC_Member01_3 {

	public static void main(String[] args) {
		new mysqlFrame3();
	}

}
