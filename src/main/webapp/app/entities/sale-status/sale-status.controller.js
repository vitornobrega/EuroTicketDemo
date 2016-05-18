(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('SaleStatusController', SaleStatusController);

    SaleStatusController.$inject = ['$scope', '$state', 'SaleStatus', 'SaleStatusSearch'];

    function SaleStatusController ($scope, $state, SaleStatus, SaleStatusSearch) {
        var vm = this;
        vm.saleStatuses = [];
        vm.loadAll = function() {
            SaleStatus.query(function(result) {
                vm.saleStatuses = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            SaleStatusSearch.query({query: vm.searchQuery}, function(result) {
                vm.saleStatuses = result;
            });
        };
        vm.loadAll();
        
    }
})();
