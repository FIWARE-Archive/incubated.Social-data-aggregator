(function () {

    var dashboard = angular.module("sdaController.dashboard", ['ui.router','ui.bootstrap']);

    dashboard.config(function ($stateProvider) {
        var dashboardState = {
                    url: '/dashboard',
                    name: "dashboard",
                    templateUrl: "dashboard/dashboard.html",
                    controller: 'DashboardController'
        };
        
        
        $stateProvider
                .state(dashboardState);           
    });
    
    dashboard.controller('DashboardController',function($scope,$http,$log){
        $scope.ck = {startWithEnv:false};
        $scope.hideLog = true;
        $scope.isStarted = false;
        $scope.commandSent = false;
        $scope.startSda=function(){
            $log.debug("starting sda.. calling /dashboard/startSda?startWithEnv="+$scope.ck.startWithEnv);
            $scope.commandSent = true;
            $http.get('/dashboard/startSda?startWithEnv='+$scope.ck.startWithEnv)
                 .then(function(resp){
                            $scope.hideLog = false;
                            $scope.isStarted = true;
                            $scope.commandSent = false;
                       },
                       function(resp){
                           $scope.$emit('error','Failed to execute start command on sda (Check permissions on start-all.sh file)');
                       });
        };
        $scope.stopSda=function(){
            $scope.commandSent = true;
            $log.debug("stopping sda..");
            $http.get('/dashboard/stopSda')
                 .then(function(resp){
                            //$scope.hideLog = true;
                            $scope.isStarted = false;
                            $scope.commandSent = false;
                       },
                       function(resp){
                           $scope.$emit('error','Failed to execute stop command on sda');
                       });
        };
        $scope.getStartupLogSSE = function(){
            $log.debug("starting sse connection..");
            var source = new EventSource('/dashboard/sdaStartupLogContent');
            source.onmessage = function(event){
                $log.debug("received "+event);
                $scope.logRows = JSON.parse(event.data);
            }
        }
    });
})();

