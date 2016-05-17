(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('PhaseDetailController', PhaseDetailController);

    PhaseDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Phase'];

    function PhaseDetailController($scope, $rootScope, $stateParams, entity, Phase) {
        var vm = this;
        vm.phase = entity;
        
        var unsubscribe = $rootScope.$on('euroTicketDemoApp:phaseUpdate', function(event, result) {
            vm.phase = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
