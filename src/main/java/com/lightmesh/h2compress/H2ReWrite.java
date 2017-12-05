package com.lightmesh.h2compress;

import java.io.File;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.tools.DeleteDbFiles;

public class H2ReWrite
{
  public String src_db_file;
  public String dst_db_file;
  public String src_db_user;
  public String src_db_pass;

  public H2ReWrite(String src_h2_file, String dst_db_file) {
    this.src_db_file = src_h2_file;
    this.dst_db_file = dst_db_file;
    src_db_user = "";
    src_db_pass = "";
  }

  // if src db file exists, we're good.
  public boolean verify_src_db() {
    File src = new File( src_db_file );
    this.src_db_file = src.getAbsolutePath();
    return src.exists();
  }

  // Ensure destination DB backup isn't already on the filesystem
  // Returns true if dst_bkp does not exist
  public boolean verify_dst_db_file() {
    File dst = new File( dst_db_file );
    this.dst_db_file = dst.getAbsolutePath();
    return !dst.exists();
  }

  /** rewrite the given H2 database by backing it up, deleting the old one, 
   *  and restoring from backup.
   *
   */
  public boolean rewrite() { 
    if ( !verify_src_db() || !verify_dst_db_file() ) {
      System.err.println( "Source and Destination DB files could not be verified.");
      return false;
    }

    try {
      exportDatabase();
      System.out.println("Export complete!");
    } catch( SQLException e) {
      System.out.println(e);
      System.out.println("Database EXPORT failed.");
      return false;
    }

    try {
      restoreDatabase();
      System.out.println("Restore complete!");
      System.out.println("You can now rename " + dst_db_file + ".h2.db to " + src_db_file + " to overwrite your bloated database");
      System.out.println("-----------------------------------------------------");
      System.out.println("rm -rf " + src_db_file);
      System.out.println("mv " + dst_db_file + ".h2.db " + src_db_file);
    } catch( SQLException e) {
      System.out.println(e);
      System.out.println("Database RESTORE failed.");
      return false;
    } 
    return true;
  }
  

  public boolean restoreDatabase() throws SQLException {
    System.out.println("Restoring to " + dst_db_file + ", from: " + src_db_file + ".sql (temp SQL file)");
    Connection conn = getDBConnection( dst_db_file );
    Statement stmt = conn.createStatement();
    stmt.execute( String.format("RUNSCRIPT FROM '%s'", src_db_file + ".sql") );
    return true;
  }
  
  public boolean exportDatabase() throws SQLException {

    System.out.println("Exporting " + src_db_file + " to: " + src_db_file + ".sql");
    Connection conn = getDBConnection(src_db_file);
    Statement stmt = conn.createStatement();
    stmt.executeQuery( String.format("SCRIPT TO '%s'", src_db_file + ".sql") );
    return true;
  }

  /**
   * Backup database to a compressed ZIP. 
   */
  public boolean backupDatabase() throws SQLException {
    System.out.println("Backing up " + src_db_file + " to: " + src_db_file + ".zip");
    Connection conn = getDBConnection(src_db_file);
    Statement stmt = conn.createStatement();
    stmt.execute(String.format("BACKUP TO '%s'", src_db_file + ".zip"));
    return true;
  }


  public static Connection getDBConnection(String db_file) {
    return getDBConnection(db_file, null, null);
  }

  public static Connection getDBConnection(String db_file, String user, String pass) {
    String connection_str = "jdbc:h2:" + db_file + ";MV_STORE=FALSE;MVCC=FALSE";
    Connection dbConnection = null;

    try {
      Class.forName("org.h2.Driver");
    } catch (ClassNotFoundException e) {
      System.out.println(e.getMessage());
    }

    try {
      dbConnection = DriverManager.getConnection(connection_str, user, pass);
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return dbConnection;
  }
}

