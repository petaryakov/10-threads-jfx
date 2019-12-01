package de.thro.inf.prg3.a10.controller;

import de.thro.inf.prg3.a10.internals.displaying.ProgressReporter;
import de.thro.inf.prg3.a10.kitchen.KitchenHatch;
import de.thro.inf.prg3.a10.kitchen.KitchenHatchImpl;
import de.thro.inf.prg3.a10.kitchen.workers.Cook;
import de.thro.inf.prg3.a10.kitchen.workers.Waiter;
import de.thro.inf.prg3.a10.model.Order;
import de.thro.inf.prg3.a10.util.NameGenerator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

import static de.thro.inf.prg3.a10.KitchenHatchConstants.*;

public class MainController implements Initializable {

	private final ProgressReporter progressReporter;
	private final KitchenHatch kitchenHatch;
	private final NameGenerator nameGenerator;

	@FXML
	private ProgressIndicator waitersBusyIndicator;

	@FXML
	private ProgressIndicator cooksBusyIndicator;

	@FXML
	private ProgressBar kitchenHatchProgress;

	@FXML
	private ProgressBar orderQueueProgress;

	public MainController() {
		nameGenerator = new NameGenerator();

		//TODO assign an instance of your implementation of the KitchenHatch interface
		final var orders = new LinkedList<Order>();

		IntStream.range(0, ORDER_COUNT)
			.mapToObj(i -> new Order(nameGenerator.getRandomDish()))
			.forEach(orders::add);

		this.kitchenHatch = new KitchenHatchImpl(KITCHEN_HATCH_SIZE, orders);
		this.progressReporter = new ProgressReporter(kitchenHatch, COOKS_COUNT, WAITERS_COUNT, ORDER_COUNT, KITCHEN_HATCH_SIZE);

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		orderQueueProgress.progressProperty().bindBidirectional(this.progressReporter.orderQueueProgressProperty());
		kitchenHatchProgress.progressProperty().bindBidirectional(this.progressReporter.kitchenHatchProgressProperty());
		waitersBusyIndicator.progressProperty().bindBidirectional(this.progressReporter.waitersBusyProperty());
		cooksBusyIndicator.progressProperty().bind(this.progressReporter.cooksBusyProperty());

		/* TODO create the cooks and waiters, pass the kitchen hatch and the reporter instance and start them */
		/* Spawn 'cook' threads */
		IntStream.range(0, COOKS_COUNT)
			.mapToObj(i -> new Thread(new Cook(nameGenerator.generateName(), kitchenHatch, progressReporter)))
			.forEach(Thread::start);

		/* Spawn 'waiter' threads */
		IntStream.range(0, WAITERS_COUNT)
			.mapToObj(i -> new Thread(new Waiter(nameGenerator.generateName(), kitchenHatch, progressReporter)))
			.forEach(Thread::start);
	}
}
