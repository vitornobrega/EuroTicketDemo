(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('TicketDeleteController',TicketDeleteController);

    TicketDeleteController.$inject = ['$uibModalInstance', 'entity', 'Ticket'];

    function TicketDeleteController($uibModalInstance, entity, Ticket) {
        var vm = this;
        vm.ticket = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Ticket.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
