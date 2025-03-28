import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private ComparableRoom[][] roomGrid;

	public class ComparableRoom implements Comparable<ComparableRoom> {
		Integer fearToReach;
		Boolean visited = false;

		public ComparableRoom(Integer fearToReach) {
			this.fearToReach = fearToReach;
		}

		public void visit() {
			this.visited = true;
		}

		public boolean isVisited() {
			return this.visited;
		}

		@Override
		public int compareTo(ComparableRoom c) {
			if (c == null) {
				return -1;
			}
			if (c.fearToReach == TRUE_WALL) {
				return -1;
			} else if (this.fearToReach == TRUE_WALL) {
				return 1;
			}
			return Integer.compare(this.fearToReach, c.fearToReach);
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
		this.roomGrid = new ComparableRoom[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				roomGrid[i][j] = new ComparableRoom(TRUE_WALL);
			}
		}
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
						this.graph.addEdge(roomGrid[i][j], roomGrid[newRow][newCol], fear);
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
				roomGrid[i][j].fearToReach = TRUE_WALL;
				roomGrid[i][j].visited = false;
			}
		}
		estimateHeap.clear();

		ComparableRoom start = roomGrid[startRow][startCol];
		start.fearToReach = 0;
		estimateHeap.add(start);

		return solve(endRow, endCol);
	}

	private Integer solve(int endRow, int endCol) {
		// Dijkstra's algorithm
		while (!estimateHeap.isEmpty()) {
			ComparableRoom curr = estimateHeap.poll();
			if (curr.isVisited()) {
				continue;
			}
			curr.visit();

			if (curr == roomGrid[endRow][endCol]) {
				return curr.fearToReach;
			}

			for (Edge e : graph.getNeighbors(curr)) {
				ComparableRoom neighbour = e.to;
				if (neighbour.isVisited()) {
					continue;
				}

				int newFear = curr.fearToReach + e.fear;
				if (newFear < neighbour.fearToReach) {
					neighbour.fearToReach = newFear;
					this.estimateHeap.add(neighbour);
				}
			}
		}
		return null;
	}

	@Override
	public Integer bonusSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		// TODO: Find minimum fear level given new rules.
		return null;
	}

	@Override
	public Integer bonusSearch(int startRow, int startCol, int endRow, int endCol, int sRow, int sCol)
			throws Exception {
		// TODO: Find minimum fear level given new rules and special room.
		return null;
	}

	public static void main(String[] args) {
		try {
			Maze maze = Maze.readMaze("maze-empty.txt");
			Maze maze2 = Maze.readMaze("haunted-maze-sample.txt");
			IMazeSolver solver = new MazeSolver();
			solver.initialize(maze2);
			solver.initialize(maze);

			System.out.println(solver.pathSearch(0, 0, 0, 3));
			System.out.println(solver.pathSearch(1, 1, 1, 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
