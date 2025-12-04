const admin = require("firebase-admin");
const bcrypt = require("bcryptjs");

// Initialize Firebase Admin SDK
// Download service account key dari Firebase Console dan taruh di folder ini
const serviceAccount = require("./waves-of-food-admin-firebase-adminsdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();
const auth = admin.auth();

async function createAdminUsers() {
  console.log("🚀 Starting admin user creation...\n");

  const adminUsers = [
    {
      id: "super-admin-001",
      name: "Ahmad Supriadi",
      email: "ahmad.supriadi@wavesoffood.com",
      password: "SuperAdmin123!",
      role: "Super Admin",
      permissions: ["all"],
      isActive: true,
      profileImage: "",
      phone: "+62812-3456-7890",
      address: "Jakarta Pusat, DKI Jakarta",
      department: "Management",
      joinDate: Date.now() - 365 * 24 * 60 * 60 * 1000, // 1 year ago
      createdAt: Date.now() - 365 * 24 * 60 * 60 * 1000,
      updatedAt: Date.now(),
      lastLogin: 0,
      loginCount: 0,
      notes: "Founder and Super Administrator of Waves of Food",
    },
    {
      id: "admin-001",
      name: "Siti Rahayu",
      email: "siti.rahayu@wavesoffood.com",
      password: "Admin123!",
      role: "Admin",
      permissions: [
        "manage_foods",
        "manage_users",
        "manage_orders",
        "view_analytics",
        "manage_categories",
        "manage_restaurants",
      ],
      isActive: true,
      profileImage: "",
      phone: "+62813-7890-1234",
      address: "Bandung, Jawa Barat",
      department: "Operations",
      joinDate: Date.now() - 180 * 24 * 60 * 60 * 1000, // 6 months ago
      createdAt: Date.now() - 180 * 24 * 60 * 60 * 1000,
      updatedAt: Date.now(),
      lastLogin: 0,
      loginCount: 0,
      notes: "Main administrator for daily operations",
    },
    {
      id: "admin-002",
      name: "Budi Santoso",
      email: "budi.santoso@wavesoffood.com",
      password: "Admin123!",
      role: "Admin",
      permissions: [
        "manage_foods",
        "manage_orders",
        "view_analytics",
        "manage_categories",
      ],
      isActive: true,
      profileImage: "",
      phone: "+62814-5678-9012",
      address: "Surabaya, Jawa Timur",
      department: "Food Management",
      joinDate: Date.now() - 120 * 24 * 60 * 60 * 1000, // 4 months ago
      createdAt: Date.now() - 120 * 24 * 60 * 60 * 1000,
      updatedAt: Date.now(),
      lastLogin: 0,
      loginCount: 0,
      notes: "Specialist in food and menu management",
    },
    {
      id: "moderator-001",
      name: "Dewi Lestari",
      email: "dewi.lestari@wavesoffood.com",
      password: "Moderator123!",
      role: "Moderator",
      permissions: ["manage_foods", "manage_users", "view_orders"],
      isActive: true,
      profileImage: "",
      phone: "+62815-2345-6789",
      address: "Yogyakarta, DIY",
      department: "Customer Service",
      joinDate: Date.now() - 90 * 24 * 60 * 60 * 1000, // 3 months ago
      createdAt: Date.now() - 90 * 24 * 60 * 60 * 1000,
      updatedAt: Date.now(),
      lastLogin: 0,
      loginCount: 0,
      notes: "Content moderator and customer support",
    },
    {
      id: "moderator-002",
      name: "Rizki Pratama",
      email: "rizki.pratama@wavesoffood.com",
      password: "Moderator123!",
      role: "Moderator",
      permissions: ["manage_users", "view_orders", "view_analytics"],
      isActive: true,
      profileImage: "",
      phone: "+62816-8901-2345",
      address: "Medan, Sumatera Utara",
      department: "User Management",
      joinDate: Date.now() - 60 * 24 * 60 * 60 * 1000, // 2 months ago
      createdAt: Date.now() - 60 * 24 * 60 * 60 * 1000,
      updatedAt: Date.now(),
      lastLogin: 0,
      loginCount: 0,
      notes: "User management and order monitoring specialist",
    },
  ];

  let successCount = 0;
  let errorCount = 0;

  for (const adminUser of adminUsers) {
    try {
      console.log(`Creating admin: ${adminUser.name} (${adminUser.email})`);

      // Create Firebase Auth user
      const userRecord = await auth.createUser({
        uid: adminUser.id,
        email: adminUser.email,
        password: adminUser.password,
        displayName: adminUser.name,
        emailVerified: true,
      });

      // Hash password for Firestore storage
      const hashedPassword = await bcrypt.hash(adminUser.password, 12);

      // Prepare data for Firestore
      const adminData = {
        ...adminUser,
        password: hashedPassword, // Store hashed password
        authUid: userRecord.uid,
      };
      delete adminData.id; // Remove id from data object

      // Save to admin_users collection
      await db.collection("admin_users").doc(adminUser.id).set(adminData);

      console.log(
        `✅ Success: ${adminUser.name} created with UID: ${userRecord.uid}`
      );
      successCount++;
    } catch (error) {
      console.error(`❌ Error creating ${adminUser.name}:`, error.message);
      errorCount++;
    }
  }

  console.log(`\n📊 Summary:`);
  console.log(`✅ Successfully created: ${successCount} admin users`);
  console.log(`❌ Failed: ${errorCount} admin users`);

  if (successCount > 0) {
    console.log(`\n🔑 Admin Login Credentials:`);
    console.log(`============================`);
    adminUsers.forEach((admin) => {
      if (admin.role === "Super Admin") {
        console.log(`👑 ${admin.role}: ${admin.email} / ${admin.password}`);
      } else if (admin.role === "Admin") {
        console.log(`⚡ ${admin.role}: ${admin.email} / ${admin.password}`);
      } else {
        console.log(`🛡️  ${admin.role}: ${admin.email} / ${admin.password}`);
      }
    });
    console.log(`\n⚠️  IMPORTANT: Change these passwords after first login!`);
  }
}

async function main() {
  console.log("🌊 Waves of Food - Admin Import Script");
  console.log("=====================================\n");

  try {
    await createAdminUsers();
    console.log("\n🎉 Admin import completed successfully!");
  } catch (error) {
    console.error("\n💥 Import failed:", error.message);
    console.error("Please check your Firebase configuration and try again.");
  } finally {
    console.log("\n👋 Closing connection...");
    process.exit(0);
  }
}

// Handle unhandled rejections
process.on("unhandledRejection", (reason, promise) => {
  console.error("Unhandled Rejection at:", promise, "reason:", reason);
  process.exit(1);
});

// Run the import
main();
