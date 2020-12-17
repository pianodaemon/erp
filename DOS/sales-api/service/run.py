import sys
import grpc
from concurrent import futures

import helloworld_pb2
import helloworld_pb2_grpc

from dal import cotizacion
from dal import pedido_cliente

class Greeter(helloworld_pb2_grpc.GreeterServicer):

    def SayHello(self, request, context):
        return helloworld_pb2.HelloReply(message='Hello, %s!' % request.name)
    
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
        return helloworld_pb2.CotResponse(
            valorRetorno='(python server) valorRetorno: {}'.format(valor_retorno)
        )

    def ValCustOrder(self, request, context):
        print(request)

        valor_retorno = pedido_cliente.val_cust_order(
            request.usrId,
            request.currVal,
            request.dateLim,
            request.payMet,
            request.account,
            request.matrix
        )
        return helloworld_pb2.ValCustOrderResponse(
            valorRetorno='(python server) valorRetorno: {}'.format(valor_retorno)
        )


def _engage():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    helloworld_pb2_grpc.add_GreeterServicer_to_server(Greeter(), server)
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
