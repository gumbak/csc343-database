import java.sql.*;

public class Assignment2 {
    
	// A connection to the database  
	Connection connection;
	
	// Statement to run queries
	Statement sql;
	
	// Prepared Statement
	PreparedStatement ps;
	
	// Resultset for the query
	ResultSet rs;
	ResultSetMetaData rsmd; 
	
	//CONSTRUCTOR
	Assignment2(){
		try{
			Class.forName("org.postgresql.Driver"); 
			return; 
		} catch (ClassNotFoundException e) {
			return; 
		}
	}
	
	//Using the input parameters, establish a connection to be used for this session. Returns true if connection is sucessful
	public boolean connectDB(String URL, String username, String password){
		// URL format: jdbc:postgresql://localhost:5432/csc343h-username
		try {
			connection = DriverManager.getConnection(URL, username, password);
			sql = connection.createStatement();
			return true; 
		}
		catch (SQLException e) {
			return false; 
		}
	}
	
	//Closes the connection. Returns true if closure was sucessful
	public boolean disconnectDB(){
		try {
			ps.close(); 	  
			rs.close();
			connection.close();
			return true; 
		}
		catch (Exception e) {
			return false; 
		} 
	}
    
	//Inserts row into the winemaker table
	public boolean insertWinemaker(int wmid, String wmname, int cid){	  
		try {
			String checkCidExists = "SELECT * FROM countries WHERE cid=" + cid + "";
			rs = sql.executeQuery(checkCidExists); 
			
			if (rs == null) {
				return false; 
			}	  
			
			String insertSQL = "INSERT INTO winemakers VALUES (?,?,?);"; 
			ps = connection.prepareStatement(insertSQL); 
			ps.setInt(1, wmid); 
			ps.setString(2, wmname); 
			ps.setInt(3, cid); 
			ps.executeUpdate(); 
			
			return true; 
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public int getWinemakersCount(String cname){
		try {
			String sqlText = "SELECT COUNT(*) FROM winemakers WHERE cid= (SELECT cid FROM countries WHERE cname='" + cname + "')"; 
			
			rs = sql.executeQuery(sqlText);
			
			if (rs == null)
				return -1; 
			
			rs.next(); 
			return rs.getInt(1); 
		}
		catch (Exception e) {
			return -1; 
		}
	}
	
	public String getWinemakerInfo(int wmid){
		try{
			rs = sql.executeQuery("SELECT WM.wmname, C.cname FROM winemakers WM, countries C WHERE WM.cid = C.cid AND WM.wmid = " + wmid + " ORDER BY WM.wmname ASC");
			
			if(rs != null){
				rs.next();
				String wmname = rs.getString("wmname");
				String countryname = rs.getString("cname");
				return wmname + ":" + countryname;
			} else
				return "";         
			
		} catch (Exception e){
			return "";
		}
		
	}
	
	public boolean chgCountry(int cid, String newCName){
		try {
			String sqlText = "UPDATE countries SET cname='" + 
				newCName + "' WHERE cid='" + cid + "'"; 
			sql.executeUpdate(sqlText); 
			return true; 
		}
		catch (Exception e){
			return false; 
		}
	}
	
	public boolean deleteCountry(int cid){
		try{
			String sqlText = "DELETE FROM countries C WHERE C.cid = " + cid;
			
			sql.executeUpdate(sqlText);
			
			return true;    
			
		} catch (Exception e) {
			return false;			 
		}        
	}
	
	public String listWines(int wmid){
		try {
			String sqlText = "SELECT wname, cname AS country, wyear AS year, " + 
			"bestbeforeny, msrp, rating FROM wine, winemakers, countries, ratings " + 
			"WHERE wine.wmid = winemakers.wmid " +
			"AND winemakers.cid = countries.cid " + 
			"AND wine.rid = ratings.rid " + 
			"AND wine.wmid = '" + wmid + "' " +
			"ORDER BY wname ASC, country ASC, year ASC"; 
			
			rs = sql.executeQuery(sqlText); 
			rsmd = rs.getMetaData(); 
			int numCols = rsmd.getColumnCount(); 
			
			String output = ""; 
			
			if (rs != null) {
				while (rs.next()) {
					for (int i = 1; i <= numCols; i++) {
						output += rs.getString(i) + ":"; 
					}
					// remove last ':' character
					output = output.substring(0, output.length()-1);
					
					output += "#"; 
				}
			} else {
				return ""; 
			}
			
			// remove last '#' character
			output = output.substring(0, output.length()-1); 
			
			return output; 
		}
		catch (Exception e) {
			return ""; 
		}
	}
	
	public boolean updateRatings(int wmid, int wyear){
		String result; 
		int curRating; 
		
		// Find rid of wmid and wyear.
		try{
			rs = sql.executeQuery("SELECT DISTINCT W.rid, R.rating FROM wine W, ratings R WHERE R.rid = W.rid AND W.wmid = " + wmid + " AND W.wyear = " + wyear + " AND R.rating <= 4");
			
			if(rs == null)
				return false;         
			
		} catch (Exception e) {
			return false; 
		}
		    
		
		// Update ratings table using found rid.
		try{		
			String sqlText = "";
			while(rs.next()){
				curRating = rs.getInt("rating") + 1;
				sqlText = sqlText +  "UPDATE ratings SET rating = " + curRating + " WHERE rid = " + rs.getInt("rid") + ";";
			}


			sql.executeUpdate(sqlText);
			
			return true;    
			
		} catch (Exception e) {
		    e.printStackTrace();
			return false; 
		}	
	}
	
	public String query7(){
		String result; 
		
		// Delete views, if they exist.
		try{
			String dropViews = "DROP VIEW IF EXISTS redCandidates CASCADE; DROP VIEW IF EXISTS roseCandidates CASCADE;";
			
			sql.executeUpdate(dropViews);

		} catch (Exception e) {
			return ""; 
		}			 


		// Create red candidates that match requirements.
		try{
			String createRedView = "CREATE VIEW redCandidates(wmid, wmname, wid) AS SELECT WM.wmid, WM.wmname, W.wid FROM countries C, winemakers WM, wine W WHERE C.cname = 'Spain' AND C.cid = WM.cid AND WM.wmid = W.wmid AND W.wcid IN (SELECT WC.wcid FROM winecolours WC WHERE WC.wcname = 'Red') AND W.msrp > ANY (SELECT P.price FROM pricelist P WHERE W.wid = P.wid AND (P.startyear < 2013 OR (P.startyear = 2013 AND P.startmonth <= 10)) AND (P.endyear > 2013 OR (P.endyear = 2013 AND P.endmonth >= 10))  ) GROUP BY WM.wmid, WM.wmname, W.wid";
			
			sql.executeUpdate(createRedView);

		} catch (Exception e) {	
			return ""; 
		}			 
		

		// Create rose.
		try{	 
			String createRoseView = "CREATE VIEW roseCandidates(wmid, wmname, wid) AS SELECT WM.wmid, WM.wmname, W.wid FROM countries C, winemakers WM, wine W WHERE C.cname = 'Spain' AND C.cid = WM.cid AND WM.wmid = W.wmid AND W.wcid IN (SELECT WC.wcid FROM winecolours WC WHERE WC.wcname = 'Rose') AND W.msrp > ANY (SELECT P.price FROM pricelist P WHERE W.wid = P.wid AND (P.startyear < 2013 OR (P.startyear = 2013 AND P.startmonth <= 10)) AND (P.endyear > 2013 OR (P.endyear = 2013 AND P.endmonth >= 10))) GROUP BY WM.wmid, WM.wmname, W.wid";
			
			sql.executeUpdate(createRoseView);
		} catch (Exception e) {
			return ""; 
		}	
		

		// Intersect red and rose candidates.
		try{
			String createRedRoseView = "CREATE VIEW redRoseCandidates(wmid) AS SELECT wmid FROM redCandidates INTERSECT SELECT wmid FROM roseCandidates;";
			sql.executeUpdate(createRedRoseView);
			
		} catch (Exception e) {
			return ""; 
		}	
		

		// Union red and rose candidates
		try{
			String createCandidatesRating = "CREATE VIEW candidatesRating(wmid,wmname,wid) AS SELECT * FROM redCandidates UNION ALL SELECT * FROM roseCandidates;";
			sql.executeUpdate(createCandidatesRating);

		} catch (Exception e) {	
			return ""; 
		}

		// Union red and rose candidates averages
		try{
			String createCandidatesAvgs = "CREATE VIEW redRoseCandidatesAvg(wmid, wmname, avgRating) AS SELECT cR.wmid, cR.wmname, AVG(R.rating) AS avgRating FROM candidatesRating cR, wine W, ratings R WHERE cR.wmid IN (SELECT * FROM redRoseCandidates) AND cR.wid = W.wid AND W.rid = R.rid GROUP BY cR.wmid, cR.wmname";
			sql.executeUpdate(createCandidatesAvgs);
		
		} catch (Exception e) {

			return ""; 
		}


		// Find highest and lowest ratings in Spain.
      		try{
			rs = sql.executeQuery("SELECT avgs.wmname, avgs.avgRating FROM (SELECT * FROM redRoseCandidatesAvg AS temp1 WHERE temp1.avgRating = (SELECT MAX(findMax.avgRating) FROM redRoseCandidatesAvg findMax) UNION ALL SELECT * FROM redRoseCandidatesAvg AS temp2 WHERE temp2.avgRating = (SELECT MIN(findMin.avgRating) FROM redRoseCandidatesAvg findMin)) AS avgs	ORDER BY avgs.avgRating DESC, avgs.wmname ASC");
		
			if(rs == null){
				// Delete views.
				try{
					String dropViews = "DROP VIEW IF EXISTS redCandidates CASCADE; DROP VIEW IF EXISTS roseCandidates CASCADE;";

					sql.executeUpdate(dropViews);

				} catch (Exception e) {
					return ""; 
				}
	
				return ""; 
			}
			
			result = "";
			
			for(int i = 1; rs.next(); i++){
					
				if(i == 1)
					result = rs.getString("wmname") + ":" + rs.getDouble("avgRating");
				else
				    result = result + "#" + rs.getString("wmname") + ":" + rs.getDouble("avgRating");
				
				
			}
							
		} catch (Exception e) {

			return "";
		}

		// Delete views, if they exist.
		try{
			String dropViews = "DROP VIEW IF EXISTS redCandidates CASCADE; DROP VIEW IF EXISTS roseCandidates CASCADE;";
			
			sql.executeUpdate(dropViews);

		} catch (Exception e) {
			return ""; 
		}

		return result;

	}
	
	public boolean updateDB(){
		// Drop winemakersForAllwines if it exits 

		// Create table winemakersForAllWines
		try{ 
			
			String dropTable = "DROP TABLE IF EXISTS winemakersForAllWines CASCADE; DROP VIEW IF EXISTS redCandidates CASCADE; DROP VIEW IF EXISTS roseCandidates CASCADE; DROP VIEW IF EXISTS whiteCandidates CASCADE;";
			sql.executeUpdate(dropTable);
			
		} catch (Exception e){
			return false;
		}	
		
		// Create table winemakersForAllWines
		try{ 
			
			String createTable = "CREATE TABLE winemakersForAllWines (wmid INTEGER PRIMARY KEY, wmname VARCHAR(20) NOT NULL);";
			
			sql.executeUpdate(createTable);
			
		} catch (Exception e) {
			return false;
		}	
		
		// Create red candidates that match requirements.
		try{
			String createRedView = "CREATE VIEW redCandidates(wmid, wmname) AS SELECT WM.wmid, WM.wmname FROM winemakers WM, wine W WHERE WM.wmid = W.wmid AND W.wcid IN (SELECT WC.wcid FROM winecolours WC WHERE WC.wcname = 'Red') GROUP BY WM.wmid, WM.wmname";
			
			sql.executeUpdate(createRedView);
		} catch (Exception e) {
		    e.printStackTrace();
			return false;
		}
		
		// Create rose.
		try{	 
			String createRoseView = "CREATE VIEW roseCandidates(wmid, wmname) AS SELECT WM.wmid, WM.wmname FROM winemakers WM, wine W WHERE WM.wmid = W.wmid AND W.wcid IN (SELECT WC.wcid FROM winecolours WC WHERE WC.wcname = 'Rose') GROUP BY WM.wmid, WM.wmname";
			
			sql.executeUpdate(createRoseView);
		} catch (Exception e) {
			return false;
		}	
		
		// Create white.
		try{	 
			String createWhiteView = "CREATE VIEW whiteCandidates(wmid, wmname) AS SELECT WM.wmid, WM.wmname FROM winemakers WM, wine W WHERE WM.wmid = W.wmid AND W.wcid IN (SELECT WC.wcid FROM winecolours WC WHERE WC.wcname = 'White') GROUP BY WM.wmid, WM.wmname";
			
			sql.executeUpdate(createWhiteView);
		} catch (Exception e) {		
			return false;
		}	
		
		// Intersect red, white, rose candidates.
		try{
			String createRedWhiteRoseView = "CREATE VIEW redWhiteRoseCandidates AS SELECT * FROM redCandidates INTERSECT SELECT * FROM roseCandidates INTERSECT SELECT * FROM whiteCandidates;";
			
			sql.executeUpdate(createRedWhiteRoseView);
			
		} catch (Exception e) {
			return false;
		}
		
		// Intersect red, white, rose candidates.
		try{
			String storeIntoTable = "INSERT INTO winemakersForAllWines( SELECT DISTINCT * FROM redWhiteRoseCandidates result ORDER BY result.wmid ASC, result.wmname ASC);";
			
			sql.executeUpdate(storeIntoTable);
			
		} catch (Exception e) {
			return false;
		}

		// Drop extra views
		try{ 
			
			String dropTable = "DROP VIEW IF EXISTS redCandidates CASCADE; DROP VIEW IF EXISTS roseCandidates CASCADE; DROP VIEW IF EXISTS whiteCandidates CASCADE;";
			sql.executeUpdate(dropTable);
			
		} catch (Exception e){
			return false;
		}	

		return true; 
	}
	
}
