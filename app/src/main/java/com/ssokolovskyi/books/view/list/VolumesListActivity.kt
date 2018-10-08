package com.ssokolovskyi.books.view.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.ssokolovskyi.books.R
import com.ssokolovskyi.books.view.list.SearchData.EmptySearch
import com.ssokolovskyi.books.view.list.SearchData.SearchValue
import com.ssokolovskyi.books.view.list.ViewData.*
import com.ssokolovskyi.books.view.list.VolumesListActivity.EmptyStateMode.*
import com.ssokolovskyi.books.view.list.adapter.VolumesListAdapter

class VolumesListActivity : AppCompatActivity() {

    companion object {
        private const val SEARCH_ARG = "SEARCH_ARG"
    }

    private lateinit var viewModel: VolumesListViewModel
    private lateinit var volumesListAdapter: VolumesListAdapter
    private lateinit var search: EditText
    private lateinit var emptyState: TextView
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volumes_list)
        viewModel = ViewModelProviders.of(this).get(VolumesListViewModel::class.java)
        emptyState = findViewById(R.id.empty_state)
        recyclerView = findViewById(R.id.recycler_view)
        search = findViewById(R.id.search)
        initAdapter()
        initToolbar()
        observeData()
        restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_ARG, search.text.toString().trim())
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        search.setText(savedInstanceState?.getString(SEARCH_ARG))
    }

    private fun initAdapter() {
        volumesListAdapter = VolumesListAdapter { viewModel.onItemClicked(it) }
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.adapter = volumesListAdapter
    }

    private fun initToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrBlank()) viewModel.onSearch(EmptySearch)
                else viewModel.onSearch(SearchValue(s.toString()))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun observeData() {
        viewModel.getViewData().observe(this, Observer {
            when (it) {
                is ListData -> {
                    setEmptyStateVisible(Hidden)
                    volumesListAdapter.submitList(it.pagedList)
                }
                NoData -> setEmptyStateVisible(EmptyFilter)
                NoNetwork -> setEmptyStateVisible(NetworkUnavailable)
            }
        })
    }

    private fun setEmptyStateVisible(mode: EmptyStateMode) {
        when (mode) {
            NetworkUnavailable -> {
                emptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                emptyState.setText(R.string.network_unavailable)
            }
            EmptyFilter -> {
                emptyState.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
                emptyState.setText(R.string.empty_state)
            }
            Hidden -> {
                emptyState.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }
    }

    private sealed class EmptyStateMode {
        object NetworkUnavailable : EmptyStateMode()
        object EmptyFilter : EmptyStateMode()
        object Hidden : EmptyStateMode()
    }
}
