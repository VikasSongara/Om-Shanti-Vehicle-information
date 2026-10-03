document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.delete-form').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            var message = form.getAttribute('data-confirm')
                || 'Are you sure you want to delete this vehicle?';
            if (!confirm(message)) {
                event.preventDefault();
            }
        });
    });

    var modeReplace = document.getElementById('modeReplace');
    var modeAppend = document.getElementById('modeAppend');
    var confirmGroup = document.getElementById('confirmReplaceGroup');
    var confirmCheckbox = document.getElementById('confirmReplace');
    var importForm = document.getElementById('importForm');

    function toggleConfirmGroup() {
        if (!confirmGroup || !modeReplace) {
            return;
        }
        if (modeReplace.checked) {
            confirmGroup.style.display = 'block';
        } else {
            confirmGroup.style.display = 'none';
            if (confirmCheckbox) {
                confirmCheckbox.checked = false;
            }
        }
    }

    if (modeReplace) {
        modeReplace.addEventListener('change', toggleConfirmGroup);
    }
    if (modeAppend) {
        modeAppend.addEventListener('change', toggleConfirmGroup);
    }
    toggleConfirmGroup();

    if (importForm && modeReplace && confirmCheckbox) {
        importForm.addEventListener('submit', function (event) {
            if (modeReplace.checked && !confirmCheckbox.checked) {
                event.preventDefault();
                alert('Please confirm that you want to replace all existing data.');
            }
        });
    }

    // Excel menu: closed by default; click Excel to expand/collapse
    var excelItem = document.querySelector('.nav-item-excel');
    var excelSubmenu = document.getElementById('excelSubmenu');
    var excelToggle = document.getElementById('excelNav');
    var mainNav = document.getElementById('mainNav');

    function setExcelOpen(open) {
        if (!excelItem || !excelSubmenu || !excelToggle) {
            return;
        }
        excelItem.classList.toggle('is-open', open);
        excelSubmenu.hidden = !open;
        excelToggle.setAttribute('aria-expanded', open ? 'true' : 'false');
    }

    if (excelToggle && excelSubmenu) {
        setExcelOpen(false);
        excelToggle.addEventListener('click', function (event) {
            event.preventDefault();
            event.stopPropagation();
            setExcelOpen(excelSubmenu.hidden);
        });
    }

    if (mainNav) {
        mainNav.addEventListener('hide.bs.collapse', function () {
            setExcelOpen(false);
        });
    }

    document.addEventListener('click', function (event) {
        if (!excelItem || !excelSubmenu || excelSubmenu.hidden) {
            return;
        }
        if (!excelItem.contains(event.target)) {
            setExcelOpen(false);
        }
    });
});
