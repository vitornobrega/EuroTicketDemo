(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('SaleDeleteController',SaleDeleteController);

    SaleDeleteController.$inject = ['$uibModalInstance', 'entity', 'Sale'];

    function SaleDeleteController($uibModalInstance, entity, Sale) {
        var vm = this;
        vm.sale = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Sale.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
