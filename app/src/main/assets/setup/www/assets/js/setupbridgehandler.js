

function bridgeReceiveObjectFromNative(signalName, msg) {

    if (window.SetupBridgeInterfaceLibObj) {
        window.SetupBridgeInterfaceLibObj._bridgeNativeEventDispatcher(msg);
    }
}

(function (window) {

    function SetupBridgeInterfaceLib() {
        var SetupBridgeInterfaceLibObj = {};
        var SetupBridgeNativeEventDispatcher = null;
        var SetupBridgeObject = null;

        SetupBridgeInterfaceLibObj.SetNativeEventDispatcher = function (SetupBridgeNativeEventDispatcher, SetupBridgeObject) {
            this.SetupBridgeNativeEventDispatcher = SetupBridgeNativeEventDispatcher;
            this.SetupBridgeObject = SetupBridgeObject;
        };


        SetupBridgeInterfaceLibObj.getControlSystems = function () {
            console.log('SetupBridgeInterfaceLibObj.getControlSystems checking webkit');
            if (window.JSInterface) {
                // Running on an iPhone
                var empty = {}
                console.log('checking window.JSInterface.messageHandlers');
                if (window.JSInterface.messageHandlers)  //iOS   
                {
                    console.log('checking window.JSInterface.messageHandlers.getControlSystems');
                    if (window.JSInterface.messageHandlers.getControlSystems) {
                        console.log('calling window.JSInterface.messageHandlers.getControlSystems.postMessage');
                        //window.JSInterface.messageHandlers.getControlSystems.postMessage(empty);
                        window.JSInterface.messageHandlers.bridgeSendObjectToNative.postMessage("Csig.GetControlSystems", empty);
                    }
                    else
                        console.log('checking window.JSInterface.messageHandlers.getControlSystems null');
                }
                else if (window.JSInterface.getControlSystems) // Android
                {
                    window.JSInterface.bridgeSendObjectToNative("Csig.GetControlSystems", empty);

                }
                else {
                    console.log('no handler functions in window.JSInterface');
                }
            }
            else {
                // Running in a browser, maybe?        
                console.log('Cannot call getControlSystemEntries');
                console.log('Not running on device');
            }

        };

        //CURRENTLY UNUSED
        // SetupBridgeInterfaceLibObj.bridgeSendObjectToNative = function (signalName, object) {
        //     console.log('SetupBridgeInterfaceLibObj.bridgeSendObjectToNative checking webkit');
        //     if (window.JSInterface) {
        //         // Running on an iPhone
        //         var empty = {}
        //         console.log('checking window.JSInterface.messageHandlers');
        //         if (window.JSInterface.messageHandlers)  //iOS   
        //         {
        //             console.log('checking window.JSInterface.messageHandlers.bridgeSendObjectToNative');
        //             if (window.JSInterface.messageHandlers.bridgeSendObjectToNative) {
        //                 console.log('calling window.JSInterface.messageHandlers.getControlSystems.postMessage');
        //                 window.JSInterface.messageHandlers.bridgeSendObjectToNative.postMessage(signalName, empty)
        //             }
        //             else
        //                 console.log('checking window.JSInterface.messageHandlers.bridgeSendObjectToNative null');
        //         }
        //         else if (window.JSInterface.getControlSystems) // Android
        //         {
        //             window.JSInterface.getControlSystems(empty);

        //         }
        //         else {
        //             console.log('no handler functions in window.JSInterface');
        //         }
        //     }
        //     else {
        //         // Running in a browser, maybe?        
        //         console.log('Cannot call getControlSystemEntries');
        //         console.log('Not running on device');
        //     }

        // };

        SetupBridgeInterfaceLibObj.launchDemo = function (demotype) {
            var msg = demotype;
            setTimeout(_bridgeNativeEventDispatcher, 3000, msg);
        };

        //CURRENTLY UNUSED
        // SetupBridgeInterfaceLibObj.getControlSystemEntry = function (controlSystemID) {
        //     console.log('SetupBridgeInterfaceLibObj.getControlSystems checking webkit');
        //     if (window.JSInterface) {
        //         console.log('Calling getControlSystemEntry with id' + controlSystemID);
        //         // Running on an iPhone
        //         if (window.JSInterface.messageHandlers)  //iOS   
        //         {
        //             var msg = { cs_id: controlSystemID };
        //             window.JSInterface.messageHandlers.getControlSystemEntry.postMessage(msg);
        //         }
        //         else if (window.JSInterface.getControlSystemEntry) // Android
        //         {
        //             window.JSInterface.getControlSystemEntry(msg);
        //         }
        //     }
        //     else {
        //         // Running in a browser, maybe?
        //         console.log('Cannot call getControlSystemEntry with id: ' + controlSystemID);
        //         console.log('Not running on device');
        //     }
        // };

        SetupBridgeInterfaceLibObj.saveControlSystemEntry = function (caControlSystemEntry) {

            console.log('SetupBridgeInterfaceLibObj.saveControlSystemEntry caControlSystemEntry.id: ' + caControlSystemEntry.controlSystemID + ', caControlSystemEntry.friendlyName: ' + caControlSystemEntry.friendlyName);
            console.log('SetupBridgeInterfaceLibObj.saveControlSystemEntry checking webkit');
            if (window.JSInterface) {
                console.log('Calling saveControlSystemEntry');
                // Running on an iPhone
                if (window.JSInterface.messageHandlers)  //iOS   
                {
                    var msg = { caControlSystemEntry: caControlSystemEntry };
                    //window.JSInterface.messageHandlers.saveControlSystemEntry.postMessage(JSON.stringify(caControlSystemEntry));
                    //window.JSInterface.messageHandlers.bridgeSendObjectToNative("Csig.SaveControlSystemEntry", msg)
                    window.JSInterface.messageHandlers.bridgeSendObjectToNative.postMessage("Csig.SaveControlSystemEntry", JSON.stringify(caControlSystemEntry))
                }
                else if (window.JSInterface.saveControlSystemEntry) // Android
                {
                    //window.JSInterface.saveControlSystemEntry();
                    window.JSInterface.bridgeSendObjectToNative("Csig.SaveControlSystemEntry", JSON.stringify(caControlSystemEntry));
                }
            }
            else {
                // Running in a browser, maybe?
                console.log('Cannot call saveControlSystemEntry ');
                console.log('Not running on device');
            }
        };

        SetupBridgeInterfaceLibObj.deleteControlSystemEntry = function (caControlSystemEntry) {
            console.log('SetupBridgeInterfaceLibObj.getControlSystems checking webkit');
            if (window.JSInterface) {
                console.log('Calling connectToControlSystem with id' + caControlSystemEntry.controlSystemID);
                if (window.JSInterface.messageHandlers) // Running on an iPhone
                {
                    var msg = { caControlSystemEntry: caControlSystemEntry };
                    //window.JSInterface.messageHandlers.deleteControlSystemEntry.postMessage(JSON.stringify(caControlSystemEntry));
                    window.JSInterface.messageHandlers.bridgeSendObjectToNative.postMessage("Csig.DeleteControlSystemEntry", JSON.stringify(caControlSystemEntry))
                }
                else if (window.JSInterface.deleteControlSystemEntry) // Android
                {
                    //window.JSInterface.deleteControlSystemEntry(JSON.stringify(caControlSystemEntry));
                    window.JSInterface.bridgeSendObjectToNative("Csig.DeleteControlSystemEntry", JSON.stringify(caControlSystemEntry));
                }
            }
            else {
                // Running in a browser, maybe?
                console.log('Cannot call deleteControlSystemEntry with id: ' + controlSystemID);
                console.log('Not running on device');
            }
        };

        SetupBridgeInterfaceLibObj.connectToControlSystem = function (caControlSystemEntry) {
            console.log('SetupBridgeInterfaceLibObj.getControlSystems checking webkit');
            if (window.JSInterface) {
                console.log('Calling connectToControlSystem with id' + caControlSystemEntry.cs_id);
                if (window.JSInterface.messageHandlers)  // Running on an iPhone
                {
                    var msg = { caControlSystemEntry: caControlSystemEntry };
                    //window.JSInterface.messageHandlers.connectToControlSystem.postMessage(JSON.stringify(caControlSystemEntry));
                    window.JSInterface.messageHandlers.bridgeSendObjectToNative.postMessage("Csig.ConnectToControlSystem", JSON.stringify(caControlSystemEntry))
                }
                else if (window.JSInterface.connectToControlSystem) // Android
                {
                    //window.JSInterface.connectToControlSystem(JSON.stringify(caControlSystemEntry));
                    window.JSInterface.bridgeSendObjectToNative("Csig.ConnectToControlSystem", JSON.stringify(caControlSystemEntry));
                }

            }
            else {
                // Running in a browser, maybe?
                console.log('Cannot call connectToControlSystem with id: ' + controlSystemID);
                console.log('Not running on device');
            }
        };

        SetupBridgeInterfaceLibObj.previewControlSystem = function (controlSystemID) {
            console.log('Not implemented yet!');
        };

        SetupBridgeInterfaceLibObj.launchSettingsScreen = function () {
            console.log('Not implemented yet!');
        };

        SetupBridgeInterfaceLibObj.exportSettings = function () {
            var msg = {};
            setTimeout(_bridgeNativeEventDispatcher, 3000, msg);
        };

        SetupBridgeInterfaceLibObj.exportLogs = function () {
            var msg = {};
            setTimeout(_bridgeNativeEventDispatcher, 3000, msg);
        };

        SetupBridgeInterfaceLibObj.enableKeyclicks = function () {
            console.log('Not implemented yet!');
        };

        SetupBridgeInterfaceLibObj.editVOIPSettings = function () {
            console.log('Not implemented yet!');
        };

        SetupBridgeInterfaceLibObj.saveVOIPSettings = function (caVOIPSettings) {
            console.log('Not implemented yet!');
        };

        SetupBridgeInterfaceLibObj.voipDial = function (SIPAddress) {
            console.log('Not implemented yet!');
        };

        SetupBridgeInterfaceLibObj.voipAnswer = function () {
            console.log('Not implemented yet!');
        };

        SetupBridgeInterfaceLibObj.voipHangUp = function () {
            console.log('Not implemented yet!');
        };

        SetupBridgeInterfaceLibObj.voipPageAll = function () {
            console.log('Not implemented yet!');
        };

        SetupBridgeInterfaceLibObj._bridgeNativeEventDispatcher = function (msg) {
            if (this.SetupBridgeNativeEventDispatcher) {

                argArr = new Array();
                argArr.push(msg);

                this.SetupBridgeNativeEventDispatcher.apply(this.SetupBridgeObject, argArr);
            }
        };

        return SetupBridgeInterfaceLibObj;
    }

    //define globally if it doesn't already exist
    if (typeof (SetupBridgeInterfaceLibObj) === 'undefined') {
        window.SetupBridgeInterfaceLibObj = SetupBridgeInterfaceLib();
    }
    else {
        console.log("SetupBridgeInterfaceLibObj already defined.");
    }
})(window);