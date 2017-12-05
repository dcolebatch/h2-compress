package com.lightmesh.h2compress;

import java.io.File;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * This app will read one h2 database file, and write to a new one.
 * Use this to compress/defragment an otherwise leaky database, usually 
 * from long-running h2 processes.
 *
 * Usage:
 *
 *   java h2compress src_db.h2 dst_tmp.sql
 *
 */
public class App {

  public static void main( String[] args ) {
    long start_time = System.currentTimeMillis();

    if ( args.length < 2 ) {
      throw new java.lang.RuntimeException("Requires two arguments: <path_to_src_h2_db> <dest_h2_db>");
    }

    H2ReWrite app = new H2ReWrite(args[0], args[1]);
    boolean verify_ok = true;

    System.out.print( "Looking for source database '" + app.src_db_file + "'... ");
    if( app.verify_src_db() ) {
      System.out.println( "OK.");
    } else {
      System.out.println( "FAIL! Does not exist.");
      verify_ok = false;
    }

    if( app.verify_dst_db_file() ) {

    } else {
      System.out.println( "FAIL! Destination database '" + app.dst_db_file + "' already exists.");
      System.out.println( "rm -rf '" + app.dst_db_file + "' and try again.");
      verify_ok = false;
    }

    if( ! verify_ok ) {
      System.exit(1);
    }

    // Rewrite the database:
    if( ! app.rewrite() ) {
      System.exit(1);
    }

    long end_time = System.currentTimeMillis();
    NumberFormat formatter = new DecimalFormat("#0.00000");
    System.out.println("Execution time is " + formatter.format((end_time - start_time) / 1000d) + " seconds");
  }
}
