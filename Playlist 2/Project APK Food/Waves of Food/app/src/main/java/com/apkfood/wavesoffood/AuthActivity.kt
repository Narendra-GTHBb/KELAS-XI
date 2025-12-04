package com.apkfood.wavesoffood

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.apkfood.wavesoffood.utils.UserSessionManager
import com.apkfood.wavesoffood.model.User

/**
 * Authentication Activity dengan UI yang indah
 * Activity untuk login dan registrasi user
 */
class AuthActivity : AppCompatActivity() {
    
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Hide action bar
        supportActionBar?.hide()
        
        createBeautifulLoginUI()
    }
    
    private fun createBeautifulLoginUI() {
        // Main ScrollView
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        
        // Main container
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(80, 100, 80, 100)
        }
        
        // App Title Layout (Logo + Text)
        val titleLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 20)
        }
        
        // App Logo (Vector Asset)
        val logo = ImageView(this).apply {
            setImageResource(R.drawable.ic_food_delivery)
            layoutParams = LinearLayout.LayoutParams(120, 120).apply {
                setMargins(0, 0, 30, 0)
            }
        }
        titleLayout.addView(logo)
        
        // App Title Text
        val title = TextView(this).apply {
            text = "Waves of Food"
            textSize = 32f
            setTextColor(Color.parseColor("#FF6B35"))
        }
        titleLayout.addView(title)
        
        mainLayout.addView(titleLayout)
        
        // Subtitle
        val subtitle = TextView(this).apply {
            text = "Delicious food delivered to your door"
            textSize = 16f
            setTextColor(Color.parseColor("#666666"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 120)
        }
        mainLayout.addView(subtitle)
        
        // Login Form Title
        val formTitle = TextView(this).apply {
            text = "Login to Your Account"
            textSize = 24f
            setTextColor(Color.parseColor("#333333"))
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 60)
        }
        mainLayout.addView(formTitle)
        
        // Username Label
        val usernameLabel = TextView(this).apply {
            text = "Email or Username"
            textSize = 14f
            setTextColor(Color.parseColor("#333333"))
            setPadding(0, 0, 0, 20)
        }
        mainLayout.addView(usernameLabel)
        
        // Username EditText
        etUsername = EditText(this).apply {
            hint = "Enter your email or username"
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            textSize = 16f
            setPadding(40, 40, 40, 40)
            background = createEditTextBackground()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                140
            ).apply {
                setMargins(0, 0, 0, 40)
            }
        }
        mainLayout.addView(etUsername)
        
        // Password Label
        val passwordLabel = TextView(this).apply {
            text = "Password"
            textSize = 14f
            setTextColor(Color.parseColor("#333333"))
            setPadding(0, 0, 0, 20)
        }
        mainLayout.addView(passwordLabel)
        
        // Password EditText
        etPassword = EditText(this).apply {
            hint = "Enter your password"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            textSize = 16f
            setPadding(40, 40, 40, 40)
            background = createEditTextBackground()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                140
            ).apply {
                setMargins(0, 0, 0, 20)
            }
        }
        mainLayout.addView(etPassword)
        
        // Forgot Password
        val forgotPassword = TextView(this).apply {
            text = "Forgot Password?"
            textSize = 14f
            setTextColor(Color.parseColor("#FF6B35"))
            gravity = Gravity.END
            setPadding(0, 0, 0, 60)
            setOnClickListener {
                Toast.makeText(this@AuthActivity, "Forgot Password feature coming soon!", Toast.LENGTH_LONG).show()
            }
        }
        mainLayout.addView(forgotPassword)
        
        // Login Button
        val loginBtn = Button(this).apply {
            text = "Login"
            textSize = 18f
            setTextColor(Color.WHITE)
            background = createOrangeButtonBackground()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                140
            ).apply {
                setMargins(0, 0, 0, 40)
            }
            setOnClickListener {
                performLogin()
            }
        }
        mainLayout.addView(loginBtn)
        
        // Demo Credentials Info
        val demoInfoCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = createEditTextBackground().apply {
                setColor(Color.parseColor("#F0F8FF"))
            }
            setPadding(30, 20, 30, 20)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 30, 0, 20)
            }
        }
        
        val demoTitle = TextView(this).apply {
            text = "🚀 Demo Accounts Available:"
            textSize = 14f
            setTextColor(Color.parseColor("#FF6B35"))
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        demoInfoCard.addView(demoTitle)
        
        val demoAccounts = TextView(this).apply {
            text = """
                Admin: admin / admin123
                User: user1 / pass123
                Email: john.doe@gmail.com / password123
                Demo: demo / demo123
            """.trimIndent()
            textSize = 12f
            setTextColor(Color.parseColor("#666666"))
            gravity = Gravity.CENTER
            setPadding(0, 10, 0, 0)
            setLineSpacing(1.2f, 1.0f)
        }
        demoInfoCard.addView(demoAccounts)
        
        mainLayout.addView(demoInfoCard)
        
        // OR Divider
        val orLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 40)
            }
        }
        
        val line1 = View(this).apply {
            setBackgroundColor(Color.parseColor("#CCCCCC"))
            layoutParams = LinearLayout.LayoutParams(0, 2, 1f)
        }
        orLayout.addView(line1)
        
        val orText = TextView(this).apply {
            text = "  OR  "
            textSize = 14f
            setTextColor(Color.parseColor("#666666"))
        }
        orLayout.addView(orText)
        
        val line2 = View(this).apply {
            setBackgroundColor(Color.parseColor("#CCCCCC"))
            layoutParams = LinearLayout.LayoutParams(0, 2, 1f)
        }
        orLayout.addView(line2)
        
        mainLayout.addView(orLayout)
        
        // Guest Button
        val guestBtn = Button(this).apply {
            text = "Continue as Guest"
            textSize = 18f
            setTextColor(Color.parseColor("#FF6B35"))
            background = createWhiteButtonBackground()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                140
            ).apply {
                setMargins(0, 0, 0, 80)
            }
            setOnClickListener {
                // Save guest session
                UserSessionManager.saveGuestSession(this@AuthActivity)
                navigateToMain()
            }
        }
        mainLayout.addView(guestBtn)
        
        // Sign Up Layout
        val signUpLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        
        val signUpText1 = TextView(this).apply {
            text = "Don't have an account? "
            textSize = 14f
            setTextColor(Color.parseColor("#666666"))
        }
        signUpLayout.addView(signUpText1)
        
        val signUpText2 = TextView(this).apply {
            text = "Sign Up"
            textSize = 14f
            setTextColor(Color.parseColor("#FF6B35"))
            setOnClickListener {
                Toast.makeText(this@AuthActivity, "Sign Up feature coming soon!", Toast.LENGTH_LONG).show()
            }
        }
        signUpLayout.addView(signUpText2)
        
        mainLayout.addView(signUpLayout)
        
        scrollView.addView(mainLayout)
        setContentView(scrollView)
    }
    
    private fun createEditTextBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = 20f
            setStroke(2, Color.parseColor("#E0E0E0"))
        }
    }
    
    private fun createOrangeButtonBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.parseColor("#FF6B35"))
            cornerRadius = 20f
        }
    }
    
    private fun createWhiteButtonBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = 20f
            setStroke(4, Color.parseColor("#FF6B35"))
        }
    }
    
    private fun performLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        
        if (username.isEmpty()) {
            etUsername.error = "Please enter your email or username"
            etUsername.requestFocus()
            return
        }
        
        if (password.isEmpty()) {
            etPassword.error = "Please enter your password"
            etPassword.requestFocus()
            return
        }
        
        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            etPassword.requestFocus()
            return
        }
        
        // Show loading
        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show()
        
        // Simulate API call with multiple valid users
        Handler(Looper.getMainLooper()).postDelayed({
            // Extract user display name from email or username
            val displayName = extractDisplayName(username)
            
            // Valid credentials (you can login with any of these)
            val validCredentials = mapOf(
                // Admin accounts
                "admin" to "admin123",
                "admin@wavesoffood.com" to "admin123",
                
                // Regular users
                "user1" to "pass123",
                "user@example.com" to "password123",
                
                // Sample email accounts
                "john.doe@gmail.com" to "password123",
                "jane.smith@yahoo.com" to "mypassword",
                "budi.wilson@gmail.com" to "123456",
                "sari.dewi@outlook.com" to "password",
                "ahmad.rizki@gmail.com" to "qwerty123",
                
                // Demo accounts
                "demo" to "demo123",
                "test" to "test123",
                "guest" to "guest123"
            )
            
            if (validCredentials[username] == password) {
                val welcomeMessage = when {
                    username.contains("admin") -> "Welcome back, Admin!"
                    username.contains("@") -> "Welcome, $displayName!"
                    else -> "Login successful!"
                }
                
                // Save user session
                val user = User(
                    uid = generateUniqueUserId(if (username.contains("@")) username else "$username@example.com"),
                    name = displayName,
                    email = if (username.contains("@")) username else "$username@example.com",
                    phone = "",
                    address = ""
                )
                UserSessionManager.saveUserSession(this, user)
                
                Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show()
                navigateToMain(displayName, true)
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_LONG).show()
            }
        }, 1000)
    }
    
    /**
     * Generate consistent user ID based on email only
     * Same email = same UID every time (no timestamp)
     * This ensures user data persists across login sessions
     */
    private fun generateUniqueUserId(email: String): String {
        // Use only email hash for consistency - no timestamp!
        val emailHash = email.lowercase().hashCode().toString().replace("-", "")
        return "user_$emailHash"
    }
    
    /**
     * Extract display name from email or username
     * Examples: 
     * - "budi.wilson@gmail.com" -> "Budi Wilson"
     * - "john.doe@example.com" -> "John Doe" 
     * - "admin" -> "Admin"
     */
    private fun extractDisplayName(input: String): String {
        return when {
            // If it's an email
            input.contains("@") -> {
                val localPart = input.substringBefore("@")
                // Convert dots/underscores to spaces and capitalize each word
                localPart.replace(".", " ")
                    .replace("_", " ")
                    .split(" ")
                    .joinToString(" ") { word ->
                        word.replaceFirstChar { it.uppercase() }
                    }
            }
            // If it's just a username
            else -> {
                input.replaceFirstChar { it.uppercase() }
            }
        }
    }
    
    private fun navigateToMain(username: String = "Guest", isLoggedIn: Boolean = false) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
