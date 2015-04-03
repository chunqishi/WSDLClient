package org.lappsgrid.wsdlclient;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
/**
 * Created by lapps on 4/2/2015.
 */
public class SOAPClient {
    public static void main(String args[]) throws Exception {
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        // Send SOAP Message to SOAP Server
        String url = "http://www.webservicex.com/globalweather.asmx";
        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);
        // print SOAP Response
        System.out.print("Response SOAP Message:");
        soapResponse.writeTo(System.out);

        soapConnection.close();
    }

    private static SOAPMessage createSOAPRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "http://www.webserviceX.NET/" + "GetWeather");
        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
//        envelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        envelope.addNamespaceDeclaration("soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
//        envelope.addNamespaceDeclaration("web", "http://www.webserviceX.NET");
        SOAPBody body = soapMessage.getSOAPBody();
        QName bodyName = new QName("http://www.webserviceX.NET", "GetWeather");
        SOAPBodyElement bodyElement = body.addBodyElement(bodyName);
        QName name = new QName("http://www.webserviceX.NET", "CountryName");
        SOAPElement symbol = bodyElement.addChildElement(name);
        symbol.addTextNode("United States");
        name = new QName("http://www.webserviceX.NET", "CityName");
        symbol = bodyElement.addChildElement(name);
        symbol.addTextNode("Boston");
        soapMessage.saveChanges();
        /* Print the request message */
        System.out.print("Request SOAP Message:");
        soapMessage.writeTo(System.out);
        System.out.println();

        return soapMessage;
    }
}
