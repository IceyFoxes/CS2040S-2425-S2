import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.Function;

public class MazeSolver implements IMazeSolver {
	private static final int TRUE_WALL = Integer.MAX_VALUE;
	private static final int EMPTY_SPACE = 0;
	private static final List<Function<Room, Integer>> WALL_FUNCTIONS = Arrays.asList(
			Room::getNorthWall,
			Room::getEastWall,
			Room::getWestWall,
			Room::getSouthWall);
	private static final int[][] DELTAS = new int[][] {
			{ -1, 0 }, // North
			{ 0, 1 }, // East
			{ 0, -1 }, // West
			{ 1, 0 } // South
	};

	// This is simply a weighted graph problem (find SSSP)
	// Edge between graph if can traverse
	// Weight = fear level
	// Since all fear > 0, we can use Dijkstra's algorithm

	private Maze maze;
	private Graph graph;
	private PriorityQueue<ComparableRoom> estimateHeap;
	private int[][] fearToReach;
	private boolean[][] visited;

	public class ComparableRoom implements Comparable<ComparableRoom> {
		int row, col, fear;

		public ComparableRoom(int row, int col, int fear) {
			this.row = row;
			this.col = col;
			this.fear = fear;
		}

		@Override
		public int compareTo(ComparableRoom c) {
			if (c == null) {
				return -1;
			}
			return Integer.compare(this.fear, c.fear);
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}
			if (o instanceof ComparableRoom) {
				ComparableRoom other = (ComparableRoom) o;
				return this.row == other.row && this.col == other.col;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return Objects.hash(row, col);
		}
	}

	public class Edge {
		ComparableRoom to;
		int fear;

		Edge(ComparableRoom to, int fear) {
			this.to = to;
			this.fear = fear;
		}
	}

	public class Graph {
		private Map<ComparableRoom, List<Edge>> adjList = new HashMap<>();

		public void addEdge(ComparableRoom u, ComparableRoom v, int weight) {
			adjList.putIfAbsent(u, new ArrayList<>());
			adjList.putIfAbsent(v, new ArrayList<>());
			adjList.get(u).add(new Edge(v, weight));
		}

		public List<Edge> getNeighbors(ComparableRoom node) {
			return adjList.getOrDefault(node, new ArrayList<>());
		}
	}

	public MazeSolver() {
		maze = null;
	}

	@Override
	public void initialize(Maze maze) {
		this.maze = maze;
		int rows = maze.getRows();
		int cols = maze.getColumns();
		this.estimateHeap = new PriorityQueue<>(rows * cols);
		this.fearToReach = new int[rows][cols];
		this.visited = new boolean[rows][cols];
		createGraph(maze);
	}

