const admin = require("firebase-admin");
const bcrypt = require("bcryptjs");

// Initialize Firebase Admin SDK
// You need to download service account key from Firebase Console
const serviceAccount = require("./waves-of-food-admin-firebase-adminsdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  // Firestore doesn't need databaseURL, only Realtime Database does
});

const db = admin.firestore();
const auth = admin.auth();

async function createAdminUsers() {
  console.log("Starting admin user creation...");

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
        "manage_settings",
      ],
      isActive: true,
      profileImage: "",
      phone: "+62813-4567-8901",
      address: "Bandung, Jawa Barat",
      department: "Operations",
      joinDate: Date.now() - 180 * 24 * 60 * 60 * 1000, // 6 months ago
      createdAt: Date.now() - 180 * 24 * 60 * 60 * 1000,
      updatedAt: Date.now(),
      lastLogin: 0,
      loginCount: 0,
      notes: "Operations Manager - handles day-to-day operations",
    },
    {
      id: "admin-002",
      name: "Budi Santoso",
      email: "budi.santoso@wavesoffood.com",
      password: "Admin456!",
      role: "Admin",
      permissions: [
        "manage_foods",
        "manage_orders",
        "view_analytics",
        "manage_categories",
        "customer_support",
      ],
      isActive: true,
      profileImage: "",
      phone: "+62814-5678-9012",
      address: "Surabaya, Jawa Timur",
      department: "Customer Service",
      joinDate: Date.now() - 120 * 24 * 60 * 60 * 1000, // 4 months ago
      createdAt: Date.now() - 120 * 24 * 60 * 60 * 1000,
      updatedAt: Date.now(),
      lastLogin: 0,
      loginCount: 0,
      notes: "Customer Service Manager - handles customer relations",
    },
    {
      id: "moderator-001",
      name: "Rina Wulandari",
      email: "rina.wulandari@wavesoffood.com",
      password: "Moderator123!",
      role: "Moderator",
      permissions: ["manage_foods", "manage_users", "view_orders"],
      isActive: true,
      profileImage: "",
      phone: "+62815-6789-0123",
      address: "Yogyakarta, DIY",
      department: "Content",
      joinDate: Date.now() - 90 * 24 * 60 * 60 * 1000, // 3 months ago
      createdAt: Date.now() - 90 * 24 * 60 * 60 * 1000,
      updatedAt: Date.now(),
      lastLogin: 0,
      loginCount: 0,
      notes: "Content Moderator - manages food listings and user content",
    },
    {
      id: "moderator-002",
      name: "Dian Pratama",
      email: "dian.pratama@wavesoffood.com",
      password: "Moderator456!",
      role: "Moderator",
      permissions: ["manage_foods", "view_orders", "customer_support"],
      isActive: true,
      profileImage: "",
      phone: "+62816-7890-1234",
      address: "Medan, Sumatera Utara",
      department: "Support",
      joinDate: Date.now() - 60 * 24 * 60 * 60 * 1000, // 2 months ago
      createdAt: Date.now() - 60 * 24 * 60 * 60 * 1000,
      updatedAt: Date.now(),
      lastLogin: 0,
      loginCount: 0,
      notes: "Support Specialist - assists with technical issues",
    },
    {
      id: "admin-003",
      name: "Agus Firmansyah",
      email: "agus.firmansyah@wavesoffood.com",
      password: "Admin789!",
      role: "Admin",
      permissions: [
        "view_analytics",
        "manage_orders",
        "manage_users",
        "financial_reports",
      ],
      isActive: true,
      profileImage: "",
      phone: "+62817-8901-2345",
      address: "Makassar, Sulawesi Selatan",
      department: "Finance",
      joinDate: Date.now() - 150 * 24 * 60 * 60 * 1000, // 5 months ago
      createdAt: Date.now() - 150 * 24 * 60 * 60 * 1000,
      updatedAt: Date.now(),
      lastLogin: 0,
      loginCount: 0,
      notes: "Financial Analyst - monitors revenue and financial reports",
    },
  ];

  try {
    for (const adminUser of adminUsers) {
      console.log(`Creating admin user: ${adminUser.email}`);

      // Create Firebase Auth user
      const userRecord = await auth.createUser({
        uid: adminUser.id,
        email: adminUser.email,
        password: adminUser.password,
        displayName: adminUser.name,
        emailVerified: true,
      });

      // Hash password for Firestore (for additional security)
      const hashedPassword = await bcrypt.hash(adminUser.password, 10);

      // Save to admin_users collection
      const adminData = {
        ...adminUser,
        password: hashedPassword, // Store hashed password
        authUid: userRecord.uid,
      };
      delete adminData.id; // Remove id from data object

      await db.collection("admin_users").doc(adminUser.id).set(adminData);

      console.log(
        `✓ Created admin user: ${adminUser.email} with UID: ${userRecord.uid}`
      );
    }

    console.log("\n✅ All admin users created successfully!");
  } catch (error) {
    console.error("❌ Error creating admin users:", error);
  }
}

