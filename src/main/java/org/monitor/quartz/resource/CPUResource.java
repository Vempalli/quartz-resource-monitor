package org.monitor.quartz.resource;

import org.monitor.quartz.entities.Memory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Produces("application/json")
@Path("/tasks/cpu")
public class CPUResource {
    @GET
    public List<Memory> listAll() {
        return Memory.listAll();
    }
}
