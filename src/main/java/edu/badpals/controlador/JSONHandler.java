package edu.badpals.controlador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.badpals.modelo.Schedule;
import edu.badpals.modelo.Serie;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;

public class JSONHandler {

    public String toSerieXML(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Serie serie = objectMapper.readValue(json, Serie.class);

            Element root = new Element("serie");
            Document doc = new Document(root);

            root.addContent(new Element("id").setText(String.valueOf(serie.getId())));
            root.addContent(new Element("name").setText(serie.getName()));
            root.addContent(new Element("type").setText(serie.getType()));
            root.addContent(new Element("language").setText(serie.getLanguage()));

            Element genres = new Element("genres");
            for (String genre : serie.getGenres()) {
                genres.addContent(new Element("genre").setText(genre));
            }
            root.addContent(genres);

            root.addContent(new Element("status").setText(serie.getStatus()));
            root.addContent(new Element("premiered").setText(serie.getPremiered()));
            root.addContent(new Element("rating").setText(String.valueOf(serie.getRating().getAverage())));

            Element schedule = new Element("schedule");
            schedule.addContent(new Element("time").setText(serie.getSchedule().getTime()));

            Element days = new Element("days");
            for (String day : serie.getSchedule().getDays()) {
                days.addContent(new Element("day").setText(day));
            }
            schedule.addContent(days);
            root.addContent(schedule);

            root.addContent(new Element("image").setText(String.valueOf(serie.getImage())));



            return toXMLString(doc);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toXMLString(Document doc) {
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(Format.getPrettyFormat());
        return xmlOutputter.outputString(doc);
    }

    public Serie toSerie(String json) {
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
}