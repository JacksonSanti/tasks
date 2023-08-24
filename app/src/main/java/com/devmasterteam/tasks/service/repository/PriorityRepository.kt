package com.devmasterteam.tasks.service.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.devmasterteam.tasks.R
import com.devmasterteam.tasks.service.listener.APIListener
import com.devmasterteam.tasks.service.model.PriorityModel
import com.devmasterteam.tasks.service.repository.local.TaskDatabase
import com.devmasterteam.tasks.service.repository.remote.PriorityService
import com.devmasterteam.tasks.service.repository.remote.RetrofitClient

class PriorityRepository(context: Context): BaseRepository(context) {

    private val remote = RetrofitClient.getService(PriorityService::class.java)
    private val database = TaskDatabase.getDatabase(context).priorityDAO()


    companion object {
        private val cache = mutableMapOf<Int, String>()

        fun getDescriptionCached(id: Int): String {
           return cache[id] ?: ""
        }

        fun setDescriptionCached(id: Int, str: String){
            cache[id] = str
        }

    }


    fun getDescription(id: Int): String{

        val cached = PriorityRepository.getDescriptionCached(id)

        return if(cached == "") {
            val description = database.getDescription(id)
            PriorityRepository.setDescriptionCached(id,description)
            description
        }else {
            cached
        }
    }



    @RequiresApi(Build.VERSION_CODES.M)
    fun list(listener: APIListener<List<PriorityModel>>){
        if (!isConnectionAvailable()){
            listener.onFailure(context.getString(R.string.ERROR_INTERNET_CONNECTION))
            return
        }
        executeCall(remote.list(),listener)
    }



    fun list():List<PriorityModel>{
        return database.list()
    }



    fun save(list: List<PriorityModel>) {
        database.clear()
        database.save(list)
    }



}