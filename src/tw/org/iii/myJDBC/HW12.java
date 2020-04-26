package tw.org.iii.myJDBC;

import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;


/*	20180826PM2 JTable 試玩	00:30:00
 * 	
 * 	Use Table to show data from DB to user.
 * 
 * 	Data -> Model -> JTable
 * 
 * 	Reference:
 * 	https://docs.oracle.com/javase/tutorial/uiswing/components/table.html
 * 	=> 使用 Java Web Start：程式在遠端執行，再呈現在使用者端	(目前很少人使用)
 * 
 * 	Abstract Table
 * 	https://docs.oracle.com/javase/7/docs/api/javax/swing/table/AbstractTableModel.html
 * 	
 * 	Table Model => 會去追蹤資料是否有更新
 * 	
 * 	重點：
 * 	從伺服器撈資料儲存至資料結構 HashMap
 * 	透過TableModel整合與呈現資料
 * 
 * 	建立基本視窗架構 > 
 */

public class HW12 extends JFrame {
//	private JTable jTable;
	//	
	//	//	範例測試資料
	//	//	陣列資料固定無法修改 => 適合表現固定資料
	//	private String[] columnNames = {"First Name", "Last Name", "Sport", "# of Years", "Vegetarian"};
	//	
	//	private Object[][] data = {
	//			{"Kathy", "Smith", "snowboarding", (Integer)5, (Boolean)false},
	//			{"John", "Doe", "Rowing", (Integer)3, (Boolean)true},
	//			{"Sue", "Black", "Knitting", (Integer)2, (Boolean)false},
	//			{"Jane", "White", "Speed reading", (Integer)20, (Boolean)true},
	//			{"Sean", "Wick", "Pool", (Integer)10, (Boolean)false}
	//	};
	//	
	//	public HW12() {
	//		super("JDBC_HW12 JTable 範例練習");
	//		
	//		setLayout(new BorderLayout());
	//		
	//		jTable = new JTable(data, columnNames);
	//		// Scrollable
	//		JScrollPane jsp = new JScrollPane(jTable);
	//		add(jsp, BorderLayout.CENTER); // java.awt.Container.add(元件, 物件Layout的約束條件)
	//		
	//		setSize(800, 600);
	//		setVisible(true);
	//		setDefaultCloseOperation(EXIT_ON_CLOSE);
	//	}


	//	-----Scrollable and auto Update Table-----
	//	Creating a Table Model => 透過 Model 管控與呈現資料
	//	實作 TableModel
	private JTable jTable;
	
	//	原始資料也可以用Property
	private LinkedList<HashMap<String, String>> data;
	
	//	建構式
	public HW12() {
		super("JDBC_HW12 JTable 練習2");
		setLayout(new BorderLayout());
		
		//	載入伺服器端資料
		initDate();
		
		jTable = new JTable(new MyTableModel());
		JScrollPane jsp = new JScrollPane(jTable);
		add(jsp, BorderLayout.CENTER);
		
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//	載入伺服器端資料的Method
	private void initDate() {
		//	模擬資料傳入
		data = new LinkedList<>();
		
		//	產生模擬資料 (無直接關聯，純粹試驗一下)
//		for (int i = 0; i < 50; i++) {
//			HashMap<String, String> row = new HashMap<>();
//			row.put("id", "" + i); // .put(key, value)
//			row.put("name", "John" + (int)(Math.random()*100));
//			row.put("tel", "1234-" + (int)(Math.random()*100));
//			row.put("birthday", "1981-01-01");
//			data.add(row);
//		}
		
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		
		String query = "SELECT * FROM 02_hw02_01";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DriverManager.getConnection(url, prop);
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				//	擷取rs的輸出資料
				String c1 = rs.getString("id");
				String c2 = rs.getString("name");
				String c3 = rs.getString("tel");
				String c4 = rs.getString("birthday");
				//	把擷取的資料放入HashMap<> (供之後的TabelModel取用)
				HashMap<String, String> row = new HashMap<>();
				row.put("id", c1);
				row.put("name", c2);
				row.put("tel", c3);
				row.put("birthday", c4);
				data.add(row);
			}
			System.out.println("OK");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("資料庫出現錯誤");
		} finally {
			try {
				if (rs != null)	rs.close();
				if (pstmt != null) pstmt.close();
				if (conn != null) conn.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
				System.out.println("關閉連線錯誤");
			}
		}
	}

	private class MyTableModel extends DefaultTableModel {
		/*		To create a concrete TableModel as a subclass of AbstractTableModel
		 * 		you need only provide implementation for the following three methods:
		 * 				public int getRowCount();
		 * 				public int getColumnCount();
		 * 				public Object getValueAt(int row, int column);
		 * 
		 * 		Model => 整合資料，透過調變器，處裡資料
		 * 
		 * 		10 column 10 row 的範例
		 * 		TableModel dataModel = new AbstractTableModel() {
         *			public int getColumnCount() { return 10; }
         * 			public int getRowCount() { return 10;}
         * 			public Object getValueAt(int row, int col) { return new Integer(row*col); }
         *		};
         *		JTable table = new JTable(dataModel);
         *		JScrollPane scrollpane = new JScrollPane(table);
		 */
		
		@Override
		public int getRowCount() {
			// 多少列
			return data.size();
		}

		@Override
		public int getColumnCount() {
			// 多少行
			return 4;
		}

		@Override
		public String getColumnName(int column) {
			// 改寫：回傳欄位名稱
			String read = "";
			switch (column) {
			case 0:
				read = "帳號"; break;
			case 1:
				read = "姓名"; break;
			case 2:
				read = "電話"; break;
			case 3:
				read = "生日"; break;
			}
			return read;
		}

		@Override
		public Object getValueAt(int row, int column) {
			// 取得指定欄位資料
			String read = "";
			switch(column) {
			case 0:
				read = data.get(row).get("id");
				break;
			case 1:
				read = data.get(row).get("name");
				break;
			case 2:
				read = data.get(row).get("tel");
				break;
			case 3:
				read = data.get(row).get("birthday");
				break;
			}
			return read;
		}
		
	}
	
	public static void main(String[] args) {
		new HW12();
	}

}
