(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('SaleDialogController', SaleDialogController);

    SaleDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Sale', 'Item', 'SaleStatus', 'Payment', 'User'];

    function SaleDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Sale, Item, SaleStatus, Payment, User) {
        var vm = this;
        vm.sale = entity;
        vm.items = Item.query();
        vm.salestatuss = SaleStatus.query({filter: 'sale-is-null'});
        $q.all([vm.sale.$promise, vm.salestatuss.$promise]).then(function() {
            if (!vm.sale.saleStatusId) {
                return $q.reject();
            }
            return SaleStatus.get({id : vm.sale.saleStatusId}).$promise;
        }).then(function(saleStatus) {
            vm.salestatuses.push(saleStatus);
        });
        vm.payments = Payment.query({filter: 'sale-is-null'});
        $q.all([vm.sale.$promise, vm.payments.$promise]).then(function() {
            if (!vm.sale.paymentId) {
                return $q.reject();
            }
            return Payment.get({id : vm.sale.paymentId}).$promise;
        }).then(function(payment) {
            vm.payments.push(payment);
        });
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('euroTicketDemoApp:saleUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.sale.id !== null) {
                Sale.update(vm.sale, onSaveSuccess, onSaveError);
            } else {
                Sale.save(vm.sale, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.saleDate = false;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
