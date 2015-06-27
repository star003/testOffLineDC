import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class offlineData {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
		newDb.createTables();
		
		newDb.getWeekGis();
		
		newDb.getFinans();
		
	}//public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException

}//public class offlineData

class newDb {
	/*
	 * проверим на существование базы данных
	 * и создадим при необходимости
	 */
	static String DB_NAME 			= "tOffLine";
	static String TBL_GISMETEO_WEEK = 
			"CREATE TABLE IF NOT EXISTS gismeteoWeek(ID INTEGER PRIMARY KEY AUTOINCREMENT,descr TEXT, phenomen TEXT ,rain TEXT ,tm TEXT,pr TEXT,wDir TEXT,wSpeed TEXT,gr TEXT);";
	static String TBL_FINANS =
			"CREATE TABLE IF NOT EXISTS finans(ID INTEGER PRIMARY KEY AUTOINCREMENT,descr TEXT,cur TEXT,delta TEXT,vol TEXT,time TEXT);";
	
	public static boolean isWrite = false;
	
	newDb() throws ClassNotFoundException, SQLException{
		
		createTables();
		clearTables();
		
	}//newDb()
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	описание:
	//		создадим таблицы в базе , если их нет.
	static public void createTables() throws SQLException, ClassNotFoundException {
		
		isWrite = true;
		Class.forName("org.sqlite.JDBC");
		Connection bd = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st  = bd.createStatement();
		st.setQueryTimeout(60);
		
		st.execute(TBL_GISMETEO_WEEK);
		st.execute(TBL_FINANS);
		
		st.close();
		isWrite = false;
		
	}//static public void createTables() throws SQLException, ClassNotFoundException

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	описание:
	//		создадим таблицы в базе , если их нет.
	static public void clearTables() throws SQLException, ClassNotFoundException {
		
		isWrite = true;
		Class.forName("org.sqlite.JDBC");
		Connection bd = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st  = bd.createStatement();
		st.setQueryTimeout(60);
		
		st.execute("delete from gismeteoWeek;");
		st.execute("delete from finans;");
		
		st.close();
		isWrite = false;
		
	}//static public void clearTables() throws SQLException, ClassNotFoundException 
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	описание:
	//		погода с сайта
	static void getWeekGis() throws IOException, ClassNotFoundException, SQLException {
		
		ArrayList<ArrayList<String>> x = gisFromSite.grabGismeteo();
		int row = x.get(1).size();
		
		if(row<13) {
			
			return;	//**на случай если что то заполнилось не так - просто выйдем
			
		}
		
		//**перепишем первую строку как надо.
		
		String bf[] = new String[x.get(0).size()];
		
		for (int i = 0; i < x.get(0).size(); i++) {
			
			bf[i] = x.get(0).get(i);
			
		}
		
		x.get(0).remove(x.get(0).size()-1);
		x.get(0).remove(x.get(0).size()-1);
		x.get(0).remove(x.get(0).size()-1);
		x.get(0).remove(x.get(0).size()-1);
		
		for (int i = 0; i<bf.length ; i++){
			
			if (i==0) {
				
				x.get(0).add(bf[i]);
			}
			
			else {
				
				x.get(0).add(bf[i]);
				x.get(0).add(bf[i]);
				x.get(0).add(bf[i]);
				x.get(0).add(bf[i]);
				
			}
			
		}
		
		//**сложим все в базу данных
		isWrite = true;
		Class.forName("org.sqlite.JDBC");
		Connection bd = DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st  = bd.createStatement();
		st.setQueryTimeout(60);
		st.execute("delete from gismeteoWeek;");
		
		for (int i = 0; i < 13 ; i++) {
			
			String stProm = "'"+x.get(0).get(i)+"','"+x.get(1).get(i)+"','"+x.get(2).get(i)+"','"
							+x.get(3).get(i)+"','"+x.get(4).get(i)+"','"+x.get(5).get(i)
							+"','"+x.get(6).get(i)+"','"+x.get(7).get(i)+"'";
			String str = "INSERT INTO gismeteoWeek (descr,phenomen,rain , tm,pr,wDir ,wSpeed,gr ) VALUES("+stProm+")";
			
			st.execute(str);
			
		}
		
		st.close();
		isWrite = false;
		
	}//static void getWeekGis() throws IOException
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	описание:
	//		фин лабуда
	static void getFinans() throws IOException, ClassNotFoundException, SQLException{
		
		ArrayList<String> a = priceBRENT.usdFinam();
		ArrayList<String> b = priceBRENT.dynBrentFinam();
		
		isWrite = true;
		//**сложим все в базу данных
		Class.forName("org.sqlite.JDBC");
		Connection bd 	= DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st  	= bd.createStatement();
		st.setQueryTimeout(60);
		
		st.execute("delete from finans;");
		
		String stProm 	= "'usd','"+a.get(0)+"','"+a.get(1)+"','"+a.get(6)+"','"+a.get(7)+"'";
		String str 		= "INSERT INTO finans (descr ,cur ,delta,vol ,time) VALUES("+stProm+")";
		
		st.execute(str);
		
		stProm 			= "'brent','"+b.get(0)+"','"+b.get(1)+"','"+b.get(6)+"','"+b.get(7)+"'";
		str 			= "INSERT INTO finans (descr ,cur ,delta,vol ,time) VALUES("+stProm+")";
		
		st.execute(str);
		st.close();
		isWrite = false;
		
	}//static void getFinans() throws IOException
		
}//class newDb