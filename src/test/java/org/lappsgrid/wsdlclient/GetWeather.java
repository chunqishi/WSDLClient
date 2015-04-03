package org.lappsgrid.wsdlclient;

import org.apache.axis.Constants;
import org.apache.axis.encoding.Serializer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by lapps on 8/18/2014.
 */
public class GetWeather implements Serializable {
    private static final long serialVersionUID = 6036235675698560104L;

    public GetWeather() {
    }

    public GetWeather(String CityName, String CountryName) {
        this.CityName = CityName;
        this.CountryName = CountryName;
    }

//    public void setCityName(String cityName) {
//        CityName = cityName;
//    }
//
//    public String getCityName() {
//
//        return CityName;
//    }
//
//    public String getCountryName() {
//        return CountryName;
//    }

    public String CityName;
    public String CountryName;

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
            new org.apache.axis.description.TypeDesc(GetWeather.class);
    static {
        org.apache.axis.description.FieldDesc field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("CityName");
        field.setXmlName(new javax.xml.namespace.QName("http://www.webserviceX.NET", "CityName"));
        typeDesc.addFieldDesc(field);
        field = new org.apache.axis.description.ElementDesc();
        field.setFieldName("CountryName");
        field.setXmlName(new javax.xml.namespace.QName("http://www.webserviceX.NET", "CountryName"));
        typeDesc.addFieldDesc(field);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }
}
