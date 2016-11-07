package gr.demokritos.iit.demokritos.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import java.util.Properties;
import java.util.logging.*;

@Path("/hello")
public class Server {

    private static final Logger LOGGER = Logger.getLogger( Server.class.getName() );
    private String propertiesFile;
    private String keywordsFilePath, logFilePath, twitterQueriesFilePath, delimiter;
    private Handler handler;

    Properties properties;
    public Server()
    {
        propertiesFile="/var/lib/tomcat7/webapps/twitterRest/WEB-INF/twitterrest.properties";
        keywordsFilePath="UNSET";
        logFilePath = "UNSET";
        twitterQueriesFilePath="UNSET";
        delimiter = "UNSET";
        InputStream input = null;
        properties = new Properties();
        try {
            input  = new FileInputStream(propertiesFile);
            properties.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new javax.ws.rs.WebApplicationException(Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .entity("IO error: properties file not found: " + e.getMessage()).build());

        } catch (IOException e) {
            e.printStackTrace();
            throw new javax.ws.rs.WebApplicationException(Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .entity("IO error on properties file: " + e.getMessage() + "\n").build());
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        keywordsFilePath = properties.getProperty("keywords_file_path","keywords_file");
        logFilePath = properties.getProperty("log_file_path","log_file");
        twitterQueriesFilePath = properties.getProperty("twitter_queries_file","");
        delimiter = properties.getProperty("twitter_queries_file_delimiter","***");

        try {
            // read properties file

            // set logging
            System.setProperty("java.util.logging.SimpleFormatter.format",
                    "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %2$s %5$s%6$s%n");
            java.util.logging.SimpleFormatter sf = new SimpleFormatter();
            handler = new FileHandler(logFilePath);
            handler.setFormatter(sf);
            Logger.getLogger("").addHandler(handler);

            handler.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
            throw new javax.ws.rs.WebApplicationException(Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity("IO error at initialization: " + e.getMessage()).build());

        }
    }

    private void writeKeywordsFile(String destination)
    {
        if(destination.isEmpty()) {
            LOGGER.log(Level.SEVERE, "writeKeywordsFile : Unset destinationfile path!");
            return;
        }
        keyword [] kws = readKeywordsFile();

        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(destination));
            for( keyword kw : kws)
            {
                wr.write(kw.getValue() + delimiter + kw.getLang() + delimiter + kw.getLimit() + "\n");
            }


            wr.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.log(Level.INFO,"Wrote keywords to destination " + destination + ".");

    }
    private keyword [] readKeywordsFile()
    {

        ArrayList<keyword> keywords = new ArrayList<>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(keywordsFilePath));
            String line;
            while((line = bf.readLine()) != null)
            {
                line = line.trim();
                if(line.isEmpty()) continue;
                keywords.add(new keyword(line));
            }
            bf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
            throw new javax.ws.rs.WebApplicationException(Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity("IO error, not found: " + e.getMessage()).build());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
            throw new javax.ws.rs.WebApplicationException(Response.status(HttpURLConnection.HTTP_INTERNAL_ERROR).entity("IO error: " + e.getMessage()).build());

        }
        LOGGER.log(Level.INFO,"Read twitter keywords: " + keywords);
        //System.out.println("Read twitter keywords: " + keywords);

        if(keywords.isEmpty()) return new keyword[0];

        keyword[] karray = new keyword[keywords.size()];
        karray = keywords.toArray(karray);
        return  karray;
    }
    @GET
    @Path("/test")
    public String test()
    {
        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
                + "<body><h1>" + "Hello Jersey HTML" + "</h1></body>" + "</html> ";
    }
    @GET
    @Path("/info")
    public String info()
    {
        String content ="<br/>kwpath:["+keywordsFilePath+"]";
        content += "<br/>logpath:["+logFilePath+"]";
        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
                + "<body><h1>" + "Info:\n"+content + "</h1></body>" + "</html> ";
    }


    @POST
    @Path("/setKeywords")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response putJSON(keyword[] obj)
    {

        if(obj.length == 0 || obj == null)
        {
            throw new javax.ws.rs.WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST).entity("Empty or null keywords list").build());
        }
        writeKeywordsFile(keywordsFilePath);
        writeKeywordsFile(twitterQueriesFilePath);
        return Response.status(200).entity("OK").build();


    }
    @GET
    @Path("/getKeywords")
    @Produces(MediaType.APPLICATION_JSON)
    public keyword[] getJSON()
    {
        keyword [] kwords = this.readKeywordsFile();

        return kwords;
    }


}