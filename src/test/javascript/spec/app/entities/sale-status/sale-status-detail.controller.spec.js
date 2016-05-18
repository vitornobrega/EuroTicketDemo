'use strict';

describe('Controller Tests', function() {

    describe('SaleStatus Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSaleStatus;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSaleStatus = jasmine.createSpy('MockSaleStatus');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'SaleStatus': MockSaleStatus
            };
            createController = function() {
                $injector.get('$controller')("SaleStatusDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'euroTicketDemoApp:saleStatusUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
