const admin = require("firebase-admin");

// Initialize Firebase Admin SDK
const serviceAccount = require("./waves-of-food-admin-firebase-adminsdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  projectId: "waves-of-food-2a777",
});

const db = admin.firestore();

async function checkOrders() {
  try {
    console.log("🔍 Checking orders collection in detail...\n");

    const ordersSnapshot = await db.collection("orders").get();

    if (ordersSnapshot.empty) {
      console.log("❌ No orders found in Firebase");
      console.log(
        "📱 This confirms orders from user app are not syncing to Firebase!\n"
      );

      // Let's also check all collections to see what exists
      console.log("🔍 Checking all collections...");
      const collections = await db.listCollections();
      console.log("Available collections:");
      collections.forEach((collection) => {
        console.log(`   - ${collection.id}`);
      });
      return;
    }

    console.log(`✅ Found ${ordersSnapshot.size} orders:\n`);

    ordersSnapshot.forEach((doc) => {
      const order = doc.data();
      console.log(`📋 Order ID: ${doc.id}`);
      console.log(`   Raw data:`, JSON.stringify(order, null, 2));
      console.log("   ----------------");
    });
  } catch (error) {
    console.error("❌ Error checking orders:", error);
  }
}

checkOrders();
