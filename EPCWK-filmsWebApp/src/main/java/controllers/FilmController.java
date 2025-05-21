package controllers;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import database.FilmDAO;
import jakarta.xml.bind.JAXBElement;
import models.Film;
import models.FilmWrapper;

/**
 * Servlet implementation class FilmController
 */
@WebServlet("/FilmController")
public class FilmController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private FilmDAO dao = new FilmDAO();
	private Gson gson = new Gson();
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
	    response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	    response.setDateHeader("Expires", 0); // Proxies.

	    
	    String dataFormat = request.getParameter("type");
//	    System.out.println(dataFormat);

	    // Server-side Pagination - Not required anymore
//	    String startParam = request.getParameter("page");
//	    String countParam = request.getParameter("count");
	    int start = 0;
	    int count = 20;
//
//	    if (startParam != null && !startParam.isEmpty()) {
//	        start = Integer.parseInt(startParam);
//	    }
//
//	    if (countParam != null && !countParam.isEmpty()) {
//	        count = Integer.parseInt(countParam);
//	    }

	    ArrayList<Film> films = dao.getAllFilms();

	    int totalFilms = dao.getTotalFilms();
	    int totalPages = (int) Math.ceil((double) totalFilms / count);
	    

	 // Check the Accept header and set the content type accordingly
	    if (dataFormat != null && dataFormat.equals("xml")) {
        	// Generate XML data and write it to the response
            try {
                String xml = generateXML(films);
                response.setContentType("application/xml");
                PrintWriter out = response.getWriter();
//                System.out.println("XML Data: " + xml);
                out.print(xml);
                out.flush();
            } catch (JAXBException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
	    } else if (dataFormat != null && dataFormat.equals("text/plain")) {
            // Generate String data and write it to the response
            String text = generateText(films);
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
//            System.out.println("Text Data: " + text);
            out.print(text);
            out.flush();
        } else {
            // Generate JSON data and write it to the response
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            String json = gson.toJson(films);
//            System.out.println("JSON Data: " + json);
            out.print("{\"data\":" + json + ",\"total_count\":" + totalFilms + ",\"total_pages\":" + totalPages + "}");
            out.flush();
        }
	}
	
	private String generateXML(ArrayList<Film> films) throws JAXBException {
	    FilmWrapper filmWrapper = new FilmWrapper(films);

	    JAXBContext jaxbContext = JAXBContext.newInstance(FilmWrapper.class);
	    Marshaller marshaller = jaxbContext.createMarshaller();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	    StringWriter sw = new StringWriter();
	    marshaller.marshal(filmWrapper, sw);

	    return sw.toString();
	}
	
	private String generateText(ArrayList<Film> films) {
        StringBuilder builder = new StringBuilder();
        for (Film film : films) {
            builder.append(film.toString());
            builder.append("\n");
        }
        return builder.toString();
    }


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
