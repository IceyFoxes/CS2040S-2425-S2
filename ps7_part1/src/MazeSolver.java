import java.util.ArrayDeque;
import java.util.Deque;

public class MazeSolver implements IMazeSolver {
	private static final int NORTH = 0, SOUTH = 1, EAST = 2, WEST = 3;
	private static int[][] DELTAS = new int[][] {
			{ -1, 0 }, // North
			{ 1, 0 }, // South
			{ 0, 1 }, // East
			{ 0, -1 } // West
	};
	private Maze maze;
	private boolean solved = false;
	private boolean[][] visited;
	private int endRow, endCol;
	private Deque<RoomPath> queue;

	public class RoomPath {
		RoomPath parent;
		int row;
		int col;
		int depth = 0;
		static int[] rooms;

		public RoomPath(int row, int col) {
			this.row = row;
			this.col = col;
		}

		public RoomPath(int length) {
			RoomPath.rooms = new int[length];
		}

		public void addParent(RoomPath roompath) {
			this.parent = roompath;
			this.depth = parent.depth + 1;
		}
	}

	public MazeSolver() {
		// TODO: Initialize variables.
		solved = false;
		maze = null;
	}

	@Override
	public void initialize(Maze maze) {
		// TODO: Initialize the solver.
		this.maze = maze;
		queue = new ArrayDeque<>();
		visited = new boolean[maze.getRows()][maze.getColumns()];
		solved = false;
	}

	@Override
	public Integer pathSearch(int startRow, int startCol, int endRow, int endCol) throws Exception {
		// TODO: Find shortest path.
		if (maze == null) {
			throw new Exception("Oh no! You cannot call me without initializing the maze!");
		}
		if (startRow < 0 || startCol < 0 || startRow >= maze.getRows() || startCol >= maze.getColumns() ||
				endRow < 0 || endCol < 0 || endRow >= maze.getRows() || endCol >= maze.getColumns()) {
			throw new IllegalArgumentException("Invalid start/end coordinate");
		}
		// set all visited flag to false
		// before we begin our search
		for (int i = 0; i < maze.getRows(); ++i) {
			for (int j = 0; j < maze.getColumns(); ++j) {
				this.visited[i][j] = false;
				maze.getRoom(i, j).onPath = false;
			}
		}
		this.endRow = endRow;
		this.endCol = endCol;
		RoomPath init = new RoomPath(maze.getRows() * maze.getColumns() + 1);
		solved = false;
		RoomPath roomPath = new RoomPath(startRow, startCol);
		queue.add(roomPath);
		return solve(startRow, startCol);
	}

	private boolean canGo(int row, int col, int dir) {
		// not needed since our maze has a surrounding block of wall
		// but Joe the Average Coder is a defensive coder!
		if (row + DELTAS[dir][0] < 0 || row + DELTAS[dir][0] >= maze.getRows())
			return false;
		if (col + DELTAS[dir][1] < 0 || col + DELTAS[dir][1] >= maze.getColumns())
			return false;
		switch (dir) {
			case NORTH:
				return !maze.getRoom(row, col).hasNorthWall();
			case SOUTH:
				return !maze.getRoom(row, col).hasSouthWall();
			case EAST:
				return !maze.getRoom(row, col).hasEastWall();
			case WEST:
				return !maze.getRoom(row, col).hasWestWall();
		}
		return false;
	}

	// BFS
	private Integer solve(int row, int col) {
		// BFS = queue; DFS = recursive stack;
		int solution = 0;
		while (!queue.isEmpty()) {
			RoomPath currPath = queue.remove();
			row = currPath.row;
			col = currPath.col;
			if (visited[row][col]) {
				// skip
				continue;
			}
			visited[row][col] = true;
			RoomPath.rooms[currPath.depth] += 1;
			for (int direction = 0; direction < 4; ++direction) {
				if (canGo(row, col, direction)) { // can we go in that direction?
					// Add to BFS queue
					int newRow = row + DELTAS[direction][0];
					int newCol = col + DELTAS[direction][1];
					RoomPath newPath = new RoomPath(newRow, newCol);
					newPath.addParent(currPath);
					queue.add(newPath);
				}
			}
			if (row == endRow && col == endCol && !solved) {
				// YES! we found it!
				solved = true;
				solution = currPath.depth;
				maze.getRoom(row, col).onPath = true;
				RoomPath newPath = currPath.parent;
				while (newPath != null) {
					row = newPath.row;
					col = newPath.col;
					maze.getRoom(row, col).onPath = true;
					newPath = newPath.parent;
				}
			}
		}
		if (solved) {
			return solution;
		} else {
			return null;
		}
	}

	@Override
	public Integer numReachable(int k) throws Exception {
		// TODO: Find number of reachable rooms.
		if (RoomPath.rooms == null || k < 0) {
			return null;
		}
		if (k >= RoomPath.rooms.length) {
			return 0;
		}
		if (maze == null) {
			throw new Exception();
		}
		return RoomPath.rooms[k];
	}

	public static void main(String[] args) {
		// Do remember to remove any references to ImprovedMazePrinter before submitting
		// your code!
		try {
			Maze maze = Maze.readMaze("maze-empty.txt");
			IMazeSolver solver = new MazeSolver();
			solver.initialize(maze);
			System.out.println(solver.pathSearch(2, 2, 3, 3));
			ImprovedMazePrinter.printMaze(maze, 2, 2);

			for (int i = 0; i <= 9; ++i) {
				System.out.println("Steps " + i + " Rooms: " + solver.numReachable(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
