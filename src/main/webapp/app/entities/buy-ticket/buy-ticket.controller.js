(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('BuyTicketController', BuyTicketController);

    BuyTicketController.$inject = ['$scope', '$state','Ticket'];

    function BuyTicketController ($scope, $state,Ticket) {
        var vm = this;  
        vm.availableTickets = [];
        vm.loadAllAvailableTickets = function() {
            Ticket.query(function(result) {
                vm.availableTickets = result;
            });
        };
        vm.loadAllAvailableTickets();
    }

 
})();
