package com.lightmesh.h2compress;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.tools.DeleteDbFiles;

/**
 * Unit test for simple App.
 */
public class AppTest 
  extends TestCase
{
  public static String path_to_h2 = System.getProperty("user.dir");
  public static String h2_file = "test.h2";
  private H2ReWrite app;

  /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public AppTest( String testName )
  {
    super( testName );
  }

  /**
   * @return the suite of tests being tested
   */
  public static Test suite()
  {
    return new TestSuite( AppTest.class );
  }

  public void setUp() {
    // create database
    this.app = new H2ReWrite(path_to_h2 + "/" + h2_file,
                             path_to_h2 + "/test_clean.h2");
    try {
      System.out.println( "Creating test database in " + path_to_h2 );
      create_test_db(path_to_h2 + "/" + h2_file);
      System.out.println( "Test DB written");
    } catch(Exception e) {
      System.out.println(e);
    }
  }

//public void tearDown() {
//  // delete database
//  DeleteDbFiles.execute(path_to_h2, h2_file, true);
//  System.out.println("test database deleted.");
//}

  /**
   * Rigourous Test :-)
   */
  public void testApp()
  {
    assertTrue( true );
  }

  // Test exporting database
  public void testExport()
  {
    boolean backup_ok = false;
    try {
     backup_ok = this.app.exportDatabase();
    } catch( SQLException e ) {
      System.out.println( e );
    }
    assertTrue( backup_ok == true );
  }

  // Test backing up database
  public void testBackup()
  {
    boolean backup_ok = false;
    try {
     backup_ok = this.app.backupDatabase();
    } catch( SQLException e ) {
      System.out.println( e );
    }
    assertTrue( backup_ok == true );
  }
  
  // Test restore database
  public void testRestore()
  {
    boolean restore_ok = false;
    try {
     restore_ok = this.app.restoreDatabase();
    } catch( SQLException e ) {
      System.out.println( e );
    }
    assertTrue( restore_ok == true );
  }


  /********************************************************************
   * PRIVATE Helpers
   **/ 
  private void create_test_db(String h2_db_file) throws java.sql.SQLException {
    Connection conn = this.app.getDBConnection(h2_db_file);

    Statement stmt = null;
    try {
      conn.setAutoCommit(false);
      stmt = conn.createStatement();
      stmt.execute("CREATE TABLE PERSON(id int primary key, name varchar(255))");
      stmt.execute("INSERT INTO PERSON(id, name) VALUES(1, 'Anju')");
      stmt.execute("INSERT INTO PERSON(id, name) VALUES(2, 'Sonia')");
      stmt.execute("INSERT INTO PERSON(id, name) VALUES(3, 'Asha')");

      ResultSet rs = stmt.executeQuery("select * from PERSON");
      System.out.println("H2 Database inserted through Statement");
      while (rs.next()) {
        System.out.println("Id "+rs.getInt("id")+" Name "+rs.getString("name"));
      }
      stmt.close();
      conn.commit();
    } catch (SQLException e) {
      System.out.println("Exception Message " + e.getLocalizedMessage());
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      conn.close();
    }
  }
}
