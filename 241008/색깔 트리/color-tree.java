import java.io.*;
import java.util.*;

public class Main {
    public static Map<Integer, Node> nodes = new HashMap<>();

    public static class Node {
        public int m_id;
        public int p_id;
        public int color;
        public int max_depth;
        public List<Node> children;

        public Node(int m_id, int p_id, int color, int max_depth) {
            this.m_id = m_id;
            this.p_id = p_id;
            this.color = color;
            this.max_depth = max_depth;
            children = new ArrayList<>();
        }
    }

    private static boolean isValidDepth(Node parent) {
        int depth = 1;
        while (true) {
            if (parent.max_depth <= depth) return false;
            if (parent.p_id == -1) break;

            parent = nodes.get(parent.p_id);
            depth++;
        }
        return true;
    }

    private static void addNode(int m_id, int p_id, int color, int max_depth) {
        if (p_id == -1) {
            nodes.put(m_id, new Node(m_id, p_id, color, max_depth));
            return;
        }

        Node parent = nodes.get(p_id);
        if (isValidDepth(parent)) {
            Node newNode = new Node(m_id, p_id, color, max_depth);
            parent.children.add(newNode);
            nodes.put(m_id, newNode);
        }
    }

    private static void changeColor(int m_id, int color) {
        // m_id 노드를 루트로 하는 서브 트리 내 모든 노드의 색깔을 color로 변경
        Node rootNode = nodes.get(m_id);
        rootNode.color = color;
        for (Node childNode : rootNode.children)
            changeColor(childNode.m_id, color);
    }

    private static void readColor(int m_id) {
        // m_id 노드의 색깔 출력
        System.out.println(nodes.get(m_id).color);
    }

    private static void readScore() {
        // 모든 노드의 가치를 계산, 가치 제곱의 합을 출력
        int score = 0;
        for (Node node : nodes.values()) {
            Set<Integer> set = new HashSet<>();
            dfs(node, set);

            int value = set.size();
            score += value * value;
        }
        System.out.println(score);
    }

    private static void dfs(Node node, Set<Integer> set) {
        set.add(node.color);
        for (Node child : node.children)
            dfs(child, set);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine());
        int Q = Integer.parseInt(st.nextToken());
        while (Q-- > 0) {
            st = new StringTokenizer(br.readLine());
            int command = Integer.parseInt(st.nextToken());

            if (command == 100) {
                int m_id = Integer.parseInt(st.nextToken());
                int p_id = Integer.parseInt(st.nextToken());
                int color = Integer.parseInt(st.nextToken());
                int max_depth = Integer.parseInt(st.nextToken());
                addNode(m_id, p_id, color, max_depth);
            } else if (command == 200) {
                int m_id = Integer.parseInt(st.nextToken());
                int color = Integer.parseInt(st.nextToken());
                changeColor(m_id, color);
            } else if (command == 300) {
                int m_id = Integer.parseInt(st.nextToken());
                readColor(m_id);
            } else if (command == 400) {
                readScore();
            }
        }
    }
}