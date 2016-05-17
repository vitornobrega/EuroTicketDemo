(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('phase', {
            parent: 'entity',
            url: '/phase',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'euroTicketDemoApp.phase.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/phase/phases.html',
                    controller: 'PhaseController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('phase');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('phase-detail', {
            parent: 'entity',
            url: '/phase/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'euroTicketDemoApp.phase.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/phase/phase-detail.html',
                    controller: 'PhaseDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('phase');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Phase', function($stateParams, Phase) {
                    return Phase.get({id : $stateParams.id});
                }]
            }
        })
        .state('phase.new', {
            parent: 'phase',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/phase/phase-dialog.html',
                    controller: 'PhaseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('phase', null, { reload: true });
                }, function() {
                    $state.go('phase');
                });
            }]
        })
        .state('phase.edit', {
            parent: 'phase',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/phase/phase-dialog.html',
                    controller: 'PhaseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Phase', function(Phase) {
                            return Phase.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('phase', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('phase.delete', {
            parent: 'phase',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/phase/phase-delete-dialog.html',
                    controller: 'PhaseDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Phase', function(Phase) {
                            return Phase.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('phase', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
