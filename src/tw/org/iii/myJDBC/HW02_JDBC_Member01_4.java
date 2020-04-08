package tw.org.iii.myJDBC;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/*	會員資料庫管理之練習	20200224
 * 	本例執行前，需先執行	HW02_JDBC_Member01_3.class 一次，即先建好會員資料庫與資料表。
 * 
 * 		先寫出基本視窗設定 > 增加文字 按鈕物件 > 增加相關資料庫物件 > 增加容器物件讓JPanel放
 * 		> 資料庫載入驅動程式 > 自訂Method: 資料庫嚴重錯誤對話框 > 資料庫連線 > 建立使用者介面
 * 		> 按鈕監聽事件 : 註冊、登入 > 自訂Method: 錯誤訊息對話面板 > panel2介面初值處理 
 * 		> 取得資料庫內的存檔紀錄 > (結束)按鈕監聽事件 : 註冊、登入 > 新增面板2 > 
 */

/*	MySQL 8的日期時間範圍
 * 	文件參考：https://dev.mysql.com/doc/refman/8.0/en/datetime.html
 * 	重點摘要：
 * 	DATE : 'YYYY-MM-DD'，支援範圍 '1000-01-01' to '9999-12-31'
 * 	DATETIME : 'YYYY-MM-DD hh:mm:ss'，支援範圍'1000-01-01 00:00:00' to '9999-12-31 23:59:59'
 * 	TIMESTAMP : 支援範圍'1970-01-01 00:00:01' UTC to '2038-01-19 03:14:07' UTC
 * 
 * 	時區問題：
 * 	serverTimeZone=Asia/Taipei 即可解決java.sql.Date 存入 MySQL Date欄位時，時間少一天(TimeStamp少13小時)的問題
 */

class mysqlFrame4 extends JFrame {
//	資料庫相關物件
		Connection conn;
		Statement stmt;
		PreparedStatement pstmt;
		ResultSet result;
		
		//	增加文字、按鈕等物件
		String id_get, password_get, temp_password, dateNow, input_sql;
		int btn_act;	// 設定給user按鈕狀態，1 = 註冊，2 = 登入
		Integer year, month, day;
		JLabel itemQ1, itemQ2; // 帳號&密碼
		JTextField id;
		JPasswordField password;
		JButton qb11, qb12; // 按鈕:註冊、登入
		JButton qb31, qb32, qb33; // 按鈕:取消、確認、刪除
		JPanel panel1, panel2; // 面板群組:登入註冊面板、會員資料面板
		JLabel item7, item8; // item7:加入日期、item8:讀取資料庫資料表的欄位(加入日期)
		Date toSqlDate;
		
		//	建立容器物件(可放入JPanel)
		Container container;
		JLabel item1, item2, item3, item4, item5, item6; // 姓名、年齡、性別、興趣、學歷、居住地址
		JCheckBox cb1, cb2, cb3, cb4, cb5;	//	興趣1至5
		ButtonGroup btn_group;	// 按鈕群組(性別)
		JRadioButton rb1, rb2;	// 男、女
		JComboBox c_box; // 學歷下拉式清單
		JTextField textName; // 填寫姓名的
		JSpinner spinner;	// 年齡下拉式選單
		JList list; // 居住地選單
		String[] edu_label = {"博士", "碩士", "大學", "高中", "國中", "國小"}; // 學歷內容陣列
		String[] city_label = {"台北", "桃園", "新竹", "苗栗", "台中", "南投", "彰化", "雲林", 
								"嘉義", "台南", "高雄", "屏東", "花蓮", "宜蘭", "台東", "澎湖"}; // 居住地內容陣列
	public mysqlFrame4() {
		// 資料庫載入驅動程式
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			errorMessage("MySQL驅動程式安裝失敗");
		}
		// 資料庫連線
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hw_jdbc_member01"
					+ "?serverTimezone=Asia/Taipei&user=root&password=root"); // &useUnicode=true&characterEncoding=UTF-8
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			errorMessage("MySQL無法啟動");
		} catch (Exception e) {
			errorMessage("發生其他錯誤");
		}
		
		//	建立使用者介面
