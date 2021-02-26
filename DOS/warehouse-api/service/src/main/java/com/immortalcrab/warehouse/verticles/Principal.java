package com.immortalcrab.warehouse.verticles;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
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

        vertx.setPeriodic(10000, v -> {
            eb.request("ping-address", "ping!", reply -> {
                if (reply.succeeded()) {
                    logger.info("Received reply " + reply.result().body());
                } else {
                    logger.warn("No reply");
                }
            });
        });

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

                Integer warehouseId = params.pathParameter("warehouseId").getInteger();
                Integer productId = params.pathParameter("productId").getInteger();
                Integer presentationId = params.pathParameter("presentationId").getInteger();
                this.logger.info("---- {}", presentationId);
            }

            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>Hello from my first Vert.x 3 application</h1>");
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
