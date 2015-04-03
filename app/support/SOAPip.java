package support;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class SOAPip {
//declares the string variables to be used
	private final String USER_AGENT = "Mozilla/5.0";
	String url;
	String[] coords = new String[2];
//public set method
	public void setUrl(String url){
		this.url = url;
	}
//takes the IP and returns lat long coordinates of that location
//makes an HTTP GET request and parses the result
	public void getLatLongByIp() throws Exception {
		 
		//String url = "http://freegeoip.net/xml/8.8.8.8";
 		//makes a url object
		URL obj = new URL(this.url);
		//opens a connection
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		// optional default is GET
		con.setRequestMethod("GET");
 
		//add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
 		
		//for debugging
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + this.url);
		System.out.println("Response Code : " + responseCode);
 
		//reads the result
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//puts the result into a doc object to parse it		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new InputSource(new StringReader(response.toString())));
 
		//finds the relevant data
		NodeList latList = doc.getElementsByTagName("Latitude");
		this.coords[0] = latList.item(0).getTextContent();		

		NodeList longList = doc.getElementsByTagName("Longitude");
		this.coords[1] = longList.item(0).getTextContent(); 
	}	
}

