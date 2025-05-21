package controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import database.FilmDAO;

@WebServlet("/DeleteServlet")
public class DeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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