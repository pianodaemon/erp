package com.immortalcrab.warehouse.endpoints;

import com.immortalcrab.warehouse.verticles.SyncDbBridge;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler;
import io.vertx.ext.web.api.validation.ParameterType;
import org.slf4j.Logger;

public class Transfers {

    private Transfers() {
    }

    public static Route bindExistancePerPresentation(EventBus eb, Route route, Logger logger) {

        Transfers cls = new Transfers();

        HTTPRequestValidationHandler valHandler = HTTPRequestValidationHandler.create()
                .addPathParam("warehouseId", ParameterType.INT)
                .addPathParam("productId", ParameterType.INT)
                .addPathParam("presentationId", ParameterType.INT);

        return route.handler(valHandler).handler(routingContext -> {
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
                        response
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .end(Json.encodePrettily(replyBody));
                    } else {
                        ReplyException ex = (ReplyException) reply.cause();
                        logger.warn("an error has occuried at the consumer {}", SyncDbBridge.EXISTANCE_PER_PRESENTATION);
                        response.setStatusCode(ex.failureCode()).end();
                    }
                });
            }

        });
    }
}
