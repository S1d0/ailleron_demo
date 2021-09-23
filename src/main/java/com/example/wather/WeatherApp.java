package com.example.wather;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

interface WeatherConnector {
    String[] weather(String location);
}

interface MailProvider {
    void sendMail(final String location, final String weatherDatum, final String datum);
}

public class WeatherApp {

    static final String[] locations = new String[]{"Cracow", "Warsaw", "London", "Lodz", "Kielce", "Tokyo", "NewYork", "Buenos Aires", "Rzeszow"};

    public static void main(String[] args) throws InterruptedException {

        Random random = new Random();

        Runnable task = () -> {
            WeatherProviderUtilsCommonHelper provider = new WeatherProviderUtilsCommonHelper();

            String location = locations[random.nextInt(locations.length-1)];

            log(location);

            Weather weather = provider.checkWeatherAndSendMailWithTemperature(location);

            log(weather);
        };
        for (int i = 0; i < locations.length * 20; i++) {
           new Thread(task).start();
        }

    }

    private static void log(Object object) {
        System.out.println("Weather=" + object.toString());
    }

    private static void log(String text) {
        System.out.println("Weather=" + text);
    }
}

class WeatherProviderUtilsCommonHelper {
    private WeatherConnector weatherConnector;
    private MailProvider mailProvider;
    private final Map<String, Weather> cacheWeather = new HashMap<>();

    public Weather checkWeatherAndSendMailWithTemperature(String location) {
        setWeatherConnector(new DummyWeatherConnectorImpl());
        setMailProvider(new MailProviderImpl());
        try {
            Weather weather = getWeather(location);
            mailProvider.sendMail(location, weather.getLocation(), weather.getTemp());
            return weather;
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    private Weather getWeather(String location) {
        Weather weather;
        if(cacheWeather.containsKey(location)){
            weather = cacheWeather.get(location);
        } else {
            String[] weatherData = weatherConnector.weather(location);
            weather = new Weather(weatherData[0], Double.valueOf(weatherData[1]));
            cacheWeather.put(location, weather);
        }
        return weather;
    }

    private void setMailProvider(MailProviderImpl mailProvider) {
        this.mailProvider = mailProvider;
    }

    public void setWeatherConnector(WeatherConnector connector) {
        this.weatherConnector = connector;
    }

    private static void log(Object object) {
        System.out.println("Weather=" + object.toString());
    }

    private static void log(String text) {
        System.out.println("Weather=" + text);
    }
}


final class Weather {
    final String location;
    final Double temp;

    Weather(String location, Double temp) {
        this.location = location;
        this.temp = temp;
    }

    public String getLocation() {
        return location;
    }

    public String getTemp() {
        return temp.toString();
    }
}

final class MailProviderImpl implements MailProvider {

    @Override
    public void sendMail(String location, String weatherDatum, String datum) {
        System.out.println("Location " + location + ", Weather location: " + weatherDatum + ", Weather temp: " + datum);
    }
}
final class DummyWeatherConnectorImpl implements WeatherConnector {
    @Override
    public String[] weather(String location) {
        return new String[]{location, "20.0"};
    }
}