import java.util.*;

class Room {
    int number;
    int x;
    int y;
    Room parent;

    public Room(int number, int x, int y) {
        this.number = number;
        this.x = x;
        this.y = y;
    }
}

class AStarAlgorithm {
    private int[][] grid;
    private int[][] gScores;
    private int[][] fScores;
    private boolean[][] visited;
    private int size;

    public AStarAlgorithm(int size) {
        this.size = size;
        this.grid = new int[size][size];
        this.gScores = new int[size][size];
        this.fScores = new int[size][size];
        this.visited = new boolean[size][size];
    }

    public void addRooms(List<Room> rooms) {
        for (Room room : rooms) {
            grid[room.x][room.y] = room.number;
        }
    }

    public List<Room> findPath(Room start, Room goal) {
        PriorityQueue<Room> openSet = new PriorityQueue<>(Comparator.comparingInt(this::getFScore));
        openSet.add(start);

        gScores[start.x][start.y] = 0;
        fScores[start.x][start.y] = heuristic(start, goal);

        while (!openSet.isEmpty()) {
            Room current = openSet.poll();

            if (current.x == goal.x && current.y == goal.y) {
                return reconstructPath(current);
            }

            visited[current.x][current.y] = true;

            List<Room> neighbors = getNeighbors(current);
            for (Room neighbor : neighbors) {
                int tentativeGScore = gScores[current.x][current.y] + 1;

                if (visited[neighbor.x][neighbor.y] && tentativeGScore >= gScores[neighbor.x][neighbor.y]) {
                    continue;
                }

                if (!openSet.contains(neighbor) || tentativeGScore < gScores[neighbor.x][neighbor.y]) {
                    gScores[neighbor.x][neighbor.y] = tentativeGScore;
                    fScores[neighbor.x][neighbor.y] = tentativeGScore + heuristic(neighbor, goal);
                    neighbor.parent = current;

                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    private int getFScore(Room room) {
        return fScores[room.x][room.y];
    }

    private int heuristic(Room start, Room goal) {
        return Math.abs(start.x - goal.x) + Math.abs(start.y - goal.y);
    }

    private List<Room> getNeighbors(Room room) {
        List<Room> neighbors = new ArrayList<>();

        int x = room.x;
        int y = room.y;

        if (x > 0) {
            neighbors.add(new Room(grid[x - 1][y], x - 1, y));
        }

        if (x < size - 1) {
            neighbors.add(new Room(grid[x + 1][y], x + 1, y));
        }

        if (y > 0) {
            neighbors.add(new Room(grid[x][y - 1], x, y - 1));
        }

        if (y < size - 1) {
            neighbors.add(new Room(grid[x][y + 1], x, y + 1));
        }

        return neighbors;
    }

    private List<Room> reconstructPath(Room current) {
        List<Room> path = new ArrayList<>();

        while (current != null) {
            path.add(current);
            current = current.parent;
        }

        Collections.reverse(path);
        return path;
    }
}

public class Main {
    public static void main(String[] args) {
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room(1, 1, 2));
        rooms.add(new Room(2, 3, 1));
        rooms.add(new Room(3, 2, 3));

        int gridSize = 5;
        AStarAlgorithm algorithm = new AStarAlgorithm(gridSize);
        algorithm.addRooms(rooms);

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the start room number: ");
        int startRoomNumber = scanner.nextInt();
        Room start = rooms.stream()
                .filter(room -> room.number == startRoomNumber)
                .findFirst()
                .orElse(null);

        System.out.print("Enter the destination room number: ");
        int goalRoomNumber = scanner.nextInt();
        Room goal = rooms.stream()
                .filter(room -> room.number == goalRoomNumber)
                .findFirst()
                .orElse(null);

        if (start == null || goal == null) {
            System.out.println("Invalid room number.");
            return;
        }

        List<Room> path = algorithm.findPath(start, goal);

        if (path.isEmpty()) {
            System.out.println("No path found.");
        } else {
            System.out.println("Path found:");
            for (Room room : path) {
                System.out.println("Room Number: " + room.number);
            }
        }
    }
}