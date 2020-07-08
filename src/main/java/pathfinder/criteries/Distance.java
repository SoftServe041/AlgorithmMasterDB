package pathfinder.criteries;

public class Distance {
	String cityName;
	private int distByTruck;
	private int distByTrain;
	private int distByPlane;
	private int distByShip;

	private int costByTruck;
	private int costByTrain;
	private int costByPlane;
	private int costByShip;

	private int priceByTruck;
	private int priceByTrain;
	private int priceByPlane;
	private int priceByShip;

	public Distance(int[] dist) {
		distByTruck = dist[0];
		distByTrain = dist[1];
		distByPlane = dist[2];
		distByShip = dist[3];

		costByTruck = 10;
		costByTrain = 5;
		costByPlane = 20;
		costByShip = 2;

		priceByTruck = distByTruck * costByTruck;
		priceByTrain = distByTrain * costByTrain;
		priceByPlane = distByPlane * costByPlane;
		priceByShip = distByShip * costByShip;
	}

	public String getCityName() {
		return cityName;
	}

	public int getDistByTruck() {
		return distByTruck;
	}

	public int getDistByTrain() {
		return distByTrain;
	}

	public int getDistByPlane() {
		return distByPlane;
	}

	public int getDistByShip() {
		return distByShip;
	}

	public int getCostByTruck() {
		return costByTruck;
	}

	public int getCostByTrain() {
		return costByTrain;
	}

	public int getCostByPlane() {
		return costByPlane;
	}

	public int getCostByShip() {
		return costByShip;
	}

	public int getPriceByTruck() {
		return priceByTruck;
	}

	public int getPriceByTrain() {
		return priceByTrain;
	}

	public int getPriceByPlane() {
		return priceByPlane;
	}

	public int getPriceByShip() {
		return priceByShip;
	}
}
