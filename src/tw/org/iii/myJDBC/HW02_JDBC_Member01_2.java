package tw.org.iii.myJDBC;

/*	刪除資料庫的練習	20200223
 * 
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

class mysqlFrame2 {
	Connection conn;
	Statement stmt;
	String jdbcURL = "jdbc:mysql://localhost:3306/hw_jdbc_member01?serverTimezone=CST&user=root&password=root";
	
	public mysqlFrame2() {
		//	載入驅動程式
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "資料庫驅動程式安裝失敗");
		}
		//	刪除資料庫
		try {
			conn = DriverManager.getConnection(jdbcURL);
			stmt = conn.createStatement();
			stmt.execute("DROP DATABASE hw_jdbc_member01"); // 刪除資料庫的指令
			JOptionPane.showMessageDialog(null, "資料庫刪除成功");
			stmt.close();
			conn.close();
			System.exit(0);
		} catch (SQLException e) {
			//e.printStackTrace();
			if (stmt == null) {
				JOptionPane.showMessageDialog(null, "資料庫不存在");
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

public class HW02_JDBC_Member01_2 {

	public static void main(String[] args) {
		new mysqlFrame2();
	}
}
