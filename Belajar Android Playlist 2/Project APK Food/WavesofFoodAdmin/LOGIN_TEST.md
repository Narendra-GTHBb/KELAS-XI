# Waves of Food Admin - Login Test

## 🔑 Test Credentials

Setelah menjalankan script import admin (`npm run import-admin`), Anda dapat menggunakan credentials berikut untuk testing:

### Super Administrator

- **Email**: ahmad.supriadi@wavesoffood.com
- **Password**: SuperAdmin123!
- **Role**: Super Admin
- **Permissions**: All permissions

### Administrator 1

- **Email**: siti.rahayu@wavesoffood.com
- **Password**: Admin123!
- **Role**: Admin
- **Permissions**: manage_foods, manage_users, manage_orders, view_analytics, manage_categories, manage_restaurants

### Administrator 2

- **Email**: budi.santoso@wavesoffood.com
- **Password**: Admin123!
- **Role**: Admin
- **Permissions**: manage_foods, manage_orders, view_analytics, manage_categories

### Moderator 1

- **Email**: dewi.lestari@wavesoffood.com
- **Password**: Moderator123!
- **Role**: Moderator
- **Permissions**: manage_foods, manage_users, view_orders

### Moderator 2

- **Email**: rizki.pratama@wavesoffood.com
- **Password**: Moderator123!
- **Role**: Moderator
- **Permissions**: manage_users, view_orders, view_analytics

## 🧪 Testing Steps

1. **Build & Install App**

   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Test Login Flow**

   - Open app (should show login screen)
   - Try invalid credentials (should show error)
   - Try valid credentials (should login successfully)
   - Close app and reopen (should auto-login if "Remember me" checked)

3. **Test Features by Role**

   - **Super Admin**: Should access all features
   - **Admin**: Should access most features except some settings
   - **Moderator**: Should access limited features

4. **Test Logout**
   - Use menu → Logout
   - Should return to login screen
   - Should not auto-login anymore

## ⚠️ Security Notes

- **Change passwords** immediately after first login in production
- All passwords are stored using bcrypt hashing
- Firebase Auth handles authentication
- Local session is stored using SharedPreferences
- Admin verification is done against Firestore admin_users collection

## 🔧 Troubleshooting

**Login Failed Issues:**

1. Make sure Firebase project is properly configured
2. Check if admin import script ran successfully
3. Verify internet connection
4. Check Firebase Auth is enabled
5. Ensure Firestore rules allow admin access

**Auto-login Issues:**

1. Check if "Remember me" is enabled
2. Clear app data if needed
3. Verify SharedPreferences are working

**Permission Issues:**

1. Verify admin role in Firestore
2. Check permissions array in admin document
3. Ensure isActive is true
