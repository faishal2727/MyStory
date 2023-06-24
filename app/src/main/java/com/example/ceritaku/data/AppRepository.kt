package com.example.ceritaku.dataimport androidx.lifecycle.LiveDataimport androidx.lifecycle.liveDataimport androidx.paging.*import com.example.ceritaku.api.ApiServiceimport com.example.ceritaku.model.*import com.example.ceritaku.preference.PreferenceLoginimport okhttp3.MultipartBodyimport okhttp3.RequestBodyclass AppRepository(private val prefe: PreferenceLogin, private val apiService: ApiService) {    fun autLogin(email: String, password: String): LiveData<Result<ResponseLogin>> = liveData {        emit(Result.Loading)        try {            val response = apiService.login(email, password)            if (response.error) {                emit(Result.Error(response.message))            } else {                emit(Result.Success(response))            }        } catch (e: Exception) {            emit(Result.Error(e.message.toString()))        }    }    fun authRegister(        name: String,        email: String,        password: String    ): LiveData<Result<ResponseRegister>> = liveData {        emit(Result.Loading)        try {            val response = apiService.register(name, email, password)            if (response.error) {                emit(Result.Error(response.message))            } else {                emit(Result.Success(response))            }        } catch (e: Exception) {            emit(Result.Error(e.message.toString()))        }    }    fun getListStories(): LiveData<PagingData<Story>> {        @OptIn(ExperimentalPagingApi::class)        return Pager(            config = PagingConfig(                pageSize = 5            ),            pagingSourceFactory = {                PagingSourceStory(prefe, apiService)            }        ).liveData    }    fun getStoriesMap(): LiveData<Result<ResponseStory>> = liveData {        emit(Result.Loading)        try {            val response = apiService.getStory(                token = "Bearer ${prefe.getUser().token}",                page = 1,                size = 100,                location = 1            )            if (response.error) {                emit(Result.Error(response.message))            } else {                emit(Result.Success(response))            }        } catch (e: Exception) {            emit(Result.Error(e.message.toString()))        }    }    fun uploadStory(        imageFile: MultipartBody.Part,        desc: RequestBody,        lat: Double,        lon: Double    ): LiveData<Result<UploadStoryResponse>> = liveData {        emit(Result.Loading)        try {            val response = apiService.uploadStory(                token = "Bearer ${prefe.getUser().token}",                file = imageFile,                description = desc,                lat = lat,                lon = lon            )            if (response.error) {                emit(Result.Error(response.message))            } else {                emit(Result.Success(response))            }        } catch (e: Exception) {            emit(Result.Error(e.message.toString()))        }    }    fun uploadStoryNotLoc(        imageFile: MultipartBody.Part,        desc: RequestBody    ): LiveData<Result<UploadStoryResponse>> = liveData {        emit(Result.Loading)        try {            val response = apiService.uploadStoryNotLoc(                token = "Bearer ${prefe.getUser().token}",                file = imageFile,                description = desc,            )            if (response.error) {                emit(Result.Error(response.message))            } else {                emit(Result.Success(response))            }        } catch (e: Exception) {            emit(Result.Error(e.message.toString()))        }    }    companion object {        @Volatile        private var instance: AppRepository? = null        fun getInstance(            prefe: PreferenceLogin,            apiService: ApiService        ): AppRepository = instance ?: synchronized(this) {            instance ?: AppRepository(prefe, apiService)        }.also { instance = it }    }}