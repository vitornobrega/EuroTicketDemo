(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('BuyTicketController', BuyTicketController);

    BuyTicketController.$inject = ['$scope', '$state','Ticket','Sale','Principal','Notification','$translate'];

    function BuyTicketController ($scope, $state,Ticket,Sale,Principal,Notification,$translate) {
        var vm = this;  
        vm.availableTickets = [];
        vm.addedTickets = [];
        vm.sale = {};
        vm.canBuy = true;
        vm.sale.payment = {};
        vm.userAccount = null;
        vm.cardDetails = {};
        vm.processStep = 'tickets';  
        //vm.processStep = 'payment';    
        vm.totalPrice = 0;
        vm.loadAllAvailableTickets = function() {
            Ticket.query(function(result) {
                vm.availableTickets = result;
            });
        };
        Principal.identity(true).then(function(account) {
            vm.userAccount = account;
            if(vm.userAccount.purchasedTickets != null) {
                if(vm.userAccount.purchasedTickets < 5){
                    var notificationMessage;
                    if(vm.userAccount.purchasedTickets== 0)
                        notificationMessage = 'euroTicketDemoApp.buyTicket.purchasedTickets0';
                    else if(vm.userAccount.purchasedTickets== 1)
                        notificationMessage = 'euroTicketDemoApp.buyTicket.purchasedTickets1';
                    else if(vm.userAccount.purchasedTickets== 2)
                        notificationMessage = 'euroTicketDemoApp.buyTicket.purchasedTickets2';
                    else if(vm.userAccount.purchasedTickets== 3)
                        notificationMessage = 'euroTicketDemoApp.buyTicket.purchasedTickets3';
                    else if(vm.userAccount.purchasedTickets== 4)
                        notificationMessage = 'euroTicketDemoApp.buyTicket.purchasedTickets4';

                    Notification.primary({positionY : 'bottom', message: $translate.instant(notificationMessage)});
                } else if(vm.userAccount.purchasedTickets == 5){
                    vm.canBuy = false;
                    Notification.error({positionY : 'bottom', message: $translate.instant('euroTicketDemoApp.buyTicket.purchasedTickets5')});
                }

            }
        });
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
           var totalQtt = 0;
           for(var i=0; i <vm.addedTickets.length; i++) {
                totalQtt+=vm.addedTickets[i].quantity;
                totalPrice += vm.addedTickets[i].quantity * vm.addedTickets[i].unitPrice; 
           }
           vm.totalPrice = totalPrice;

           if(totalQtt + vm.userAccount.purchasedTickets > 5) {
                  vm.canBuy = false;
                  var remainingTickets = 5 - vm.userAccount.purchasedTickets;
                  var remainingMsg = null;
                  if(remainingTickets == 5) {
                        remainingMsg = 'euroTicketDemoApp.buyTicket.remainingTickets5';
                  } else if(remainingTickets == 4){
                        remainingMsg = 'euroTicketDemoApp.buyTicket.remainingTickets4';
                  } else if(remainingTickets == 3){
                        remainingMsg = 'euroTicketDemoApp.buyTicket.remainingTickets3';
                  } else if(remainingTickets == 2){
                        remainingMsg = 'euroTicketDemoApp.buyTicket.remainingTickets2';
                  } else if(remainingTickets == 1){
                        remainingMsg = 'euroTicketDemoApp.buyTicket.remainingTickets1';
                  }
                  Notification.error({
                        positionY : 'bottom', 
                        message: $translate.instant(remainingMsg)
                    });
           } else {
                vm.canBuy = true;
           }
        };

        vm.goToPayment = function () {
            vm.processStep = 'payment';    
        };

        vm.backPreviousStep = function () {
            vm.processStep = 'tickets';    
        };

        vm.finishPayment = function(){
            var saleItems = [];
             for(var i=0; i <vm.addedTickets.length; i++) {
                var item  = {},
                    ticket = vm.addedTickets[i];
                item.quantity = ticket.quantity;
                item.ticketId = ticket.id;
                saleItems.push(item);
           }
            vm.sale.items = saleItems;
            vm.sale.userId = 1;
            //BuyTicket.createBuyTicket(saleDTO);
            Sale.save(vm.sale, onSaveSuccess, onSaveError);
        };

        var onSaveSuccess = function (result) {
          //  $scope.$emit('euroTicketDemoApp:saleUpdate', result);
           // $uibModalInstance.close(result);
            vm.isSaving = false;
            Notification.success({positionY : 'bottom', message: $translate.instant('euroTicketDemoApp.buyTicket.success')});
            $state.go('sale');
        };

        var onSaveError = function () {
            vm.isSaving = false;
             Notification.error({positionY : 'bottom', message: $translate.instant('euroTicketDemoApp.buyTicket.failure')});
        };
    }

 
})();
