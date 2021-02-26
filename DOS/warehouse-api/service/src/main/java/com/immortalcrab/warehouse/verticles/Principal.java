package com.immortalcrab.warehouse.verticles;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler;
import io.vertx.ext.web.api.validation.ParameterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Principal extends AbstractVerticle {

    private Future<String> spinUpHttpServer(final int port) {

        Promise<String> promise = Promise.promise();

        EventBus eb = vertx.eventBus();

        Router baseRouter = Router.router(vertx);
        Router apiRouter = Router.router(vertx);

        baseRouter.mountSubRouter("/api/v1", apiRouter);

        HTTPRequestValidationHandler validationHandler;
        validationHandler = HTTPRequestValidationHandler.create()
                .addPathParam("warehouseId", ParameterType.INT)
                .addPathParam("productId", ParameterType.INT)
                .addPathParam("presentationId", ParameterType.INT);

        apiRouter.get("/existence/:warehouseId/:productId/:presentationId").handler(validationHandler).handler(routingContext -> {
            HttpServerResponse response = routingContext.response();

            {
                RequestParameters params = routingContext.get("parsedParameters");

                JsonObject payload = new JsonObject()
                        .put("presentationId", params.pathParameter("presentationId").getInteger())
                        .put("warehouseId", params.pathParameter("warehouseId").getInteger())
                        .put("productId", params.pathParameter("productId").getInteger());

                eb.<JsonObject>request(SyncDbBridge.EXISTANCE_PER_PRESENTATION, payload, reply -> {
                    if (reply.succeeded()) {
                        JsonObject replyBody = reply.result().body();
                        response.end(Json.encodePrettily(replyBody));
                    } else {
                        logger.warn("an error has occuried at the consumer {}", SyncDbBridge.EXISTANCE_PER_PRESENTATION);
                        response.setStatusCode(502).end();
                    }
                });
            }

        });

        vertx.createHttpServer().requestHandler(baseRouter).listen(port, http -> {
            if (http.succeeded()) {
                promise.complete("HTTP server started on port " + port);
            } else {
                promise.fail(http.cause());
            }
        });

        return promise.future();
    }

    private void gearUpVerticles() {

        DeploymentOptions opts = new DeploymentOptions()
                .setInstances(Principal.WORKER_INSTANCES)
                .setWorker(true);

        vertx.deployVerticle(SyncDbBridge.class.getName(), opts);

        vertx.deployVerticle(StaticFileServer.class.getName());
    }

    @Override
    public void start(Promise<Void> pro) throws Exception {

        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        retriever.getConfig(config -> {
            if (config.failed()) {
                pro.fail(config.cause());
            } else {
                final int port = config.result().getInteger("HTTP_DYN_PORT", 8888);
                this.setUp(this.spinUpHttpServer(port));
                pro.complete();
            }
        });
    }

    private void setUp(Future<String> fut) {

        this.gearUpVerticles();

        fut.onComplete(ar -> {
            if (ar.failed()) {
                logger.warn("Something bad happened: {}", ar.cause().toString());
            } else {
                logger.info("Result: " + ar.result());
            }
        });
    }

    private final Logger logger = LoggerFactory.getLogger(Principal.class);
    public static final int WORKER_INSTANCES = 4;
}
