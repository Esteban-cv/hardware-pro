$(document).ready(function() {
    const saleDetailsModal = document.getElementById('saleDetailsModal') ? new bootstrap.Modal(document.getElementById('saleDetailsModal')) : null;
    const articleDetailsModal = document.getElementById('articleDetailsModal') ? new bootstrap.Modal(document.getElementById('articleDetailsModal')) : null;
    const purchaseDetailsModal = document.getElementById('purchaseDetailsModal') ? new bootstrap.Modal(document.getElementById('purchaseDetailsModal')) : null;
    const entryDetailsModal = document.getElementById('entryDetailsModal') ? new bootstrap.Modal(document.getElementById('entryDetailsModal')) : null;
    const issueDetailsModal = document.getElementById('issueDetailsModal') ? new
    bootstrap.Modal(document.getElementById('issueDetailsModal')) : null;
    const clientDetailsModal = document.getElementById('clientDetailsModal') ? new bootstrap.Modal(document.getElementById('clientDetailsModal')) : null;

    $('body').on('click', '.btn-view', function() {
        const type = $(this).data('type');
        const id = $(this).data('id');
        let url = '';

        if (type === 'sale') {
            url = '/sales/view/' + id;
        } else if (type === 'article') {
            url = '/articles/view/' + id;
        } else if (type === 'purchase') {
            url = '/purchases/view/' + id;
        } else if (type === 'entry') {
            url = '/entries/view/' + id;
        } else if (type === 'issue') {
            url = '/issues/view/' + id;
        } else if (type === 'client') {
            url = '/clients/view/' + id;
        }

        if (!url) return;

        $.ajax({
            url: url,
            method: 'GET',
            success: function(data) {
                if (type === 'sale') {
                    populateSaleModal(data);
                } else if (type === 'article') {
                    populateArticleModal(data);
                } else if (type === 'purchase') {
                    populatePurchaseModal(data);
                } else if (type === 'entry') {
                    populateEntryModal(data);
                } else if (type === 'issue') {
                    populateIssueModal(data);
                } else if (type === 'client') {
                    populateClientModal(data);
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
    function populateSaleModal(data) {
        if (!saleDetailsModal) return;

        const formatCurrency = (num) => new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', minimumFractionDigits: 0 }).format(num);

        $('#modalSaleId').text(data.idSale);
        $('#modalClientName').text(data.clientName);
        $('#modalEmployeeName').text(data.employeeName);
        $('#modalDate').text(data.date);

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
    // FUNCIÓN PARA POBLAR EL MODAL DE ARTÍCULOS
    // ===============================================================
    function populateArticleModal(data) {
        if (!articleDetailsModal) return;

        const formatCurrency = (num) => new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', minimumFractionDigits: 0 }).format(num);

        // Llenar los campos del modal de artículo
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

    // ===============================================================
    // 4. FUNCIÓN NUEVA PARA POBLAR EL MODAL DE COMPRAS
    // ===============================================================
    function populatePurchaseModal(data) {
        if (!purchaseDetailsModal) return;

        const formatCurrency = (num) => new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', minimumFractionDigits: 0 }).format(num);

        $('#modalPurchaseId').text(data.idPurchase);
        $('#modalPurchaseDate').text(data.date);
        $('#modalPurchaseSupplier').text(data.supplierName);
        $('#modalPurchaseEmployee').text(data.employeeName);

        $('#modalPurchaseSubtotal').text(formatCurrency(data.subTotal));
        $('#modalPurchaseTax').text(formatCurrency(data.tax));
        $('#modalPurchaseTotal').text(formatCurrency(data.total));

        const detailsTbody = $('#modalPurchaseDetailsTbody');
        detailsTbody.empty(); // Limpiar la tabla antes de llenarla

        data.details.forEach(detail => {
            const row = `<tr>
                       <td>${detail.articleName}</td>
                       <td class="text-end">${detail.quantity}</td>
                       <td class="text-end">${formatCurrency(detail.unitPrice)}</td>
                       <td class="text-end">${formatCurrency(detail.total)}</td>
                     </tr>`;
            detailsTbody.append(row);
        });

        purchaseDetailsModal.show();
    }

    // ===============================================================
    // FUNCIÓN PARA POBLAR EL MODAL DE ENTRY
    // ===============================================================
    function populateEntryModal(data) {
        if (!entryDetailsModal) return;

        $('#modalEntryId').text(data.idEntry);
        $('#modalEntryDate').text(data.date);
        $('#modalEntryQuantity').text(data.quantity);
        $('#modalEntryObservations').text(data.observations);
        $('#modalEntryArticle').text(data.articleName);

        entryDetailsModal.show();
    }

    // ===============================================================
    // FUNCIÓN PARA POBLAR EL MODAL DE ISSUE
    // ===============================================================
    function populateIssueModal(data) {
        if (!issueDetailsModal) return;

        $('#modalIssueId').text(data.idIssue);
        $('#modalIssueDate').text(data.date);
        $('#modalIssueQuantity').text(data.quantity);
        $('#modalIssueObservations').text(data.observations);
        $('#modalIssueArticle').text(data.articleName);
        $('#modalIssueEmployee').text(data.employeeName);
        $('#modalIssueClient').text(data.clientName);

        issueDetailsModal.show();
    }

    // ===============================================================
    // FUNCIÓN PARA POBLAR EL MODAL DE CLIENT
    // ===============================================================
    function populateClientModal(data) {
        if (!clientDetailsModal) return;

        $('#modalClientId').text(data.idClient);
        $('#modalClientName').text(data.name);
        $('#modalClientDocument').text(data.document);
        $('#modalClientEmail').text(data.email || 'N/A');
        $('#modalClientAddress').text(data.address || 'N/A');
        $('#modalClientPhone').text(data.phone || 'N/A');
        $('#modalClientRut').text(data.rut || 'N/A');

        clientDetailsModal.show();
    }
});