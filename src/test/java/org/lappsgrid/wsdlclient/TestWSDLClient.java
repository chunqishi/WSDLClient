package org.lappsgrid.wsdlclient;


import org.junit.Test;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;


public class TestWSDLClient {
    @Test
    public void test() throws MalformedURLException, RemoteException, WSDLClientException {
        WSDLClient ws = new WSDLClient();
        ws.init(new URL("http://www.webservicex.com/globalweather.asmx?WSDL"));
        ws.register(GetWeather.class, new QName("http://www.webserviceX.NET/","GetWeather"));
        GetWeather getWeather = new GetWeather();
        getWeather.setCountryName("United States");
        getWeather.setCityName("Boston");
        String arr =  ws.callService("http://www.webserviceX.NET/","GetWeather",getWeather).toString();
        System.out.println(arr);
    }
}
