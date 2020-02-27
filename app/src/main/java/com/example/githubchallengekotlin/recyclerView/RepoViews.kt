package com.example.githubchallengekotlin.recyclerView

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubchallengekotlin.MainActivity
import com.example.githubchallengekotlin.R
import com.example.githubchallengekotlin.model.RepoOrg
import com.example.githubchallengekotlin.recyclerView.RepoViewsAdapter.OnClickListener

class RepoViews (private val mContext : Context) {
    private val activity : MainActivity = mContext as MainActivity
    private lateinit var adapter : RepoViewsAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var recyclerOrgList : MutableList<RepoOrg>

    fun topRepos(){
        recyclerView = activity.findViewById(R.id.repoRecyclerView)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(mContext)
        recyclerOrgList = activity.sortedRepos()
        adapter = RepoViewsAdapter(recyclerOrgList)
        recyclerView.recycledViewPool.setMaxRecycledViews(0,0)
        recyclerView.adapter = adapter
        setListener()
    }

    private fun setListener(){
        adapter.setOnClickListener(object : OnClickListener {
            override fun onRepoClick(position: Int, org: RepoOrg, v: View) {
                activity.openGithubLink(org.repoUrl!!)
            }
        })
    }
}