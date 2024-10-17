package edu.badpals.controlador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import edu.badpals.modelo.Image;
import edu.badpals.modelo.Rating;
import edu.badpals.modelo.Schedule;
import edu.badpals.modelo.Serie;
import edu.badpals.modelo.Episodio;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class JSONHandler {

    public Document serieToXML(Serie serie) throws ParserConfigurationException {
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
        image.appendChild(doc.createTextNode(String.valueOf(serie.getImage().getMedium())));
        rootElement.appendChild(image);

        return doc;
    }

    public String XMLtoString(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Serie JSONtoSerie(String json) {
        try {
            if (json != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(json, Serie.class);
            }
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        return new Serie();

    }

    public Serie fromXML(File xmlFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            Serie serie = new Serie();

            serie.setId(Integer.parseInt(getTagValue("id", root)));
            serie.setName(getTagValue("name", root));
            serie.setType(getTagValue("type", root));
            serie.setLanguage(getTagValue("language", root));
            serie.setStatus(getTagValue("status", root));
            serie.setPremiered(getTagValue("premiered", root));
            serie.setRating(new Rating(Double.parseDouble(getTagValue("rating", root))));

            List<String> genres = new ArrayList<>();
            NodeList genreNodes = root.getElementsByTagName("genre");
            for (int i = 0; i < genreNodes.getLength(); i++) {
                genres.add(genreNodes.item(i).getTextContent());
            }
            serie.setGenres(genres);

            Schedule schedule = new Schedule();
            Element scheduleElement = (Element) root.getElementsByTagName("schedule").item(0);
            schedule.setTime(getTagValue("time", scheduleElement));

            List<String> days = new ArrayList<>();
            NodeList dayNodes = scheduleElement.getElementsByTagName("day");
            for (int i = 0; i < dayNodes.getLength(); i++) {
                days.add(dayNodes.item(i).getTextContent());
            }
            schedule.setDays(days);
            serie.setSchedule(schedule);

            serie.setImage(new Image(getTagValue("image", root)));

            return serie;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList != null && nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                return node.getTextContent();
            }
        }
        return null;
    }

    public static Serie cargarSerie() {
        try{
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            xmlMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            return xmlMapper.readValue(new File("data/Serie.xml"), Serie.class);

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new Serie();
    }

    public List<Episodio> JSONtoEpisodios(String json) {
        try {
            if (json != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Episodio> episodios = Arrays.asList(objectMapper.readValue(json, Episodio[].class));
                return episodios;
            }
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }

    public Document episodiosToXML(List<Episodio> episodios) throws ParserConfigurationException {
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

    public List<Episodio> fromXMLtoEpisodios(File xmlFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList episodioNodes = doc.getElementsByTagName("episodio");
            List<Episodio> episodios = new ArrayList<>();

            for (int i = 0; i < episodioNodes.getLength(); i++) {
                Element episodioElement = (Element) episodioNodes.item(i);
                Episodio episodio = new Episodio();

                episodio.setSeason(Integer.parseInt(getTagValue("season", episodioElement)));
                episodio.setNumber(Integer.parseInt(getTagValue("number", episodioElement)));
                episodio.setName(getTagValue("name", episodioElement));

                episodios.add(episodio);
            }

            return episodios;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



}