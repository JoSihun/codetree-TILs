import java.util.*;

public class Main {
    static int K, M;
    static int[][] grid = new int[5][5];
    static int[] wallPieces;
    static int wallIndex = 0;

    // 방향 벡터 (상, 하, 좌, 우)
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        K = sc.nextInt();
        M = sc.nextInt();
        sc.nextLine();

        // 초기 격자 입력
        for(int i=0; i<5; i++) {
            String[] line = sc.nextLine().split(" ");
            for(int j=0; j<5; j++) {
                grid[i][j] = Integer.parseInt(line[j]);
            }
        }

        // 벽면에 적힌 유물 조각 번호 입력
        wallPieces = new int[M];
        for(int i=0; i<M; i++) {
            wallPieces[i] = sc.nextInt();
        }

        List<Integer> result = new ArrayList<>();

        for(int turn=0; turn<K; turn++) {
            // 최적의 회전 방법을 찾기 위해 모든 가능성 시도
            Rotation bestRotation = null;
            int maxValue = -1;

            // 모든 3x3 격자 시작점 (0~2 행, 0~2 열)
            for(int startRow=0; startRow<=2; startRow++) {
                for(int startCol=0; startCol<=2; startCol++) {
                    // 각 회전 각도 시도
                    for(int angle : new int[]{90, 180, 270}) {
                        // 현재 격자 복사
                        int[][] tempGrid = copyGrid(grid);

                        // 3x3 격자 회전
                        rotateSubGrid(tempGrid, startRow, startCol, angle);

                        // 유물 획득 시뮬레이션
                        int value = simulateArtifactCollection(tempGrid);

                        // 최적의 회전 방법 업데이트
                        if(value > maxValue) {
                            maxValue = value;
                            bestRotation = new Rotation(startRow, startCol, angle);
                        }
                        // 가치가 같을 경우, 회전 각도가 작은 것을 우선
                        else if(value == maxValue && bestRotation != null) {
                            if(angle < bestRotation.angle) {
                                bestRotation = new Rotation(startRow, startCol, angle);
                            }
                            // 각도도 같을 경우, 열 번호가 작은 것을 우선
                            else if(angle == bestRotation.angle) {
                                if(startCol < bestRotation.startCol) {
                                    bestRotation = new Rotation(startRow, startCol, angle);
                                }
                                // 열도 같을 경우, 행 번호가 작은 것을 우선
                                else if(startCol == bestRotation.startCol && startRow < bestRotation.startRow) {
                                    bestRotation = new Rotation(startRow, startCol, angle);
                                }
                            }
                        }
                    }
                }
            }

            // 만약 어떤 회전 방법으로도 유물을 획득할 수 없으면 탐사 종료
            if(bestRotation == null || maxValue == 0) {
                break;
            }

            // 최적의 회전 방법 적용
            rotateSubGrid(grid, bestRotation.startRow, bestRotation.startCol, bestRotation.angle);

            // 유물 획득 및 제거, 새로운 조각 삽입
            int totalValue = performArtifactCollectionAndInsertion();

            // 획득한 가치 추가
            result.add(totalValue);
        }

