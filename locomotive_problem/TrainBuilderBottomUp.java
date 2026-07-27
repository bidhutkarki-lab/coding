import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
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

Time complexity: O(n * W * L) - the algorithm creates three nested loops
Space complexity: O(n * W * L) - the algorithm creates a 3D array of size n * W * L
 */

public class TrainBuilderBottomUp {

    private static final int CAPACITY_PER_LOCOMOTIVE = 150;

    @Data
    @AllArgsConstructor
    public static class TrainCar {
        private String id;
        private int weightTons;
        private int lengthFeet;
        private int value;
    }

    @Data
    @AllArgsConstructor
    public static class TrainPlan {
        private int locomotiveCount;
        private int totalCarWeight;
        private int totalTrainLength;
        private int totalValue;
        private List<TrainCar> selectedCars;
    }

    public static TrainPlan buildOptimalTrain(
            List<TrainCar> cars,
            int maxTrainLength,
            int locomotiveLength) {

        TrainPlan oneLocomotivePlan = solveForLocomotiveCount(
                cars,
                1,
                maxTrainLength,
                locomotiveLength
        );

        TrainPlan twoLocomotivePlan = solveForLocomotiveCount(
                cars,
                2,
                maxTrainLength,
                locomotiveLength
        );

        if (twoLocomotivePlan.getTotalValue()
                > oneLocomotivePlan.getTotalValue()) {
            return twoLocomotivePlan;
        }

        // Tie goes to fewer locomotives.
        return oneLocomotivePlan;
    }

    private static TrainPlan solveForLocomotiveCount(
            List<TrainCar> cars,
            int locomotiveCount,
            int maxTrainLength,
            int locomotiveLength) {

        int maxWeight =
                locomotiveCount * CAPACITY_PER_LOCOMOTIVE;
        int locomotiveTotalLength =
                locomotiveCount * locomotiveLength;

        int maxCarLength =
                maxTrainLength - locomotiveTotalLength;

        if (maxCarLength < 0) {
            return new TrainPlan(
                    locomotiveCount,
                    0,
                    locomotiveTotalLength,
                    0,
                    List.of()
            );
        }

        int numberOfCars = cars.size();

        int[][][] dp = new int[
                numberOfCars + 1
        ][
                maxWeight + 1
        ][
                maxCarLength + 1
        ];

        for (int i = 1; i <= numberOfCars; i++) {
            TrainCar currentCar = cars.get(i - 1);

            for (int weight = 0; weight <= maxWeight; weight++) {
                for (int length = 0;
                     length <= maxCarLength;
                     length++) {

                    int exclude = dp[i - 1][weight][length];

                    int include = Integer.MIN_VALUE;

                    if (currentCar.getWeightTons() <= weight
                            && currentCar.getLengthFeet() <= length) {

                        include = currentCar.getValue()
                                + dp[i - 1]
                                    [weight - currentCar.getWeightTons()]
                                    [length - currentCar.getLengthFeet()];
                    }

                    dp[i][weight][length] =
                            Math.max(include, exclude);
                }
            }
        }

        List<TrainCar> selectedCars = reconstructCars(
                cars,
                dp,
                maxWeight,
                maxCarLength
        );

        int totalCarWeight = selectedCars.stream()
                .mapToInt(TrainCar::getWeightTons)
                .sum();

        int totalCarLength = selectedCars.stream()
                .mapToInt(TrainCar::getLengthFeet)
                .sum();

        return new TrainPlan(
                locomotiveCount,
                totalCarWeight,
                locomotiveTotalLength + totalCarLength,
                dp[numberOfCars][maxWeight][maxCarLength],
                selectedCars
        );
    }

    private static List<TrainCar> reconstructCars(
            List<TrainCar> cars,
            int[][][] dp,
            int maxWeight,
            int maxLength) {

        List<TrainCar> selectedCars = new ArrayList<>();

        int weight = maxWeight;
        int length = maxLength;

        for (int i = cars.size(); i > 0; i--) {
            if (dp[i][weight][length]
                    == dp[i - 1][weight][length]) {
                continue;
            }

            TrainCar selectedCar = cars.get(i - 1);
            selectedCars.add(selectedCar);

            weight -= selectedCar.getWeightTons();
            length -= selectedCar.getLengthFeet();
        }

        Collections.reverse(selectedCars);
        return selectedCars;
    }




}
