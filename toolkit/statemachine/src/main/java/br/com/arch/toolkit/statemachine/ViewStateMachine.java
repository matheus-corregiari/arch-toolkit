package br.com.arch.toolkit.statemachine;

import android.view.View;
import android.view.ViewStub;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

        // Handle view references
        final View rootView = state.rootView;
        if (rootView != null) {
            // Visibility
            for (@IdRes final Integer viewId : state.goneReferences) {
                final View view = rootView.findViewById(viewId);
                if (view == null) continue;
                view.setVisibility(View.GONE);
            }
            for (@IdRes final Integer viewId : state.visibleReferences) {
                final View view = rootView.findViewById(viewId);
                if (view == null) continue;
                if (view instanceof ViewStub) {
                    ((ViewStub) view).inflate();
                }
                view.setVisibility(View.VISIBLE);
            }
            for (@IdRes final Integer viewId : state.invisibleReferences) {
                final View view = rootView.findViewById(viewId);
                if (view == null) continue;
                view.setVisibility(View.INVISIBLE);
            }

            // Enable
            for (@IdRes final Integer viewId : state.enableReferences) {
                final View view = rootView.findViewById(viewId);
                if (view == null) continue;
                view.setEnabled(true);
            }

            for (@IdRes final Integer viewId : state.disableReferences) {
                final View view = rootView.findViewById(viewId);
                if (view == null) continue;
                view.setEnabled(false);
            }
        }
    }

    public static final class State extends StateMachine.State {

        @NonNull
        private final List<View> visibles = new ArrayList<>();
        @NonNull
        private final List<Integer> visibleReferences = new ArrayList<>();
        @NonNull
        private final List<View> gones = new ArrayList<>();
        @NonNull
        private final List<Integer> goneReferences = new ArrayList<>();
        @NonNull
        private final List<View> invisibles = new ArrayList<>();
        @NonNull
        private final List<Integer> invisibleReferences = new ArrayList<>();
        @NonNull
        private final List<View> enables = new ArrayList<>();
        @NonNull
        private final List<Integer> enableReferences = new ArrayList<>();
        @NonNull
        private final List<View> disables = new ArrayList<>();
        @NonNull
        private final List<Integer> disableReferences = new ArrayList<>();
        @Nullable
        private View rootView = null;

        private State() {
        }

        public final State root(@NonNull final View view) {
            rootView = view;
            return this;
        }

        public final State visibles(@NonNull final View... views) {
            visibles.addAll(Arrays.asList(views));
            return this;
        }

        public final State visibles(@NonNull @IdRes final Integer... ids) {
            visibleReferences.addAll(Arrays.asList(ids));
            return this;
        }

        public final State invisibles(@NonNull final View... views) {
            invisibles.addAll(Arrays.asList(views));
            return this;
        }

        public final State invisibles(@NonNull @IdRes final Integer... ids) {
            goneReferences.addAll(Arrays.asList(ids));
            return this;
        }

        public final State gones(@NonNull final View... views) {
            gones.addAll(Arrays.asList(views));
            return this;
        }

        public final State gones(@NonNull @IdRes final Integer... ids) {
            invisibleReferences.addAll(Arrays.asList(ids));
            return this;
        }

        public final State enables(@NonNull final View... views) {
            enables.addAll(Arrays.asList(views));
            return this;
        }

        public final State enables(@NonNull @IdRes final Integer... ids) {
            enableReferences.addAll(Arrays.asList(ids));
            return this;
        }

        public final State disables(@NonNull final View... views) {
            disables.addAll(Arrays.asList(views));
            return this;
        }

        public final State disables(@NonNull @IdRes final Integer... ids) {
            disableReferences.addAll(Arrays.asList(ids));
            return this;
        }

        @Override
        public State onEnter(@NonNull Callback callback) {
            return (State) super.onEnter(callback);
        }

        @Override
        public State onExit(@NonNull Callback callback) {
            return (State) super.onExit(callback);
        }
    }
}