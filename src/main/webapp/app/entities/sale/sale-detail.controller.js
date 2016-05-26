(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('SaleDetailController', SaleDetailController);

    SaleDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Sale', 'Item', 'SaleStatus', 'Payment', 'User'];

    function SaleDetailController($scope, $rootScope, $stateParams, entity, Sale, Item, SaleStatus, Payment, User) {
        var vm = this;
            var totalPrice = 0;
         
               entity.$promise.then(function(entity) {
               for(var y=0; y<entity.items.length; y++) {
                                totalPrice+=entity.items[y].quantity * entity.items[y].unitPrice;
                        }
                        entity.totalPrice = totalPrice;
        vm.sale = entity;
              });
                        
        
        var unsubscribe = $rootScope.$on('euroTicketDemoApp:saleUpdate', function(event, result) {
                    var totalPrice = 0;

                        for(var y=0; y<result.items.length; y++) {
                                totalPrice+=result.items[y].quantity * result[i].items[y].unitPrice;
                        }
                        result.totalPrice = totalPrice;
            vm.sale = result;

        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
