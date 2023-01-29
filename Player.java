import java.util.Scanner;

class Player {
    static int laps;
    static int checkpointCount;
    static int[][] checkPoints;
    static String[] cp2go;             // remaining CPs for each pod
    static int leaderIdx;
    static boolean firstCpReached;
    static int[] pushCourse;
    static int[] crashCourse;
    static int[] nextButOneCp;
    static int opponentCpIdx;
    static int opponentSecondCpIdx;
    static int furthestCpId;

    static Pod[] pods;
    static Pod my1;
    static Pod my2;
    static Pod his1;
    static Pod his2;

    static class Pod {
        String name;
        String cp2go;
        double nextCpId;
        State state;
        State stateFormer;
        State stateFuture;

        public Pod(String name) {
            this.name = name;
            this.cp2go = "";
            this.nextCpId = 0;
            this.state = new State();
            this.stateFormer = new State();
            this.stateFuture = new State();
        }

        public void updateState(Scanner in) {
            this.state = new State();
            this.state.xy[0] = in.nextInt();     // x position of your pod
            this.state.xy[1] = in.nextInt();     // y position of your pod
                System.err.println(this.name + " - future error: " +
                                   distance(this.stateFuture.xy[0], this.stateFuture.xy[1], this.state.xy[0], this.state.xy[1]));
            this.state.v[0] = in.nextInt();      // x speed of your pod
            this.state.v[1] = in.nextInt();      // y speed of your pod
            this.state.angle = in.nextInt();     // angle of your pod
            this.state.nextCpIdx = in.nextInt(); // next check point id of your pod
            this.state.apex = computeApex(this.state.xy[0], this.state.xy[1], this.state.nextCpIdx);
            this.state.direction = angle2vector(this.state.angle);

            // future =: position + speed + 250 * angle
            this.stateFuture.xy[0] = (int) (this.state.xy[0] + this.state.v[0] + 100 * this.state.direction[0]);
            this.stateFuture.xy[1] = (int) (this.state.xy[1] + this.state.v[1] + 100 * this.state.direction[1]);
        }

        public void updateFormerState(State state) {
            this.stateFormer = new State(state);
        }
    }

    static class State {
        int[] xy;
        int[] v;
        int[] apex;
        double angle;
        int nextCpIdx;
        double[] direction;

        public State() {
            this.xy = new int[2];
            this.v = new int[2];
            this.apex = new int[2];
            this.angle = 0;
            this.direction = new double[2];
        }

        public State(State state) {
            this.xy = new int[]{state.xy[0], state.xy[1]};
            this.v = new int[]{state.v[0], state.v[1]};
            this.apex = new int[2];
            this.angle = state.angle;
            this.nextCpIdx = state.nextCpIdx;
            this.direction = state.direction;
        }
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        initializeRace();
        readInitialInputs(in);

        while (true) {
            readInputs(in);
            updateOpponentsProgress();
            outputForPod1();            // attack
            outputForPod2();            // race
            updateFormerVariables();
        }
    }

    private static void initializeRace() {
        my1 = new Pod("my1");
        my2 = new Pod("my2");
        his1 = new Pod("his1");
        his2 = new Pod("his2");
        pods = new Pod[]{my1, my2, his1, his2};
    }

