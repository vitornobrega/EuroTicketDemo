(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('SaleDetailController', SaleDetailController);

    SaleDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Sale', 'Item', 'SaleStatus', 'Payment', 'User'];

    function SaleDetailController($scope, $rootScope, $stateParams, entity, Sale, Item, SaleStatus, Payment, User) {
        var vm = this;
        vm.sale = entity;
        
        var unsubscribe = $rootScope.$on('euroTicketDemoApp:saleUpdate', function(event, result) {
            vm.sale = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
