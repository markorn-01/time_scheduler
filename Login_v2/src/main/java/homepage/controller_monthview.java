package homepage;


import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;

public class controller_monthview extends controller {
	static Calendar cal = Calendar.getInstance(TimeZone.getDefault());
	static boolean buttoncreated = false;
	static Button[] buttons = new Button[42];
	private ScrollPane scrollPane = new ScrollPane();

	private final DoubleProperty zoomProperty = new SimpleDoubleProperty(1.0d);
	private final DoubleProperty deltaY = new SimpleDoubleProperty(0.0d);

	private final Group group = new Group();
	@FXML
	public void ChangeView(ActionEvent e) throws IOException {
		doChangeview(e, "design.fxml");
	}

	@FXML
	GridPane gridpane_monthview = new GridPane();

	@FXML
	AnchorPane mainPane = new AnchorPane();
	int[] dayOrder = new int[42];

	static public Stage popup2 = new Stage();

	@FXML
	void initialize() {
		AnchorPane root = new AnchorPane();
		AnchorPane.setTopAnchor(scrollPane, 10.0d);
		AnchorPane.setRightAnchor(scrollPane, 10.0d);
		AnchorPane.setBottomAnchor(scrollPane, 10.0d);
		AnchorPane.setLeftAnchor(scrollPane, 10.0d);

		scrollPane.setPannable(true);
		scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
		PanAndZoomPane canvas = new PanAndZoomPane();

		Rectangle rect = new Rectangle(100, 100);
		rect.setStroke(Color.NAVY);
		rect.setFill(Color.NAVY);
		rect.setStrokeType(StrokeType.INSIDE);

		group.getChildren().add(rect);
		canvas.getChildren().add(group);
		zoomProperty.bind(canvas.myScale);
		deltaY.bind(canvas.deltaY);
		scrollPane.setContent(canvas);

		canvas.toBack();

		SceneGestures sceneGestures = new SceneGestures(canvas);
		scrollPane.addEventFilter(MouseEvent.MOUSE_CLICKED, sceneGestures.getOnMouseClickedEventHandler());
		scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
		scrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
		scrollPane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());

		root.getChildren().add(scrollPane);
		Scene scene2 = new Scene(root, 600, 400);
		popup2.setScene(scene2);
		popup2.initStyle(StageStyle.UNDECORATED);


