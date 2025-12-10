const admin = require("firebase-admin");

// Initialize Firebase Admin SDK
const serviceAccount = require("./waves-of-food-admin-firebase-adminsdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  projectId: "waves-of-food-2a777",
});

const db = admin.firestore();

async function checkUsers() {
  try {
    console.log("🔍 Checking users collection in detail...\n");

    const usersSnapshot = await db.collection("users").get();

    if (usersSnapshot.empty) {
      console.log("❌ No users found in Firebase");
      return;
    }

    console.log(`✅ Found ${usersSnapshot.size} users:\n`);

    usersSnapshot.forEach((doc) => {
      const user = doc.data();
      console.log(`📋 User ID: ${doc.id}`);
      console.log(`   Raw data:`, JSON.stringify(user, null, 2));
      console.log("   ----------------");
    });
  } catch (error) {
    console.error("❌ Error checking users:", error);
  }
}

checkUsers();
