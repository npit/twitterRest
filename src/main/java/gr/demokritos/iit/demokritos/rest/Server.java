package gr.demokritos.iit.demokritos.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.*;

import java.lang.Class;
import java.lang.reflect.Constructor;

@Path("/hello")
public class Server {

    private static final Logger LOGGER = Logger.getLogger( Server.class.getName() );
    private String propertiesFile;
    private String keywordsFilePath, logFilePath, tweetIDsFilePath, delimiter;
    private Handler handler;

    Properties properties;
    public Server()
    {
        propertiesFile="/var/lib/tomcat7/webapps/twitterRest/WEB-INF/twitterrest.properties";
        keywordsFilePath="UNSET";
        logFilePath = "UNSET";
        tweetIDsFilePath="UNSET";
        delimiter = "UNSET";
        InputStream input = null;
        properties = new Properties();
        try {
            input  = new FileInputStream(propertiesFile);
            properties.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new javax.ws.rs.WebApplicationException(Response.
                status(HttpURLConnection.HTTP_INTERNAL_ERROR)
                    .entity("IO error: properties file not found: " + e.getMessage()).build());

        } catch (IOException e) {
            e.printStackTrace();
            throw new javax.ws.rs.WebApplicationException(Response.
                status(HttpURLConnection.HTTP_INTERNAL_ERROR)
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
        tweetIDsFilePath = properties.getProperty("twitter_ids_file","");
        delimiter = properties.getProperty("twitter_queries_file_delimiter","***");
        Parsable.delimiter = delimiter;

        StringObj.filePath = (keywordsFilePath);
        StringObj.filePath = (tweetIDsFilePath);

        Parsable.ClassNames = new HashMap();
        Parsable.ClassNames.put("str","StringObj");
        Parsable.ClassNames.put("kw","keyword");
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
            throw new javax.ws.rs.WebApplicationException(Response.
                status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                entity("IO error at initialization: " + e.getMessage()).build());

        }
    }

    private void writeKeywordsFile(Parsable[] data, Class<?> classtype) throws Exception, IOException
    {

        String destination = data[0].filePath;
        if(destination.isEmpty()) {
            LOGGER.log(Level.SEVERE, "writeKeywordsFile : Unset destinationfile path!");
            String msg = "IO error destination file to writeKeywordsFile is unset: ";
            throw new Exception(msg);

        }

        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(destination));
            for( Parsable datum : data)
            {
                LOGGER.log(Level.INFO,"Writing " + data.toString());
                wr.write(datum.toString() + "\n");
            }
            wr.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,"Error wtile writing keywords to destination " + 
                destination + "." + e.getMessage());

