$(document).ready(function() {

    // Instancia los modals una sola vez al cargar la página para ser más eficiente
    const saleDetailsModal = document.getElementById('saleDetailsModal') ? new bootstrap.Modal(document.getElementById('saleDetailsModal')) : null;
    const articleDetailsModal = document.getElementById('articleDetailsModal') ? new bootstrap.Modal(document.getElementById('articleDetailsModal')) : null;

    // Selector genérico para CUALQUIER botón de ver en la página
    $('body').on('click', '.btn-view', function() {
        const type = $(this).data('type');
        const id = $(this).data('id');
        let url = '';

        // Decide qué URL usar basándose en el 'type' del botón
        if (type === 'sale') {
            url = '/sales/view/' + id;
        } else if (type === 'article') {
            url = '/articles/view/' + id;
        }
        // ... aquí puedes añadir 'else if' para futuros modals (clientes, etc.)

        if (!url) return;

        // Petición AJAX para obtener los datos
        $.ajax({
            url: url,
            method: 'GET',
            success: function(data) {
                // Llama a la función correcta para poblar el modal correspondiente
                if (type === 'sale') {
                    populateSaleModal(data);
                } else if (type === 'article') {
                    // CORRECCIÓN AQUÍ: Llamamos a la función para artículos
                    populateArticleModal(data);
                }
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

        $('#modalSaleId').text(data.idSale);
        $('#modalClientName').text(data.clientName);
        $('#modalEmployeeName').text(data.employeeName);
        $('#modalDate').text(data.date);

        // Suponiendo que tienes un span con id="modalObservations"
        const observations = data.observations || 'N/A';
        $('#modalObservations').text(observations);

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

        saleDetailsModal.show();
    }

    // ===============================================================
    // FUNCIÓN NUEVA PARA POBLAR EL MODAL DE ARTÍCULOS
    // ===============================================================
    function populateArticleModal(data) {
        if (!articleDetailsModal) return;

        const formatCurrency = (num) => new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', minimumFractionDigits: 0 }).format(num);

        // Llenar los campos del modal de artículo
        // Asegúrate de que los IDs coincidan con tu modal de artículos HTML
        $('#modalArticleId').text(data.idArticle);
        $('#modalArticleName').text(data.name);
        $('#modalArticleCode').text(data.code);
        $('#modalArticleCategory').text(data.categoryName);
        $('#modalArticleSupplier').text(data.supplierName);
        $('#modalArticleUnit').text(data.unitName);
        $('#modalArticlePrice').text(formatCurrency(data.price));
        $('#modalArticleStock').text(data.quantity + ' unidades');

        articleDetailsModal.show();
    }
});