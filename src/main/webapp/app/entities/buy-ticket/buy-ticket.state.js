(function() {
    'use strict';

    angular
        .module('euroTicketDemoApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('buy-tickets', {
            parent: 'entity',
            url: '/buy-tickets',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'euroTicketDemoApp.buyTicket.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/buy-ticket/buy-ticket.html',
                    controller: 'BuyTicketController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('buyTicket');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        });
    }

})();
