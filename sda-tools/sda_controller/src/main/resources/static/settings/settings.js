(function () {

    var controllerSettings = angular.module("sdaController.settings", ['ui.router']);

    controllerSettings.config(function ($stateProvider) {
        var settingsState = {
                    url: '/settings',
                    name: "settings",
                    templateUrl: "settings/settings.html"
        };
        
        var globalSettingsState = {
                    name: 'settings.global',
                    parent: settingsState,
                    controller: 'globalSettingsController',
                    templateUrl: "settings/global-settings/globalSettings.html"
        };
        
        var startupScriptSettings ={
                name:'settings.startupScript',
                parent: settingsState,
                controller: 'startupSettingsController',
                templateUrl: 'settings/startup-script-settings/startupScriptSettings.html'
        }
        
        $stateProvider
                .state(settingsState)
                .state(globalSettingsState)
                .state(startupScriptSettings);           
    });
})();