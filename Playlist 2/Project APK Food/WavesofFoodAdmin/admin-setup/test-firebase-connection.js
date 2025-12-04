// Debug script untuk test Firebase connection
const admin = require("firebase-admin");

// Initialize Firebase Admin (using existing config)
const serviceAccount = require("./waves-of-food-admin-firebase-adminsdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  projectId: "waves-of-food-2a777",
});

const db = admin.firestore();

async function testFirebaseConnection() {
  try {
    console.log("🔍 Testing Firebase connection...");

    // Test 1: Check foods collection
    console.log("\n📋 Testing foods collection:");
    const foodsSnapshot = await db.collection("foods").limit(5).get();
    console.log(`✅ Foods found: ${foodsSnapshot.size} documents`);

    foodsSnapshot.forEach((doc) => {
      const data = doc.data();
      console.log(
        `   - ${doc.id}: ${data.name || "No name"} - ${
          data.price || "No price"
        }`
      );
    });

    // Test 2: Check users collection
    console.log("\n👥 Testing users collection:");
    const usersSnapshot = await db.collection("users").limit(5).get();
    console.log(`✅ Users found: ${usersSnapshot.size} documents`);

    usersSnapshot.forEach((doc) => {
      const data = doc.data();
      console.log(
        `   - ${doc.id}: ${data.fullName || data.email || "No name"}`
      );
    });

    // Test 3: Check categories collection
    console.log("\n🏷️ Testing categories collection:");
    const categoriesSnapshot = await db.collection("categories").limit(5).get();
    console.log(`✅ Categories found: ${categoriesSnapshot.size} documents`);

    categoriesSnapshot.forEach((doc) => {
      const data = doc.data();
      console.log(`   - ${doc.id}: ${data.name || "No name"}`);
    });

    // Test 4: Check admin_users collection
    console.log("\n👨‍💼 Testing admin_users collection:");
    const adminSnapshot = await db.collection("admin_users").limit(5).get();
    console.log(`✅ Admin users found: ${adminSnapshot.size} documents`);

    adminSnapshot.forEach((doc) => {
      const data = doc.data();
      console.log(
        `   - ${doc.id}: ${data.fullName || data.email || "No name"}`
      );
    });

    // Test 5: Check Firebase Rules
    console.log("\n🔒 Firebase connection test completed successfully!");
    console.log("📱 If Android app still shows empty, the issue is likely:");
    console.log("   1. Firebase Security Rules blocking Android access");
    console.log("   2. Android app authentication not working");
    console.log("   3. Network connectivity issues");
  } catch (error) {
    console.error("❌ Firebase connection test failed:", error);
  } finally {
    process.exit(0);
  }
}

testFirebaseConnection();
