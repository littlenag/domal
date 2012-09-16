package com.irksomeideas.domal;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import com.irksomeideas.domal.model.MetricStreamService;
import com.irksomeideas.domal.model.MetricStreamServiceImpl;
import com.irksomeideas.domal.model.ObservableMetricStream;

public class DashboardController implements Initializable {

    @FXML
    TableView<ObservableMetricStream> table;
    @FXML
    TableColumn<ObservableMetricStream, String> colMetricName;
    @FXML
    TableColumn<ObservableMetricStream, String> colUnits;
    @FXML
    TableColumn<ObservableMetricStream, String> colTags;

    @FXML
    ListView<String> list;
    
    @FXML
    LineChart chart;
    
    private String displayedBugId; // the id of the bug displayed in the details section.
    private String displayedBugProject; // the name of the project of the bug displayed in the details section.
    
    @FXML
    Label displayedIssueLabel; // the displayedIssueLabel will contain a concatenation
                               // of the project name and the bug id.
    @FXML
    AnchorPane details;
    
    @FXML
    Label messageBar;
    
    ObservableList<String> projectsView = FXCollections.observableArrayList();
    
    MetricStreamService model = null;
    
    final ObservableList<ObservableMetricStream> tableContent = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rsrcs) {
        assert details != null : "fx:id=\"details\" was not injected: check your FXML file 'IssueTracking.fxml'.";
        assert displayedIssueLabel != null : "fx:id=\"displayedIssueLabel\" was not injected: check your FXML file 'IssueTracking.fxml'.";
        assert messageBar != null : "fx:id=\"messageBar\" was not injected: check your FXML file 'IssueTracking.fxml'.";
        assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'IssueTracking.fxml'.";
        assert list != null : "fx:id=\"list\" was not injected: check your FXML file 'IssueTracking.fxml'.";

        System.out.println(this.getClass().getSimpleName() + ".initialize");
        configureTable();
        connectToService();
        if (list != null) {
            list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            list.getSelectionModel().selectedItemProperty().addListener(projectItemSelected);
            displayedProjectNames.addListener(projectNamesListener);
        }
    }

    FadeTransition messageTransition = null;

    public void displayMessage(String message) {
        if (messageBar != null) {
            if (messageTransition != null) {
                messageTransition.stop();
            } else {
                messageTransition = new FadeTransition(Duration.millis(2000), messageBar);
                messageTransition.setFromValue(1.0);
                messageTransition.setToValue(0.0);
                messageTransition.setDelay(Duration.millis(1000));
                messageTransition.setOnFinished(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent event) {
                        messageBar.setVisible(false);
                    }
                });
            }
            messageBar.setText(message);
            messageBar.setVisible(true);
            messageBar.setOpacity(1.0);
            messageTransition.playFromStart();
        }
    }

    // An observable list of project names obtained from the model.
    // This is a live list, and we will react to its changes by removing
    // and adding project names to/from our list widget.
    private ObservableList<String> displayedProjectNames;
    
    // The list of Issue IDs relevant to the selected project. Can be null
    // if no project is selected. This list is obtained from the model.
    // This is a live list, and we will react to its changes by removing
    // and adding Issue objects to/from our table widget.
    private ObservableList<String> displayedIssues;
    
    // This listener will listen to changes in the displayedProjectNames list,
    // and update our list widget in consequence.
    private final ListChangeListener<String> projectNamesListener = new ListChangeListener<String>() {

        @Override
        public void onChanged(Change<? extends String> c) {
            if (projectsView == null) {
                return;
            }
            while (c.next()) {
                if (c.wasAdded() || c.wasReplaced()) {
                    for (String p : c.getAddedSubList()) {
                        projectsView.add(p);
                    }
                }
                if (c.wasRemoved() || c.wasReplaced()) {
                    for (String p : c.getRemoved()) {
                        projectsView.remove(p);
                    }
                }
            }
            FXCollections.sort(projectsView);
        }
    };
    
    // This listener will listen to changes in the displayedIssues list,
    // and update our table widget in consequence.
    private final ListChangeListener<String> projectIssuesListener = new ListChangeListener<String>() {

        @Override
        public void onChanged(Change<? extends String> c) {
            if (table == null) {
                return;
            }
            while (c.next()) {
                if (c.wasAdded() || c.wasReplaced()) {
                    for (String p : c.getAddedSubList()) {
                        table.getItems().add(model.getMetricStream(p));
                    }
                }
                if (c.wasRemoved() || c.wasReplaced()) {
                    for (String p : c.getRemoved()) {
                        ObservableMetricStream removed = null;
                        // Issue already removed:
                        // we can't use model.getIssue(issueId) to get it.
                        // we need to loop over the table content instead.
                        // Then we need to remove it - but outside of the for loop
                        // to avoid ConcurrentModificationExceptions.
                        for (ObservableMetricStream t : table.getItems()) {
                            if (t.getId().equals(p)) {
                                removed = t;
                                break;
                            }
                        }
                        if (removed != null) {
                            table.getItems().remove(removed);
                        }
                    }
                }
            }
        }
    };

    // Connect to the model, get the project's names list, and listen to
    // its changes. Initializes the list widget with retrieved project names.
    private void connectToService() {
        if (model == null) {
            model = new MetricStreamServiceImpl();
            displayedProjectNames = model.getDeviceNames();
        }
        projectsView.clear();
        List<String> sortedProjects = new ArrayList<String>(displayedProjectNames);
        Collections.sort(sortedProjects);
        projectsView.addAll(sortedProjects);
        list.setItems(projectsView);
    }
    
    // This listener listen to changes in the table widget selection and
    // update the DeleteIssue button state accordingly.
    private final ListChangeListener<ObservableMetricStream> tableSelectionChanged =
            new ListChangeListener<ObservableMetricStream>() {

                @Override
                public void onChanged(Change<? extends ObservableMetricStream> c) {
                    updateBugDetails();
                }
            };

    private static String nonNull(String s) {
        return s == null ? "" : s;
    }

    private void updateBugDetails() {
        final ObservableMetricStream selectedIssue = getSelectedIssue();
        if (details != null && selectedIssue != null) {
            if (displayedIssueLabel != null) {
                displayedBugId = selectedIssue.getId();
                displayedBugProject = selectedIssue.getDeviceName();
                displayedIssueLabel.setText( displayedBugId + " / " + displayedBugProject );
            }
        } else {
            displayedIssueLabel.setText("");
            displayedBugId = null;
            displayedBugProject = null;
        }
        if (details != null) {
            details.setVisible(selectedIssue != null);
        }
    }

    private boolean isVoid(Object o) {
        if (o instanceof String) {
            return isEmpty((String) o);
        } else {
            return o == null;
        }
    }

    private boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    private boolean equal(Object o1, Object o2) {
        if (isVoid(o1)) {
            return isVoid(o2);
        }
        return o1.equals(o2);
    }

    private String formatDate(long date) {
        final SimpleDateFormat format = new SimpleDateFormat();
        return format.format(new Date(date));
    }
    
    // Configure the table widget: set up its column, and register the
    // selection changed listener.
    private void configureTable() {
        colMetricName.setCellValueFactory(new PropertyValueFactory<ObservableMetricStream, String>("id"));
        colUnits.setCellValueFactory(new PropertyValueFactory<ObservableMetricStream, String>("units"));
        colTags.setCellValueFactory(new PropertyValueFactory<ObservableMetricStream, String>("tags"));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setItems(tableContent);
        assert table.getItems() == tableContent;

        final ObservableList<ObservableMetricStream> tableSelection = table.getSelectionModel().getSelectedItems();

        tableSelection.addListener(tableSelectionChanged);
    }

    /**
     * Return the name of the project currently selected, or null if no project
     * is currently selected.
     *
     */
    public String getSelectedProject() {
        if (model != null && list != null) {
            final ObservableList<String> selectedProjectItem = list.getSelectionModel().getSelectedItems();
            final String selectedProject = selectedProjectItem.get(0);
            return selectedProject;
        }
        return null;
    }

    public ObservableMetricStream getSelectedIssue() {
        if (model != null && table != null) {
            List<ObservableMetricStream> selectedIssues = table.getSelectionModel().getSelectedItems();
            if (selectedIssues.size() == 1) {
                final ObservableMetricStream selectedIssue = selectedIssues.get(0);
                return selectedIssue;
            }
        }
        return null;
    }
    
    /**
     * Listen to changes in the list selection, and updates the table widget and
     * DeleteIssue and NewIssue buttons accordingly.
     */
    private final ChangeListener<String> projectItemSelected = new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            projectUnselected(oldValue);
            projectSelected(newValue);
        }
    };

    // Called when a project is unselected.
    private void projectUnselected(String oldProjectName) {
        if (oldProjectName != null) {
            displayedIssues.removeListener(projectIssuesListener);
            displayedIssues = null;
            table.getSelectionModel().clearSelection();
            table.getItems().clear();
        }
    }

    // Called when a project is selected.
    private void projectSelected(String newProjectName) {
        if (newProjectName != null) {
            table.getItems().clear();
            displayedIssues = model.getMetricStreamIds(newProjectName);
            for (String id : displayedIssues) {
                final ObservableMetricStream issue = model.getMetricStream(id);
                table.getItems().add(issue);
            }
            displayedIssues.addListener(projectIssuesListener);
        }
    }
}
