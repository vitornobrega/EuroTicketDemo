(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('SaleStatusDetailController', SaleStatusDetailController);

    SaleStatusDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'SaleStatus'];

    function SaleStatusDetailController($scope, $rootScope, $stateParams, entity, SaleStatus) {
        var vm = this;
        vm.saleStatus = entity;
        
        var unsubscribe = $rootScope.$on('euroTicketDemoApp:saleStatusUpdate', function(event, result) {
            vm.saleStatus = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
