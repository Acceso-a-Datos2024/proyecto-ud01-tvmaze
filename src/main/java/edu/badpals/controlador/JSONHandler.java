package edu.badpals.controlador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.badpals.modelo.*;
import org.jdom2.input.SAXBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

public class JSONHandler {

    // Aquí sacamos los objetos a partir del file en xml
    public static Serie fileToSerie(File xmlFile) {
        if (xmlFile.length() == 0) {
            return new LinkPaginasController().SERIEVACIA;
        }
        return XMLToSerieCache(LectorFile(xmlFile).toString());
    }

    public static List<Episodio> fileToEpisodios(File xmlFile) {
        return XMLToEpisodios(LectorFile(xmlFile).toString());
    }

    public static List<String> fileToActores(File xmlFile) {
        return XMLToCast(LectorFile(xmlFile).toString());
    }

    // Método para leer el contenido de un archivo XML y devolverlo como un StringBuilder
    public static StringBuilder LectorFile(File xmlFile) {
        StringBuilder xmlContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(xmlFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                xmlContent.append(line);
            }
        } catch (IOException e) {
            System.out.println("Error lector File");
        }
        return xmlContent;
    }

    // Método para escribir el texto
    public static void EscritorFile(String texto, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file,true))) {
            writer.newLine();
            writer.write(texto);
        } catch (IOException e) {
            System.out.println("Error lector File");
        }
    }

    // Método para escribir el objeto
    public static void EscritorObjects(Object o, File file) {
        try (ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(file, true))) {
            writer.writeObject(o);
        } catch (IOException e) {
            System.out.println("Error lector File");
        }
    }

    // Método para convertir un XML en un objeto Serie
    public static Serie XMLToSerie(String xmlString) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            document.getDocumentElement().normalize();

            Element rootElement = document.getDocumentElement();

            Serie serie = new Serie();
            serie.setId(Integer.parseInt(getTagValue("id", rootElement)));
            serie.setName(getTagValue("name", rootElement));
            serie.setType(getTagValue("type", rootElement));
            serie.setLanguage(getTagValue("language", rootElement));
            serie.setStatus(getTagValue("status", rootElement));
            serie.setPremiered(getTagValue("premiered", rootElement));
            serie.setRating(new Rating(Double.parseDouble(getTagValue("rating", rootElement))));

            List<String> genres = new ArrayList<>();
            NodeList genreNodes = rootElement.getElementsByTagName("genre");
            for (int i = 0; i < genreNodes.getLength(); i++) {
                genres.add(genreNodes.item(i).getTextContent());
            }
            serie.setGenres(genres);

            Schedule schedule = new Schedule();
            Element scheduleElement = (Element) rootElement.getElementsByTagName("schedule").item(0);
            schedule.setTime(getTagValue("time", scheduleElement));

            List<String> days = new ArrayList<>();
            NodeList dayNodes = scheduleElement.getElementsByTagName("day");
            for (int i = 0; i < dayNodes.getLength(); i++) {
                days.add(dayNodes.item(i).getTextContent());
            }
            schedule.setDays(days);
            serie.setSchedule(schedule);

            Image image = new Image();
            NodeList imageNodes = rootElement.getElementsByTagName("image");
            if (imageNodes != null && imageNodes.getLength() > 0) {
                Node imageNode = imageNodes.item(0);
                if (imageNode.hasChildNodes()) {
                    image.setMedium(getTagValue("medium", rootElement));
                    image.setOriginal(getTagValue("original", rootElement));
                } else {
                    image.setMedium(imageNode.getTextContent());
                    image.setOriginal(imageNode.getTextContent());
                }
            }
            serie.setImage(image);

            return serie;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para convertir un XML en un objeto Serie (con caché)
    public static Serie XMLToSerieCache(String xmlString) {
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            org.jdom2.Document document = saxBuilder.build(new StringReader(xmlString));
            org.jdom2.Element rootElement = document.getRootElement();

            Serie serie = new Serie();
            serie.setId(Integer.parseInt(rootElement.getChildText("id")));
            serie.setName(rootElement.getChildText("name"));
            serie.setType(rootElement.getChildText("type"));
            serie.setLanguage(rootElement.getChildText("language"));
            serie.setStatus(rootElement.getChildText("status"));
            serie.setPremiered(rootElement.getChildText("premiered"));
            serie.setRating(new Rating(Double.parseDouble(rootElement.getChildText("rating"))));

            List<String> genres = new ArrayList<>();
            for (org.jdom2.Element genreElement : rootElement.getChildren("genre")) {
                genres.add(genreElement.getText());
            }
            serie.setGenres(genres);

            org.jdom2.Element scheduleElement = rootElement.getChild("schedule");
            Schedule schedule = new Schedule();
            schedule.setTime(scheduleElement.getChildText("time"));

            List<String> days = new ArrayList<>();
            for (org.jdom2.Element dayElement : scheduleElement.getChildren("day")) {
                days.add(dayElement.getText());
            }
            schedule.setDays(days);
            serie.setSchedule(schedule);

            org.jdom2.Element imageElement = rootElement.getChild("image");
            Image image = new Image();
            if (imageElement != null) {
                image.setMedium(imageElement.getChildText("medium"));
                image.setOriginal(imageElement.getChildText("original"));
            }
            serie.setImage(image);

            return serie;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para convertir un XML en una lista de episodios
    public static List<Episodio> XMLToEpisodios(String xmlString) {
        List<Episodio> episodios = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            document.getDocumentElement().normalize();

            NodeList episodioNodes = document.getElementsByTagName("episodio");
            for (int i = 0; i < episodioNodes.getLength(); i++) {
                Element episodioElement = (Element) episodioNodes.item(i);

                Episodio episodio = new Episodio();
                episodio.setSeason(Integer.parseInt(getTagValue("season", episodioElement)));
                episodio.setNumber(Integer.parseInt(getTagValue("number", episodioElement)));
                episodio.setName(getTagValue("name", episodioElement));

                episodios.add(episodio);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return episodios;
    }

    // Método para convertir un XML en una lista de actores
    public static List<String> XMLToCast(String xmlString) {
        List<String> castList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            document.getDocumentElement().normalize();

            NodeList actorNodes = document.getElementsByTagName("actor");

            for (int i = 0; i < actorNodes.getLength(); i++) {
                Element actorElement = (Element) actorNodes.item(i);

                String name = actorElement.getElementsByTagName("nombre").item(0).getTextContent();
                String character = actorElement.getElementsByTagName("personaje").item(0).getTextContent();

                castList.add("Actor: " + name + ", Personaje: " + character);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error parsing XML string");
        }
        return castList;
    }

    // Aquí pasamos de objetos a XML

    // Método para convertir un objeto Serie a XML
    public static Document serieToXML(Serie serie) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element rootElement = doc.createElement("serie");
        doc.appendChild(rootElement);

        Element id = doc.createElement("id");
        id.appendChild(doc.createTextNode(String.valueOf(serie.getId())));
        rootElement.appendChild(id);

        Element name = doc.createElement("name");
        name.appendChild(doc.createTextNode(serie.getName()));
        rootElement.appendChild(name);

        Element type = doc.createElement("type");
        type.appendChild(doc.createTextNode(serie.getType()));
        rootElement.appendChild(type);

        Element language = doc.createElement("language");
        language.appendChild(doc.createTextNode(serie.getLanguage()));
        rootElement.appendChild(language);

        Element genres = doc.createElement("genres");
        for (String genre : serie.getGenres()) {
            Element genreElement = doc.createElement("genre");
            genreElement.appendChild(doc.createTextNode(genre));
            genres.appendChild(genreElement);
        }
        rootElement.appendChild(genres);

        Element status = doc.createElement("status");
        status.appendChild(doc.createTextNode(serie.getStatus()));
        rootElement.appendChild(status);

        Element premiered = doc.createElement("premiered");
        premiered.appendChild(doc.createTextNode(serie.getPremiered()));
        rootElement.appendChild(premiered);

        Element rating = doc.createElement("rating");
        rating.appendChild(doc.createTextNode(String.valueOf(serie.getRating().getAverage())));
        rootElement.appendChild(rating);

        Element schedule = doc.createElement("schedule");
        Element time = doc.createElement("time");
        time.appendChild(doc.createTextNode(serie.getSchedule().getTime()));
        schedule.appendChild(time);

        Element days = doc.createElement("days");
        for (String day : serie.getSchedule().getDays()) {
            Element dayElement = doc.createElement("day");
            dayElement.appendChild(doc.createTextNode(day));
            days.appendChild(dayElement);
        }
        schedule.appendChild(days);
        rootElement.appendChild(schedule);

        Element image = doc.createElement("image");
        if (serie.getImage() != null) {
            image.appendChild(doc.createTextNode(serie.getImage().getMedium()));
        }
        rootElement.appendChild(image);

        return doc;
    }

    // Método para convertir una lista de episodios a XML
    public static Document episodiosToXML(List<Episodio> episodios) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element rootElement = doc.createElement("episodios");
        doc.appendChild(rootElement);

        for (Episodio episodio : episodios) {
            Element episodioElement = doc.createElement("episodio");
            rootElement.appendChild(episodioElement);

            Element season = doc.createElement("season");
            season.appendChild(doc.createTextNode(String.valueOf(episodio.getSeason())));
            episodioElement.appendChild(season);

            Element number = doc.createElement("number");
            number.appendChild(doc.createTextNode(String.valueOf(episodio.getNumber())));
            episodioElement.appendChild(number);

            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(episodio.getName()));
            episodioElement.appendChild(name);
        }
        return doc;
    }

    // Método para convertir una lista de actores a XML
    public static Document actoresToXML(List<String> actores) throws ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("actores");
        doc.appendChild(rootElement);
        for (String actorData : actores) {
            String[] datos = actorData.split(", Personaje: ");
            String actorName = datos[0].replace("Actor: ", "").trim();
            String characterName = datos.length > 1 ? datos[1].trim() : "Desconocido";
            Element actorElement = doc.createElement("actor");
            Element nombreElement = doc.createElement("nombre");
            nombreElement.appendChild(doc.createTextNode(actorName));
            actorElement.appendChild(nombreElement);
            Element personajeElement = doc.createElement("personaje");
            personajeElement.appendChild(doc.createTextNode(characterName));
            actorElement.appendChild(personajeElement);
            rootElement.appendChild(actorElement);
        }

        return doc;
    }

    // Aquí manejamos los JSON

    // Método para convertir un JSON a un objeto Serie
    public static Serie JSONtoSerie(String json) {
        try {
            if (json.startsWith("{")) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(json, Serie.class);
            }
        } catch (JsonProcessingException e) {
            System.out.println("Error al parsear el json a serie");
        }
        return new LinkPaginasController().SERIEVACIA;
    }

    // Método para convertir un JSON a una lista de episodios
    public static List<Episodio> JSONtoEpisodios(String json) {
        try {
            if (json != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Episodio> episodios = Arrays.asList(objectMapper.readValue(json, Episodio[].class));
                return episodios;
            }
        } catch (JsonProcessingException e) {
            System.out.println("Error al parsear el json a episodios");
        }
        return new ArrayList<>();
    }

    // Método para convertir un JSON a una lista de actores
    public static List<String> JSONtoActores(String json) {
        List<String> nombres = new ArrayList<>();
        try {
            if (json != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                Actor[] actoresArray = objectMapper.readValue(json, Actor[].class);
                for (Actor actor : actoresArray) {
                    nombres.add(actor.toString());
                }
            }
        } catch (JsonProcessingException e) {
            System.out.println("Error al parsear el json a cast");
        }
        return nombres;
    }

    // Aquí manejamos los Users del login

    // Método para crear un usuario y guardarlo en un archivo
    public static void crearUser(String user, String pswd) {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        try (BufferedWriter lector = new BufferedWriter(new FileWriter("data/Users.txt", true))) {
            lector.newLine();
            String coded = LZ78.encode(user + "," + pswd);
            lector.write(coded);

        } catch (Exception e) {
            System.out.println("Error al registrar User");
        }
    }

    // Método para leer los usuarios desde un archivo y devolverlos en un mapa
    public static Map<String, String> leerUsers() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        Map<String, String> map = new HashMap<>();
        try (BufferedReader lector = new BufferedReader(new FileReader("data/Users.txt"))) {
            String linea;
            while (((linea = lector.readLine()) != null)) {
                linea = LZ78.decode(linea);
                String[] array = linea.split(",");
                String user = array[0];
                String pswd = array[1];
                map.put(user, pswd);
            }
        } catch (Exception e) {
            System.out.println("Error al leer User.txt");
        }
        return map;
    }

    // Métodos privados

    // Método para obtener el valor de una etiqueta XML
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                return node.getTextContent();
            }
        }
        return "";
    }
}