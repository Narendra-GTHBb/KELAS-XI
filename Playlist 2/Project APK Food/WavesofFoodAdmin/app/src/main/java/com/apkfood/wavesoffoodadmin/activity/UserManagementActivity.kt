package com.apkfood.wavesoffoodadmin.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.apkfood.wavesoffoodadmin.R
import com.apkfood.wavesoffoodadmin.adapter.UserAdapter
import com.apkfood.wavesoffoodadmin.model.User
import com.apkfood.wavesoffoodadmin.viewmodel.UserManagementViewModel

class UserManagementActivity : AppCompatActivity() {
    
    private lateinit var userViewModel: UserManagementViewModel
    private lateinit var userAdapter: UserAdapter
    
    // Views
    private lateinit var etSearch: TextInputEditText
    private lateinit var chipGroupFilter: ChipGroup
    private lateinit var chipAllUsers: Chip
    private lateinit var chipActiveUsers: Chip
    private lateinit var chipBannedUsers: Chip
    private lateinit var rvUsers: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var loadingOverlay: LinearLayout
    private lateinit var fabBulkActions: FloatingActionButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)
        
        initViews()
        setupToolbar()
        setupRecyclerView()
        setupViewModel()
        setupSearchAndFilter()
        setupClickListeners()
    }
    
    private fun initViews() {
        etSearch = findViewById(R.id.etSearch)
        chipGroupFilter = findViewById(R.id.chipGroupFilter)
        chipAllUsers = findViewById(R.id.chipAllUsers)
        chipActiveUsers = findViewById(R.id.chipActiveUsers)
        chipBannedUsers = findViewById(R.id.chipBannedUsers)
        rvUsers = findViewById(R.id.rvUsers)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        loadingOverlay = findViewById(R.id.loadingOverlay)
        fabBulkActions = findViewById(R.id.fabBulkActions)
    }
    
    private fun setupToolbar() {
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }
    
    private fun setupRecyclerView() {
        userAdapter = UserAdapter(
            onUserClick = { user ->
                showUserDetailsDialog(user)
            },
            onUserAction = { user, action ->
                when (action) {
                    "ban" -> showBanUserDialog(user)
                    "unban" -> showUnbanUserDialog(user)
                }
            }
        )
        
        rvUsers.apply {
            layoutManager = LinearLayoutManager(this@UserManagementActivity)
            adapter = userAdapter
        }
    }
    
    private fun setupViewModel() {
        Log.d("UserManagement", "Setting up UserManagementViewModel...")
        userViewModel = ViewModelProvider(this)[UserManagementViewModel::class.java]
        
        userViewModel.users.observe(this) { users ->
            Log.d("UserManagement", "Received ${users.size} users from ViewModel")
            users.forEach { user ->
                Log.d("UserManagement", "User: ${user.getDisplayName()} - ${user.email}")
            }
            if (etSearch.text.toString().isEmpty() && chipAllUsers.isChecked) {
                userAdapter.submitList(users)
                showEmptyState(users.isEmpty())
            }
        }
        
        userViewModel.searchResults.observe(this) { users ->
            userAdapter.submitList(users)
            showEmptyState(users.isEmpty())
        }
        
        userViewModel.isLoading.observe(this) { isLoading ->
            loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
            swipeRefresh.isRefreshing = isLoading
        }
        
        userViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        }
        
        userViewModel.operationResult.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Operation completed successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Operation failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupSearchAndFilter() {
        // Search functionality
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    applyCurrentFilter()
                } else {
                    userViewModel.searchUsers(query)
                }
            }
        })
        
        // Filter chips
        chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                applyCurrentFilter()
            }
        }
    }
    
    private fun setupClickListeners() {
        swipeRefresh.setOnRefreshListener {
            userViewModel.loadUsers()
        }
        
        fabBulkActions.setOnClickListener {
            showBulkActionsDialog()
        }
    }
    
    private fun applyCurrentFilter() {
        when (chipGroupFilter.checkedChipId) {
            R.id.chipAllUsers -> {
                val allUsers = userViewModel.users.value ?: emptyList()
                userViewModel.updateSearchResults(allUsers)
            }
            R.id.chipActiveUsers -> {
                userViewModel.filterUsers(showBannedOnly = false)
            }
            R.id.chipBannedUsers -> {
                userViewModel.filterUsers(showBannedOnly = true)
            }
        }
    }
    
    private fun showEmptyState(isEmpty: Boolean) {
        rvUsers.visibility = if (isEmpty) View.GONE else View.VISIBLE
        layoutEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }
    
    private fun showUserDetailsDialog(user: User) {
        userViewModel.getUserDetails(user.id)
        
        val dialogView = layoutInflater.inflate(R.layout.dialog_user_details, null)
        
        // TODO: Populate dialog with user details
        // This would include order history, spending patterns, etc.
        
        MaterialAlertDialogBuilder(this)
            .setTitle("User Details")
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .setNeutralButton("View Orders") { _, _ ->
                // TODO: Navigate to user's order history
            }
            .show()
    }
    
    private fun showBanUserDialog(user: User) {
        val input = TextInputEditText(this)
        input.hint = "Reason for ban (optional)"
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Ban User")
            .setMessage("Are you sure you want to ban ${user.name}?")
            .setView(input)
            .setPositiveButton("Ban") { _, _ ->
                val reason = input.text.toString().trim()
                userViewModel.banUser(user.id, reason)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showUnbanUserDialog(user: User) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Unban User")
            .setMessage("Are you sure you want to unban ${user.name}?")
            .setPositiveButton("Unban") { _, _ ->
                userViewModel.unbanUser(user.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showBulkActionsDialog() {
        val actions = arrayOf("Export User Data", "Send Bulk Notification", "Generate Report")
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Bulk Actions")
            .setItems(actions) { _, which ->
                when (which) {
                    0 -> {
                        // TODO: Export user data
                        Toast.makeText(this, "Export functionality coming soon", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        // TODO: Navigate to bulk notification screen
                        Toast.makeText(this, "Bulk notification coming soon", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        // TODO: Generate user report
                        Toast.makeText(this, "Report generation coming soon", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }
}
