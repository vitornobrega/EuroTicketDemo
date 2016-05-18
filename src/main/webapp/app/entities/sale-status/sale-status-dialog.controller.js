(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('SaleStatusDialogController', SaleStatusDialogController);

    SaleStatusDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'SaleStatus'];

    function SaleStatusDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, SaleStatus) {
        var vm = this;
        vm.saleStatus = entity;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('euroTicketDemoApp:saleStatusUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.saleStatus.id !== null) {
                SaleStatus.update(vm.saleStatus, onSaveSuccess, onSaveError);
            } else {
                SaleStatus.save(vm.saleStatus, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
