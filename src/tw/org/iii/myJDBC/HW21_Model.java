package tw.org.iii.myJDBC;

import java.io.Serializable;
import java.sql.Date;
import java.util.Calendar;

/*		java物件序列化，並儲存到檔案和資料庫
 * 		
 * 		實現可序列化的介面，以利後續的存取操作
 * 
 * 		=>	要讓物件實現 Serializable 介面，這樣就能將java物件用
 * 			二進制流儲存並恢復。
 * 			
 */

public class HW21_Model implements Serializable {
	private String name;	//	姓名
	private int year;		//	年齡
	private String city;	//	居住城市
	private Date birth;		//	生日
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		this.year = year;
		this.city = city;
	}
	
	public int getYear() {
		return year;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public Date getBirth() {
		return birth;
	}
	
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	
	/*	@see java.lang.Object#toString()
	 * 	override toString，不然序列化之後顯示的是記憶體位址
	 */
	
	@Override
	public String toString() {
		return this.name + " " + this.year + " " + this.city + " " + this.birth.toString();
	}
}
