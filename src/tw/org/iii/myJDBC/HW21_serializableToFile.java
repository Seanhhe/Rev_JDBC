package tw.org.iii.myJDBC;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/*		(此為單一Class)
 * 		儲存物件到檔案並恢復
 * 		=> 
 * 		要儲存到檔案首先必須得獲得檔案輸入流，然後將檔案輸入流作為引數，
 * 		建構物件輸入流，然後就能直接將物件輸入到檔案中。而要將物件恢復，
 * 		則需要先獲得檔案輸出流，然後將檔案輸出流作為引數，構造物件輸出流，
 * 		就能夠得到物件，然後再強制性轉換為原始物件即可。
 * 		
 */

public class HW21_serializableToFile {
	private String fileName = "./dir01/HW21_obj.txt";
	
//	public HW21_serializableToFile() {
//		
//	}
	
	public HW21_serializableToFile(String fileName) {
		this.fileName = fileName;
	}
	
	/*
	 * 	將 HW21_Model 物件儲存到檔案中
	 * 	params:
	 * 		M:model類物件
	 */
	public void saveObjToFile(HW21_Model M) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
			
			oos.writeObject(M);		//	把HW21_Model物件M，寫入到oos
			
			oos.close();	//	關閉檔案流
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("找不到檔案");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IO Exception");
		}
		
	}
	
	/*
	 * 	從檔案中讀出物件，並且返回HW21_Model物件
	 */
	public HW21_Model getObjFromFile() {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
			
			HW21_Model model = (HW21_Model) ois.readObject();
		
			return model;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
