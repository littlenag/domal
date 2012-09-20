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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import org.apache.log4j.Logger;

import com.irksomeideas.domal.model.Metric;
import com.irksomeideas.domal.model.MetricStreamService;

public class DashboardController implements Initializable  {
  
  private Logger logger = Logger.getLogger(DashboardController.class);

  @FXML
  TableView<Metric> table;
  @FXML
  TableColumn<Metric, String> colMetricName;
  @FXML
  TableColumn<Metric, String> colUnits;
  @FXML
  TableColumn<Metric, String> colTags;

  @FXML
  ListView<String> list;

  @FXML
  LineChart chart;
  
  @FXML
  Button refreshButton;

  private String displayedMetricStreamId;   // the id of the bug displayed in the details
                                            // section.
  private String displayedDeviceName;       // the name of the project of the bug
                                            // displayed in the details section.

  @FXML
  Label displayedMetricStreamLabel; // a concatenation of the device and metric names

  @FXML
  AnchorPane details;

  @FXML
  Label messageBar;

  ObservableList<String> devicesView = FXCollections.observableArrayList();
  
  final ObservableList<Metric> tableContent = FXCollections.observableArrayList();

  MetricStreamService model;
  
  // So that Spring can inject the model
  public void setModelService(MetricStreamService modelService) {
    this.model = modelService;
  }
  
