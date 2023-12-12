import java.util.Collections;
import java.util.Comparator;

import static java.util.Arrays.sort;

public class ActivitySolver {
    class Activity implements Comparable<Activity> {
        public int startTime;
        public int endTime;
        public int profit;

        public Activity(int startTime, int endTime, int profit){
            this.startTime = startTime;
            this.endTime = endTime;
            this.profit = profit;
        }

        @Override
        public boolean equals(Object activity){
            if (activity == this)
                return true;
            if(!(activity instanceof Activity))
                return false;
            Activity a = (Activity) activity;
            return this.startTime == a.startTime && this.endTime == a.endTime && this.profit == a.profit;
        }
        @Override
        public int compareTo(Activity activity) {
            if(this.equals(activity))
                return 0;
            if(this.endTime < activity.endTime)
                return -1;
            if(this.endTime == activity.endTime && this.startTime < activity.startTime)
                return -1;
            return 1;
        }
    }

    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        Activity[] a = new Activity[startTime.length];
        for (int i = 0; i < startTime.length; i++)
            a[i] = new Activity(startTime[i], endTime[i], profit[i]);

        sort(a);
        int n = a.length;

        int[] dp = new int[n+1];
        for (int i = 1; i <= n; i++) {

            int j = getMostRecentCompatibleActivity(a, i);
            dp[i] = Math.max(dp[i-1], dp[j] + a[i-1].profit);
        }

        return dp[n];
    }

    public int getMostRecentCompatibleActivity(Activity[] a, int i) {
        int lo = 1, hi = i;
        while(lo < hi) {
            int mid = (lo + hi) / 2;
            if(a[mid-1].endTime <= a[i-1].startTime)
                lo = mid+1;
            else
                hi = mid;
        }

        return lo-1;
    }
}
