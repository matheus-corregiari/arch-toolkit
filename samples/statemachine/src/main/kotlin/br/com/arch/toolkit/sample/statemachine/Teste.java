package br.com.arch.toolkit.sample.statemachine;

import br.com.arch.toolkit.statemachine.StateMachine;
import br.com.arch.toolkit.statemachine.ViewStateMachine;

public class Teste {

    private void setupStateMachine() {

        final ViewStateMachine stateMachine = new ViewStateMachine();

        final ViewStateMachine.State state = stateMachine.newStateInstance();
        state.gones();
        state.invisibles();
        state.visibles();
        state.enables();
        state.disables();

        state.onEnter(() -> {
            // QUERO QUE SEJA ASSIM
        });

        stateMachine
                .addState(0, state)
                .addState(0, state)
                .addState(0, state)
                .addState(0, state);

        final StateMachine.Config config = stateMachine.getConfig();
        config.setOnChangeState(integer -> {
            // Eita
        });
        config.setInitialState(0);

        stateMachine.start();
    }
}
