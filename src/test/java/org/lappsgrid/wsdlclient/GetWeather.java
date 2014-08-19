package org.lappsgrid.wsdlclient;

/**
 * Created by lapps on 8/18/2014.
 */
public class GetWeather {
    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public void setCountryName(String countryName) {
        CountryName = countryName;
    }

    public String getCityName() {
        return CityName;
    }

    public String getCountryName() {
        return CountryName;
    }

    String CityName;
    String CountryName;
}
