


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/////////////////////////////////////////////////////////////////////////////////////////////////////////
//	��������:
//		��������� ������ � ���� �� �����

public class priceBRENT {
	
	static ArrayList<String> item = new ArrayList<String>();
	static ArrayList<String> itemVal = new ArrayList<String>();
	final String url = "http://www.finam.ru/";
	static String indicator;
	static String indicatorValue;
	static String currTime;

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	priceBRENT() throws IOException {
		Document doc  = Jsoup.connect(url).get();
		Elements metaElements = doc.select("span.usd.sm.pl05");
		for (Element x:metaElements) {
			itemVal.add(x.text());
		}
		Elements metaElementsName = doc.select("td.fst-col");
		
		for (Element x:metaElementsName) {
			item.add(x.text());
		}
		currTime = currTime();
	}//priceBRENT() throws IOException
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	priceBRENT(String indicator) throws IOException ,SocketTimeoutException {
		priceBRENT.indicatorValue = indicator;
		Document doc  = Jsoup.connect(url).get();
		Elements metaElements = doc.select("span.usd.sm.pl05");
		for (Element x:metaElements) {
			itemVal.add(x.text());
		}
		int i = 0;
		Elements metaElementsName = doc.select("td.fst-col");
		for (Element x:metaElementsName) {
			if (x.text().indexOf(indicator)>0){
				priceBRENT.indicatorValue = itemVal.get(i);
			}
			item.add(x.text());
			i++;
		}
		currTime = currTime();
	}//priceBRENT(String indicator) throws IOException
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static String currTime() {
		Calendar currentTime = Calendar.getInstance();
	    return String.valueOf(currentTime.get(1))+"-"
	    		+ (currentTime.get(2) + 1 >= 10 ? String.valueOf(currentTime.get(2) + 1) : "0"+String.valueOf(currentTime.get(2) + 1)) +"-"
	    		+(currentTime.get(5) >=10 ? String.valueOf(currentTime.get(5))  : "0"+String.valueOf(currentTime.get(5)))+" "
	    		+((currentTime.get(11)) >=10 ? String.valueOf(currentTime.get(11))  : "0"+String.valueOf(currentTime.get(11)))+":"
	    		+((currentTime.get(12)) >=10 ? String.valueOf(currentTime.get(12))  : "0"+String.valueOf(currentTime.get(12)))+":"
	    		+((currentTime.get(13)) >=10 ? String.valueOf(currentTime.get(13))  : "0"+String.valueOf(currentTime.get(13)));
	    				
	}//public static String currTime()
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		
		new priceBRENT("Brent*");
		System.out.println(priceBRENT.indicatorValue);
		System.out.println(currTime());
	}//public static void main(String[] args) throws IOException

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static String getVal() {
		return indicatorValue;
	}//public static String getVal()

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static String investing ()   throws IOException {
		Document doc;
		try {
			doc = Jsoup.connect("http://www.investing.com/commodities/brent-oil")
					.userAgent("Mozilla")
					.get();
		
			Elements metaElements = doc.select("span.arial_26.pid-8833-last");
			for (Element x:metaElements) {
				return x.text();
			}
		
		} catch (IOException e) {
			return "";
		}
		return "";
	}//public static String investing () throws IOException
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static String tinkoff() throws IOException {
		Document doc  = Jsoup.connect("http://www.londonstockexchange.com/exchange/prices-and-markets/stocks/summary/company-summary.html?fourWayKey=US87238U2033USUSDIOBE")
				.userAgent("Mozilla")
				.get();
		Elements metaElements = doc.select("tr.odd");
				
		return metaElements.first().text().split(" ")[0];
	}//public static String tinkoff()
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static String usd() throws IOException {
		try {
			Document doc  = Jsoup.connect("http://www.finam.ru")
				.userAgent("Mozilla")
				.get();
			Elements metaElements = doc.select("a.dark.no");
			return metaElements.first().text();
		} catch (IOException e) {
			return "";
		}	
	}//public static String usd() throws IOException
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static String gis() throws IOException {
		String rez=" ";
		String rz = "\r\n";
		Document doc  = Jsoup.connect("http://www.gismeteo.ru/city/daily/4298/").get();
		Elements a1 = doc.select("div.section.higher");
		//**�����
		rez= rez.concat((a1.select("h2.typeM").first().text())+rz);
		//**�������
		rez=rez.concat(a1.select("dl.cloudness").first().text()+rz);
		//**�����������
		Elements a2 =a1.select("div.temp");
		rez=rez.concat(a2.select("dd.value.m_temp.c").first().text().replaceAll("[^\\d-+]", "")+rz);
		//**�����
		Elements a3 =a1.select("div.wicon.wind");
		rez=rez.concat(a3.select("dl[title]").attr("title"));
		//**�������� �����
		rez=rez.concat(": ");
		rez=rez.concat(a3.select("dd.value.m_wind.ms").first().text().replaceAll("[^\\d]", ""));
		rez=rez.concat(" m/s "+rz);
		//**��������
		rez=rez.concat("��������: ");
		Elements a4 =a1.select("div.wicon.barp");
		rez=rez.concat(a4.select("dd.value.m_press.torr").first().text().replaceAll("[^\\d]", "")+rz);
		
		//**���������
		rez=rez.concat("���������: ");
		rez=rez.concat(a1.select("div.wicon.hum").first().text().replaceAll("[^\\d]", "")+rz);
		return rez;
	}
	
	/*
	TOWN       ���������� � ������ ���������������:
	Index      ���������� ����������� ��� ������
	Sname    �������������� �������� ������
	Latitude   ������ � ����� ��������
	Longitude   ������� � ����� ��������
	FORECAST     ���������� � ����� ���������������:
	day, month, year   ����, �� ������� ��������� ������� � ������ �����
	hour        ������� �����, �� ������� ��������� �������
	tod         ����� �����, ��� �������� ��������� �������: 0 - ���� 1 - ����, 2 - ����, 3 - �����
	weekday   ���� ������, 1 - �����������, 2 - �����������, � �.�.
	predict      ������������������ �������� � �����
	PHENOMENA    ����������� �������:
	cloudiness       ���������� �� ���������:  0 - ����, 1- �����������, 2 - �������, 3 - ��������
	
	precipitation    ��� �������: 4 - �����, 5 - ������, 6,7 � ����, 8 - �����, 9 - ��� ������, 10 - ��� �������
	rpower            ������������� �������, ���� ��� ����. 0 - �������� �����/����, 1 - �����/����
	spower            ����������� �����, ���� ��������������: 0 - �������� �����, 1 - �����
	PRESSURE        ����������� ��������, � ��.��.��.
	TEMPERATURE     ����������� �������, � �������� �������
	WIND     ��������� �����
	min, max          ����������� � ������������ �������� ������� �������� �����, ��� �������
	direction          ����������� ����� � ������, 0 - ��������, 1 - ������-���������,  � �.�.
	RELWET          ������������� ��������� �������, � %
	HEAT            ������� - ����������� ������� �� �������� ������� �� ������ ��������, ���������� �� �����
	*/
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	��������:
	//		������� ������ �� ��������� 1/4 ���
	static String getPrognoz() throws IOException {
		String rez = "";
		String rz = "\n";
		List<String> weekday  		= Arrays.asList("","�����������","�����������","�������","�����","�������","�������","�������");
		List<String> tod  			= Arrays.asList("����","����","����","�����");
		List<String> cloudiness 	= Arrays.asList("����","�����������","�������","��������");
		List<String> precipitation  = Arrays.asList("","","","","�����","������","����","����","�����","��� ������","��� �������");
		//List<String> rpower  		= Arrays.asList("","�������� �����/����","�����/����");
		//List<String> spower  		= Arrays.asList("","�������� �����","�����");
		List<String> direction  	= Arrays.asList("�","��","�","��","�","��","�","��");
		Document doc  = Jsoup.connect("http://informer.gismeteo.ru/xml/27225_1.xml").get();
		Elements FORECAST = doc.select("FORECAST");
		rez = rez.concat((FORECAST.attr("day"))+"-");
		rez = rez.concat(FORECAST.attr("month")+"-");
		rez = rez.concat(FORECAST.attr("year")+" ");
		rez = rez.concat(FORECAST.attr("hour")+" �. ");
		rez = rez.concat(tod.get(Integer.valueOf(FORECAST.attr("tod")))+" ");
		rez = rez.concat(weekday.get(Integer.valueOf(FORECAST.attr("weekday")))+rz);
		Elements PHENOMENA = doc.select("PHENOMENA");
		rez = rez.concat("����������: ");
		rez = FORECAST.attr("cloudiness").equals("") ? rez.concat(cloudiness.get(0))+rz : rez.concat(cloudiness.get(Integer.valueOf(FORECAST.attr("cloudiness")))+rz);
		rez = rez.concat("������: ");
		rez = rez.concat(precipitation.get(Integer.valueOf(PHENOMENA.attr("precipitation")))+rz);
		if (PHENOMENA.attr("precipitation").equals("10")==false) {
			rez = rez.concat(PHENOMENA.attr("rpower")+rz);
			rez = rez.concat(PHENOMENA.attr("spower")+rz);
		}
		rez = rez.concat("���. ����. �� : ");
		
		Elements PRESSURE  = doc.select("PRESSURE");
		rez = rez.concat(PRESSURE .attr("min"));
		rez = rez.concat("...");
		rez = rez.concat(PRESSURE .attr("max")+rz);
		rez = rez.concat("����������� : ");
		Elements TEMPERATURE   = doc.select("TEMPERATURE");
		rez = rez.concat(TEMPERATURE .attr("min"));
		rez = rez.concat("...");
		rez = rez.concat(TEMPERATURE .attr("max")+rz);
		rez = rez.concat("����� ��: ");
		Elements WIND   = doc.select("WIND");
		rez = rez.concat(WIND .attr("min"));
		rez = rez.concat("...");
		rez = rez.concat(WIND .attr("max"));
		rez = rez.concat("�/� ,");
		rez = rez.concat(direction.get(Integer.valueOf(WIND.attr("direction")))+rz);
		rez = rez.concat("��������: ");
		Elements HEAT   = doc.select("HEAT");
		rez = rez.concat(HEAT .attr("min"));
		rez = rez.concat("...");
		rez = rez.concat(HEAT .attr("max")+rz);
		return rez;
	}//static String getPrognoz()
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////
	//	��������
	//		�������� ����� �� ����
	static ArrayList<String> getUsdDay() throws IOException {
		ArrayList<String> x = new ArrayList<String>();
		Document doc  = Jsoup.connect("http://www.micex.ru/issrpc/marketdata/currency/selt/daily/short/result_2014_03_20.xml?boardid=CETS&secid=USD000UTSTOM").get();
		Elements a1 = doc.select("row");

		x.add(a1.attr("LAST"));
		x.add(a1.attr("OPEN"));
		x.add(a1.attr("VOLTODAY"));
		x.add(a1.attr("UPDATETIME"));

		return x;
	}//static ArrayList<String> getUsdDay()
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	�� ������� � �����
	/*
	  	0 ��������� ������ 53,5840
		1 ��������� 0,2490 (0,47%)
		2 �������, max 54,1680
		3 �������, min 53,4620
		4 ���� �������� 53,6760
		5 ����. �������� 53,3350
		6 ����� ������ 2�023�013�000
		7 ����� ���������
		0,1,4,6,7
	 */
	public static ArrayList<String> usdFinam() throws IOException {
		ArrayList<String> x = new ArrayList<String>();
		Document doc  = Jsoup.connect("http://www.finam.ru/profile/mosbirzha-valyutnyj-rynok/usdrubtom-usd-rub/").get();
		Elements a1 = doc.select("td.value");
		int i = 0;
		for (Element d:a1) {
			if(i>5 & i<13 ) {
				x.add(d.text());
			}	
			i++;
		}
		String[] f = gisFromSite.getCurrData();
		x.add(f[3]+":"+f[4]);
		return x;
	}//public static String gis() throws IOException

	/////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	�� brent � �����
	/*
	  	0 ��������� ������ 53,5840
		1 ��������� 0,2490 (0,47%)
		2 �������, max 54,1680
		3 �������, min 53,4620
		4 ���� �������� 53,6760
		5 ����. �������� 53,3350
		6 ����� ������ 2�023�013�000
		7 ����� ���������
		0,1,4,6,7
	 */
	public static ArrayList<String> dynBrentFinam() throws IOException {
		ArrayList<String> x = new ArrayList<String>();
		Document doc  = Jsoup.connect("http://www.finam.ru/profile/tovary/brent/").get();
		Elements a1 = doc.select("td.value");
		int i = 0;
		for (Element d:a1) {
			if(i>5 & i<13) {
				x.add(d.text());
			}	
			i++;
		}
		String[] f = gisFromSite.getCurrData();
		x.add(f[3]+":"+f[4]);
		return x;
	}//public static ArrayList<String> dynBrentFinam() throws IOException

}//public class priceBRENT
