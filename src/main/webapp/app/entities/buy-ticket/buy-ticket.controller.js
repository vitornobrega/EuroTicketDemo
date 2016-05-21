(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('BuyTicketController', BuyTicketController);

    BuyTicketController.$inject = ['$scope', '$state','Ticket'];

    function BuyTicketController ($scope, $state,Ticket) {
        var vm = this;  
        vm.availableTickets = [];
        vm.addedTickets = [];
        vm.cardDetails = {};
        vm.processStep = 'tickets';  
        //vm.processStep = 'payment';    
        vm.totalPrice = 0;
        vm.loadAllAvailableTickets = function() {
            Ticket.query(function(result) {
                vm.availableTickets = result;
            });
        };
        vm.loadAllAvailableTickets();

        vm.addTicketToShoppingCart = function (ticket) {
            var indexToRemove = vm.availableTickets.indexOf(ticket);
            if(indexToRemove >= 0){
                ticket.quantity = 1;
                ticket.availableQtt = ticket.availableQtt -1;
                vm.addedTickets.push(ticket);
                vm.totalPrice += ticket.quantity * ticket.unitPrice; 
                vm.availableTickets.splice(indexToRemove, 1); 
            }
        };

        vm.deleteTicketFromShoppingCart = function (ticket) {
            var indexToRemove = vm.addedTickets.indexOf(ticket);
            if(indexToRemove >= 0){
                ticket.availableQtt = ticket.availableQtt +1;
                vm.availableTickets.push(ticket);
                vm.addedTickets.splice(indexToRemove, 1); 
                vm.totalPrice -= ticket.quantity * ticket.unitPrice; 
            }
        };

        vm.onTicketQttChange = function (ticket) {
           // calculate total price
           var totalPrice = 0;
           for(var i=0; i <vm.addedTickets.length; i++) {
                totalPrice += vm.addedTickets[i].quantity * vm.addedTickets[i].unitPrice; 
           }
           vm.totalPrice = totalPrice;
        };

        vm.goToPayment = function () {
            vm.processStep = 'payment';    
        };

        vm.backPreviousStep = function () {
            vm.processStep = 'tickets';    
        };

        vm.finishPayment = function(){

        }
    }

 
})();
