import java.io.*;
import java.util.*;

public class Main {
    public static int K, M;
    public static int wallIndex;
    public static int[] wallPieces;

    public static int[] dx = { -1, 1, 0, 0 };
    public static int[] dy = { 0, 0, -1, 1 };
    public static int[][] map = new int[5][5];

    private static int[][] deepCopy(int[][] src) {
        int[][] dst = new int[5][5];
        for (int i = 0; i < 5; i++)
            dst[i] = Arrays.copyOf(src[i], src[i].length);
        return dst;
    }

    private static void rotate(int sx, int sy, int angle, int[][] map) {
        int[][] temp = new int[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                temp[i][j] = map[sx + i][sy + j];

        for (int r = 0; r < angle / 90; r++) {
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    map[sx + i][sy + j] = temp[2 - j][i];

            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    temp[i][j] = map[sx + i][sy + j];
        }
    }

    private static int simulateValue(int[][] map) {
        int value = 0;
        boolean[][] visited = new boolean[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (visited[i][j]) continue;
                value += countFloodFill(i, j, map, visited);
            }
        }
        return value;
    }

    private static int countFloodFill(int sx, int sy, int[][] map, boolean[][] visited) {
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[]{ sx, sy });
        visited[sx][sy] = true;

        int count = 1;
        int num = map[sx][sy];
        while (!queue.isEmpty()) {
            int x = queue.peek()[0];
            int y = queue.poll()[1];

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];

                if (0 <= nx && nx < 5 && 0 <= ny && ny < 5) {
                    if (!visited[nx][ny] && map[nx][ny] == num) {
                        queue.offer(new int[]{ nx, ny });
                        visited[nx][ny] = true;
                        count++;
                    }
                }
            }
        }
        return count >= 3 ? count : 0;
    }

    public static class BestRotationInfo implements Comparable<BestRotationInfo> {
        public int x, y, angle, value;

        public BestRotationInfo(int x, int y, int angle, int value) {
            this.x = x;
            this.y = y;
            this.angle = angle;
            this.value = value;
        }

        @Override
        public int compareTo(BestRotationInfo o) {
            if (this.value == o.value) {
                if (this.angle == o.angle) {
                    if (this.x == o.x)
                        return this.y - o.y;
                    return this.x - o.x;
                }
                return this.angle - o.angle;
            }
            return o.value - this.value;
        }
    }

    public static void exploration() {
        Queue<BestRotationInfo> pq = new PriorityQueue<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int angle : new int[]{ 90, 180, 270 }) {
                    int[][] temp = deepCopy(map);
                    rotate(i, j, angle, temp);

                    int value = simulateValue(temp);
                    if (value > 0) pq.offer(new BestRotationInfo(i, j, angle, value));
                }
            }
        }

        if (!pq.isEmpty()) {
            BestRotationInfo rotate = pq.poll();
            rotate(rotate.x, rotate.y, rotate.angle, map);
        }
    }

    public static void acquisition() {
        int acquiredValue = 0;
        while (true) {
            int value = acquireValue();
            if (value == 0) break;

            acquiredValue += value;
            fill();
        }

        if (acquiredValue == 0) return;
        System.out.print(acquiredValue + " ");
    }

    private static int acquireValue() {
        int value = 0;
        boolean[][] visited = new boolean[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (visited[i][j]) continue;
                value += eraseFloodFill(i, j, map, visited);
            }
        }
        return value;
    }

    private static int eraseFloodFill(int sx, int sy, int[][] map, boolean[][] visited) {
        Queue<int[]> erase = new ArrayDeque<>();
        Queue<int[]> queue = new ArrayDeque<>();
        erase.offer(new int[]{ sx, sy });
        queue.offer(new int[]{ sx, sy });
        visited[sx][sy] = true;

        int count = 1;
        int num = map[sx][sy];
        while (!queue.isEmpty()) {
            int x = queue.peek()[0];
            int y = queue.poll()[1];

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];

                if (0 <= nx && nx < 5 && 0 <= ny && ny < 5) {
                    if (!visited[nx][ny] && map[nx][ny] == num) {
                        erase.offer(new int[]{ nx, ny });
                        queue.offer(new int[]{ nx, ny });
                        visited[nx][ny] = true;
                        count++;
                    }
                }
            }
        }

        if (erase.size() >= 3) {
            while (!erase.isEmpty()) {
                int x = erase.peek()[0];
                int y = erase.poll()[1];
                map[x][y] = 0;
            }
        }
        return count >= 3 ? count : 0;
    }

    private static void fill() {
        for (int y = 0; y < 5; y++) {
            for (int x = 4; x >= 0; x--) {
                if (map[x][y] != 0) continue;
                map[x][y] = wallPieces[wallIndex++];
            }
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());

        for (int i = 0; i < 5; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < 5; j++)
                map[i][j] = Integer.parseInt(st.nextToken());
        }

        st = new StringTokenizer(br.readLine());
        wallPieces = new int[M];
        for (int i = 0; i < M; i++)
            wallPieces[i] = Integer.parseInt(st.nextToken());

        while (K-- > 0) {
            exploration();
            acquisition();
        }
    }
}