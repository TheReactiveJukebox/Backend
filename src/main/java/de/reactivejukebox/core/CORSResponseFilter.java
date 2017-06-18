package de.reactivejukebox.core;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;

/**
 * Custom CORS-Filter
 * (Cross-Origin Resource Sharing)
 */
public class CORSResponseFilter
        implements ContainerResponseFilter {

    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {

        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        //headers.add("Access-Control-Allow-Origin", "http://reactivejukebox.de"); //allows CORS requests only coming from reactivejukebox.de
        headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, UPDATE");
        headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Authorization");
    }

}