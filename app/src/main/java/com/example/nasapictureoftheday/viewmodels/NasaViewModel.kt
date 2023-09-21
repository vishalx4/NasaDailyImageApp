package com.example.nasapictureoftheday.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasapictureoftheday.R
import com.example.nasapictureoftheday.RetrofitInstance
import com.example.nasapictureoftheday.data.models.NasaModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class NasaViewModel(private var context: Context?): ViewModel() {

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val _errorMessage: MutableLiveData<String> = MutableLiveData("")
    val errorMessage
        get() = _errorMessage

    private val _nasaModel: MutableLiveData<NasaModel> = MutableLiveData()
    val nasaModel
        get() = _nasaModel

    private val _state: MutableLiveData<State> = MutableLiveData()
    val state
        get() = _state

    companion object {
        private const val API_KEY = "rcGUaAlI9dcHslUhPbFEFv838kZKiwm32kE9Wefb"
    }

    // initial request
    init {
        sendRequestForTheDate(Calendar.getInstance().time.time)
    }

    // request for the date
    fun sendRequestForTheDate(date: Long) {
        viewModelScope.launch {
            val todayDate = sdf.format(date)
            val response = try {
                state.postValue(State.IN_PROGRESS)
                RetrofitInstance.api.getNasaDailyImageData(
                    endDate = todayDate,
                    startDate = todayDate,
                    key = API_KEY
                )
            } catch (e: SocketTimeoutException) {
                _errorMessage.postValue(context!!.getString(R.string.str_timeout_please_retry))
                return@launch
            } catch (e: HttpException) {
                _errorMessage.postValue(e.message())
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                response.body()?.let {
                    _state.postValue(State.SUCCESS)
                    if (it.isNotEmpty()) {
                        nasaModel.postValue(it[0])
                    }
                }
            } else {
                try {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    val msg = jObjError.getString("msg")
                    _errorMessage.value = msg
                } catch (e: Exception) {
                    _errorMessage.postValue(e.message)
                }
                _state.postValue(State.FAILED)
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        context = null
    }

}


enum class State {
    IN_PROGRESS,
    SUCCESS,
    FAILED
}