async function createFoodCategories() {
  console.log("\nCreating food categories...");

  const categories = [
    {
      id: "cat-001",
      name: "Main Course",
      description: "Primary dishes and meals",
      imageUrl: "",
      isActive: true,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    },
    {
      id: "cat-002",
      name: "Appetizers",
      description: "Starters and small plates",
      imageUrl: "",
      isActive: true,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    },
    {
      id: "cat-003",
      name: "Desserts",
      description: "Sweet treats and desserts",
      imageUrl: "",
      isActive: true,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    },
    {
      id: "cat-004",
      name: "Beverages",
      description: "Drinks and beverages",
      imageUrl: "",
      isActive: true,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    },
    {
      id: "cat-005",
      name: "Snacks",
      description: "Light snacks and finger foods",
      imageUrl: "",
      isActive: true,
      createdAt: Date.now(),
      updatedAt: Date.now(),
    },
  ];

  try {
    for (const category of categories) {
      const categoryData = { ...category };
      delete categoryData.id;

      await db.collection("categories").doc(category.id).set(categoryData);
      console.log(`✓ Created category: ${category.name}`);
    }

    console.log("✅ All categories created successfully!");
  } catch (error) {
    console.error("❌ Error creating categories:", error);
  }
}

async function createAppSettings() {
  console.log("\nCreating app settings...");

  const appSettings = {
    appName: "Waves of Food",
    version: "1.0.0",
    adminPanelVersion: "1.0.0",
    maintenanceMode: false,
    allowNewRegistrations: true,
    maxOrdersPerDay: 100,
    defaultCurrency: "IDR",
    supportEmail: "support@wavesoffood.com",
    termsOfServiceUrl: "https://wavesoffood.com/terms",
    privacyPolicyUrl: "https://wavesoffood.com/privacy",
    updatedAt: Date.now(),
    updatedBy: "super-admin-001",
  };

  try {
    await db.collection("app_settings").doc("general").set(appSettings);
    console.log("✅ App settings created successfully!");
  } catch (error) {
    console.error("❌ Error creating app settings:", error);
  }
}

async function createAnalyticsData() {
  console.log("\nCreating initial analytics data...");

  const analyticsData = {
    totalUsers: 0,
    totalOrders: 0,
    totalRevenue: 0,
    totalRestaurants: 1, // Admin panel counts as 1 restaurant
    totalFoods: 0,
    totalCategories: 5, // We created 5 categories
    lastUpdated: Date.now(),
  };

  try {
    await db.collection("analytics").doc("summary").set(analyticsData);
    console.log("✅ Analytics data created successfully!");
  } catch (error) {
    console.error("❌ Error creating analytics data:", error);
  }
}

async function main() {
  console.log("🚀 Starting Waves of Food Admin Setup...\n");

  try {
    await createAdminUsers();
    await createFoodCategories();
    await createAppSettings();
    await createAnalyticsData();

    console.log("\n🎉 Setup completed successfully!");
    console.log("\nAdmin Login Credentials:");
    console.log("========================");
    console.log("Super Admin: superadmin@wavesoffood.com / SuperAdmin123!");
    console.log("Admin: admin@wavesoffood.com / Admin123!");
    console.log("Moderator: moderator@wavesoffood.com / Moderator123!");
    console.log("\n⚠️  Please change these passwords after first login!");
  } catch (error) {
    console.error("❌ Setup failed:", error);
  } finally {
    process.exit(0);
  }
}

// Run the setup
main();
