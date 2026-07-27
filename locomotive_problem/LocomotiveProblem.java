import java.util.ArrayList;
import java.util.List;

/**
Locomotive loading problem.
A railway company needs to build a freight train from a set of available freight cars.

The train must satisfy the following constraints:

A train can have either 1 or 2 locomotives.
Each locomotive can pull 150 tons.
Each locomotive has a fixed length of 75 feet.
The total train length (locomotives + freight cars) cannot exceed MAX_LENGTH feet.
Each freight car can be used at most once.
Every freight car has:
weight (tons)
length (feet)
value (cargo value or profit)

Your task is to build the train that maximizes the total cargo value while satisfying all constraints.

If two solutions have the same value, prefer the one with fewer locomotives.

Solution:

For every car, make two decisions:

Include the car, if both weight and length allow
Exclude the car
Take the better result

Do this once with 1 locomotive and once with 2 locomotives. Then choose the better train.

Time : 2 X O(2ⁿ)
Space: O(n) - recursive stack depth
 */
public class LocomotiveProblem {

    private static final int CAPACITY_PER_LOCOMOTIVE = 150;

    @Data
    @AllArgsConstructor
    public static final class Car {
        private String id;
        private int weight;
        private int lengthFleet;
        private int value;
    }

    @Data
    @AllArgsConstructor
    public static final class Plan {
        private int locomotiveCount;
        private int totalCarWeight;
        private int totalTrainLength;
        private int totalValue;
        private List<TrainCar> selectedCars;
    }

    /**
     * Picks the number of locomotives (1 or 2) and the subset of wagons that
     * maximizes total cargo value without exceeding pulling capacity or the
     * train-length limit.
     *
     * @param wagons          available wagons
     * @param capacityPerLoco tons a single locomotive can pull (X)
     * @param maxLength       maximum train length in cars, locomotives included (Y)
     */
    public static TrainPlan buildOptimalTrain(
        List<TrainCar> cars,
        int maxTrainLength,
        int locomotiveLength) {

        TrainPlan oneLocomotivePlan = solveForLocomotives(
            cars,
            1,
            maxTrainLength,
            locomotiveLength
        );

        TrainPlan twoLocomotivePlan = solveForLocomotives(
                cars,
                2,
                maxTrainLength,
                locomotiveLength
        );

        if (twoLocomotivePlan.getTotalValue()
            > oneLocomotivePlan.getTotalValue()) {
            return twoLocomotivePlan;
        }

        // Tie goes to fewer locomotives
        return oneLocomotivePlan;
    }

    /**
     * 0/1 knapsack for a fixed locomotive count, constrained by both total
     * weight (<= L*X) and wagon count (<= Y - L). Returns null when the train
     * cannot even hold this many locomotives.
     */
    private static TrainPlan solveForLocomotives(
            List<TrainCar> cars,
            int locomotiveCount,
            int maxTrainLength,
            int locomotiveLength) {

        int maxCarWeight =
                locomotiveCount * CAPACITY_PER_LOCOMOTIVE;

        int locomotiveTotalWeight = locomotiveCount * locomotiveLength;

        int maxCarLength = locomotiveCount * locomotiveLength;

        if(maxCarLength < 0) {
            return new TrainPlan(
                locomotiveCount,
                0,
                locomotiveTotalLength,
                0,
                List.of()
            );
        }

        return search(cars, 0, maxCarWeight, maxCarLength, locomotiveCount, locomotiveTotalLength);
    }

    private static TrainPlan search(
            List<TrainCar> cars,
            int index,
            int remainingWeight,
            int remainingLength,
            int locomotiveCount,
            int locomotiveTotalLength) {

        if(index == cars.size()) {
            return new TrainPlan(locomotiveCount, 0, locomotiveTotalLength, 0, List.of());
        }

        TrainCar currentCar = cars.get(index);

        // Option 1: skip this car
        TrainPlan excludePlan = search(
            cars,
            index + 1,
            remainingWeight,
            remainingLength,
            locomotiveCount,
            locomotiveTotalLength
        );

        // Option 2: include this car
        TrainPlan includePlan = null;
        if(currentCar.getWeight() <= remainingWeight && currentCar.getLength() <= remainingLength) {
            TrainPlan remainingPlan = search(
                cars,
                index + 1,
                remainingWeight - currentCar.getWeight(),
                remainingLength - currentCar.getLength(),
                locomotiveCount,
                locomotiveTotalLength
            );

            selectedCars = new ArrayList<>(remainingPlan.getSelectedCars());

            selectedCars.add(currentCar);

            includePlan = new TrainPlan(
                locomotiveCount,
                remainingPlan.getTotalCarWeight() + currentCar.getWeight(),
                remainingPlan.getTotalTrainLength() + currentCar.getLength(),
                remainingPlan.getTotalValue() + currentCar.getValue(),
                selectedCars
            );
        }

        if(includePlan == null) {
            return excludePlan;
        }

        if(excludePlan.getTotalValue() > includePlan.getTotalValue()) {
            return excludePlan;
        }

        return includePlan;

    }

    public static void main(String[] args) {
        // Case 1: two very valuable heavy wagons only fit weight-wise with a
        // second locomotive, and there are enough length slots for both.
        // -> 2 locomotives wins.
        List<Car> heavy = List.of(
                new Car("H1", 9, 20),
                new Car("H2", 9, 20),
                new Car("L1", 1, 1),
                new Car("L2", 1, 1),
                new Car("L3", 1, 1));
        // X=10, Y=4. L=1: cap 10, 3 slots -> H1+L1 = 21. L=2: cap 20, 2 slots -> H1+H2 = 40.
        System.out.println(solve(heavy, 10, 4)); // 2 locomotives, value=40

        // Case 2: capacity is abundant, length is the binding constraint, so the
        // second locomotive just wastes a wagon slot. -> 1 locomotive wins.
        List<Car> light = List.of(
                new Car("A", 1, 10),
                new Car("B", 1, 10),
                new Car("C", 1, 10),
                new Car("D", 1, 10),
                new Car("E", 1, 10));
        // X=100, Y=3. L=1: 2 slots -> 20. L=2: 1 slot -> 10.
        System.out.println(solve(light, 100, 3)); // 1 locomotive, value=20

        // Case 3: mixed weights/values, weight is the real constraint.
        List<Car> mixed = List.of(
                new Car("W1", 6, 5),
                new Car("W2", 5, 5),
                new Car("W3", 4, 4),
                new Car("W4", 3, 3),
                new Car("W5", 2, 2));
        // X=10, Y=4.
        System.out.println(solve(mixed, 10, 4));
    }
}