        // 결과 출력
        for(int i=0; i<result.size(); i++) {
            if(i > 0) System.out.print(" ");
            System.out.print(result.get(i));
        }
    }

    // 회전 방법을 나타내는 클래스
    static class Rotation {
        int startRow, startCol, angle;

        Rotation(int startRow, int startCol, int angle) {
            this.startRow = startRow;
            this.startCol = startCol;
            this.angle = angle;
        }
    }

    // 격자 복사
    static int[][] copyGrid(int[][] original) {
        int[][] newGrid = new int[5][5];
        for(int i=0; i<5; i++) {
            System.arraycopy(original[i], 0, newGrid[i], 0, 5);
        }
        return newGrid;
    }

    // 3x3 격자 회전
    static void rotateSubGrid(int[][] g, int startRow, int startCol, int angle) {
        int[][] temp = new int[3][3];
        // 회전 전 상태 저장
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                temp[i][j] = g[startRow + i][startCol + j];
            }
        }

        int rotations = angle / 90;
        for(int r=0; r<rotations; r++) {
            // 90도 회전
            for(int i=0; i<3; i++) {
                for(int j=0; j<3; j++) {
                    g[startRow + j][startCol + 2 - i] = temp[i][j];
                }
            }
            // 업데이트 temp for next rotation if needed
            for(int i=0; i<3; i++) {
                for(int j=0; j<3; j++) {
                    temp[i][j] = g[startRow + i][startCol + j];
                }
            }
        }
    }

    // 유물 획득 시뮬레이션 (회전 후의 그리드에서 유물을 찾아 제거하고 그 가치를 반환)
    static int simulateArtifactCollection(int[][] g) {
        boolean[][] visited = new boolean[5][5];
        int totalValue = 0;
        List<List<int[]>> artifacts = new ArrayList<>();

        for(int i=0; i<5; i++) {
            for(int j=0; j<5; j++) {
                if(!visited[i][j]) {
                    List<int[]> group = new ArrayList<>();
                    Queue<int[]> queue = new LinkedList<>();
                    queue.offer(new int[]{i, j});
                    visited[i][j] = true;
                    int num = g[i][j];

                    while(!queue.isEmpty()) {
                        int[] current = queue.poll();
                        group.add(current);
                        for(int d=0; d<4; d++) {
                            int ni = current[0] + dx[d];
                            int nj = current[1] + dy[d];
                            if(ni >=0 && ni <5 && nj >=0 && nj <5 && !visited[ni][nj] && g[ni][nj] == num) {
                                visited[ni][nj] = true;
                                queue.offer(new int[]{ni, nj});
                            }
                        }
                    }

                    if(group.size() >=3) {
                        artifacts.add(group);
                        totalValue += group.size();
                    }
                }
            }
        }

        return totalValue;
    }

    // 유물 획득 및 제거, 새로운 조각 삽입을 수행하고 획득한 총 가치를 반환
    static int performArtifactCollectionAndInsertion() {
        int totalValue = 0;

        while(true) {
            // 유물 탐색
            boolean[][] visited = new boolean[5][5];
            List<List<int[]>> artifacts = new ArrayList<>();
            int currentValue = 0;

            for(int i=0; i<5; i++) {
                for(int j=0; j<5; j++) {
                    if(!visited[i][j]) {
                        List<int[]> group = new ArrayList<>();
                        Queue<int[]> queue = new LinkedList<>();
                        queue.offer(new int[]{i, j});
                        visited[i][j] = true;
                        int num = grid[i][j];

                        while(!queue.isEmpty()) {
                            int[] current = queue.poll();
                            group.add(current);
                            for(int d=0; d<4; d++) {
                                int ni = current[0] + dx[d];
                                int nj = current[1] + dy[d];
                                if(ni >=0 && ni <5 && nj >=0 && nj <5 && !visited[ni][nj] && grid[ni][nj] == num) {
                                    visited[ni][nj] = true;
                                    queue.offer(new int[]{ni, nj});
                                }
                            }
                        }

                        if(group.size() >=3) {
                            artifacts.add(group);
                            currentValue += group.size();
                        }
                    }
                }
            }

            // 유물이 없으면 종료
            if(artifacts.isEmpty()) {
                break;
            }

            // 유물 제거 및 가치 합산
            totalValue += currentValue;
            List<int[]> removedPositions = new ArrayList<>();
            for(List<int[]> group : artifacts) {
                for(int[] pos : group) {
                    grid[pos[0]][pos[1]] = 0; // 제거된 위치는 0으로 표시
                    removedPositions.add(pos);
                }
            }

            // 제거된 위치에 새로운 조각 삽입
            // 삽입 순서는 열 번호가 작은 순, 같은 열 내에서는 행 번호가 큰 순
            removedPositions.sort((a, b) -> {
                if(a[1] != b[1]) return a[1] - b[1];
                return b[0] - a[0];
            });

            for(int[] pos : removedPositions) {
                grid[pos[0]][pos[1]] = getNextWallPiece();
            }
        }

        return totalValue;
    }

    // 벽면에서 다음 유물 조각을 가져오는 메서드
    static int getNextWallPiece() {
        int piece = wallPieces[wallIndex % M];
        wallIndex++;
        return piece;
    }
}