            throw new IOException(e);
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
            throw new javax.ws.rs.WebApplicationException(Response.
                status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                entity("IO error, not found: " + e.getMessage()).build());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
            throw new javax.ws.rs.WebApplicationException(Response.
                status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                entity("IO error: " + e.getMessage()).build());

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
        content += "<br/>tweetidpath:["+tweetIDsFilePath+"]";
        content += "<br/>logpath:["+logFilePath+"]";
        content += "<br/>delim:["+delimiter+"]";
        content += "<br/>props:["+propertiesFile+"]";
        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
                + "<body><h1>" + "Info:\n"+content + "</h1></body>" + "</html> ";
    }

    // we have to specify each interface explicitly to be able to deserialize the argument
    // a more clever way should obviously exist
    @POST
    @Path("/setstr")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response set(StringObj[] obj)
    {
        Class classtype = StringObj.class;
        if(obj.length == 0 || obj == null)
        {
            throw new javax.ws.rs.WebApplicationException(Response.
                status(HttpURLConnection.HTTP_BAD_REQUEST).
                entity("Empty or null keywords list").build());
        }
        try
        {
            writeKeywordsFile( obj,(classtype));
//            writeKeywordsFile(twitterQueriesFilePath, obj, Class.forName(classtype));
        }
        catch (ClassNotFoundException e) {
            throw new javax.ws.rs.WebApplicationException(Response.
                    status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity("Undefined object type[" +classtype+ "] :\n" + e.getMessage()).build());
        }
        catch(Exception e)
        {
            throw new javax.ws.rs.WebApplicationException(Response.
                status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                entity("Failed to write internal files:\n" + e.getMessage()).build());

        }
        return Response.status(200).entity("OK").build();


    }
    @POST
    @Path("/setkw")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response set(keyword[] obj)
    {
        Class classtype = keyword.class;
        if(obj.length == 0 || obj == null)
        {
            throw new javax.ws.rs.WebApplicationException(Response.
                    status(HttpURLConnection.HTTP_BAD_REQUEST).
                    entity("Empty or null keywords list").build());
        }
        try
        {
            writeKeywordsFile( obj,(classtype));
//            writeKeywordsFile(twitterQueriesFilePath, obj, Class.forName(classtype));
        }
        catch (ClassNotFoundException e) {
            throw new javax.ws.rs.WebApplicationException(Response.
                    status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity("Undefined object type[" +classtype+ "] :\n" + e.getMessage()).build());
        }
        catch(Exception e)
        {
            throw new javax.ws.rs.WebApplicationException(Response.
                    status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity("Failed to write internal files:\n" + e.getMessage()).build());

        }
        return Response.status(200).entity("OK").build();

    }

    @GET
    @Path("/getKeywords")
    @Produces(MediaType.APPLICATION_JSON)
    public keyword[] getKeywords()
    {
        keyword [] kwords = this.readKeywordsFile();

        return kwords;
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Parsable [] getJSON(@QueryParam("type") String type)
    {
        type = Parsable.ClassNames.get(type);
        String classtype = "gr.demokritos.iit.demokritos.rest." + type;
        Parsable [] obj = null;
        try {
            obj = readParsablesFile(Class.forName(classtype));
        } catch (ClassNotFoundException e) {
            throw new javax.ws.rs.WebApplicationException(Response.
                    status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity("Undefined object type[" +classtype+ "] :\n" + e.getMessage()).build());
        }
        if(obj == null)
        {
            obj = (Parsable[]) new StringObj[1];
            obj[0] = new StringObj("EMPTY");
        }
        return obj;
    }

    private Parsable [] readParsablesFile(Class<?> classtype)
    {


        ArrayList<Parsable> data = new ArrayList<>();
        try {
            Constructor Constructor = classtype.getConstructor();
            Object obj = (Constructor.newInstance());
            String filepath = (String) obj.getClass().getField("filePath").get(obj);

                    BufferedReader bf = new BufferedReader(new FileReader(filepath));
            String line;
            while((line = bf.readLine()) != null)
            {
                line = line.trim();
                if(line.isEmpty()) continue;

                Parsable p = (Parsable)Constructor.newInstance();
                p.fromString(line);
                data.add(p);
            }
            bf.close();

        LOGGER.log(Level.INFO,"Read twitter data: " + data);


        if(data.isEmpty()) return null;

        Parsable[] karray = (Parsable[]) Array.newInstance(classtype,data.size());
        karray = data.toArray(karray);
        return  karray;

        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
            throw new javax.ws.rs.WebApplicationException(Response.
                    status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity("IO error, not found: " + e.getMessage()).build());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE,e.getMessage(),e);
            throw new javax.ws.rs.WebApplicationException(Response.
                    status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity("IO error: " + e.getMessage()).build());

        } catch (NoSuchMethodException e) {
            throw new javax.ws.rs.WebApplicationException(Response.
                    status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity("NoSuchMethodException: " + e.getMessage()).build());
        } catch (InstantiationException e) {
            throw new javax.ws.rs.WebApplicationException(Response.
                    status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity("InstantiationException: " + e.getMessage()).build());
        } catch (IllegalAccessException e) {
            throw new javax.ws.rs.WebApplicationException(Response.
                    status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity("IllegalAccessException: " + e.getMessage()).build());
        } catch (InvocationTargetException e) {
            throw new javax.ws.rs.WebApplicationException(Response.
                    status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity("InvocationTargetException: " + e.getMessage()).build());
        } catch (NoSuchFieldException e) {
            throw new javax.ws.rs.WebApplicationException(Response.
                    status(HttpURLConnection.HTTP_INTERNAL_ERROR).
                    entity("NoSuchFieldException: " + e.getMessage()).build());
        }

    }

}