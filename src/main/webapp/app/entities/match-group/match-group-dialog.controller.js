(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('MatchGroupDialogController', MatchGroupDialogController);

    MatchGroupDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'MatchGroup'];

    function MatchGroupDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, MatchGroup) {
        var vm = this;
        vm.matchGroup = entity;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('euroTicketDemoApp:matchGroupUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.matchGroup.id !== null) {
                MatchGroup.update(vm.matchGroup, onSaveSuccess, onSaveError);
            } else {
                MatchGroup.save(vm.matchGroup, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
