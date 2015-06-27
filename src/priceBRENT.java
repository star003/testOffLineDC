


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
//	описание:
//		получение данных о цене на нефть

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
		//**город
		rez= rez.concat((a1.select("h2.typeM").first().text())+rz);
		//**явления
		rez=rez.concat(a1.select("dl.cloudness").first().text()+rz);
		//**температура
		Elements a2 =a1.select("div.temp");
		rez=rez.concat(a2.select("dd.value.m_temp.c").first().text().replaceAll("[^\\d-+]", "")+rz);
		//**ветер
		Elements a3 =a1.select("div.wicon.wind");
		rez=rez.concat(a3.select("dl[title]").attr("title"));
		//**скорость ветра
		rez=rez.concat(": ");
		rez=rez.concat(a3.select("dd.value.m_wind.ms").first().text().replaceAll("[^\\d]", ""));
		rez=rez.concat(" m/s "+rz);
		//**давление
		rez=rez.concat("давление: ");
		Elements a4 =a1.select("div.wicon.barp");
		rez=rez.concat(a4.select("dd.value.m_press.torr").first().text().replaceAll("[^\\d]", "")+rz);
		
		//**влажность
		rez=rez.concat("влажность: ");
		rez=rez.concat(a1.select("div.wicon.hum").first().text().replaceAll("[^\\d]", "")+rz);
		return rez;
	}
	
	/*
	TOWN       информация о пункте прогнозирования:
	Index      уникальный пятизначный код города
	Sname    закодированное название города
	Latitude   широта в целых градусах
	Longitude   долгота в целых градусах
	FORECAST     информация о сроке прогнозирования:
	day, month, year   дата, на которую составлен прогноз в данном блоке
	hour        местное время, на которое составлен прогноз
	tod         время суток, для которого составлен прогноз: 0 - ночь 1 - утро, 2 - день, 3 - вечер
	weekday   день недели, 1 - воскресенье, 2 - понедельник, и т.д.
	predict      заблаговременность прогноза в часах
	PHENOMENA    атмосферные явления:
	cloudiness       облачность по градациям:  0 - ясно, 1- малооблачно, 2 - облачно, 3 - пасмурно
	
	precipitation    тип осадков: 4 - дождь, 5 - ливень, 6,7 – снег, 8 - гроза, 9 - нет данных, 10 - без осадков
	rpower            интенсивность осадков, если они есть. 0 - возможен дождь/снег, 1 - дождь/снег
	spower            вероятность грозы, если прогнозируется: 0 - возможна гроза, 1 - гроза
	PRESSURE        атмосферное давление, в мм.рт.ст.
	TEMPERATURE     температура воздуха, в градусах Цельсия
	WIND     приземный ветер
	min, max          минимальное и максимальное значения средней скорости ветра, без порывов
	direction          направление ветра в румбах, 0 - северный, 1 - северо-восточный,  и т.д.
	RELWET          относительная влажность воздуха, в %
	HEAT            комфорт - температура воздуха по ощущению одетого по сезону человека, выходящего на улицу
	*/
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	описание:
	//		прогноз погоды на следующее 1/4 дня
	static String getPrognoz() throws IOException {
		String rez = "";
		String rz = "\n";
		List<String> weekday  		= Arrays.asList("","воскресенье","понедельник","вторник","среда","четверг","пятница","суббота");
		List<String> tod  			= Arrays.asList("ночь","утро","день","вечер");
		List<String> cloudiness 	= Arrays.asList("ясно","малооблачно","облачно","пасмурно");
		List<String> precipitation  = Arrays.asList("","","","","дождь","ливень","снег","снег","гроза","нет данных","без осадков");
		//List<String> rpower  		= Arrays.asList("","возможен дождь/снег","дождь/снег");
		//List<String> spower  		= Arrays.asList("","возможна гроза","гроза");
		List<String> direction  	= Arrays.asList("С","СВ","В","ЮВ","Ю","ЮЗ","З","СЗ");
		Document doc  = Jsoup.connect("http://informer.gismeteo.ru/xml/27225_1.xml").get();
		Elements FORECAST = doc.select("FORECAST");
		rez = rez.concat((FORECAST.attr("day"))+"-");
		rez = rez.concat(FORECAST.attr("month")+"-");
		rez = rez.concat(FORECAST.attr("year")+" ");
		rez = rez.concat(FORECAST.attr("hour")+" ч. ");
		rez = rez.concat(tod.get(Integer.valueOf(FORECAST.attr("tod")))+" ");
		rez = rez.concat(weekday.get(Integer.valueOf(FORECAST.attr("weekday")))+rz);
		Elements PHENOMENA = doc.select("PHENOMENA");
		rez = rez.concat("Облачность: ");
		rez = FORECAST.attr("cloudiness").equals("") ? rez.concat(cloudiness.get(0))+rz : rez.concat(cloudiness.get(Integer.valueOf(FORECAST.attr("cloudiness")))+rz);
		rez = rez.concat("Осадки: ");
		rez = rez.concat(precipitation.get(Integer.valueOf(PHENOMENA.attr("precipitation")))+rz);
		if (PHENOMENA.attr("precipitation").equals("10")==false) {
			rez = rez.concat(PHENOMENA.attr("rpower")+rz);
			rez = rez.concat(PHENOMENA.attr("spower")+rz);
		}
		rez = rez.concat("атм. давл. от : ");
		
		Elements PRESSURE  = doc.select("PRESSURE");
		rez = rez.concat(PRESSURE .attr("min"));
		rez = rez.concat("...");
		rez = rez.concat(PRESSURE .attr("max")+rz);
		rez = rez.concat("температура : ");
		Elements TEMPERATURE   = doc.select("TEMPERATURE");
		rez = rez.concat(TEMPERATURE .attr("min"));
		rez = rez.concat("...");
		rez = rez.concat(TEMPERATURE .attr("max")+rz);
		rez = rez.concat("Ветер от: ");
		Elements WIND   = doc.select("WIND");
		rez = rez.concat(WIND .attr("min"));
		rez = rez.concat("...");
		rez = rez.concat(WIND .attr("max"));
		rez = rez.concat("м/с ,");
		rez = rez.concat(direction.get(Integer.valueOf(WIND.attr("direction")))+rz);
		rez = rez.concat("Ощущения: ");
		Elements HEAT   = doc.select("HEAT");
		rez = rez.concat(HEAT .attr("min"));
		rez = rez.concat("...");
		rez = rez.concat(HEAT .attr("max")+rz);
		return rez;
	}//static String getPrognoz()
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////////////////////////////////
	//	описание
	//		колбания курса за день
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
	//	по доллару с финам
	/*
	  	0 Последняя сделка 53,5840
		1 Изменение 0,2490 (0,47%)
		2 Сегодня, max 54,1680
		3 Сегодня, min 53,4620
		4 Цена открытия 53,6760
		5 Пред. закрытие 53,3350
		6 Объём торгов 2 023 013 000
		7 время получения
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
	//	по brent с финам
	/*
	  	0 Последняя сделка 53,5840
		1 Изменение 0,2490 (0,47%)
		2 Сегодня, max 54,1680
		3 Сегодня, min 53,4620
		4 Цена открытия 53,6760
		5 Пред. закрытие 53,3350
		6 Объём торгов 2 023 013 000
		7 время получения
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