  /**
   * Called when the NewIssue button is fired.
   *
   * @param event the action event.
   */
  public void refreshButtonFired(ActionEvent event) {
      if (model != null) {
        model.refresh();
        logger.info("refreshing devices list");
      }
  }

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL url, ResourceBundle rsrcs) {
    assert details != null : "fx:id=\"details\" was not injected: check your FXML file 'IssueTracking.fxml'.";
    assert displayedMetricStreamLabel != null : "fx:id=\"displayedIssueLabel\" was not injected: check your FXML file 'IssueTracking.fxml'.";
    assert messageBar != null : "fx:id=\"messageBar\" was not injected: check your FXML file 'IssueTracking.fxml'.";
    assert table != null : "fx:id=\"table\" was not injected: check your FXML file 'IssueTracking.fxml'.";
    assert list != null : "fx:id=\"list\" was not injected: check your FXML file 'IssueTracking.fxml'.";

    System.out.println(this.getClass().getSimpleName() + ".initialize");
    configureMetricStreamTable();
    connectToService();
    if (list != null) {
      list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
      list.getSelectionModel().selectedItemProperty().addListener(deviceSelected);
      displayedDevices.addListener(deviceNamesListener);
    }
  }

  // An observable list of project names obtained from the model.
  // This is a live list, and we will react to its changes by removing
  // and adding project names to/from our list widget.
  private ObservableList<String> displayedDevices;

  // The list of Issue IDs relevant to the selected project. Can be null
  // if no project is selected. This list is obtained from the model.
  // This is a live list, and we will react to its changes by removing
  // and adding Issue objects to/from our table widget.
  private ObservableList<String> displayedMetricStreams;

  // This listener will listen to changes in the displayedProjectNames list,
  // and update our list widget in consequence.
  private final ListChangeListener<String> deviceNamesListener = new ListChangeListener<String>() {

    @Override
    public void onChanged(Change<? extends String> c) {
      if (devicesView == null) {
        return;
      }
      while (c.next()) {
        if (c.wasAdded() || c.wasReplaced()) {
          for (String p : c.getAddedSubList()) {
            devicesView.add(p);
          }
        }
        if (c.wasRemoved() || c.wasReplaced()) {
          for (String p : c.getRemoved()) {
            devicesView.remove(p);
          }
        }
      }
      FXCollections.sort(devicesView);
    }
  };

  // This listener will listen to changes in the devices list,
  // and update our table of metric streams in consequence.
  private final ListChangeListener<String> devicesListener = new ListChangeListener<String>() {

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
            Metric removed = null;
            // Issue already removed:
            // we can't use model.getIssue(issueId) to get it.
            // we need to loop over the table content instead.
            // Then we need to remove it - but outside of the for loop
            // to avoid ConcurrentModificationExceptions.
            for (Metric t : table.getItems()) {
              if (t.getMetricName().equals(p)) {
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
    displayedDevices = model.getDeviceNames();
    devicesView.clear();
    List<String> sortedProjects = new ArrayList<String>(displayedDevices);
    Collections.sort(sortedProjects);
    devicesView.addAll(sortedProjects);
    list.setItems(devicesView);
  }

  // This listener listen to changes in the table widget selection and
  // update the DeleteIssue button state accordingly.
  private final ListChangeListener<Metric> tableSelectionChanged = new ListChangeListener<Metric>() {
    @Override
    public void onChanged(Change<? extends Metric> c) {
      updateMetricStreamChart();
    }
  };

  private static String nonNull(String s) {
    return s == null ? "" : s;
  }

  private void updateMetricStreamChart() {
    final Metric selectedMetricStream = getSelectedMetricStream();
    if (details != null && selectedMetricStream != null) {
      if (displayedMetricStreamLabel != null) {
        displayedMetricStreamId = selectedMetricStream.getMetricName();
        displayedDeviceName = selectedMetricStream.getDeviceName();
        displayedMetricStreamLabel.setText(displayedDeviceName + " / " + displayedMetricStreamId);
      }
    } else {
      displayedMetricStreamLabel.setText("");
      displayedMetricStreamId = null;
      displayedDeviceName = null;
    }
    if (details != null) {
      details.setVisible(selectedMetricStream != null);
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
  private void configureMetricStreamTable() {
    colMetricName.setCellValueFactory(new PropertyValueFactory<Metric, String>("metricName"));
    colUnits.setCellValueFactory(new PropertyValueFactory<Metric, String>("units"));
    colTags.setCellValueFactory(new PropertyValueFactory<Metric, String>("tags"));

    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    table.setItems(tableContent);
    assert table.getItems() == tableContent;

    final ObservableList<Metric> tableSelection = table.getSelectionModel().getSelectedItems();

    tableSelection.addListener(tableSelectionChanged);
  }

  /**
   * Return the name of the project currently selected, or null if no project is
   * currently selected.
   * 
   */
  public String getSelectedDevice() {
    if (model != null && list != null) {
      final ObservableList<String> selectedProjectItem = list.getSelectionModel().getSelectedItems();
      final String selectedProject = selectedProjectItem.get(0);
      return selectedProject;
    }
    return null;
  }

  public Metric getSelectedMetricStream() {
    if (model != null && table != null) {
      List<Metric> selectedIssues = table.getSelectionModel().getSelectedItems();
      if (selectedIssues.size() == 1) {
        final Metric selectedIssue = selectedIssues.get(0);
        return selectedIssue;
      }
    }
    return null;
  }

  /**
   * Listen to changes in the list selection, and updates the table widget and
   * DeleteIssue and NewIssue buttons accordingly.
   */
  private final ChangeListener<String> deviceSelected = new ChangeListener<String>() {

    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
      deviceUnselected(oldValue);
      deviceSelected(newValue);
    }
  };

  // Called when a project is unselected.
  private void deviceUnselected(String oldDeviceName) {
    if (oldDeviceName != null) {
      logger.info("Unselected device: " + oldDeviceName);
      displayedMetricStreams.removeListener(devicesListener);
      displayedMetricStreams = null;
      table.getSelectionModel().clearSelection();
      table.getItems().clear();
    }
  }

  // Called when a project is selected.
  private void deviceSelected(String newDeviceName) {
    if (newDeviceName != null) {
      logger.info("Select deviced: " + newDeviceName);
      table.getItems().clear();
      displayedMetricStreams = model.getMetricStreamIds(newDeviceName);
      for (String id : displayedMetricStreams) {
        final Metric stream = model.getMetricStream(id);
        table.getItems().add(stream);
      }
      displayedMetricStreams.addListener(devicesListener);
    }
  }
}
