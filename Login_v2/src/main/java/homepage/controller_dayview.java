package homepage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public class controller_dayview extends controller {
	static Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

	void display_DayCalendar(int weekday, int day, int month, int year) {
		txtWeekday.setText(dayOfWeek[weekday - 1]);
		txtDay.setText(String.valueOf(day));
		txtMonth.setText(String.valueOf(month));
		txtYear.setText(String.valueOf(year));
		currentDayMonth.setText(dayOfWeek[weekday-1]);
	}

	@FXML
	void initialize() {
		int day = calendar.get(Calendar.DATE);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		display_DayCalendar(dayOfWeek, day, month, year);
	}

	public void ChangeView(ActionEvent e) throws IOException {
		doChangeview(e, "design_monthview.fxml");
	}

	public void changeviewday(ActionEvent e) {
		if (e.getSource() == btnPrevDay)
			calendar.add(Calendar.DATE, -1);
		else if (e.getSource() == btnNextDay)
			calendar.add(Calendar.DATE, 1);
		int day = calendar.get(Calendar.DATE);
		// Note: +1 the month for current month
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		display_DayCalendar(dayOfWeek, day, month, year);
	}
}
