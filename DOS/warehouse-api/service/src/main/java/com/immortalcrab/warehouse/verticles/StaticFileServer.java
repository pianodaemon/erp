package com.immortalcrab.warehouse.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaticFileServer extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {

        final Router router = Router.router(vertx);
        router.route("/static/*").handler(StaticHandler.create());

        vertx.createHttpServer().requestHandler(router).listen(8889, http -> {
            if (http.succeeded()) {
                startPromise.complete();
                logger.info("HTTP server started on port 8889");
            } else {
                startPromise.fail(http.cause());
            }
        });
    }

    private final Logger logger = LoggerFactory.getLogger(StaticFileServer.class);
}
