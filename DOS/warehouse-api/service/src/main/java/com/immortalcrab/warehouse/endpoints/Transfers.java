package com.immortalcrab.warehouse.endpoints;

import com.immortalcrab.warehouse.persistence.PgsqlInteractions;
import com.immortalcrab.warehouse.verticles.SyncDbBridge;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler;
import io.vertx.ext.web.api.validation.ParameterType;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import org.javatuples.Pair;
import org.slf4j.Logger;

public class Transfers {

    public static String EXISTANCE_PER_PRESENTATION = "existance-per-presentation";

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

                eb.<JsonObject>request(EXISTANCE_PER_PRESENTATION, payload, reply -> {
                    if (reply.succeeded()) {
                        JsonObject replyBody = reply.result().body();
                        response
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .end(Json.encodePrettily(replyBody));
                    } else {
                        ReplyException ex = (ReplyException) reply.cause();
                        logger.warn("an error has occuried at the consumer {}", EXISTANCE_PER_PRESENTATION);
                        response.setStatusCode(ex.failureCode()).end();
                    }
                });
            }

        });
    }

    public static void actOnExistancePerPresentation(Message<JsonObject> message, Logger logger) {
        JsonObject body = message.body();

        try {
            Pair<Double, Integer> answer = PgsqlInteractions.existancePerPresentation(body.getInteger("productId"),
                    body.getInteger("presentationId"),
                    body.getInteger("warehouseId"),
                    logger);

            //Shapping the json object reply (AKA the jor)
            {
                JsonObject jor = new JsonObject();
                jor.put("existance", answer.getValue0());
                jor.put("digits", answer.getValue1());
                message.reply(jor);
            }

        } catch (SQLException | NoSuchElementException ex) {
            logger.error(ex.getMessage());
            if (ex instanceof SQLException) {
                message.fail(502, ex.getMessage());
            }
            if (ex instanceof NoSuchElementException) {
                message.fail(404, ex.getMessage());
            }
        }
    }
}
