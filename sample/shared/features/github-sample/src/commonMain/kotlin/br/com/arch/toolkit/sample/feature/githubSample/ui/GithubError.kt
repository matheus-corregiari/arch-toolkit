package br.com.arch.toolkit.sample.feature.githubSample.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.com.arch.toolkit.sample.design.AppText
import br.com.arch.toolkit.sample.design.text
import br.com.arch.toolkit.sample.github.shared.designSystem.component.ErrorState
import br.com.arch.toolkit.sample.github.shared.structure.repository.GithubFailure

@Composable
fun GithubError(failure: GithubFailure, retry: () -> Unit) {
    val message = when (failure) {
        GithubFailure.CONNECTION -> AppText.CONNECTION_ERROR
        GithubFailure.RATE_LIMIT -> AppText.RATE_LIMIT_ERROR
        GithubFailure.NOT_FOUND -> AppText.NOT_FOUND_ERROR
        GithubFailure.INVALID_RESPONSE -> AppText.RESPONSE_ERROR
    }
    ErrorState(Modifier, text(message), retryLabel = text(AppText.RETRY), retry = retry)
}
