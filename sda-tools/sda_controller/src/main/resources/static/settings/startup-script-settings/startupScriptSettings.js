(function () {

    angular.module("sdaController.settings")
            .controller('startupSettingsController',function($scope,$http,$log){
                 
                $scope.startupScriptSections=[];
        
                $http.get('/config/sdaConfig').then(function(resp){
                    $log.debug(resp.data);
                    $scope.startupScriptData = resp.data;
                    $scope.startupScriptSections = $scope.startupScriptData.sections;
                    $scope.startupScriptOldProps = angular.copy($scope.startupScriptSections);
                },function(error){
                    $scope.$emit('error','Failed to retrieve startup script configurations');
                    $log.error("failed to call api data for sdaConfig");
                    $log.error(error);
                });
                
                $scope.reset=function(){
                    $scope.startupScriptSections=$scope.startupScriptOldProps;
                };
                
                $scope.save=function(){
                    $log.debug("Sending sdaConfs to update..");
                    $log.debug(JSON.stringify($scope.startupScriptData));
                    $http.put("/config/sdaConfig",$scope.startupScriptData)
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