//		((JFrame) container).getContentPane();
		//	取得日期字串供加入日期使用
		Calendar calendar = Calendar.getInstance();	// 真正的日期時間
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1; // 0是指一月份
		day = calendar.get(Calendar.DAY_OF_MONTH);
		dateNow = year.toString() + "-" + month.toString() + "-" + day.toString(); // 給使用者看的字串格式日期時間
		/**		
		 * 		1)
		 * 		Java.sql.Timestamp
		 * 		繼承java.util.Date。包含了年月日時分秒奈秒(nano)更多資訊。在資料庫相關操作中使用，例如
		 * 		ResultSet.getTimestamp，preparedStatement.setTimeStamp等。
		 * 		例如：資料庫中某欄位為Date型別，則使用getTimestamp能將年月日時分秒資訊取出。但使用getDate只能取出年月日。
		 * 		因此，一搬推薦使用getTimestamp。
		 * 
		 * 		另有一說，使用Timestamp，等到西元2038年2月時程式會出錯。(適合紀錄變更)
		 * 		
		 * 		// java.util.Calendar轉換為java.sql.Timestamp
		 * 		new Timestamp(Calendar.getInstance().getTimeInMillis());
		 * 		
		 * 		// java.util.Date轉換為java.sql.Timestamp
		 * 		new Timestamp(date.getTime());
		 * 		
		 * 		//	String轉換為java.sql.Timestamp
		 * 		//	String格式：yyyy-mm-dd hh:mm:ss [.f...]，方括號表示可選
		 * 		Timestamp.valueOf("2020-03-20 13:56:30");
		 * 
		 * 		2)
		 * 		Java.util.Calendar
		 * 		包含年月日時分秒毫秒等資訊。(用來取代 java.util.Date)
		 * 		
		 * 		// Date轉為Calendar
		 * 		Date date = new Date();
		 * 		Calendar calendar = Calendar.getInstance();
		 * 		calendar.setTime(date);
		 * 
		 * 		// Calendar轉Date
		 * 		Calendar ca = Calendar.getInstance();
		 * 		Date d = (Date) ca.getTime();
		 * 
		 * 		3)
		 * 		Java.sql.Date
		 * 		包含年月日資訊。
		 * 		繼承自java.util.Date。在資料庫操作中使用，
		 * 		例如 ResultSet.getDate、preparedStatement.setDate。
		 * 
		 * 		// java.util.Date 轉 java.sql.Date
		 * 		new java.sql.Date(utilDate.getTime()); 
		 * 		上面utilDate為java.util.Date型別的物件。
		 * 
		 * 		// Calendar 轉 java.sql.Date
		 * 		Calendar ca = Calendar.getInstance();
		 * 		String year, month, day, stringDate;
		 * 		year = ca.get(Calendar.YEAR); month = ca.get(Calendar.MONTH) + 1; day = ca.get(Calendar.DAY_OF_MONTH);
		 * 		stringDate = year.toString() + "-" + month.toString() + "-" + day.toString();
		 * 		字串格式必須符合yyyy-mm-dd，才能正確使用valueOf()方法做轉換
		 * 		Date toSqlDate = Date.valueOf(stringDate);
		 * 		搭配pstmt.setDate(index順序, toSqlDate);
		 * 		即可把toSqlDate的日期傳到MySQL資料表內的DATE格式之欄位
		 */
