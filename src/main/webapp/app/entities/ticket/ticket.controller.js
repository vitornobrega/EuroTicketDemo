(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('TicketController', TicketController);

    TicketController.$inject = ['$scope', '$state', 'Ticket', 'TicketSearch'];

    function TicketController ($scope, $state, Ticket, TicketSearch) {
        var vm = this;
        vm.tickets = [];
        vm.loadAll = function() {
            Ticket.query(function(result) {
                vm.tickets = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            TicketSearch.query({query: vm.searchQuery}, function(result) {
                vm.tickets = result;
            });
        };
        vm.loadAll();
        
    }
})();
