const admin = require("firebase-admin");

// Initialize Firebase Admin SDK
const serviceAccount = require("./waves-of-food-admin-firebase-adminsdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();
const auth = admin.auth();

async function listFirebaseUsers() {
  console.log("🔍 Checking Firebase Auth Users...\n");

  try {
    // List users from Firebase Auth
    const listUsersResult = await auth.listUsers(1000);

    console.log(
      `📊 Found ${listUsersResult.users.length} users in Firebase Auth:`
    );
    console.log("=".repeat(60));

    for (const userRecord of listUsersResult.users) {
      console.log(`👤 UID: ${userRecord.uid}`);
      console.log(`   Email: ${userRecord.email || "No email"}`);
      console.log(`   Name: ${userRecord.displayName || "No name"}`);
      console.log(`   Created: ${userRecord.metadata.creationTime}`);
      console.log(`   Verified: ${userRecord.emailVerified}`);
      console.log("   ---");
    }
  } catch (error) {
    console.error("❌ Error listing Firebase Auth users:", error.message);
  }
}

async function listFirestoreAdmins() {
  console.log("\n🔍 Checking Firestore admin_users Collection...\n");

  try {
    // List documents from admin_users collection
    const snapshot = await db.collection("admin_users").get();

    console.log(
      `📊 Found ${snapshot.docs.length} documents in admin_users collection:`
    );
    console.log("=".repeat(60));

    snapshot.forEach((doc) => {
      const data = doc.data();
      console.log(`🔑 Document ID: ${doc.id}`);
      console.log(`   Email: ${data.email || "No email"}`);
      console.log(`   Name: ${data.name || "No name"}`);
      console.log(`   Role: ${data.role || "No role"}`);
      console.log(`   Active: ${data.isActive}`);
      console.log(`   Auth UID: ${data.authUid || "No auth UID"}`);
      console.log("   ---");
    });
  } catch (error) {
    console.error("❌ Error listing Firestore admin users:", error.message);
  }
}

async function checkSpecificUser(email) {
  console.log(`\n🔍 Checking specific user: ${email}\n`);

  try {
    // Check in Firebase Auth
    try {
      const userRecord = await auth.getUserByEmail(email);
      console.log(`✅ Found in Firebase Auth:`);
      console.log(`   UID: ${userRecord.uid}`);
      console.log(`   Email verified: ${userRecord.emailVerified}`);
      console.log(`   Disabled: ${userRecord.disabled}`);
    } catch (authError) {
      console.log(`❌ Not found in Firebase Auth: ${authError.message}`);
    }

    // Check in Firestore
    try {
      const snapshot = await db
        .collection("admin_users")
        .where("email", "==", email)
        .get();
      if (!snapshot.empty) {
        const doc = snapshot.docs[0];
        const data = doc.data();
        console.log(`✅ Found in Firestore:`);
        console.log(`   Document ID: ${doc.id}`);
        console.log(`   Name: ${data.name}`);
        console.log(`   Role: ${data.role}`);
        console.log(`   Active: ${data.isActive}`);
        console.log(`   Auth UID: ${data.authUid}`);
      } else {
        console.log(`❌ Not found in Firestore admin_users collection`);
      }
    } catch (firestoreError) {
      console.log(`❌ Error checking Firestore: ${firestoreError.message}`);
    }
  } catch (error) {
    console.error("❌ Error checking user:", error.message);
  }
}

async function main() {
  console.log("🌊 Waves of Food - Firebase Account Checker");
  console.log("==========================================\n");

  try {
    await listFirebaseUsers();
    await listFirestoreAdmins();

    // Check specific test user
    await checkSpecificUser("siti.rahayu@wavesoffood.com");

    console.log("\n🎯 RECOMMENDED LOGIN CREDENTIALS:");
    console.log("================================");
    console.log("Email: siti.rahayu@wavesoffood.com");
    console.log("Password: Admin123!");
    console.log("\nAlternatives:");
    console.log("Email: ahmad.supriadi@wavesoffood.com");
    console.log("Password: SuperAdmin123!");
  } catch (error) {
    console.error("\n💥 Check failed:", error.message);
  } finally {
    console.log("\n👋 Check completed.");
    process.exit(0);
  }
}

// Run the check
main();
