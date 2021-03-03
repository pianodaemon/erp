package com.immortalcrab.warehouse.verticles;

import com.immortalcrab.warehouse.applications.Transfers;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.Router;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Principal extends AbstractVerticle {

    private Future<String> spinUpHttpServer(final int port) {

        Promise<String> promise = Promise.promise();

        EventBus eb = vertx.eventBus();

        Router baseRouter = Router.router(vertx);
        Router apiRouter = Router.router(vertx);

        baseRouter.mountSubRouter("/api/v1", apiRouter);

        {
            bindHealth(baseRouter.get("/health"));

            Transfers.bindExistancePerPresentation(eb,
                    apiRouter.get("/existence/:warehouseId/:productId/:presentationId"),
                    this.logger);

            Transfers.bindWarehouses(eb,
                    apiRouter.get("/warehouses/:empresaId"),
                    this.logger);
        }
        
        vertx.createHttpServer().requestHandler(baseRouter).listen(port, http -> {
            this.started = http.succeeded();
            if (this.started) {
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
                this.gearUpVerticles();
                final int port = config.result().getInteger("HTTP_DYN_PORT", 8888);
                Future<String> fut = this.spinUpHttpServer(port);
                fut.onComplete(ar -> {
                    if (ar.failed()) {
                        logger.warn("Something bad happened: {}", ar.cause().toString());
                    } else {
                        logger.info("Result: " + ar.result());
                    }
                });
                pro.complete();
            }
        });
    }

    private void bindHealth(Route route) {
        route.handler(HealthCheckHandler.create(vertx)
                        .register("http-server-running", future -> future.complete(started ? Status.OK() : Status.KO())));
    }

    private final Logger logger = LoggerFactory.getLogger(Principal.class);
    private boolean started;
    public static final int WORKER_INSTANCES = 4;
}
