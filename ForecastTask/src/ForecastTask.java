import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.google.gson.Gson;


public class ForecastTask {
    public static void main(String[] args) {
        try {
            int forecastDaysLimit = 10; //Must not be less than 1

            Gson gson = new Gson();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://api.weather.yandex.ru/v2/forecast?lat=55.75&lon=37.62&limit=" + forecastDaysLimit))
                    .headers("X-Yandex-Weather-Key", "<YOUR KEY HERE>")
                    .GET()
                    .build();

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            String response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

            System.out.println("Received JSON:\n" + response);

            TemperatureContainer temperatureContainer = gson.fromJson(response, TemperatureContainer.class);

            System.out.println("Temperature:\n" + temperatureContainer.fact.temp);

            ForecastTemperatures forecastTemperatures = gson.fromJson(response, ForecastTemperatures.class);

            int avgTemp = 0;

            for (int i = 0; i < forecastTemperatures.forecasts.size(); i++) {
                avgTemp += forecastTemperatures.forecasts.get(i).parts.day.temp_avg;
            }

            if (!forecastTemperatures.forecasts.isEmpty()) {
                System.out.println("Average temperature (number of days = " + forecastDaysLimit +"):\n" + (double) avgTemp / forecastTemperatures.forecasts.size());
            }

            client.close();
        } catch (Exception e) {
            System.out.println("Error occurred:");
            e.printStackTrace();
        }
    }

    static class ForecastTemperatures {

        List<PartsContainer> forecasts;

        static class PartsContainer {

            Parts parts;

            static class Parts {
                DayPart day;

                static class DayPart {
                    int temp_avg;
                }
            }
        }
    }

    static class TemperatureContainer {

        FactTemperature fact;

        static class FactTemperature {
            int temp;
        }
    }
}