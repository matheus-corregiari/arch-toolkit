package br.com.arch.toolkit.splinter.factory

import br.com.arch.toolkit.flow.ResponseFlow
import br.com.arch.toolkit.livedata.ResponseLiveData
import br.com.arch.toolkit.splinter.oneShotDonatello
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import br.com.arch.toolkit.splinter.Splinter as SplinterExec

/**
 * Annotation used to configure some behaviors inside generated splinter
 *
 * @param id - Used to identify the logs from this splinter in logcat
 * @param quiet - Used to turn on/off the logs inside logcat
 */
annotation class SplinterConfig(val id: String = "", val quiet: Boolean = false)

/**
 * This evil class is responsible to teach Retrofit how to deliver a Splinter, ResponseLiveData or ResponseFlow
 * directly from the retrofit api interface ^^
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

        val splinterConfig = annotations.filterIsInstance(SplinterConfig::class.java).firstOrNull()
            ?: SplinterConfig("", false)

        return when (returnClass) {
            /**
             * When you want to deliver the ResponseFlow
             */
            ResponseFlow::class.java -> {
                Adapter.AsFlow(
                    annotation = splinterConfig,
                    responseType = getRawType(type),
                    kClass = returnClass,
                )
            }

            /**
             * When you want to deliver the ResponseLiveData
             */
            ResponseLiveData::class.java -> {
                Adapter.AsLiveData(
                    annotation = splinterConfig,
                    responseType = getRawType(type),
                    kClass = returnClass,
                )
            }

            /**
             * When you want to deliver the Splinter
             */
            SplinterExec::class.java -> {
                Adapter.AsSplinter(
                    annotation = splinterConfig,
                    responseType = getRawType(type),
                    kClass = returnClass,
                )
            }

            /**
             * When this factory cannot determine any valid format to it,
             * so return null to follow the default configurations or another factories
             */
            else -> null
        }
    }
}

private sealed class Adapter<T, R>(
    private val id: String,
    private val quiet: Boolean,
    private val responseType: Type,
    private val kClass: Class<T>
) : CallAdapter<T, R> {

    class AsSplinter<T : Any>(annotation: SplinterConfig, responseType: Type, kClass: Class<T>) :
        Adapter<T, SplinterExec<T>>(annotation.id, annotation.quiet, responseType, kClass) {
        override fun adapt(call: Call<T>) = executeWithSplinter(call)
    }

    class AsLiveData<T : Any>(annotation: SplinterConfig, responseType: Type, kClass: Class<T>) :
        Adapter<T, ResponseLiveData<T>>(annotation.id, annotation.quiet, responseType, kClass) {
        override fun adapt(call: Call<T>) = executeWithSplinter(call).liveData
    }

    class AsFlow<T : Any>(annotation: SplinterConfig, responseType: Type, kClass: Class<T>) :
        Adapter<T, ResponseFlow<T>>(annotation.id, annotation.quiet, responseType, kClass) {
        override fun adapt(call: Call<T>) = executeWithSplinter(call).flow
    }

    override fun responseType() = responseType

    protected fun executeWithSplinter(call: Call<T>) = oneShotDonatello(id, quiet) {
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
