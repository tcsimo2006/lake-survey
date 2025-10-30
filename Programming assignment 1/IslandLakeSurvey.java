import java.util.*;
import java.io.*;

class Info {
    int row, col;
    public Info(int r, int c) {
        row = r;
        col = c;
    }
}

public class IslandLakeSurvey {
    static Partition<Info> BP;
    static Node<Info>[][] cluster;
    static int[][] grid;
    static Partition<Info> WP;
    static Node<Info>[][] watercluster;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int S = sc.nextInt();
        int T = sc.nextInt();
        sc.nextLine();

        grid = new int[S][T];
        cluster = new Node[S][T];
        BP = new Partition<>();
        WP = new Partition<>();
        watercluster = new Node[S][T];

        for (int i = 0; i < S; i++) {
            String line = sc.nextLine().trim();
            for (int j = 0; j < T; j++) {
                grid[i][j] = line.charAt(j) - '0';
            }
        }

        int wdx[] = {-1, 0, 1, 0, -1, -1, 1, 1};
        int wdy[] = {0, -1, 0, 1, -1, 1, -1, 1};
        int dx[] = {-1, 0, 1, 0};
        int dy[] = {0, -1, 0, 1};

        // Create initial clusters
        createClusters(S, T);

        // Union adjacent clusters
        unionClusters(S, T, dx, dy, wdx, wdy);

        // Identify lakes
        Map<Node<Info>, Boolean> isLake = identifyLakes(S, T);

        // Print initial survey
        printSurvey(S, T, wdx, wdy, isLake);

        // Handle new phases of land appearing
        int numberOfPhases = sc.nextInt();
        for (int phase = 0; phase < numberOfPhases; phase++) {
            int numberofChanges = sc.nextInt();
            for (int changes = 0; changes < numberofChanges; changes++) {
                int i = sc.nextInt();
                int j = sc.nextInt();

                if (grid[i][j] == 0) { // turn water into land
                    grid[i][j] = 1;
                    cluster[i][j] = BP.makeCluster(new Info(i, j));
                    watercluster[i][j] = null;

                    for (int D = 0; D < 4; D++) {
                        int ni = i + dx[D];
                        int nj = j + dy[D];
                        if (ni >= 0 && ni < S && nj >= 0 && nj < T && cluster[ni][nj] != null) {
                            BP.union(cluster[i][j], cluster[ni][nj]);
                        }
                    }
                }
            }

            // Recompute water clusters and lakes after updates
            updateWaterSurvey(S, T, wdx, wdy, isLake);
            printSurvey(S, T, wdx, wdy, isLake);
        }
    }

    // Creates the initial clusters
    public static void createClusters(int S, int T) {
        BP = new Partition<>();
        WP = new Partition<>();
        cluster = new Node[S][T];
        watercluster = new Node[S][T];

        for (int i = 0; i < S; i++) {
            for (int j = 0; j < T; j++) {
                if (grid[i][j] == 1) cluster[i][j] = BP.makeCluster(new Info(i,j));
                else watercluster[i][j] = WP.makeCluster(new Info(i,j));
            }
        }
    }

    // Union adjacent land and water clusters
    public static void unionClusters(int S, int T, int[] dx, int[] dy, int[] wdx, int[] wdy) {
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < T; j++) {
                if (cluster[i][j] != null) {
                    for (int D = 0; D < 4; D++) {
                        int ni = i + dx[D];
                        int nj = j + dy[D];
                        if (ni >= 0 && ni < S && nj >= 0 && nj < T && cluster[ni][nj] != null) {
                            BP.union(cluster[i][j], cluster[ni][nj]);
                        }
                    }
                }
                if (watercluster[i][j] != null) {
                    for (int D = 0; D < 8; D++) {
                        int ni = i + wdx[D];
                        int nj = j + wdy[D];
                        if (ni >= 0 && ni < S && nj >= 0 && nj < T && watercluster[ni][nj] != null) {
                            WP.union(watercluster[i][j], watercluster[ni][nj]);
                        }
                    }
                }
            }
        }
    }

    // Identify lakes (water clusters not touching edges)
    public static Map<Node<Info>, Boolean> identifyLakes(int S, int T) {
        Map<Node<Info>, Boolean> isLake = new HashMap<>();
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < T; j++) {
                if (watercluster[i][j] != null) {
                    Node<Info> leader = WP.find(watercluster[i][j]);
                    isLake.put(leader, true);
                    if (i == 0 || j == 0 || i == S-1 || j == T-1) {
                        isLake.put(leader, false);
                    }
                }
            }
        }
        return isLake;
    }

    public static void printSurvey(int S, int T, int[] wdx, int[] wdy, Map<Node<Info>, Boolean> isLake) {
        Set<Node<Info>> merged = new HashSet<>();

        for (int i = 0; i < S; i++) {
            for (int j = 0; j < T; j++) {
                if (watercluster[i][j] == null) continue;
                Node<Info> wLead = WP.find(watercluster[i][j]);
                if (!isLake.getOrDefault(wLead,false) || merged.contains(wLead)) continue;

                boolean mergedIntoIsland = false;
                for (int D = 0; D < 8 && !mergedIntoIsland; D++) {
                    int ni = i + wdx[D];
                    int nj = j + wdy[D];
                    if (ni >= 0 && ni < S && nj >= 0 && nj < T && cluster[ni][nj] != null) {
                        BP.find(cluster[ni][nj]).clusterSize += wLead.clusterSize;
                        merged.add(wLead);
                        mergedIntoIsland = true;
                    }
                }
            }
        }

        System.out.println(BP.numberOfClusters());
        List<Integer> islandSizes = BP.clusterSizes();
        System.out.println(islandSizes.isEmpty() ? -1 : islandSizes);
        int total = islandSizes.stream().mapToInt(Integer::intValue).sum();
        System.out.println(total);


        int totalLakes = 0;
        int totalLakeSize = 0;
         Map<Node<Info>, Integer> seen = new HashMap<>();
        for (Node<Info> sq : WP.getAllNodes()) {
             Node<Info> leader = sq.leader;
            if (seen.containsKey(leader)) continue;
            if (isLake.getOrDefault(leader,false)) {
                seen.put(leader, 1);
                totalLakes++;
                totalLakeSize += leader.clusterSize;
            }
        }
        System.out.println(totalLakes);
        System.out.println(totalLakeSize);
    }

    public static void updateWaterSurvey(int S, int T, int[] wdx, int[] wdy, Map<Node<Info>, Boolean> isLake) {
        WP = new Partition<>();
        watercluster = new Node[S][T];

        for (int i = 0; i < S; i++) {
            for (int j = 0; j < T; j++) {
                if (grid[i][j] == 0) {
                    watercluster[i][j] = WP.makeCluster(new Info(i,j));
                }
            }
        }

        int wdxLocal[] = wdx;
        int wdyLocal[] = wdy;

        unionClusters(S,T,new int[]{-1,0,1,0},new int[]{0,-1,0,1}, wdxLocal, wdyLocal);

        isLake.clear();
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < T; j++) {
                if (watercluster[i][j] != null) {
                    Node<Info> leader = WP.find(watercluster[i][j]);
                    isLake.put(leader,true);
                    if (i == 0 || j == 0 || i == S-1 || j == T-1) isLake.put(leader,false);
                }
            }
        }
    }
}
