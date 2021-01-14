import sys
import grpc
from concurrent import futures

import sales_pb2
import sales_pb2_grpc

from dal import cotizacion
from dal import pedido_ventas

class Sales(sales_pb2_grpc.SalesServicer):
    
    def EditCot(self, request, context):
        print(request)
        
        valor_retorno = cotizacion.edit_cot(
            request.usuarioId,
            request.identificador,
            request.selectTipoCotizacion,
            request.idClienteOProspecto,
            request.checkDescripcionLarga,
            request.observaciones,
            request.tipoCambio,
            request.monedaId,
            request.fecha,
            request.agenteId,
            request.vigencia,
            request.incluyeIva,
            request.tcUSD,
            request.extraData
        )
        return sales_pb2.CotResponse(
            valorRetorno='{}'.format(valor_retorno)
        )


    def EditPedido(self, request, context):
        print(request)

        valor_retorno = pedido_ventas.edit_pedido(
            request.usuarioId,
            request.agenteId,
            request.clienteId,
            request.clienteDfId,
            request.almacenId,
            request.monedaId,
            request.provCrediasId,
            request.cfdiMetPagoId,
            request.formaPagoId,
            request.cfdiUsoId,
            request.pedidoId,
            request.tasaRetencionImmex,
            request.tipoCambio,
            request.porcentajeDescto,
            request.desctoAllowed,
            request.enviarObserFac,
            request.fleteEnabled,
            request.enviarRuta,
            request.observaciones,
            request.motivoDescto,
            request.transporte,
            request.fechaCompromiso,
            request.lugarEntrega,
            request.ordenCompra,
            request.numCuenta,
            request.folioCot,
            request.gridDetalle
        )
        return sales_pb2.PedidoResponse(
            valorRetorno='{}'.format(valor_retorno)
        )


    def CancelPedido(self, request, context):
        print(request)

        valor_retorno = pedido_ventas.cancel_pedido(
            request.pedidoId,
            request.usuarioId
        )
        return sales_pb2.PedidoCancelResponse(
            valorRetorno='{}'.format(valor_retorno)
        )


def _engage():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    sales_pb2_grpc.add_SalesServicer_to_server(Sales(), server)
    server.add_insecure_port('[::]:10090')
    server.start()
    server.wait_for_termination()


if __name__ == '__main__':

    try:
        _engage()
    except KeyboardInterrupt:
        print('Exiting')
    except:
        if True:
            print('Whoops! Problem in server:', file=sys.stderr)
            # traceback.print_exc(file=sys.stderr)
        sys.exit(1)

    # assuming everything went right, exit gracefully
    sys.exit(0)