//		Timestamp toSqlTimestamp = new Timestamp(Calendar.getInstance().getTimeInMillis()); // 備用給MySQL的Timestamp型態
//		String calendarToString = Calendar.getInstance().toString();	// 因為得到字串不是yyyy-mm-dd格式，所以編譯出現錯誤
//		Date toSqlDate = Date.valueOf(calendarToString);
		toSqlDate = Date.valueOf(dateNow);
		System.out.println("toSqlDate: " + toSqlDate);
		//	配置帳號密碼輸入欄位及註冊、登入按鈕與監聽事件
		panel1 = new JPanel();
		panel1.setBounds(0, 0, 500, 40);
		panel1.setBackground(Color.LIGHT_GRAY);
		itemQ1 = new JLabel("帳號 : ");
		itemQ2 = new JLabel("密碼 : ");
		id = new JTextField("輸入英文數字",12);	// 括弧內寫預設寬度(行數)
		password = new JPasswordField(12); // 括弧內寫預設寬度(行數)
		qb11 = new JButton("註冊");
		qb12 = new JButton("登入");
		qb11.addActionListener(checkActionListener);
		qb12.addActionListener(checkActionListener);
		panel1.add(itemQ1);
		panel1.add(id);
		panel1.add(itemQ2);
		panel1.add(password);
		panel1.add(qb11);
		panel1.add(qb12);
		add(panel1); //	把panel1加入JFrame中
		
		//	新增面板2=>配置個人資料項目及內容
		panel2 = new JPanel();
		panel2.setBounds(0, 40, 500, 200);
		panel2.setLayout(null);	//	null表示使用絕對座標。
		item1 = new JLabel("姓名：");	item1.setBounds(40, 60, 40, 20);
		item2 = new JLabel("年齡：");	item2.setBounds(200, 60, 40, 20);
		item3 = new JLabel("性別：");	item3.setBounds(40, 80, 40, 20);
		item4 = new JLabel("興趣：");	item4.setBounds(40, 100, 50, 20);
		item5 = new JLabel("學歷：");	item5.setBounds(40, 130, 50, 20);
		item6 = new JLabel("居住地區：");	item6.setBounds(200, 130, 70, 20);
		item7 = new JLabel("加入日期：");	item7.setBounds(40, 230, 70, 20);
		item8 = new JLabel();	item8.setBounds(110, 230, 80, 20);	//	放置日期內容的物件
		panel2.add(item1);	panel2.add(item2);	panel2.add(item3);
		panel2.add(item4);	panel2.add(item5);	panel2.add(item6);
		panel2.add(item7);	panel2.add(item8);
		
		//	帳號姓名欄位
		textName = new JTextField(10);	//	10代表預設字元框框寬度
		textName.setBounds(80, 60, 80, 20);
		panel2.add(textName);
		//	年齡
		spinner = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));	//	SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
		spinner.setBounds(240, 60, 80, 20);
		panel2.add(spinner);
		//	性別
		btn_group = new ButtonGroup();
		rb1 = new JRadioButton("男性", false);	rb1.setBounds(80, 80, 60, 20);
		rb2 = new JRadioButton("女性", false);	rb2.setBounds(140, 80, 60, 20);
		btn_group.add(rb1);	btn_group.add(rb2);	// 加入ButtonGroup才可單選
		panel2.add(rb1);	panel2.add(rb2);
		//	興趣
		cb1 = new JCheckBox("電腦");	cb1.setBounds(80, 100, 60, 20);
		cb2 = new JCheckBox("唱歌");	cb2.setBounds(140, 100, 60, 20);
		cb3 = new JCheckBox("電影");	cb3.setBounds(200, 100, 60, 20);
		cb4 = new JCheckBox("繪圖");	cb4.setBounds(260, 100, 60, 20);
		cb5 = new JCheckBox("旅遊");	cb5.setBounds(320, 100, 60, 20);
		panel2.add(cb1);	panel2.add(cb2);	panel2.add(cb3);
		panel2.add(cb4);	panel2.add(cb5);
		//	學歷
		c_box = new JComboBox<Object>(edu_label);	//	new JComboBox(edu_label)
		c_box.setBounds(80, 130, 100, 20);
		panel2.add(c_box);
		//	居住地
		list = new JList<Object>(city_label);
		JScrollPane s_pane = new JScrollPane(list);
		s_pane.setBounds(270, 130, 80, 80);
		panel2.add(s_pane);
		//	新增下方取消、確認、刪除等按鈕
		qb31 = new JButton("取消"); qb31.setBounds(270, 230, 60, 30);
		qb32 = new JButton("確認"); qb32.setBounds(340, 230, 60, 30);
		qb33 = new JButton("刪除"); qb33.setBounds(410, 230, 60, 30);
		qb31.addActionListener(reset);
		qb32.addActionListener(submit);
		qb33.addActionListener(delete);
		panel2.add(qb31);	panel2.add(qb32);	panel2.add(qb33);
		add(panel2);
		panel2.setVisible(false);	// 尚未註冊或登入前將個人資料面板隱藏
		
		//	JFrame視窗基本設定
		setTitle("會員註冊登錄系統");
		setSize(500, 360);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	//	按鈕監聽事件 : 註冊、登入
	public ActionListener checkActionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			id_get = id.getText().trim();	// 取得去除頭尾空白格的字串，再給id_get
