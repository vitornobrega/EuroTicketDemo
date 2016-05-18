(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('SaleStatusDeleteController',SaleStatusDeleteController);

    SaleStatusDeleteController.$inject = ['$uibModalInstance', 'entity', 'SaleStatus'];

    function SaleStatusDeleteController($uibModalInstance, entity, SaleStatus) {
        var vm = this;
        vm.saleStatus = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            SaleStatus.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