	// Create the graph to run Djiksstra's algorithm (AdjacencyList)
	private void createGraph(Maze maze) {
		int rows = maze.getRows();
		int cols = maze.getColumns();
		this.graph = new Graph();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				Room room = maze.getRoom(i, j);
				for (int direction = 0; direction < 4; ++direction) {
					int fear = WALL_FUNCTIONS.get(direction).apply(room);
					if (fear != TRUE_WALL) {
						int newRow = i + DELTAS[direction][0];
						int newCol = j + DELTAS[direction][1];
						if (fear == EMPTY_SPACE) {
							fear = 1;
						}
						this.graph.addEdge(new ComparableRoom(i, j, TRUE_WALL),
								new ComparableRoom(newRow, newCol, TRUE_WALL), fear);
					}
				}
			}
		}
	}

	@Override
	public Integer pathSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		if (maze == null) {
			throw new Exception("Oh no! You cannot call me without initializing the maze!");
		}
		if (startRow < 0 || startCol < 0 || startRow >= maze.getRows() || startCol >= maze.getColumns() ||
				endRow < 0 || endCol < 0 || endRow >= maze.getRows() || endCol >= maze.getColumns()) {
			throw new IllegalArgumentException("Invalid start/end coordinate");
		}

		if (startRow == endRow && startCol == endCol) {
			return 0;
		}

		// Clear previous search
		for (int i = 0; i < maze.getRows(); i++) {
			for (int j = 0; j < maze.getColumns(); j++) {
				fearToReach[i][j] = TRUE_WALL;
				visited[i][j] = false;
			}
		}
		estimateHeap.clear();

		ComparableRoom start = new ComparableRoom(startRow, startCol, 0);
		fearToReach[startRow][startCol] = 0;
		estimateHeap.add(start);

		return solve(endRow, endCol);
	}

	private Integer solve(int endRow, int endCol) {
		// Dijkstra's algorithm
		while (!estimateHeap.isEmpty()) {
			ComparableRoom curr = estimateHeap.poll();
			int row = curr.row;
			int col = curr.col;
			if (visited[row][col]) {
				continue;
			}
			visited[row][col] = true;

			if (row == endRow && col == endCol) {
				return fearToReach[row][col];
			}

			for (Edge e : graph.getNeighbors(curr)) {
				ComparableRoom neighbour = e.to;
				int newRow = neighbour.row;
				int newCol = neighbour.col;
				if (visited[newRow][newCol]) {
					continue;
				}

				// Relax estimates
				int newFear = fearToReach[row][col] + e.fear;
				if (newFear < fearToReach[newRow][newCol]) {
					ComparableRoom newNode = new ComparableRoom(newRow, newCol, newFear);
					fearToReach[newRow][newCol] = newFear;
					this.estimateHeap.add(newNode);
				}
			}
		}
		return null;
	}

	@Override
	public Integer bonusSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		if (maze == null) {
			throw new Exception("Oh no! You cannot call me without initializing the maze!");
		}
		if (startRow < 0 || startCol < 0 || startRow >= maze.getRows() || startCol >= maze.getColumns() ||
				endRow < 0 || endCol < 0 || endRow >= maze.getRows() || endCol >= maze.getColumns()) {
			throw new IllegalArgumentException("Invalid start/end coordinate");
		}

		if (startRow == endRow && startCol == endCol) {
			return 0;
		}

		// Clear previous search
		for (int i = 0; i < maze.getRows(); i++) {
			for (int j = 0; j < maze.getColumns(); j++) {
				fearToReach[i][j] = TRUE_WALL;
				visited[i][j] = false;
			}
		}
		estimateHeap.clear();

		ComparableRoom start = new ComparableRoom(startRow, startCol, 0);
		fearToReach[startRow][startCol] = 0;
		estimateHeap.add(start);

		return bonusSolve(endRow, endCol);
	}

	private Integer bonusSolve(int endRow, int endCol) {
		// Dijkstra's algorithm
		while (!estimateHeap.isEmpty()) {
			ComparableRoom curr = estimateHeap.poll();
			int row = curr.row;
			int col = curr.col;
			if (visited[row][col]) {
				continue;
			}
			visited[row][col] = true;

			if (row == endRow && col == endCol) {
				return fearToReach[row][col];
			}

			for (Edge e : graph.getNeighbors(curr)) {
				ComparableRoom neighbour = e.to;
				int newRow = neighbour.row;
				int newCol = neighbour.col;
				if (visited[newRow][newCol]) {
					continue;
				}

				// Change of relaxation here
				int newFear = fearToReach[row][col];
				if (e.fear == 1) {
					newFear += 1;
				} else if (e.fear > fearToReach[row][col]) {
					newFear = e.fear;
				}

				if (newFear < fearToReach[newRow][newCol]) {
					ComparableRoom newNode = new ComparableRoom(newRow, newCol, newFear);
					fearToReach[newRow][newCol] = newFear;
					this.estimateHeap.add(newNode);
				}
			}
		}
		return null;
	}

	@Override
	public Integer bonusSearch(int startRow, int startCol, int endRow, int endCol, int sRow, int sCol)
			throws Exception {
		if (maze == null) {
			throw new Exception("Oh no! You cannot call me without initializing the maze!");
		}
		if (startRow < 0 || startCol < 0 || startRow >= maze.getRows() || startCol >= maze.getColumns() ||
				endRow < 0 || endCol < 0 || endRow >= maze.getRows() || endCol >= maze.getColumns()) {
			throw new IllegalArgumentException("Invalid start/end coordinate");
		}

		if (startRow == endRow && startCol == endCol) {
			return 0;
		}

		// Clear previous search
		for (int i = 0; i < maze.getRows(); i++) {
			for (int j = 0; j < maze.getColumns(); j++) {
				fearToReach[i][j] = TRUE_WALL;
				visited[i][j] = false;
			}
		}
		estimateHeap.clear();

		ComparableRoom start = new ComparableRoom(startRow, startCol, 0);
		fearToReach[startRow][startCol] = 0;
		estimateHeap.add(start);

		return alternative(startRow, startCol, endRow, endCol, sRow, sCol);
		// return bellmanFord(endRow, endCol, sRow, sCol);
	}

	private Integer alternative(int startRow, int startCol, int endRow, int endCol, int sRow, int sCol) {
		/*
		 * Case 1 can reach special and end:
		 * start -> (end) -> special -> end
		 * start -> end (shorter)
		 * Case 2 cannot reach special
		 * start -> end
		 * Case 3 cannot reach end
		 */

		try {
			Integer startToSpecial = bonusSearch(startRow, startCol, sRow, sCol);
			Integer startToEnd = bonusSearch(startRow, startCol, endRow, endCol);
			// Case 1:
			if (startToSpecial != null && startToEnd != null) {
				Integer path1 = startToSpecial + bonusSearch(sRow, sCol, endRow, endCol);
				Integer path2 = startToEnd;
				return Math.min(path1, path2);
			}
			// Case 2:
			if (startToSpecial == null) {
				return startToEnd;
			}
			// Case 3:
			return startToEnd;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return -1; // Shouldn't reach here
	}

	private Integer bellmanFord(int endRow, int endCol, int sRow, int sCol) {
		// We can do Bellman Ford here since the special room is similar to a negative
		// edge that resets our fear (always positive) to -1
		int vertices = maze.getRows() * maze.getColumns();
		boolean terminate = false;
		// Traverse all edges (V - 1) times

		for (int v = 1; v < vertices; v++) {
			if (terminate) {
				break;
			}
			terminate = true;
			for (int row = 0; row < maze.getRows(); row++) {
				for (int col = 0; col < maze.getColumns(); col++) {

					// Not reachable yet
					if (fearToReach[row][col] == Integer.MAX_VALUE) {
						continue;
					}

					for (Edge e : graph.getNeighbors(new ComparableRoom(row, col, 0))) {
						ComparableRoom neighbour = e.to;
						int newRow = neighbour.row;
						int newCol = neighbour.col;

						// Change of relaxation here
						int newFear = fearToReach[row][col];
						if (newRow == sRow && newCol == sCol) {
							newFear = -1;
						} else if (e.fear == 1) {
							newFear += 1;
						} else if (e.fear > fearToReach[row][col]) {
							newFear = newFear + e.fear;
						}

						if (newFear < fearToReach[newRow][newCol]) {
							fearToReach[newRow][newCol] = newFear;
							terminate = false;
						}
						System.out.println(
								"row: " + newRow + " " + "col: " + newCol + "\n" + fearToReach[newRow][newCol]);
					}
				}
			}
		}
		if (fearToReach[endRow][endCol] == TRUE_WALL) {
			return null;
		}
		return fearToReach[endRow][endCol];
	}

	public static void main(String[] args) {
		try {
			Maze maze = Maze.readMaze("maze-empty.txt");
			Maze maze2 = Maze.readMaze("haunted-maze-sample.txt");
			IMazeSolver solver = new MazeSolver();
			solver.initialize(maze2);

			System.out.println(solver.pathSearch(0, 0, 0, 3));
			System.out.println(solver.bonusSearch(0, 0, 0, 3, 0, 2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
