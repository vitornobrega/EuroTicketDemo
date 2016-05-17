(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('PhaseDialogController', PhaseDialogController);

    PhaseDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Phase'];

    function PhaseDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Phase) {
        var vm = this;
        vm.phase = entity;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('euroTicketDemoApp:phaseUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.phase.id !== null) {
                Phase.update(vm.phase, onSaveSuccess, onSaveError);
            } else {
                Phase.save(vm.phase, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
