(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('ItemController', ItemController);

    ItemController.$inject = ['$scope', '$state', 'Item', 'ItemSearch'];

    function ItemController ($scope, $state, Item, ItemSearch) {
        var vm = this;
        vm.items = [];
        vm.loadAll = function() {
            Item.query(function(result) {
                vm.items = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ItemSearch.query({query: vm.searchQuery}, function(result) {
                vm.items = result;
            });
        };
        vm.loadAll();
        
    }
})();