//			password_get = String.valueOf(password.getPassword()).trim();
			password_get = new String(password.getPassword()).trim();
			
			//	如果帳號或密碼欄位空白則不處理
			if ("".equals(id_get) || "".equals(password_get)) {
				warningMessage("帳號及密碼欄位請勿空白");
				return;
			}
			//	如果帳號密碼超過10字則顯示警告，並且不處理
			if ((id_get.length() > 10) || (password_get.length() > 10)) {
				warningMessage("帳號最多10個字元，密碼8-10個字元");
				return;
			}
			//	正規表示法	預防 SQL Injection
			if (!(id_get.matches("^[0-9a-zA-Z._]{1,10}$") && password_get.matches("^[0-9a-zA-Z._@]{8,10}$"))) {
				warningMessage("帳號只能輸入數字英文、.、_\n密碼只能輸入數字英文、.、_、@");
				return;
			}
			//	依據輸入帳號&密碼查詢資料庫
			try {
				input_sql = "SELECT * FROM personal_data WHERE acc_id=? AND password=?";
				pstmt = conn.prepareStatement(input_sql);
				pstmt.setString(1, id_get);
				pstmt.setString(2, password_get);
				result = pstmt.executeQuery(); // 把執行結果傳回result
				if (result.next()) {	//	若為true代表有查詢到正確的資料
					if (e.getSource() == qb11) {	//	java.util.EventObject底下的.getSource()方法
						//	如果按下的是註冊，代表帳號重複，不能繼續處理
						warningMessage("帳號已被註冊");
						return;
					} else {
//						if ((e.getSource() == qb12) && (password_get == result.getString("password").trim())) {		// 錯誤比較
						if ((e.getSource() == qb12) && (password_get.equals(result.getString("password").trim()))) {		// 正確比較
							//	如果按下的是登入，且密碼正確。
							//可查詢、修改、刪除資料。
							btn_act = 2;	// 設定為2代表已按下登入
							initialProcess();	//	panel2介面初值處理
							//	取得資料庫內的存檔紀錄
							item8.setText(result.getString("date_join"));	//	加入日期:資料表內的欄位名稱
							textName.setText(result.getString("name"));	//	讀取資料表內的欄位(姓名)
							spinner.setValue(result.getInt("age"));	//	讀取資料表的年齡欄位
							int gender = result.getInt("gender");	//	讀取資料表的性別欄位 (男=1, 女=2)
							if (gender == 1) {
								rb1.setSelected(true);	//	若讀取性別欄位為1，則設定JRadioButton.setSelected(true)
							}
							cb1.setSelected(result.getBoolean("habbit1"));	//	讀取資料表的興趣checkbox欄位
							cb2.setSelected(result.getBoolean("habbit2"));	//	興趣2
							cb3.setSelected(result.getBoolean("habbit3"));	//	興趣3
							cb4.setSelected(result.getBoolean("habbit4"));	//	興趣4
							cb5.setSelected(result.getBoolean("habbit5"));	//	興趣5
							c_box.setSelectedIndex(result.getInt("education"));	//	讀取資料表的學歷欄位 (代號 0~5)
							list.setSelectedIndex(result.getInt("home"));	//	居住地代號：1-16
						} else {
							warningMessage("密碼錯誤");
							return;
						}
					}
				} else {
					//	找不到符合帳號密碼條件資料
					if (e.getSource() == qb11) {
						//	如果按下的是註冊，可查詢、更正資料
						btn_act = 1;	// 使用者按鈕狀態 1 = 註冊 狀態
						initialProcess();	//	介面初值處理
						defaultValueProcess();	// 預設值寫入處理
					}else {
						//	如果使用者按下的是登入，因為不符合帳號密碼，所以不能繼續處理
						warningMessage("帳號或密碼錯誤");
						return;
					}
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				errorMessage("資料庫發生錯誤");
			}
		}
	};
	
	// 自訂Method: 資料庫嚴重錯誤對話框
	private void errorMessage(String msg) {
		String message = msg;
		JOptionPane.showMessageDialog(null, message, "嚴重錯誤", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}

	//	自訂Method: 錯誤訊息對話面板
	private void warningMessage(String msg) {
		String message = msg;
		JOptionPane.showMessageDialog(null, message, "錯誤訊息", JOptionPane.ERROR_MESSAGE);
		id.setText("");
		password.setText("");
	}
	
	//	自訂Method: 資料庫異動成功對話框
	private void correctMessage(String msg) {
		String message = msg;
		JOptionPane.showMessageDialog(null, message, "資料庫異動", JOptionPane.INFORMATION_MESSAGE);
	}
	
	//	panel2介面初值處理
	protected void initialProcess() {
		id.setEnabled(false);	//	上方輸入介面無作用
		password.setEnabled(false);
		qb11.setEnabled(false);
		qb12.setEnabled(false);
		//	顯示下方個人資料面板data
		panel2.setVisible(true);
		if (btn_act == 1) {
			//	若在註冊模式btn_act = 1，則讓"刪除"按鈕失效
			qb33.setEnabled(false);
		}
	}
	
	//	介面結束處理
	protected void endProcess() {
		id.setEnabled(true);		id.setText("");
		password.setEnabled(true);	password.setText("");
		panel2.setVisible(false);
		qb11.setEnabled(true);
		qb12.setEnabled(true);
		qb33.setEnabled(true);	//	恢復刪除按鈕功能
		return;
	}
	
	//	重置輸入處理
	protected void resetProcess(String msg) {
		String message = msg;
		JOptionPane.showMessageDialog(null, message, "錯誤訊息", JOptionPane.ERROR_MESSAGE);
		initialProcess();
		defaultValueProcess();
	}
	
	//	預設值寫入處理
	protected void defaultValueProcess() {
		item8.setText(dateNow);	//	加入日期時間由系統抓取Calendar物件的時間
		//	個人資料內容初值由系統提供 (註冊狀態)
		textName.setText("");	//	姓名欄位空白
		spinner.setValue(28);	//	年齡欄位預設28
		rb1.setSelected(false);	rb2.setSelected(false);	//	性別預設空白
		cb1.setSelected(false);	cb2.setSelected(false);
		cb3.setSelected(false);	cb4.setSelected(false);
		cb5.setSelected(false);	//	興趣欄位預設空白
		c_box.setSelectedIndex(0);	//學歷預設欄位博士
		list.setSelectedIndex(0);	//居住地預設欄位台北
	}
	
	//	取消按鈕事件監聽
	public ActionListener reset = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			endProcess();	//	無任何動作，直接進入>介面結束處理
		}
	};
	//	確認按鈕事件監聽
	public ActionListener submit = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			
			//	正則表示法檢查輸入字元 (這是最後才加入的if判斷)
