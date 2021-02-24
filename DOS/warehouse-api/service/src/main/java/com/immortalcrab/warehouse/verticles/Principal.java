package com.immortalcrab.warehouse.verticles;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Principal extends AbstractVerticle {

    private Future<String> spinUpHttpServer() {

        Promise<String> promise = Promise.promise();

        vertx.createHttpServer().requestHandler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello from Vert.x!");
        }).listen(8888, http -> {
            if (http.succeeded()) {
                promise.complete("HTTP server started on port 8888");
            } else {
                promise.fail(http.cause());
            }
        });

        return promise.future();
    }

    private void gearUpVerticles(ConfigRetriever retriever) {

        DeploymentOptions opts = new DeploymentOptions()
                .setInstances(Principal.WORKER_INSTANCES)
                .setWorker(true);

        vertx.deployVerticle(SyncDbBridge.class.getName(), opts);

        vertx.deployVerticle(StaticFileServer.class.getName());
    }

    @Override
    public void start() throws Exception {

        Future<String> future = this.spinUpHttpServer();
        ConfigRetriever retriever = ConfigRetriever.create(vertx);

        this.gearUpVerticles(retriever);

        future.onComplete(ar -> {
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
