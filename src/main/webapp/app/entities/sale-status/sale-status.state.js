(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sale-status', {
            parent: 'entity',
            url: '/sale-status',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'euroTicketDemoApp.saleStatus.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sale-status/sale-statuses.html',
                    controller: 'SaleStatusController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('saleStatus');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('sale-status-detail', {
            parent: 'entity',
            url: '/sale-status/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'euroTicketDemoApp.saleStatus.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sale-status/sale-status-detail.html',
                    controller: 'SaleStatusDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('saleStatus');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'SaleStatus', function($stateParams, SaleStatus) {
                    return SaleStatus.get({id : $stateParams.id});
                }]
            }
        })
        .state('sale-status.new', {
            parent: 'sale-status',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sale-status/sale-status-dialog.html',
                    controller: 'SaleStatusDialogController',
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
                    $state.go('sale-status', null, { reload: true });
                }, function() {
                    $state.go('sale-status');
                });
            }]
        })
        .state('sale-status.edit', {
            parent: 'sale-status',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sale-status/sale-status-dialog.html',
                    controller: 'SaleStatusDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['SaleStatus', function(SaleStatus) {
                            return SaleStatus.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('sale-status', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('sale-status.delete', {
            parent: 'sale-status',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sale-status/sale-status-delete-dialog.html',
                    controller: 'SaleStatusDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['SaleStatus', function(SaleStatus) {
                            return SaleStatus.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('sale-status', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
