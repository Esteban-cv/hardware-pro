// Se ejecuta cuando la página está completamente cargada
document.addEventListener('DOMContentLoaded', function() {
    // Solo inicializa el módulo si estamos en el formulario de NUEVA compra
    if (document.getElementById('purchaseForm')) {
        initializePurchaseModule();
    }
});

// Variable global para guardar los artículos de la compra
let purchaseCart = [];

// Función principal que arranca todo
function initializePurchaseModule() {
    setupArticleSearch();
    setupPurchaseFormSubmission();
    setupFormTriggers();
    updatePurchaseUI();
}

// ===============================================================
// SETUP DE EVENTOS
// ===============================================================

function setupFormTriggers() {
    document.getElementById('supplier')?.addEventListener('change', updateProcessButtonState);
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

function setupPurchaseFormSubmission() {
    document.getElementById('purchaseForm')?.addEventListener('submit', function(e) {
        e.preventDefault();

        if (purchaseCart.length === 0) {
            Swal.fire('Carrito Vacío', 'Debe agregar al menos un producto para registrar la compra.', 'warning');
            return;
        }
        if (!document.getElementById('supplier').value || !document.getElementById('employee').value) {
            Swal.fire('Faltan Datos', 'Debe seleccionar un proveedor y un empleado.', 'warning');
            return;
        }

        const hiddenContainer = document.getElementById('purchaseItemsHidden');
        hiddenContainer.innerHTML = '';
        purchaseCart.forEach(item => {
            hiddenContainer.innerHTML += `<input type="hidden" name="articleIds" value="${item.id}">`;
            hiddenContainer.innerHTML += `<input type="hidden" name="quantities" value="${item.quantity}">`;
            hiddenContainer.innerHTML += `<input type="hidden" name="unitPrices" value="${item.price}">`;
        });

        Swal.fire({ title: 'Procesando Compra...', allowOutsideClick: false, didOpen: () => { Swal.showLoading() } });
        this.submit();
    });
}

// ===============================================================
// MANEJO DEL "CARRITO" DE COMPRA
// ===============================================================

function addToPurchaseOrder(button) {
    const card = button.closest('.card[data-id]');
    const articleData = { id: parseInt(card.dataset.id), name: card.dataset.name, code: card.dataset.code, price: parseFloat(card.dataset.price), stock: parseInt(card.dataset.stock) };
    const existingItem = purchaseCart.find(item => item.id === articleData.id);

    if (existingItem) {
        existingItem.quantity++;
    } else {
        purchaseCart.push({ ...articleData, quantity: 1 });
    }
    Swal.fire({ toast: true, position: 'top-end', icon: 'success', title: `Añadido: ${articleData.name}`, showConfirmButton: false, timer: 1500 });
    updatePurchaseUI();
}

function updatePurchaseItemQuantity(articleId, newQuantity) {
    const item = purchaseCart.find(item => item.id === articleId);
    if (!item) return;
    newQuantity = parseInt(newQuantity);
    if (isNaN(newQuantity) || newQuantity <= 0) {
        purchaseCart = purchaseCart.filter(cartItem => cartItem.id !== articleId);
    } else {
        item.quantity = newQuantity;
    }
    updatePurchaseUI();
}

function removeFromPurchaseOrder(articleId) {
    purchaseCart = purchaseCart.filter(item => item.id !== articleId);
    updatePurchaseUI();
}

// ===============================================================
// ACTUALIZACIÓN DE LA INTERFAZ (UI)
// ===============================================================

function updatePurchaseUI() {
    updatePurchaseCartDisplay();
    updatePurchaseTotals();
    updateProcessButtonState();
}

function updatePurchaseCartDisplay() {
    const cartContainer = document.getElementById('purchaseCartItems');
    const emptyCartMessage = document.getElementById('emptyPurchaseCart');
    const cartCount = document.getElementById('purchaseCartCount');

    cartCount.textContent = purchaseCart.length;

    if (purchaseCart.length === 0) {
        emptyCartMessage.style.display = 'block';
        cartContainer.innerHTML = '';
    } else {
        emptyCartMessage.style.display = 'none';
        cartContainer.innerHTML = purchaseCart.map(item => `
            <div class="d-flex justify-content-between align-items-center mb-2 border-bottom pb-2">
                <div class="small">
                    <div class="fw-bold">${escapeHtml(item.name)}</div>
                    <div>${formatCurrency(item.price)} x ${item.quantity} = ${formatCurrency(item.price * item.quantity)}</div>
                </div>
                <div class="d-flex align-items-center">
                    <input type="number" class="form-control form-control-sm" value="${item.quantity}" onchange="updatePurchaseItemQuantity(${item.id}, this.value)" min="1" style="width: 70px;">
                    <button class="btn btn-outline-danger btn-sm ms-2" onclick="removeFromPurchaseOrder(${item.id})" type="button">
                        <i data-feather="trash-2" style="width:16px; height:16px;"></i>
                    </button>
                </div>
            </div>
        `).join('');
        feather.replace();
    }
}

function updatePurchaseTotals() {
    let subtotal = 0;
    purchaseCart.forEach(item => { subtotal += item.price * item.quantity; });
    const tax = subtotal * 0.19;
    const total = subtotal + tax;
    document.getElementById('purchaseSubtotal').textContent = formatCurrency(subtotal);
    document.getElementById('purchaseTax').textContent = formatCurrency(tax);
    document.getElementById('purchaseTotal').textContent = formatCurrency(total);
}

function updateProcessButtonState() {
    const processButton = document.getElementById('processPurchaseButton');
    if (!processButton) return;
    const supplierSelect = document.getElementById('supplier');
    const employeeSelect = document.getElementById('employee');
    const isReady = purchaseCart.length > 0 && supplierSelect.value && employeeSelect.value;
    processButton.disabled = !isReady;
}

// ===============================================================
// FUNCIONES AUXILIARES
// ===============================================================

function formatCurrency(amount) {
    return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', minimumFractionDigits: 0 }).format(amount);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}