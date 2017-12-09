let functions = require('firebase-functions');
let admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

// Based on
// https://android.jlelse.eu/serverless-notifications-with-cloud-functions-for-firebase-685d7c327cd4

// Listen for changes to the notification table
exports.sendNotification = functions.database.ref('/notifications/{pushId}')
    .onWrite(event => {
        const notification = event.data.current.val();
        const message = notification.message;
        const recipientUid = notification.recipient;

        // Resolve a Promise to get the device token from the database
        const targetDeviceTokenPromise = admin.database().ref(`/users/${recipientUid}/deviceToken`).once('value');
        return Promise.resolve(targetDeviceTokenPromise).then(targetDeviceToken => {

            // Construct message itself
            const payload = {
                notification: {
                    title: 'Example Notification',
                    body: message
                }
            };

            // Deliver the message to the target device w/ correct payload
            admin.messaging().sendToDevice(targetDeviceToken.val(), payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
                });

        });
    });
