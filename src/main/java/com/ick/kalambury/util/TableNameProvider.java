package com.ick.kalambury.util;

import com.ick.kalambury.config.Parameters;
import com.ick.kalambury.service.TableKind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TableNameProvider {

    private final Map<TableKind, Parameters.GameConfig.TableNameConfig> nameConfigs;
    private final Map<TableKind, List<String>> names;

    @Autowired
    public TableNameProvider(Parameters parameters) {
        this.nameConfigs = parameters.getGameConfig().getTableNameConfig();
        this.names = new HashMap<>();
    }

    @PostConstruct
    private void init() {
        nameConfigs.forEach((key, value) -> names.put(key, new ArrayList<>()));
    }

    public synchronized String acquireName(TableKind kind) {
        int index = indexOfFirstEmptyElement(names.get(kind));
        Parameters.GameConfig.TableNameConfig config = nameConfigs.get(kind);
        String name = String.format("%s%d", config.getPrefix(), config.getBaseNumber() + index);
        names.get(kind).add(index, name);
        return name;
    }

    public synchronized void releaseName(TableKind kind, String name) {
        List<String> namesList = names.get(kind);
        for (int i = 0; i < namesList.size(); i++) {
            if (name.equals(namesList.get(i))) {
                namesList.set(i, null);
            }
        }
    }

    private int indexOfFirstEmptyElement(List<String> namesList) {
        for (int i = 0; i < namesList.size(); i++) {
            if (namesList.get(i) == null) {
                return i;
            }
        }

        return namesList.size();
    }
}
