package sk.intersoft.vicinity.platform.semantic.service;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import sk.intersoft.vicinity.platform.semantic.service.resource.AliveResource;
import sk.intersoft.vicinity.platform.semantic.service.resource.RemoveThingFromOntologyResource;
import sk.intersoft.vicinity.platform.semantic.service.resource.SPARQLResource;
import sk.intersoft.vicinity.platform.semantic.service.resource.Thing2OntologyResource;

public class SemanticRepositoryApplication extends Application {
    public static final String ALIVE = "/alive";
    public static final String SPARQL = "/sparql";
    public static final String CREATE_TD = "/td/create";
    public static final String REMOVE_TD = "/td/remove/{oid}";

    private ChallengeAuthenticator createApiGuard(Restlet next) {

        ChallengeAuthenticator apiGuard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "realm");

        apiGuard.setNext(next);

        // In case of anonymous access supported by the API.
        apiGuard.setOptional(true);

        return apiGuard;
    }

    public Router createApiRouter() {
        Router apiRouter = new Router(getContext());
        apiRouter.attach(ALIVE, AliveResource.class);
        apiRouter.attach(SPARQL, SPARQLResource.class);
        apiRouter.attach(CREATE_TD, Thing2OntologyResource.class);
        apiRouter.attach(REMOVE_TD, RemoveThingFromOntologyResource.class);

        return apiRouter;
    }

    public Restlet createInboundRoot() {

        Router apiRouter = createApiRouter();
        ChallengeAuthenticator guard = createApiGuard(apiRouter);
        return guard;
    }

}
