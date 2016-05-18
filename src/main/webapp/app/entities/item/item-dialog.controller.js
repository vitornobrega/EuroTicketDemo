(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('ItemDialogController', ItemDialogController);

    ItemDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Item', 'Ticket', 'Sale'];

    function ItemDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Item, Ticket, Sale) {
        var vm = this;
        vm.item = entity;
        vm.tickets = Ticket.query({filter: 'item-is-null'});
        $q.all([vm.item.$promise, vm.tickets.$promise]).then(function() {
            if (!vm.item.ticketId) {
                return $q.reject();
            }
            return Ticket.get({id : vm.item.ticketId}).$promise;
        }).then(function(ticket) {
            vm.tickets.push(ticket);
        });
        vm.sales = Sale.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('euroTicketDemoApp:itemUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.item.id !== null) {
                Item.update(vm.item, onSaveSuccess, onSaveError);
            } else {
                Item.save(vm.item, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
