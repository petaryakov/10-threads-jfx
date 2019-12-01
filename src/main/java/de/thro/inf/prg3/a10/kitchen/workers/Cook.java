package de.thro.inf.prg3.a10.kitchen.workers;

import de.thro.inf.prg3.a10.internals.displaying.ProgressReporter;
import de.thro.inf.prg3.a10.kitchen.KitchenHatch;
import de.thro.inf.prg3.a10.model.Dish;
import de.thro.inf.prg3.a10.model.Order;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Cook implements Runnable {

	private static final Logger logger = LogManager.getLogger(Cook.class);
	private String name;
	private ProgressReporter progressReporter;
	private KitchenHatch kitchenHatch;

	public Cook(String name, KitchenHatch kitchenHatch, ProgressReporter progressReporter){
		this.name = name;
		this.kitchenHatch = kitchenHatch;
		this.progressReporter = progressReporter;
	}

	@Override
	public void run(){

		Order o;
		do {
			/* try to deque an order */
			o = kitchenHatch.dequeueOrder(2000);
			if(o != null){
				Dish d = new Dish(o.getMealName());
				try {
					/* 'prepare' the dish */
					Thread.sleep(d.getCookingTime());
					logger.info("Cook {} prepared meal {}", name, d.getMealName());
				} catch (InterruptedException e) {
					logger.error("Failed to cook meal", e);
				}
				/* pass the dish to the kitchen hatch */
				kitchenHatch.enqueueDish(d);
				logger.info( "Cook {} put meal {} into the kitchen hatch", name, d.getMealName());

				/* update progress - reduce orders progress and optionally enlarge kitchen hatch fill level */
				progressReporter.updateProgress();
			}
		}while (o != null);

		/* notify the progress reporter that the cook is leaving */
		progressReporter.notifyCookLeaving();

		// My Code
		/*try{
			while(kitchenHatch.getOrderCount() != 0) {
				while(kitchenHatch.getDishesCount() == kitchenHatch.getMaxDishes())
					wait();

				Order order = kitchenHatch.dequeueOrder();
				Dish dish = new Dish(order.getMealName());

				Thread.sleep(dish.getCookingTime());

				kitchenHatch.enqueueDish(dish);
				progressReporter.updateProgress();

			}

			progressReporter.notifyCookLeaving();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}*/

	}
}
