package org.dataportal.users;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Date;
import java.util.Random;
import org.dataportal.datasources.Db;

public class User {
	public static final String NOT_CONFIRMED = "NOT_CONFIRMED";
	public static final String ACTIVE = "ACTIVE";
	public static final String BLOCKED = "BLOCKED";
	public static final String NONEXISTENT = "NONEXISTENT";

	private static final char[] HEX = new String("0123456789abcdef").toCharArray();
	private static final char[] CHARS = new String("0123456789abcdefghijklmnopqrstuvwxyz").toCharArray();
	
	private String login = null;
	private String password = null;
	
	public User(String login, String password) {
		this.login = login;
		this.password = password;
	}
	
	public User() {
	}
	
	public boolean isActive() throws Exception {
		return this.getState().equals(ACTIVE);
	}
	
	public boolean exists() throws Exception {
		return !(this.getState().equals(NONEXISTENT));
	}
	
	public String save() throws Exception {
		Connection conn = null;
		String now = Long.toString(new Date().getTime());
		String hash = User.hex_md5(this.login+now);
		try {
			conn = new Db().getConnection();
            PreparedStatement st = conn.prepareStatement(
                "INSERT INTO users (id, password, state, hash) VALUES (?, ?, ?, ?);"
            );
            st.setString(1, this.login);
            st.setString(2, this.password);
            st.setString(3, User.NOT_CONFIRMED);
            st.setString(4, hash);
            conn.setAutoCommit(true);
            st.executeUpdate();
            //st.close();
            conn.close();
            //conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if(conn != null) conn.close();
			} catch (Exception ee){
				ee.printStackTrace();
			}
			throw (new Exception(e));
		}
		return hash;
	}
	
	public boolean changePass(String newPassword) throws Exception {
		if (this.isActive()) {
			Connection conn = null;
			int r = 0;
			try {
				conn = new Db().getConnection();
	            PreparedStatement st = conn.prepareStatement(
	                "UPDATE users SET password = ? WHERE id = ? and password = ?;"
	            );
	            st.setString(1, newPassword);
	            st.setString(2, this.login);
	            st.setString(3, this.password);
	            r = st.executeUpdate();
	            st.close();
	            conn.close();
	            conn = null;
			} catch (SQLException e) {
				e.printStackTrace();
				try {
					if(conn != null) conn.close();
				} catch (Exception ee){
					ee.printStackTrace();
				}
				throw (new Exception(e));
			}
			return (r==1);			
		} else {
			return false;
		}
	}
	
	public String setHash(String login, String state) throws Exception {
		String now = Long.toString(new Date().getTime());
		String hash = User.hex_md5(login+now);
		int r = 0;
		Connection conn = null;
		try {
			conn = new Db().getConnection();
            PreparedStatement st = conn.prepareStatement(
                "UPDATE users SET hash = ? WHERE id = ? and state = ?;"
            );
            st.setString(1, hash);
            st.setString(2, login);
            st.setString(3, state);
            r = st.executeUpdate();
            st.close();
            conn.close();
            conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if(conn != null) conn.close();
			} catch (Exception ee){
				ee.printStackTrace();
			}
			throw (new Exception(e));
		}
		if (r==1) {
			return hash;
		} else {
			return null;
		}
	}
	
	public String login() {
		return this.login;
	}
	
	public String newPass(String hash) throws Exception {
		String password = User.randomString(6);
		int r = 0;
		Connection conn = null;
		try {
			conn = new Db().getConnection();
			PreparedStatement st = conn.prepareStatement(
            	"SELECT id FROM users WHERE hash = ?;"
            );
			st.setString(1, hash);
			ResultSet rs = st.executeQuery();
            if(!rs.next()) {
            	conn.close();
            	return null;
            }
            this.login = rs.getString(1);
            st = conn.prepareStatement(
                "UPDATE users SET hash='', password = ? WHERE hash = ?;"
            );
            st.setString(1, User.hex_md5(password));
            st.setString(2, hash);
            r = st.executeUpdate();
            st.close();
            conn.close();
            conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if(conn != null) conn.close();
			} catch (Exception ee){
				ee.printStackTrace();
			}
			throw (new Exception(e));
		}
		if (r==1) {
			return password;
		} else {
			return null;
		}
	}

	public String activate(String hash) throws Exception {
		Connection conn = null;
		try {
			conn = new Db().getConnection();
			PreparedStatement st = conn.prepareStatement(
            	"SELECT id FROM users WHERE hash = ?;"
            );
			st.setString(1, hash);
			ResultSet rs = st.executeQuery();
            if(!rs.next()) {
            	conn.close();
            	return null;
            }
            this.login = rs.getString(1);
            rs.close();
			st = conn.prepareStatement(
                "UPDATE users SET hash='', state = ? WHERE hash = ?;"
            );
            st.setString(1, User.ACTIVE);
            st.setString(2, hash);
            st.executeUpdate();
            st.close();
            conn.close();
            conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if(conn != null) conn.close();
			} catch (Exception ee){
				ee.printStackTrace();
			}
			throw (new Exception(e));
		}
		return this.login;
	}
	
	public String getState() throws Exception {
	
		String state = null;
		Connection conn = null;
		try {
			conn = new Db().getConnection();
            PreparedStatement st = conn.prepareStatement(
                "SELECT state FROM users WHERE id = ? and password = ?;"
            );
            st.setString(1, this.login);
            st.setString(2, this.password);
            ResultSet rs = st.executeQuery();
    
           if(rs.next()) {
        	   state = rs.getString("state");
           } else {
        	   state = NONEXISTENT;
           }
           st.close();
           conn.close();
           conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if(conn != null) conn.close();
			} catch (Exception ee){
				ee.printStackTrace();
			}
			throw (new Exception(e));
		}
		return state;
	}
	
    private static String hex_md5(String stringToHash)  {
            MessageDigest md;
			try {
				md = MessageDigest.getInstance("MD5");
	            byte[] bytes = md.digest(stringToHash.getBytes());
	            StringBuilder sb = new StringBuilder(2 * bytes.length);
	            for (int i = 0; i < bytes.length; i++) {
	                int low = (int)(bytes[i] & 0x0f);
	                int high = (int)((bytes[i] & 0xf0) >> 4);
	                sb.append(HEX[high]);
	                sb.append(HEX[low]);
	            }
	            return sb.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			}
    }

    private static String randomString(int length) {
    	Random rnd = new Random(new Date().getTime());
    	StringBuilder sb = new StringBuilder(length);
    	for(int i=0; i<length; i++) {
    		sb.append(CHARS[rnd.nextInt(CHARS.length)]);
    	}
    	return sb.toString();
    }

	public void delete(int id) throws Exception {
		Connection conn = null;
		try {
			conn = new Db().getConnection();
            PreparedStatement st = conn.prepareStatement(
                "DELETE FROM users WHERE id = ?;"
            );
            st.setInt(1, id);
            st.executeUpdate();
            st.close();
            conn.close();
            conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if(conn != null) conn.close();
			} catch (Exception ee){
				ee.printStackTrace();
			}
			throw (new Exception(e));
		}
	}

	public void changeState(int id, String newState) throws Exception {
		Connection conn = null;
		try {
			conn = new Db().getConnection();
            PreparedStatement st = conn.prepareStatement(
                "UPDATE users SET state = ? WHERE id = ?;"
            );
            st.setString(1, newState);
            st.setInt(2, id);
            st.executeUpdate();
            st.close();
            conn.close();
            conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				if(conn != null) conn.close();
			} catch (Exception ee){
				ee.printStackTrace();
			}
			throw (new Exception(e));
		}
	}
    
}
