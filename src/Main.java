/**
 * @author 19086
 * @version 1.0
 * Create by 2023/11/22 11:49
 */
import java.util.*;

class Graph {      //邻接矩阵类
    private int[][] adjacencyMatrix;    //邻接矩阵
    private int numVertices;        //顶点数

    public Graph(int numVertices) {          //根据顶点数来生成初始的临界矩阵
        this.numVertices = numVertices;
        this.adjacencyMatrix = new int[numVertices][numVertices];
    }

    public void addEdge(int start, int end, int weight) {    //创建顶点间的关系，完善邻接矩阵
        adjacencyMatrix[start][end] = weight;
        adjacencyMatrix[end][start] = weight; //因为是双向通的，所以反过来也创建一个
    }

    public int shortestDistance(int start, int end, List<Integer> path) {   //计算最短距离
        int[] distance = new int[numVertices];     //记录起始节点到每个节点的最近距离
        int[] predecessor = new int[numVertices]; // 记录前驱节点
        Arrays.fill(distance, Integer.MAX_VALUE);    //初始化每个节点距离为∞
        Arrays.fill(predecessor, -1); // 初始化前驱节点为 -1

        distance[start] = 0;    //起点到起点的距离是0
         //优先队列 pq（自带比较器），用于按照节点到起始节点的距离进行排序。队列中的节点将按照它们到起始节点的距离从小到大排列。
        PriorityQueue<Integer> pq = new PriorityQueue<>((v1, v2) -> Integer.compare(distance[v1], distance[v2]));
        pq.add(start);

        while (!pq.isEmpty()) {
            int u = pq.poll();  //当前锁定的节点

           //如果从起始节点到节点 u 的距离（distance[u]）不是无穷大、
            // 从节点 u 到节点 v 有连接、以及通过节点 u 到节点 v 的距离更短，
            // 那么更新从起始节点到节点 v 的距离为更短的值，并将节点 v 加入优先队列，以便下一轮迭代。

            for (int v = 0; v < numVertices; v++) {    //遍历u的所有邻接节点
                if (adjacencyMatrix[u][v] != 0 && distance[u] != Integer.MAX_VALUE && distance[u] + adjacencyMatrix[u][v] < distance[v]) {
                    distance[v] = distance[u] + adjacencyMatrix[u][v];
                    predecessor[v] = u; // 记录前驱节点
                    pq.add(v);
                }
            }
        }

        // 回溯找到最短路径上的节点
        reconstructPath(start, end, predecessor, path);

        return distance[end];
    }

    private void reconstructPath(int start, int end, int[] predecessor, List<Integer> path) {
        //回溯经过的节点路径
        int current = end;
        while (current != start) {
            path.add(current);
            current = predecessor[current];
        }
        path.add(start);

        Collections.reverse(path); // 反转List，使其按照起始节点到目标节点的顺序排列
    }
}


public class Main {
    public static void main(String[] args) {
        //创建实际地点与邻接矩阵的表关系
        HashMap<Integer,String> map = new HashMap<>();
        map.put(0,"卸甲甸");
        map.put(1,"长芦");
        map.put(2,"六合");
        map.put(3,"盘城");
        map.put(4,"大厂");
        map.put(5,"新街口");
        map.put(6,"泰冯路");
        map.put(7,"浦口");
        map.put(8,"新集");
        map.put(9,"葛塘");
        //再创建一个反过来的哈希表
        Map<String, Integer> reversedMap = new HashMap<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            reversedMap.put(entry.getValue(), entry.getKey());
        }
        // 创建图并添加边
        Graph graph = new Graph(10);
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 4, 2);
        graph.addEdge(0, 5, 5);
        graph.addEdge(1,2 ,5 );
        graph.addEdge(2, 7, 4);
        graph.addEdge(2, 6,13 );
        graph.addEdge(3,4 ,6 );
        graph.addEdge(5, 7, 21);
        graph.addEdge(6, 9,8 );
        graph.addEdge(7,8 , 7);
        graph.addEdge(8,9 ,9 );

        //查询条件
        String startPlace = "葛塘";
        String endPlace = "新街口";
        // 计算最短距离
        List<Integer> path = new ArrayList<>();
        int shortestDistance = graph.shortestDistance(reversedMap.get(startPlace),
                reversedMap.get(endPlace), path);

        System.out.println("两站点的最短路径距离是" + startPlace + " to "+endPlace+": " + shortestDistance);
        System.out.println("路径途径为: ");
        for (int i : path){
            System.out.print("--->" + i);
        }
    }
}
