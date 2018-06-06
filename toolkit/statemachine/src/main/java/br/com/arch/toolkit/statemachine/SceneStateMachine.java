package br.com.arch.toolkit.statemachine;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.ViewGroup;

/**
 * Implementation of [StateMachine]
 * This implementation uses the Scene framework to make State transitions
 */
public final class SceneStateMachine extends StateMachine<SceneStateMachine.State> {

    @NonNull
    @Override
    public State newStateInstance() {
        return new State();
    }

    @Override
    protected void performChangeState(@NonNull final State state) {
        if (state.scene == null) return;
        final boolean attached = state.scene.getSceneRoot().isAttachedToWindow();

        if (state.transition == null || !attached) {
            state.scene.enter();
        } else {
            TransitionManager.go(state.scene, state.transition.clone());
        }
    }

    public final static class State extends StateMachine.State {

        private State() {
        }

        @Nullable
        private Scene scene;
        @Nullable
        private Transition transition;

        public State scene(@LayoutRes final int sceneLayout, @NonNull final ViewGroup container) {
            scene = Scene.getSceneForLayout(container, sceneLayout, container.getContext());
            return this;
        }

        public State scene(@NonNull final Scene scene) {
            this.scene = scene;
            return this;
        }

        public State transition(@NonNull final Transition transition) {
            this.transition = transition;
            return this;
        }
    }
}
