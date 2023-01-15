import java.util.Scanner;

class Player {
    static int laps;
    static int checkpointCount;
    static int[][] checkPoints;
    static int[][] apexes;

    static int xMy1;                // x position of your pod
    static int yMy1;                // y position of your pod
    static int vxMy1;               // x speed of your pod
    static int vyMy1;               // y speed of your pod
    static int angleMy1;            // angle of your pod (0 ... EAST, 90 ... SOUTH)
    static int nextCheckPointIdMy1; // next check point id of your pod

    static int xMy2;                // x position of your 2nd pod
    static int yMy2;                // y position of your 2nd pod
    static int vxMy2;               // x speed of your 2nd pod
    static int vyMy2;               // y speed of your 2nd pod
    static int angleMy2;            // angle of your 2nd pod
    static int nextCheckPointIdMy2; // next check point id of your 2nd pod

    static int xHis1;               // x position of the opponent's pod
    static int yHis1;               // y position of the opponent's pod
    static int vxHis1;              // x speed of the opponent's pod
    static int vyHis1;              // y speed of the opponent's pod
    static int angleHis1;           // angle of the opponent's pod
    static int nextCheckPointIdHis1;// next check point id of the opponent's pod

    static int xHis2;               // x position of the opponent's 2nd pod
    static int yHis2;               // y position of the opponent's 2nd pod
    static int vxHis2;              // x speed of the opponent's 2nd pod
    static int vyHis2;              // y speed of the opponent's 2nd pod
    static int angleHis2;           // angle of the opponent's 2nd pod
    static int nextCheckPointIdHis2;// next check point id of the opponent's 2nd pod

    static int xMy1Past;            // former x position of your pod
    static int yMy1Past;            // former y position of your pod

    static int xMy2Past;            // former x position of your 2nd pod
    static int yMy2Past;            // former y position of your 2nd pod

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        readInitialInputs(in);

