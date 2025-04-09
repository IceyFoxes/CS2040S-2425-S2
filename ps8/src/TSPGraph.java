import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.ArrayDeque;

public class TSPGraph implements IApproximateTSP {
    ArrayList<Integer>[] adj;

    public void mstAlternative(TSPMap map) {
        // O (n^2 log n) for complete graphs
        int n = map.getCount();
        boolean[] visited = new boolean[n];
        double[] dist = new double[n];
        int[] parent = new int[n];

        // For TSP question
        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] adjacent = (ArrayList<Integer>[]) new ArrayList<?>[n];
        this.adj = adjacent;
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }

        // Distance and node (represented by int)
        TreeMapPriorityQueue<Double, Integer> minHeap = new TreeMapPriorityQueue<>();

        // Prim's algorithm
        for (int i = 0; i < n; i++) {
            dist[i] = Double.MAX_VALUE;
            parent[i] = -1;
        }

        // Start
        dist[0] = 0.0;
        for (int i = 0; i < n; i++) {
            minHeap.add(i, dist[i]);
        }

        while (!minHeap.isEmpty()) {
            int curr = minHeap.extractMin();
            visited[curr] = true;

            // Join the new node
            if (parent[curr] != -1) {
                map.setLink(curr, parent[curr], false);
                // For TSP question
                this.adj[curr].add(parent[curr]);
                this.adj[parent[curr]].add(curr);
            }

            // Update neighbors of u
            for (int v = 0; v < n; v++) {
                if (!visited[v]) {
                    double d = map.pointDistance(curr, v);
                    if (d < dist[v]) {
                        dist[v] = d;
                        parent[v] = curr;
                        minHeap.decreasePriority(v, d);
                    }
                }
            }
        }

        map.redraw();
    }

    @Override
    public void MST(TSPMap map) {
        // O (n^2)
        int n = map.getCount();
        boolean[] visited = new boolean[n];
        visited[0] = true;

        // For TSP question
        @SuppressWarnings("unchecked")
        ArrayList<Integer>[] adjacent = (ArrayList<Integer>[]) new ArrayList<?>[n];
        this.adj = adjacent;
        for (int i = 0; i < n; i++) {
            adj[i] = new ArrayList<>();
        }

        // N - 1 edges
        for (int e = 0; e < n - 1; e++) {
            double minDist = Double.MAX_VALUE;
            int from = -1; // Init
            int to = -1; // Init

            // Loop over all visited nodes
            for (int i = 0; i < n; i++) {
                if (!visited[i]) {
                    continue;
                }

                for (int j = 0; j < n; j++) {
                    if (visited[j])
                        continue;

                    double dist = map.pointDistance(i, j);
                    if (dist < minDist) {
                        minDist = dist;
                        from = j;
                        to = i;
                    }
                }
            }

            // Set the link from the new node to the MST
            if (from != -1 && to != -1) {
                map.setLink(from, to, false);
                visited[from] = true;
                // For TSP question
                this.adj[from].add(to);
                this.adj[to].add(from);
            }
        }

        map.redraw();
        // mstAlternative(map);
    }

    @Override
    public void TSP(TSPMap map) {
        MST(map);
        int n = map.getCount();
        boolean[] visited = new boolean[n];
        ArrayList<Integer> tour = new ArrayList<Integer>(2 * n);

        dfs(map, 0, visited, tour);

        // Skip cities already visited
        tour = tour.stream()
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

        // Clear map
        for (int i = 0; i < n; i++) {
            map.eraseLink(i, false);
        }

        // Construct map from tour
        for (int i = 0; i < tour.size() - 1; i++) {
            int curr = tour.get(i);
            int next = tour.get(i + 1);
            map.setLink(curr, next, false);
        }

        // End to Start (Complete cycle)
        int last = tour.get(tour.size() - 1);
        int start = tour.get(0);
        map.setLink(last, start, false);

        // Redraw the completed tour
        map.redraw();
    }

    private void dfs(TSPMap map, Integer curr, boolean[] visited, ArrayList<Integer> tour) {
        if (visited[curr]) {
            return;
        }

        tour.add(curr);
        visited[curr] = true;

        for (int neighbour : adj[curr]) {
            if (!visited[neighbour]) {
                dfs(map, neighbour, visited, tour);
                tour.add(curr);
            }
        }
    }

    @Override
    public boolean isValidTour(TSPMap map) {
        // Note: this function should work with *any* map, and not just results from
        // TSP().
        // A valid tour is one that visits every city only once (other than the start),
        // so we are checking for cycles == 1
        int n = map.getCount();
        boolean[] visited = new boolean[n];
        ArrayDeque<Integer> queue = new ArrayDeque<Integer>();
        queue.push(0);
        int cycle = 0;
        int uniqueNodes = 0;

        // BFS
        while (!queue.isEmpty()) {
            Integer curr = queue.poll();
            if (visited[curr]) {
                if (cycle == 0 && curr == 0) {
                    cycle += 1;
                    continue;
                } else {
                    return false;
                }
            }
            visited[curr] = true;
            uniqueNodes += 1;

            Integer next = map.getLink(curr);
            if (next < 0 || next >= map.getCount()) {
                return false; // Invalid tour: Tour -> Cycle -> All nodes have a valid next
            }
            queue.push(next);
        }

        return cycle == 1 && uniqueNodes == n;
    }

    @Override
    public double tourDistance(TSPMap map) {
        // Note: this function should work with *any* map, and not just results from
        // TSP().
        if (isValidTour(map)) {
            int curr = 0;
            int next = map.getLink(curr);
            double sum = map.pointDistance(curr, next);
            curr = next;

            while (curr != 0) {
                next = map.getLink(curr);
                sum += map.pointDistance(curr, next);
                curr = next;
            }
            return sum;
        }
        return -1;
    }

    public static void main(String[] args) {
        TSPMap map = new TSPMap(args.length > 0 ? args[0] : "fiftypoints.txt");
        TSPGraph graph = new TSPGraph();

        graph.MST(map);
        graph.TSP(map);
        System.out.println(graph.isValidTour(map));
        System.out.println(graph.tourDistance(map));
    }
}
