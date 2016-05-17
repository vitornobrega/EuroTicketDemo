(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('TicketDetailController', TicketDetailController);

    TicketDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Ticket', 'Team', 'MatchGroup', 'Phase'];

    function TicketDetailController($scope, $rootScope, $stateParams, entity, Ticket, Team, MatchGroup, Phase) {
        var vm = this;
        vm.ticket = entity;
        
        var unsubscribe = $rootScope.$on('euroTicketDemoApp:ticketUpdate', function(event, result) {
            vm.ticket = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
