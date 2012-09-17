/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package com.irksomeideas.domal.model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

public class MetricStreamServiceImpl implements MetricStreamService {

    // You add a device by adding an entry with an empty observable array list
    // of issue IDs in the projects Map.
    final ObservableMap<String, ObservableList<String>> devicesMap;
    {
        final Map<String, ObservableList<String>> map = new TreeMap<String, ObservableList<String>>();
        devicesMap = FXCollections.observableMap(map);
        for (String s : newList("domal", "router", "raid", "vm-host", "vm-1")) {
            devicesMap.put(s, FXCollections.<String>observableArrayList());
        }
    }

    // The projectNames list is kept in sync with the project's map by observing
    // the projectsMap and modifying the projectNames list in consequence.
    final MapChangeListener<String, ObservableList<String>> projectsMapChangeListener = new MapChangeListener<String, ObservableList<String>>() {
        @Override
        public void onChanged(Change<? extends String, ? extends ObservableList<String>> change) {
            if (change.wasAdded()) deviceNames.add(change.getKey());
            if (change.wasRemoved()) deviceNames.remove(change.getKey());
        }
    };
    
    final ObservableList<String> deviceNames;
    {
        deviceNames = FXCollections.<String>observableArrayList();
        deviceNames.addAll(devicesMap.keySet());
        devicesMap.addListener(projectsMapChangeListener);
    }

    // A Issue stub.
    public final class IssueStub implements ObservableMetricStream {
        private final SimpleLongProperty creationTime;
        private final SimpleStringProperty id;
        private final SimpleStringProperty projectName;
        private final SimpleStringProperty title;
        private final SimpleStringProperty description;

        IssueStub(String projectName, String id) {
            this(projectName, id, null);
        }
        IssueStub(String projectName, String id, String title) {
            assert deviceNames.contains(projectName);
            assert ! devicesMap.get(projectName).contains(id);
            assert ! issuesMap.containsKey(id);
            this.projectName = new SimpleStringProperty(projectName);
            this.id = new SimpleStringProperty(id);
            this.creationTime = new SimpleLongProperty(System.currentTimeMillis());
            this.title = new SimpleStringProperty(title);
            this.description = new SimpleStringProperty("");
        }

        @Override
        public long getDate() {
            return creationTime.get();
        }

        @Override
        public String getId() {
            return id.get();
        }

        @Override
        public String getDeviceName() {
            return projectName.get();
        }

        @Override
        public String getMetricName() {
            return title.get();
        }

        @Override
        public String getUnits() {
            return description.get();
        }

        @Override
        public ObservableValue<Number> dateProperty() {
            return creationTime;
        }

        @Override
        public ObservableValue<String> idProperty() {
            return id;
        }

        @Override
        public ObservableValue<String> deviceNameProperty() {
            return projectName;
        }

        @Override
        public ObservableValue<String> synopsisProperty() {
            return title;
        }

        @Override
        public ObservableValue<String> descriptionProperty() {
            return description;
        }
    }

    // You create new issue by adding a IssueStub instance to the issuesMap.
    // the new id will be automatically added to the corresponding list in
    // the projectsMap.
    //
    final MapChangeListener<String, IssueStub> issuesMapChangeListener = new MapChangeListener<String, IssueStub>() {
        @Override
        public void onChanged(Change<? extends String, ? extends IssueStub> change) {
            if (change.wasAdded()) {
                final IssueStub val = change.getValueAdded();
                devicesMap.get(val.getDeviceName()).add(val.getId());
            }
            if (change.wasRemoved()) {
                final IssueStub val = change.getValueRemoved();
                devicesMap.get(val.getDeviceName()).remove(val.getId());
            }
        }
    };
    
    final AtomicInteger issueCounter = new AtomicInteger(0);
    final ObservableMap<String, IssueStub> issuesMap;
    {
        final Map<String, IssueStub> map = new TreeMap<String, IssueStub>();
        issuesMap = FXCollections.observableMap(map);
        issuesMap.addListener(issuesMapChangeListener);
    }

    private static <T> List<T> newList(T... items) {
        return Arrays.asList(items);
    }

    @Override
    public ObservableList<String> getDeviceNames() {
        return deviceNames;
    }

    @Override
    public ObservableList<String> getMetricStreamIds(String projectName) {
        return devicesMap.get(projectName);
    }

    @Override
    public IssueStub getMetricStream(String issueId) {
        return issuesMap.get(issueId);
    }
}