//			if (("".equals(textName.getText()) || textName.getText().equals("^[^a-zA-Z\u2E80-\u9FA5]{1,10}$"))) {	// equals錯誤用法
			if (("".equals(textName.getText()) || textName.getText().matches("^[^a-zA-Z\u2E80-\u2FDF\u4E00-\u9FFF]{1,10}$"))) {
				//輸入錯誤
				resetProcess("姓名請輸入中文或英文，最多10個字元\n資料已重置，請重新輸入正確資料"); // 資料reset，需重新輸入所有資料
			}else {
				//輸入正確
				// 區塊開始
				int mgender; // 供設定性別狀態識別用途
				Boolean mcb1, mcb2, mcb3, mcb4, mcb5;	// 紀錄興趣是否被勾選
				//	讀取性別選擇圓鈕，將boolean值轉為數字代號
				if (rb1.isSelected()) {
					mgender = 1;
				}else mgender = 2;
				//	取得興趣複選方鈕的boolean值
				mcb1 = cb1.isSelected();
				mcb2 = cb2.isSelected();
				mcb3 = cb3.isSelected();
				mcb4 = cb4.isSelected();
				mcb5 = cb5.isSelected();
				
				try {
					//如果在註冊狀態btn_act == 1，資料庫插入新資料
					if (btn_act == 1) {
						input_sql = "INSERT INTO personal_data (acc_id, password, date_join, name, gender, age, habbit1, habbit2, habbit3, habbit4, habbit5, education, home) "
								+ "VALUES( ?, ?, ?, ?," + mgender + ", " + spinner.getValue() + ", " + mcb1 + ", " + mcb2 + ", " + mcb3 + ", " + mcb4 + ", " + mcb5 + ", " + c_box.getSelectedIndex() + ", " + list.getSelectedIndex() + ");";
						pstmt = conn.prepareStatement(input_sql);
						pstmt.setString(1, id_get);
						pstmt.setString(2, password_get);
						pstmt.setDate(3, toSqlDate);
						pstmt.setString(4, textName.getText().trim());
					} else {
						//	如果在登入狀態btn_act == 2，資料庫對現有資料的內容更新
//						input_sql = "UPDATE personal_data SET name = ?, gender = " + mgender + ", age = " + spinner.getValue() + ", habbit1 = " + mcb1 + ", habbit2 = " + mcb2 + ", habbit3 = " + mcb3 + ", habbit4 = " + mcb4 + ", habbit5 = " + mcb5 + ", education = " + c_box.getSelectedIndex() + ", home = " + list.getSelectedIndex() + "WHERE acc_id = " + id_get + " AND password = " + password_get + ";";
						// 注意字串前後得多加單引號'
						input_sql = "UPDATE personal_data SET name = ?, gender = ?, age = ?, habbit1 = ?, habbit2 = ?, habbit3 = ?, habbit4 = ?, habbit5 = ?, education = " + c_box.getSelectedIndex() + ", home = " + list.getSelectedIndex() + " WHERE acc_id = '" + id_get + "' AND password = '" + password_get + "';";
						// id_get與password_get在進入此迴圈前已有檢查輸入字元，故這裡暫時用動態式查詢。
						pstmt = conn.prepareStatement(input_sql);
						pstmt.setString(1, textName.getText().trim());	pstmt.setInt(2, mgender);
						pstmt.setObject(3, spinner.getValue());			pstmt.setBoolean(4, mcb1);
						pstmt.setBoolean(5, mcb2);						pstmt.setBoolean(6, mcb3);
						pstmt.setBoolean(7, mcb4);						pstmt.setBoolean(8, mcb5);
					}
					pstmt.executeUpdate();
					
					if (btn_act == 1) {
						correctMessage("新會員註冊成功");
					}else {
						correctMessage("資料更新成功");
					}
					endProcess();	//	介面結束處理
				} catch (SQLException e1) {
					e1.printStackTrace();
					errorMessage("資料庫發生錯誤");
				}	// 區塊結束
			}
		}
	};
	//	刪除按鈕事件監聽
	public ActionListener delete = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			//	刪除資料前需再次確認
			JOptionPane option = new JOptionPane("請確認刪除或取消",JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
			JDialog dialog = option.createDialog(null, "刪除確認");
			dialog.setVisible(true);
			Object answer = option.getValue(); // 回傳值: YES為0, NO為1
			if (answer != null) {
				if ((Integer)answer == 0) {
					//	資料庫刪除該筆資料
					input_sql = "DELETE FROM personal_data WHERE acc_id = ? AND password = ?;";
					try {
						pstmt = conn.prepareStatement(input_sql);
						pstmt.setString(1, id_get);
						pstmt.setString(2, password_get);
						pstmt.executeUpdate();
						correctMessage("資料刪除成功");
						endProcess();
					} catch (SQLException e1) {
						e1.printStackTrace();
						errorMessage("資料庫發生錯誤");
					}
				}
			}
		}
	};
}

public class HW02_JDBC_Member01_4 {

	public static void main(String[] args) {
		new mysqlFrame4();
	}
}
