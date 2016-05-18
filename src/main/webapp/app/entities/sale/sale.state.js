(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sale', {
            parent: 'entity',
            url: '/sale',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'euroTicketDemoApp.sale.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sale/sales.html',
                    controller: 'SaleController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sale');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('sale-detail', {
            parent: 'entity',
            url: '/sale/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'euroTicketDemoApp.sale.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/sale/sale-detail.html',
                    controller: 'SaleDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('sale');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Sale', function($stateParams, Sale) {
                    return Sale.get({id : $stateParams.id});
                }]
            }
        })
        .state('sale.new', {
            parent: 'sale',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sale/sale-dialog.html',
                    controller: 'SaleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                saleDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('sale', null, { reload: true });
                }, function() {
                    $state.go('sale');
                });
            }]
        })
        .state('sale.edit', {
            parent: 'sale',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sale/sale-dialog.html',
                    controller: 'SaleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Sale', function(Sale) {
                            return Sale.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('sale', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('sale.delete', {
            parent: 'sale',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/sale/sale-delete-dialog.html',
                    controller: 'SaleDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Sale', function(Sale) {
                            return Sale.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('sale', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