		int numRows = 6;
		int numColumns = 7;
		for (int i = 0; i < numRows; i++)
			for (int j = 0; j < numColumns; j++) {
				buttons[i * numColumns + j] = new Button();
				buttons[i * numColumns + j].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
				buttons[i * numColumns + j].setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						if (paneOn == 0) {
							Button notedPane=(Button) e.getSource();
							Bounds bnds = notedPane.localToScreen(notedPane.getLayoutBounds());
							double x = bnds.getMinX() - (scrollPane.getWidth() / 2) +
									(notedPane.getWidth() / 2);
							double y = bnds.getMinY() - scrollPane.getHeight();
							System.out.println(x);
							popup2.setX(x - 10);
							popup2.setY(y - 20);
							popup2.show();
							paneOn = 1;
						}
					}
				});
				gridpane_monthview.add(buttons[i * numColumns + j], j, i);
			}
		displayMonthCalendar();

	}
	class PanAndZoomPane extends Pane {

		public static final double DEFAULT_DELTA = 1.3d;
		DoubleProperty myScale = new SimpleDoubleProperty(1.0);
		public DoubleProperty deltaY = new SimpleDoubleProperty(0.0);
		private Timeline timeline;

		public PanAndZoomPane() {

			this.timeline = new Timeline(60);

			// add scale transform
			scaleXProperty().bind(myScale);
			scaleYProperty().bind(myScale);
		}

		public double getScale() {
			return myScale.get();
		}

		public void setScale(double scale) {
			myScale.set(scale);
		}

		public void setPivot(double x, double y, double scale) {
			// note: pivot value must be untransformed, i. e. without scaling
			// timeline that scales and moves the node
			timeline.getKeyFrames().clear();
			timeline.getKeyFrames().addAll(
					new KeyFrame(Duration.millis(200), new KeyValue(translateXProperty(), getTranslateX() - x)),
					new KeyFrame(Duration.millis(200), new KeyValue(translateYProperty(), getTranslateY() - y)),
					new KeyFrame(Duration.millis(200), new KeyValue(myScale, scale)));
			timeline.play();

		}

		public void fitWidth() {
			double scale = getParent().getLayoutBounds().getMaxX() / getLayoutBounds().getMaxX();
			double oldScale = getScale();

			double f = scale - oldScale;

			double dx = getTranslateX() - getBoundsInParent().getMinX() - getBoundsInParent().getWidth() / 2;
			double dy = getTranslateY() - getBoundsInParent().getMinY() - getBoundsInParent().getHeight() / 2;

			double newX = f * dx + getBoundsInParent().getMinX();
			double newY = f * dy + getBoundsInParent().getMinY();

			setPivot(newX, newY, scale);

		}

		public void resetZoom() {
			double scale = 1.0d;

			double x = getTranslateX();
			double y = getTranslateY();

			setPivot(x, y, scale);
		}

		public double getDeltaY() {
			return deltaY.get();
		}

		public void setDeltaY(double dY) {
			deltaY.set(dY);
		}
	}

	/**
	 * Mouse drag context used for scene and nodes.
	 */
	class DragContext {

		double mouseAnchorX;
		double mouseAnchorY;

		double translateAnchorX;
		double translateAnchorY;

	}

	/**
	 * Listeners for making the scene's canvas draggable and zoomable
	 */
	public class SceneGestures {

		private DragContext sceneDragContext = new DragContext();

		PanAndZoomPane panAndZoomPane;

		public SceneGestures(PanAndZoomPane canvas) {
			this.panAndZoomPane = canvas;
		}

		public EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
			return onMouseClickedEventHandler;
		}

		public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
			return onMousePressedEventHandler;
		}

		public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
			return onMouseDraggedEventHandler;
		}

		public EventHandler<ScrollEvent> getOnScrollEventHandler() {
			return onScrollEventHandler;
		}

		private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {

				sceneDragContext.mouseAnchorX = event.getX();
				sceneDragContext.mouseAnchorY = event.getY();

				sceneDragContext.translateAnchorX = panAndZoomPane.getTranslateX();
				sceneDragContext.translateAnchorY = panAndZoomPane.getTranslateY();

			}

		};

		private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {

				panAndZoomPane.setTranslateX(
						sceneDragContext.translateAnchorX + event.getX() - sceneDragContext.mouseAnchorX);
				panAndZoomPane.setTranslateY(
						sceneDragContext.translateAnchorY + event.getY() - sceneDragContext.mouseAnchorY);

				event.consume();
			}
		};

		/**
		 * Mouse wheel handler: zoom to pivot point
		 */
		private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {

				double delta = PanAndZoomPane.DEFAULT_DELTA;

				double scale = panAndZoomPane.getScale(); // currently we only use Y, same value is used for X
				double oldScale = scale;

				panAndZoomPane.setDeltaY(event.getDeltaY());
				if (panAndZoomPane.deltaY.get() < 0) {
					scale /= delta;
				} else {
					scale *= delta;
				}

				double f = (scale / oldScale) - 1;
				double dx = (event.getX() - (panAndZoomPane.getBoundsInParent().getWidth() / 2
						+ panAndZoomPane.getBoundsInParent().getMinX()));
				double dy = (event.getY() - (panAndZoomPane.getBoundsInParent().getHeight() / 2
						+ panAndZoomPane.getBoundsInParent().getMinY()));

				panAndZoomPane.setPivot(f * dx, f * dy, scale);

				event.consume();

			}
		};

		/**
		 * Mouse click handler
		 */
		private EventHandler<MouseEvent> onMouseClickedEventHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getButton().equals(MouseButton.PRIMARY)) {
					if (event.getClickCount() == 2) {
						panAndZoomPane.resetZoom();
					}
				}
				if (event.getButton().equals(MouseButton.SECONDARY)) {
					if (event.getClickCount() == 2) {
						panAndZoomPane.fitWidth();
					}
				}
			}
		};
	}
	private void getDayOrder() {
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int totaldays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int pivot = cal.get(Calendar.DAY_OF_WEEK);
		int i = 0;
		while (i < totaldays) {
			dayOrder[pivot - 1 + i] = i + 1;
			i++;
		}
		cal.add(Calendar.MONTH, -1);
		int totaldays_neighbor = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		for (int j = pivot - 2; j >= 0; j--) {
			dayOrder[j] = totaldays_neighbor;
			totaldays_neighbor--;
		}
		int nextmonthday = 1;
		for (; i + pivot - 1 < 42; i++) {
			dayOrder[i + pivot - 1] = nextmonthday;
			nextmonthday++;
		}
		cal.add(Calendar.MONTH, 1);

	}

	public void changeviewmonth(ActionEvent e) {
		if (e.getSource() == btnPrevDay)
			cal.add(Calendar.MONTH, -1);
		else if (e.getSource() == btnNextDay)
			cal.add(Calendar.MONTH, 1);
		displayMonthCalendar();
	}

	private void displayMonthCalendar() {
		getDayOrder();
		for (int i = 0; i < 42; i++)
			buttons[i].setText(String.valueOf(dayOrder[i]));
		currentDayMonth.setText(String.valueOf(cal.get(Calendar.MONTH)+1));
	}
}