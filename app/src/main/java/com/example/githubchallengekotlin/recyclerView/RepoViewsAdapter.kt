package com.example.githubchallengekotlin.recyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.githubchallengekotlin.R
import com.example.githubchallengekotlin.model.RepoOrg

class RepoViewsAdapter internal constructor(private val dataSet : MutableList<RepoOrg>) : RecyclerView.Adapter<RepoViewsAdapter.ViewHolder>(){

    companion object{
        private var clickListener : OnClickListener ?= null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchedOrg = dataSet[position]
        val companyName = holder.companyName
        val repoName = holder.repoName
        val starCount = holder.starCount

        repoName.text = searchedOrg.repoName
        val companyText = searchedOrg.companyName?.split("/")
        companyName.text = companyText!![0]
        starCount.text = searchedOrg.starCount.toString()

        holder.setCurrentOrg(searchedOrg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.repo_view, parent, false))
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder constructor(itemView : View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        private lateinit var currentOrg : RepoOrg
        var repoLayout : CardView
        var companyName : TextView
        var repoName : TextView
        var starCount : TextView

        init {
            super.itemView
            repoLayout = itemView.findViewById(R.id.repo_card_layout)
            companyName = itemView.findViewById(R.id.repo_company_display)
            repoName = itemView.findViewById(R.id.repo_name_display)
            starCount = itemView.findViewById(R.id.repo_stars_display)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view : View) {
            clickListener!!.onRepoClick(adapterPosition, currentOrg, view)
        }

        fun setCurrentOrg(searchedOrg: RepoOrg) {
            currentOrg = searchedOrg
        }

    }

    fun setOnClickListener(cListener : OnClickListener){
        clickListener = cListener
    }

    interface OnClickListener{
        fun onRepoClick(position : Int, org : RepoOrg, v : View)
    }

}