import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class offlineData {
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
		newDb.createTables();
		
		newDb.setWeekGisToBase();
		
		newDb.setFinansToBase();
		
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
	
	static String TBL_GISMETEO_WEEK_TR = 
			"CREATE TABLE IF NOT EXISTS gismeteoWeekTr(ID INTEGER PRIMARY KEY AUTOINCREMENT,a1 TEXT," +
			"a2 TEXT,a3 TEXT,a4 TEXT,a5 TEXT ,a6 TEXT,"+
			"a7 TEXT,a8 TEXT,a9 TEXT,a10 TEXT,a11 TEXT,"+
			"a12 TEXT,a13 TEXT);";
	
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
		st.execute(TBL_GISMETEO_WEEK_TR);
		
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
		st.execute("delete from gismeteoWeekTr;");
		st.execute("delete from finans;");
		
		st.close();
		isWrite = false;
		
	}//static public void clearTables() throws SQLException, ClassNotFoundException 
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	описание:
	//		погода с сайта
	static void setWeekGisToBase() throws IOException, ClassNotFoundException, SQLException {
		
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
		st.execute("delete from gismeteoWeekTR;");
		
		//** в виде как есть...
		
		for (int i = 0 ; i < x.size() ; i++){
			
			PreparedStatement queryTR = bd.prepareStatement("INSERT INTO gismeteoWeekTr (a1,a2,a3,a4,a5,a6,a7,a8,a9,a10,a11,a12,a13 ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);");
			
			for(int j=0 ; j<x.get(i).size() ; j++) {
				
				queryTR.setString(j+1, x.get(i).get(j));
				
			}
			
			queryTR.execute();
		}
		
		for (int i = 0; i < 13 ; i++) {
			
			PreparedStatement query = bd.prepareStatement("INSERT INTO gismeteoWeek (descr,phenomen,rain , tm,pr,wDir ,wSpeed,gr ) VALUES(?,?,?,?,?,?,?,?);");
			
			for (int j=1;j<9;j++){
				
				query.setString(j,x.get(j-1).get(i));
			
			}
			
			query.execute();
			
		}
		
		st.close();
		isWrite = false;
		
	}//static void setWeekGisToBase() throws IOException
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	описание:
	//		фин лабуда
	static void setFinansToBase() throws IOException, ClassNotFoundException, SQLException{
		
		ArrayList<String> a = priceBRENT.usdFinam();
		ArrayList<String> b = priceBRENT.dynBrentFinam();
		
		isWrite = true;
		//**сложим все в базу данных
		Class.forName("org.sqlite.JDBC");
		Connection bd 	= DriverManager.getConnection("jdbc:sqlite:"+DB_NAME+".db");
		Statement st  	= bd.createStatement();
		st.setQueryTimeout(60);
		
		st.execute("delete from finans;");
		
		List<Integer> ind = Arrays.asList(0,1,6,7);
		
		PreparedStatement q1 , q2 ;
		q1 = q2 = bd.prepareStatement("INSERT INTO finans (descr ,cur ,delta,vol ,time) VALUES('usd',?,?,?,?);");
		
		for(int j = 0 ;j < ind.size();j++){
			
			q1.setString(j+1, a.get(ind.get(j)));
			q2.setString(j+1, b.get(ind.get(j)));
			
		}
		
		q1.execute();
		q2.execute();
		
		st.close();
		isWrite = false;
		
	}//static void setFinansToBase() throws IOException
		
}//class newDb