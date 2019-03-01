package nl.hu.v1ipass.thirdapp.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import nl.hu.v1ipass.thirdapp.model.CustomerSeries;

public class CustomerSeriesDAO extends BaseDAO {

	public CustomerSeriesDAO() {
	}

	// Alle CustomerSeries ontvangen
	public ArrayList<CustomerSeries> getAll() {
		ArrayList<CustomerSeries> CustomerSerieslijst = new ArrayList<CustomerSeries>();
		try {
			Connection conn = super.getConnection();

			// Een eerste SQL PreparedStatement maken
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CustomerSeries");

			// Een tweede PreparedStatement uitvoeren
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				CustomerSerieslijst.add(convertCS(rs));
			}
			// De resultset, het PreparedStatement en de verbinding sluiten
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return CustomerSerieslijst;
	}

	// Alle CustomerSeries van een klant ophalen
	public ArrayList<CustomerSeries> getCustomerSeriesbyCustomerID(int cd) {
		ArrayList<CustomerSeries> CustomerSerieslijst = new ArrayList<CustomerSeries>();
		try {
			Connection conn = super.getConnection();

			// Een eerste SQL PreparedStatement maken
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM CustomerSeries s WHERE s.ID=?");

			// Een tweede PreparedStatement uitvoeren
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				CustomerSerieslijst.add(convertCS(rs));
			}
			// De resultset, het PreparedStatement en de verbinding sluiten
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return CustomerSerieslijst;
	}

	// Informatie over Series+Email Notificaties ophalen van alle aan de klant
	// gekoppelde CustomerSeries van airing Series
	public ArrayList<CustomerSeries> getFinishedCustomerSeriesbyCustomerID(int cd, String date) {
		ArrayList<CustomerSeries> CustomerSerieslijst = new ArrayList<CustomerSeries>();
		try {
			Connection conn = super.getConnection();

			// Een eerste SQL PreparedStatement maken
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customerseries cs where cs.customerid=? AND cs.seriesid in (select s.id from Series s WHERE s.enddate<to_date(?,'DD/MM/YY'))");
			stmt.setInt(1, cd);
			stmt.setString(2, date);
			
			// Een tweede PreparedStatement uitvoeren
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				CustomerSerieslijst.add(convertCS(rs));
			}
			
			// De resultset, het PreparedStatement en de verbinding sluiten
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return CustomerSerieslijst;
	}

	// Informatie over Series+Email Notificaties ophalen van alle aan de klant
	// gekoppelde CustomerSeries van afgelopen series
	public ArrayList<CustomerSeries> getUnfinishedCustomerSeriesbyCustomerID(int cd, String date) {
		ArrayList<CustomerSeries> CustomerSerieslijst = new ArrayList<CustomerSeries>();
		try {
			Connection conn = super.getConnection();

			// Een eerste SQL PreparedStatement maken
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customerseries cs where cs.customerid=? AND cs.seriesid in (select s.id from Series s WHERE s.enddate is null OR s.enddate>to_date(?,'DD/MM/YY'))");
			stmt.setInt(1, cd);
			stmt.setString(2, date);
			
			// Een tweede PreparedStatement uitvoeren
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				CustomerSerieslijst.add(convertCS(rs));
			}
			// De resultset, het PreparedStatement en de verbinding sluiten
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return CustomerSerieslijst;
	}

	public ArrayList<CustomerSeries> getEmailbyCustomerID(int cd, String date) {
		ArrayList<CustomerSeries> CustomerSerieslijst = new ArrayList<CustomerSeries>();
		try {
			Connection conn = super.getConnection();

			// Een eerste SQL PreparedStatement maken
			PreparedStatement stmt = conn.prepareStatement(
					"SELECT * FROM customerseries cs where cs.customerid=? AND email_not='Yes' AND finished='No' AND cs.seriesid in (select s.id from Series s WHERE s.enddate=(to_date(?,'DD/MM/YY')+7))");
			stmt.setInt(1, cd);
			stmt.setString(2, date);

			// Een tweede PreparedStatement uitvoeren
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				CustomerSeries cs=convertCS(rs);
				CustomerSerieslijst.add(cs);
				stmt = conn
						.prepareStatement("Update CustomerSeries set finished='Yes' where customerid=? AND seriesid=?");
				stmt.setInt(1, cs.getCustID());
				stmt.setInt(2, cs.getSeriesID());
				stmt.executeUpdate();
			}
			// De resultset, het PreparedStatement en de verbinding sluiten
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return CustomerSerieslijst;
	}

	// Voeg CustomerSeries toe
	public void addEntry(CustomerSeries cs) {
		try {
			Connection conn = super.getConnection();
			conn.setAutoCommit(false);

			PreparedStatement stmt = conn.prepareStatement("insert into customerseries values(?,?,0, 'No','No','PTW')");
			stmt.setInt(1, cs.getCustID());
			stmt.setInt(2, cs.getSeriesID());

			stmt.executeUpdate();
			stmt.close();
			conn.commit();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Verwijder CustomerSeries
	public void deleteEntry(CustomerSeries cs) {
		try {
			Connection conn = super.getConnection();
			conn.setAutoCommit(false);

			PreparedStatement stmt = conn
					.prepareStatement("delete from customerseries where customerid=? AND seriesid=?");
			stmt.setInt(1, cs.getCustID());
			stmt.setInt(2, cs.getSeriesID());

			// Een tweede PreparedStatement uitvoeren
			stmt.executeUpdate();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Update Score van een CustomerSeries
	public void updateScore(CustomerSeries cs, int score) {
		try {
			Connection conn = super.getConnection();
			conn.setAutoCommit(false);

			PreparedStatement stmt = conn
					.prepareStatement("update customerseries set myscore=? where customerid=? AND seriesid=?");
			stmt.setInt(1, score);
			stmt.setInt(2, cs.getCustID());
			stmt.setInt(3, cs.getSeriesID());

			// Een tweede PreparedStatement uitvoeren
			stmt.executeUpdate();
			conn.commit();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Update emailnotificaties van een CustomerSeries
	public void updateEmail(CustomerSeries cs, String email) {
		try {
			Connection conn = super.getConnection();
			conn.setAutoCommit(false);
			System.out.println("Ready to put");

			PreparedStatement stmt = conn
					.prepareStatement("update customerseries set email_not=? where customerid=? AND seriesid=?");
			stmt.setString(1, email);
			stmt.setInt(2, cs.getCustID());
			stmt.setInt(3, cs.getSeriesID());

			// Een tweede PreparedStatement uitvoeren
			stmt.executeUpdate();
			conn.commit();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateStatus(CustomerSeries cs, String status) {
		try {
			Connection conn = super.getConnection();

			// Een eerste SQL PreparedStatement maken
			PreparedStatement stmt = conn
					.prepareStatement("update customerseries set status=? where customerid=? AND seriesid=?");
			stmt.setString(1, status);
			stmt.setInt(2, cs.getCustID());
			stmt.setInt(3, cs.getSeriesID());

			// Een tweede PreparedStatement uitvoeren
			stmt.executeQuery();
			conn.commit();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private CustomerSeries convertCS(ResultSet rs) throws SQLException {
		CustomerSeries result=null;
		int custid = rs.getInt("CUSTOMERID");
		int seriesid = rs.getInt("SERIESID");
		int score = rs.getInt("myscore");
		String finished = rs.getString("FINISHED");
		String email = rs.getString("email_not");
		String status = rs.getString("status");
		result = new CustomerSeries(custid, seriesid, score, finished, email, status);
		return result;
	}
}
