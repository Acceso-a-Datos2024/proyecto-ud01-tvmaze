package edu.badpals.controlador;

import edu.badpals.modelo.Episodio;
import edu.badpals.modelo.Serie;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class Conexion {

    private static final String URL = "https://api.tvmaze.com/";
    private HttpClient client = HttpClient.newHttpClient();


    public Serie getSerie(String serieString) {

        Serie serie = new Serie();
        try {
            File serieDir = checkCache(serieString);
            if (serieDir == null || isSerieCache(serieString) == null) {
                String encodedSerie = URLEncoder.encode(serieString, StandardCharsets.UTF_8.toString());
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(URL + "singlesearch/shows?q=" + encodedSerie)).GET().build();
                String respuesta = this.client.send(request, HttpResponse.BodyHandlers.ofString()).body();
                serie = JSONHandler.JSONtoSerie(respuesta);
                guardarSerieCache(serie);
                return serie;
            }
            System.out.println("desde cache");
            serie = JSONHandler.fileToSerie(isSerieCache(serieString));
            return serie;


        } catch (Exception e) {
            System.out.println("Error al buscar la serie");
        }
        serie.setId(0);
        return serie;
    }


    public List<Episodio> getEpisodios(int idSerie) {

        List<Episodio> episodios = new ArrayList<>();
        try {
            File serieDir = checkCache(idSerie);
            if (serieDir == null || isEpisodiosCache(idSerie) == null) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(URL + "shows/" + idSerie + "/episodes")).GET().build();
                String respuesta = this.client.send(request, HttpResponse.BodyHandlers.ofString()).body();
                episodios = JSONHandler.JSONtoEpisodios(respuesta);
                guardarEpisodiosCache(episodios,idSerie);
                return episodios;
            }
            System.out.println("desde cache");
            episodios = JSONHandler.fileToEpisodios(isEpisodiosCache(idSerie));
            return episodios;

        } catch (Exception e) {
            System.out.println("Error al buscar los episodios");
        }
        return episodios;
    }


    public List<String> getCast(int idSerie) {
        List<String> cast = new ArrayList<>();
        try {

            File serieDir = checkCache(idSerie);
            if (serieDir == null || isCastCache(idSerie) == null) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(URL + "shows/" + idSerie + "/cast")).GET().build();
                String respuesta = this.client.send(request, HttpResponse.BodyHandlers.ofString()).body();
                cast = JSONHandler.JSONtoActores(respuesta);
                guardarCastCache(cast,idSerie);
                return cast;
            }
            System.out.println("desde cache");
            cast = JSONHandler.fileToActores(isEpisodiosCache(idSerie));
            return cast;
        } catch (Exception e) {
            System.out.println("Error al buscar el cast");
        }
        return cast;
    }

    public static File checkCache(String nombre) {
        nombre = nombre.replaceAll("[\\\\/:*?\"<>|]", "");
        File cacheDir = cacheExists();
        if (cacheDir == null){
            return null;
        }
        File[] cacheDirs = cacheDir.listFiles();
        if (cacheDirs != null) {
            for (File file : cacheDirs) {
                if (file.getName().startsWith(nombre + "-")) {
                    return file;
                }
            }
        }
        return null;
    }

    public static File checkCache(int id) {
        File cacheDir = cacheExists();
        if (cacheDir == null){
            return null;
        }
        File[] cacheDirs = cacheDir.listFiles();
        if (cacheDirs != null) {
            for (File file : cacheDirs) {
                if (file.getName().endsWith("-" + id)) {
                    return file;
                }
            }
        }
        return null;
    }

    private static File cacheExists() {
        File cacheDir = new File("data/cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        return cacheDir;
    }

    private void guardarSerieCache(Serie serie) {
        try {
            File serieDir = checkCache(serie.getName());
            if (serieDir == null){
                crearSerieDir(serie);
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(JSONHandler.serieToXML(serie)), new StreamResult(isSerieCache(serie.getName())));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al guardar Serie en cache");
        }
    }



    private void guardarEpisodiosCache(List<Episodio> episodios, int id) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(JSONHandler.episodiosToXML(episodios)), new StreamResult(isEpisodiosCache(id)));
        } catch (Exception e) {
            System.out.println("Error al guardar Episodios en cache");
        }
    }

    private void guardarCastCache(List<String> cast, int id) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(JSONHandler.actoresToXML(cast)), new StreamResult(isCastCache(id)));
        } catch (Exception e) {
            System.out.println("Error al guardar Cast en cache");
        }
    }

    private List<Episodio> leerEpisodiosCache(int id) {
        return JSONHandler.XMLToEpisodios(JSONHandler.LectorFile(isEpisodiosCache(id)).toString());
    }

    private static File getDirSerie(int id) {
        File cacheDir = new File("data/cache");
        File[] cacheDirs = cacheDir.listFiles();
        if (cacheDirs != null) {
            for (File file : cacheDirs) {
                if (file.getName().endsWith("-" + id)) {
                    return file;
                }
            }
        }
        return null;
    }

    //Aqui checkeo q existan los files dentro del file contenedor en el cache

    private static File isSerieCache(String serie) {
        serie = serie.replaceAll("[\\\\/:*?\"<>|]", "");
        File cacheDir = checkCache(serie);
        if (cacheDir == null){
            return null;
        }
        File[] cacheDirs = cacheDir.listFiles();
        if (cacheDirs != null) {
            for (File file : cacheDirs) {
                if (file.getName().endsWith("Serie.xml")) {
                    return file;

                }
            }
        }
        return null;
    }

    private static File isEpisodiosCache(int id) {
        File cacheDir = checkCache(id);
        if (cacheDir == null){
            return null;
        }
        File[] cacheDirs = cacheDir.listFiles();
        if (cacheDirs != null) {
            for (File file : cacheDirs) {
                if (file.getName().endsWith("episodios.xml")) {
                    return file;

                }
            }
        }
        return null;
    }

    private static File isCastCache(int id) {
        File cacheDir = checkCache(id);
        if (cacheDir == null){
            return null;
        }
        File[] cacheDirs = cacheDir.listFiles();
        if (cacheDirs != null) {
            for (File file : cacheDirs) {
                if (file.getName().endsWith("cast.xml")) {
                    return file;

                }
            }
        }
        return null;
    }

    private void crearSerieDir(Serie serie) {
        try {
            File cacheDir = new File("data/cache/" + serie.getName().replaceAll("[\\\\/:*?\"<>|]", "") + "-" + serie.getId());
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }

            File serieFile = new File(cacheDir, "Serie.xml");
            File episodiosFile = new File(cacheDir, "episodios.xml");
            File castFile = new File(cacheDir, "cast.xml");

            if (!serieFile.exists()) {
                serieFile.createNewFile();
            }
            if (!episodiosFile.exists()) {
                episodiosFile.createNewFile();
            }
            if (!castFile.exists()) {
                castFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al crear el directorio de la serie");
        }
    }
}