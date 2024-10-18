const accountDropdown = {
    myAccountBtn: null,
    dropdownEl: null,
    initialize() {
        accountDropdown.myAccountBtn = document.getElementById('myAccountBtn');
        accountDropdown.dropdownEl = document.getElementById('myAccountDropdown');
        accountDropdown.addEvents();
    },
    addEvents() {
        window.addEventListener('click', accountDropdown.onWindowClick);
        accountDropdown.myAccountBtn.addEventListener('click', accountDropdown.toggleDropdown);
    },
    onWindowClick(e) {
        if (!e.target.matches('.myAccountBtn')) {
            if (accountDropdown.dropdownEl.classList.contains('show')) {
                accountDropdown.dropdownEl.classList.remove('show');
            }
        }
    },
    toggleDropdown() {
        accountDropdown.dropdownEl.classList.toggle('show');
    }
}

document.addEventListener('DOMContentLoaded', function(){
    accountDropdown.initialize();
});
