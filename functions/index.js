const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const admin = require("firebase-admin");

admin.initializeApp();

exports.onNewMessage = onDocumentCreated("chats/{chatId}/messages/{messageId}", async (event) => {
  const msg = event.data.data();
  const chatId = event.params.chatId;

  // 1. Get the two duoIds from the chatId (e.g., "duoA_duoB")
  const duoIds = chatId.split("_");
  const tokens = [];

  // 2. Loop through both duos to find all 4 users
  for (const duoId of duoIds) {
    try {
      const duoDoc = await admin.firestore().collection("duos").doc(duoId).get();

      if (duoDoc.exists) {
        const userIds = duoDoc.data().userIds || [];

        // 3. For each user, get their fcmToken
        for (const userId of userIds) {
          // Don't notify the person who sent the message!
          if (userId !== msg.senderId) {
            const userDoc = await admin.firestore().collection("users").doc(userId).get();
            const userData = userDoc.data();
            const token = userData ? userData.fcmToken : null;
            if (token) {
              tokens.push(token);
            }
          }
        }
      }
    } catch (error) {
      console.error("Error fetching duo or user data:", error);
    }
  }

  // 4. Send the notification to the recipients
  if (tokens.length > 0) {
    const message = {
      notification: {
        title: `Message from ${msg.senderName}`,
        body: msg.text,
      },
      android: {
          notification: {
            // This is the key! It tells Android to trigger the Deep Link
            link: `duodating://chat/${chatId}`,
            channelId: "chat_notifications",
          }
        },
      data: {
        chatId: chatId,
        type: "CHAT_MESSAGE",
      },
      tokens: tokens,
    };

    try {
      const response = await admin.messaging().sendEachForMulticast(message);
      console.log(`Successfully sent ${response.successCount} messages.`);
    } catch (error) {
      console.error("Error sending notification:", error);
    }
  }
  return null;
});