
package com.linecorp.example.testing;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.linecorp.example.testing.db.DbContract;
import com.linecorp.example.testing.db.PostgresHelper;

@RestController
@RequestMapping(value="/testing")
public class UploadController
{
    PostgresHelper client = new PostgresHelper(DbContract.HOST, DbContract.DB_NAME,
                                               DbContract.USERNAME, DbContract.PASSWORD);
    
    @RequestMapping(value="/singleSave", method=RequestMethod.POST )
    public @ResponseBody String singleSave(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc ){
        System.out.println("File Description:"+desc);
        String fileName = null;
        Map<String,Object> vals = new HashMap<>();
        if (!file.isEmpty()) {
            try {
                fileName = file.getOriginalFilename();
                byte[] bytes = file.getBytes();
                BufferedOutputStream buffStream =
                new BufferedOutputStream(new FileOutputStream(new File("/Users/line/Desktop/file/" + fileName)));
                buffStream.write(bytes);
                buffStream.close();
                
                vals.put("name", fileName);
                vals.put("mid", desc);
                vals.put("url", "/Users/line/Desktop/file/" + fileName);
                
                saveData(vals);
                return "You have successfully uploaded " + fileName;
            } catch (Exception e) {
                return "You failed to upload " + fileName + ": " + e.getMessage();
            }
        } else {
            return "Unable to upload. File is empty.";
        }
    }
    
    private void saveData(Map<String,Object> dataValue){
        try {
            if (client.connect()) {
                System.out.println("DB connected");
                if (client.insert("files", dataValue) == 1) {
                    System.out.println("Record added");
                }
            }
            
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
        catch(Exception e)
        {
            System.out.println("Unknown exception occurs");
        }
    }
};
