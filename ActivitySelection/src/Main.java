public class Main {
    public static void main(String[] args) {
        int[] startTime = new int[]{33,8,9,18,16,36,18,4,42,45,29,43};
        int[] endTime = new int[]{40,16,32,39,46,43,28,13,44,46,39,44};
        int[] profit = new int[]{2,6,5,14,5,19,5,12,19,14,14,17};

        ActivitySolver a = new ActivitySolver();

        System.out.println(a.jobScheduling(startTime, endTime, profit));

    }
}