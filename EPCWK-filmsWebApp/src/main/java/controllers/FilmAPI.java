package controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.google.gson.Gson;

import database.FilmDAO;
import models.Film;
import models.FilmWrapper;

/**
 * Servlet implementation class FilmAPI
 */
@WebServlet("/FilmAPI")
public class FilmAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private FilmDAO dao = new FilmDAO();
	private Gson gson = new Gson();
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
	    response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	    response.setDateHeader("Expires", 0); // Proxies.

	    
	    String dataFormat = request.getParameter("type");
	    System.out.println(dataFormat);

	    
	    int start = 0;
	    int count = 20;

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
//                out.print("{\"data\":" + xml + ",\"total_count\":" + totalFilms + ",\"total_pages\":" + totalPages + "}");
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
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String title = request.getParameter("title");
        int year = Integer.parseInt(request.getParameter("year"));
        String director = request.getParameter("director");
        String stars = request.getParameter("stars");
        String review = request.getParameter("review");

        // Create a Film object with the form data
        Film film = new Film();
        film.setTitle(title);
        film.setYear(year);
        film.setDirector(director);
        film.setStars(stars);
        film.setReview(review);

        // Call the insertFilm method from your DAO to insert the film into the database
        FilmDAO filmDAO = new FilmDAO();
        filmDAO.insertFilm(film);
        
        // Set the appropriate response
        String jsonResponse = "{\"status\":\"success\",\"message\":\"Film inserted successfully\"}";

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
	}
	
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Retrieve the updated film data from the request parameters
        int id = Integer.parseInt(request.getParameter("id"));
        String title = request.getParameter("title");
        int year = Integer.parseInt(request.getParameter("year"));
        String director = request.getParameter("director");
        String stars = request.getParameter("stars");
        String review = request.getParameter("review");

        // Create a Film object with the updated data
        Film updatedFilm = new Film(id, title, year, director, stars, review);

        // Update the film in the database using the FilmDAO
        FilmDAO filmDAO = new FilmDAO();
        filmDAO.updateFilm(updatedFilm);
        
        // Assuming the update operation was successful, return appropriate response
        String jsonResponse = "{\"status\":\"success\",\"message\":\"Film updated successfully\"}";

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get the ID of the film record to delete from the request parameter
        int id = Integer.parseInt(request.getParameter("id"));

        // Delete the film record from the database
        FilmDAO dao = new FilmDAO();
        dao.deleteFilm(id);

        // Set the response type to JSON
        String jsonResponse = "{\"status\":\"success\",\"message\":\"Film deleted successfully\"}";
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Write the JSON response
        response.getWriter().write(jsonResponse);
    }
}
