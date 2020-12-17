/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.maxima.sales.cli.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class Process {
  private static final Logger logger = Logger.getLogger(Process.class.getName());

  private final GreeterGrpc.GreeterBlockingStub blockingStub;

  /** Construct client for accessing HelloWorld server using the existing channel. */
  public Process(Channel channel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
    blockingStub = GreeterGrpc.newBlockingStub(channel);
  }

  /** Say hello to server. */
  public void greet(String name) {
    logger.info("Will try to greet " + name + " ...");
    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
    HelloReply response;
    try {
      response = blockingStub.sayHello(request);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("Greeting: " + response.getMessage());

    CotRequest cotRequest =
      CotRequest.newBuilder()
        .setUsuarioId(2)
        .setIdentificador(0)
        .setSelectTipoCotizacion(1)
        .setIdClienteOProspecto(156)
        .setCheckDescripcionLarga(true)
        .setObservaciones("mi observacion")
        .setTipoCambio(20.0015)
        .setMonedaId(3)
        .setFecha("2020-12-10")
        .setAgenteId(22)
        .setVigencia(44)
        .setIncluyeIva(false)
        .setTcUSD(21.9908)
        .addExtraData(
          CotRequest.GridRenglonCot.newBuilder()
            .setRemovido(1)
            .setIdDetalle(0)
            .setIdProducto(45)
            .setIdPresentacion(9)
            .setCantidad(15.11)
            .setPrecio(1588.12)
            .setMonedaGrId(2)
            .setNotr("mi notr, sabe que es eso")
            .setIdImpProd(33)
            .setValorImp(160.00)
            .setUnidadId(3)
            .setStatusAutorizacion(false)
            .setPrecioAutorizado(1500.25)
            .setIdUserAut(16)
            .setRequiereAutorizacion(true)
            .setSalvarRegistro("salvado el record"))
        .addExtraData(
          CotRequest.GridRenglonCot.newBuilder()
            .setRemovido(1)
            .setIdDetalle(0)
            .setIdProducto(47)
            .setIdPresentacion(11)
            .setCantidad(62.5)
            .setPrecio(884.33)
            .setMonedaGrId(1)
            .setNotr("mi notr2, sabe que es eso2")
            .setIdImpProd(30)
            .setValorImp(270.00)
            .setUnidadId(3)
            .setStatusAutorizacion(true)
            .setPrecioAutorizado(520.25)
            .setIdUserAut(15)
            .setRequiereAutorizacion(false)
            .setSalvarRegistro("2.salvado el 2record"))
        .build();
    CotResponse cotResponse;
    
    try {
      cotResponse = blockingStub.editCot(cotRequest);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("(java client) Cot Response valorRetorno: " + cotResponse.getValorRetorno());

    ValCustOrderRequest valCustOrderRequest =
      ValCustOrderRequest.newBuilder()
        .setUsrId(2)
        .setCurrVal("20.1411")
        .setDateLim("2020-12-15")
        .setPayMet(2)
        .setAccount("8147")
        .addMatrix("1___1___25___2___15.44___notr1___0___3")
        .addMatrix("1___2___5___2___1.00___notr2___0___2")
        .build();
    ValCustOrderResponse valCustOrderResponse;

    try {
      valCustOrderResponse = blockingStub.valCustOrder(valCustOrderRequest);
    } catch (StatusRuntimeException e) {
      logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
      return;
    }
    logger.info("(java client) ValCustOrder Response valorRetorno: " + valCustOrderResponse.getValorRetorno());
  }

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting. The second argument is the target server.
   */
  public static void main(String[] args) throws Exception {
    String user = "world";
    // Access a service running on the local machine
    String target = "localhost:10090";
    // Allow passing in the user and target strings as command line arguments
    if (args.length > 0) {
      if ("--help".equals(args[0])) {
        System.err.println("Usage: [name [target]]");
        System.err.println("");
        System.err.println("  name    The name you wish to be greeted by. Defaults to " + user);
        System.err.println("  target  The server to connect to. Defaults to " + target);
        System.exit(1);
      }
      user = args[0];
    }
    if (args.length > 1) {
      target = args[1];
    }

    // Create a communication channel to the server, known as a Channel. Channels are thread-safe
    // and reusable. It is common to create channels at the beginning of your application and reuse
    // them until the application shuts down.
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build();
    try {
      Process client = new Process(channel);
      client.greet(user);
    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
      // resources the channel should be shut down when it will no longer be used. If it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
