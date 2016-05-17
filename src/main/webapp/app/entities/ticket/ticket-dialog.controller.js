(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('TicketDialogController', TicketDialogController);

    TicketDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Ticket', 'Team', 'MatchGroup', 'Phase'];

    function TicketDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Ticket, Team, MatchGroup, Phase) {
        var vm = this;
        vm.ticket = entity;
        vm.hometeams = Team.query({filter: 'ticket-is-null'});
        $q.all([vm.ticket.$promise, vm.hometeams.$promise]).then(function() {
            if (!vm.ticket.homeTeamId) {
                return $q.reject();
            }
            return Team.get({id : vm.ticket.homeTeamId}).$promise;
        }).then(function(homeTeam) {
            vm.hometeams.push(homeTeam);
        });
        vm.awayteams = Team.query({filter: 'ticket-is-null'});
        $q.all([vm.ticket.$promise, vm.awayteams.$promise]).then(function() {
            if (!vm.ticket.awayTeamId) {
                return $q.reject();
            }
            return Team.get({id : vm.ticket.awayTeamId}).$promise;
        }).then(function(awayTeam) {
            vm.awayteams.push(awayTeam);
        });
        vm.matchgroups = MatchGroup.query({filter: 'ticket-is-null'});
        $q.all([vm.ticket.$promise, vm.matchgroups.$promise]).then(function() {
            if (!vm.ticket.matchGroupId) {
                return $q.reject();
            }
            return MatchGroup.get({id : vm.ticket.matchGroupId}).$promise;
        }).then(function(matchGroup) {
            vm.matchgroups.push(matchGroup);
        });
        vm.phases = Phase.query({filter: 'ticket-is-null'});
        $q.all([vm.ticket.$promise, vm.phases.$promise]).then(function() {
            if (!vm.ticket.phaseId) {
                return $q.reject();
            }
            return Phase.get({id : vm.ticket.phaseId}).$promise;
        }).then(function(phase) {
            vm.phases.push(phase);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('euroTicketDemoApp:ticketUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.ticket.id !== null) {
                Ticket.update(vm.ticket, onSaveSuccess, onSaveError);
            } else {
                Ticket.save(vm.ticket, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.matchDate = false;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
