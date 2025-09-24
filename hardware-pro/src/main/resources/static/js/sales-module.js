// ======================================
// VARIABLES GLOBALES
// ======================================
let cart = [];

// ======================================
// INICIALIZACIÓN
// ======================================
document.addEventListener('DOMContentLoaded', function() {
    // Solo inicializa el módulo si estamos en la página del formulario de ventas
    if (document.getElementById('saleForm')) {
        initializeSalesModule();
    }
});

// ======================================
// LÓGICA PRINCIPAL DEL MÓDULO DE VENTAS
// ======================================
function initializeSalesModule() {
    setupSearchFilter();
    setupFormSubmission();
    setupClientEmployeeValidation();
    updateCartDisplay();
    updateTotals();
}

// ======================================
// VALIDACIONES
// ======================================
function validateStock(productId, requestedQuantity) {
    const productCard = document.querySelector(`.card[data-id='${productId}']`);
    if (!productCard) return false;
    const availableStock = parseInt(productCard.dataset.stock) || 0;
    return requestedQuantity <= availableStock;
}

// ======================================
// GESTIÓN DEL CARRITO
// ======================================
function addToCart(button) {
    const productCard = button.closest('.card[data-id]');
    const productData = {
        id: parseInt(productCard.dataset.id),
        name: productCard.dataset.name,
        code: productCard.dataset.code,
        price: parseFloat(productCard.dataset.price),
        stock: parseInt(productCard.dataset.stock)
    };

    if (productData.stock <= 0) {
        Swal.fire({ toast: true, position: 'top-end', icon: 'error', title: 'Producto sin stock', showConfirmButton: false, timer: 2000 });
        return;
    }

    const existingItem = cart.find(item => item.id === productData.id);

    if (existingItem) {
        if (existingItem.quantity < productData.stock) {
            existingItem.quantity++;
            // No mostramos notificación para no ser muy intrusivos al añadir repetidamente
        } else {
            Swal.fire({ toast: true, position: 'top-end', icon: 'warning', title: 'Stock máximo alcanzado', showConfirmButton: false, timer: 2000 });
        }
    } else {
        cart.push({ ...productData, quantity: 1 });
        Swal.fire({
            toast: true,
            position: 'top-end',
            icon: 'success',
            title: `Agregado: ${productData.name}`,
            showConfirmButton: false,
            timer: 1500
        });
    }

    updateCartDisplay();
    updateTotals();
}

function updateQuantity(productId, newQuantity) {
    const item = cart.find(item => item.id === productId);
    if (!item) return;

    newQuantity = parseInt(newQuantity);
    if (isNaN(newQuantity) || newQuantity <= 0) {
        removeFromCart(productId);
        return;
    }

    if (!validateStock(productId, newQuantity)) {
        Swal.fire({ toast: true, position: 'top-end', icon: 'warning', title: 'Cantidad excede el stock', showConfirmButton: false, timer: 2500 });
        const quantityInput = document.querySelector(`input[data-product-id="${productId}"]`);
        if (quantityInput) quantityInput.value = item.quantity; // Restaura el valor
        return;
    }

    item.quantity = newQuantity;
    updateCartDisplay();
    updateTotals();
}

function removeFromCart(productId) {
    const itemIndex = cart.findIndex(item => item.id === productId);
    if (itemIndex > -1) {
        const itemName = cart[itemIndex].name;
        cart.splice(itemIndex, 1);
        updateCartDisplay();
        updateTotals();
        Swal.fire({
            toast: true,
            position: 'top-end',
            icon: 'info',
            title: `Removido: ${itemName}`,
            showConfirmButton: false,
            timer: 1500
        });
    }
}

function clearCart() {
    if (cart.length === 0) return;

    Swal.fire({
        title: '¿Limpiar el carrito?',
        text: "Se quitarán todos los productos de la venta actual.",
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, limpiar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            cart = [];
            updateCartDisplay();
            updateTotals();
            Swal.fire({ toast: true, position: 'top-end', icon: 'success', title: 'Carrito limpiado', showConfirmButton: false, timer: 2000 });
        }
    });
}

