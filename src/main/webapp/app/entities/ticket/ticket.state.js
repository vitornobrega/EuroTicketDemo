(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('ticket', {
            parent: 'entity',
            url: '/ticket',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'euroTicketDemoApp.ticket.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/ticket/tickets.html',
                    controller: 'TicketController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('ticket');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('ticket-detail', {
            parent: 'entity',
            url: '/ticket/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'euroTicketDemoApp.ticket.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/ticket/ticket-detail.html',
                    controller: 'TicketDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('ticket');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Ticket', function($stateParams, Ticket) {
                    return Ticket.get({id : $stateParams.id});
                }]
            }
        })
        .state('ticket.new', {
            parent: 'ticket',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ticket/ticket-dialog.html',
                    controller: 'TicketDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                location: null,
                                matchDate: null,
                                unitPrice: null,
                                availableQtt: null,
                                totalQtt: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('ticket', null, { reload: true });
                }, function() {
                    $state.go('ticket');
                });
            }]
        })
        .state('ticket.edit', {
            parent: 'ticket',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ticket/ticket-dialog.html',
                    controller: 'TicketDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Ticket', function(Ticket) {
                            return Ticket.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('ticket', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('ticket.delete', {
            parent: 'ticket',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/ticket/ticket-delete-dialog.html',
                    controller: 'TicketDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Ticket', function(Ticket) {
                            return Ticket.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('ticket', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
