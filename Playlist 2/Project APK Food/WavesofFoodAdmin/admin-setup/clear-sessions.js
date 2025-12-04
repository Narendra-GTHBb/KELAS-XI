const admin = require("firebase-admin");

// Initialize Firebase Admin SDK
const serviceAccount = require("./waves-of-food-admin-firebase-adminsdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

async function clearFirebaseAuthSessions() {
  console.log("🧹 Clearing Firebase Auth Sessions...\n");

  try {
    // Get all users from Firebase Auth
    const listUsersResult = await admin.auth().listUsers(1000);

    console.log(
      `📊 Found ${listUsersResult.users.length} users in Firebase Auth`
    );

    // Revoke refresh tokens for all admin users
    for (const userRecord of listUsersResult.users) {
      if (userRecord.email && userRecord.email.includes("@wavesoffood.com")) {
        try {
          await admin.auth().revokeRefreshTokens(userRecord.uid);
          console.log(`🔐 Revoked tokens for: ${userRecord.email}`);
        } catch (error) {
          console.log(
            `❌ Failed to revoke tokens for ${userRecord.email}: ${error.message}`
          );
        }
      }
    }

    console.log("\n✅ All admin sessions cleared!");
    console.log("🎯 Now admin users must login again from the app");
  } catch (error) {
    console.error("❌ Error clearing sessions:", error.message);
  } finally {
    console.log("\n👋 Session cleanup completed!");
    process.exit(0);
  }
}

// Run the cleanup
clearFirebaseAuthSessions();
