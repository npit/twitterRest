package gr.demokritos.iit.demokritos.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import java.util.logging.*;

@Path("/hello")
public class Server {

    private static final Logger LOGGER = Logger.getLogger( Server.class.getName() );
    private static String keywordsFilePath="keywords";
    private Handler handler;
    public Server(String logfile)
    {
        try {

            System.setProperty("java.util.logging.SimpleFormatter.format",
                    "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %2$s %5$s%6$s%n");
            java.util.logging.SimpleFormatter sf = new SimpleFormatter();
            handler = new FileHandler(logfile);
            handler.setFormatter(sf);
            Logger.getLogger("").addHandler(handler);

            handler.setLevel(Level.INFO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Server()
    {
        this("/home/nik/server.log");
        //this("server.log");
    }

    public keyword [] readKeywordsFile()
    {
        if(true)
        {
            ArrayList<keyword> keywords = new ArrayList<>();
            keywords.add(new keyword());keywords.add(new keyword());
            //keywords.get(0).setId(0);keywords.get(1).setId(1);
            keywords.get(0).setValue("value0");keywords.get(1).setValue("value1");
            return  keywords.toArray(new keyword[keywords.size()]);
        }
        ArrayList<String> keywords = new ArrayList<>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(keywordsFilePath));
            String line;
            while((line = bf.readLine()) != null)
            {
                line = line.trim();
                if(line.isEmpty()) continue;
                keywords.add(line);
            }
            bf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,e.toString(),e);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,e.toString(),e);
        }
        LOGGER.log(Level.INFO,"Read twitter keywords: " + keywords);
        //System.out.println("Read twitter keywords: " + keywords);

        return  keywords.toArray(new keyword[keywords.size()]);
    }
    @GET
    @Path("/test")
    public String test()
    {
        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
                + "<body><h1>" + "Hello Jersey HTML" + "</h1></body>" + "</html> ";
    }

    @GET
    @Path("/testParam")
    public String testparam(@DefaultValue("DefaultParamValue") @QueryParam("value") String value)
    {

        return "<html> " + "<title>" + "value is:[" + value + "]" + "</title>"
                + "<body><h1>" + "Hello Jersey HTML" + "</h1></body>" + "</html> ";
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
        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(keywordsFilePath));
            for(keyword k: obj)
            {
                wr.write(k.getValue() + "\n");
            }
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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