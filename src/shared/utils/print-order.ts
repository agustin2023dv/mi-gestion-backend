import type { Delivery } from '../../features/logistics/types';

export function printOrderReceipt(delivery: Delivery) {
  const { pedido } = delivery;
  const printWindow = window.open('', '_blank', 'width=400,height=600');
  if (!printWindow) return;

  const content = `
    <html>
      <head>
        <title>Comanda #${pedido.numeroPedido}</title>
        <style>
          body { font-family: 'Courier New', Courier, monospace; padding: 20px; line-height: 1.2; font-size: 14px; }
          .header { text-align: center; border-bottom: 1px dashed #000; padding-bottom: 10px; margin-bottom: 15px; }
          .order-info { margin-bottom: 15px; }
          .customer-info { margin-bottom: 15px; }
          .total { font-weight: bold; border-top: 1px dashed #000; padding-top: 10px; margin-top: 15px; font-size: 18px; text-align: right; }
          .footer { margin-top: 30px; text-align: center; font-size: 10px; }
          @media print { .no-print { display: none; } }
        </style>
      </head>
      <body>
        <div class="header">
          <h2 style="margin:0">COMANDA DE ENTREGA</h2>
          <p style="margin:5px 0">#{pedido.numeroPedido}</p>
          <p style="margin:0">${new Date().toLocaleString()}</p>
        </div>
        
        <div class="customer-info">
          <strong>CLIENTE:</strong><br/>
          ${pedido.cliente.nombre} ${pedido.cliente.apellido}<br/>
          ${pedido.cliente.telefono}
        </div>

        <div class="customer-info">
          <strong>DIRECCIÓN:</strong><br/>
          ${pedido.direccionEntrega.calle} ${pedido.direccionEntrega.numero}<br/>
          ${pedido.direccionEntrega.ciudad}, ${pedido.direccionEntrega.provincia}
        </div>

        <div class="order-info">
          <strong>MÉTODO DE PAGO:</strong><br/>
          ${pedido.metodoPago.toUpperCase()}
        </div>

        <div class="total">
          TOTAL: $${pedido.total}
        </div>

        <div class="footer">
          <p>Gracias por su preferencia</p>
        </div>

        <script>
          window.onload = () => {
            window.print();
            setTimeout(() => window.close(), 100);
          };
        </script>
      </body>
    </html>
  `;

  printWindow.document.write(content);
  printWindow.document.close();
}
