(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('match-group', {
            parent: 'entity',
            url: '/match-group',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'euroTicketDemoApp.matchGroup.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/match-group/match-groups.html',
                    controller: 'MatchGroupController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('matchGroup');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('match-group-detail', {
            parent: 'entity',
            url: '/match-group/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'euroTicketDemoApp.matchGroup.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/match-group/match-group-detail.html',
                    controller: 'MatchGroupDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('matchGroup');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'MatchGroup', function($stateParams, MatchGroup) {
                    return MatchGroup.get({id : $stateParams.id});
                }]
            }
        })
        .state('match-group.new', {
            parent: 'match-group',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/match-group/match-group-dialog.html',
                    controller: 'MatchGroupDialogController',
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
                    $state.go('match-group', null, { reload: true });
                }, function() {
                    $state.go('match-group');
                });
            }]
        })
        .state('match-group.edit', {
            parent: 'match-group',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/match-group/match-group-dialog.html',
                    controller: 'MatchGroupDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['MatchGroup', function(MatchGroup) {
                            return MatchGroup.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('match-group', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('match-group.delete', {
            parent: 'match-group',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/match-group/match-group-delete-dialog.html',
                    controller: 'MatchGroupDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['MatchGroup', function(MatchGroup) {
                            return MatchGroup.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('match-group', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
