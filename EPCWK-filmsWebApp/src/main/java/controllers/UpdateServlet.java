package controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.FilmDAO;
import models.Film;

/**
 * Servlet implementation class UpdateServlet
 */
@WebServlet("/UpdateServlet")
public class UpdateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
}