package support;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class SOAPClient {

//declares the objects to be used in the main body
	SOAPConnectionFactory soapConnectionFactory;
	SOAPConnection soapConnection;	
	SOAPMessage soapResponse;
	SOAPBody body;
//declares the strings to be used
	String latLong, date, length, url;
	String unit = "e", format = "24 hourly";

//constructor	
//makes a connection to SOAP
	public SOAPClient(){		
		//makes connection			
		try {
			this.soapConnectionFactory = SOAPConnectionFactory.newInstance();				
			this.soapConnection = soapConnectionFactory.createConnection();	 
		} catch (UnsupportedOperationException e) {			
			e.printStackTrace();
		} catch (SOAPException e) {			
			e.printStackTrace();	        
		}
	}
//public set methods
	public void setLatLong(String latLong){
		this.latLong = latLong;
	}
	public void setDate(String date){
		this.date = date;
	}
	public void setLength(String length){
		this.length = length;
	}
	public void setUrl(String url){
		this.url = url;
	}	
//main method that makes a SOAP request and process the SOAP responce
	public String[][] run(){
		//declares the 2D string array to hold the weather data
		String[][] days = new String[Integer.parseInt(this.length)][5];
		
		//makes a SOAP request and takes the responce to build a SOAP body
		try {
			this.soapResponse = soapConnection.call(SOAPRequest(), this.url);		   
			this.body = soapResponse.getSOAPBody();//soapResponce is a SOAPMessage object
		} catch (SOAPException e) {			
			e.printStackTrace();
		}     
		//runs the SOAPResponce function which uses the initilized SOAP body to populate the 2D string array with the weather data
		try {
			days = SOAPResponce();
		} catch (DOMException e) {			
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		//returns the 2D string array with the weather data
		return days;
		
	}
//SOAP request method
//create an XML SOAP request and forms a SOAPMessage with it
	public SOAPMessage SOAPRequest() throws SOAPException{
		
		//Creates a new message and envelope
		MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        
        /*namespace declaration
        for(int i = 0; i<URI.length;i++){
        	envelope.addNamespaceDeclaration(namespace[i], URI[i]);
        }
        */

        //building the XML

        //hardcoded namespace declaration
        String serverURI1 = "http://www.w3.org/2001/XMLSchema-instance", serverURI2 = "http://www.w3.org/2001/XMLSchema",
        		serverURI3 = "http://schemas.xmlsoap.org/soap/envelope/", serverURI4 = "http://graphical.weather.gov/xml/DWMLgen/wsdl/ndfdXML.wsdl";
        //namespace declerations in the main tag
        envelope.addNamespaceDeclaration("xsi", serverURI1);
        envelope.addNamespaceDeclaration("xsd", serverURI2);
        envelope.addNamespaceDeclaration("soapenv", serverURI3);
        envelope.addNamespaceDeclaration("ndf", serverURI4);
        
        //makes the SOAPBody
        SOAPBody soapBody = envelope.getBody();
        
        //builds the request XML

        SOAPElement soapBodyElem = soapBody.addChildElement("NDFDgenByDayLatLonList", "ndf");//adds a new tag with namespace and value      
		  //adds the attributes to the tag with a namespace and value     
        soapBodyElem.addAttribute(envelope.createName("soapenv:encodingStyle"), "http://schemas.xmlsoap.org/soap/encoding/");

        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("listLatLon");
        soapBodyElem1.addAttribute(envelope.createName("xsi:type"),"dwml:listLatLonType");
        soapBodyElem1.addAttribute(envelope.createName("xmlns:dwml"), "http://graphical.weather.gov/xml/DWMLgen/schema/DWML.xsd");
        soapBodyElem1.addTextNode(this.latLong);//this adds a value inside the tag
        
        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("startDate");        
        soapBodyElem2.addAttribute(envelope.createName("xsi:type"), "xsd:date");
        soapBodyElem2.addTextNode(this.date);
        
        SOAPElement soapBodyElem3 = soapBodyElem.addChildElement("numDays");
        soapBodyElem3.addAttribute(envelope.createName("xsi:type"), "xsd:integer");
        soapBodyElem3.addTextNode(this.length);
        
        SOAPElement soapBodyElem4 = soapBodyElem.addChildElement("Unit");
        soapBodyElem4.addAttribute(envelope.createName("xsi:type"), "dwml:unitType");
        soapBodyElem4.addAttribute(envelope.createName("xmlns:dwml"), "http://graphical.weather.gov/xml/DWMLgen/schema/DWML.xsd");
        soapBodyElem4.addTextNode(this.unit);
        
        SOAPElement soapBodyElem5 = soapBodyElem.addChildElement("format");
        soapBodyElem5.addAttribute(envelope.createName("xsi:type"), "dwml:formatType");
        soapBodyElem5.addAttribute(envelope.createName("xmlns:dwml"), "http://graphical.weather.gov/xml/DWMLgen/schema/DWML.xsd");
        soapBodyElem5.addTextNode(this.format);
		  //save the changes and return the SOAPMessage object
        soapMessage.saveChanges();
       
        return soapMessage;
	}

//SOAP responce method
//this method takes the generated soap body responce and finds and extracts the relevant data
	public String[][] SOAPResponce() throws DOMException, SAXException, IOException, ParserConfigurationException{
		
		//parse down to the inner XML
		NodeList ns1List = body.getElementsByTagName("ns1:NDFDgenByDayLatLonListResponse");
		Element ns1 = (Element)ns1List.item(0);      
		NodeList dwmlByDayOutList = ns1.getElementsByTagName("dwmlByDayOut");
		Element dwmlByDayOut = (Element)dwmlByDayOutList.item(0);
		
		//create a doc object out of inner XML
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new InputSource(new StringReader(dwmlByDayOut.getTextContent())));
		
		//parse down the doc object to the parameters tag 
		NodeList dwmlList = doc.getElementsByTagName("dwml");
		Element dwml = (Element)dwmlList.item(0);
		NodeList dataList = dwml.getElementsByTagName("data");
		Element data = (Element)dataList.item(0);
		NodeList parametersList = data.getElementsByTagName("parameters");       
		Element parameters = (Element)parametersList.item(0);
	    
		//parse down to the values tags inside the first temperature tag inside parameters
		NodeList temperatureList = parameters.getElementsByTagName("temperature");
		Element temperature = (Element)temperatureList.item(0);
		NodeList valueList = temperature.getElementsByTagName("value"); 
	     
		//parse down to the values tags inside the second temperature tag inside parameters
		Element temperatureLow = (Element)temperatureList.item(1);
		NodeList valueList2 = temperatureLow.getElementsByTagName("value");
	     
		//parse down to the values tags inside the probability-of-precipitation tag inside parameters
		NodeList precipitationList = parameters.getElementsByTagName("probability-of-precipitation");
		Element precipitation = (Element)precipitationList.item(0);
		NodeList valueList3 = precipitation.getElementsByTagName("value");
	     
		//parse down to the icon-link tags inside the conditions-icon tag inside parameters
		NodeList iconList = parameters.getElementsByTagName("conditions-icon");
		Element icon = (Element)iconList.item(0);
		NodeList valueList4 = icon.getElementsByTagName("icon-link");
	    
		//store the XML contents inside the days array
		String[][] days = new String[Integer.parseInt(this.length)][5];
		for(int i = 0;i<days.length;i++){
			days[i][0] = valueList.item(i).getTextContent();
			days[i][1] = valueList2.item(i).getTextContent();
			days[i][2] = valueList3.item((2*i)).getTextContent();
			days[i][3] = valueList3.item(((2*i)+1)).getTextContent();
			days[i][4] = valueList4.item(i).getTextContent();
		}
		//return the 2D string array       
		return days;
	}
	
}

