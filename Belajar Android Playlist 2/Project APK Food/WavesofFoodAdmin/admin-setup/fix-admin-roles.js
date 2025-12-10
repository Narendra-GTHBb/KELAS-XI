const admin = require("firebase-admin");

// Initialize Firebase Admin SDK
const serviceAccount = require("./waves-of-food-admin-firebase-adminsdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();

async function fixAdminRoles() {
  console.log("🔧 Fixing Admin Roles in Firestore...\n");

  try {
    // Get all admin users
    const snapshot = await db.collection("admin_users").get();

    console.log(`📊 Found ${snapshot.docs.length} admin documents to update:`);
    console.log("=".repeat(60));

    const batch = db.batch();
    let updateCount = 0;

    snapshot.forEach((doc) => {
      const data = doc.data();
      let newRole = data.role;

      // Convert role to match enum in Android code
      if (data.role === "Super Admin") {
        newRole = "SUPER_ADMIN";
      } else if (data.role === "Admin") {
        newRole = "ADMIN";
      } else if (data.role === "Moderator") {
        newRole = "MODERATOR";
      }

      if (newRole !== data.role) {
        console.log(`🔄 Updating ${data.email}: "${data.role}" → "${newRole}"`);
        batch.update(doc.ref, { role: newRole });
        updateCount++;
      } else {
        console.log(`✅ Already correct ${data.email}: "${data.role}"`);
      }
    });

    if (updateCount > 0) {
      await batch.commit();
      console.log(`\n✅ Successfully updated ${updateCount} admin roles!`);
    } else {
      console.log(`\n🎯 All roles are already correct!`);
    }

    // Verify the changes
    console.log("\n🔍 Verifying updated data:");
    console.log("=".repeat(40));

    const verifySnapshot = await db.collection("admin_users").get();
    verifySnapshot.forEach((doc) => {
      const data = doc.data();
      console.log(`👤 ${data.email} - Role: ${data.role}`);
    });
  } catch (error) {
    console.error("❌ Error fixing admin roles:", error.message);
  } finally {
    console.log("\n👋 Fix completed!");
    process.exit(0);
  }
}

// Run the fix
fixAdminRoles();
