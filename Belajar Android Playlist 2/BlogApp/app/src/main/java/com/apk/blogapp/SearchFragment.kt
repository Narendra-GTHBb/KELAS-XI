package com.apk.blogapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class SearchFragment : Fragment() {
    
    private lateinit var searchView: SearchView
    private lateinit var tvSearchPlaceholder: TextView
    private lateinit var recyclerViewSearch: RecyclerView
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupSearch()
    }
    
    private fun initViews(view: View) {
        searchView = view.findViewById(R.id.searchView)
        tvSearchPlaceholder = view.findViewById(R.id.tvSearchPlaceholder)
        recyclerViewSearch = view.findViewById(R.id.recyclerViewSearch)
        
        // Show placeholder for now
        tvSearchPlaceholder.visibility = View.VISIBLE
        recyclerViewSearch.visibility = View.GONE
    }
    
    private fun setupSearch() {
        // TODO: Implement search functionality
        tvSearchPlaceholder.text = "Search for articles, topics, or authors..."
    }
}