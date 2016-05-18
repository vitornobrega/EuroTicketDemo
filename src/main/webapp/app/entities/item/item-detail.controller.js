(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('ItemDetailController', ItemDetailController);

    ItemDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Item', 'Ticket', 'Sale'];

    function ItemDetailController($scope, $rootScope, $stateParams, entity, Item, Ticket, Sale) {
        var vm = this;
        vm.item = entity;
        
        var unsubscribe = $rootScope.$on('euroTicketDemoApp:itemUpdate', function(event, result) {
            vm.item = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
