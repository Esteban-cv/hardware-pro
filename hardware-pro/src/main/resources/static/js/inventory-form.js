document.addEventListener('DOMContentLoaded', function() {
    const articleSelect = document.getElementById('article');
    const stockInput = document.getElementById('currentStock');

    if (articleSelect && stockInput) {

        articleSelect.addEventListener('change', function() {

            const selectedOption = this.options[this.selectedIndex];
            const stock = selectedOption.getAttribute('data-stock');
            if (stock) {
                stockInput.value = stock;
            } else {
                stockInput.value = '';
            }
        });
    }
});