import sys
import logging
import grpc
from concurrent import futures

import cobranza_pb2
import cobranza_pb2_grpc

from dal import cobranza


class Cobranza(cobranza_pb2_grpc.CobranzaServicer):

    def RegistrarPago(self, request, context):
        print(request)

        valor_retorno = cobranza.registrar_pago(
            request.usuarioId,
            request.clienteId,
            request.moneda,
            request.fechaHora,
            request.banco,
            request.observaciones,
            request.formaPago,
            request.cheque,
            request.referencia,
            request.tarjeta,
            request.montoPago,
            request.fechaDeposito,
            request.fichaMovimientoDeposito,
            request.fichaCuentaDeposito,
            request.fichaBancoKemikal,
            request.tipoCambio,
            request.anticipoGastado,
            request.noTransaccionAnticipo,
            request.saldoAFavor,
            request.gridDetalle
        )
        return cobranza_pb2.PagoResponse(
            valorRetorno='{}'.format(valor_retorno)
        )


def _engage():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    cobranza_pb2_grpc.add_CobranzaServicer_to_server(Cobranza(), server)
    server.add_insecure_port('[::]:10110')
    server.start()
    server.wait_for_termination()


if __name__ == '__main__':

    logging.basicConfig(filename='cobranza-grpc.log', level=logging.DEBUG)

    try:
        _engage()
    except KeyboardInterrupt:
        print('Exiting')
    except:
        if True:
            print('Whoops! Problem in server:', file=sys.stderr)
        sys.exit(1)

    # assuming everything went right, exit gracefully
    sys.exit(0)
