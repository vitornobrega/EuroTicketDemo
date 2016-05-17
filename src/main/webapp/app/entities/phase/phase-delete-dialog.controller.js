(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('PhaseDeleteController',PhaseDeleteController);

    PhaseDeleteController.$inject = ['$uibModalInstance', 'entity', 'Phase'];

    function PhaseDeleteController($uibModalInstance, entity, Phase) {
        var vm = this;
        vm.phase = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Phase.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
