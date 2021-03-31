package com.immortalcrab.warehouse.verticles;

import com.immortalcrab.warehouse.applications.Transfers;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.CompositeFuture;
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
                    apiRouter.get("/almacenes/por_empresa/:empresaId"),
                    this.logger);

            Transfers.bindWarehousesTraspasos(eb,
                    apiRouter.post("/almacenes/traspasos"),
                    this.logger,
                    vertx);
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

    private Future<Void> asyncVerticleDeployer(final Class cls) {
        final String name = cls.getName();
        final Promise<Void> promise = Promise.promise();
        vertx.deployVerticle(name, res -> {
            if (res.failed()) {
                logger.error("Failed to deploy async verticle " + name);
                promise.fail(res.cause());
            } else {
                promise.complete();
            }
        });

        return promise.future();
    }

    private Future<Void> syncVerticleDeployer(final Class cls, final int nthreads) {

        final String name = cls.getName();
        final Promise<Void> promise = Promise.promise();
        vertx.deployVerticle(name, new DeploymentOptions()
                .setInstances(nthreads)
                .setWorker(true), res -> {
            if (res.failed()) {
                logger.error("Failed to deploy sync verticle " + name);
                promise.fail(res.cause());
            } else {
                promise.complete();
            }
        });

        return promise.future();
    }

    private void gearUpVerticles(Promise<Void> pro, final int port) {

        CompositeFuture fut = CompositeFuture.all(
                syncVerticleDeployer(SyncDbBridge.class, SyncDbBridge.REQUIRED_WORKER_THREADS),
                asyncVerticleDeployer(StaticFileServer.class),
                this.spinUpHttpServer(port));

        fut.onComplete(result -> {
            if (result.succeeded()) {
                logger.info("Result: " + result.result());
                pro.complete();
            } else {
                pro.fail(result.cause());
            }
        });
    }

    @Override
    public void start(Promise<Void> pro) throws Exception {

        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        retriever.getConfig(config -> {
            if (config.failed()) {
                pro.fail(config.cause());
            } else {

                this.gearUpVerticles(pro, config.result().getInteger("HTTP_DYN_PORT", 8888));
            }
        });
    }

    private void bindHealth(Route route) {
        route.handler(HealthCheckHandler.create(vertx)
                .register("http-server-running", future -> future.complete(started ? Status.OK() : Status.KO())));
    }

    private final Logger logger = LoggerFactory.getLogger(Principal.class);
    private boolean started;
}
