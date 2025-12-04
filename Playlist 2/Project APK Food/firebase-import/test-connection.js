const admin = require("firebase-admin");
require("dotenv").config();

/**
 * Quick test script to verify Firebase connection
 */
async function testConnection() {
  try {
    console.log("🔥 Testing Firebase connection...\n");

    // Initialize Firebase Admin (if not already initialized)
    if (!admin.apps.length) {
      const serviceAccount = require("./serviceAccountKey.json");
      admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
        projectId: process.env.FIREBASE_PROJECT_ID,
      });
    }

    const db = admin.firestore();

    // Test Firestore connection
    console.log("📊 Testing Firestore connection...");
    const testDoc = await db.collection("test").doc("connection").set({
      timestamp: admin.firestore.FieldValue.serverTimestamp(),
      message: "Connection test successful",
    });

    console.log("✅ Firestore connection successful!");

    // Clean up test document
    await db.collection("test").doc("connection").delete();
    console.log("🧹 Test document cleaned up");

    // Get project info
    console.log("\n📋 Project Information:");
    console.log(`   Project ID: ${process.env.FIREBASE_PROJECT_ID}`);
    console.log(`   Admin SDK: Connected`);

    console.log("\n🎉 Firebase connection test completed successfully!");
  } catch (error) {
    console.error("❌ Firebase connection test failed:", error.message);

    if (error.code === "permission-denied") {
      console.error("\n💡 Check your Firebase Security Rules:");
      console.error("   - Ensure Firestore rules allow authenticated requests");
      console.error("   - Verify service account has proper permissions");
    }

    if (error.code === "not-found") {
      console.error("\n💡 Check your configuration:");
      console.error("   - Verify FIREBASE_PROJECT_ID in .env file");
      console.error("   - Ensure serviceAccountKey.json is valid");
    }
  } finally {
    // Close Firebase connection
    if (admin.apps.length) {
      await admin.app().delete();
    }
    process.exit(0);
  }
}

// Run test
testConnection();
