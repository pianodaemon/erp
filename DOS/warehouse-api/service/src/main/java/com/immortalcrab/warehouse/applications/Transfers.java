package com.immortalcrab.warehouse.applications;

import com.immortalcrab.warehouse.persistence.PgsqlInteractions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.validation.HTTPRequestValidationHandler;
import io.vertx.ext.web.api.validation.ParameterType;
import io.vertx.json.schema.Schema;
import io.vertx.json.schema.SchemaParser;
import io.vertx.json.schema.SchemaRouter;
import io.vertx.json.schema.SchemaRouterOptions;
import io.vertx.json.schema.NoSyncValidationException;
import io.vertx.json.schema.ValidationException;
import io.vertx.ext.web.handler.BodyHandler;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.slf4j.Logger;

public class Transfers {

    public static String EXISTANCE_PER_PRESENTATION = "existance-per-presentation";
    public static String WAREHOUSES = "warehouses";
    public static String WAREHOUSES_TRASPASOS_NUEVO = "warehouses-traspasos-nuevo";

    private Transfers() {
    }

    public static Route bindExistancePerPresentation(EventBus eb, Route route, Logger logger) {

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

            //Shaping the json object reply (AKA the jor)
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

    public static Route bindWarehouses(EventBus eb, Route route, Logger logger) {

        HTTPRequestValidationHandler valHandler = HTTPRequestValidationHandler.create()
                .addPathParam("empresaId", ParameterType.INT);

        return route.handler(valHandler).handler(routingContext -> {
            HttpServerResponse response = routingContext.response();

            {
                RequestParameters params = routingContext.get("parsedParameters");

                JsonObject payload = new JsonObject()
                        .put("empresaId", params.pathParameter("empresaId").getInteger());

                eb.<JsonObject>request(WAREHOUSES, payload, reply -> {
                    if (reply.succeeded()) {
                        JsonObject replyBody = reply.result().body();
                        response
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .end(Json.encodePrettily(replyBody));
                    } else {
                        ReplyException ex = (ReplyException) reply.cause();
                        logger.warn("an error has occuried at the consumer {}", WAREHOUSES);
                        response.setStatusCode(ex.failureCode()).end();
                    }
                });
            }

        });
    }

    public static void actOnWarehouses(Message<JsonObject> message, Logger logger) {
        JsonObject body = message.body();

        try {
            ArrayList<Pair<Integer, String>> answer = PgsqlInteractions.getWarehouses(body.getInteger("empresaId"), logger);

            //Shaping the json object reply (AKA the jor)
            {
                JsonArray jArr = new JsonArray();

                answer.stream().map(pair -> {
                    JsonObject jObj = new JsonObject();
                    jObj.put("id", pair.getValue0());
                    jObj.put("titulo", pair.getValue1());
                    return jObj;
                }).forEachOrdered(jObj -> {
                    jArr.add(jObj);
                });

                JsonObject jor = new JsonObject();
                jor.put("almacenes", jArr);
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

    public static Route bindWarehousesTraspasos(EventBus eb, Route route, Logger logger, Vertx vertx) {

        SchemaParser parser = SchemaParser.createDraft201909SchemaParser(
            SchemaRouter.create(vertx, new SchemaRouterOptions())
        );
        Schema schema = parser.parseFromString(
                  "{\n"
                + "  \"type\": \"object\",\n"
                + "  \"properties\": {\n"
                + "      \"usuarioId\"        : {\"type\": \"integer\"},\n"
                + "      \"sucursalOrigenId\" : {\"type\": \"integer\"},\n"
                + "      \"almacenOrigenId\"  : {\"type\": \"integer\"},\n"
                + "      \"sucursalDestinoId\": {\"type\": \"integer\"},\n"
                + "      \"almacenDestinoId\" : {\"type\": \"integer\"},\n"
                + "      \"observaciones\"    : {\"type\": \"string\" },\n"
                + "      \"fechaTraspaso\"    : {\"type\": \"string\" },\n"
                + "      \"gridDetalle\"      : {\n"
                + "          \"type\"       : \"array\",\n"
                + "          \"items\"      : {\n"
                + "              \"type\"      : \"object\",\n"
                + "              \"properties\": {\n"
                + "                  \"prodId\"   : {\"type\": \"integer\"},\n"
                + "                  \"cantidad\" : {\"type\": \"number\" },\n"
                + "                  \"presentId\": {\"type\": \"integer\"}\n"
                + "                },\n"
                + "              \"required\"  : [\"prodId\", \"cantidad\", \"presentId\"]\n"
                + "            },\n"
                + "          \"minItems\"   : 1,\n"
                + "          \"uniqueItems\": true\n"
                + "        }\n"
                + "    },\n"
                + "  \"required\": [\n"
                + "      \"usuarioId\",\n"
                + "      \"sucursalOrigenId\",\n"
                + "      \"almacenOrigenId\",\n"
                + "      \"sucursalDestinoId\",\n"
                + "      \"almacenDestinoId\",\n"
                + "      \"observaciones\",\n"
                + "      \"fechaTraspaso\",\n"
                + "      \"gridDetalle\"\n"
                + "    ]\n"
                + "}\n"
        );

        return route.handler(BodyHandler.create()).handler(routingContext -> {

            HttpServerResponse response = routingContext.response();

            {
                JsonObject payload = routingContext.getBodyAsJson();

                try {
                    schema.validateSync(payload);
                    // Successful validation
                    eb.<JsonObject>request(WAREHOUSES_TRASPASOS_NUEVO, payload, reply -> {
                        if (reply.succeeded()) {
                            JsonObject replyBody = reply.result().body();
                            response
                                    .putHeader("content-type", "application/json; charset=utf-8")
                                    .end(Json.encodePrettily(replyBody));
                        } else {
                            ReplyException ex = (ReplyException) reply.cause();
                            logger.warn("an error has occurred at the consumer {}", WAREHOUSES_TRASPASOS_NUEVO);
                            response.setStatusCode(ex.failureCode()).end();
                        }
                    });

                } catch (ValidationException e) {
                    // Failed validation
                    var jObj = new JsonObject("{\"msg\": \"Failed validation\"}");
                    response
                            .putHeader("content-type", "application/json; charset=utf-8")
                            .setStatusCode(400)
                            .end(Json.encodePrettily(jObj));

                } catch (NoSyncValidationException e) {
                    // Cannot validate synchronously. You must validate using validateAsync
                    schema.validateAsync(payload).onComplete(ar -> {
                        if (ar.succeeded()) {
                            // Validation succeeded

                        } else {
                            // Validation failed
                            ar.cause(); // Contains ValidationException
                        }
                    });
                }
            }
        });
    }

    public static void actOnWarehousesTraspasos(Message<JsonObject> message, Logger logger) {

        JsonObject body = message.body();

        try {
            ArrayList <Triplet<Integer, Double, Integer>> gridDetalle =
                    convertJsonTraspasoDetalleToArrayList(body.getJsonArray("gridDetalle"));

            String answer = PgsqlInteractions.getWarehousesTraspasoNuevo(
                    body.getInteger("usuarioId"),
                    body.getInteger("sucursalOrigenId"),
                    body.getInteger("almacenOrigenId"),
                    body.getInteger("sucursalDestinoId"),
                    body.getInteger("almacenDestinoId"),
                    body.getString("observaciones"),
                    body.getString("fechaTraspaso"),
                    gridDetalle,
                    logger
            );

            //Shaping the json object reply (AKA the jor)
            {
                var jor = new JsonObject();
                jor.put("valorRetorno", answer);
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

    private static ArrayList<Triplet<Integer, Double, Integer>> convertJsonTraspasoDetalleToArrayList(JsonArray detalle) {

        ArrayList<Triplet<Integer, Double, Integer>> al = new ArrayList<>();

        for (JsonObject i: (ArrayList<JsonObject>) detalle.getList()) {

            Triplet<Integer, Double, Integer> t = new Triplet<>(
                    i.getInteger("prodId"),
                    i.getDouble("cantidad"),
                    i.getInteger("presentId"));

            al.add(t);
        }
        return al;
    }
}
