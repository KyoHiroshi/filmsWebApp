package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import models.Film;

public class FilmDAO {

	Connection conn = null;
	Statement stmt = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	
	// Note none default port used, 6306 not 3306
	
	// Mudfoot
	String user = "tariqmuh";
	String password = "grompTer8";
	String url = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/" + user;
	
	// AWS
//	String db = "tariqmuh";
//	String user = "root";
//	String password = "grompTer8";
//	String url = "jdbc:mysql://films-db-instance0.cy7v3nby9dzr.eu-west-2.rds.amazonaws.com:3306/" + db + "?user=" + user + "&password=" + password;

	public FilmDAO() {}

	private void openConnection() {
		// loading jdbc driver for mysql
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.out.println(e);
		}

		// connecting to database
		try {
			// connection string for demos database, username demos, password demos
			conn = DriverManager.getConnection(url, user, password);
			stmt = conn.createStatement();
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private void closeConnection() {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Film getNextFilm(ResultSet rs) {
		Film thisFilm = null;
		try {
			thisFilm = new Film(rs.getInt("id"), rs.getString("title"), rs.getInt("year"), rs.getString("director"),
					rs.getString("stars"), rs.getString("review"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return thisFilm;
	}
	
	public ArrayList<Film> getFilmsByPage(int page, int limit) {
	    ArrayList<Film> films = new ArrayList<Film>();
	    openConnection();

	    try {
	        String selectSQL = "SELECT * FROM films LIMIT ? OFFSET ?";
	        pstmt = conn.prepareStatement(selectSQL);
	        pstmt.setInt(1, limit);
	        pstmt.setInt(2, Math.max(0, (page - 1) * limit)); // use Math.max to ensure offset >= 0
	        rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Film oneFilm = getNextFilm(rs);
	            films.add(oneFilm);
	        }
	    } catch (SQLException se) {
	        se.printStackTrace();
	    } finally {
	        closeConnection();
	    }
	    return films;
	}

	
	public int getTotalFilms() {
	    int totalFilms = 0;
	    openConnection();
	    try {
	        String countSQL = "SELECT COUNT(*) FROM films";
	        stmt = conn.createStatement();
	        rs = stmt.executeQuery(countSQL);
	        if (rs.next()) {
	            totalFilms = rs.getInt(1);
	        }
	    } catch (SQLException se) {
	        se.printStackTrace();
	    } finally {
	        closeConnection();
	    }
	    return totalFilms;
	}

	public ArrayList<Film> getAllFilms() {

		ArrayList<Film> allFilms = new ArrayList<Film>();
		openConnection();

		// Create select statement and execute it
		try {
//			String selectSQL = "SELECT * FROM films LIMIT 100"; // <-- Change back when finished
//			String selectSQL = "SELECT * FROM films WHERE director like " + "'%Test%'";
//			System.out.println(selectSQL);
			
			String selectSQL = "SELECT * FROM films";
			rs = stmt.executeQuery(selectSQL);
			// Retrieve the results
			while (rs.next()) {
				Film oneFilm = getNextFilm(rs);
				allFilms.add(oneFilm);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			closeConnection();
		}

		return allFilms;
	}

	public Film getFilmByID(int id) {

		Film oneFilm = null;
		openConnection();

		// Create select statement and execute it
		try {
			String selectSQL = "SELECT * FROM films WHERE id = ?";
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setInt(1, id);
			rs = pstmt.executeQuery();
			// Retrieve the results
			if (rs.next()) {
				oneFilm = getNextFilm(rs);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			closeConnection();
		}

		return oneFilm;
	}

	public void insertFilm(Film f) {

		openConnection();

		// Create insert statement and execute it
		try {
			String insertSQL = "INSERT INTO films (title, year, director, stars, review) VALUES (?, ?, ?, ?, ?)";
			pstmt = conn.prepareStatement(insertSQL);
			pstmt.setString(1, f.getTitle());
			pstmt.setInt(2, f.getYear());
			pstmt.setString(3, f.getDirector());
			pstmt.setString(4, f.getStars());
			pstmt.setString(5, f.getReview());
			pstmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			closeConnection();
		}
	}

	public void updateFilm(Film f) {

		openConnection();

		// Create update statement and execute it
		try {
			String updateSQL = "UPDATE films SET title = ?, year = ?, director = ?, stars = ?, review = ? WHERE id = ?";
			pstmt = conn.prepareStatement(updateSQL);
			pstmt.setString(1, f.getTitle());
			pstmt.setInt(2, f.getYear());
			pstmt.setString(3, f.getDirector());
			pstmt.setString(4, f.getStars());
			pstmt.setString(5, f.getReview());
			pstmt.setInt(6, f.getId());
			pstmt.executeUpdate();
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			closeConnection();
		}
	}
	
	
	public void deleteFilm(int id) {
	    openConnection();

	    try {
	        // Create the SQL statement to delete the film record
	        String deleteSQL = "DELETE FROM films WHERE id = ?";
	        PreparedStatement pstmt = conn.prepareStatement(deleteSQL);
	        pstmt.setInt(1, id);

	        // Execute the SQL statement
	        pstmt.executeUpdate();

	        pstmt.close();
	    } catch (SQLException se) {
	        System.out.println(se);
	    } finally {
	        closeConnection();
	    }
	}


	public ArrayList<Film> searchFilms(String searchStr) {

		ArrayList<Film> allFilms = new ArrayList<Film>();
		openConnection();

		// Create select statement with LIKE operator and execute it
		try {
			String selectSQL = "SELECT * FROM films WHERE title LIKE ? OR director LIKE ? OR stars LIKE ?";
			pstmt = conn.prepareStatement(selectSQL);
			pstmt.setString(1, "%" + searchStr + "%");
			pstmt.setString(2, "%" + searchStr + "%");
			pstmt.setString(3, "%" + searchStr + "%");
			rs = pstmt.executeQuery();
			// Retrieve the results
			while (rs.next()) {
				Film oneFilm = getNextFilm(rs);
				allFilms.add(oneFilm);
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			closeConnection();
		}

		return allFilms;
	}
}