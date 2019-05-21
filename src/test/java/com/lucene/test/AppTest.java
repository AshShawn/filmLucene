package com.lucene.test;

import java.io.File;
import java.util.Properties;



/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    public static void main(String[] args) {

        Properties properties = new Properties();

        File file= new File("src/main/resources/conf.properties");
        System.out.println(file.exists());

    }
    
}
