package io.github.whimthen.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface HistoryService extends PersistentStateComponent<HistoryService.State> {

    static HistoryService getInstance() {
        return ServiceManager.getService(HistoryService.class);
    }

    class State {
        public List<String> histories;

        public State add(String wsUrl) {
            if (Objects.isNull(this.histories)) {
                this.histories = new ArrayList<>();
            }
            this.histories.add(wsUrl);
            return this;
        }
    }

    State get();

    void save(String wsUrl);

    boolean isEmpty();

}
