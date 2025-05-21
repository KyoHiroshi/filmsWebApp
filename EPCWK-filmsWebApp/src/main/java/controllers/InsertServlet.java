package controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import database.FilmDAO;
import models.Film;

@WebServlet("/InsertServlet")
public class InsertServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
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
}