package com.example.lovci_pokladov.services;

import java.util.ArrayList;
import java.util.List;

public class Observable<T> {
    private List<VariableChangeListener> listeners = new ArrayList<>();
    private T genericVariable;

    public T getValue() {
        return genericVariable;
    }

    public void setValue(T value) {
        if (this.genericVariable != value) {
            this.genericVariable = value;
            notifyListeners();
        }
    }

    public void onChangeListener(VariableChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(VariableChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (VariableChangeListener listener : listeners) {
            listener.onChange(genericVariable);
        }
    }
}

