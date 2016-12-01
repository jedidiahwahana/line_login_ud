
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
        PostgresHelper client = new PostgresHelper(DbContract.URL);
        
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
        
        return new ResponseEntity<String>(existsData.toString(), HttpStatus.OK);
    }
};
