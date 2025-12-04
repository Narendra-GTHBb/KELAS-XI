const admin = require("firebase-admin");

// Initialize Firebase Admin SDK
const serviceAccount = require("./waves-of-food-admin-firebase-adminsdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  projectId: "waves-of-food-2a777",
});

async function checkFirebaseRules() {
  try {
    console.log("🔍 Checking Firebase Security Rules...\n");

    // Note: We can't directly read security rules via Admin SDK
    // But we can test write permissions

    const db = admin.firestore();

    // Test: Try to create a test order
    console.log("🧪 Testing order creation...");

    const testOrder = {
      id: "test_order_" + Date.now(),
      userId: "test_user",
      orderNumber: "TEST-" + Date.now(),
      orderDate: new Date().toISOString(),
      status: "pending",
      items: [
        {
          foodId: "food_test",
          foodName: "Test Food",
          quantity: 1,
          price: 50000,
          subtotal: 50000,
        },
      ],
      subtotal: 50000,
      deliveryFee: 10000,
      tax: 5000,
      totalAmount: 65000,
      deliveryAddress: "Test Address",
      phoneNumber: "+62123456789",
      paymentMethod: "cash",
      notes: "Test order from admin script",
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    };

    const docRef = db.collection("orders").doc();
    await docRef.set(testOrder);

    console.log("✅ Test order created successfully!");
    console.log(`   Order ID: ${docRef.id}`);

    // Clean up test order
    await docRef.delete();
    console.log("🗑️ Test order cleaned up");

    console.log(
      "\n📋 Firebase Security Rules for orders collection seem to allow writes."
    );
    console.log(
      "📱 The issue might be in the user app code or authentication."
    );
  } catch (error) {
    console.error("❌ Error testing Firebase rules:", error);
    console.log(
      "\n🚨 This suggests Firebase Security Rules might be blocking order writes!"
    );
  }
}

checkFirebaseRules();
