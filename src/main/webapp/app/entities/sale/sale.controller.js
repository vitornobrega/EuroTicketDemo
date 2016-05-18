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
