package edu.badpals.controlador;
import org.json.JSONArray;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class Conexion {

    private static final String URL = "https://api.tvmaze.com/";
    private HttpClient client = HttpClient.newHttpClient();


    public String getSerie(String serie) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "singlesearch/shows?q=" + serie)).GET().build();
            return this.client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getEpisodios(int idSerie) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "shows/" +idSerie+"/episodes")).GET().build();
            return this.client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getCast(int idSerie) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + "shows/" +idSerie+"/cast")).GET().build();
            return this.client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}