package sintef.android.controller.algorithm;

import java.util.List;

import sintef.android.controller.sensor.data.AccelerometerData;

/**
 * Created by Andreas on 10.02.2015.
 */
public class AlgorithmPhone
{
    private static double totAccThreshold = 6;
    private static double verticalAccThreshold = 5;
    private static double accComparisonThreshold = 0.5;
    private static double angleThreshold = 30;
    private static double gravity = 9.81;
    private static double impactThreshold = 3;
    private static double preimpactThreshold = 3;
    private static double postImpactThreshold = 5;

    public static boolean isFall(double x, double y, double z, double tetaY, double tetaZ)
    {
        double totalAcceleration = Math.abs(gravity-accelerationTotal(x, y, z));    
        double verticalAcceleration = Math.abs(gravity-verticalAcceleration(x, y, z, tetaY, tetaZ));

        if (totalAcceleration >= totAccThreshold && verticalAcceleration >= verticalAccThreshold)
        {
            if (verticalComparedToTotal(verticalAcceleration, totalAcceleration) >= accComparisonThreshold)
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isFall(double x, double y, double z, double tetaY, double tetaZ, double testtotAccThreshold, double testverticalAccThreshold, double testaccComparisonThreshold)
    {
        double totalAcceleration = Math.abs(gravity-accelerationTotal(x, y, z));    
        double verticalAcceleration = Math.abs(gravity-verticalAcceleration(x, y, z, tetaY, tetaZ));

        if (totalAcceleration >= testtotAccThreshold && verticalAcceleration >= testverticalAccThreshold)
        {
            if (verticalComparedToTotal(verticalAcceleration, totalAcceleration) >= testaccComparisonThreshold)
            {
                return true;
            }
        }
        return false;
    }

    /*   Test values == TRUE
    x = [0, 6, 5, 5, 2, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0]
    y = [0, 6, 5, 5, 2, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0]
    z = [0, 6, 5, 5, 2, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0]

        Test values == FALSE
    x = [5, 6, 5, 5, 2, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0]
    y = [5, 6, 5, 5, 2, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0]
    z = [5, 6, 5, 5, 2, 1, 1, 1, 1, 1, 1, 1, 2, 0, 0]

        Test values == FALSE
    x = [0, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2, 0, 0]
    y = [0, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2, 0, 0]
    z = [0, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 2, 0, 0]

        Test values == FALSE
    x = [0, 6, 5, 5, 2, 1, 1, 1, 1, 1, 1, 5, 5, 5, 5]
    y = [0, 6, 5, 5, 2, 1, 1, 1, 1, 1, 1, 5, 5, 5, 5]
    z = [0, 6, 5, 5, 2, 1, 1, 1, 1, 1, 1, 5, 5, 5, 5]
    Main pattern recognition method*/
    public static boolean PatternRecognition(List<AccelerometerData> accelerometerData){
        double maxAcceleration = 0;
        double currentAcceleration;
        int index = 0;
        int iterations = 10;

        //iterating over the data and finds the point with the highest acceleration
        for (int i = 0; i < accelerometerData.size(); i++){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getY());
            if (currentAcceleration > maxAcceleration){
                index = i;
                maxAcceleration = currentAcceleration;
            }
        }

        if (preImpactPattern(accelerometerData, index, iterations,maxAcceleration) && impactPattern(accelerometerData, index, iterations,maxAcceleration) && postImpactPattern(accelerometerData, index+iterations)){
            return true;
        }
        return false;
    }
    /*
    impact pattern recognition:
    if there is a massive deaccelreation close after the max acceleration impact indicates that it is a fall.

        Test values == TRUE:
    x = [3, 4, 2, 6, 4, 4, 2]
    y = [3, 4, 2, 6, 4, 3, 1]
    z = [3, 4, 2, 6, 4, 4, 2]
    index = 3
    iterations = 5
    maxAcceleration = 10.39
    TESTimpactThreshold = 3

        Test values == FALSE:
    x = [6, 5, 5, 4, 4, 3, 0]
    y = [6, 5, 5, 4, 3, 3, 0]
    z = [6, 5, 5, 4, 3, 3, 0]
    index = 0
    iterations = 5
    maxAcceleration = 10.39
    TESTimpactThreshold = 3
     */
    private static boolean impactPattern(List<AccelerometerData> accelerometerData, int index, int iterations,double maxAcceleration){
        double currentAcceleration;

        //iterating from toppoint to see if there is a big deacceleration after it.
        for (int i = index+1; i <= index+iterations; i++){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getY());
            if (currentAcceleration*impactThreshold <= maxAcceleration){
                return true;
            }
            return false;
        }
        return false;
    }
    //FOR TESTING PURPOSES
    public static boolean impactPattern(List<AccelerometerData> accelerometerData, int index, int iterations,double maxAcceleration, double TESTimpactThreshold){
        double currentAcceleration;

        //iterating from toppoint to see if there is a big deacceleration after it.
        for (int i = index+1; i <= index+iterations; i++){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getY());
            if (currentAcceleration*TESTimpactThreshold <= maxAcceleration){
                return true;
            }
            return false;
        }
        return false;
    }

    /*
    pattern for pre impact:
    increase in acceleration, to the max
    hvis det ved et punkt før var mye lavere akslerasjon så indikerer det et fall i preimpact stadiet.

        Test values == TRUE:
    x = [1, 3, 4, 6]
    y = [1, 3, 4, 6]
    z = [1, 3, 4, 6]
    index = 3
    iterations = 5
    maxAcceleration = 10,39
    TESTpreimpactThreshold = 3

        Test values == FALSE
    x = [1, 3, 4, 4, 4, 4, 6]
    y = [1, 3, 4, 4, 4, 4, 6]
    z = [1, 3, 4, 4, 4, 4, 6]
    index = 6
    iterations = 5
    maxAcceleration = 10,39
    TESTpreimpactThreshold = 3
    */
    private static boolean preImpactPattern(List<AccelerometerData> accelerometerData, int index, int iterations,double maxAcceleration){
        double currentAcceleration;
        int endLoop = index-iterations;

        if (endLoop < 0){endLoop = 0;}

        for (int i = index-1; i >= endLoop; i--){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getZ());
            if (currentAcceleration*preimpactThreshold < maxAcceleration){return true;}
        }
        return false;
    }
    //for testing purposes
    public static boolean preImpactPattern(List<AccelerometerData> accelerometerData, int index, int iterations,double maxAcceleration, double TESTpreimpactThreshold){
        double currentAcceleration;

        for (int i = index-1; i >= index-iterations; i--){
            currentAcceleration = accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getZ());
            if (currentAcceleration*TESTpreimpactThreshold < maxAcceleration){return true;}
        }
        return false;
    }

    /*
    Pattern for post impact:
        Test values == TRUE
    x = [0, 0, 5, 6]
    y = [0, 0, 5, 6]
    z = [0, 0, 5, 6]
    index = 0
    TESTPostImpactThreshold = 5

        Test values == FALSE
    x = [0, 0, 5, 6]
    y = [0, 0, 5, 6]
    z = [0, 0, 5, 6]
    index = 2
    TESTPostImpactThreshold = 5
    */
    private static boolean postImpactPattern(List<AccelerometerData> accelerometerData, int index){
        double sumOfAccelerations = 0;

        for (int i = index; i < accelerometerData.size(); i++){
            sumOfAccelerations += accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getZ());
        }

        if(sumOfAccelerations/(accelerometerData.size()-index) < postImpactThreshold){
            return true;
        }
        return false;
    }
    //For testing
    public static boolean postImpactPattern(List<AccelerometerData> accelerometerData, int index, double TESTPostImpactThreshold){
        double sumOfAccelerations = 0;

        for (int i = index; i < accelerometerData.size(); i++){
            sumOfAccelerations += accelerationTotal(accelerometerData.get(i).getX(), accelerometerData.get(i).getY(), accelerometerData.get(i).getZ());
        }
        if(sumOfAccelerations/(accelerometerData.size()-index) < TESTPostImpactThreshold){
            return true;
        }
        return false;
    }




    public static boolean isPhoneVertical(double priorAngle, double postAngle, double angleThreshold)
    {
        return postAngle - priorAngle >= angleThreshold;
    }

    private static double verticalComparedToTotal(double vertical, double total)
    {
        return total/vertical;
    }

    //Calculates total acceleration at one point
    private static double accelerationTotal(double x, double y, double z)
    {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    //calculates vertical acceleration at one point
    private static double verticalAcceleration(double x, double y, double z, double tetaY, double tetaZ)
    {
        return Math.abs(x*Math.sin(tetaZ) + y*Math.sin(tetaY) - z*Math.cos(tetaY)*Math.cos(tetaZ));
    }
}
