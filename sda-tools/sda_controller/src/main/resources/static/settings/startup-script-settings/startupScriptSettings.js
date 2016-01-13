(function () {

    angular.module("sdaController.settings")
            .controller('startupSettingsController',function($scope,$http,$log){
                 
                $scope.startupScriptProps=[];
        
                $http.get('/sdaConfig').then(function(resp){
                    $log.debug(resp.data);
                    $scope.startupScriptData = resp.data;
                    $scope.startupScriptProps = $scope.startupScriptData.props;
                    $scope.startupScriptOldProps = angular.copy($scope.startupScriptProps);
                },function(error){
                    $scope.$emit('error','Failed to retrieve startup script configurations');
                    $log.error("failed to call api data for sdaConfig");
                    $log.error(error);
                });
                
                $scope.reset=function(){
                    $scope.startupScriptProps=$scope.startupScriptOldProps;
                };
                
                $scope.save=function(){
                    $log.debug("Sending sdaConfs to update..");
                    $log.debug(JSON.stringify($scope.startupScriptData));
                    $http.put("/sdaConfig",$scope.startupScriptData)
                         .then(function(resp){
                             $scope.$emit('notification','Startup Script configurations update successfully');
                             $scope.startupScriptOldProps = angular.copy($scope.startupScriptProps);
                         },function(error){
                             $log.error(error);
                             $scope.$emit('error','Startup Script configurations update failed');
                         });
                };
            });
    
})();


