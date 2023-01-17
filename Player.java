import java.util.Scanner;

class Player {
    static int laps;
    static int checkpointCount;
    static int[][] checkPoints;
    static int[] apexForPod1;
    static int[] apexForPod2;
    static double[][] variables;     // [my1, my2, his1, his2][0:x, 1:y, 2:vx, 3:vy, 4:angle, 5:nextCpId]
    static double[][] pastVariables; // [my1, my2, his1, his2][0:x, 1:y, 2:vx, 3:vy, 4:angle, 5:nextCpId]
    static double[][] myFuture;      // predicted position of my pods
    static double[][] hisFuture;     // predicted position of opponent's pods
    static String[] cp2go;           // remaining CPs for each pod
    static int leaderIdx;
    static boolean firstCpReached;
    static int[] pushCourse;
    static int[] crashCourse;
    static int[] nextButOneCp;
    static int opponentCpIdx;
    static int opponentSecondCpIdx;

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        readInitialInputs(in);

        while (true) {
            readInputs(in);
            updateVariables();
            outputForPod1();        // attack
            outputForPod2();        // race
            updatePastVariables();
        }
    }

    public static void updateVariables() {
        System.err.println("His1Future error: " + distance(hisFuture[0][0], hisFuture[0][1], variables[2][0], variables[2][1]));
        System.err.println("His2Future error: " + distance(hisFuture[1][0], hisFuture[1][1], variables[3][0], variables[3][1]));

        updateOpponentsProgress();

        apexForPod1 = computeApex((int) variables[0][0], (int) variables[0][1], (int) variables[0][5]);
        apexForPod2 = computeApex((int) variables[1][0], (int) variables[1][1], (int) variables[1][5]);

        // future =: position + speed + 250 * angle
        double[] myDirection1 = angle2vector(variables[0][4]);
        double[] myDirection2 = angle2vector(variables[1][4]);
        double[] hisDirection1 = angle2vector(variables[2][4]);
        double[] hisDirection2 = angle2vector(variables[3][4]);
        myFuture[0][0]  = variables[0][0] + variables[0][2] + 100 * myDirection1[0];  // my 1st x
        myFuture[0][1]  = variables[0][1] + variables[0][3] + 100 * myDirection1[1];  // my 1st y
        myFuture[1][0]  = variables[1][0] + variables[1][2] + 100 * myDirection2[0];  // my 2nd x
        myFuture[1][1]  = variables[1][1] + variables[1][3] + 100 * myDirection2[1];  // my 2nd y
        hisFuture[0][0] = variables[2][0] + variables[2][2] + 100 * hisDirection1[0]; // his 1st x
        hisFuture[0][1] = variables[2][1] + variables[2][3] + 100 * hisDirection1[1]; // his 2nd y
        hisFuture[1][0] = variables[3][0] + variables[3][2] + 100 * hisDirection2[0]; // his 1st x
        hisFuture[1][1] = variables[3][1] + variables[3][3] + 100 * hisDirection2[1]; // his 2nd y
    }

    private static void updateOpponentsProgress() {
        firstCpReached = cp2go[leaderIdx].length() < checkpointCount * laps;
        for (int i = 0; i < 4; i++) {
            if ((int) variables[i][5] != Integer.parseInt(Character.toString(cp2go[i].charAt(0)))) {
                cp2go[i] = cp2go[i].substring(1);
            }
            System.err.println("cp2go[" + i + "]: " + cp2go[i]);
        }
        if (cp2go[2].length() < cp2go[3].length()) {
            leaderIdx = 2;
        }
        if (cp2go[2].length() > cp2go[3].length()) {
            leaderIdx = 3;
        }
        updateTarget();
        System.err.println("Opponent's leader: pod #" + (leaderIdx - 1));
    }

    private static void updateTarget() {
        opponentCpIdx = Integer.parseInt(Character.toString(cp2go[leaderIdx].charAt(0)));
        if (cp2go[leaderIdx].length() > 1) {
            opponentSecondCpIdx = Integer.parseInt(Character.toString(cp2go[leaderIdx].charAt(1)));
        } else {
            opponentSecondCpIdx = opponentCpIdx;
        }

        pushCourse[0] = (checkPoints[opponentCpIdx][0] + (int) ((hisFuture[leaderIdx - 2][0] - checkPoints[opponentCpIdx][0]) / Math.PI));
        pushCourse[1] = (checkPoints[opponentCpIdx][1] + (int) ((hisFuture[leaderIdx - 2][1] - checkPoints[opponentCpIdx][1]) / Math.PI));

        crashCourse[0] = (int) (hisFuture[leaderIdx - 2][0] + ((checkPoints[opponentCpIdx][0] - hisFuture[leaderIdx - 2][0]) / 2));
        crashCourse[1] = (int) (hisFuture[leaderIdx - 2][1] + ((checkPoints[opponentCpIdx][1] - hisFuture[leaderIdx - 2][1]) / 2));

        nextButOneCp[0] = checkPoints[opponentSecondCpIdx][0];
        nextButOneCp[1] = checkPoints[opponentSecondCpIdx][1];
    }

    private static void outputForPod1() {
        if (!firstCpReached) { // until opponent reaches the first CP, head to the 2nd CP
            System.out.println(checkPoints[2][0] + " " + checkPoints[2][1] + " 42");
        } else {
            String thrust = " 100";
            if (intercourse(0)) {
                thrust = " SHIELD";
            }
            double hisDistanceToCp = distance(checkPoints[opponentCpIdx][0], checkPoints[opponentCpIdx][1],
                                              hisFuture[leaderIdx - 2][0], hisFuture[leaderIdx - 2][1]);
            double myDistanceToHim = distance(hisFuture[leaderIdx - 2][0], hisFuture[leaderIdx - 2][1],
                                              variables[1][0], variables[1][1]);
            double myDistanceToHisCp = distance(variables[1][0], variables[1][1],
                                                checkPoints[opponentCpIdx][0], checkPoints[opponentCpIdx][1]);
            if (hisDistanceToCp > 1500 ||
                myDistanceToHim < 1000) {
                if (myDistanceToHisCp > myDistanceToHim) {
                    System.out.println(pushCourse[0] + " " + pushCourse[1] + thrust);
                } else {
                    System.out.println(crashCourse[0] + " " + crashCourse[1] + thrust);
                }
            } else {
                System.out.println(nextButOneCp[0] + " " + nextButOneCp[1] + " 25");
            }
        }
    }

    private static void outputForPod2() {
        System.err.println("nextCheckPointIdMy2: " + variables[1][5]);
        int xNextCp2 = apexForPod2[0];
        int yNextCp2 = apexForPod2[1];
        System.err.println("xNextCheckPointMy2: " + xNextCp2);
        System.err.println("yNextCheckPointMy2: " + yNextCp2);

        int[] directionToCp2 = new int [2];
        directionToCp2[0] = (int) (xNextCp2 - variables[1][0]);
        directionToCp2[1] = (int) (yNextCp2 - variables[1][1]);
        double angleToCp2 = vector2angle(directionToCp2);
        System.err.println("2nd pod oriented: " + variables[1][4]);
        System.err.println("2nd pod to CP: " + (int) angleToCp2);
        double formerDistToCP2 = distance(pastVariables[1][0], pastVariables[1][1], xNextCp2, yNextCp2);
        int nextCheckpointDist2 = (int) distance(variables[1][0], variables[1][1], xNextCp2, yNextCp2);
        double xCorrection2 = (pastVariables[1][0] != 0) ? (variables[1][0] - pastVariables[1][0]) : 0;
        System.err.println("xCorrection2: " + xCorrection2);
        double yCorrection2 = (pastVariables[1][1] != 0) ? (variables[1][1] - pastVariables[1][1]) : 0;
        System.err.println("yCorrection2: " + yCorrection2);
        double xCorrectedGoal2 = xNextCp2 - xCorrection2;
        System.err.println("xCorrectedGoal2: " + xCorrectedGoal2);
        double yCorrectedGoal2 = yNextCp2 - yCorrection2;
        System.err.println("yCorrectedGoal2: " + yCorrectedGoal2);

        String thrust2 = getThrust(nextCheckpointDist2, angleDifference(variables[1][4], angleToCp2), formerDistToCP2, 1);
        System.err.println("thrust2: " + thrust2);
        System.out.println((int) xCorrectedGoal2 + " " + (int) yCorrectedGoal2 + " " + thrust2);
    }

    private static void updatePastVariables() {
        pastVariables = variables;
    }

    private static void readInitialInputs(Scanner in) {
        laps = in.nextInt();
        checkpointCount = in.nextInt();
        checkPoints = new int[checkpointCount][2];
        apexForPod1 = new int[2];
        apexForPod2 = new int[2];
        variables = new double[4][6];
        pastVariables = new double[4][6];
        myFuture = new double[2][2];
        hisFuture = new double[2][2];
        for (int i = 0; i < checkpointCount; i++) {
            checkPoints[i][0] = in.nextInt();
            checkPoints[i][1] = in.nextInt();
            System.err.println("checkPoint[" + i + "]: " +
                    "(" + checkPoints[i][0] + ", " + checkPoints[i][1] + ")");
        }
        apexForPod1 = computeApex(checkPoints[checkpointCount - 1][0], checkPoints[checkpointCount - 1][1], 0);
        apexForPod2 = computeApex(checkPoints[checkpointCount - 1][0], checkPoints[checkpointCount - 1][1], 0);
        cp2go = cp2go();
        leaderIdx = 2;
        pushCourse = new int[2];
        crashCourse = new int[2];
        nextButOneCp = new int[2];
        pushCourse[0] = checkPoints[2][0];
        pushCourse[1] = checkPoints[2][1];
        System.err.println();
    }

    public static String[] cp2go() {
        StringBuilder raceBuilder = new StringBuilder();
        for (int l = 1; l <= laps; l++) {
            for (int cp = 1; cp < checkpointCount; cp++) {
                raceBuilder.append(cp);
            }
            raceBuilder.append("0");
        }
        String race = raceBuilder.toString();
        System.err.println(race);
        String[] cp2go = new String[4];
        for (int i = 0; i < 4; i++) {
            cp2go[i] = race;
        }
        return cp2go;
    }

    public static int[] computeApex(int myX, int myY, int nextCpId) {
        int xCp = checkPoints[nextCpId][0];
        int yCp = checkPoints[nextCpId][1];
        int nextCpIdx = nextCpId + 1;
        if (nextCpId == checkpointCount - 1) {
            nextCpIdx = 0;
        }
        int xNext = checkPoints[nextCpIdx][0];
        int yNext = checkPoints[nextCpIdx][1];
        double[] apexFromCpRadius = apexFromCp(myX, xCp, xNext, myY, yCp, yNext);
        int[] apex = new int[2];
        apex[0] = (int) (checkPoints[nextCpId][0] + apexFromCpRadius[0]);
        apex[1] = (int) (checkPoints[nextCpId][1] + apexFromCpRadius[1]);
        return apex;
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
        // [my1, my2, his1, his2][0:x, 1:y, 2:vx, 3:vy, 4:angle, 5:nextCpId, 6:pastX, 7:pastY]
        variables[0][0] = in.nextInt();     // x position of your pod
        variables[0][1] = in.nextInt();     // y position of your pod
        variables[0][2] = in.nextInt();     // x speed of your pod
        variables[0][3] = in.nextInt();     // y speed of your pod
        variables[0][4] = in.nextInt();     // angle of your pod
        variables[0][5] = in.nextInt();     // next check point id of your pod

        variables[1][0] = in.nextInt();     // x position of your pod
        variables[1][1] = in.nextInt();     // y position of your pod
        variables[1][2] = in.nextInt();     // x speed of your pod
        variables[1][3] = in.nextInt();     // y speed of your pod
        variables[1][4] = in.nextInt();     // angle of your pod
        variables[1][5] = in.nextInt();     // next check point id of your pod

        variables[2][0] = in.nextInt();     // x position of the opponent's pod
        variables[2][1] = in.nextInt();     // y position of the opponent's pod
        variables[2][2] = in.nextInt();     // x speed of the opponent's pod
        variables[2][3] = in.nextInt();     // y speed of the opponent's pod
        variables[2][4] = in.nextInt();     // angle of the opponent's pod
        variables[2][5] = in.nextInt();     // next check point id of the opponent's pod

        variables[3][0] = in.nextInt();     // x position of the opponent's 2nd pod
        variables[3][1] = in.nextInt();     // y position of the opponent's 2nd pod
        variables[3][2] = in.nextInt();     // x speed of the opponent's 2nd pod
        variables[3][3] = in.nextInt();     // y speed of the opponent's 2nd pod
        variables[3][4] = in.nextInt();     // angle of the opponent's 2nd pod
        variables[3][5] = in.nextInt();     // next check point id of the opponent's 2nd pod
    }


    public static double distance(double aX, double aY, double bX, double bY) {
        double verticalDistance = Math.abs(bY - aY);
        double horizontalDistance = Math.abs(bX - aX);
        return Math.hypot(verticalDistance, horizontalDistance);
    }

    public static double vector2angle(int[] dirToCp1) {
        int [] azimut = new int[2];
        azimut[0] = 1;
        azimut[1] = 0;
        double num = (dirToCp1[0] * azimut[0] + dirToCp1[1] * azimut[1]);
        double den = (Math.sqrt(Math.pow(dirToCp1[0], 2) + Math.pow(dirToCp1[1], 2)) *
                (Math.sqrt(Math.pow(1, 2) + Math.pow(0, 2))) );
        double cos =  num / den;
        double degrees = Math.acos(cos) * 180 / Math.PI;
        if (dirToCp1[1] < 0) {
            degrees = 360 - degrees;
        }
        return degrees;
    }

    public static double[] angle2vector(double degrees) {
        double[] vector = new double[2];
        double rad = Math.PI * (0.5 + degrees / 180);
        if (rad > Math.PI) {
            rad -= 2 * Math.PI;
        }
        vector[0] = Math.sin(rad);
        vector[1] = Math.cos(rad + Math.PI);
        return vector;
    }

    public static double angleDifference(double a, double b) {
        return 180 - Math.abs(Math.abs(a - b) - 180);
    }

    public static String getThrust(double nextCheckpointDist, double nextCheckpointAngle, double formerGoalDist, int podIdx) {
        String thrust = "BOOST";
        System.err.println("nextCheckpointDist: " + nextCheckpointDist);
        System.err.println("angle difference: " + (int) nextCheckpointAngle);
        System.err.println("formerGoalDist: " + (int) formerGoalDist);
        if (Math.abs(nextCheckpointAngle) > 90 && // wrong direction & moving
                variables[podIdx][2] != 0 && variables[podIdx][3] != 0) {
            return "0";
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

        if (intercourse(podIdx)) {
            thrust = "SHIELD";
        }

        return thrust;
    }

    private static boolean intercourse(int podIdx) {
        if (distance(myFuture[podIdx][0], myFuture[podIdx][1], hisFuture[0][0], hisFuture[0][1]) < 800) {
            System.err.println("Collision of pods my#" + (podIdx + 1) + " and his#1 predicted!");
            return true;
        }
        if (distance(myFuture[podIdx][0], myFuture[podIdx][1], hisFuture[1][0], hisFuture[1][1]) < 800) {
            System.err.println("Collision of pods my#" + (podIdx + 1) + " and his#2 predicted!");
            return true;
        }
        return false;
    }
}
