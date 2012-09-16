/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package com.irksomeideas.domal.model;

import javafx.collections.ObservableList;

import com.irksomeideas.domal.model.Issue.IssuePriority;
import com.irksomeideas.domal.model.Issue.IssueStatus;

public interface TrackingService {

    public ObservableList<String> getIssueIds(String projectName);
    public ObservableList<String> getProjectNames();
    public ObservableIssue getIssue(String tickectId);
    public ObservableIssue createIssueFor(String projectName);
    public void deleteIssue(String issueId);
    public void saveIssue(String issueId,
            IssueStatus status, IssuePriority priority,
            String synopsis, String description);
}
