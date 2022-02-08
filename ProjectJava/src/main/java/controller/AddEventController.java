package controller;

import backend.ID_management;
import backend.Sess1on;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import project.Main;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddEventController {
    private final LocalTime firstSlotStart = LocalTime.of(0, 0);
    private final java.time.Duration slotLength = java.time.Duration.ofMinutes(15);
    private final LocalTime lastSlotStart = LocalTime.of(23, 59);
    @FXML
    TextField eventName = new TextField();
    @FXML
    ComboBox<customLocalDateTime> cbStart = new ComboBox<customLocalDateTime>();
    @FXML
    ComboBox<customLocalDateTime> cbEnd = new ComboBox<customLocalDateTime>();
    @FXML
    ComboBox<customPriority> cbPriority = new ComboBox<customPriority>();
    @FXML
    DatePicker dpDate;
    @FXML
    Button btnDelete=new Button();
    @FXML
    void initialize() {
        if (Sess1on.isCreatingEvent)
        {
            btnDelete.setVisible(false);
            hpLink.setVisible(false);
            txtLink.setVisible(true);
        }
        else
        {
            btnDelete.setVisible(true);
            hpLink.setVisible(true);
            hpLink.setText(Sess1on.tempEvent.getMeetinglink());
            txtLink.setVisible(false);
            System.out.println(Sess1on.tempEvent.getListParticipants());
            txtParticipants.setText(Sess1on.tempEvent.getListParticipants().stream().
                    map(Object::toString).
                    collect(Collectors.joining(",")));
        }
        txtLink.setOnMouseClicked(event ->
        {
            if (event.getButton() == MouseButton.SECONDARY) swapTextLink();
        });
        hpLink.setOnMouseClicked(event ->
        {
            if (event.getButton() == MouseButton.PRIMARY)
            {

                Application a = new Application() {

                    @Override
                    public void start(Stage stage) throws Exception {
                        //To change body of generated methods, choose Tools | Templates.
                    }
                };
                a.getHostServices().showDocument(hpLink.getText());
            } else if (event.getButton() == MouseButton.SECONDARY)
            {
                swapTextLink();
            }
        });
        LocalDate today = LocalDate.now();
        ObservableList<customLocalDateTime> timeslot = FXCollections.observableArrayList();
        ObservableList<customPriority> priority = FXCollections.observableArrayList();
        for (LocalDateTime startTime = today.atTime(firstSlotStart); !startTime
                .isAfter(today.atTime(lastSlotStart)); startTime = startTime.plus(slotLength)) {
            timeslot.add(new customLocalDateTime(startTime));
        }
        cbStart.setItems(timeslot);
        cbEnd.setItems(timeslot);
        cbPriority.setItems(priority);
        for (int i = 0; i < 3; i++) priority.add(new customPriority(i));
        if (Sess1on.tempEvent.getName().equals("thisisdummyEvent")) {
            cbPriority.setValue(priority.get(0));
            cbStart.setValue(timeslot.get(0));
            cbEnd.setValue(timeslot.get(1));
            dpDate.setValue(today);
            eventName.setText("Untitled");
        }
        else
        {
            eventName.setText(Sess1on.tempEvent.getName());
            cbPriority.setValue(new customPriority(Sess1on.tempEvent.getPriority()));
            customLocalDateTime tempCustom=new customLocalDateTime(Sess1on.tempEvent.getDate());
            cbStart.setValue(tempCustom);
            System.out.println(Sess1on.tempEvent.getDuration());
            tempCustom= new customLocalDateTime(tempCustom.localDateTime.plusMinutes(Sess1on.tempEvent.getDuration()));
            cbEnd.setValue(tempCustom);
            dpDate.setValue(Sess1on.tempEvent.getDate().toLocalDate());
            txtLink.setText(Sess1on.tempEvent.getMeetinglink());
            hpLink.setText(Sess1on.tempEvent.getMeetinglink());
        }
    }

    private void swapTextLink() {
        hpLink.setVisible(!hpLink.isVisible());
        txtLink.setVisible(!txtLink.isVisible());
    }

    @FXML
    TextField txtLink=new TextField();
    @FXML
    Hyperlink hpLink=new Hyperlink();
    @FXML
    TextField txtParticipants=new TextField();
    @FXML
    public void createEvent(ActionEvent e) {
        String name = eventName.getText();
        int duration = (int) ChronoUnit.MINUTES.between(cbStart.getValue().localDateTime, cbEnd.getValue().localDateTime);
        int tp = cbPriority.getValue().priority;
        String location = "";
        LocalDateTime dateOfEvent = dpDate.getValue().atTime(cbStart.getValue().localDateTime.toLocalTime());
        String meettingLink=txtLink.getText();
        List<String>listParticipants=new ArrayList<>();
        String ttt=txtParticipants.getText();
        for (String x:ttt.replace("\\s","").split(","))
        {
            listParticipants.add(x);
        }
        System.out.println(listParticipants);
        Sess1on.tempEvent.updateEvent(name, location, duration, dateOfEvent, tp,meettingLink,listParticipants);
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }
    @FXML
    public void deleteEvent(ActionEvent e)
    {
        Sess1on.deleteEvent=true;
        ID_management.deleteID(Sess1on.tempEvent.getID());
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }
}
