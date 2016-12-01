
package com.linecorp.example.testing;

import com.google.gson.Gson;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/testing")
public class Upload
{
    @RequestMapping(value="/fileUpload", method=RequestMethod.GET)
    public ResponseEntity<String> world()
    {
        final String html = "<html><body><h1>Single File Upload</h1><form method=\"post\" enctype=\"multipart/form-data\" action=\"singleSave\">Upload File: <input type=\"file\" name=\"file\"><br /><br />MID User: <input type=\"text\" name=\"desc\"/><br/><br/><input type=\"submit\" value=\"Upload\"></form></body></html> ";
        return new ResponseEntity<String>(html, HttpStatus.OK);
    }
};
