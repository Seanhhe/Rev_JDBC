package tw.org.iii.myJDBC;

/*	新增資料庫的練習	20200222
 * 
 */

import java.sql.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

class mysqlFrame {
	JFrame popUpMessage = new JFrame();
	Connection conn;
	Statement stmt;
	String jdbcURL = "jdbc:mysql://localhost:3306/";
	
	public mysqlFrame() {
		try {	//	載入資料庫驅動程式
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver Loaded OK!!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "MySQL驅動程式安裝失敗");
		}
		
		//	建立資料庫連線
		try {
			conn = DriverManager.getConnection(jdbcURL +
					"?user=root&password=root&serverTimezone=CST");
			stmt = conn.createStatement();	// 利用傳回的Connection物件產生Statement物件，再用stmt物件下SQL命令
			stmt.executeUpdate("CREATE DATABASE HW_JDBC_Member01"); // SQL指令>建立新的資料庫
			JOptionPane.showMessageDialog(null, "資料庫建立成功");
			stmt.close();
			conn.close();
			//conn = null;
			System.exit(0); // 0是讓程式正常結束
		} catch (SQLException e) {
			//e.printStackTrace();
			if (stmt != null) {
				JOptionPane.showMessageDialog(null, "資料庫已存在");
				System.exit(0);
			}else {
				JOptionPane.showMessageDialog(null, "資料庫無法啟動");
				System.exit(0);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "發生其他錯誤");
			System.exit(0);
		}
	}
}

public class HW02_JDBC_Member01_1 {

	public static void main(String[] args) {
		new mysqlFrame();
	}

}
