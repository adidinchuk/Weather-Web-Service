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


public class SOAPClientZip {

//declares the objects to be used in the main body
	SOAPConnectionFactory soapConnectionFactory;
	SOAPConnection soapConnection;	
	SOAPMessage soapResponse;
	SOAPBody body;
//declares the string array to be used
	String[] coords = new String[2];
//declares the strings to be used
	String zipCode, url;

//makes a connection to SOAP and sends SOAP request	
	public SOAPClientZip(){		
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
	public void setZipCode(String zipCode){
		this.zipCode = zipCode;
	}

	public void setUrl(String url){
		this.url = url;
	}	
	
	public void run(){
		
		//makes a SOAP request and takes the responce to build a SOAP body
		try {
			this.soapResponse = soapConnection.call(SOAPRequest(), this.url);		   
			this.body = soapResponse.getSOAPBody();	
		} catch (SOAPException e) {			
			e.printStackTrace();
		}     
		//runs the SOAPResponce function 
		try {
			SOAPResponce();
		} catch (DOMException e) {			
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}		
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
        
        //hardcoded namespace declaration
        String serverURI1 = "http://www.w3.org/2001/XMLSchema-instance", serverURI2 = "http://www.w3.org/2001/XMLSchema",
        		serverURI3 = "http://schemas.xmlsoap.org/soap/envelope/", serverURI4 = "http://graphical.weather.gov/xml/DWMLgen/wsdl/ndfdXML.wsdl";
        
        envelope.addNamespaceDeclaration("xsi", serverURI1);
        envelope.addNamespaceDeclaration("xsd", serverURI2);
        envelope.addNamespaceDeclaration("soapenv", serverURI3);
        envelope.addNamespaceDeclaration("ndf", serverURI4);
        
        //makes the SOAPBody
        SOAPBody soapBody = envelope.getBody();
        
        //builds the request XML
        SOAPElement soapBodyElem = soapBody.addChildElement("LatLonListZipCode", "ndf");    
        soapBodyElem.addAttribute(envelope.createName("xmlns:ns8077"), "uri:DWMLgen");
        
        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("listZipCodeList");
        soapBodyElem1.addAttribute(envelope.createName("xsi:type"),"xsd:string");
        soapBodyElem1.addTextNode(this.zipCode);        


        soapMessage.saveChanges();
        try {
			soapMessage.writeTo(System.out);
		} catch (IOException e) {
			System.out.println("Error");
			e.printStackTrace();
		}
       
        return soapMessage;
	}
//SOAP responce method
//this method takes the generated soap body responce and finds and extracts the relevant data
	public void SOAPResponce() throws DOMException, SAXException, IOException, ParserConfigurationException{
		
		//parse down to the inner XML
		NodeList ns1List = body.getElementsByTagName("ns1:LatLonListZipCodeResponse");
		Element ns1 = (Element)ns1List.item(0);      
		NodeList dwmlByDayOutList = ns1.getElementsByTagName("listLatLonOut");
		Element dwmlByDayOut = (Element)dwmlByDayOutList.item(0);
		
		//create a doc object out of inner XML
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(new InputSource(new StringReader(dwmlByDayOut.getTextContent())));
		
		//parse down the doc object to the parameters tag 
		NodeList latLonList = doc.getElementsByTagName("latLonList");
		System.out.println();
		this.coords = latLonList.item(0).getTextContent().split(",");	
	}	
}
