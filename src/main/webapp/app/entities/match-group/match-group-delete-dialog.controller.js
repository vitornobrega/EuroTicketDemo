(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .controller('MatchGroupDeleteController',MatchGroupDeleteController);

    MatchGroupDeleteController.$inject = ['$uibModalInstance', 'entity', 'MatchGroup'];

    function MatchGroupDeleteController($uibModalInstance, entity, MatchGroup) {
        var vm = this;
        vm.matchGroup = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            MatchGroup.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
