package sk.intersoft.vicinity.platform.semantic.service.resource;

import org.restlet.resource.Delete;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.platform.semantic.Thing2Ontology;
import sk.intersoft.vicinity.platform.semantic.utils.DateTimeUtil;

import java.util.logging.Level;

public class RemoveThingFromOntologyResource extends ServerResource {
    final static Logger logger = LoggerFactory.getLogger(RemoveThingFromOntologyResource.class.getName());

    @Delete()
    public String doRemoval() throws Exception {

        try{
            String oid = getAttribute("oid");

            logger.info("=============================");
            logger.info("=============================");
            logger.info("EXECUTE DELETE FOR: ["+oid+"]");

            Thing2Ontology handler = new Thing2Ontology();

            long start = DateTimeUtil.millis();
            boolean result = handler.delete(oid.trim());

            long end = DateTimeUtil.duration(start);
            logger.info("DELETE TOOK: " +DateTimeUtil.format(end));

            return ServiceResponse.success(ServiceResponse.REMOVED, result+"").toString();
        }
        catch(Exception e){
            return ServiceResponse.failure(e).toString();
        }
    }
}
