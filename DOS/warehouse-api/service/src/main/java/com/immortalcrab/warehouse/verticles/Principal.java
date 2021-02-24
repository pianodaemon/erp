package com.immortalcrab.warehouse.verticles;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Principal extends AbstractVerticle {

    private Future<String> spinUpHttpServer(int httpPort) {

        Promise<String> promise = Promise.promise();

        vertx.createHttpServer().requestHandler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!");
        }).listen(httpPort, http -> {
            if (http.succeeded()) {
                promise.complete("HTTP server started on port " + httpPort);
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
                final int httpPort = config.result().getInteger("HTTP_DYN_PORT", 8888);
                this.setUp(this.spinUpHttpServer(httpPort));
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
    public static final int WORKER_INSTANCES = 2;
}
