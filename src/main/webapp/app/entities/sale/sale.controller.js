(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('SaleController', SaleController);

    SaleController.$inject = ['$scope', '$state', 'Sale', 'SaleSearch'];

    function SaleController ($scope, $state, Sale, SaleSearch) {
        var vm = this;
        vm.sales = [];
        vm.loadAll = function() {
            Sale.query(function(result) {
                for(var i=0; i<result.length; i++) {
                    var ticketsNumber= 0;
                    var totalPrice = 0;
                        for(var y=0; y<result[i].items.length; y++) {
                                ticketsNumber+=result[i].items[y].quantity;
                                totalPrice+=result[i].items[y].quantity * result[i].items[y].unitPrice;
                        }
                        result[i].ticketsNumber = ticketsNumber;
                        result[i].totalPrice = totalPrice;
                }
                vm.sales = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            SaleSearch.query({query: vm.searchQuery}, function(result) {
                vm.sales = result;
            });
        };
        vm.loadAll();
        
    }
})();
