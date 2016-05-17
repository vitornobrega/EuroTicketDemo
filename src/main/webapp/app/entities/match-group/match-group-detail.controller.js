(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('MatchGroupDetailController', MatchGroupDetailController);

    MatchGroupDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'MatchGroup'];

    function MatchGroupDetailController($scope, $rootScope, $stateParams, entity, MatchGroup) {
        var vm = this;
        vm.matchGroup = entity;
        
        var unsubscribe = $rootScope.$on('euroTicketDemoApp:matchGroupUpdate', function(event, result) {
            vm.matchGroup = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
