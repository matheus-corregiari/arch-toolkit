package br.com.arch.toolkit.splinter.factory

import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.livedata.response.ResponseLiveData
import br.com.arch.toolkit.splinter.oneShotDonatello
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import br.com.arch.toolkit.splinter.Splinter as SplinterExec

/**
 *
 */
annotation class SplinterConfig(val tag: String = "")

/**
 *
 */
class SplinterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {

        val returnClass = getRawType(returnType).takeIf {
            it in listOf(
                ResponseFlow::class.java,
                ResponseLiveData::class.java,
                SplinterExec::class.java,
            )
        } ?: return null

        val type: Type =
            when (val upper = getParameterUpperBound(0, returnType as ParameterizedType)) {
                is ParameterizedType -> upper
                is Class<*> -> upper
                is Type -> upper

                else -> error("Resource must be Parameterized")
            }

        val tag = annotations.filterIsInstance(SplinterConfig::class.java).firstOrNull()?.tag
            .takeUnless(String?::isNullOrBlank) ?: ""

        return when (returnClass) {

            /**
             *
             */
            ResponseFlow::class.java -> {
                Adapter.AsFlow(
                    tag = tag,
                    responseType = getRawType(type),
                    kClass = returnClass,
                )
            }

            /**
             *
             */
            ResponseLiveData::class.java -> {
                Adapter.AsLiveData(
                    tag = tag,
                    responseType = getRawType(type),
                    kClass = returnClass,
                )
            }

            /**
             *
             */
            SplinterExec::class.java -> {
                Adapter.AsSplinter(
                    tag = tag,
                    responseType = getRawType(type),
                    kClass = returnClass,
                )
            }

            /**
             *
             */
            else -> null
        }
    }
}

private sealed class Adapter<T, R>(
    private val tag: String,
    private val responseType: Type,
    private val kClass: Class<T>
) : CallAdapter<T, R> {

    class AsSplinter<T : Any>(tag: String, responseType: Type, kClass: Class<T>) :
        Adapter<T, SplinterExec<T>>(tag, responseType, kClass) {
        override fun adapt(call: Call<T>) = executeWithSplinter(call)

    }

    class AsLiveData<T : Any>(tag: String, responseType: Type, kClass: Class<T>) :
        Adapter<T, ResponseLiveData<T>>(tag, responseType, kClass) {
        override fun adapt(call: Call<T>) = executeWithSplinter(call).liveData

    }

    class AsFlow<T : Any>(tag: String, responseType: Type, kClass: Class<T>) :
        Adapter<T, ResponseFlow<T>>(tag, responseType, kClass) {
        override fun adapt(call: Call<T>) = executeWithSplinter(call).flow

    }

    override fun responseType() = responseType

    protected fun executeWithSplinter(call: Call<T>) = oneShotDonatello(tag) {
        val response = makeRequest(call)
        if (response.isSuccessful) {
            requireNotNull(response.body()) {
                "Unable to get response Body from response: ${response.raw().body?.string()}"
            }
        } else {
            error("Error executing request ${response.message()}")
        }
    }.onCancel {
        if (call.isExecuted.not() && call.isCanceled.not()) {
            call.cancel()
        }
    }

    @Throws(IOException::class)
    private fun <T> makeRequest(call: Call<T>): Response<T> {
        return if (call.isExecuted) {
            call.clone().execute()
        } else {
            call.execute()
        }
    }
}
