package com.example.githubchallengekotlin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.githubchallengekotlin.apiConnections.ApiCall
import com.example.githubchallengekotlin.model.RepoOrg
import com.example.githubchallengekotlin.recyclerView.RepoViews
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.Response
import java.lang.Exception
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var searchButton : Button ?= null
    private var orgName : String ?= null
    private var publicRepoCount = -1
    private var repoPageCount = 1
    private val orgList : MutableList<RepoOrg> = ArrayList()

    private val baseUrl = "https://api.github.com/orgs/"

    private lateinit var repoViews : RepoViews

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchButton = findViewById(R.id.searchButton)
        repoViews = RepoViews(this)
        searchButtonListener()
    }


    private fun searchButtonListener(){
        searchButton?.setOnClickListener {
            orgName = (findViewById<EditText>(R.id.searchBar).text.toString()).toLowerCase()
            findViewById<FrameLayout>(R.id.reposUpdateFrame).bringToFront()
            resetApiData()
            makeApiCall(getBaseUrl(), false)
        }
    }


    private fun resetApiData() {
        publicRepoCount = -1
        repoPageCount = 1
        orgList.clear()
    }


    private fun getBaseUrl() : String{
        return baseUrl + orgName
    }


    private fun makeApiCall(url : String, getList : Boolean){
        Observable.defer{
            try{
                val response : Response = ApiCall(url).getResponse()
                Observable.just(response)
            } catch (e : Exception){
                Observable.error<Exception>(e)
            }
        }.subscribeOn(Schedulers.io())
            //.toFlowable(BackpressureStrategy.DROP)
            .subscribe (
                { onNext -> if(!getList) getRepoCount(onNext as Response) else parseRepoResponse(onNext as Response)},
                { onError -> println(onError) },
                { if(publicRepoCount > 0) {
                    val tempUrl = "${getBaseUrl()}/repos?per_page=100;page=$repoPageCount"
                    repoPageCount++
                    publicRepoCount -= 100
                    makeApiCall(tempUrl, true)
                } else setRepos()
                }
            )
    }


    private fun getRepoCount(response: Response) {
        val moshi : Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val jsonAdapter = moshi.adapter(RepoOrg::class.java)
        val repoOrgForCount = jsonAdapter.fromJson(response.body!!.string())
        try{
            if(repoOrgForCount?.repoCount!! != 0) {
                publicRepoCount = repoOrgForCount.repoCount!!
                updateUi(View.VISIBLE,View.GONE)
            } else {
                updateUi(8, 0)
                findViewById<FrameLayout>(R.id.reposUpdateFrame).bringToFront()
            }
        } catch (e : Exception) {
            updateUi(View.GONE, View.VISIBLE)
        }
    }

    private fun updateUi(foundRepos : Int, noReposFound : Int){
        try{
            runOnUiThread {
                run {
                    foundReposVisibility(foundRepos)
                    noReposFoundVisibility(noReposFound)
                }
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }


    private fun parseRepoResponse(response: Response) {
        val resp = response.body!!.string()
        val moshi : Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val type = Types.newParameterizedType(List::class.java, RepoOrg::class.java)
        val jsonAdapter : JsonAdapter<List<RepoOrg>> = moshi.adapter(type)

        try{
            val list : List<RepoOrg>? = jsonAdapter.fromJson(resp)
            orgList.addAll((list!!))
        } catch (e : Exception){
            e.printStackTrace()
        }
    }


    private fun setRepos() {
        sortByStars()
        displayRepoData()
    }


    private fun sortByStars() {
        val compareByStars = Comparator<RepoOrg> { org1, org2 -> (org1.starCount!!).compareTo(org2.starCount!!) }
        Collections.sort(orgList, compareByStars.reversed())
    }


    private fun displayRepoData(){
        runOnUiThread{
            foundReposVisibility(View.GONE)
            findViewById<RecyclerView>(R.id.repoRecyclerView).bringToFront()
            repoViews.topRepos()
        }
    }


    private fun foundReposVisibility(setting : Int){
        findViewById<LinearLayout>(R.id.reposFound).visibility = setting
    }


    private fun noReposFoundVisibility(setting : Int){
        findViewById<TextView>(R.id.noReposFound).visibility = setting
    }


    fun sortedRepos() : MutableList<RepoOrg> {
        val tempList = mutableListOf<RepoOrg>()
        try{
            var topTen = 10
            if(orgList.size in 1 until (topTen-1)){
                topTen = publicRepoCount + 100
            }
            for(i in 0 until topTen){
                tempList.add(orgList[i])
            }
        } catch (e : Exception){
            updateUi(View.GONE, View.VISIBLE)
            e.printStackTrace()
        }
        return tempList
    }


    fun openGithubLink(url : String){
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}
