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
                    .uri(URI.create("https://api.tvmaze.com/search/shows?q=" + serie)) // URL de la API
                    .GET() // Método HTTP GET
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            System.out.println("Código de estado: " + statusCode);
            String responseBody = response.body();
            return responseBody;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}