        while (true) {
            readInputs(in);
            outputForPod1();
            outputForPod2();
            updatePastValues();
        }
    }

    private static void outputForPod1() {
            System.err.println("nextCheckPointIdMy1: " + nextCheckPointIdMy1);
        int xNextCp1 = apexes[nextCheckPointIdMy1][0];
        int yNextCp1 = apexes[nextCheckPointIdMy1][1];
            System.err.println("xNextCheckPointMy1: " + xNextCp1);
            System.err.println("yNextCheckPointMy1: " + yNextCp1);

        int[] dirToCp1 = new int [2];
        dirToCp1[0] = xNextCp1 - xMy1;
        dirToCp1[1] = yNextCp1 - yMy1;
        double angleToCp1 = angleFromCp(dirToCp1);
            System.err.println("1st pod oriented: " + angleMy1);
            System.err.println("1st pod to CP: " + (int) angleToCp1);
        double formerDistToCP1 = dist(xMy1Past, yMy1Past, xNextCp1, yNextCp1);
        int nextCheckpointDist1 = (int) dist(xMy1, yMy1, xNextCp1, yNextCp1);
        int xCorrection1 = (xMy1Past != 0) ? (xMy1 - xMy1Past) : 0;
            System.err.println("xCorrection1: " + xCorrection1);
        int yCorrection1 = (yMy1Past != 0) ? (yMy1 - yMy1Past) : 0;
            System.err.println("yCorrection1: " + yCorrection1);
        int xCorrectedGoal1 = xNextCp1 - xCorrection1;
            System.err.println("xCorrectedGoal1: " + xCorrectedGoal1);
        int yCorrectedGoal1 = yNextCp1 - yCorrection1;
            System.err.println("yCorrectedGoal1: " + yCorrectedGoal1);

        String thrust1 = getThrust(nextCheckpointDist1, angleDifference(angleMy1, angleToCp1), formerDistToCP1);
        System.out.println(xCorrectedGoal1 + " " + yCorrectedGoal1 + " " + thrust1);
            System.err.println();
    }

    private static void outputForPod2() {
            System.err.println("nextCheckPointIdMy2: " + nextCheckPointIdMy2);
        int xNextCp2 = apexes[nextCheckPointIdMy2][0];
        int yNextCp2 = apexes[nextCheckPointIdMy2][1];
            System.err.println("xNextCheckPointMy2: " + xNextCp2);
            System.err.println("yNextCheckPointMy2: " + yNextCp2);

        int[] dirToCp2 = new int [2];
        dirToCp2[0] = xNextCp2 - xMy2;
        dirToCp2[1] = yNextCp2 - yMy2;
        double angleToCp2 = angleFromCp(dirToCp2);
            System.err.println("2nd pod oriented: " + angleMy2);
            System.err.println("2nd pod to CP: " + (int) angleToCp2);
        double formerDistToCP2 = dist(xMy2Past, yMy2Past, xNextCp2, yNextCp2);
        int nextCheckpointDist2 = (int) dist(xMy2, yMy2, xNextCp2, yNextCp2);
        int xCorrection2 =  (xMy2Past != 0) ? (xMy2 - xMy2Past) : 0;
            System.err.println("xCorrection2: " + xCorrection2);
        int yCorrection2 =  (yMy2Past != 0) ? (yMy2 - yMy2Past) : 0;
            System.err.println("yCorrection2: " + yCorrection2);
        int xCorrectedGoal2 = xNextCp2 - xCorrection2;
            System.err.println("xCorrectedGoal2: " + xCorrectedGoal2);
        int yCorrectedGoal2 = yNextCp2 - yCorrection2;
            System.err.println("yCorrectedGoal2: " + yCorrectedGoal2);

        String thrust2 = getThrust(nextCheckpointDist2, angleDifference(angleMy2, angleToCp2), formerDistToCP2);
        System.out.println(xCorrectedGoal2 + " " + yCorrectedGoal2 + " " + thrust2);
    }

    private static void updatePastValues() {
        xMy1Past = xMy1;
        yMy1Past = yMy1;
        xMy2Past = xMy2;
        yMy2Past = yMy2;
    }

    private static void readInitialInputs(Scanner in) {
        laps = in.nextInt();
        checkpointCount = in.nextInt();
        checkPoints = new int[checkpointCount][2];
        apexes = new int[checkpointCount][2];
        for (int i = 0; i < checkpointCount; i++) {
            checkPoints[i][0] = in.nextInt();
            checkPoints[i][1] = in.nextInt();
            System.err.println("checkPoint[" + i + "]: " +
                               "(" + checkPoints[i][0] + ", " + checkPoints[i][1] + ")");
        }
        computeApexes();
        System.err.println();
    }

    public static void computeApexes() {
        int xPrevious = checkPoints[checkpointCount - 1][0];
        int yPrevious = checkPoints[checkpointCount - 1][1];
        int xCp = checkPoints[0][0];
        int yCp = checkPoints[0][1];
        int xNext = checkPoints[1][0];
        int yNext = checkPoints[1][1];
        double [] apexFromCpRadius = apexFromCp(xPrevious, xCp, xNext, yPrevious, yCp, yNext);
        // apex for CP 0:
        apexes[0][0] = (int) (checkPoints[0][0] + apexFromCpRadius[0]);
        apexes[0][1] = (int) (checkPoints[0][1] + apexFromCpRadius[1]);

        for (int i = 1; i < checkpointCount - 1; i++) {
            // apex for CP i:
            xPrevious = checkPoints[i - 1][0];
            yPrevious = checkPoints[i - 1][1];
            xCp = checkPoints[i][0];
            yCp = checkPoints[i][1];
            xNext = checkPoints[i + 1][0];
            yNext = checkPoints[i + 1][1];
            apexFromCpRadius = apexFromCp(xPrevious, xCp, xNext, yPrevious, yCp, yNext);
            apexes[i][0] = (int) (checkPoints[i][0] + apexFromCpRadius[0]);
            apexes[i][1] = (int) (checkPoints[i][1] + apexFromCpRadius[1]);
        }

        // apex for CP checkpointCount-1:
        xPrevious = checkPoints[checkpointCount - 2][0];
        yPrevious = checkPoints[checkpointCount - 2][1];
        xCp = checkPoints[checkpointCount - 1][0];
        yCp = checkPoints[checkpointCount - 1][1];
        xNext = checkPoints[0][0];
        yNext = checkPoints[0][1];
        apexFromCpRadius = apexFromCp(xPrevious, xCp, xNext, yPrevious, yCp, yNext);
        apexes[checkpointCount - 1][0] = (int) (checkPoints[checkpointCount - 1][0] + apexFromCpRadius[0]);
        apexes[checkpointCount - 1][1] = (int) (checkPoints[checkpointCount - 1][1] + apexFromCpRadius[1]);
    }

    private static double[] apexFromCp(int xPrevious, int xCp, int xNext, int yPrevious, int yCp, int yNext) {
        double [] nextDirection = new double [2];
        double [] previousDirection = new double [2];
        double [] apexDirection = new double [2];

        nextDirection[0] = xNext - xCp;
        nextDirection[1] = yNext - yCp;
        nextDirection = normalizeVector(nextDirection, 1);

        previousDirection[0] = xPrevious - xCp;
        previousDirection[1] = yPrevious - yCp;
        previousDirection = normalizeVector(previousDirection, 1);

        apexDirection[0] = previousDirection[0] + nextDirection[0];
        apexDirection[1] = previousDirection[1] + nextDirection[1];
        apexDirection = normalizeVector(apexDirection, 590);

        return apexDirection;
    }

    private static double vectorLength(double [] direction) {
        return Math.sqrt(direction[0] * direction[0] + direction[1] * direction[1]);
    }

    private static double[] normalizeVector(double[] vector, int length) {
        double [] normalized = new double [2];
        double norm = vectorLength(vector);
        normalized[0] = vector[0] / norm * length;
        normalized[1] = vector[1] / norm * length;
        return normalized;
    }

    private static void readInputs(Scanner in) {
        xMy1 = in.nextInt();                // x position of your pod
        yMy1 = in.nextInt();                // y position of your pod
        vxMy1 = in.nextInt();               // x speed of your pod
        vyMy1 = in.nextInt();               // y speed of your pod
        angleMy1 = in.nextInt();            // angle of your pod
        nextCheckPointIdMy1 = in.nextInt(); // next check point id of your pod

        xMy2 = in.nextInt();                // x position of your 2nd pod
        yMy2 = in.nextInt();                // y position of your 2nd pod
        vxMy2 = in.nextInt();               // x speed of your 2nd pod
        vyMy2 = in.nextInt();               // y speed of your 2nd pod
        angleMy2 = in.nextInt();            // angle of your 2nd pod
        nextCheckPointIdMy2 = in.nextInt(); // next check point id of your 2nd pod

        xHis1 = in.nextInt();               // x position of the opponent's pod
        yHis1 = in.nextInt();               // y position of the opponent's pod
        vxHis1 = in.nextInt();              // x speed of the opponent's pod
        vyHis1 = in.nextInt();              // y speed of the opponent's pod
        angleHis1 = in.nextInt();           // angle of the opponent's pod
        nextCheckPointIdHis1 = in.nextInt();// next check point id of the opponent's pod

        xHis2 = in.nextInt();               // x position of the opponent's 2nd pod
        yHis2 = in.nextInt();               // y position of the opponent's 2nd pod
        vxHis2 = in.nextInt();              // x speed of the opponent's 2nd pod
        vyHis2 = in.nextInt();              // y speed of the opponent's 2nd pod
        angleHis2 = in.nextInt();           // angle of the opponent's 2nd pod
        nextCheckPointIdHis2 = in.nextInt();// next check point id of the opponent's 2nd pod
    }


    public static double dist(int aX, int aY, int bX, int bY) {
        double verticalDistance = Math.abs(bY - aY);
        double horizontalDistance = Math.abs(bX - aX);
        return Math.hypot(verticalDistance, horizontalDistance);
    }

    public static double angleFromCp(int[] dirToCp1) {
        int [] azimut = new int[2];
        azimut[0] = 1;
        azimut[1] = 0;
        int num = (dirToCp1[0] * azimut[0] + dirToCp1[1] * azimut[1]);
        double den = (Math.sqrt(Math.pow(dirToCp1[0], 2) + Math.pow(dirToCp1[1], 2)) *
                (Math.sqrt(Math.pow(1, 2) + Math.pow(0, 2))) );
        double cos =  num / den;
        double degrees = Math.acos(cos)*180/Math.PI;
        if (dirToCp1[1] < 0) {
            degrees = 360 - degrees;
        }
        return degrees;
    }

    public static double angleDifference(double a, double b) {
        return 180 - Math.abs(Math.abs(a - b) - 180);
    }

    public static String getThrust(int nextCheckpointDist, double nextCheckpointAngle, double formerGoalDist) {
        String thrust = "BOOST";
        System.err.println("nextCheckpointDist: " + nextCheckpointDist);
        System.err.println("angle difference: " + (int) nextCheckpointAngle);
        System.err.println("formerGoalDist: " + (int) formerGoalDist);
        if (Math.abs(nextCheckpointAngle) > 100) {      // wrong direction
            return "5";
        }

        if (formerGoalDist > nextCheckpointDist &&      // getting closer to CP
                nextCheckpointDist < 3500) {
            return "80";
        }

        if (formerGoalDist > nextCheckpointDist &&      // getting even more close
                nextCheckpointDist < 2500) {
            return "40";
        }

        if (nextCheckpointDist < 2500 &&                // CP is close
                Math.abs(nextCheckpointAngle) > 33) {   // CP is not straight ahead
            return "20";
        }

        if (Math.abs(nextCheckpointAngle) > 60 &&       // CP is not ahead
                nextCheckpointDist < 5000) {            // CP is not far
            thrust = "65";
        }
        return thrust;
    }
}