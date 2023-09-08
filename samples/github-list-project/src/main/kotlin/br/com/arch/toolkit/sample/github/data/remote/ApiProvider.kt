package br.com.arch.toolkit.sample.github.data.remote

import br.com.arch.toolkit.sample.github.data.remote.api.GithubApi
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

object ApiProvider {

    internal val githubApi: GithubApi by lazy { retrofit.create(GithubApi::class.java) }

    private val moshi: Moshi by lazy { Moshi.Builder().build() }
    private val okHttp: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor { Timber.i(it) }.setLevel(Level.BODY)
        OkHttpClient.Builder().addInterceptor(logging).build()
    }
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttp)
//            .baseUrl(BuildConfig.BASE_URL)
            .build()
    }

}