package tw.org.iii.myJDBC;

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

/*		JTable的建構式有七個：
 * 		1) JTable()
 * 		2) JTable(int numRows, int numColumns)
 * 		3) JTable(Object[][] rowData, Object[] columnNames)  (固定資料)
 * 		4. JTable(TableModel dm)
 * 		5. JTable(TableModel dm, TableColumnModel cm)
 * 		6. JTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm)
 * 		7. JTable(Vector rowData, Vector columnNames)  (可變資料)
 */

import java.awt.BorderLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class HW12_2 extends JFrame {
	
	private JTable jTable;
	private int dataCount;
	private String[] fields;
	Connection conn = null;
	PreparedStatement pstmt0 = null;
	PreparedStatement pstmt = null;
	ResultSet rs0 = null;
	ResultSet rs = null;
	ResultSetMetaData rsMetaData = null;
	
	public HW12_2() {
		super("HW12_2 JTable 練習");
		setLayout(new BorderLayout());
		
		//	載入伺服器端的資料
		initData();
		
		jTable = new JTable(new MyTableModel());
		jTable.setFont(new Font("", Font.PLAIN, 16));
		JScrollPane jsp = new JScrollPane(jTable);
		add(jsp, BorderLayout.CENTER);
		
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	//	載入伺服器端的資料
	private void initData() {
		String url = "jdbc:mysql://localhost:3306/rev_jdbc_members?serverTimezone=Asia/Taipei";
		Properties prop = new Properties();
		prop.setProperty("user", "root");
		prop.setProperty("password", "root");
		String query = "SELECT id, name, tel, birthday FROM 02_hw02_01";
//		Connection conn = null;
//		PreparedStatement pstmt0 = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs0 = null;
//		ResultSet rs = null;
//		ResultSetMetaData rsMetaData = null;
		try {
			conn = DriverManager.getConnection(url, prop);
			pstmt0 = conn.prepareStatement("SELECT COUNT(*) AS COUNT FROM 02_hw02_01");
			rs0 = pstmt0.executeQuery();
			
			rs0.next(); // 資料庫若有查到資料並回傳，就會顯示true
			dataCount = rs0.getInt("資料表欄位名稱_Count");
			
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			rs = pstmt.executeQuery();
			//從 ResultSet 取得此物件的行(column)之 number、type、屬性等描述
			rsMetaData = rs.getMetaData(); 
			fields = new String[rsMetaData.getColumnCount()];
			for (int i = 0; i < fields.length; i++) {
				fields[i] = rsMetaData.getColumnLabel(i + 1); // getColumnLabel(int) 裡頭int參數若要得到第一行就是1，第二行是2。所以i+1
				// 逐一取出 ResultSetMetaData 的回傳資料表欄位名稱(column)
				// 並放入字串陣列 fields[]中
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("資料庫發生錯誤");
		}
	}
	
	private class MyTableModel extends DefaultTableModel {

		@Override
		public int getRowCount() {
			return dataCount;
		}

		@Override
		public int getColumnCount() {
			return fields.length;
		}

		@Override
		public String getColumnName(int column) {
			return fields[column];
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return fields[column].equals("id")?false:true;	// 只要是ID欄位，都不給修改
			//return true;
		}

		@Override
		public Object getValueAt(int row, int column) {
			// 回傳該row,column對應的儲存格的值
			// 類似操作夾娃娃機的控制
			try {
				rs.absolute(row + 1);	// 把指標移向(row + 1)位置
				return rs.getString(fields[column]);	// 回傳(row+1, fields[column])座標欄位的資料
				// 須留意索取資料的型別(getInt, getDate, getString 等不同型別)
			} catch (SQLException e) {
				e.printStackTrace();
				return "---";
			}
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			super.setValueAt(aValue, row, column);
			//	設定資料至指定的 row,column 儲存格
			try {
				rs.absolute(row + 1);
				rs.updateString(fields[column], (String)aValue);
				// 須留意設定資料的各種型別，要對應才正確。
				rs.updateRow();	// 把上述指定位置的資料更新到 row
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("更新的目標欄位錯誤");
				
			}
		}
		
	}
	public static void main(String[] args) {
		new HW12_2();
	}

}
