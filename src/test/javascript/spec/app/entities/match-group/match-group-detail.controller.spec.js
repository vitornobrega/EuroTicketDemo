'use strict';

describe('Controller Tests', function() {

    describe('MatchGroup Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockMatchGroup;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockMatchGroup = jasmine.createSpy('MockMatchGroup');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'MatchGroup': MockMatchGroup
            };
            createController = function() {
                $injector.get('$controller')("MatchGroupDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'euroTicketDemoApp:matchGroupUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
