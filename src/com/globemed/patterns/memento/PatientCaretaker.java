/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.globemed.patterns.memento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Hansana
 */
public class PatientCaretaker {

    private List<PatientMemento> mementos;
    private int maxVersions;

    public PatientCaretaker(int maxVersions) {
        this.mementos = new ArrayList<>();
        this.maxVersions = maxVersions;
    }

    public void saveMemento(PatientMemento memento) {
        mementos.add(memento);

        // Keep only the most recent versions
        while (mementos.size() > maxVersions) {
            mementos.remove(0);
        }

        System.out.println("Saved patient version: " + memento.getChangeReason()
                + " (Total versions: " + mementos.size() + ")");
    }

    public PatientMemento getMemento(int index) {
        if (index >= 0 && index < mementos.size()) {
            return mementos.get(index);
        }
        return null;
    }

    public PatientMemento getLatestMemento() {
        return mementos.isEmpty() ? null : mementos.get(mementos.size() - 1);
    }

    public List<PatientMemento> getAllMementos() {
        return new ArrayList<>(mementos);
    }

    public List<PatientMemento> getMementosSince(LocalDateTime since) {
        return mementos.stream()
                .filter(m -> m.getSnapshotTime().isAfter(since))
                .collect(Collectors.toList());
    }

    public int getVersionCount() {
        return mementos.size();
    }

    public void clearHistory() {
        mementos.clear();
        System.out.println("Patient version history cleared");
    }
}
