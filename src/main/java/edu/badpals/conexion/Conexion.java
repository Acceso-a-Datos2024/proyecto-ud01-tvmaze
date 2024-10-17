package edu.badpals.conexion;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class Conexion {
    public static String getSerie(String serie) {

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tvmaze.com/search/shows?q=" + serie)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            return responseBody;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getEpisodios(String idSerie) {

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tvmaze.com/shows/"+idSerie+"/episodes")).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            return responseBody;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getCast(String idSerie) {

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tvmaze.com/shows/"+idSerie+"/cast")).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            return responseBody;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}