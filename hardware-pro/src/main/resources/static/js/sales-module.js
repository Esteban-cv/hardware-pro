document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('saleForm')) {
        initializeSaleModule();
    }
});

let saleCart = [];

function initializeSaleModule() {
    setupArticleSearch();
    setupSaleFormSubmission();
    setupFormTriggers();
    updateSaleUI();
}

function setupFormTriggers() {
    document.getElementById('client')?.addEventListener('change', updateProcessButtonState);
    document.getElementById('employee')?.addEventListener('change', updateProcessButtonState);
}

function setupArticleSearch() {
    document.getElementById('articleSearch')?.addEventListener('input', function(e) {
        const searchTerm = e.target.value.toLowerCase();
        document.querySelectorAll('#articlesGrid .col').forEach(col => {
            const card = col.querySelector('.card');
            const name = card.dataset.name.toLowerCase();
            const code = card.dataset.code.toLowerCase();
            col.style.display = (name.includes(searchTerm) || code.includes(searchTerm)) ? '' : 'none';
        });
    });
}

function setupSaleFormSubmission() {
    document.getElementById('saleForm')?.addEventListener('submit', function(e) {
        e.preventDefault();

        if (saleCart.length === 0 || !document.getElementById('client').value || !document.getElementById('employee').value) {
            Swal.fire('Faltan Datos', 'Asegúrese de seleccionar un cliente, un vendedor y al menos un artículo.', 'warning');
            return;
        }

        const hiddenContainer = document.getElementById('saleItemsHidden');
        hiddenContainer.innerHTML = '';
        saleCart.forEach(item => {
            hiddenContainer.innerHTML += `<input type="hidden" name="articleIds" value="${item.id}">`;
            hiddenContainer.innerHTML += `<input type="hidden" name="quantities" value="${item.quantity}">`;
            hiddenContainer.innerHTML += `<input type="hidden" name="prices" value="${item.price}">`;
        });

        Swal.fire({ title: 'Procesando Venta...', allowOutsideClick: false, didOpen: () => { Swal.showLoading() } });
        this.submit();
    });
}

function addToSaleCart(button) {
    const card = button.closest('.card[data-id]');
    const articleData = { id: parseInt(card.dataset.id), name: card.dataset.name, code: card.dataset.code, price: parseFloat(card.dataset.price), stock: parseInt(card.dataset.stock) };

    if (articleData.stock <= 0) {
        Swal.fire({ toast: true, position: 'top-end', icon: 'error', title: 'Producto sin stock', showConfirmButton: false, timer: 2000 });
        return;
    }

    const existingItem = saleCart.find(item => item.id === articleData.id);

    if (existingItem) {
        if (existingItem.quantity < articleData.stock) {
            existingItem.quantity++;
        } else {
            Swal.fire({ toast: true, position: 'top-end', icon: 'warning', title: 'Stock máximo alcanzado', showConfirmButton: false, timer: 2000 });
            return;
        }
    } else {
        saleCart.push({ ...articleData, quantity: 1 });
    }

    Swal.fire({ toast: true, position: 'top-end', icon: 'success', title: `Añadido: ${articleData.name}`, showConfirmButton: false, timer: 1500 });
    updateSaleUI();
}

function updateSaleItemQuantity(articleId, newQuantity) {
    const item = saleCart.find(item => item.id === articleId);
    if (!item) return;
    newQuantity = parseInt(newQuantity);

    if (isNaN(newQuantity) || newQuantity <= 0) {
        saleCart = saleCart.filter(cartItem => cartItem.id !== articleId);
    } else {
        if (newQuantity > item.stock) {
            Swal.fire({ toast: true, position: 'top-end', icon: 'warning', title: `Stock máximo: ${item.stock}`, showConfirmButton: false, timer: 2500 });
        } else {
            item.quantity = newQuantity;
        }
    }
    updateSaleUI();
}

function removeFromSaleCart(articleId) {
    saleCart = saleCart.filter(item => item.id !== articleId);
    updateSaleUI();
}

function updateSaleUI() {
    updateSaleCartDisplay();
    updateSaleTotals();
    updateProcessButtonState();
}

function updateSaleCartDisplay() {
    const cartContainer = document.getElementById('saleCartItems');
    const emptyCartMessage = document.getElementById('emptySaleCart');
    const cartCount = document.getElementById('saleCartCount');

    cartCount.textContent = saleCart.length;

    if (saleCart.length === 0) {
        emptyCartMessage.style.display = 'block';
        cartContainer.innerHTML = '';
    } else {
        emptyCartMessage.style.display = 'none';
        cartContainer.innerHTML = saleCart.map(item => `
            <div class="d-flex justify-content-between align-items-center mb-2 border-bottom pb-2">
                <div class="small">
                    <div class="fw-bold">${escapeHtml(item.name)}</div>
                    <div>${formatCurrency(item.price)} x ${item.quantity} = ${formatCurrency(item.price * item.quantity)}</div>
                </div>
                <div class="d-flex align-items-center">
                    <input type="number" class="form-control form-control-sm" value="${item.quantity}" onchange="updateSaleItemQuantity(${item.id}, this.value)" min="1" max="${item.stock}" style="width: 70px;">
                    <button class="btn btn-outline-danger btn-sm ms-2" onclick="removeFromSaleCart(${item.id})" type="button">
                        <i data-feather="trash-2" style="width:16px; height:16px;"></i>
                    </button>
                </div>
            </div>
        `).join('');
        feather.replace();
    }
}

function updateSaleTotals() {
    let subtotal = 0;
    saleCart.forEach(item => { subtotal += item.price * item.quantity; });

    const vatRateString = $('#saleForm').data('vat-rate') || "19";
    const vatRate = parseFloat(vatRateString) / 100;

    const tax = subtotal * vatRate;
    const total = subtotal + tax;
    document.getElementById('saleSubtotal').textContent = formatCurrency(subtotal);
    document.getElementById('saleTax').textContent = formatCurrency(tax);
    document.getElementById('saleTotal').textContent = formatCurrency(total);
}

function updateProcessButtonState() {
    const processButton = document.getElementById('processSaleButton');
    if (!processButton) return;
    const clientSelect = document.getElementById('client');
    const employeeSelect = document.getElementById('employee');
    const isReady = saleCart.length > 0 && clientSelect.value && employeeSelect.value;
    processButton.disabled = !isReady;
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', minimumFractionDigits: 0 }).format(amount);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}