package pathfinder.criteries;

public class DistanceByTruck extends Criteria<Integer> {
	int price;

	public DistanceByTruck(int distance) {
		this.distance = distance;
		this.cost = 5;
		this.price = this.distance * this.cost;
	}
}