    private static void updateOpponentsProgress() {
        firstCpReached = cp2go[leaderIdx].length() < checkpointCount * laps;
        for (int i = 0; i < 4; i++) {
            if (pods[i].state.nextCpIdx != Integer.parseInt(Character.toString(cp2go[i].charAt(0)))) {
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

        pushCourse[0] = (checkPoints[opponentCpIdx][0] + (int) ((pods[leaderIdx].stateFuture.xy[0] - checkPoints[opponentCpIdx][0]) / Math.PI));
        pushCourse[1] = (checkPoints[opponentCpIdx][1] + (int) ((pods[leaderIdx].stateFuture.xy[1] - checkPoints[opponentCpIdx][1]) / Math.PI));

        crashCourse[0] = (int) (pods[leaderIdx].stateFuture.xy[0] + ((checkPoints[opponentCpIdx][0] - pods[leaderIdx].stateFuture.xy[0]) / Math.PI));
        crashCourse[1] = (int) (pods[leaderIdx].stateFuture.xy[1] + ((checkPoints[opponentCpIdx][1] - pods[leaderIdx].stateFuture.xy[1]) / Math.PI));

        nextButOneCp[0] = checkPoints[opponentSecondCpIdx][0];
        nextButOneCp[1] = checkPoints[opponentSecondCpIdx][1];
    }

    private static void outputForPod1() {
        if (!firstCpReached) { // until opponent reaches the first CP, head to the 2nd CP
            System.out.println(checkPoints[2][0] + " " + checkPoints[2][1] + " 42");
        } else {
            System.err.println();
            System.err.println("opponentCpIdx: " + opponentCpIdx);
            System.err.println("leaderIdx: " + (leaderIdx - 1));


            int[] dirToCp = new int[2];
            dirToCp[0] = checkPoints[opponentCpIdx][0] - my1.stateFormer.xy[0];
            dirToCp[1] = checkPoints[opponentCpIdx][1] - my1.stateFormer.xy[1];
            int[] dirToLeader = new int[2];
            dirToLeader[0] = pods[leaderIdx].stateFuture.xy[0] - my1.stateFormer.xy[0];
            dirToLeader[1] = pods[leaderIdx].stateFuture.xy[1] - my1.stateFormer.xy[1];

            System.err.println("dirToCp[0]: " + dirToCp[0]);
            System.err.println("dirToCp[1]: " + dirToCp[1]);
            System.err.println("dirToLeader[0]: " + dirToLeader[0]);
            System.err.println("dirToLeader[1]: " + dirToLeader[1]);
            System.err.println();

            double alpha = angleDifference(vector2angle(dirToCp), vector2angle(dirToLeader)); // difference between my directions to the enemy and his CP
            double beta = angleDifference(my1.state.angle, vector2angle(dirToLeader)); // difference between orientation and direction to enemy

            // TODO: could hisDistanceToCp be used to improve attack?
            double hisDistanceToCp = distance(checkPoints[opponentCpIdx][0], checkPoints[opponentCpIdx][1],
                    pods[leaderIdx].stateFuture.xy[0], pods[leaderIdx].stateFuture.xy[1]);
            double myDistanceToHim = distance(pods[leaderIdx].stateFuture.xy[0], pods[leaderIdx].stateFuture.xy[1],
                    my1.state.xy[0], my1.state.xy[1]);
            double myDistanceToHisCp = distance(my1.state.xy[0], my1.state.xy[1],
                    checkPoints[opponentCpIdx][0], checkPoints[opponentCpIdx][1]);

            System.err.println("myDistanceToHisCp: " + myDistanceToHisCp);
            System.err.println("myDistanceToHim: " + myDistanceToHim);
            System.err.println();

            String thrust = " 100";
            int x;
            int y;
            if (alpha > 90) {
                x = crashCourse[0];
                y = crashCourse[1];
                if (beta > 45 && myDistanceToHim > 2000) {
                    thrust = " 15";
                }
            } else if (alpha > 30) {
                x = pushCourse[0];
                y = pushCourse[1];
            } else {
                if (myDistanceToHisCp > myDistanceToHim) {
                    x = nextButOneCp[0];
                    y = nextButOneCp[1];
                    thrust = " 25";
                } else {
                    x = crashCourse[0];
                    y = crashCourse[1];
                }
            }
            if (intercourse(0)) {
                thrust = " SHIELD";
            }
            String msg = " alpha:" + (int) alpha + " beta:" + (int) beta + ", x: " + x + ", y: " + y + ", thrust: " + thrust;
            System.out.println(x + " " + y + thrust + msg);
        }
    }

    private static void outputForPod2() {
        System.err.println("nextCheckPointIdMy2: " + my2.state.nextCpIdx);
        int xNextCp2 = my2.state.apex[0];
        int yNextCp2 = my2.state.apex[1];
        System.err.println("xNextCheckPointMy2: " + xNextCp2);
        System.err.println("yNextCheckPointMy2: " + yNextCp2);

        int[] directionToCp2 = new int [2];
        directionToCp2[0] = xNextCp2 - my2.state.xy[0];
        directionToCp2[1] = yNextCp2 - my2.state.xy[1];
        double angleToCp2 = vector2angle(directionToCp2);
        System.err.println("2nd pod oriented: " + my2.state.angle);
        System.err.println("2nd pod to CP: " + (int) angleToCp2);
        double formerDistToCP2 = distance(my2.stateFormer.xy[0], my2.stateFormer.xy[1], xNextCp2, yNextCp2);
        int nextCheckpointDist2 = (int) distance(my2.state.xy[0], my2.state.xy[1], xNextCp2, yNextCp2);
        double xCorrection2 = (my2.stateFormer.xy[0] != 0) ? (my2.state.xy[0] - my2.stateFormer.xy[0]) : 0;
        System.err.println("xCorrection2: " + xCorrection2);
        double yCorrection2 = (my2.stateFormer.xy[1] != 0) ? (my2.state.xy[1] - my2.stateFormer.xy[1]) : 0;
        System.err.println("yCorrection2: " + yCorrection2);
        double xCorrectedGoal2 = xNextCp2 - xCorrection2;
        System.err.println("xCorrectedGoal2: " + xCorrectedGoal2);
        double yCorrectedGoal2 = yNextCp2 - yCorrection2;
        System.err.println("yCorrectedGoal2: " + yCorrectedGoal2);

        String thrust2 = getThrust(nextCheckpointDist2, angleDifference(my2.state.angle, angleToCp2), formerDistToCP2, 1, my2.state.nextCpIdx);
        System.err.println("thrust2: " + thrust2);
        System.out.println((int) xCorrectedGoal2 + " " + (int) yCorrectedGoal2 + " " + thrust2);
    }

    private static void updateFormerVariables() {
        for (Pod pod : pods) {
            pod.updateFormerState(pod.state);
        }
    }

    private static void readInitialInputs(Scanner in) {
        laps = in.nextInt();
        checkpointCount = in.nextInt();
        checkPoints = new int[checkpointCount][2];
        for (int i = 0; i < checkpointCount; i++) {
            checkPoints[i][0] = in.nextInt();
            checkPoints[i][1] = in.nextInt();
            System.err.println("checkPoint[" + i + "]: " +
                    "(" + checkPoints[i][0] + ", " + checkPoints[i][1] + ")");
        }
        cp2go = cp2go();
        leaderIdx = 2;
        pushCourse = new int[2];
        crashCourse = new int[2];
        nextButOneCp = new int[2];
        pushCourse[0] = checkPoints[2][0];
        pushCourse[1] = checkPoints[2][1];
        findFurthestCp();

        System.err.println();
    }

    private static void findFurthestCp() {
        int[] intervalsLength = new int[checkpointCount];
        intervalsLength[0] = (int) distance(checkPoints[checkpointCount - 1][0], checkPoints[checkpointCount - 1][1], checkPoints[0][0], checkPoints[0][1]);
        System.err.println("intervalsLength[0]: " + intervalsLength[0]);
        for (int i = 1; i < checkpointCount; i++) {
            intervalsLength[i] = (int) distance(checkPoints[i - 1][0], checkPoints[i - 1][1], checkPoints[i][0], checkPoints[i][1]);
            System.err.println("intervalsLength[" + i + "]: " + intervalsLength[i]);
        }
        int maxIdx = 0;
        int maxDist = 0;
        for (int i = 0; i < checkpointCount; i++) {
            if (intervalsLength[i] > maxDist) {
                maxIdx = i;
                maxDist = intervalsLength[i];
            }
        }
        furthestCpId = maxIdx;
        System.err.println("furthestCpId: " + furthestCpId);
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
        my1.updateState(in);
        my2.updateState(in);
        his1.updateState(in);
        his2.updateState(in);
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

    public static String getThrust(double nextCheckpointDist, double nextCheckpointAngle, double formerGoalDist, int podIdx, int cPiD) {
        int gas = 100;
        System.err.println("nextCheckpointDist: " + nextCheckpointDist);
        System.err.println("angle difference: " + (int) nextCheckpointAngle);
        System.err.println("formerGoalDist: " + (int) formerGoalDist);
        if (Math.abs(nextCheckpointAngle) > 90 && // wrong direction & moving
                pods[podIdx].state.v[0] != 0 && pods[podIdx].state.v[1] != 0) {
            return "0";
        }

        if (formerGoalDist > nextCheckpointDist &&      // getting closer to CP
                nextCheckpointDist < 3500) {
            gas -= 11;
        }

        if (formerGoalDist > nextCheckpointDist &&      // getting even more close
                nextCheckpointDist < 2500) {
            gas -= 11;
        }

        if (Math.abs(nextCheckpointAngle) > 35 ) {      // CP is not straight ahead
            gas -= 23;
        }

        if (Math.abs(nextCheckpointAngle) > 65 ) {      // CP is not straight ahead
            gas -= 25;
        }

        if (nextCheckpointDist < 5000) {                // CP is not far
            gas -= 2;
        }

        if (nextCheckpointDist < 3000) {                // CP is not far
            gas -= 10;
        }

        String thrust = "" + gas;

        if (firstCpReached && intercourse(podIdx)) {
            thrust = "SHIELD";
        }

        if (cPiD == furthestCpId && nextCheckpointAngle < 30) {
            thrust = "BOOST";
        }

        return thrust;
    }

    private static boolean intercourse(int podIdx) {
        if (distance(pods[podIdx].stateFuture.xy[0], pods[podIdx].stateFuture.xy[1],
                     pods[2].stateFuture.xy[0], pods[2].stateFuture.xy[1]) < 1000) {
            System.err.println("Collision of pods my#" + (podIdx + 1) + " and his#1 predicted!");
            return true;
        }
        if (distance(pods[podIdx].stateFuture.xy[0], pods[podIdx].stateFuture.xy[1],
                     pods[3].stateFuture.xy[0], pods[3].stateFuture.xy[1]) < 1000) {
            System.err.println("Collision of pods my#" + (podIdx + 1) + " and his#2 predicted!");
            return true;
        }
        return false;
    }
}