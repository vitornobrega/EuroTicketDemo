(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('PaymentDetailController', PaymentDetailController);

    PaymentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Payment'];

    function PaymentDetailController($scope, $rootScope, $stateParams, entity, Payment) {
        var vm = this;
        vm.payment = entity;
        
        var unsubscribe = $rootScope.$on('euroTicketDemoApp:paymentUpdate', function(event, result) {
            vm.payment = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
