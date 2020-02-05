package tw.org.iii.myJDBC;

//import java.sql.*; // JRE有，但無法載入 java.sql.*;
import java.lang.reflect.Method;

/*		20180826AM2 JDBC 基本設定
 * 		載入 MySQL Driver設定
 * 		Class物件
 */
public class HW01 {

	public static void main(String[] args) {
		//	指定載入特定的類別名稱	[JDBC載入驅動程式的方式之一]
		//	Class.forName(className);
		
		//	Class類別物件方法介紹
//		String str1 = new String();
//		String str2 = "";
//		Class class1 = str1.getClass();
//		System.out.println(class1.getName());
//		
//		//	取得class1的父類別物件 (Object)
//		Class class2 = class1.getSuperclass();
//		System.out.println("getSuperclass() : " + class2.getName());
//		
//		//	使用class.getModifiers方法
//		int modfy = class1.getModifiers();
//		System.out.println("modfy: " + modfy);
//		
//		//	.getDeclaredMethods()
//		//	回傳此class宣告方法物件的陣列
//		Method[] methods = class2.getDeclaredMethods();
//		for (Method method : methods) {
//			System.out.println("getDeclaredMethods() : " + method.getName());
//		}
		
		//	載入JDBC Driver
		//	
		//	故障排除：https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-driver-name.html
		
		try {
			System.out.println(System.getProperty("java.class.path"));
			Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 8.0的驅動程式名稱有變
//			Class.forName("com.mysql.jdbc.Driver"); // MySQl 5.1的驅動程式名稱
			System.out.println("OK: Driver Loaded");
			//	reference設定完後，顯示OK代表專案已經自動載入Driver
		} catch (ClassNotFoundException e) {
			System.out.println("Driver Not Found");
		}
	}

}
