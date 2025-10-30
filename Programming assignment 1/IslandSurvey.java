import java.util.*;

class Info {
    int row, col;
    public Info(int r, int c) {
        row = r;
        col = c;
    }
}

public class IslandSurvey {

    static Partition<Info> BP;
    static Node<Info>[][] cluster;
    static int[][] grid;

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        //Reads the given grid
        Scanner sc = new Scanner(System.in);
        int S = sc.nextInt();
        int T = sc.nextInt();
        sc.nextLine();
        grid = new int[S][T];
        cluster = new Node[S][T];
        BP = new Partition<>();
        for (int i=0;i< S; i++){
            String line = sc.nextLine().trim();
            for (int j=0; j<T; j++){
                grid[i][j] = line.charAt(j) - '0';
            }
        }

        //Create clusters for each black square
        for (int i=0;i< S; i++){
            for (int j=0; j<T; j++){
               if (grid[i][j] == 1){
                    cluster[i][j] = BP.makeCluster(new Info(i,j));
               }
            }
        }

        // Union all adjacent clusters
        int dx[] = {-1,0,1,0};
        int dy[] = {0,-1,0,1};
         for (int i=0;i< S; i++){
            for (int j=0; j<T; j++){
                if (cluster[i][j]!=null)
                {
                    for (int D=0; D<4; D++){
                       int ni = i +dx[D];
                       int nj = j +dy[D];
                       if (ni>=0 && ni<S && nj>=0 && nj<T && cluster[ni][nj] != null){
                            BP.union(cluster[ni][nj], cluster[i][j]);
                       }
                    }
                }
                
            }
        }
        
        //Print initial survey
        printSurvey();

        //Handle new phases of land appearing and update survey

        int numberOfPhases = sc.nextInt();
        for (int phase = 0; phase<numberOfPhases; phase++){
            int numberofChanges = sc.nextInt();
            for (int changes=0; changes<numberofChanges; changes++){
                int i = sc.nextInt();
                int j = sc.nextInt();

                if (grid[i][j] == 0){ //Checks square is not already land
                    grid[i][j] = 1;
                    cluster[i][j] = BP.makeCluster(new Info(i,j));
                    for (int D=0; D<4; D++){
                        int ni = i +dx[D];
                        int nj = j +dy[D];
                        if (ni>=0 && ni<S && nj>=0 && nj<T && cluster[ni][nj] != null){
                         BP.union(cluster[ni][nj], cluster[i][j]);
                        }
                    }   
                }
            }
            printSurvey();
            sc.close();
        }
       


    }
    public static void printSurvey(){
        System.out.println(BP.numberOfClusters());
        List<Integer> islandSizes = BP.clusterSizes();
        if (islandSizes.isEmpty()){
            System.out.println(-1);
        }
        else {
            System.out.println(islandSizes);
        }
        int total = 0;
        for (int i : islandSizes){
            total += i;
        }
        System.out.println(total);
    }
}
