const fs = require("fs");
const path = require("path");

/**
 * Data generator untuk sample data tambahan
 */

// Template untuk generate makanan tambahan
const foodTemplates = {
  pizza: [
    "Hawaiian Pizza",
    "Veggie Supreme",
    "Meat Lovers",
    "BBQ Chicken Pizza",
    "Four Cheese Pizza",
  ],
  burger: [
    "Cheeseburger Deluxe",
    "Mushroom Swiss Burger",
    "Bacon Burger",
    "Veggie Burger",
    "Fish Burger",
  ],
  drinks: [
    "Mango Smoothie",
    "Strawberry Shake",
    "Cola",
    "Lemonade",
    "Hot Chocolate",
  ],
  dessert: ["Tiramisu", "Cheesecake", "Apple Pie", "Brownies", "Donuts"],
};

// Generate random price between min and max
function randomPrice(min, max) {
  return Math.floor(Math.random() * (max - min + 1) + min) * 1000;
}

// Generate random rating between 3.5 and 5.0
function randomRating() {
  return Math.round((Math.random() * 1.5 + 3.5) * 10) / 10;
}

// Generate additional food items
function generateAdditionalFoods(count = 20) {
  const categories = ["cat_pizza", "cat_burger", "cat_drinks", "cat_dessert"];
  const foods = [];

  for (let i = 0; i < count; i++) {
    const categoryId =
      categories[Math.floor(Math.random() * categories.length)];
    const categoryType = categoryId.replace("cat_", "");
    const names = foodTemplates[categoryType] || ["Special Food"];
    const foodName = names[Math.floor(Math.random() * names.length)];

    const food = {
      id: `food_generated_${i + 1}`,
      name: foodName,
      description: `Delicious ${foodName.toLowerCase()} made with premium ingredients`,
      categoryId: categoryId,
      price: randomPrice(20, 100),
      imageUrl: `https://images.unsplash.com/photo-${1500000000000 + i}`,
      rating: randomRating(),
      isPopular: Math.random() > 0.7,
      isAvailable: Math.random() > 0.1,
      preparationTime: Math.floor(Math.random() * 20) + 5,
      ingredients: ["Premium ingredients", "Fresh herbs", "Special sauce"],
    };

    foods.push(food);
  }

  return foods;
}

// Generate sample users
function generateUsers(count = 10) {
  const firstNames = [
    "Alice",
    "Bob",
    "Charlie",
    "Diana",
    "Edward",
    "Fiona",
    "George",
    "Helen",
  ];
  const lastNames = [
    "Anderson",
    "Brown",
    "Chen",
    "Davis",
    "Evans",
    "Garcia",
    "Johnson",
    "Williams",
  ];
  const users = [];

  for (let i = 0; i < count; i++) {
    const firstName = firstNames[Math.floor(Math.random() * firstNames.length)];
    const lastName = lastNames[Math.floor(Math.random() * lastNames.length)];
    const fullName = `${firstName} ${lastName}`;

    const user = {
      id: `user_generated_${i + 1}`,
      email: `${firstName.toLowerCase()}.${lastName.toLowerCase()}@example.com`,
      fullName: fullName,
      phoneNumber: `+628123456${String(i).padStart(4, "0")}`,
      address: `Jl. ${lastName} No. ${
        Math.floor(Math.random() * 999) + 1
      }, Jakarta`,
      profileImageUrl: `https://images.unsplash.com/photo-${1500000000000 + i}`,
      isActive: Math.random() > 0.1,
      registrationDate: new Date(
        2024,
        Math.floor(Math.random() * 8),
        Math.floor(Math.random() * 28) + 1
      )
        .toISOString()
        .split("T")[0],
      lastLoginDate: new Date(2024, 7, Math.floor(Math.random() * 12) + 1)
        .toISOString()
        .split("T")[0],
      orderCount: Math.floor(Math.random() * 30),
      totalSpent: Math.floor(Math.random() * 2000000),
      favoriteCategories: ["cat_pizza", "cat_burger"].slice(
        0,
        Math.floor(Math.random() * 2) + 1
      ),
    };

    users.push(user);
  }

  return users;
}

// Save generated data
function saveGeneratedData() {
  const dataDir = path.join(__dirname, "data");

  // Create data directory if it doesn't exist
  if (!fs.existsSync(dataDir)) {
    fs.mkdirSync(dataDir);
  }

  // Load existing data
  const existingFoods = JSON.parse(
    fs.readFileSync(path.join(dataDir, "foods.json"), "utf8")
  );
  const existingUsers = JSON.parse(
    fs.readFileSync(path.join(dataDir, "users.json"), "utf8")
  );

  // Generate additional data
  const additionalFoods = generateAdditionalFoods(15);
  const additionalUsers = generateUsers(8);

  // Merge with existing data
  const allFoods = [...existingFoods, ...additionalFoods];
  const allUsers = [...existingUsers, ...additionalUsers];

  // Save updated data
  fs.writeFileSync(
    path.join(dataDir, "foods-extended.json"),
    JSON.stringify(allFoods, null, 2)
  );

  fs.writeFileSync(
    path.join(dataDir, "users-extended.json"),
    JSON.stringify(allUsers, null, 2)
  );

  console.log("📊 Generated additional sample data:");
  console.log(
    `   Foods: ${additionalFoods.length} (Total: ${allFoods.length})`
  );
  console.log(
    `   Users: ${additionalUsers.length} (Total: ${allUsers.length})`
  );
  console.log("\n📁 Files created:");
  console.log("   - data/foods-extended.json");
  console.log("   - data/users-extended.json");
}

// Run generator if called directly
if (require.main === module) {
  console.log("🎲 Generating additional sample data...\n");
  saveGeneratedData();
  console.log("\n✅ Data generation completed!");
}

module.exports = {
  generateAdditionalFoods,
  generateUsers,
  saveGeneratedData,
};
