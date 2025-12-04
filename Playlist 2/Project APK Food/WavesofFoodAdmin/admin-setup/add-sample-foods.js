const admin = require("firebase-admin");

// Initialize Firebase Admin (using existing config)
const serviceAccount = require("./waves-of-food-admin-firebase-adminsdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  projectId: "waves-of-food",
});

const db = admin.firestore();

async function addSampleFoods() {
  try {
    console.log("🍽️ Adding sample food items...");

    const sampleFoods = [
      {
        id: "food001",
        name: "Nasi Gudeg Yogya",
        description:
          "Authentic Yogyakarta-style gudeg with tender young jackfruit, served with steamed rice, chicken, and spicy sambal.",
        price: 25000,
        categoryId: "traditional",
        categoryName: "Traditional Indonesian",
        imageUrl:
          "https://images.unsplash.com/photo-1594736797933-d0401ba2fe65?w=400",
        ingredients: [
          "Young jackfruit",
          "Coconut milk",
          "Palm sugar",
          "Chicken",
          "Boiled egg",
          "Tofu",
        ],
        preparationTime: 25,
        rating: 4.8,
        ratingCount: 156,
        isAvailable: true,
        isPopular: true,
        isVegetarian: false,
        isSpicy: true,
        calories: 450,
        allergens: ["Egg", "Soy"],
        createdAt: admin.firestore.Timestamp.now(),
        updatedAt: admin.firestore.Timestamp.now(),
      },
      {
        id: "food002",
        name: "Rendang Daging Sapi",
        description:
          "Slow-cooked beef rendang with rich coconut curry sauce, herbs and spices from Padang.",
        price: 35000,
        categoryId: "traditional",
        categoryName: "Traditional Indonesian",
        imageUrl:
          "https://images.unsplash.com/photo-1565299624946-b28f40a0ca4b?w=400",
        ingredients: [
          "Beef",
          "Coconut milk",
          "Lemongrass",
          "Galangal",
          "Chili",
          "Shallots",
        ],
        preparationTime: 45,
        rating: 4.9,
        ratingCount: 234,
        isAvailable: true,
        isPopular: true,
        isVegetarian: false,
        isSpicy: true,
        calories: 520,
        allergens: [],
        createdAt: admin.firestore.Timestamp.now(),
        updatedAt: admin.firestore.Timestamp.now(),
      },
      {
        id: "food003",
        name: "Gado-Gado Jakarta",
        description:
          "Traditional Indonesian salad with mixed vegetables, tofu, tempeh, and peanut sauce.",
        price: 18000,
        categoryId: "healthy",
        categoryName: "Healthy & Fresh",
        imageUrl:
          "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?w=400",
        ingredients: [
          "Bean sprouts",
          "Tofu",
          "Tempeh",
          "Peanut sauce",
          "Cucumber",
          "Lettuce",
        ],
        preparationTime: 15,
        rating: 4.6,
        ratingCount: 89,
        isAvailable: true,
        isPopular: false,
        isVegetarian: true,
        isSpicy: false,
        calories: 320,
        allergens: ["Peanuts", "Soy"],
        createdAt: admin.firestore.Timestamp.now(),
        updatedAt: admin.firestore.Timestamp.now(),
      },
      {
        id: "food004",
        name: "Nasi Fried Rice Special",
        description:
          "Indonesian-style fried rice with prawns, chicken, vegetables, and topped with fried egg.",
        price: 22000,
        categoryId: "rice",
        categoryName: "Rice & Noodles",
        imageUrl:
          "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=400",
        ingredients: [
          "Jasmine rice",
          "Prawns",
          "Chicken",
          "Egg",
          "Vegetables",
          "Sweet soy sauce",
        ],
        preparationTime: 20,
        rating: 4.5,
        ratingCount: 178,
        isAvailable: true,
        isPopular: true,
        isVegetarian: false,
        isSpicy: false,
        calories: 480,
        allergens: ["Egg", "Shellfish"],
        createdAt: admin.firestore.Timestamp.now(),
        updatedAt: admin.firestore.Timestamp.now(),
      },
      {
        id: "food005",
        name: "Bakso Malang Original",
        description:
          "Traditional Indonesian meatball soup with various types of meatballs, noodles, and clear broth.",
        price: 20000,
        categoryId: "soup",
        categoryName: "Soups & Broths",
        imageUrl:
          "https://images.unsplash.com/photo-1590301157890-4810ed352733?w=400",
        ingredients: [
          "Beef meatballs",
          "Fish balls",
          "Tofu",
          "Noodles",
          "Clear broth",
          "Fried onions",
        ],
        preparationTime: 15,
        rating: 4.7,
        ratingCount: 145,
        isAvailable: true,
        isPopular: true,
        isVegetarian: false,
        isSpicy: false,
        calories: 380,
        allergens: ["Gluten"],
        createdAt: admin.firestore.Timestamp.now(),
        updatedAt: admin.firestore.Timestamp.now(),
      },
      {
        id: "food006",
        name: "Es Teh Manis",
        description:
          "Traditional Indonesian sweet iced tea, refreshing and perfect for hot weather.",
        price: 8000,
        categoryId: "drinks",
        categoryName: "Beverages",
        imageUrl:
          "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=400",
        ingredients: ["Black tea", "Palm sugar", "Ice cubes"],
        preparationTime: 5,
        rating: 4.3,
        ratingCount: 67,
        isAvailable: true,
        isPopular: false,
        isVegetarian: true,
        isSpicy: false,
        calories: 120,
        allergens: [],
        createdAt: admin.firestore.Timestamp.now(),
        updatedAt: admin.firestore.Timestamp.now(),
      },
    ];

    // Add each food item to Firestore
    const batch = db.batch();

    sampleFoods.forEach((food) => {
      const foodRef = db.collection("foods").doc(food.id);
      batch.set(foodRef, food);
    });

    await batch.commit();

    console.log("✅ Successfully added sample food items:");
    sampleFoods.forEach((food) => {
      console.log(`   📱 ${food.name} - Rp ${food.price.toLocaleString()}`);
    });

    // Also add food categories
    const categories = [
      {
        id: "traditional",
        name: "Traditional Indonesian",
        description: "Authentic Indonesian traditional dishes",
        imageUrl:
          "https://images.unsplash.com/photo-1594736797933-d0401ba2fe65?w=300",
        sortOrder: 1,
        isActive: true,
      },
      {
        id: "healthy",
        name: "Healthy & Fresh",
        description: "Fresh and healthy meal options",
        imageUrl:
          "https://images.unsplash.com/photo-1567620832903-9fc6debc209f?w=300",
        sortOrder: 2,
        isActive: true,
      },
      {
        id: "rice",
        name: "Rice & Noodles",
        description: "Rice and noodle-based dishes",
        imageUrl:
          "https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=300",
        sortOrder: 3,
        isActive: true,
      },
      {
        id: "soup",
        name: "Soups & Broths",
        description: "Warm and comforting soup dishes",
        imageUrl:
          "https://images.unsplash.com/photo-1590301157890-4810ed352733?w=300",
        sortOrder: 4,
        isActive: true,
      },
      {
        id: "drinks",
        name: "Beverages",
        description: "Refreshing drinks and beverages",
        imageUrl:
          "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=300",
        sortOrder: 5,
        isActive: true,
      },
    ];

    const categoryBatch = db.batch();
    categories.forEach((category) => {
      const categoryRef = db.collection("food_categories").doc(category.id);
      categoryBatch.set(categoryRef, category);
    });

    await categoryBatch.commit();
    console.log("✅ Successfully added food categories");

    console.log("\n🎉 Sample food data setup complete!");
    console.log(
      "📱 You can now test the Food Management CRUD features in the app"
    );
  } catch (error) {
    console.error("❌ Error adding sample foods:", error);
  } finally {
    process.exit(0);
  }
}

addSampleFoods();
