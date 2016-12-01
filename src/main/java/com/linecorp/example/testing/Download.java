
package com.linecorp.example.testing;

import com.google.gson.Gson;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.linecorp.example.testing.db.DbContract;
import com.linecorp.example.testing.db.PostgresHelper;

import org.json.JSONArray;

import java.sql.SQLException;

@RestController
@RequestMapping(value="/testing")
public class Download
{
    @RequestMapping(value="/fileDownload", method=RequestMethod.GET)
    public ResponseEntity<String> world()
    {
        PostgresHelper client = new PostgresHelper(DbContract.HOST, DbContract.DB_NAME,
                                                   DbContract.USERNAME, DbContract.PASSWORD);
        
        JSONArray existsData = new JSONArray();
        System.out.println("Temp Data: " + existsData.toString());
        try {
            if (client.connect()) {
                existsData = client.getUserFiles("files", "u0548b5a7b118920bb2285820ec04f931");
                System.out.println("Exists Data: " + existsData.toString());
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
        catch(Exception e)
        {
            System.out.println("Unknown exception occurs");
        }
        
        /*
        final String html = "<html><body><h1>Download File</h1><a href='/download/internal'\">Download This File (located inside project)</a><br/><a href='/download/external'\">Download This File (located outside project, on file system)</a></body></html> ";
        */
        
        return new ResponseEntity<String>(existsData.toString(), HttpStatus.OK);
    }
};
