package br.com.arch.toolkit.statemachine;

import android.support.annotation.NonNull;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of [StateMachine]
 * This implementation uses View Visibility to make State transitions
 */
public final class ViewStateMachine extends StateMachine<ViewStateMachine.State> {

    @Override
    @NonNull
    public State newStateInstance() {
        return new State();
    }

    @Override
    protected void performChangeState(@NonNull final State state) {

        // Visibility
        for (final View view : state.gones) {
            if (view == null) continue;
            view.setVisibility(View.GONE);
        }

        for (final View view : state.visibles) {
            if (view == null) continue;
            view.setVisibility(View.VISIBLE);
        }

        for (final View view : state.invisibles) {
            if (view == null) continue;
            view.setVisibility(View.INVISIBLE);
        }

        // Enable
        for (final View view : state.enables) {
            if (view == null) continue;
            view.setEnabled(true);
        }

        for (final View view : state.disables) {
            if (view == null) continue;
            view.setEnabled(false);
        }
    }

    public static final class State extends StateMachine.State {

        private State() {
        }

        @NonNull
        private final List<View> visibles = new ArrayList<>();
        @NonNull
        private final List<View> gones = new ArrayList<>();
        @NonNull
        private final List<View> invisibles = new ArrayList<>();
        @NonNull
        private final List<View> enables = new ArrayList<>();
        @NonNull
        private final List<View> disables = new ArrayList<>();

        public final State visibles(@NonNull final View... views) {
            visibles.addAll(Arrays.asList(views));
            return this;
        }

        public final State invisibles(@NonNull final View... views) {
            invisibles.addAll(Arrays.asList(views));
            return this;
        }

        public final State gones(@NonNull final View... views) {
            gones.addAll(Arrays.asList(views));
            return this;
        }

        public final State enables(@NonNull final View... views) {
            enables.addAll(Arrays.asList(views));
            return this;
        }

        public final State disables(@NonNull final View... views) {
            disables.addAll(Arrays.asList(views));
            return this;
        }
    }
}