'use strict';

describe('Controller Tests', function() {

    describe('Ticket Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTicket, MockTeam, MockMatchGroup, MockPhase;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTicket = jasmine.createSpy('MockTicket');
            MockTeam = jasmine.createSpy('MockTeam');
            MockMatchGroup = jasmine.createSpy('MockMatchGroup');
            MockPhase = jasmine.createSpy('MockPhase');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Ticket': MockTicket,
                'Team': MockTeam,
                'MatchGroup': MockMatchGroup,
                'Phase': MockPhase
            };
            createController = function() {
                $injector.get('$controller')("TicketDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'euroTicketDemoApp:ticketUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
