package tw.org.iii.myJDBC;

import java.sql.Driver; // 在Module-info中，使用指令 requires java.sql;，即可正常載入 java.sql.*
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
		
		String jdbcName =  "com.mysql.cj.jdbc.Driver";
		
		try {
			System.out.println("查看載入的Class-Path : " + System.getProperty("java.class.path"));
			Class.forName(jdbcName); // MySQL 8.0.18的驅動程式名稱有變
//			Class.forName("com.mysql.jdbc.Driver"); // MySQl 5.1的驅動程式名稱
			System.out.println("OK: Driver Loaded");
			//	reference設定完後，顯示OK代表專案已經自動載入Driver
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Driver Not Found");
		}
	}

}

/*		2020-FEB-06
 * 		ConnectorJ 8.0.19 可能有bug，其 com.mysql.cj.jdbc 下的 Driver Class，沒有包含 source。
 * 		造成 Class.forName("com.mysql.cj.jdbc.Driver") 載入JVM執行時，
 * 		出現錯誤  Exception in thread "main" java.lang.NoClassDefFoundError: java.sql.Driver
 * 		及 Caused by: java.lang.ClassNotFoundException: java.sql.Driver
 * 
 * 		以上兩種錯誤都是JVM找不到類別而拋出的錯誤，但型別卻不同。 (皆為Throwable的子孫類別)
 * 
 * 		型別：
 * 			NoClassDefFoundError = Errors 系列
 * 				Errors系列錯誤拋出，代表此錯誤是無法在程式內修復的，正常情況下不應出現。
 * 			ClassNotFoundException = Exception 系列
 * 				此錯誤允許在應用程式中處理，屬於可預測的錯誤。(可在捕捉拋出的程式區塊內做其他處理)
 * 		
 * 		NoClassDefFoundError 代表JVM載入Class時，找不到class定義而拋出的錯誤。
 * 		但其實在java文件中，它歸類於 java.lang 下的 Errors，而非 Exceptions。
 * 		
 * 		ClassNotFoundException 代表JVM載入Class時，發現Class不存在所拋出的例外。
 * 		在java文件中，歸類於 java.lang 下的 Exceptions。
 * 		可在 try-catch 的 Exception程式區塊，適時放入e.printStackTrace()觀察列出的資訊。
 * 		
 * 		Eclipse的專案 Library:
 * 			引入的jar檔案會放在這裡。(需在"環境變數"設置CLASSPATH=實體路徑;)
 * 			才能讓JVM找到正確路徑載入jar檔案。
 * 
 * 		Class.forName(className) 方法：
 * 			用於註冊類別，把類別的字串名稱放在裏頭，(需先引入Library)讓JVM去載入該類別，進行註冊，
 * 			若有載入成功會回傳該類別的名稱。
 * 
 * 		猶如 console 輸出所提示，此次例外是ClassNotFoundException找不到類別 (source not found)，
 * 		進而引發 NoClassDefFoundError。
 * 
 * 		找到原因是驅動程式jar檔案中的其中一個類別，com.mysql.cj.jdbc 下的 Driver Class，
 * 		沒有附上source，導致一連串例外錯誤。
 * 		把驅動程式jar檔改換成舊一點的版本後重測OK。
 * 		
 */
