$(document).ready(function() {

    // Instancia los modals una sola vez al cargar la página
    const saleDetailsModal = document.getElementById('saleDetailsModal') ? new bootstrap.Modal(document.getElementById('saleDetailsModal')) : null;
    // const clientDetailsModal = new bootstrap.Modal... (para futuros modals)

    // Selector genérico para CUALQUIER botón de ver
    $('body').on('click', '.btn-view', function() {
        const type = $(this).data('type');
        const id = $(this).data('id');
        let url = '';

        // Decide qué URL usar
        if (type === 'sale') url = '/sales/view/' + id;
        // else if (type === 'client') url = '/clients/view/' + id;

        if (!url) return;

        // Petición AJAX
        $.ajax({
            url: url,
            method: 'GET',
            success: function(data) {
                // Llama a la función correcta para poblar el modal
                if (type === 'sale') populateSaleModal(data);
                // else if (type === 'client') populateClientModal(data);
            },
            error: function() {
                Swal.fire('Error', 'No se pudieron cargar los detalles.', 'error');
            }
        });
    });

    // ===============================================================
    // FUNCIONES ESPECÍFICAS PARA CADA TIPO DE MODAL
    // ===============================================================

    // Función para poblar el modal de Ventas
    function populateSaleModal(data) {
        if (!saleDetailsModal) return;

        const formatCurrency = (num) => new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', minimumFractionDigits: 0 }).format(num);

        // Llenar datos
        $('#modalSaleId').text(data.idSale);
        $('#modalClientName').text(data.clientName);
        $('#modalEmployeeName').text(data.employeeName);
        $('#modalDate').text(data.date);

        $('#modalSubTotal').text(formatCurrency(data.subTotal));
        $('#modalTax').text(formatCurrency(data.tax));
        $('#modalTotal').text(formatCurrency(data.total));

        const detailsTbody = $('#modalDetailsTbody');
        detailsTbody.empty();
        data.details.forEach(detail => {
            const row = `<tr>
                           <td>${detail.articleName}</td>
                           <td class="text-end">${detail.quantity}</td>
                           <td class="text-end">${formatCurrency(detail.unitPrice)}</td>
                           <td class="text-end">${formatCurrency(detail.total)}</td>
                         </tr>`;
            detailsTbody.append(row);
        });

        // Mostrar el modal
        saleDetailsModal.show();
    }

    // Función para poblar el modal de Clientes (EJEMPLO)
    function populateClientModal(data) {
        var clientDetailsModal = new bootstrap.Modal(document.getElementById('clientDetailsModal'));

        // Llenarías aquí los campos del modal de cliente
        // Ejemplo: $('#modalClientDocument').text(data.document);
        // Ejemplo: $('#modalClientEmail').text(data.email);

        clientDetailsModal.show();
    }

    // Función para poblar el modal de Productos (EJEMPLO)
    function populateProductModal(data) {
        // Lógica para el modal de productos...
    }
});