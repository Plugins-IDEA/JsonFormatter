package io.github.whimthen.service;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

@State(name = "io.github.whimthen.prettyJson.History", storages = {
        @Storage(value = "io.github.whimthen.prettyJson.xml")
})
public class HistoryServiceImpl implements HistoryService {

    private State state;

    @Override
    @NotNull
    public State get() {
        State state = Objects.isNull(this.state) ? new State() : this.state;
        if (Objects.isNull(state.histories))
            state.histories = new ArrayList<>();
        return state;
    }

    @Override
    public void save(String history) {
        if (get().histories.contains(history))
            return;
        loadState(get().add(history));
    }

    @Override
    public boolean isEmpty() {
        State state = getState();
        return Objects.isNull(state) || Objects.isNull(state.histories) || state.histories.isEmpty();
    }

    @Nullable
    @Override
    public State getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

}
