package sk.intersoft.vicinity.platform.semantic.lifting.model;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.intersoft.vicinity.platform.semantic.Repository;
import sk.intersoft.vicinity.platform.semantic.ontology.NamespacePrefix;
import sk.intersoft.vicinity.platform.semantic.ontology.Namespaces;

public class AnnotationsHierarchy {
    final static Logger logger = LoggerFactory.getLogger(AnnotationsHierarchy.class.getName());
    Repository repository = Repository.getInstance();
    ValueFactory factory = SimpleValueFactory.getInstance();

    private static final String CLASS_KEY = "class";
    private static final String PATH_KEY = "path";
    private static final String SUBCLASSES_KEY = "sub-classes";
    private static final String INDIVIDUALS_KEY = "individuals";

    public JSONObject traverse(String className, String path, boolean withIndividuals) {
        path = path + " / "+className;

//        logger.info("GETTING ANNOTATIONS HIERARCHY FOR [" + className + " :: path: " + path + " :: individuals: " + withIndividuals + "]");
        JSONObject result = new JSONObject();
        result.put(PATH_KEY, path);
        JSONArray subclassArray = new JSONArray();
        JSONArray individualArray = new JSONArray();

        result.put(CLASS_KEY, className);
        result.put(SUBCLASSES_KEY, subclassArray);
        if (withIndividuals){
            result.put(INDIVIDUALS_KEY, individualArray);
        }

        try{
            RepositoryConnection connection = repository.getConnection();
            try{
                RepositoryResult<Statement> subclasses =
                        connection.getStatements(
                                null,
                                factory.createIRI(
                                        Namespaces.toURI(
                                                Namespaces.prefixed(NamespacePrefix.rdfs, "subClassOf"))),
                                factory.createIRI(
                                        Namespaces.toURI(className)),
                                false);
//                logger.debug("annotation subclasses for ["+className+"] .. triples .. has next: "+subclasses.hasNext());
                if(subclasses.hasNext()){
                    while (subclasses.hasNext()) {
                        Statement st = subclasses.next();
                        try{
                            String subclass = Namespaces.toPrefixed(st.getSubject().stringValue());
//                            logger.debug("trying subclass: ["+subclass+"]");
//                            logger.debug("predicate::object ["+st.getPredicate().toString()+" :: "+st.getObject().toString()+"]");
                            subclassArray.put(traverse(subclass, path, withIndividuals));
                        }
                        catch(Exception ex){
                            logger.error("", ex);
                        }
                    }
                }

                if(withIndividuals){
                    RepositoryResult<Statement> individuals =
                            connection.getStatements(
                                    null,
                                    factory.createIRI(
                                            Namespaces.toURI(
                                                    Namespaces.prefixed(NamespacePrefix.rdf, "type"))),
                                    factory.createIRI(
                                            Namespaces.toURI(className)),
                                    false);
//                    logger.debug("annotation individuals for ["+className+"] .. triples .. has next: "+individuals.hasNext());
                    if(individuals.hasNext()){
                        while (individuals.hasNext()) {
                            Statement st = individuals.next();
                            try{
                                String individual = Namespaces.toPrefixed(st.getSubject().stringValue());
//                                logger.debug("individual: ["+individual+"]");
                                individualArray.put(individual);
                            }
                            catch(Exception ex){
                                logger.error("", ex);
                            }
                        }
                    }

                }
            }
            catch (Exception e) {
                logger.error("", e);
            }
            finally {
                connection.close();
            }
            if(subclassArray.length() == 0) result.remove(SUBCLASSES_KEY);
            if(!withIndividuals) result.remove(INDIVIDUALS_KEY);

        }
        catch(Exception e){
            logger.error("", e);
        }
        return result;
    }

    public JSONObject dump(){
        JSONObject result = new JSONObject();
        result.put("device-hierarchy", traverse("core:Device", "", false));
        result.put("service-hierarchy", traverse("core:Service", "", false));
        result.put("property-hierarchy", traverse("ssn:Property", "", true));
        return result;
    }
}
