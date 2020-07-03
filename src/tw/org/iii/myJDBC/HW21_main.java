package tw.org.iii.myJDBC;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//import tw.org.iii.myJDBC.HW21_Model;
//import tw.org.iii.myJDBC.HW21_serializableToFile;
//import tw.org.iii.myJDBC.HW21_serialObjToDb;

/*		HW21_main方法 => 測試物件儲存及讀取
 * 		
 * 		注意：測試前請把MySQL的資料表內容刪除乾淨再測試，
 * 			可避免出現反序列化錯誤。
 * 			
 * 		錯誤訊息：java.io.InvalidClassException: XXX; local class incompatible: stream classdesc serialVersionUID
 */

public class HW21_main {
	public HW21_main() {
		//	物件序列化儲存至檔案
		HW21_serializableToFile s2f = new HW21_serializableToFile("./dir01/HW21_obj.txt");
		
		HW21_Model model01 = new HW21_Model();
		model01.setName("安海瑟威");
		model01.setYear(39);
		model01.setCity("加州");
		model01.setBirth(LocalDate.of(1981, 2, 7));
		s2f.saveObjToFile(model01);	//	model01物件存入s2f物件指定的檔案中
		
		//	從檔案取出被序列化的物件內容，並還原成物件
		HW21_Model model02 = s2f.getObjFromFile();	//	從sf2物件指定檔案中，取出model01物件，恢復至model02物件中
		System.out.println(model02.toString());
		
		//	物件序列化至DB (前置：建立物件內容)
		HW21_Model model03 = new HW21_Model();
		model03.setName("徐巃傣");
		model03.setYear(39);
		model03.setCity("埔里");
		model03.setBirth(LocalDate.of(1981, 1, 5));
		
		HW21_Model model04 = new HW21_Model();
		model04.setName("阿龍");
		model04.setYear(41);
		model04.setCity("台中市");
		model04.setBirth(LocalDate.of(1979, 1, 5));
		
		HW21_Model model05 = new HW21_Model();
		model05.setName("阿傣");
		model05.setYear(42);
		model05.setCity("台中市南屯區");
		model05.setBirth(LocalDate.of(1978, 2, 7));
		
		/*	序列化物件儲存至DB
		 * 	先建立List<HW21_Model>，把物件放入List<HW21_Model>
		 * 	其次，建立s2db物件，才能使用其方法把Obj存入DB
		 */
		List<HW21_Model> setList = new ArrayList<HW21_Model>();
		setList.add(model03);
		setList.add(model04);
		setList.add(model05);
		HW21_serialObjToDb s2db = new HW21_serialObjToDb();
		s2db.saveModelToDB(setList); // 存入DB
		System.out.println("HW21_main：序列化物件存入DB完成");
		
		System.out.println("----分隔線----");
		/*	從DB取出序列化的物件內容，並列印內容
		 * 	1)	先建立List<HW21_Model>的getList物件。
		 * 		把已存入s2db物件，透過其getModel()方法，
		 * 		把存入的序列化物件取出並指定給getList物件。
		 * 	2)	使用for迴圈取得List<HW21_Model>內容。
		 */
		List<HW21_Model> getList = s2db.getModel();
		for (int i = 0; i < getList.size(); i++) {
			System.out.println(getList.get(i).toString());
		}
		System.out.println("HW21_main：反序列化成功");
	}
	
	public static void main(String[] args) {
		new HW21_main();
	}

}
