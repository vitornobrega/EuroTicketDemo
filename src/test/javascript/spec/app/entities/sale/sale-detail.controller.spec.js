'use strict';

describe('Controller Tests', function() {

    describe('Sale Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockSale, MockItem, MockSaleStatus, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockSale = jasmine.createSpy('MockSale');
            MockItem = jasmine.createSpy('MockItem');
            MockSaleStatus = jasmine.createSpy('MockSaleStatus');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Sale': MockSale,
                'Item': MockItem,
                'SaleStatus': MockSaleStatus,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("SaleDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'euroTicketDemoApp:saleUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