// ======================================
// ACTUALIZACIÓN DE LA UI (VISTA)
// ======================================
function updateCartDisplay() {
    const cartCount = document.getElementById('cartCount');
    const emptyCart = document.getElementById('emptyCart');
    const cartItems = document.getElementById('cartItems');

    if (cartCount) cartCount.textContent = cart.length;

    if (cart.length === 0) {
        if(emptyCart) emptyCart.style.display = 'block';
        if(cartItems) cartItems.innerHTML = '';
    } else {
        if(emptyCart) emptyCart.style.display = 'none';
        if(cartItems) {
            cartItems.innerHTML = cart.map(item => `
                <div class="d-flex justify-content-between align-items-center mb-2 border-bottom pb-2">
                    <div class="small">
                        <div class="fw-bold">${escapeHtml(item.name)}</div>
                        <div>${formatCurrency(item.price)} x ${item.quantity} = ${formatCurrency(item.price * item.quantity)}</div>
                    </div>
                    <div class="d-flex align-items-center">
                        <input type="number" class="form-control form-control-sm" value="${item.quantity}"
                               data-product-id="${item.id}"
                               onchange="updateQuantity(${item.id}, this.value)"
                               min="1" max="${item.stock}" style="width: 60px;">
                        <button class="btn btn-outline-danger btn-sm ms-2" onclick="removeFromCart(${item.id})" type="button">
                            <i data-feather="trash-2" style="width:16px; height:16px;"></i>
                        </button>
                    </div>
                </div>
            `).join('');
            feather.replace(); // Para que los nuevos iconos de Feather se rendericen
        }
    }
    updateProcessButton();
}

function updateTotals() {
    let subtotal = 0;
    cart.forEach(item => { subtotal += item.price * item.quantity; });
    const tax = subtotal * 0.19;
    const total = subtotal + tax;

    document.getElementById('subtotal').textContent = formatCurrency(subtotal);
    document.getElementById('tax').textContent = formatCurrency(tax);
    document.getElementById('total').textContent = formatCurrency(total);

    document.getElementById('subTotalInput').value = subtotal.toFixed(2);
    document.getElementById('taxInput').value = tax.toFixed(2);
    document.getElementById('totalInput').value = total.toFixed(2);

    updateProcessButton();
}

function updateProcessButton() {
    const processButton = document.getElementById('processButton');
    const clientSelect = document.getElementById('client');
    const employeeSelect = document.getElementById('employee');

    if (processButton) {
        const isReady = cart.length > 0 && clientSelect.value && employeeSelect.value;
        processButton.disabled = !isReady;
    }
}

// ======================================
// CONFIGURACIÓN DE EVENTOS
// ======================================
function setupClientEmployeeValidation() {
    document.getElementById('client')?.addEventListener('change', updateProcessButton);
    document.getElementById('employee')?.addEventListener('change', updateProcessButton);
}

function setupSearchFilter() {
    document.getElementById('productSearch')?.addEventListener('input', function(e) {
        const searchTerm = e.target.value.toLowerCase();
        document.querySelectorAll('.card[data-id]').forEach(card => {
            const name = card.dataset.name.toLowerCase();
            const code = card.dataset.code.toLowerCase();
            card.closest('.col').style.display = (name.includes(searchTerm) || code.includes(searchTerm)) ? '' : 'none';
        });
    });
}

function setupFormSubmission() {
    document.getElementById('saleForm')?.addEventListener('submit', function(e) {
        e.preventDefault();

        if (cart.length === 0) {
            Swal.fire('Carrito Vacío', 'Debe agregar al menos un producto para procesar la venta.', 'warning');
            return;
        }
        if (!document.getElementById('client').value) {
            Swal.fire('Falta Cliente', 'Debe seleccionar un cliente.', 'warning');
            return;
        }
        if (!document.getElementById('employee').value) {
            Swal.fire('Falta Vendedor', 'Debe seleccionar un vendedor.', 'warning');
            return;
        }

        generateCartInputs();

        Swal.fire({
            title: 'Procesando Venta',
            text: 'Por favor, espere...',
            allowOutsideClick: false,
            didOpen: () => { Swal.showLoading(); }
        });

        this.submit();
    });
}

// ======================================
// FUNCIONES AUXILIARES
// ======================================
function generateCartInputs() {
    const container = document.getElementById('cartItemsHidden');
    container.innerHTML = '';
    cart.forEach(item => {
        container.innerHTML += `<input type="hidden" name="articleIds" value="${item.id}">`;
        container.innerHTML += `<input type="hidden" name="quantities" value="${item.quantity}">`;
        container.innerHTML += `<input type="hidden" name="prices" value="${item.price}">`;
    });
}

function formatCurrency(amount) {
    return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', minimumFractionDigits: 0 }).format(amount);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}