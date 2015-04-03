package org.lappsgrid.wsdlclient;


import org.apache.axis.Constants;
import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.ser.BeanSerializer;
import org.junit.Test;
import org.lappsgrid.api.Data;

import javax.xml.namespace.QName;
import javax.xml.rpc.encoding.Serializer;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class TestWSDLClient {

    @Test
    public void testLapps() throws Exception {
        WSDLClient ws = new WSDLClient();
        ws.init(new URL(
                "http://eldrad.cs-i.brandeis.edu:8080/service_manager/wsdl/brandeis_eldrad_grid_1:opennlp.splitter"));
        ws.authorize("tester", "tester");
        String s = ws.callService("", "execute", "How are you today.").toString();
        System.out.println(s);
    }

    public void testBeanSerializer()throws Exception {

        BeanSerializer ser = new BeanSerializer(GetWeather.class, new QName("http://www.webserviceX.NET","GetWeather"));
        StringWriter sw = new StringWriter();
        SerializationContext ctx = new SerializationContext(sw);
        GetWeather getWeather = new GetWeather("Boston", "United States");
        ser.serialize(new QName("http://www.webserviceX.NET", "GetWeather"), null, getWeather, ctx);
        System.out.println(sw);
    }


    @Test
    public void test() throws Exception {
        WSDLClient ws = new WSDLClient();
        ws.init(new URL("http://www.webservicex.com/globalweather.asmx?WSDL"));
        ws.register(GetWeather.class, new QName("http://www.webserviceX.NET", "GetWeather"));
        GetWeather getWeather = new GetWeather("Boston", "United States");
        String arr =  ws.callService(new QName("http://www.webserviceX.NET", "GetWeather"),getWeather).toString();
        System.out.println(arr);
    }
}
