package edu.badpals.controlador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.badpals.modelo.*;
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


    // aqui sacamos los objetos del xml
    public static Serie fileToSerie(File xmlFile) {
        return XMLToSerie(LectorFile(xmlFile).toString());
    }

    public static List<Episodio> fileToEpisodios(File xmlFile) {
        return XMLToEpisodios(LectorFile(xmlFile).toString());
    }

    public static List<String> fileToActores(File xmlFile) {
        return XMLToCast(LectorFile(xmlFile).toString());
    }

    public static StringBuilder LectorFile(File xmlFile) {
        StringBuilder xmlContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(xmlFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                xmlContent.append(line);
            }
        } catch (IOException e){
            System.out.println("Error lector File");
        }
        return xmlContent;
    }

    public static List<String> XMLToCast(String xmlString) {
        List<String> castList = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            document.getDocumentElement().normalize();

            NodeList castNodes = document.getElementsByTagName("cast");
            for (int i = 0; i < castNodes.getLength(); i++) {
                Element castElement = (Element) castNodes.item(i);

                String name = getTagValue("name", castElement);
                String character = getTagValue("character", castElement);

                castList.add("Actor: " + name + ", Personaje: " + character);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return castList;
    }

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

    public static Serie XMLToSerieCache(String xmlString) {
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
            serie.setImage(image);

            return serie;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

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


    // aqui pasamos de objetos a xml

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


    // Aqui manejamos los JSON

    public static Serie JSONtoSerie(String json) {
        try {
            if (json.startsWith("{")) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(json, Serie.class);
            }
            Serie serie = new Serie();
            serie.setId(0);
            return serie;
        } catch (JsonProcessingException e) {
            System.out.println("Error al parsear el json a serie");
        }
        Serie serie = new Serie();
        serie.setId(0);
        return serie;

    }

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

    // Aqui manejamos los Users del login

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

    public static Map<String, String> leerUsers() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        try (BufferedReader lector = new BufferedReader(new FileReader("data/Users.txt"))) {
            Map<String, String> map = new HashMap<>();
            String linea;
            while (((linea = lector.readLine()) != null)) {
                linea = LZ78.decode(linea);
                String[] array = linea.split(",");
                String user = array[0];
                String pswd = array[1];
                map.put(user, pswd);
            }
            return map;
        } catch (Exception e) {
            System.out.println("Error al leer User.txt");
        }
        return null;
    }


    // metodos privados

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                return node.getTextContent();
            }
        }
        return null;
    }


}