package support;

import java.util.Calendar;
import java.util.Date;

public class SOAPController {
//declares objects and arrays to be used
	private String[] dates;// array of dates to pull
	private String[][] days;
	private Day[] Days;// array of objects to store the values
	private boolean current;
	private String url, latLong, dayRange, startDate, zipCode;

//basic constructor
//everything is set to default
	public SOAPController() {
		//get the date
		Calendar calendar = Calendar.getInstance();
		// set the format for dates (yyyy-MM-dd format is required for the SOAP
		// request)
		java.text.SimpleDateFormat dateTime = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		Date date = calendar.getTime();
		//set the Strings to be used in the run function
		setStartDate(dateTime.format(date));
		setDayRange("7");
		setLatLong("37.386,-122.084");
		setUrl("http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php");
		setZipCode("94040");
	}
//day number operated constructor
//this constructor lets you set the number of days to pull and leaves the rest as default
	public SOAPController(String days) {
			//get the date
		Calendar calendar = Calendar.getInstance();
		// set the format for dates (yyyy-MM-dd format is required for the SOAP
		// request)
		java.text.SimpleDateFormat dateTime = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		Date date = calendar.getTime();
		//set the Strings to be used in the run function
		setStartDate(dateTime.format(date));
		setDayRange(days);
		setLatLong("37.386,-122.084");
		setUrl("http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php");
		setZipCode("94040");
	}

//manually set all values with this constructor
	public SOAPController(String dayRange, String startDate, String latLong,
			String url, String zipCode) {
		setStartDate(startDate);
		setDayRange(dayRange);
		setLatLong(latLong);
		setUrl(url);
		setZipCode(zipCode);
	}
//pulls all relevant data avaliable for the set paramaters
//if there data is not current it resends the SOAP request to get new data
	public Day[] getAllData() {
		if (!this.current) {
			run();
			this.current = true;//set data as current
		}
		return this.Days;
	}
//pulls the data for just one day 
//if there data is not current it resends the SOAP request to get new data
	public Day getDayByIndex(int i) {
		if (!this.current) {
			run();
			this.current = true;//set data as current
		}
		return this.Days[i];
	}
//gets the lat long inforamtion based on zip code
	public void getLatLong(){
		SOAPClientZip soapClientZip = new SOAPClientZip();
		soapClientZip.setZipCode(this.zipCode);
		soapClientZip.setUrl(this.url);
		soapClientZip.run();
		this.latLong = soapClientZip.coords[0] + "," + soapClientZip.coords[1];
		this.current = false;//since parameters are reset data is set to not current
	}
//gets the lat long inforamtion based on IP
	public void getLatLongByIp(String ip){
		String url = "http://freegeoip.net/xml/" + ip;
		SOAPip soapIp = new SOAPip();
		soapIp.setUrl(url);
		try {
			soapIp.getLatLongByIp();
		} catch (Exception e) {			
			e.printStackTrace();
		}
		this.latLong = soapIp.coords[0] + "," + soapIp.coords[1];
		this.current = false;//since parameters are reset data is set to not current
	}
//runs the soap client with the currentl set parameters and populates the Day objects with the received 2D array
	public void run() {
		SOAPClient soapClient = new SOAPClient();
		soapClient.setDate(this.startDate);
		soapClient.setLatLong(this.latLong);
		soapClient.setLength(this.dayRange);
		soapClient.setUrl(this.url);

		// get the 2D array of values for the required days
		this.days = soapClient.run();
		this.Days = new Day[Integer.parseInt(dayRange)];

		// populate the Day array
		for (int i = 0; i < days.length; i++) {
			this.Days[i] = new Day();
			this.Days[i].setTempHigh(this.days[i][0]);
			this.Days[i].setTempLow(this.days[i][1]);
			this.Days[i].setPrecipEarly(this.days[i][2]);
			this.Days[i].setPrecipLate(this.days[i][3]);
			this.Days[i].setIconUrl(this.days[i][4]);
			this.Days[i].setDate(this.dates[i]);
		}
	}
//public set methods

//sets the start range and populates the dates array with the date of days to be accesed
	public void setDayRange(String dayRange) {
		this.dayRange = dayRange;

		// parse startDate
		String[] splitDate = this.startDate.split("-");
		int year = Integer.parseInt(splitDate[0]), month = Integer
				.parseInt(splitDate[1]), day = Integer.parseInt(splitDate[2]);
		// set date to start date and get the dates for the day range
		Calendar calendar;
		calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		// set the format for dates (yyyy-MM-dd format is required for the SOAP
		// request)
		java.text.SimpleDateFormat dateTime = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		this.dates = new String[Integer.parseInt(dayRange)];
		for (int i = 0; i < this.dates.length; i++) {
			Date date = calendar.getTime();
			this.dates[i] = dateTime.format(date);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		this.current = false;//since parameters are reset data is set to not current
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
		this.current = false;//since parameters are reset data is set to not current
	}

	public void setLatLong(String latLong) {
		this.latLong = latLong;
		this.current = false;//since parameters are reset data is set to not current
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setZipCode(String zipCode){
		this.zipCode = zipCode;
	}
}

