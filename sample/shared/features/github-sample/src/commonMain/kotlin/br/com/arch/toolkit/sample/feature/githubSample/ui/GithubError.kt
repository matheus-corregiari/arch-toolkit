package br.com.arch.toolkit.sample.feature.githubSample.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubFailure

@Composable
fun GithubError(failure: GithubFailure, retry: () -> Unit) {
    val message = when (failure) {
        GithubFailure.CONNECTION -> AppText.CONNECTION_ERROR
        GithubFailure.RATE_LIMIT -> AppText.RATE_LIMIT_ERROR
        GithubFailure.NOT_FOUND -> AppText.NOT_FOUND_ERROR
        GithubFailure.INVALID_RESPONSE -> AppText.RESPONSE_ERROR
    }
    Column {
        Text(text(message))
        Button(onClick = retry) { Text(text(AppText.RETRY)) }
    }
}
