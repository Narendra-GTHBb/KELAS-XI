const admin = require("firebase-admin");
const fs = require("fs");
const path = require("path");
require("dotenv").config();

// Firebase Admin SDK initialization
const serviceAccount = require("./serviceAccountKey.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  projectId: process.env.FIREBASE_PROJECT_ID,
});

const db = admin.firestore();

// Configuration
const config = {
  collections: {
    categories: process.env.COLLECTION_CATEGORIES || "categories",
    foods: process.env.COLLECTION_FOODS || "foods",
    users: process.env.COLLECTION_USERS || "users",
    orders: process.env.COLLECTION_ORDERS || "orders",
  },
  clearExistingData: process.env.CLEAR_EXISTING_DATA === "true",
  batchSize: parseInt(process.env.BATCH_SIZE) || 10,
};

/**
 * Clear collection data
 */
async function clearCollection(collectionName) {
  console.log(`🗑️ Clearing collection: ${collectionName}`);

  const collection = db.collection(collectionName);
  const snapshot = await collection.get();

  const batch = db.batch();
  snapshot.docs.forEach((doc) => {
    batch.delete(doc.ref);
  });

  await batch.commit();
  console.log(`✅ Cleared ${snapshot.size} documents from ${collectionName}`);
}

/**
 * Import data to collection
 */
async function importCollection(collectionName, data) {
  console.log(`📁 Importing ${data.length} documents to ${collectionName}...`);

  const collection = db.collection(collectionName);
  let imported = 0;

  // Process in batches
  for (let i = 0; i < data.length; i += config.batchSize) {
    const batch = db.batch();
    const chunk = data.slice(i, i + config.batchSize);

    chunk.forEach((item) => {
      const docRef = collection.doc(item.id);
      batch.set(docRef, {
        ...item,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    });

    await batch.commit();
    imported += chunk.length;
    console.log(`   ⏳ Imported ${imported}/${data.length} documents...`);
  }

  console.log(
    `✅ Successfully imported ${imported} documents to ${collectionName}`
  );
}

/**
 * Load JSON data file
 */
function loadData(filename) {
  const filePath = path.join(__dirname, "data", filename);
  if (!fs.existsSync(filePath)) {
    console.error(`❌ File not found: ${filePath}`);
    return [];
  }

  try {
    const data = JSON.parse(fs.readFileSync(filePath, "utf8"));
    console.log(`📂 Loaded ${data.length} items from ${filename}`);
    return data;
  } catch (error) {
    console.error(`❌ Error reading ${filename}:`, error.message);
    return [];
  }
}

/**
 * Main import function
 */
async function importData() {
  console.log("🚀 Starting Waves of Food data import...\n");

  try {
    // Clear existing data if requested
    if (config.clearExistingData) {
      console.log("🗑️ Clearing existing data...");
      await clearCollection(config.collections.categories);
      await clearCollection(config.collections.foods);
      await clearCollection(config.collections.users);
      await clearCollection(config.collections.orders);
      console.log("");
    }

    // Load data files
    const categories = loadData("categories.json");
    const foods = loadData("foods.json");
    const users = loadData("users.json");
    const orders = loadData("orders.json");

    console.log("");

    // Import data
    if (categories.length > 0) {
      await importCollection(config.collections.categories, categories);
    }

    if (foods.length > 0) {
      await importCollection(config.collections.foods, foods);
    }

    if (users.length > 0) {
      await importCollection(config.collections.users, users);
    }

    if (orders.length > 0) {
      await importCollection(config.collections.orders, orders);
    }

    console.log("\n🎉 Data import completed successfully!");
    console.log("\n📊 Summary:");
    console.log(`   Categories: ${categories.length}`);
    console.log(`   Foods: ${foods.length}`);
    console.log(`   Users: ${users.length}`);
    console.log(`   Orders: ${orders.length}`);
  } catch (error) {
    console.error("❌ Import failed:", error);
  } finally {
    // Close Firebase connection
    await admin.app().delete();
    process.exit(0);
  }
}

/**
 * Validate environment and files
 */
function validateSetup() {
  const errors = [];

  // Check service account key
  if (!fs.existsSync("./serviceAccountKey.json")) {
    errors.push("❌ serviceAccountKey.json not found");
  }

  // Check environment variables
  if (!process.env.FIREBASE_PROJECT_ID) {
    errors.push("❌ FIREBASE_PROJECT_ID not set in .env");
  }

  // Check data directory
  if (!fs.existsSync("./data")) {
    errors.push("❌ data/ directory not found");
  }

  if (errors.length > 0) {
    console.error("🚫 Setup validation failed:\n");
    errors.forEach((error) => console.error(error));
    console.error("\n💡 Please check README.md for setup instructions");
    process.exit(1);
  }

  console.log("✅ Setup validation passed");
}

// Run import
console.log("🔥 Waves of Food - Firebase Data Import Tool\n");
validateSetup();
importData();
