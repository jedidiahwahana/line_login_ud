
package com.linecorp.example.testing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.linecorp.example.testing.db.DbContract;

@SpringBootApplication(exclude=DataSourceAutoConfiguration.class)
public class Application extends SpringBootServletInitializer
{
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder app)
    {
        return app.sources(Application.class);
    }

    public static void main(String [] args)
    {
        SpringApplication.run(Application.class, args);
        
        try {
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection(
                                                       DbContract.HOST+DbContract.DB_NAME,
                                                       DbContract.USERNAME,
                                                       DbContract.PASSWORD);
            
            System.out.println("DB connected");
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
};
