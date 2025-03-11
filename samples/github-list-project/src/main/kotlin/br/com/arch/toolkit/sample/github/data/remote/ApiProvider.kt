package br.com.arch.toolkit.sample.github.data.remote

import br.com.arch.toolkit.lumber.Lumber
import br.com.arch.toolkit.sample.github.BuildConfig
import br.com.arch.toolkit.sample.github.data.remote.api.GithubApi
import br.com.arch.toolkit.splinter.factory.SplinterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal object ApiProvider {

    internal val githubApi: GithubApi by lazy { retrofit.create(GithubApi::class.java) }

    private val moshi: Moshi by lazy { Moshi.Builder().build() }
    private val okHttp: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor { Lumber.tag("OkHttp").info(it) }.setLevel(Level.BODY)
        OkHttpClient.Builder().addInterceptor(logging).build()
    }
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            /* Add default configurations */
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttp)
            .baseUrl(BuildConfig.BASE_URL)
            /* This is for receiving as proper observable directly from retrofit interface */
            .addCallAdapterFactory(SplinterFactory())
            /* Finish Retrofit creation */
            .build()
    }
}
